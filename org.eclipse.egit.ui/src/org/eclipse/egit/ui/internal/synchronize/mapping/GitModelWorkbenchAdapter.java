/*******************************************************************************
 * Copyright (C) 2010, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.synchronize.mapping;

import org.eclipse.egit.ui.internal.synchronize.model.GitModelObject;
import org.eclipse.egit.ui.internal.synchronize.model.GitModelRoot;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Adapts Git ChangeSet model into workbench
 */
public class GitModelWorkbenchAdapter implements IWorkbenchAdapter {

	private GitChangeSetLabelProvider gitLabelProvider;

	public Object[] getChildren(Object o) {
		if (o instanceof GitModelObject)
			return ((GitModelObject) o).getChildren();

		if (o instanceof GitModelRoot)
			return ((GitModelRoot) o).getChildren();

		return null;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		if (gitLabelProvider == null)
			gitLabelProvider = new GitChangeSetLabelProvider();

		Image image = gitLabelProvider.getImage(object);

		return ImageDescriptor.createFromImage(image);
	}

	public String getLabel(Object o) {
		if (o instanceof GitModelObject)
			return ((GitModelObject) o).getName();

		return null;
	}

	public Object getParent(Object o) {
		if (o instanceof GitModelObject)
			return ((GitModelObject) o).getParent();

		return null;
	}

}
