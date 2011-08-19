/*******************************************************************************
 * Copyright (C) 2010, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core.synchronize;

import org.eclipse.egit.core.synchronize.dto.GitSynchronizeData;
import org.eclipse.egit.core.synchronize.dto.GitSynchronizeDataSet;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.variants.ResourceVariantByteStore;

/**
 * Implementation of abstract {@link GitResourceVariantTree} class. It is only
 * used in {@link GitResourceVariantTreeTest} for testing public methods that
 * are implemented in base class.
 */
class GitTestResourceVariantTree extends GitResourceVariantTree {

	GitTestResourceVariantTree(GitSynchronizeDataSet data,
			ResourceVariantByteStore store) {
		super(store, data);
	}

	@Override
	protected RevCommit getRevCommit(GitSynchronizeData gsd)
			throws TeamException {
		return null;
	}

}
