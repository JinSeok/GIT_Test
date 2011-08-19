/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.ui.internal;

import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.ui.UIText;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;
import org.eclipse.team.core.history.provider.FileRevision;

/**
 * A LocalFileRevision is used for wrapping local files in order to display them
 * in the History View. As such, this class can be used to wrap either a local
 * file's IFileState or an IFile.
 */
public class LocalFileRevision extends FileRevision {
	/*
	 * Either one or the other of these fields is intended to be used.
	 */
	// Used for wrapping local file history items
	private IFileState state;

	// Used for displaying the "real" current file
	private IFile file;

	// Used for displaying which base revision
	private IFileRevision baseRevision;

	/**
	 * @param state
	 *            the state to wrap
	 */
	public LocalFileRevision(IFileState state) {
		this.state = state;
		this.file = null;
		this.baseRevision = null;
	}

	/**
	 * @param file
	 *            the file to wrap<br>
	 *            This is generally used to represent the local current version
	 *            of the file being displayed in the history. Make sure to also
	 *            pass in the base revision associated with this current version
	 *            (see {@link #setBaseRevision(IFileRevision)})
	 */
	public LocalFileRevision(IFile file) {
		this.file = file;
		this.baseRevision = null;
		this.state = null;
	}

	public String getContentIdentifier() {
		if (file != null)
			return baseRevision == null ? NLS.bind(
					UIText.LocalFileRevision_CurrentVersion, "") : NLS.bind(UIText.LocalFileRevision_CurrentVersion, baseRevision.getContentIdentifier()); //$NON-NLS-1$
		return ""; //$NON-NLS-1$
	}

	public String getAuthor() {
		return ""; //$NON-NLS-1$
	}

	public String getComment() {
		if (file != null)
			return UIText.LocalFileRevision_currentVersionTag;
		return null;
	}

	public ITag[] getTags() {
		return new ITag[0];
	}

	public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
		if (file != null) {
			return file;
		}
		return state;
	}

	public String getName() {
		if (file != null) {
			return file.getName();
		}

		return state.getName();
	}

	public long getTimestamp() {
		if (file != null) {
			return file.getLocalTimeStamp();
		}

		return state.getModificationTime();
	}

	public boolean exists() {
		if (file != null) {
			return file.exists();
		}

		return state.exists();
	}

	/**
	 * @param baseRevision
	 * <br>
	 *            Can be used to associate a base revision with an IFile.
	 */
	public void setBaseRevision(IFileRevision baseRevision) {
		this.baseRevision = baseRevision;
	}

	public boolean isPropertyMissing() {
		return true;
	}

	public IFileRevision withAllProperties(IProgressMonitor monitor) {
		return this;
	}

	/**
	 * @param revision
	 * @return the result
	 */
	public boolean isPredecessorOf(IFileRevision revision) {
		long compareRevisionTime = revision.getTimestamp();
		return (this.getTimestamp() < compareRevisionTime);
	}

	/**
	 * @param revision
	 * @return the result
	 */
	public boolean isDescendentOf(IFileRevision revision) {
		long compareRevisionTime = revision.getTimestamp();
		return (this.getTimestamp() > compareRevisionTime);
	}

	public URI getURI() {
		if (file != null)
			return file.getLocationURI();

		return URIUtil.toURI(state.getFullPath());
	}

	/**
	 * @return the file
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @return the state
	 */
	public IFileState getState() {
		return state;
	}

	/**
	 * @return true or false
	 */
	public boolean isCurrentState() {
		return file != null;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof LocalFileRevision) {
			LocalFileRevision other = (LocalFileRevision) obj;
			if (file != null && other.file != null)
				return file.equals(other.file);
			if (state != null && other.state != null)
				return statesEqual(state, other.state);
		}
		return false;
	}

	private boolean statesEqual(IFileState s1, IFileState s2) {
		return (s1.getFullPath().equals(s2.getFullPath()) && s1
				.getModificationTime() == s2.getModificationTime());
	}

	public int hashCode() {
		if (file != null)
			return file.hashCode();
		if (state != null)
			return (int) state.getModificationTime();
		return super.hashCode();
	}
}
