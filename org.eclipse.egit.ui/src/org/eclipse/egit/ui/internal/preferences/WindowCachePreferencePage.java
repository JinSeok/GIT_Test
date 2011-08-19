/*******************************************************************************
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.GitCorePreferences;
import org.eclipse.egit.core.project.GitProjectData;
import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preferences for our window cache. */
public class WindowCachePreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	private static final int MB = 1024 * 1024;

	private static final int GB = 1024 * MB;

	/** */
	public WindowCachePreferencePage() {
		super(GRID);
		setTitle(UIText.WindowCachePreferencePage_title);
		ScopedPreferenceStore store = new ScopedPreferenceStore(
				new InstanceScope(), Activator.getPluginId());
		setPreferenceStore(store);
	}

	@Override
	protected void createFieldEditors() {
		addField(new StorageSizeFieldEditor(
				GitCorePreferences.core_packedGitWindowSize,
				UIText.WindowCachePreferencePage_packedGitWindowSize,
				getFieldEditorParent(), 512, 128 * MB) {
			protected boolean checkValue(final int number) {
				return super.checkValue(number)
						&& Integer.bitCount(number) == 1;
			}
		});

		addField(new StorageSizeFieldEditor(
				GitCorePreferences.core_packedGitLimit,
				UIText.WindowCachePreferencePage_packedGitLimit,
				getFieldEditorParent(), 512, 1 * GB));
		addField(new StorageSizeFieldEditor(
				GitCorePreferences.core_deltaBaseCacheLimit,
				UIText.WindowCachePreferencePage_deltaBaseCacheLimit,
				getFieldEditorParent(), 512, 1 * GB));

		addField(new BooleanFieldEditor(GitCorePreferences.core_packedGitMMAP,
				UIText.WindowCachePreferencePage_packedGitMMAP,
				getFieldEditorParent()));
	}

	public boolean performOk() {
		// first put the editor values into the configuration
		super.performOk();
		try {
			GitProjectData.reconfigureWindowCache();
			return true;
		} catch (RuntimeException e) {
			org.eclipse.egit.ui.Activator.handleError(e.getMessage(), e, true);
			return false;
		}
	}

	public void init(IWorkbench workbench) {
		// Nothing to do
	}
}
