/*******************************************************************************
 * Copyright (C) 2010, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core.op;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.egit.core.CoreText;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TagBuilder;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.TeamException;

/**
 * Tags repository with given {@link TagBuilder} object.
 */
public class TagOperation implements IEGitOperation {

	private final TagBuilder tag;
	private final Repository repo;
	private final boolean shouldMoveTag;

	/**
	 * Construct TagOperation
	 *
	 * @param repo
	 * @param tag
	 * @param shouldMoveTag if <code>true</code> it will replace tag with same name
	 */
	public TagOperation(Repository repo, TagBuilder tag, boolean shouldMoveTag) {
		this.tag = tag;
		this.repo = repo;
		this.shouldMoveTag = shouldMoveTag;
	}


	public void execute(IProgressMonitor m) throws CoreException {
		IProgressMonitor monitor;
		if (m == null)
			monitor = new NullProgressMonitor();
		else
			monitor = m;
		try {
			monitor.beginTask(NLS.bind(CoreText.TagOperation_performingTagging,
					tag.getTag()), 3);

			ObjectId tagId = updateTagObject();
			monitor.worked(1);

			updateRepo(tagId);
			monitor.worked(1);

		} finally {
			monitor.done();
		}
	}

	private void updateRepo(ObjectId tagId) throws TeamException {
		String refName = Constants.R_TAGS + tag.getTag();

		try {
			RefUpdate tagRef = repo.updateRef(refName);
			tagRef.setNewObjectId(tagId);

			tagRef.setForceUpdate(shouldMoveTag);
			Result updateResult = tagRef.update();

			if (updateResult != Result.NEW && updateResult != Result.FORCED)
				throw new TeamException(NLS.bind(CoreText.TagOperation_taggingFailure,
						tag.getTag(), updateResult));
		} catch (IOException e) {
			throw new TeamException(NLS.bind(CoreText.TagOperation_taggingFailure,
					tag.getTag(), e.getMessage()), e);
		}
	}

	private ObjectId updateTagObject() throws TeamException {
		ObjectId startPointRef = tag.getObjectId();

		try {
			ObjectId tagId;
			repo.open(startPointRef);
			ObjectInserter inserter = repo.newObjectInserter();
			try {
				tagId = inserter.insert(tag);
				inserter.flush();
			} finally {
				inserter.release();
			}
			return tagId;
		} catch (IOException e) {
			throw new TeamException(NLS.bind(CoreText.TagOperation_objectIdNotFound,
					tag.getTag(), e.getMessage()), e);
		}
	}


	public ISchedulingRule getSchedulingRule() {
		return null;
	}

}
