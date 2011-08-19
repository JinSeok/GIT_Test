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
package org.eclipse.egit.ui.internal.repository.tree.command;

import java.net.URISyntaxException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.internal.push.PushWizard;
import org.eclipse.egit.ui.internal.repository.tree.RepositoryNode;
import org.eclipse.jface.wizard.WizardDialog;

/**
 * Implements "Push" from a Repository
 */
public class PushCommand extends RepositoriesViewCommandHandler<RepositoryNode> {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		RepositoryNode node = getSelectedNodes(event).get(0);

		try {
			WizardDialog dlg = new WizardDialog(getShell(event),
					new PushWizard(node.getRepository()));
			dlg.setHelpAvailable(true);
			dlg.open();
		} catch (URISyntaxException e1) {
			Activator.handleError(e1.getMessage(), e1, true);
		}

		return null;
	}

	public void setEnabled(Object evaluationContext) {
		enableWhenRepositoryHaveHead(evaluationContext);
	}
}
