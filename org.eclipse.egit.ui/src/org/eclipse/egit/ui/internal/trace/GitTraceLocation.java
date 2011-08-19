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
package org.eclipse.egit.ui.internal.trace;

import org.eclipse.egit.ui.Activator;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugTrace;

/**
 * EGit Trace locations
 */
public enum GitTraceLocation implements ITraceLocation {

	/** UI */
	UI("/debug/ui"), //$NON-NLS-1$
	/** HISTORYVIEW */
	HISTORYVIEW("/debug/ui/historyview"), //$NON-NLS-1$
	/** REPOSITORIESVIEW */
	REPOSITORIESVIEW("/debug/ui/repositoriesview"), //$NON-NLS-1$
	/** REPOSITORYCHANGESCANNER */
	REPOSITORYCHANGESCANNER("/debug/repochangescanner"), //$NON-NLS-1$
	/** DECORATION */
	DECORATION("/debug/ui/decoration"), //$NON-NLS-1$
	/** QUICKDIFF */
	QUICKDIFF("/debug/quickdiff"); //$NON-NLS-1$

	/**
	 * Initialize the locations
	 *
	 * @param options
	 * @param pluginIsDebugging
	 */
	public static void initializeFromOptions(DebugOptions options,
			boolean pluginIsDebugging) {

		// we evaluate the plug-in switch
		if (pluginIsDebugging) {
			myTrace = options.newDebugTrace(Activator.getPluginId());

			for (GitTraceLocation loc : values()) {
				boolean active = options.getBooleanOption(loc.getFullPath(),
						false);
				loc.setActive(active);
			}
		} else {
			// if the plug-in switch is off, we don't set the trace instance
			// to null to avoid problems with possibly running trace calls
			for (GitTraceLocation loc : values()) {
				loc.setActive(false);
			}
		}
	}

	private final String location;

	private final String fullPath;

	private boolean active = false;

	private static DebugTrace myTrace;

	private GitTraceLocation(String path) {
		this.fullPath = Activator.getPluginId() + path;
		this.location = path;
	}

	/**
	 * Convenience method
	 *
	 * @return the debug trace (may be null)
	 *
	 **/
	public static DebugTrace getTrace() {
		return GitTraceLocation.myTrace;
	}

	/**
	 * @return <code>true</code> if this location is active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @return the full path
	 */
	public String getFullPath() {
		return this.fullPath;
	}

	public String getLocation() {
		return this.location;
	}

	/**
	 * Sets the "active" flag for this location.
	 * <p>
	 * Used by the initializer
	 *
	 * @param active
	 *            the "active" flag
	 */
	private void setActive(boolean active) {
		this.active = active;
	}
}
