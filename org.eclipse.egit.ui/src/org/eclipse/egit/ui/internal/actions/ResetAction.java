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

import org.eclipse.egit.core.op.ResetOperation;

/**
 * An action to reset the current branch to a specific revision.
 *
 * @see ResetOperation
 */
public class ResetAction extends RepositoryAction {
	/**
	 *
	 */
	public ResetAction() {
		super(ActionCommands.RESET_ACTION, new ResetActionHandler());
	}
}
