/*******************************************************************************
 * Copyright (C) 2010, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.history.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.compare.ITypedElement;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.CompareUtils;
import org.eclipse.egit.ui.internal.EgitUiEditorUtils;
import org.eclipse.egit.ui.internal.GitCompareFileRevisionEditorInput;
import org.eclipse.egit.ui.internal.history.GitHistoryPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;

/**
 * Show versions/open.
 * <p>
 * If a single version is selected, open it, otherwise open several versions of
 * the file content.
 */
public class ShowVersionsHandler extends AbstractHistoryCommandHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean compareMode = Boolean.TRUE.toString().equals(
				event.getParameter(HistoryViewCommands.COMPARE_MODE_PARAM));
		IStructuredSelection selection = getSelection(getPage());
		if (selection.size() < 1)
			return null;
		Object input = getPage().getInputInternal().getSingleFile();
		if (input == null)
			return null;
		boolean errorOccurred = false;
		List<ObjectId> ids = new ArrayList<ObjectId>();
		String gitPath = null;
		if (input instanceof IFile) {
			IFile resource = (IFile) input;
			final RepositoryMapping map = RepositoryMapping
					.getMapping(resource);
			gitPath = map.getRepoRelativePath(resource);
			Iterator<?> it = selection.iterator();
			while (it.hasNext()) {
				RevCommit commit = (RevCommit) it.next();
				IFileRevision rev = null;
				try {
					rev = CompareUtils.getFileRevision(gitPath, commit, map
							.getRepository(), null);
				} catch (IOException e) {
					Activator.logError(NLS.bind(
							UIText.GitHistoryPage_errorLookingUpPath, gitPath,
							commit.getId()), e);
					errorOccurred = true;
				}
				if (rev != null) {
					if (compareMode) {
						ITypedElement right = CompareUtils
								.getFileRevisionTypedElement(gitPath, commit,
										map.getRepository());
						final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(
								SaveableCompareEditorInput
										.createFileElement(resource), right,
								null);
						try {
							openInCompare(event, in);
						} catch (Exception e) {
							errorOccurred = true;
						}
					} else {
						try {
							EgitUiEditorUtils.openEditor(getPart(event)
									.getSite().getPage(), rev,
									new NullProgressMonitor());
						} catch (CoreException e) {
							Activator.logError(
									UIText.GitHistoryPage_openFailed, e);
							errorOccurred = true;
						}
					}
				} else {
					ids.add(commit.getId());
				}
			}
		}
		if (input instanceof File) {
			File fileInput = (File) input;
			Repository repo = getRepository(event);
			gitPath = getRepoRelativePath(repo, fileInput);
			Iterator<?> it = selection.iterator();
			while (it.hasNext()) {
				RevCommit commit = (RevCommit) it.next();
				IFileRevision rev = null;
				try {
					rev = CompareUtils.getFileRevision(gitPath, commit, repo,
							null);
				} catch (IOException e) {
					Activator.logError(NLS.bind(
							UIText.GitHistoryPage_errorLookingUpPath, gitPath,
							commit.getId()), e);
					errorOccurred = true;
				}
				if (rev != null) {
					if (compareMode) {
						try {
							ITypedElement left = CompareUtils
									.getFileRevisionTypedElement(gitPath,
											new RevWalk(repo).parseCommit(repo
													.resolve(Constants.HEAD)),
											repo);
							ITypedElement right = CompareUtils
									.getFileRevisionTypedElement(gitPath,
											commit, repo);
							final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(
									left, right, null);
							openInCompare(event, in);
						} catch (IOException e) {
							errorOccurred = true;
						}
					} else {
						try {
							EgitUiEditorUtils.openEditor(getPart(event)
									.getSite().getPage(), rev,
									new NullProgressMonitor());
						} catch (CoreException e) {
							Activator.logError(
									UIText.GitHistoryPage_openFailed, e);
							errorOccurred = true;
						}
					}
				} else {
					ids.add(commit.getId());
				}
			}
		}
		if (errorOccurred)
			Activator.showError(UIText.GitHistoryPage_openFailed, null);
		if (ids.size() > 0) {
			StringBuilder idList = new StringBuilder(""); //$NON-NLS-1$
			for (ObjectId objectId : ids) {
				idList.append(objectId.getName()).append(' ');
			}
			MessageDialog.openError(getPart(event).getSite().getShell(),
					UIText.GitHistoryPage_fileNotFound, NLS.bind(
							UIText.GitHistoryPage_notContainedInCommits,
							gitPath, idList.toString()));
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		GitHistoryPage page = getPage();
		if (page == null)
			return false;
		int size = getSelection(page).size();
		if (size == 0)
			return false;
		return page.getInputInternal().isSingleFile();
	}
}
