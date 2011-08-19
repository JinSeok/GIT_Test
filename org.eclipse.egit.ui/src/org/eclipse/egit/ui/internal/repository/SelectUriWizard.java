/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mathias Kinzler (SAP AG) - initial implementation
 *******************************************************************************/
package org.eclipse.egit.ui.internal.repository;

import org.eclipse.egit.core.securestorage.UserPasswordCredentials;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.SecureStoreUtils;
import org.eclipse.egit.ui.internal.components.RepositorySelectionPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.transport.URIish;

/**
 * Wizard to select a URI
 */
public class SelectUriWizard extends Wizard {
	private URIish uri;
	private RepositorySelectionPage page;


	/**
	 * @param sourceSelection
	 */
	public SelectUriWizard(boolean sourceSelection) {
		page = new RepositorySelectionPage(sourceSelection, null);
		addPage(page);
		setWindowTitle(UIText.SelectUriWiazrd_Title);
	}

	/**
	 * @param sourceSelection
	 * @param presetUri
	 */
	public SelectUriWizard(boolean sourceSelection, String presetUri) {
		page = new RepositorySelectionPage(sourceSelection, presetUri);
		addPage(page);
		setWindowTitle(UIText.SelectUriWiazrd_Title);
	}

	/**
	 * @return the URI
	 */
	public URIish getUri() {
		return uri;
	}

	@Override
	public boolean performFinish() {
		uri = page.getSelection().getURI();
		if (page.getStoreInSecureStore()) {
			if (!SecureStoreUtils.storeCredentials(page.getCredentials(), uri))
				return false;
		}

		return uri != null;
	}

	/**
	 * @return credentials
	 */
	public UserPasswordCredentials getCredentials() {
		return page.getCredentials();
	}
}
