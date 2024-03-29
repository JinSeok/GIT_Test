/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.ui.internal.commit;

import java.io.IOException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIIcons;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.notes.NoteMap;
import org.eclipse.jgit.util.IO;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Repository commit note class.
 */
public class RepositoryCommitNote extends PlatformObject implements
		IWorkbenchAdapter {

	private RepositoryCommit commit;

	private Ref ref;

	private Note note;

	/**
	 * Create repository commit note
	 *
	 * @param commit
	 * @param ref
	 * @param note
	 */
	public RepositoryCommitNote(RepositoryCommit commit, Ref ref, Note note) {
		Assert.isNotNull(commit, "Commit cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(ref, "Ref cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(note, "Note cannot be null"); //$NON-NLS-1$
		this.commit = commit;
		this.ref = ref;
		this.note = note;
	}

	/**
	 * Get note text. This method open and read the note blob each time it is
	 * called.
	 *
	 * @return note text or empty string if lookup failed.
	 */
	public String getNoteText() {
		try {
			ObjectLoader loader = commit.getRepository().open(note.getData(),
					Constants.OBJ_BLOB);
			byte[] contents;
			if (loader.isLarge())
				contents = IO.readWholeStream(loader.openStream(),
						(int) loader.getSize()).array();
			else
				contents = loader.getCachedBytes();
			return new String(contents);
		} catch (IOException e) {
			Activator.logError("Error loading note text", e); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		if (RepositoryCommit.class == adapter)
			return this.commit;

		if (Repository.class == adapter)
			return this.commit.getRepository();

		return super.getAdapter(adapter);
	}

	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return UIIcons.NOTE;
	}

	public String getLabel(Object o) {
		return NoteMap.shortenRefName(ref.getName());
	}

	public Object getParent(Object o) {
		return commit;
	}

}
