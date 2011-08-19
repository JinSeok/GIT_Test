/*******************************************************************************
 * Copyright (C) 2008, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core.internal.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.CoreText;
import org.eclipse.egit.core.GitTag;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;

/**
 * An {@link IFileRevision} for a version of a specified resource in the
 * specified commit (revision).
 */
class CommitFileRevision extends GitFileRevision {
	private final Repository db;

	private final RevCommit commit;

	private final PersonIdent author;

	private final String path;

	private ObjectId blobId;

	CommitFileRevision(final Repository repo, final RevCommit rc,
			final String fileName) {
		this(repo, rc, fileName, null);
	}

	CommitFileRevision(final Repository repo, final RevCommit rc,
			final String fileName, final ObjectId blob) {
		super(fileName);
		db = repo;
		commit = rc;
		author = rc.getAuthorIdent();
		path = fileName;
		blobId = blob;
	}

	String getGitPath() {
		return path;
	}

	public IStorage getStorage(final IProgressMonitor monitor)
			throws CoreException {
		if (blobId == null)
			blobId = locateBlobObjectId();
		return new CommitBlobStorage(db, path, blobId, commit);
	}

	public long getTimestamp() {
		return author != null ? author.getWhen().getTime() : 0;
	}

	public String getContentIdentifier() {
		return commit.getId().name();
	}

	public String getAuthor() {
		return author != null ? author.getName() : null;
	}

	public String getComment() {
		return commit.getShortMessage();
	}

	public String toString() {
		return commit.getId() + ":" + path;  //$NON-NLS-1$
	}

	public ITag[] getTags() {
		final Collection<GitTag> ret = new ArrayList<GitTag>();
		for (final Map.Entry<String, Ref> tag : db.getTags().entrySet()) {
			final ObjectId ref = tag.getValue().getPeeledObjectId();
			if (ref == null)
				continue;
			if (!AnyObjectId.equals(ref, commit))
				continue;
			ret.add(new GitTag(tag.getKey()));
		}
		return ret.toArray(new ITag[ret.size()]);
	}

	/**
	 * Get the commit that introduced this file revision.
	 *
	 * @return the commit we most recently noticed this file in.
	 */
	public RevCommit getRevCommit() {
		return commit;
	}

	private ObjectId locateBlobObjectId() throws CoreException {
		try {
			final TreeWalk w = TreeWalk.forPath(db, path, commit.getTree());
			if (w == null)
				throw new CoreException(Activator.error(NLS.bind(
						CoreText.CommitFileRevision_pathNotIn, commit.getId().name(),
						path), null));
			return w.getObjectId(0);
		} catch (IOException e) {
			throw new CoreException(Activator.error(NLS.bind(
					CoreText.CommitFileRevision_errorLookingUpPath, commit
							.getId().name(), path), e));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((blobId == null) ? 0 : blobId.hashCode());
		result = prime * result + ((commit == null) ? 0 : commit.hashCode());
		result = prime * result + ((db == null) ? 0 : db.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommitFileRevision other = (CommitFileRevision) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (blobId == null) {
			if (other.blobId != null)
				return false;
		} else if (!blobId.equals(other.blobId))
			return false;
		if (commit == null) {
			if (other.commit != null)
				return false;
		} else if (!commit.equals(other.commit))
			return false;
		if (db == null) {
			if (other.db != null)
				return false;
		} else if (!db.equals(other.db))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
