/*******************************************************************************
 * Copyright (C) 2010, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.merge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.egit.core.AdaptableFileTreeIterator;
import org.eclipse.egit.core.internal.storage.GitFileRevision;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.FileRevisionTypedElement;
import org.eclipse.egit.ui.internal.dialogs.CompareTreeView;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeIterator;
import org.eclipse.jgit.treewalk.filter.OrTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * A Git-specific {@link CompareEditorInput}
 */
public class GitCompareEditorInput extends CompareEditorInput {
	private static final Image FOLDER_IMAGE = PlatformUI.getWorkbench()
			.getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

	private final String baseVersion;

	private final String compareVersion;

	private final IResource[] resources;

	private final List<String> filterPathStrings = new ArrayList<String>();

	private final Map<IPath, IDiffContainer> diffRoots = new HashMap<IPath, IDiffContainer>();

	private Repository repository;

	/**
	 * @param compareVersion
	 *            (shown on the left side in compare); currently only commit IDs
	 *            are supported
	 * @param baseVersion
	 *            (shown on the right side in compare); currently only commit
	 *            IDs are supported
	 * @param resources
	 *            as selected by the user
	 */
	public GitCompareEditorInput(String compareVersion, String baseVersion,
			IResource... resources) {
		super(new CompareConfiguration());
		this.resources = convertResourceInput(resources);
		this.baseVersion = baseVersion;
		this.compareVersion = compareVersion;
	}

	/**
	 * @param compareVersion
	 *            (shown on the left side in compare); currently only commit IDs
	 *            are supported
	 * @param baseVersion
	 *            (shown on the right side in compare); currently only commit
	 *            IDs are supported
	 * @param repository
	 *            as selected by the user
	 */
	public GitCompareEditorInput(String compareVersion, String baseVersion,
			Repository repository) {
		super(new CompareConfiguration());
		this.resources = new IResource[0];
		this.baseVersion = baseVersion;
		this.compareVersion = compareVersion;
		this.repository = repository;
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
		// make sure all resources belong to the same repository
		RevWalk rw = null;
		try {
			monitor.beginTask(
					UIText.GitCompareEditorInput_CompareResourcesTaskName,
					IProgressMonitor.UNKNOWN);

			for (IResource resource : resources) {
				RepositoryMapping map = RepositoryMapping.getMapping(resource
						.getProject());
				if (repository != null && repository != map.getRepository())
					throw new InvocationTargetException(
							new IllegalStateException(
									UIText.GitCompareEditorInput_ResourcesInDifferentReposMessagge));
				String repoRelativePath = map.getRepoRelativePath(resource);
				filterPathStrings.add(repoRelativePath);
				DiffNode node = new DiffNode(Differencer.NO_CHANGE) {
					@Override
					public Image getImage() {
						return FOLDER_IMAGE;
					}
				};
				diffRoots
						.put(new Path(map.getRepoRelativePath(resource)), node);
				repository = map.getRepository();
			}

			if (repository == null)
				throw new InvocationTargetException(
						new IllegalStateException(
								UIText.GitCompareEditorInput_ResourcesInDifferentReposMessagge));

			if (monitor.isCanceled())
				throw new InterruptedException();

			rw = new RevWalk(repository);

			final RevCommit baseCommit;
			try {
				baseCommit = rw.parseCommit(repository.resolve(baseVersion));
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			}

			final RevCommit compareCommit;
			if (compareVersion == null)
				compareCommit = null;
			else
				try {
					compareCommit = rw.parseCommit(repository
							.resolve(compareVersion));
				} catch (IOException e) {
					throw new InvocationTargetException(e);
				}

			if (monitor.isCanceled())
				throw new InterruptedException();

			// set the labels
			CompareConfiguration config = getCompareConfiguration();
			config.setLeftLabel(compareVersion);
			config.setRightLabel(baseVersion);
			// set title and icon
			if (resources.length == 0) {
				Object[] titleParameters = new Object[] {
						Activator.getDefault().getRepositoryUtil()
								.getRepositoryName(repository), compareVersion,
						baseVersion };
				setTitle(NLS.bind(UIText.GitCompareEditorInput_EditorTitle,
						titleParameters));
			} else if (resources.length == 1) {
				Object[] titleParameters = new Object[] {
						resources[0].getFullPath().makeRelative().toString(),
						compareVersion, baseVersion };
				setTitle(NLS.bind(
						UIText.GitCompareEditorInput_EditorTitleSingleResource,
						titleParameters));
			} else
				setTitle(NLS
						.bind(
								UIText.GitCompareEditorInput_EditorTitleMultipleResources,
								compareVersion, baseVersion));

			// build the nodes
			try {
				return buildDiffContainer(baseCommit, compareCommit, monitor);
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			}
		} finally {
			if (rw != null)
				rw.dispose();
			monitor.done();
		}
	}

