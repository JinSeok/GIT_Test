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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Creates a branch based on another branch or on a commit.
 */
public class CreateBranchWizard extends Wizard {
	private String newBranchName;

	private CreateBranchPage myPage;

	/**
	 * @param repository
	 *            the repository
	 */
	public CreateBranchWizard(Repository repository) {
		this(repository, null);
	}

	/**
	 * @param repository
	 *            the repository
	 * @param base
	 *            a {@link Ref} name or {@link RevCommit} id, or null
	 */
	public CreateBranchWizard(Repository repository, String base) {
		try {
			if (base == null) {
				myPage = new CreateBranchPage(repository, (Ref) null);
			} else if (ObjectId.isId(base)) {
				RevCommit commit = new RevWalk(repository).parseCommit(ObjectId
						.fromString(base));
				myPage = new CreateBranchPage(repository, commit);
			} else {
				if (base.startsWith(Constants.R_HEADS)
						|| base.startsWith(Constants.R_REMOTES)
						|| base.startsWith(Constants.R_TAGS)) {
					Ref currentBranch = repository.getRef(base);
					myPage = new CreateBranchPage(repository, currentBranch);
				} else {
					// the page only knows some special Refs
					RevCommit commit = new RevWalk(repository)
							.parseCommit(repository.resolve(base + "^{commit}")); //$NON-NLS-1$
					myPage = new CreateBranchPage(repository, commit);
				}
			}
		} catch (IOException e) {
			// simply don't select the drop down
			myPage = new CreateBranchPage(repository, (Ref) null);
		}
		setWindowTitle(UIText.CreateBranchWizard_NewBranchTitle);
	}

	@Override
	public void addPages() {
		addPage(myPage);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					CreateBranchPage cp = (CreateBranchPage) getPages()[0];
					try {
						newBranchName = cp.getBranchName();
						cp.createBranch(monitor);
					} catch (CoreException ce) {
						throw new InvocationTargetException(ce);
					} catch (IOException ioe) {
						throw new InvocationTargetException(ioe);
					}
				}
			});
		} catch (InvocationTargetException ite) {
			Activator.handleError(UIText.CreateBranchWizard_CreationFailed, ite
					.getCause(), true);
			return false;
		} catch (InterruptedException ie) {
			// ignore here
		}
		return true;
	}

	/**
	 * @return the name (without ref/heads/) of the new branch
	 */
	public String getNewBranchName() {
		return newBranchName;
	}
}
