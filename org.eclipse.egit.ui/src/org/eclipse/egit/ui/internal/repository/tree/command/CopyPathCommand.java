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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.ui.internal.repository.tree.FileNode;
import org.eclipse.egit.ui.internal.repository.tree.FolderNode;
import org.eclipse.egit.ui.internal.repository.tree.RepositoryTreeNode;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * Implements "Copy Path to Clipboard"
 */
public class CopyPathCommand extends
		RepositoriesViewCommandHandler<RepositoryTreeNode> {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		RepositoryTreeNode node = getSelectedNodes(event).get(0);
		String path;

		switch (node.getType()) {
		case REPO:
			path = node.getRepository().getDirectory().toString();
			break;
		case WORKINGDIR:
			if (node.getRepository().isBare())
				return null;
			path = node.getRepository().getWorkTree().toString();
			break;
		case FILE:
			path = ((FileNode) node).getObject().getPath().toString();
			break;
		case FOLDER:
			path = ((FolderNode) node).getObject().getPath().toString();
			break;
		default:
			return null;
		}

		Clipboard clipboard = new Clipboard(null);
		try {
			TextTransfer textTransfer = TextTransfer.getInstance();
			Transfer[] transfers = new Transfer[] { textTransfer };
			Object[] data = new Object[] { path };
			clipboard.setContents(data, transfers);
		} finally {
			clipboard.dispose();
		}

		return null;
	}
}
