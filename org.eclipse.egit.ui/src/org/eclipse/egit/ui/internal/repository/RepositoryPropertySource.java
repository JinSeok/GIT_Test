/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mathias Kinzler (SAP AG) - initial implementation
 *******************************************************************************/
package org.eclipse.egit.ui.internal.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIIcons;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.preferences.ConfigurationEditorComponent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * Properties for repository and user configuration (read-only).
 * <p>
 * Depending on which mode is selected, either the user configuration, the
 * repository configuration, or the effective configuration is shown.
 */
public class RepositoryPropertySource implements IPropertySource {

	static final String CHANGEMODEACTIONID = "ChangeMode"; //$NON-NLS-1$

	static final String SINGLEVALUEACTIONID = "SingleValueToggle"; //$NON-NLS-1$

	static final String EDITACTIONID = "Edit"; //$NON-NLS-1$

	private static final String SYSTEM_ID_PREFIX = "system"; //$NON-NLS-1$

	private static final String USER_ID_PREFIX = "user"; //$NON-NLS-1$

	private static final String REPO_ID_PREFIX = "repo"; //$NON-NLS-1$

	private static final String EFFECTIVE_ID_PREFIX = "effe"; //$NON-NLS-1$

	private final PropertySheetPage myPage;

	private final FileBasedConfig systemConfig;

	private final FileBasedConfig userHomeConfig;

	private final StoredConfig repositoryConfig;

	private final StoredConfig effectiveConfig;

	private ActionContributionItem changeModeAction;

	private ActionContributionItem editAction;

	private ActionContributionItem singleValueToggleAction;

	/**
	 * @param repository
	 *            the repository
	 * @param page
	 *            the page showing the properties
	 */
	public RepositoryPropertySource(Repository repository,
			PropertySheetPage page) {
		myPage = page;

		effectiveConfig = repository.getConfig();
		systemConfig = SystemReader.getInstance().openSystemConfig(null, FS.DETECTED);
		userHomeConfig = SystemReader.getInstance().openUserConfig(systemConfig, FS.DETECTED);

		if (effectiveConfig instanceof FileBasedConfig) {
			File configFile = ((FileBasedConfig) effectiveConfig).getFile();
			repositoryConfig = new FileBasedConfig(configFile, repository
					.getFS());
		} else {
			repositoryConfig = effectiveConfig;
		}

		synchronized (myPage) {
			// check if the actions are already there, if not, create them
			IActionBars bars = myPage.getSite().getActionBars();

			changeModeAction = (ActionContributionItem) bars
					.getToolBarManager().find(CHANGEMODEACTIONID);
			singleValueToggleAction = (ActionContributionItem) bars
					.getToolBarManager().find(SINGLEVALUEACTIONID);

			if (changeModeAction != null) {
				return;
			}

			changeModeAction = new ActionContributionItem(new Action(
					DisplayMode.REPO.getText(), IAction.AS_DROP_DOWN_MENU) {
				@Override
				public String getId() {
					return CHANGEMODEACTIONID;
				}

				@Override
				public void run() {
					MenuManager mgr = new MenuManager();
					ToolItem item = (ToolItem) changeModeAction.getWidget();
					ToolBar control = item.getParent();
					final Menu ctxMenu = mgr.createContextMenu(control);

					for (final DisplayMode aMode : DisplayMode.values()) {
						mgr.add(new Action(aMode.getText()) {
							@Override
							public void run() {
								changeModeAction.getAction().setText(
										aMode.getText());
								editAction.getAction().setEnabled(
										aMode != DisplayMode.EFFECTIVE);
								myPage.refresh();
							}

							@Override
							public boolean isEnabled() {
								return aMode != getCurrentMode();
							}

							@Override
							public boolean isChecked() {
								return aMode == getCurrentMode();
							}

							@Override
							public int getStyle() {
								return IAction.AS_CHECK_BOX;
							}
						});
					}

					ctxMenu.setVisible(true);
				}

				@Override
				public String getToolTipText() {
					return UIText.RepositoryPropertySource_SelectModeTooltip;
				}

				@Override
				public int getStyle() {
					return IAction.AS_DROP_DOWN_MENU;
				}

			});

			editAction = new ActionContributionItem(new Action(
					UIText.RepositoryPropertySource_EditConfigButton,
					UIIcons.EDITCONFIG) {
				@Override
				public String getId() {
					return EDITACTIONID;
				}

				@Override
				public void run() {

					final StoredConfig config;

					switch (getCurrentMode()) {
					case EFFECTIVE:
						return;
					case SYSTEM:
						config = systemConfig;
						break;
					case USER:
						config = userHomeConfig;
						break;
					case REPO:
						config = repositoryConfig;
						break;
					default:
						return;
					}

					new EditDialog(myPage.getSite().getShell(),
							(FileBasedConfig) config, getCurrentMode()
									.getText()).open();
					myPage.refresh();
				}

				@Override
				public int getStyle() {
					return IAction.AS_PUSH_BUTTON;
				}
			});

			singleValueToggleAction = new ActionContributionItem(new Action(
					UIText.RepositoryPropertySource_SingleValueButton) {
				@Override
				public String getId() {
					return SINGLEVALUEACTIONID;
				}

				@Override
				public void run() {
					myPage.refresh();
				}

				@Override
				public int getStyle() {
					return IAction.AS_CHECK_BOX;
				}

				@Override
				public String getToolTipText() {
					return UIText.RepositoryPropertySource_SuppressMultipleValueTooltip;
				}
			});

			bars.getToolBarManager().add(new Separator());
			bars.getToolBarManager().add(changeModeAction);
			bars.getToolBarManager().add(singleValueToggleAction);
			bars.getToolBarManager().add(editAction);

			bars.getToolBarManager().update(false);
		}
	}

