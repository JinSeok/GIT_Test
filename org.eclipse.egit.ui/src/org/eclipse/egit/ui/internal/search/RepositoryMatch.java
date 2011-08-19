/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.ui.internal.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.egit.ui.UIIcons;
import org.eclipse.egit.ui.internal.commit.RepositoryCommit;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Repository match class.
 */
public class RepositoryMatch extends PlatformObject implements
		IWorkbenchAdapter {

	private List<RepositoryCommit> commits = new ArrayList<RepositoryCommit>();

	private Repository repository;

	/**
	 * Create repository match
	 *
	 * @param repository
	 */
	public RepositoryMatch(Repository repository) {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		this.repository = repository;
	}

	public int hashCode() {
		return 31 * repository.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RepositoryMatch))
			return false;
		RepositoryMatch other = (RepositoryMatch) obj;
		return repository.getDirectory()
				.equals(other.repository.getDirectory());
	}

	/**
	 * Get repository
	 *
	 * @return repository
	 */
	public Repository getRepository() {
		return this.repository;
	}

	/**
	 * Add commit
	 *
	 * @param commit
	 * @return this match
	 */
	public RepositoryMatch addCommit(RepositoryCommit commit) {
		if (commit != null)
			commits.add(commit);
		return this;
	}

	/**
	 * Get match count
	 *
	 * @return number of matches
	 */
	public int getMatchCount() {
		return this.commits.size();
	}

	public Object[] getChildren(Object o) {
		return this.commits.toArray();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return UIIcons.REPOSITORY;
	}

	public String getLabel(Object o) {
		if (repository.isBare())
			return repository.getDirectory().getName();
		else
			return repository.getDirectory().getParentFile().getName();
	}

	public Object getParent(Object o) {
		return null;
	}

}
