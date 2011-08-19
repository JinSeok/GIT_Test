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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * Read-only view of remote configuration
 */
public class RepositoryRemotePropertySource implements IPropertySource {

	private final StoredConfig myConfig;

	private final String myName;

	/**
	 * @param config
	 * @param remoteName
	 * @param page
	 *
	 */
	public RepositoryRemotePropertySource(StoredConfig config,
			String remoteName, PropertySheetPage page) {
		myConfig = config;
		myName = remoteName;
		IToolBarManager mgr = page.getSite().getActionBars()
				.getToolBarManager();
		// we may have some contributions from the RepositoryPropertySource that
		// we should remove
		boolean update = false;
		update = update
				| mgr.remove(RepositoryPropertySource.CHANGEMODEACTIONID) != null;
		update = update
				| mgr.remove(RepositoryPropertySource.SINGLEVALUEACTIONID) != null;
		update = update
				| mgr.remove(RepositoryPropertySource.EDITACTIONID) != null;
		if (update)
			mgr.update(false);
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {

		try {
			myConfig.load();
		} catch (IOException e) {
			Activator.handleError(
					UIText.RepositoryRemotePropertySource_ErrorHeader, e, true);
		} catch (ConfigInvalidException e) {
			Activator.handleError(
					UIText.RepositoryRemotePropertySource_ErrorHeader, e, true);
		}
		List<IPropertyDescriptor> resultList = new ArrayList<IPropertyDescriptor>();
		PropertyDescriptor desc = new PropertyDescriptor(RepositoriesView.URL,
				UIText.RepositoryRemotePropertySource_RemoteFetchURL_label);
		resultList.add(desc);
		desc = new PropertyDescriptor(RepositoriesView.FETCH,
				UIText.RepositoryRemotePropertySource_FetchLabel);
		resultList.add(desc);
		desc = new PropertyDescriptor(RepositoriesView.PUSHURL,
				UIText.RepositoryRemotePropertySource_RemotePushUrl_label);
		resultList.add(desc);
		desc = new PropertyDescriptor(RepositoriesView.PUSH,
				UIText.RepositoryRemotePropertySource_PushLabel);
		resultList.add(desc);
		return resultList.toArray(new IPropertyDescriptor[resultList.size()]);
	}

	public Object getPropertyValue(Object id) {
		String[] list = myConfig.getStringList(RepositoriesView.REMOTE, myName,
				(String) id);
		if (list != null && list.length > 1) {
			// let's show this as "[some/uri][another/uri]"
			StringBuilder sb = new StringBuilder();
			for (String s : list) {
				sb.append('[');
				sb.append(s);
				sb.append(']');
			}
			return sb.toString();
		}
		return myConfig.getString(RepositoriesView.REMOTE, myName, (String) id);
	}

	public boolean isPropertySet(Object id) {
		// no default values
		return false;
	}

	public void resetPropertyValue(Object id) {
		// nothing to do
	}

	public void setPropertyValue(Object id, Object value) {
		// read-only
	}

}
