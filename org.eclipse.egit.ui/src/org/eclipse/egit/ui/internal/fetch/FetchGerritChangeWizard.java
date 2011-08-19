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
package org.eclipse.egit.ui.internal.fetch;

import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;

/**
 * Wizard for fetching a Gerrit change
 */
public class FetchGerritChangeWizard extends Wizard {
	private final Repository repository;

	FetchGerritChangePage page;

	private String refName;

	/**
	 * @param repository
	 *            the repository
	 */
	public FetchGerritChangeWizard(Repository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);
		setWindowTitle(UIText.FetchGerritChangeWizard_WizardTitle);
	}

	/**
	 * @param repository
	 * @param refName initial value for the ref field
	 */
	public FetchGerritChangeWizard(Repository repository, String refName) {
		this(repository);
		this.refName = refName;
	}

	@Override
	public void addPages() {
		page = new FetchGerritChangePage(repository, refName);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return page.doFetch();
	}
}
