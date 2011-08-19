/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.ui.internal;

import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An Editor input for file revisions
 */
class FileRevisionEditorInput extends PlatformObject implements
		IWorkbenchAdapter, IStorageEditorInput {

	private final Object fileRevision;

	private final IStorage storage;

	/**
	 * @param revision
	 *            the file revision
	 * @param monitor
	 * @return a file revision editor input
	 * @throws CoreException
	 */
	public static FileRevisionEditorInput createEditorInputFor(
			IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {
		IStorage storage = revision.getStorage(monitor);
		return new FileRevisionEditorInput(revision, storage);
	}

	private static IStorage wrapStorage(final IStorage storage,
			final String charset) {
		if (charset == null)
			return storage;
		if (storage instanceof IFileState) {
			return new IFileState() {
				public Object getAdapter(Class adapter) {
					return storage.getAdapter(adapter);
				}

				public boolean isReadOnly() {
					return storage.isReadOnly();
				}

				public String getName() {
					return storage.getName();
				}

				public IPath getFullPath() {
					return storage.getFullPath();
				}

				public InputStream getContents() throws CoreException {
					return storage.getContents();
				}

				public String getCharset() throws CoreException {
					return charset;
				}

				public boolean exists() {
					return ((IFileState) storage).exists();
				}

				public long getModificationTime() {
					return ((IFileState) storage).getModificationTime();
				}
			};
		}

		return new IEncodedStorage() {
			public Object getAdapter(Class adapter) {
				return storage.getAdapter(adapter);
			}

			public boolean isReadOnly() {
				return storage.isReadOnly();
			}

			public String getName() {
				return storage.getName();
			}

			public IPath getFullPath() {
				return storage.getFullPath();
			}

			public InputStream getContents() throws CoreException {
				return storage.getContents();
			}

			public String getCharset() throws CoreException {
				return charset;
			}
		};
	}

	/**
	 * @param revision
	 *            the file revision
	 * @param storage
	 *            the contents of the file revision
	 */
	public FileRevisionEditorInput(Object revision, IStorage storage) {
		Assert.isNotNull(revision);
		Assert.isNotNull(storage);
		this.fileRevision = revision;
		this.storage = storage;
	}

	/**
	 * @param state
	 *            the file state
	 */
	public FileRevisionEditorInput(IFileState state) {
		this(state, state);
	}

	/**
	 * @param revision
	 * @param storage
	 * @param charset
	 */
	public FileRevisionEditorInput(Object revision, IStorage storage,
			String charset) {
		this(revision, wrapStorage(storage, charset));
	}

	public IStorage getStorage() throws CoreException {
		return storage;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		IFileRevision rev = (IFileRevision) getAdapter(IFileRevision.class);
		if (rev != null)
			return NLS.bind(
					UIText.FileRevisionEditorInput_NameAndRevisionTitle,
					new String[] { rev.getName(), rev.getContentIdentifier() });
		IFileState state = (IFileState) getAdapter(IFileState.class);
		if (state != null)
			return state.getName()
					+ " " + DateFormat.getInstance().format(new Date(state.getModificationTime())); //$NON-NLS-1$
		return storage.getName();

	}

	public IPersistableElement getPersistable() {
		// can't persist
		return null;
	}

	public String getToolTipText() {
		return storage.getFullPath().toString();
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return this;
		if (adapter == IStorage.class)
			return storage;
		Object object = super.getAdapter(adapter);
		if (object != null)
			return object;
		return CompareUtils.getAdapter(fileRevision, adapter);
	}

	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		IFileRevision rev = (IFileRevision) getAdapter(IFileRevision.class);
		if (rev != null)
			return rev.getName();
		return storage.getName();
	}

	public Object getParent(Object o) {
		return null;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof FileRevisionEditorInput) {
			FileRevisionEditorInput other = (FileRevisionEditorInput) obj;
			return (other.fileRevision.equals(this.fileRevision));
		}
		return false;
	}

	public int hashCode() {
		return fileRevision.hashCode();
	}

	/**
	 * @return the revision
	 */
	public IFileRevision getFileRevision() {
		if (fileRevision instanceof IFileRevision) {
			return (IFileRevision) fileRevision;
		}
		return null;
	}

	/**
	 * @return the revision
	 */
	public URI getURI() {
		if (fileRevision instanceof IFileRevision) {
			IFileRevision fr = (IFileRevision) fileRevision;
			return fr.getURI();
		}
		return null;
	}
}
