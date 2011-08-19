/*******************************************************************************
 * Copyright (C) 2010, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.push;

import org.eclipse.egit.ui.internal.components.RefSpecPage;
import org.eclipse.egit.ui.internal.components.RepositorySelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;

/**
 * Wizard to maintain RefSpecs
 */
public class RefSpecWizard extends Wizard {
	private final boolean pushMode;

	private final RemoteConfig config;

	private RefSpecPage page;

	/**
	 * @param repository
	 * @param config
	 * @param pushMode
	 */
	public RefSpecWizard(Repository repository, RemoteConfig config,
			boolean pushMode) {
		setNeedsProgressMonitor(true);
		this.pushMode = pushMode;
		this.config = config;
		page = new RefSpecPage(repository, pushMode);
	}

	@Override
	public void addPages() {
		addPage(page);
	}

	@Override
	public IWizardPage getStartingPage() {
		// only now do we set the selection (which will be progress-monitored by
		// the wizard)
		page.setSelection(new RepositorySelection(null, config));
		return super.getStartingPage();
	}

	@Override
	public boolean performFinish() {
		if (pushMode) {
			config.setPushRefSpecs(page.getRefSpecs());
		} else {
			config.setFetchRefSpecs(page.getRefSpecs());
			config.setTagOpt(page.getTagOpt());
		}
		return true;
	}
}
