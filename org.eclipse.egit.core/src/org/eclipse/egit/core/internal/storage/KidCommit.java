/*******************************************************************************
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core.internal.storage;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

class KidCommit extends RevCommit {
	static final KidCommit[] NO_CHILDREN = {};

	KidCommit[] children = NO_CHILDREN;

	KidCommit(final AnyObjectId id) {
		super(id);
	}

	void addChild(final KidCommit c) {
		final int cnt = children.length;
		if (cnt == 0)
			children = new KidCommit[] { c };
		else if (cnt == 1)
			children = new KidCommit[] { children[0], c };
		else {
			final KidCommit[] n = new KidCommit[cnt + 1];
			System.arraycopy(children, 0, n, 0, cnt);
			n[cnt] = c;
			children = n;
		}
	}

	@Override
	public void reset() {
		children = NO_CHILDREN;
		super.reset();
	}
}