	private DisplayMode getCurrentMode() {
		String actionText = changeModeAction.getAction().getText();
		for (DisplayMode aMode : DisplayMode.values()) {
			if (aMode.getText().equals(actionText))
				return aMode;
		}
		return null;
	}

	private boolean getSingleValueMode() {
		if (singleValueToggleAction != null)
			return singleValueToggleAction.getAction().isChecked();
		return false;
	}

	private Object getValueFromConfig(Config config, String keyString) {
		StringTokenizer tok = new StringTokenizer(keyString, "."); //$NON-NLS-1$

		String section;
		String subsection;
		String name;

		String[] valueList = null;
		if (tok.countTokens() == 2) {
			section = tok.nextToken();
			subsection = null;
			name = tok.nextToken();
		} else if (tok.countTokens() == 3) {
			section = tok.nextToken();
			subsection = tok.nextToken();
			name = tok.nextToken();
		} else {
			return ""; //$NON-NLS-1$
		}

		if (getSingleValueMode())
			valueList = new String[] { config.getString(section, subsection,
					name) };
		else
			valueList = config.getStringList(section, subsection, name);

		if (valueList == null || valueList.length == 0)
			return null;

		if (valueList.length == 1) {
			return valueList[0];
		}

		StringBuilder sb = new StringBuilder();
		for (String value : valueList) {
			sb.append('[');
			sb.append(value);
			sb.append(']');
		}

		return sb.toString();
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		try {
			systemConfig.load();
			userHomeConfig.load();
			repositoryConfig.load();
			effectiveConfig.load();
		} catch (IOException e) {
			showExceptionMessage(e);
		} catch (ConfigInvalidException e) {
			showExceptionMessage(e);
		}

		List<IPropertyDescriptor> resultList = new ArrayList<IPropertyDescriptor>();

		StoredConfig config;
		String category;
		String prefix;
		switch (getCurrentMode()) {
		case EFFECTIVE:
			prefix = EFFECTIVE_ID_PREFIX;
			category = UIText.RepositoryPropertySource_EffectiveConfigurationCategory;
			config = effectiveConfig;
			break;
		case REPO: {
			prefix = REPO_ID_PREFIX;
			String location = ""; //$NON-NLS-1$
			if (repositoryConfig instanceof FileBasedConfig) {
				location = ((FileBasedConfig) repositoryConfig).getFile()
						.getAbsolutePath();
			}
			category = NLS
					.bind(
							UIText.RepositoryPropertySource_RepositoryConfigurationCategory,
							location);
			config = repositoryConfig;
			break;
		}
		case USER: {
			prefix = USER_ID_PREFIX;
			String location = userHomeConfig.getFile().getAbsolutePath();
			category = NLS
					.bind(UIText.RepositoryPropertySource_GlobalConfigurationCategory,
							location);
			config = userHomeConfig;
			break;
		}
		case SYSTEM: {
			prefix = SYSTEM_ID_PREFIX;
			String location = systemConfig.getFile().getAbsolutePath();
			category = NLS
					.bind(UIText.RepositoryPropertySource_GlobalConfigurationCategory,
							location);
			config = systemConfig;
			break;
		}
		default:
			return new IPropertyDescriptor[0];
		}
		for (String key : config.getSections()) {
			for (String sectionItem : config.getNames(key)) {
				String sectionId = key + "." + sectionItem; //$NON-NLS-1$
				PropertyDescriptor desc = new PropertyDescriptor(prefix
						+ sectionId, sectionId);
				desc.setCategory(category);
				resultList.add(desc);
			}
			for (String sub : config.getSubsections(key)) {
				for (String sectionItem : config.getNames(key, sub)) {
					String sectionId = key + "." + sub + "." + sectionItem; //$NON-NLS-1$ //$NON-NLS-2$
					PropertyDescriptor desc = new PropertyDescriptor(prefix
							+ sectionId, sectionId);
					desc.setCategory(category);
					resultList.add(desc);
				}
			}
		}

		return resultList.toArray(new IPropertyDescriptor[0]);
	}

