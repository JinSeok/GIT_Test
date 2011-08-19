/*******************************************************************************
 * Copyright (C) 2010, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.history.command;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.decorators.GitQuickDiffProvider;
import org.eclipse.jgit.lib.Repository;

/**
 * "Reset quickdiff baseline" with parameter (HEAD, or HEAD^1)
 */
public class ResetQuickdiffBaselineHandler extends
		AbstractHistoryCommandHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String baseline = event
				.getParameter(HistoryViewCommands.BASELINE_TARGET);
		Repository repo = getRepository(event);
		if (baseline == null)
			throw new ExecutionException(
					UIText.ResetQuickdiffBaselineHandler_NoTargetMessage);

		try {
			GitQuickDiffProvider.setBaselineReference(repo, baseline);
		} catch (IOException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}
}
