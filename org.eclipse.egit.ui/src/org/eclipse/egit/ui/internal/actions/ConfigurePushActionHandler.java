/*******************************************************************************
 * Copyright (C) 2011, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.ui.internal.push.SimpleConfigurePushDialog;
import org.eclipse.jgit.lib.Repository;

/**
 * Open the "Configure Push" dialog
 */
public class ConfigurePushActionHandler extends RepositoryActionHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Repository repository = getRepository(true, event);
		if (repository != null)
			SimpleConfigurePushDialog.getDialog(getShell(event), repository)
					.open();
		return null;
	}

	@Override
	public boolean isEnabled() {
		return getRepository() != null
				&& SimpleConfigurePushDialog
						.getConfiguredRemote(getRepository()) != null;
	}

}
