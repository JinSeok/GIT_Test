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
package org.eclipse.egit.ui.test.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.ui.JobFamilies;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.common.LocalRepositoryTestCase;
import org.eclipse.egit.ui.test.ContextMenuHelper;
import org.eclipse.egit.ui.test.TestUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarToggleButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the Team->Branch action
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class HistoryViewTest extends LocalRepositoryTestCase {
	private static final String SECONDFOLDER = "secondFolder";

	private static final String ADDEDFILE = "another.txt";

	private static final String ADDEDMESSAGE = "A new file in a new folder";

	private static SWTBotPerspective perspective;

	private static int commitCount;

	private static File repoFile;

	@BeforeClass
	public static void setup() throws Exception {
		// File repoFile =
		repoFile = createProjectAndCommitToRepository();
		perspective = bot.activePerspective();
		bot.perspectiveById("org.eclipse.pde.ui.PDEPerspective").activate();
		IProject prj = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(PROJ1);
		IFolder folder2 = prj.getFolder(SECONDFOLDER);
		folder2.create(false, true, null);
		IFile addedFile = folder2.getFile(ADDEDFILE);
		addedFile.create(
				new ByteArrayInputStream("More content".getBytes(prj
						.getDefaultCharset())), false, null);
		addAndCommit(addedFile, ADDEDMESSAGE);
		// TODO count the commits
		commitCount = 3;
	}

	@AfterClass
	public static void shutdown() {
		perspective.activate();
	}

	@Test
	public void testOpenHistoryOnFileNoFilter() throws Exception {
		initFilter(0);

		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1).rowCount());
		assertEquals("Wrong number of commits", commitCount - 1,
				getHistoryViewTable(PROJ1, FOLDER).rowCount());
		assertEquals("Wrong number of commits", commitCount - 1,
				getHistoryViewTable(PROJ1, FOLDER, FILE1).rowCount());
		assertEquals("Wrong number of commits", 1,
				getHistoryViewTable(PROJ1, FOLDER, FILE2).rowCount());
		assertEquals("Wrong number of commits", 1,
				getHistoryViewTable(PROJ1, SECONDFOLDER).rowCount());
		assertEquals("Wrong number of commits", 1,
				getHistoryViewTable(PROJ1, SECONDFOLDER, ADDEDFILE).rowCount());
		assertEquals("Wrong number of commits", 1, getHistoryViewTable(PROJ2)
				.rowCount());

		assertEquals("Wrong commit message", ADDEDMESSAGE,
				getHistoryViewTable(PROJ1, SECONDFOLDER, ADDEDFILE)
						.getTableItem(0).getText(0));
		assertEquals("Wrong commit message", "Initial commit",
				getHistoryViewTable(PROJ1, FOLDER, FILE2).getTableItem(0)
						.getText(0));
	}

	@Test
	public void testOpenHistoryOnFileRepoFilter() throws Exception {
		initFilter(1);
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, FOLDER).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, FOLDER, FILE1).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, FOLDER, FILE2).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, SECONDFOLDER).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, SECONDFOLDER, ADDEDFILE).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ2).rowCount());
	}

	@Test
	public void testOpenHistoryOnFileProjectFilter() throws Exception {
		initFilter(2);
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, FOLDER).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, FOLDER, FILE1).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, FOLDER, FILE2).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, SECONDFOLDER).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, SECONDFOLDER, ADDEDFILE).rowCount());
		assertEquals("Wrong number of commits", 1, getHistoryViewTable(PROJ2)
				.rowCount());
	}

	@Test
	public void testOpenHistoryOnFileFolderFilter() throws Exception {
		initFilter(3);
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, FOLDER).rowCount());
		assertEquals("Wrong number of commits", commitCount - 1,
				getHistoryViewTable(PROJ1, FOLDER, FILE1).rowCount());
		assertEquals("Wrong number of commits", commitCount - 1,
				getHistoryViewTable(PROJ1, FOLDER, FILE2).rowCount());
		assertEquals("Wrong number of commits", commitCount,
				getHistoryViewTable(PROJ1, SECONDFOLDER).rowCount());
		assertEquals("Wrong number of commits", 1,
				getHistoryViewTable(PROJ1, SECONDFOLDER, ADDEDFILE).rowCount());
		assertEquals("Wrong number of commits", 1,
				getHistoryViewTable(PROJ2).rowCount());
	}

	/**
	 * @param filter
	 *            0: none, 1: repository, 2: project, 3: folder
	 * @throws Exception
	 */
	private void initFilter(int filter) throws Exception {
		getHistoryViewTable(PROJ1);
		SWTBotView view = bot
				.viewById("org.eclipse.team.ui.GenericHistoryView");
		SWTBotToolbarToggleButton folder = (SWTBotToolbarToggleButton) view
				.toolbarButton(UIText.GitHistoryPage_AllInParentTooltip);
		SWTBotToolbarToggleButton project = (SWTBotToolbarToggleButton) view
				.toolbarButton(UIText.GitHistoryPage_AllInProjectTooltip);
		SWTBotToolbarToggleButton repo = (SWTBotToolbarToggleButton) view
				.toolbarButton(UIText.GitHistoryPage_AllInRepoTooltip);
		switch (filter) {
		case 0:
			if (folder.isChecked())
				folder.click();
			if (project.isChecked())
				project.click();
			if (repo.isChecked())
				repo.click();
			break;
		case 1:
			if (!repo.isChecked())
				repo.click();
			break;
		case 2:
			if (!project.isChecked())
				project.click();
			break;
		case 3:
			if (!folder.isChecked())
				folder.click();
			break;
		default:
			break;
		}
	}

	@Test
	public void testOpenHistoryOnProject() throws Exception {
		SWTBotTable table = getHistoryViewTable(PROJ1);
		int rowCount = table.rowCount();
		assertTrue(table.rowCount() > 0);
		assertEquals(table.getTableItem(rowCount - 1).getText(0),
				"Initial commit");
	}

	@Test
	public void testAddCommit() throws Exception {
		String commitMessage = "The special commit";
		int countBefore = getHistoryViewTable(PROJ1).rowCount();
		touchAndSubmit(commitMessage);
		int countAfter = getHistoryViewTable(PROJ1).rowCount();
		assertEquals("Wrong number of entries", countBefore + 1, countAfter);
		assertEquals("Wrong comit message", commitMessage,
				getHistoryViewTable(PROJ1).getTableItem(0).getText(0));
	}

	/**
	 * @param path
	 *            must be length 2 or three (folder or file)
	 * @return the bale
	 * @throws Exception
	 */
	private SWTBotTable getHistoryViewTable(String... path) throws Exception {
		SWTBotTree projectExplorerTree = bot
				.viewById("org.eclipse.jdt.ui.PackageExplorer").bot().tree();
		SWTBotTreeItem explorerItem;
		if (path.length == 1)
			explorerItem = getProjectItem(projectExplorerTree, path[0]);
		else if (path.length == 2)
			explorerItem = getProjectItem(projectExplorerTree, path[0])
					.expand().getNode(path[1]);
		else
			explorerItem = getProjectItem(projectExplorerTree, path[0])
					.expand().getNode(path[1]).expand().getNode(path[2]);
		explorerItem.select();
		ContextMenuHelper.clickContextMenu(projectExplorerTree, "Show In",
				"History");
		// join GenerateHistoryJob
		Job.getJobManager().join(JobFamilies.GENERATE_HISTORY, null);
		// join UI update triggered by GenerateHistoryJob
		projectExplorerTree.widget.getDisplay().syncExec(new Runnable() {

			public void run() {
				// empty
			}
		});
		return bot.viewById("org.eclipse.team.ui.GenericHistoryView").bot()
				.table();
	}

	@Test
	public void testAddBranch() throws Exception {
		Repository repo = lookupRepository(repoFile);
		assertNull(repo.resolve(Constants.R_HEADS + "NewBranch"));
		SWTBotTable table = getHistoryViewTable(PROJ1);
		SWTBotTableItem item = table.getTableItem(0);
		item.select();
		ContextMenuHelper.clickContextMenu(table,
				UIText.GitHistoryPage_CreateBranchMenuLabel);
		SWTBotShell dialog = bot
				.shell(UIText.CreateBranchWizard_NewBranchTitle);
		dialog.bot().textWithId("BranchName").setText("NewBranch");
		// for some reason, checkboxwithlabel doesn't seem to work
		dialog.bot().checkBox().deselect();
		dialog.bot().button(IDialogConstants.FINISH_LABEL).click();
		assertNotNull(repo.resolve(Constants.R_HEADS + "NewBranch"));
	}

	@Test
	public void testAddTag() throws Exception {
		Repository repo = lookupRepository(repoFile);
		assertNull(repo.resolve(Constants.R_TAGS + "NewTag"));
		final SWTBotTable table = getHistoryViewTable(PROJ1);
		table.getTableItem(0).select();
		final RevCommit[] commit = new RevCommit[1];

		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				TableItem tableItem = table.widget.getSelection()[0];
				ensureTableItemLoaded(tableItem);
				commit[0] = (RevCommit) tableItem.getData();
			}
		});

		ContextMenuHelper.clickContextMenu(table,
				UIText.GitHistoryPage_CreateTagMenuLabel);
		SWTBotShell dialog = bot.shell(UIText.CreateTagDialog_NewTag);
		dialog.bot().textWithLabel(UIText.CreateTagDialog_tagName)
				.setText("NewTag");
		dialog.bot().styledTextWithLabel(UIText.CreateTagDialog_tagMessage)
				.setText("New Tag message");
		dialog.bot().button(IDialogConstants.OK_LABEL).click();
		TestUtil.joinJobs(JobFamilies.TAG);
		assertNotNull(repo.resolve(Constants.R_TAGS + "NewTag"));
	}

	@Test
	public void testCheckOut() throws Exception {
		Repository repo = lookupRepository(repoFile);
		assertEquals(Constants.MASTER, repo.getBranch());

		final SWTBotTable table = getHistoryViewTable(PROJ1);
		// check out the second line
		final RevCommit[] commit = checkoutLine(table, 1);
		assertEquals(commit[0].getId().name(), repo.getBranch());
	}

	@Test
	public void testShowAllBranches() throws Exception {
		toggleShowAllBranchesButton(true);
		final SWTBotTable table = getHistoryViewTable(PROJ1);
		int commits = getHistoryViewTable(PROJ1).rowCount();
		checkoutLine(table, 1);

		toggleShowAllBranchesButton(false);
		assertEquals("Wrong number of commits", commits - 1,
				getHistoryViewTable(PROJ1).rowCount());
		toggleShowAllBranchesButton(true);
		assertEquals("Wrong number of commits", commits,
				getHistoryViewTable(PROJ1).rowCount());
	}

	private RevCommit[] checkoutLine(final SWTBotTable table, int line)
			throws InterruptedException {
		table.getTableItem(line).select();
		final RevCommit[] commit = new RevCommit[1];

		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				TableItem tableItem = table.widget.getSelection()[0];
				ensureTableItemLoaded(tableItem);
				commit[0] = (RevCommit) tableItem.getData();
			}
		});

		ContextMenuHelper.clickContextMenu(table,
				UIText.GitHistoryPage_CheckoutMenuLabel);
		TestUtil.joinJobs(JobFamilies.CHECKOUT);
		return commit;
	}

	/**
	 * Workaround to ensure that the TableItem of a SWT table with style
	 * SWT_VIRTUAL is loaded.
	 * 
	 * @param item
	 */
	private static void ensureTableItemLoaded(TableItem item) {
		item.setText(item.getText()); // TODO: is there a better solution?
	}

	private void toggleShowAllBranchesButton(boolean checked) throws Exception{
		getHistoryViewTable(PROJ1);
		SWTBotView view = bot
				.viewById("org.eclipse.team.ui.GenericHistoryView");
		SWTBotToolbarToggleButton showAllBranches = (SWTBotToolbarToggleButton) view
				.toolbarButton(UIText.GitHistoryPage_showAllBranches);
		boolean isChecked = showAllBranches.isChecked();
		if(isChecked && !checked || !isChecked && checked)
			showAllBranches.click();
	}
}
