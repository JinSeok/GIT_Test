/*******************************************************************************
 * Copyright (C) 2008, Marek Zawirski <marek.zawirski@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.components;

/**
 * General interface for receivers of selection-changed notifications from
 * various components.
 */
public interface SelectionChangeListener {
	/**
	 * Called when selection in calling component has changed.
	 */
	public void selectionChanged();
}
