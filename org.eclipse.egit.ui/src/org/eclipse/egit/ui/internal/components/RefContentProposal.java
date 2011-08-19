/*******************************************************************************
 * Copyright (C) 2008, Marek Zawirski <marek.zawirski@gmail.com>
 * Copyright (C) 2010, Chris Aniszczyk <caniszczyk@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.components;

import java.io.IOException;

import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.osgi.util.NLS;

/**
 * Content proposal class for refs names, specifically Ref objects - name with
 * optionally associated object id. This class can be used for Eclipse field
 * assist as content proposal.
 * <p>
 * Content of this proposal is simply a ref name, but description and labels
 * tries to be smarter - showing easier to read label for user (stripping
 * prefixes) and information about pointed object if it exists locally.
 */
public class RefContentProposal implements IContentProposal {
	private static final String PREFIXES[] = new String[] { Constants.R_HEADS,
			Constants.R_REMOTES, Constants.R_TAGS };

	private final static String branchPF = " ["   //$NON-NLS-1$
		+ UIText.RefContentProposal_branch
		+ "]";  //$NON-NLS-1$

	private final static String trackingBranchPF = " ["  //$NON-NLS-1$
		+ UIText.RefContentProposal_trackingBranch
		+ "]";  //$NON-NLS-1$

	private final static String tagPF = " ["  //$NON-NLS-1$
		+ UIText.RefContentProposal_tag
		+ "]"; //$NON-NLS-1$

	private static final String PREFIXES_DESCRIPTIONS[] = new String[] {
			branchPF, trackingBranchPF, tagPF };

	private static void appendObjectSummary(final StringBuilder sb,
			final String type, final PersonIdent author, final String message) {
		sb.append(type);
		sb.append(" "); //$NON-NLS-1$
		sb.append(UIText.RefContentProposal_by);
		sb.append(" "); //$NON-NLS-1$
		sb.append(author.getName());
		sb.append("\n");  //$NON-NLS-1$
		sb.append(author.getWhen());
		sb.append("\n\n");  //$NON-NLS-1$
		final int newLine = message.indexOf('\n');
		final int last = (newLine != -1 ? newLine : message.length());
		sb.append(message.substring(0, last));
	}

	private final Repository db;

	private final String refName;

	private final ObjectId objectId;

	/**
	 * Create content proposal for specified ref.
	 *
	 * @param repo
	 *            repository for accessing information about objects. Could be a
	 *            local repository even for remote objects.
	 * @param ref
	 *            ref being a content proposal. May have null or locally
	 *            non-existent object id.
	 */
	public RefContentProposal(final Repository repo, final Ref ref) {
		this(repo, ref.getName(), ref.getObjectId());
	}

	/**
	 * Create content proposal for specified ref name and object id.
	 *
	 * @param repo
	 *            repository for accessing information about objects. Could be a
	 *            local repository even for remote objects.
	 * @param refName
	 *            ref name being a content proposal.
	 * @param objectId
	 *            object being pointed by this ref name. May be null or locally
	 *            non-existent object.
	 */
	public RefContentProposal(final Repository repo, final String refName,
			final ObjectId objectId) {
		this.db = repo;
		this.refName = refName;
		this.objectId = objectId;
	}

	public String getContent() {
		return refName;
	}

	public int getCursorPosition() {
		return refName.length();
	}

	public String getDescription() {
		if (objectId == null)
			return null;
		ObjectReader reader = db.newObjectReader();
		try {
			final ObjectLoader loader = reader.open(objectId);
			final StringBuilder sb = new StringBuilder();
			sb.append(refName);
			sb.append('\n');
			sb.append(reader.abbreviate(objectId).name());
			sb.append(" - "); //$NON-NLS-1$

			switch (loader.getType()) {
			case Constants.OBJ_COMMIT:
				RevCommit c = new RevWalk(db).parseCommit(objectId);
				appendObjectSummary(sb, UIText.RefContentProposal_commit, c
						.getAuthorIdent(), c.getFullMessage());
				break;
			case Constants.OBJ_TAG:
				RevWalk walk = new RevWalk(db);
				RevTag t = walk.parseTag(objectId);
				appendObjectSummary(sb, UIText.RefContentProposal_tag, t
						.getTaggerIdent(), t.getFullMessage());
				break;
			case Constants.OBJ_TREE:
				sb.append(UIText.RefContentProposal_tree);
				break;
			case Constants.OBJ_BLOB:
				sb.append(UIText.RefContentProposal_blob);
				break;
			default:
				sb.append(UIText.RefContentProposal_unknownObject);
			}
			return sb.toString();
		} catch (IOException e) {
			Activator.logError(NLS.bind(
					UIText.RefContentProposal_errorReadingObject, objectId), e);
			return null;
		} finally {
			reader.release();
		}
	}

	public String getLabel() {
		for (int i = 0; i < PREFIXES.length; i++)
			if (refName.startsWith(PREFIXES[i]))
				return refName.substring(PREFIXES[i].length())
						+ PREFIXES_DESCRIPTIONS[i];
		return refName;

	}
}
