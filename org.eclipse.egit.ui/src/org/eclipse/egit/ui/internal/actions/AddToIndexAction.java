/*******************************************************************************
 * Copyright (C) 2010, Jens Baumgart <jens.baumgart@sap.com>
 * Copyright (C) 2010, Stefan Lay <stefan.lay@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.actions;

import org.eclipse.egit.core.op.AddToIndexOperation;

/**
 * An action to add files to a Git index.
 *
 * @see AddToIndexOperation
 */
public class AddToIndexAction extends RepositoryAction {

	/**
	 * Constructs this action
	 */
	public AddToIndexAction() {
		super(ActionCommands.ADD_TO_INDEX, new AddToIndexActionHandler());
	}

}
