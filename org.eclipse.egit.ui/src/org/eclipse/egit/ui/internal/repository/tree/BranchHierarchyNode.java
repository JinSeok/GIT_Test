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
package org.eclipse.egit.ui.internal.repository.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

/**
 * Represents the "Branch Hierarchy" node
 */
public class BranchHierarchyNode extends RepositoryTreeNode<IPath> {

	/**
	 * Constructs the node.
	 *
	 * @param parent
	 *            the parent node (may be null)
	 * @param repository
	 *            the {@link Repository}
	 * @param path
	 *            the path
	 */
	public BranchHierarchyNode(RepositoryTreeNode parent,
			Repository repository, IPath path) {
		// path must end with /
		super(parent, RepositoryTreeNodeType.BRANCHHIERARCHY, repository, path.addTrailingSeparator());
	}

	/**
	 * @return the child paths
	 * @throws IOException
	 */
	public List<IPath> getChildPaths() throws IOException {
		List<IPath> result = new ArrayList<IPath>();
		for (IPath myPath : getPathList()) {
			if (getObject().isPrefixOf(myPath)) {
				int segmentDiff = myPath.segmentCount()
						- getObject().segmentCount();
				if (segmentDiff > 1) {
					IPath newPath = getObject().append(
							myPath.segment(getObject().segmentCount()));
					if (!result.contains(newPath))
						result.add(newPath);
				}
			}
		}
		return result;
	}

	/**
	 * @return the child Refs (branches)
	 * @throws IOException
	 */
	public List<Ref> getChildRefs() throws IOException {
		List<Ref> childRefs = new ArrayList<Ref>();
		for (IPath myPath : getPathList()) {
			if (getObject().isPrefixOf(myPath)) {
				int segmentDiff = myPath.segmentCount()
						- getObject().segmentCount();
				if (segmentDiff == 1) {
					Ref ref = getRepository().getRef(myPath.toPortableString());
					childRefs.add(ref);
				}
			}
		}
		return childRefs;
	}

	private List<IPath> getPathList() throws IOException {
		List<IPath> result = new ArrayList<IPath>();
		Map<String, Ref> refsMap = getRepository().getRefDatabase().getRefs(
				getObject().toPortableString()); // getObject() returns path ending with /
		for (Map.Entry<String, Ref> entry : refsMap.entrySet()) {
			if (entry.getValue().isSymbolic())
				continue;
			result.add(getObject().append(new Path(entry.getKey())));
		}
		return result;
	}
}