	@Override
	protected void contentsCreated() {
		super.contentsCreated();
		// select the first conflict
		getNavigator().selectChange(true);
	}

	@Override
	protected void handleDispose() {
		super.handleDispose();
		// we do NOT dispose the images, as these are shared
	}

	private IDiffContainer buildDiffContainer(RevCommit baseCommit,
			RevCommit compareCommit, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		boolean useIndex = compareVersion.equals(CompareTreeView.INDEX_VERSION);
		boolean checkIgnored = false;

		IDiffContainer result = new DiffNode(Differencer.CONFLICTING);

		TreeWalk tw = new TreeWalk(repository);

		// filter by selected resources
		if (filterPathStrings.size() > 1) {
			List<TreeFilter> suffixFilters = new ArrayList<TreeFilter>();
			for (String filterPath : filterPathStrings)
				suffixFilters.add(PathFilter.create(filterPath));
			TreeFilter otf = OrTreeFilter.create(suffixFilters);
			tw.setFilter(otf);
		} else if (filterPathStrings.size() > 0)
			tw.setFilter(PathFilter.create(filterPathStrings.get(0)));

		tw.setRecursive(true);

		int baseTreeIndex;
		if (baseCommit == null) {
			// compare workspace with something
			checkIgnored = true;
			baseTreeIndex = tw.addTree(new AdaptableFileTreeIterator(
					repository, ResourcesPlugin.getWorkspace().getRoot()));
		} else
			baseTreeIndex = tw.addTree(new CanonicalTreeParser(null, repository
					.newObjectReader(), baseCommit.getTree()));
		int compareTreeIndex;
		if (!useIndex)
			compareTreeIndex = tw.addTree(new CanonicalTreeParser(null,
					repository.newObjectReader(), compareCommit.getTree()));
		else
			// compare something with the index
			compareTreeIndex = tw.addTree(new DirCacheIterator(repository
					.readDirCache()));

		try {
			while (tw.next()) {
				if (monitor.isCanceled())
					throw new InterruptedException();
				AbstractTreeIterator compareVersionIterator = tw.getTree(
						compareTreeIndex, AbstractTreeIterator.class);
				AbstractTreeIterator baseVersionIterator = tw.getTree(
						baseTreeIndex, AbstractTreeIterator.class);
				if (checkIgnored
						&& baseVersionIterator != null
						&& ((WorkingTreeIterator) baseVersionIterator)
								.isEntryIgnored())
					continue;

				if (compareVersionIterator != null
						&& baseVersionIterator != null) {
					// content exists on both sides
					boolean equalContent = compareVersionIterator
							.getEntryObjectId().equals(
									baseVersionIterator.getEntryObjectId());
					if (equalContent)
						continue;
					monitor.setTaskName(baseVersionIterator
							.getEntryPathString());
					GitFileRevision baseRev = GitFileRevision.inCommit(
							repository, baseCommit, baseVersionIterator
									.getEntryPathString(), tw
									.getObjectId(baseTreeIndex));
					GitFileRevision compareRev;
					if (!useIndex)
						compareRev = GitFileRevision.inCommit(repository,
								compareCommit, compareVersionIterator
										.getEntryPathString(), tw
										.getObjectId(compareTreeIndex));
					else
						compareRev = GitFileRevision.inIndex(repository,
								compareVersionIterator.getEntryPathString());

					add(result, baseVersionIterator.getEntryPathString(),
							new DiffNode(new FileRevisionTypedElement(baseRev),
									new FileRevisionTypedElement(compareRev)));

				} else if (baseVersionIterator != null
						&& compareVersionIterator == null) {
					monitor.setTaskName(baseVersionIterator
							.getEntryPathString());
					// only on base side
					GitFileRevision baseRev = GitFileRevision.inCommit(
							repository, baseCommit, baseVersionIterator
									.getEntryPathString(), tw
									.getObjectId(baseTreeIndex));
					add(result, baseVersionIterator.getEntryPathString(),
							new DiffNode(Differencer.DELETION, null, null,
									new FileRevisionTypedElement(baseRev)));
				} else if (compareVersionIterator != null
						&& baseVersionIterator == null) {
					monitor.setTaskName(compareVersionIterator
							.getEntryPathString());
					// only on compare side
					GitFileRevision compareRev = GitFileRevision.inCommit(
							repository, compareCommit, compareVersionIterator
									.getEntryPathString(), tw
									.getObjectId(compareTreeIndex));
					add(result, compareVersionIterator.getEntryPathString(),
							new DiffNode(Differencer.ADDITION, null, null,
									new FileRevisionTypedElement(compareRev)));
				}

				if (monitor.isCanceled())
					throw new InterruptedException();
			}
			return result;
		} finally {
			tw.release();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((baseVersion == null) ? 0 : baseVersion.hashCode());
		result = prime * result
				+ ((compareVersion == null) ? 0 : compareVersion.hashCode());
		result = prime
				* result
				+ ((repository == null) ? 0 : repository.getDirectory()
						.hashCode());
		result = prime * result + Arrays.hashCode(resources);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GitCompareEditorInput other = (GitCompareEditorInput) obj;
		if (baseVersion == null) {
			if (other.baseVersion != null)
				return false;
		} else if (!baseVersion.equals(other.baseVersion))
			return false;
		if (compareVersion == null) {
			if (other.compareVersion != null)
				return false;
		} else if (!compareVersion.equals(other.compareVersion))
			return false;
		if (repository == null) {
			if (other.repository != null)
				return false;
		} else if (!repository.getDirectory().equals(
				other.repository.getDirectory()))
			return false;
		if (!Arrays.equals(resources, other.resources))
			return false;
		return true;
	}

	private void add(IDiffContainer result, String filePath, DiffNode diffNode) {
		IDiffContainer container = getFileParent(result, filePath);
		container.add(diffNode);
		diffNode.setParent(container);

	}

	private IDiffContainer getFileParent(IDiffContainer root, String filePath) {
		IPath path = new Path(filePath);
		IDiffContainer child = root;
		if (diffRoots.isEmpty()) {
			for (int i = 0; i < path.segmentCount() - 1; i++)
				child = getOrCreateChild(child, path.segment(i));
			return child;
		} else {
			for (Entry<IPath, IDiffContainer> entry : diffRoots.entrySet()) {
				if (entry.getKey().isPrefixOf(path)) {
					for (int i = entry.getKey().segmentCount(); i < path
							.segmentCount() - 1; i++)
						child = getOrCreateChild(child, path.segment(i));
					return child;
				}
			}
			return null;
		}
	}

	private DiffNode getOrCreateChild(IDiffContainer parent, final String name) {
		for (IDiffElement child : parent.getChildren()) {
			if (child.getName().equals(name)) {
				return ((DiffNode) child);
			}
		}
		DiffNode child = new DiffNode(parent, Differencer.NO_CHANGE) {

			@Override
			public String getName() {
				return name;
			}

			@Override
			public Image getImage() {
				return FOLDER_IMAGE;
			}
		};
		return child;
	}

	private IResource[] convertResourceInput(final IResource[] input) {
		if (input.length > 0) {
			// we must make sure to only show the topmost resources as roots
			List<IResource> resourceList = new ArrayList<IResource>(
					input.length);
			List<IPath> allPaths = new ArrayList<IPath>(input.length);
			for (IResource originalInput : input) {
				allPaths.add(originalInput.getFullPath());
			}
			for (IResource originalInput : input) {
				boolean skip = false;
				for (IPath path : allPaths) {
					if (path.isPrefixOf(originalInput.getFullPath())
							&& path.segmentCount() < originalInput
									.getFullPath().segmentCount()) {
						skip = true;
						break;
					}
				}
				if (!skip)
					resourceList.add(originalInput);
			}
			return resourceList.toArray(new IResource[resourceList.size()]);
		} else
			return input;
	}
}
