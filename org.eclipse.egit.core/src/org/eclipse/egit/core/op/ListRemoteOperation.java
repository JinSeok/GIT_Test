/*******************************************************************************
 * Copyright (C) 2008, Marek Zawirski <marek.zawirski@gmail.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core.op;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.CoreText;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.Connection;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;

/**
 * Operation of listing remote repository advertised refs.
 */
public class ListRemoteOperation {
	private final Repository localDb;

	private final URIish uri;

	private final int timeout;

	private Map<String, Ref> remoteRefsMap;

	private CredentialsProvider credentialsProvider;

	/**
	 * Create listing operation for specified local repository (needed by
	 * transport) and remote repository URI.
	 *
	 * @param localDb
	 *            local repository (needed for transport) where fetch would
	 *            occur.
	 * @param uri
	 *            URI of remote repository to list.
	 * @param timeout
	 *            timeout is seconds; 0 means no timeout
	 */
	public ListRemoteOperation(final Repository localDb, final URIish uri,
			int timeout) {
		this.localDb = localDb;
		this.uri = uri;
		this.timeout = timeout;
	}

	/**
	 * @return collection of refs advertised by remote side.
	 * @throws IllegalStateException
	 *             if error occurred during earlier remote refs listing.
	 */
	public Collection<Ref> getRemoteRefs() {
		checkState();
		return remoteRefsMap.values();
	}

	/**
	 * @param refName
	 *            remote ref name to search for.
	 * @return ref with specified refName or null if not found.
	 * @throws IllegalStateException
	 *             if error occurred during earlier remote refs listing.
	 */
	public Ref getRemoteRef(final String refName) {
		checkState();
		return remoteRefsMap.get(refName);
	}

	/**
	 * Sets a credentials provider
	 * @param credentialsProvider
	 */
	public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
		this.credentialsProvider = credentialsProvider;
	}

	/**
	 * @param pm
	 *            the monitor to be used for reporting progress and responding
	 *            to cancellation. The monitor is never <code>null</code>
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public void run(IProgressMonitor pm) throws InvocationTargetException,
			InterruptedException {
		Transport transport = null;
		Connection connection = null;
		try {
			transport = Transport.open(localDb, uri);
			if (credentialsProvider != null)
				transport.setCredentialsProvider(credentialsProvider);
			transport.setTimeout(this.timeout);

			if (pm != null)
				pm.beginTask(CoreText.ListRemoteOperation_title,
						IProgressMonitor.UNKNOWN);
			connection = transport.openFetch();
			remoteRefsMap = connection.getRefsMap();
		} catch (NotSupportedException e) {
			throw new InvocationTargetException(e);
		} catch (TransportException e) {
			throw new InvocationTargetException(e);
		} finally {
			if (connection != null)
				connection.close();
			if (transport != null)
				transport.close();
			if (pm != null)
				pm.done();
		}
	}

	private void checkState() {
		if (remoteRefsMap == null)
			throw new IllegalStateException(
					"Error occurred during remote repo " +  //$NON-NLS-1$
					"listing, no refs available"); //$NON-NLS-1$
	}
}