	public Object getPropertyValue(Object id) {
		String actId = ((String) id);
		Object value = null;
		if (actId.startsWith(SYSTEM_ID_PREFIX)) {
			value = getValueFromConfig(systemConfig, actId.substring(4));
		} else if (actId.startsWith(USER_ID_PREFIX)) {
			value = getValueFromConfig(userHomeConfig, actId.substring(4));
		} else if (actId.startsWith(REPO_ID_PREFIX)) {
			value = getValueFromConfig(repositoryConfig, actId.substring(4));
		} else if (actId.startsWith(EFFECTIVE_ID_PREFIX)) {
			value = getValueFromConfig(effectiveConfig, actId.substring(4));
		}
		if (value == null)
			// the text editor needs this to work
			return ""; //$NON-NLS-1$

		return value;
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
		// no editing here
	}

	public void setPropertyValue(Object id, Object value) {
		// no editing here
	}

	private void showExceptionMessage(Exception e) {
		Activator.handleError(UIText.RepositoryPropertySource_ErrorHeader, e,
				true);
	}

	private enum DisplayMode {
		/* The effective configuration as obtained from the repository */
		EFFECTIVE(UIText.RepositoryPropertySource_EffectiveConfigurationAction),
		/* System wide configuration */
		SYSTEM(UIText.RepositoryPropertySource_SystemConfigurationMenu),
		/* The user specific configuration */
		USER(UIText.RepositoryPropertySource_GlobalConfigurationMenu),
		/* The repository specific configuration */
		REPO(UIText.RepositoryPropertySource_RepositoryConfigurationButton);

		/**
		 * @return the human-readable String for this mode
		 */
		String getText() {
			return this.text;
		}

		private final String text;

		private DisplayMode(String text) {
			this.text = text;
		}
	}

	/**
	 * Wraps the editor component into a dialog
	 */
	private final static class EditDialog extends TitleAreaDialog {
		private final FileBasedConfig myConfig;

		private final String myTitle;

		ConfigurationEditorComponent editor;

		public EditDialog(Shell shell, FileBasedConfig config, String title) {
			super(shell);
			myConfig = config;
			setShellStyle(getShellStyle() | SWT.SHELL_TRIM);
			myTitle = title;
			setHelpAvailable(false);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite main = (Composite) super.createDialogArea(parent);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
					true).applyTo(main);
			editor = new ConfigurationEditorComponent(main, myConfig, true) {
				@Override
				protected void setErrorMessage(String message) {
					EditDialog.this.setErrorMessage(message);
				}

				@Override
				protected void setDirty(boolean dirty) {
					getButton(IDialogConstants.OK_ID).setEnabled(dirty);
				}
			};

			Control result = editor.createContents();
			applyDialogFont(main);
			return result;
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell
					.setText(UIText.RepositoryPropertySource_EditConfigurationTitle);
			newShell.setSize(700, 600);
		}

		@Override
		public void create() {
			super.create();
			setTitle(myTitle);
			setMessage(UIText.RepositoryPropertySource_EditorMessage);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}

		@Override
		protected void okPressed() {
			try {
				editor.save();
				super.okPressed();
			} catch (IOException e) {
				Activator.handleError(e.getMessage(), e, true);
			}
		}
	}
}
