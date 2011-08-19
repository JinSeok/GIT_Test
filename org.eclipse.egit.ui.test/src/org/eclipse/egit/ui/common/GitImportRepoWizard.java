/*******************************************************************************
 * Copyright (C) 2009, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2010, Ketan Padegaonkar <KetanPadegaonkar@gmail.com>
 * Copyright (C) 2010, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.common;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

public class GitImportRepoWizard {

	private static final SWTWorkbenchBot bot = new SWTWorkbenchBot();

	// TODO: speed it up by calling the wizard using direct eclipse API.
	public void openWizard() {
		bot.menu("File").menu("Import...").click();
		bot.shell("Import").activate();

		bot.tree().expandNode("Git").select("Projects from Git");

		bot.button("Next >").click();
	}

	public RepoPropertiesPage openCloneWizard() {
		bot.shell("Import Projects from Git").activate();

		bot.button("Clone...").click();

		bot.shell("Clone Git Repository").activate();

		return new RepoPropertiesPage();
	}

	public int configuredRepoCount() {
		bot.shell("Import Projects from Git").activate();

		return bot.table(0).rowCount();
	}

	public boolean containsRepo(String projectName) {
		SWTBotTable table = bot.table(0);
		int repoCount = configuredRepoCount();

		for (int i = 0; i < repoCount; i++) {
			String rowName = table.getTableItem(i).getText();
			if (rowName.contains(projectName))
				return true;
		}
		return false;
	}

	public void selectAndCloneRepository(String repoName) throws Exception {
		SWTBotShell importShell = bot.shell("Import Projects from Git")
				.activate();

		SWTBotTable table = bot.table(0);
		for (int i = 0; i < table.rowCount(); i++) {
			String rowName = table.getTableItem(i).getText();
			if (rowName != null && rowName.startsWith(repoName)) {
				table.select(i);
				break;
			}
		}
		// TODO investigate why we need Thread.sleep here
		bot.button("Next >").click();
		Thread.sleep(1000);
		// set the radio buttons properly
		pressAltAndChar(importShell, 'E');
		Thread.sleep(1000);
		pressAltAndChar(importShell, 'a');
		Thread.sleep(1000);

		bot.button("Next >").click();
		Thread.sleep(1000);

		bot.button("Select All").click();
		Thread.sleep(1000);
	}

	public void waitForCreate() {
		bot.button("Finish").click();

		SWTBotShell shell = bot.shell("Import Projects from Git");

		bot.waitUntil(shellCloses(shell), 120000);
	}

	// TODO: move this to some utility class
	private void pressAltAndChar(final SWTBotShell shell, final char charToPress) {
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				Event evt = new Event();
				// Alt down
				evt.type = SWT.KeyDown;
				evt.item = shell.widget;
				evt.keyCode = SWT.ALT;
				display.post(evt);
				// G down
				evt.keyCode = 0;
				evt.character = charToPress;
				display.post(evt);
				// G up
				evt.type = SWT.KeyUp;
				display.post(evt);
				// Alt up
				evt.keyCode = SWT.ALT;
				evt.character = ' ';
				display.post(evt);
			}
		});
	}

}
