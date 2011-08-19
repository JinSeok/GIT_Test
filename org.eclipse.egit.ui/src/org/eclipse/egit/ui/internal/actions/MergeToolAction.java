/*******************************************************************************
 * Copyright (C) 2008, Marek Zawirski <marek.zawirski@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.actions;

/**
 * Action for displaying fetch wizard - allowing selection of specifications for
 * fetch, and fetching objects/refs from another repository.
 */
public class MergeToolAction extends RepositoryAction {
	/**
	 *
	 */
	public MergeToolAction() {
		super(ActionCommands.MERGE_TOOL_ACTION, new MergeToolActionHandler());
	}
}
