/*******************************************************************************
 * Copyright (C) 2007, Dave Watson <dwatson@mimvista.com>
 * Copyright (C) 2008, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2006, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.core.op.ResetOperation;
import org.eclipse.egit.core.op.ResetOperation.ResetType;
import org.eclipse.egit.ui.JobFamilies;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.dialogs.ResetTargetSelectionDialog;
import org.eclipse.egit.ui.internal.job.JobUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.osgi.util.NLS;

/**
 * An action to reset the current branch to a specific revision.
 *
 * @see ResetOperation
 */
public class ResetActionHandler extends RepositoryActionHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Repository repository = getRepository(true, event);
		if (repository == null)
			return null;
		if (!repository.getRepositoryState().canResetHead()) {
			MessageDialog.openError(getShell(event),
					UIText.ResetAction_errorResettingHead, NLS.bind(
							UIText.ResetAction_repositoryState, repository
									.getRepositoryState().getDescription()));
			return null;
		}
		ResetTargetSelectionDialog branchSelectionDialog = new ResetTargetSelectionDialog(
				getShell(event), repository);
		if (branchSelectionDialog.open() == IDialogConstants.OK_ID) {
			final String refName = branchSelectionDialog.getRefName();
			final ResetType type = branchSelectionDialog.getResetType();
			String jobname = NLS.bind(UIText.ResetAction_reset, refName);
			final ResetOperation operation = new ResetOperation(repository,
					refName, type);
			JobUtil.scheduleUserJob(operation, jobname, JobFamilies.RESET);
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return getRepository() != null && containsHead();
	}
}
