/*******************************************************************************
 * Copyright (C) 2010, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.egit.ui.internal.actions;

/**
 * The "compare with ref" action. This action opens a Ref selection dialog to
 * select a branch or tag and then a compare editor comparing the version.
 */
public class CompareWithRefAction extends RepositoryAction {
	/**
	 *
	 */
	public CompareWithRefAction() {
		super(ActionCommands.COMPARE_WITH_REF_ACTION,
				new CompareWithRefActionHandler());
	}
}
