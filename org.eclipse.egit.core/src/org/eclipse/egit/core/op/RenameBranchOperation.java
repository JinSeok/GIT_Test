/*******************************************************************************
 * Copyright (C) 2010, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core.op;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.CoreText;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.osgi.util.NLS;

/**
 * This class implements renaming of a branch
 */
public class RenameBranchOperation implements IEGitOperation {
	private final Repository repository;

	private final Ref branch;

	private final String newName;

	/**
	 * @param repository
	 * @param branch
	 *            the branch to rename
	 * @param newName
	 *            the new name
	 */
	public RenameBranchOperation(Repository repository, Ref branch,
			String newName) {
		this.repository = repository;
		this.branch = branch;
		this.newName = newName;
	}

	public void execute(IProgressMonitor m) throws CoreException {
		IProgressMonitor monitor;
		if (m == null)
			monitor = new NullProgressMonitor();
		else
			monitor = m;

		IWorkspaceRunnable action = new IWorkspaceRunnable() {
			public void run(IProgressMonitor actMonitor) throws CoreException {
				String taskName = NLS.bind(
						CoreText.RenameBranchOperation_TaskName, branch
								.getName(), newName);
				actMonitor.beginTask(taskName, 1);
				try {
					new Git(repository).branchRename().setOldName(
							branch.getName()).setNewName(newName).call();
				} catch (JGitInternalException e) {
					throw new CoreException(Activator.error(e.getMessage(), e));
				} catch (RefNotFoundException e) {
					throw new CoreException(Activator.error(e.getMessage(), e));
				} catch (InvalidRefNameException e) {
					throw new CoreException(Activator.error(e.getMessage(), e));
				} catch (RefAlreadyExistsException e) {
					throw new CoreException(Activator.error(e.getMessage(), e));
				} catch (DetachedHeadException e) {
					throw new CoreException(Activator.error(e.getMessage(), e));
				}
				actMonitor.worked(1);
				actMonitor.done();
			}
		};
		// lock workspace to protect working tree changes
		ResourcesPlugin.getWorkspace().run(action, monitor);
	}

	public ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
}
