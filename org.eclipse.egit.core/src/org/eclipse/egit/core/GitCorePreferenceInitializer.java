/*******************************************************************************
 * Copyright (C) 2008, Roger C. Soares <rogersoares@intelinet.com.br>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/** Initializes plugin preferences with default values. */
public class GitCorePreferenceInitializer extends AbstractPreferenceInitializer {
	private static final int MB = 1024 * 1024;

	public void initializeDefaultPreferences() {
		final IEclipsePreferences p  = new DefaultScope().getNode(Activator.getPluginId());

		p.putInt(GitCorePreferences.core_packedGitWindowSize, 8 * 1024);
		p.putInt(GitCorePreferences.core_packedGitLimit, 10 * MB);
		p.putBoolean(GitCorePreferences.core_packedGitMMAP, false);
		p.putInt(GitCorePreferences.core_deltaBaseCacheLimit, 10 * MB);
	}
}
