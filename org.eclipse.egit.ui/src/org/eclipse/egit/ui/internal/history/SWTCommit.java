/*******************************************************************************
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.history;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.swt.widgets.Widget;

class SWTCommit extends PlotCommit<SWTCommitList.SWTLane> {
	Widget widget;

	SWTCommit(final AnyObjectId id) {
		super(id);
	}

	@Override
	public void reset() {
		widget = null;
		super.reset();
	}
}
