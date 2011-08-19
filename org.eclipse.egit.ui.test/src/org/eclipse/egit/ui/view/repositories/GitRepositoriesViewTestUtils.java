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
package org.eclipse.egit.ui.view.repositories;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.eclipse.egit.ui.JobFamilies;
import org.eclipse.egit.ui.internal.repository.RepositoriesViewLabelProvider;
import org.eclipse.egit.ui.internal.repository.tree.BranchesNode;
import org.eclipse.egit.ui.internal.repository.tree.LocalNode;
import org.eclipse.egit.ui.internal.repository.tree.RemoteTrackingNode;
import org.eclipse.egit.ui.internal.repository.tree.RemotesNode;
import org.eclipse.egit.ui.internal.repository.tree.RepositoryNode;
import org.eclipse.egit.ui.internal.repository.tree.AdditionalRefsNode;
import org.eclipse.egit.ui.internal.repository.tree.TagsNode;
import org.eclipse.egit.ui.internal.repository.tree.WorkingDirNode;
import org.eclipse.egit.ui.test.TestUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class GitRepositoriesViewTestUtils {

	protected static final TestUtil myUtil = new TestUtil();
	// the human-readable view name
	protected final static String viewName = myUtil
			.getPluginLocalizedValue("GitRepositoriesView_name");
	// the human readable Git category
	private final static String gitCategory = myUtil
			.getPluginLocalizedValue("GitCategory_name");

	private final RepositoriesViewLabelProvider labelProvider = new RepositoriesViewLabelProvider();

	public SWTBotTreeItem getLocalBranchesItem(SWTBotTree tree, File repo)
			throws Exception {
		Repository repository = lookupRepository(repo);
		RepositoryNode root = new RepositoryNode(null, repository);
		BranchesNode branches = new BranchesNode(root, repository);
		LocalNode localBranches = new LocalNode(branches,
				repository);

		String rootText = labelProvider.getStyledText(root).getString();
		SWTBotTreeItem rootItem = tree.getTreeItem(rootText);
		SWTBotTreeItem branchesItem = rootItem.expand().getNode(
				labelProvider.getStyledText(branches).getString());
		SWTBotTreeItem localItem = branchesItem.expand().getNode(
				labelProvider.getStyledText(localBranches).getString());
		return localItem;
	}

	public SWTBotTreeItem getTagsItem(SWTBotTree tree, File repo)
			throws Exception {
		Repository repository = lookupRepository(repo);
		RepositoryNode root = new RepositoryNode(null, repository);
		TagsNode tags = new TagsNode(root, repository);

		String rootText = labelProvider.getStyledText(root).getString();
		SWTBotTreeItem rootItem = tree.getTreeItem(rootText);
		SWTBotTreeItem tagsItem = rootItem.expand().getNode(
				labelProvider.getStyledText(tags).getString());
		return tagsItem;
	}

	public SWTBotTreeItem getRemoteBranchesItem(SWTBotTree tree,
			File repositoryFile) throws Exception {
		Repository repository = lookupRepository(repositoryFile);
		RepositoryNode root = new RepositoryNode(null, repository);
		BranchesNode branches = new BranchesNode(root, repository);
		RemoteTrackingNode remoteBranches = new RemoteTrackingNode(branches,
				repository);

		String rootText = labelProvider.getStyledText(root).getString();
		SWTBotTreeItem rootItem = tree.getTreeItem(rootText);
		SWTBotTreeItem branchesItem = rootItem.expand().getNode(
				labelProvider.getStyledText(branches).getString());
		SWTBotTreeItem remoteItem = branchesItem.expand().getNode(
				labelProvider.getStyledText(remoteBranches).getString());
		return remoteItem;
	}

	public SWTBotTreeItem getWorkdirItem(SWTBotTree tree, File repositoryFile)
			throws Exception {
		Repository repository = lookupRepository(repositoryFile);
		RepositoryNode root = new RepositoryNode(null, repository);

		WorkingDirNode workdir = new WorkingDirNode(root, repository);

		String rootText = labelProvider.getStyledText(root).getString();
		SWTBotTreeItem rootItem = tree.getTreeItem(rootText);
		SWTBotTreeItem workdirItem = rootItem.expand().getNode(
				labelProvider.getStyledText(workdir).getString());
		return workdirItem;
	}

	public SWTBotTreeItem getRootItem(SWTBotTree tree, File repositoryFile)
			throws Exception {
		Repository repository = lookupRepository(repositoryFile);
		RepositoryNode root = new RepositoryNode(null, repository);
		String rootText = labelProvider.getStyledText(root).getString();
		SWTBotTreeItem rootItem = tree.getTreeItem(rootText);
		return rootItem;
	}

	public SWTBotTreeItem getSymbolicRefsItem(SWTBotTree tree,
			File repositoryFile) throws Exception {
		Repository repository = lookupRepository(repositoryFile);
		RepositoryNode root = new RepositoryNode(null, repository);
		AdditionalRefsNode symrefsnode = new AdditionalRefsNode(root, repository);
		SWTBotTreeItem rootItem = tree.getTreeItem(
				labelProvider.getStyledText(root).getString()).expand();
		SWTBotTreeItem symrefsitem = rootItem.getNode(labelProvider
				.getText(symrefsnode));
		return symrefsitem;
	}

	public SWTBotTreeItem getRemotesItem(SWTBotTree tree, File repositoryFile)
			throws Exception {
		Repository repository = lookupRepository(repositoryFile);
		RepositoryNode root = new RepositoryNode(null, repository);
		RemotesNode remotes = new RemotesNode(root, repository);

		String rootText = labelProvider.getStyledText(root).getString();
		SWTBotTreeItem rootItem = tree.getTreeItem(rootText);
		SWTBotTreeItem remotesItem = rootItem.expand().getNode(
				labelProvider.getStyledText(remotes).getString());
		return remotesItem;
	}

	public Repository lookupRepository(File directory) throws Exception {
		return org.eclipse.egit.core.Activator.getDefault()
				.getRepositoryCache().lookupRepository(directory);
	}

	public SWTBotView openRepositoriesView(SWTWorkbenchBot bot)
			throws Exception {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		SWTBotShell shell = bot.shell("Show View").activate();
		shell.bot().tree().expandNode(gitCategory).getNode(viewName).select();
		shell.bot().button(IDialogConstants.OK_LABEL).click();
		TestUtil.joinJobs(JobFamilies.REPO_VIEW_REFRESH);
		SWTBotView viewbot = bot.viewByTitle(viewName);
		assertNotNull("Repositories View should not be null", viewbot);
		return viewbot;
	}
}
