/*******************************************************************************
 * Copyright (C) 2010, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.history.command;

/**
 * Constants for the commands used in the history view.
 * <p>
 * Only those constants are listed that are being used in code.
 */
public class HistoryViewCommands {
	/** "Target" parameter for setting the quickdiff baseline (HEAD or HEAD^1) */
	public static final String BASELINE_TARGET = "org.eclipse.egit.ui.history.ResetQuickdiffBaselineTarget"; //$NON-NLS-1$

	/** "Checkout" */
	public static final String CHECKOUT = "org.eclipse.egit.ui.history.CheckoutCommand"; //$NON-NLS-1$

	/**
	 * "Compare mode" parameter for the "open" command (see
	 * {@link #SHOWVERSIONS})
	 */
	public static final String COMPARE_MODE_PARAM = "org.eclipse.egit.ui.history.CompareMode"; //$NON-NLS-1$

	/** "Compare with each other" */
	public static final String COMPARE_VERSIONS = "org.eclipse.egit.ui.history.CompareVersions"; //$NON-NLS-1$

	/** "Compare in Tree Compare" */
	public static final String COMPARE_VERSIONS_IN_TREE = "org.eclipse.egit.ui.history.CompareVersionsInTree"; //$NON-NLS-1$

	/** "Compare with working tree" */
	public static final String COMPARE_WITH_TREE = "org.eclipse.egit.ui.history.CompareWithWorkingTree"; //$NON-NLS-1$

	/** "Create Branch" */
	public static final String CREATE_BRANCH = "org.eclipse.egit.ui.history.CreateBranch"; //$NON-NLS-1$

	/** "Create Patch" */
	public static final String CREATE_PATCH = "org.eclipse.egit.ui.history.CreatePatch"; //$NON-NLS-1$

	/** "Create Tag" */
	public static final String CREATE_TAG = "org.eclipse.egit.ui.history.CreateTag"; //$NON-NLS-1$

	/** "Open" */
	public static final String OPEN = "org.eclipse.egit.ui.history.ShowVersions"; //$NON-NLS-1$

	/** "Reset" (with parameter {@link #RESET_MODE}) */
	public static final String RESET = "org.eclipse.egit.ui.history.Reset"; //$NON-NLS-1$

	/** "Reset" mode (soft, mixed, hard) */
	public static final String RESET_MODE = "org.eclipse.egit.ui.history.ResetMode"; //$NON-NLS-1$

	/** "Reset quickdiff baseline" (with parameter {@link #BASELINE_TARGET}) */
	public static final String RESET_QUICKDIFF_BASELINE = "org.eclipse.egit.ui.history.ResetQuickdiffBaseline"; //$NON-NLS-1$

	/** "Set as quickdiff baseline" */
	public static final String SET_QUICKDIFF_BASELINE = "org.eclipse.egit.ui.history.SetQuickdiffBaseline"; //$NON-NLS-1$

	/** "Open" or "Show Versions" (depending on the selection) */
	public static final String SHOWVERSIONS = "org.eclipse.egit.ui.history.ShowVersions"; //$NON-NLS-1$

	/** Open in Text Editor */
	public static final String OPEN_IN_TEXT_EDITOR = "org.eclipse.egit.ui.history.OpenInTextEditorCommand"; //$NON-NLS-1$

	/** "cherry-pick" a commit */
	public static final String CHERRYPICK = "org.eclipse.egit.ui.history.CherryPick"; //$NON-NLS-1$

	/** revert a commit */
	public static final String REVERT = "org.eclipse.egit.ui.history.Revert"; //$NON-NLS-1$

	/** merge with branch/tag/commit */
	public static final String MERGE = "org.eclipse.egit.ui.history.Merge"; //$NON-NLS-1$

	/** rebase on top of commit */
	public static final String REBASECURRENT = "org.eclipse.egit.ui.RebaseCurrent"; //$NON-NLS-1$

	/** Open in Commit Viewer */
	public static final String OPEN_IN_COMMIT_VIEWER = "org.eclipse.egit.ui.history.OpenInCommitViewerCommand"; //$NON-NLS-1$
}
