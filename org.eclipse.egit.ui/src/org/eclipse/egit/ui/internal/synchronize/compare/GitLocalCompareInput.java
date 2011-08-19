/*******************************************************************************
 * Copyright (C) 2010, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.synchronize.compare;

import static org.eclipse.egit.ui.internal.CompareUtils.getFileCachedRevisionTypedElement;

import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.egit.ui.internal.LocalResourceTypedElement;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.team.ui.mapping.ISynchronizationCompareInput;

/**
 * Specific implementation of {@link ISynchronizationCompareInput} for showing
 * locally changed files
 */
public class GitLocalCompareInput extends GitCompareInput {

	private static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();

	/**
	 * Creates {@link GitLocalCompareInput}
	 *
	 * @param repo
	 *            repository that is connected with this object
	 * @param ancestorDataSource
	 *            data that should be use to obtain common ancestor object data
	 * @param baseDataSource
	 *            data that should be use to obtain base object data
	 * @param remoteDataSource
	 *            data that should be used to obtain remote object data
	 * @param gitPath
	 *            repository relative path of object
	 */
	public GitLocalCompareInput(Repository repo,
			ComparisonDataSource ancestorDataSource,
			ComparisonDataSource baseDataSource,
			ComparisonDataSource remoteDataSource, String gitPath) {
		super(repo, ancestorDataSource, baseDataSource, remoteDataSource,
				gitPath);
	}

	public ITypedElement getLeft() {
		String absoluteFilePath = repo.getWorkTree().getAbsolutePath()
				+ "/" + gitPath; //$NON-NLS-1$
		IFile file = ROOT.getFileForLocation(new Path(absoluteFilePath));

		if (file == null)
			return new LocalNonWorkspaceTypedElement(absoluteFilePath);

		return new LocalResourceTypedElement(file);
	}

	public ITypedElement getRight() {
		return getFileCachedRevisionTypedElement(gitPath, repo);
	}

	@Override
	public ITypedElement getAncestor() {
		return getRight();
	}

}
