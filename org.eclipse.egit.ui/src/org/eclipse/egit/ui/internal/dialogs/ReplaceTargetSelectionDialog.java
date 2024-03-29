/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Muskalla (Tasktop Technologies) - initial implementation
 *******************************************************************************/
package org.eclipse.egit.ui.internal.dialogs;

import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for selecting a compare target.
 *
 */
public class ReplaceTargetSelectionDialog extends AbstractBranchSelectionDialog {
	private final String pathString;

	/**
	 * @param parentShell
	 * @param repo
	 * @param pathString
	 */
	public ReplaceTargetSelectionDialog(Shell parentShell, Repository repo,
			String pathString) {
		super(parentShell, repo, SHOW_LOCAL_BRANCHES | SHOW_REMOTE_BRANCHES
				| SHOW_TAGS | SHOW_REFERENCES | EXPAND_LOCAL_BRANCHES_NODE
				| SELECT_CURRENT_REF);
		this.pathString = pathString;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(Window.OK).setText(
				UIText.ReplaceTargetSelectionDialog_ReplaceButton);
	}

	@Override
	protected String getMessageText() {
		return UIText.ReplaceTargetSelectionDialog_ReplaceMessage;
	}

	@Override
	protected String getTitle() {
		return NLS.bind(UIText.ReplaceTargetSelectionDialog_ReplaceTitle,
				pathString);
	}

	@Override
	protected String getWindowTitle() {
		return UIText.ReplaceTargetSelectionDialog_ReplaceWindowTitle;
	}

	@Override
	protected void refNameSelected(String refName) {
		getButton(Window.OK).setEnabled(refName != null);
	}
}
