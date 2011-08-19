/*******************************************************************************
 * Copyright (c) 2010, SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mathias Kinzler (SAP AG) - initial implementation
 *******************************************************************************/
package org.eclipse.egit.ui.internal.preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.SWTUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Displays the global Git configuration and allows to edit it.
 * <p>
 * In EGit, this maps to the user configuration.
 */
public class GlobalConfigurationPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	/** The ID of this page */
	public static final String ID = "org.eclipse.egit.ui.internal.preferences.GlobalConfigurationPreferencePage"; //$NON-NLS-1$

	private FileBasedConfig userConfig;

	private FileBasedConfig sysConfig;

	private StackLayout repoConfigStackLayout;

	private List<Repository> repositories;

	private Map<Repository, ConfigurationEditorComponent> repoConfigEditors = new HashMap<Repository, ConfigurationEditorComponent>();

	private Set<Repository> dirtyRepositories = new HashSet<Repository>();

	private boolean userIsDirty;

	private boolean sysIsDirty;

	private ConfigurationEditorComponent userConfigEditor;

	private ConfigurationEditorComponent sysConfigEditor;

	private Composite repoConfigComposite;

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = SWTUtils.createHVFillComposite(parent,
				SWTUtils.MARGINS_NONE);
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(SWTUtils.createHVFillGridData());
		userConfigEditor = new ConfigurationEditorComponent(tabFolder, userConfig, true) {
			@Override
			protected void setErrorMessage(String message) {
				GlobalConfigurationPreferencePage.this.setErrorMessage(message);
			}

			@Override
			protected void setDirty(boolean dirty) {
				userIsDirty = dirty;
				updateApplyButton();
			}
		};
		sysConfigEditor = new ConfigurationEditorComponent(tabFolder, sysConfig, true) {
			@Override
			protected void setErrorMessage(String message) {
				GlobalConfigurationPreferencePage.this.setErrorMessage(message);
			}

			@Override
			protected void setDirty(boolean dirty) {
				sysIsDirty = dirty;
				updateApplyButton();
			}
		};
		Control result = userConfigEditor.createContents();
		Dialog.applyDialogFont(result);
		TabItem userTabItem = new TabItem(tabFolder, SWT.FILL);
		userTabItem.setControl(result);
		userTabItem.setText(UIText.GlobalConfigurationPreferencePage_userSettingTabTitle);

		result = sysConfigEditor.createContents();
		Dialog.applyDialogFont(result);
		TabItem sysTabItem = new TabItem(tabFolder, SWT.FILL);
		sysTabItem.setControl(result);
		sysTabItem.setText(UIText.GlobalConfigurationPreferencePage_systemSettingTabTitle);

		Composite repoTab = new Composite(tabFolder, SWT.NONE);
		repoTab.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(repoTab);
		Composite repositoryComposite = new Composite(repoTab, SWT.NONE);
		repositoryComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(
				repositoryComposite);
		Label repoLabel = new Label(repositoryComposite, SWT.NONE);
		repoLabel
				.setText(UIText.GlobalConfigurationPreferencePage_repositorySettingRepositoryLabel);

		Combo repoCombo = new Combo(repositoryComposite, SWT.READ_ONLY);
		repoCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo = (Combo) e.widget;
				showRepositoryConfiguration(combo.getSelectionIndex());
			}
		});
		repoCombo.setItems(getRepositoryComboItems());

		repoConfigComposite = new Composite(repoTab, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(repoConfigComposite);
		repoConfigStackLayout = new StackLayout();
		repoConfigComposite.setLayout(repoConfigStackLayout);

		TabItem repoTabItem = new TabItem(tabFolder, SWT.FILL);
		repoTabItem.setControl(repoTab);
		repoTabItem.setText(UIText.GlobalConfigurationPreferencePage_repositorySettingTabTitle);

		if (repoCombo.getItemCount() > 0) {
			repoCombo.select(0);
			showRepositoryConfiguration(0);
		} else {
			repoCombo.setItems(new String[] {UIText.GlobalConfigurationPreferencePage_repositorySettingNoRepositories});
			repoCombo.select(0);
			repoCombo.setEnabled(false);
		}

		return composite;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible)
			updateApplyButton();
		super.setVisible(visible);
	}

	@Override
	protected void updateApplyButton() {
		if (getApplyButton() != null)
			getApplyButton().setEnabled(userIsDirty || sysIsDirty || !dirtyRepositories.isEmpty());
	}

	@Override
	public boolean performOk() {
		boolean ok = true;
		if (userIsDirty) {
			try {
				userConfigEditor.save();
			} catch (IOException e) {
				Activator.handleError(e.getMessage(), e, true);
				ok = false;
			}
		}
		if (sysIsDirty) {
			try {
				sysConfigEditor.save();
			} catch (IOException e) {
				Activator.handleError(e.getMessage(), e, true);
				ok = false;
			}
		}
		for (Repository repository : dirtyRepositories) {
			ConfigurationEditorComponent editor = repoConfigEditors.get(repository);
			try {
				editor.save();
			} catch (IOException e) {
				Activator.handleError(e.getMessage(), e, true);
				ok = false;
			}
		}
		return ok;
	}

	@Override
	protected void performDefaults() {
		try {
			userConfigEditor.restore();
			sysConfigEditor.restore();
			for (ConfigurationEditorComponent editor : repoConfigEditors.values()) {
				editor.restore();
			}

		} catch (IOException e) {
			Activator.handleError(e.getMessage(), e, true);
		}
		super.performDefaults();
	}

	public void init(IWorkbench workbench) {
		if (sysConfig == null)
			sysConfig = SystemReader.getInstance().openSystemConfig(null, FS.DETECTED);
		if (userConfig == null)
			userConfig = SystemReader.getInstance().openUserConfig(null, FS.DETECTED); // no inherit here!
		if (repositories == null) {
			repositories = new ArrayList<Repository>();
			List<String> repoPaths = Activator.getDefault().getRepositoryUtil().getConfiguredRepositories();
			RepositoryCache repositoryCache = org.eclipse.egit.core.Activator.getDefault().getRepositoryCache();
			for (String repoPath : repoPaths) {
				try {
					Repository repository = repositoryCache.lookupRepository(new File(repoPath));
					repositories.add(repository);
				} catch (IOException e) {
					continue;
				}
			}
			sortRepositoriesByName();
		}
	}

	private void sortRepositoriesByName() {
		Collections.sort(repositories, new Comparator<Repository>() {

			public int compare(Repository repo1, Repository repo2) {
				String repo1Name = repo1.getDirectory().getParentFile().getName();
				String repo2Name = repo2.getDirectory().getParentFile().getName();
				return repo1Name.compareTo(repo2Name);
			}
		});
	}

	private String[] getRepositoryComboItems() {
		List<String> items = new ArrayList<String>();
		for (Repository repository : repositories) {
			String repoName = repository.getDirectory().getParentFile().getName();
			items.add(repoName);
		}
		return items.toArray(new String[items.size()]);
	}

	private void showRepositoryConfiguration(int index) {
		Repository repository = repositories.get(index);
		ConfigurationEditorComponent editor = repoConfigEditors.get(repository);
		if (editor == null) {
			editor = createConfigurationEditor(repository);
			repoConfigEditors.put(repository, editor);
		}
		repoConfigStackLayout.topControl = editor.getContents();
		repoConfigComposite.layout();
	}

	private ConfigurationEditorComponent createConfigurationEditor(final Repository repository) {
		StoredConfig repositoryConfig;
		if (repository.getConfig() instanceof FileBasedConfig) {
			File configFile = ((FileBasedConfig) repository.getConfig()).getFile();
			repositoryConfig = new FileBasedConfig(configFile, repository
					.getFS());
		} else {
			repositoryConfig = repository.getConfig();
		}
		ConfigurationEditorComponent editorComponent = new ConfigurationEditorComponent(repoConfigComposite, repositoryConfig, true) {
			@Override
			protected void setErrorMessage(String message) {
				GlobalConfigurationPreferencePage.this.setErrorMessage(message);
			}

			@Override
			protected void setDirty(boolean dirty) {
				if (dirty)
					dirtyRepositories.add(repository);
				else
					dirtyRepositories.remove(repository);
				updateApplyButton();
			}
		};
		editorComponent.createContents();
		return editorComponent;
	}
}
