/*******************************************************************************
 * Copyright (C) 2011, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.clone;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.egit.ui.UIText;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * Utilities for creating projects
 */
public class ProjectUtils {
	/**
	 * @param projectsToCreate
	 *            the projects to create
	 * @param repository
	 *            if not null, the projects will be automatically shared
	 * @param selectedWorkingSets
	 *            the workings sets to add the created projects to, may be null
	 *            or empty
	 * @param monitor
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public static void createProjects(
			final Set<ProjectRecord> projectsToCreate,
			final Repository repository,
			final IWorkingSet[] selectedWorkingSets, IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
		IWorkspaceRunnable wsr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor actMonitor) throws CoreException {
				IWorkingSetManager workingSetManager = PlatformUI
						.getWorkbench().getWorkingSetManager();
				try {
					actMonitor.beginTask("", projectsToCreate.size()); //$NON-NLS-1$
					if (actMonitor.isCanceled())
						throw new OperationCanceledException();
					for (ProjectRecord projectRecord : projectsToCreate) {
						if (actMonitor.isCanceled())
							throw new OperationCanceledException();
						actMonitor.setTaskName(projectRecord.getProjectLabel());
						IProject project = createExistingProject(projectRecord,
								new SubProgressMonitor(actMonitor, 1));
						if (repository != null) {
							ConnectProviderOperation connectProviderOperation = new ConnectProviderOperation(
									project, repository.getDirectory());
							connectProviderOperation.execute(actMonitor);
						}
						if (selectedWorkingSets != null
								&& selectedWorkingSets.length > 0)
							workingSetManager.addToWorkingSets(project,
									selectedWorkingSets);
					}
				} finally {
					actMonitor.done();
				}
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wsr, monitor);
		} catch (OperationCanceledException e) {
			throw new InterruptedException();
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}
	}

	private static IProject createExistingProject(final ProjectRecord record,
			IProgressMonitor monitor) throws CoreException {
		String projectName = record.getProjectName();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(projectName);
		if (record.getProjectDescription() == null) {
			// error case
			record.setProjectDescription(workspace
					.newProjectDescription(projectName));
			IPath locationPath = new Path(record.getProjectSystemFile()
					.getAbsolutePath());

			// If it is under the root use the default location
			if (Platform.getLocation().isPrefixOf(locationPath))
				record.getProjectDescription().setLocation(null);
			else
				record.getProjectDescription().setLocation(locationPath);
		} else
			record.getProjectDescription().setName(projectName);

		try {
			monitor.beginTask(
					UIText.WizardProjectsImportPage_CreateProjectsTask, 100);
			project.create(record.getProjectDescription(),
					new SubProgressMonitor(monitor, 30));
			project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 50));
			return project;
		} finally {
			monitor.done();
		}
	}
}