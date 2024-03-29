/*******************************************************************************
 * Copyright (C) 2008, Marek Zawirski <marek.zawirski@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.fetch;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;

/**
 * Content provided for fetch result table viewer.
 * <p>
 * Input of this provided must be {@link FetchResult} instance, while returned
 * elements are instances of {@link TrackingRefUpdate}. Input may be null (no
 * elements).
 *
 * @see FetchResult
 * @see TrackingRefUpdate
 */
class TrackingRefUpdateContentProvider implements IStructuredContentProvider {
	public Object[] getElements(final Object inputElement) {
		if (inputElement == null)
			return new TrackingRefUpdate[0];

		final FetchResult result = (FetchResult) inputElement;
		return result.getTrackingRefUpdates().toArray(new TrackingRefUpdate[0]);
	}

	public void dispose() {
		// nothing to do
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing to do
	}
}
