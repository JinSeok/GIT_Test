/*******************************************************************************
 * Copyright (C) 2010, Jens Baumgart <jens.baumgart@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.common;

import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.push.PushWizard;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.ui.PlatformUI;

public class PushWizardTester {

	private final SWTWorkbenchBot bot = new SWTWorkbenchBot();

	public RepoPropertiesPage openPushWizard(final Repository repository)
			throws Exception {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					PushWizard pushWizard = new PushWizard(repository);
					WizardDialog dlg = new WizardDialog(shell, pushWizard);
					dlg.setHelpAvailable(false);
					dlg.open();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		bot.waitUntil(Conditions.shellIsActive(UIText.PushWizard_windowTitleDefault),
				20000);
		return new RepoPropertiesPage();
	}

	public void nextPage() {
		bot.button(IDialogConstants.NEXT_LABEL).click();
	}

	public void finish() {
		bot.button(IDialogConstants.FINISH_LABEL).click();
	}

}
