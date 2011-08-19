/*******************************************************************************
 * Copyright (C) 2007, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Roger C. Soares <rogersoares@intelinet.com.br>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;
import org.eclipse.ui.IWorkbenchPage;

/**
 * The input provider for the compare editor when working on resources
 * under Git control.
 */
public class GitCompareFileRevisionEditorInput extends SaveableCompareEditorInput {

	private ITypedElement left;
	private ITypedElement right;

	/**
	 * Creates a new CompareFileRevisionEditorInput.
	 * @param left
	 * @param right
	 * @param page
	 */
	public GitCompareFileRevisionEditorInput(ITypedElement left, ITypedElement right, IWorkbenchPage page) {
		super(new CompareConfiguration(), page);
		this.left = left;
		this.right = right;
	}

	FileRevisionTypedElement getRightRevision() {
		if (right instanceof FileRevisionTypedElement) {
			return (FileRevisionTypedElement) right;
		}
		return null;
	}

	FileRevisionTypedElement getLeftRevision() {
		if (left instanceof FileRevisionTypedElement) {
			return (FileRevisionTypedElement) left;
		}
		return null;
	}

	private static void ensureContentsCached(FileRevisionTypedElement left, FileRevisionTypedElement right,
			IProgressMonitor monitor) {
		if (left != null) {
			try {
				left.cacheContents(monitor);
			} catch (CoreException e) {
				Activator.logError(e.getMessage(), e);
			}
		}
		if (right != null) {
			try {
				right.cacheContents(monitor);
			} catch (CoreException e) {
				Activator.logError(e.getMessage(), e);
			}
		}
	}

	private boolean isLeftEditable(ICompareInput input) {
		Object tmpLeft = input.getLeft();
		return isEditable(tmpLeft);
	}

	private boolean isRightEditable(ICompareInput input) {
		Object tmpRight = input.getRight();
		return isEditable(tmpRight);
	}

	private boolean isEditable(Object object) {
		if (object instanceof IEditableContent) {
			return ((IEditableContent) object).isEditable();
		}
		return false;
	}

	private IResource getResource(@SuppressWarnings("unused") ICompareInput input) {
		if (getLocalElement() != null) {
			return getLocalElement().getResource();
		}
		return null;
	}

	private ICompareInput createCompareInput() {
		return compare(left, right);
	}

	private DiffNode compare(ITypedElement actLeft, ITypedElement actRight) {
		if (actLeft.getType().equals(ITypedElement.FOLDER_TYPE)) {
			//			return new MyDiffContainer(null, left,right);
			DiffNode diffNode = new DiffNode(null,Differencer.CHANGE,null,actLeft,actRight);
			ITypedElement[] lc = (ITypedElement[])((IStructureComparator)actLeft).getChildren();
			ITypedElement[] rc = (ITypedElement[])((IStructureComparator)actRight).getChildren();
			int li=0;
			int ri=0;
			while (li<lc.length && ri<rc.length) {
				ITypedElement ln = lc[li];
				ITypedElement rn = rc[ri];
				int compareTo = ln.getName().compareTo(rn.getName());
				// TODO: Git ordering!
				if (compareTo == 0) {
					if (!ln.equals(rn))
						diffNode.add(compare(ln,rn));
					++li;
					++ri;
				} else if (compareTo < 0) {
					DiffNode childDiffNode = new DiffNode(Differencer.ADDITION, null, ln, null);
					diffNode.add(childDiffNode);
					if (ln.getType().equals(ITypedElement.FOLDER_TYPE)) {
						ITypedElement[] children = (ITypedElement[])((IStructureComparator)ln).getChildren();
						if(children != null && children.length > 0) {
							for (ITypedElement child : children) {
								childDiffNode.add(addDirectoryFiles(child, Differencer.ADDITION));
							}
						}
					}
					++li;
				} else {
					DiffNode childDiffNode = new DiffNode(Differencer.DELETION, null, null, rn);
					diffNode.add(childDiffNode);
					if (rn.getType().equals(ITypedElement.FOLDER_TYPE)) {
						ITypedElement[] children = (ITypedElement[])((IStructureComparator)rn).getChildren();
						if(children != null && children.length > 0) {
							for (ITypedElement child : children) {
								childDiffNode.add(addDirectoryFiles(child, Differencer.DELETION));
							}
						}
					}
					++ri;
				}
			}
			while (li<lc.length) {
				ITypedElement ln = lc[li];
				DiffNode childDiffNode = new DiffNode(Differencer.ADDITION, null, ln, null);
				diffNode.add(childDiffNode);
				if (ln.getType().equals(ITypedElement.FOLDER_TYPE)) {
					ITypedElement[] children = (ITypedElement[])((IStructureComparator)ln).getChildren();
					if(children != null && children.length > 0) {
						for (ITypedElement child : children) {
							childDiffNode.add(addDirectoryFiles(child, Differencer.ADDITION));
						}
					}
				}
				++li;
			}
			while (ri<rc.length) {
				ITypedElement rn = rc[ri];
				DiffNode childDiffNode = new DiffNode(Differencer.DELETION, null, null, rn);
				diffNode.add(childDiffNode);
				if (rn.getType().equals(ITypedElement.FOLDER_TYPE)) {
					ITypedElement[] children = (ITypedElement[])((IStructureComparator)rn).getChildren();
					if(children != null && children.length > 0) {
						for (ITypedElement child : children) {
							childDiffNode.add(addDirectoryFiles(child, Differencer.DELETION));
						}
					}
				}
				++ri;
			}
			return diffNode;
		} else {
			return new DiffNode(actLeft, actRight);
		}
	}

	private DiffNode addDirectoryFiles(ITypedElement elem, int diffType) {
		ITypedElement l = null;
		ITypedElement r = null;
		if (diffType == Differencer.DELETION) {
			r = elem;
		} else {
			l = elem;
		}

		if (elem.getType().equals(ITypedElement.FOLDER_TYPE)) {
			DiffNode diffNode = null;
			diffNode = new DiffNode(null,Differencer.CHANGE,null,l,r);
			ITypedElement[] children = (ITypedElement[])((IStructureComparator)elem).getChildren();
			for (ITypedElement child : children) {
				diffNode.add(addDirectoryFiles(child, diffType));
			}
			return diffNode;
		} else {
			return new DiffNode(diffType, null, l, r);
		}
	}

	private void initLabels(ICompareInput input) {
		CompareConfiguration cc = getCompareConfiguration();
		if (getLeftRevision() != null) {
			String leftLabel = getFileRevisionLabel(getLeftRevision());
			cc.setLeftLabel(leftLabel);
		} else if (getResource(input) != null) {
			String label = NLS.bind(UIText.GitCompareFileRevisionEditorInput_LocalLabel, new Object[]{ input.getLeft().getName() });
			cc.setLeftLabel(label);
		} else {
			cc.setLeftLabel(left.getName());
		}
		if (getRightRevision() != null) {
			String rightLabel = getFileRevisionLabel(getRightRevision());
			cc.setRightLabel(rightLabel);
		} else {
			cc.setRightLabel(right.getName());
		}

	}

	private String getFileRevisionLabel(FileRevisionTypedElement element) {
		Object fileObject = element.getFileRevision();
		if (fileObject instanceof LocalFileRevision){
			return NLS.bind(UIText.GitCompareFileRevisionEditorInput_LocalHistoryLabel, new Object[]{element.getName(), element.getTimestamp()});
		} else {
			return NLS.bind(UIText.GitCompareFileRevisionEditorInput_RevisionLabel, new Object[]{element.getName(),
					CompareUtils.truncatedRevision(element.getContentIdentifier()), element.getAuthor()});
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.CompareEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		Object[] titleObject = new Object[3];
		titleObject[0] = getLongName(left);
		titleObject[1] = CompareUtils.truncatedRevision(getContentIdentifier(getLeftRevision()));
		titleObject[2] = CompareUtils.truncatedRevision(getContentIdentifier(getRightRevision()));
		return NLS.bind(UIText.GitCompareFileRevisionEditorInput_CompareTooltip, titleObject);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.CompareEditorInput#getTitle()
	 */
	public String getTitle() {
		Object[] titleObject = new Object[3];
		titleObject[0] = getShortName(left);
		titleObject[1] = CompareUtils.truncatedRevision(getContentIdentifier(getLeftRevision()));
		titleObject[2] = CompareUtils.truncatedRevision(getContentIdentifier(getRightRevision()));
		return NLS.bind(UIText.GitCompareFileRevisionEditorInput_CompareTooltip, titleObject);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.CompareEditorInput#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == IFile.class || adapter == IResource.class) {
			if (getLocalElement() != null) {
				return getLocalElement().getResource();
			}
			return null;
		}
		return super.getAdapter(adapter);
	}

	private String getShortName(ITypedElement element) {
		if (element instanceof FileRevisionTypedElement){
			FileRevisionTypedElement fileRevisionElement = (FileRevisionTypedElement) element;
			return fileRevisionElement.getName();
		}
		else if (element instanceof LocalResourceTypedElement){
			LocalResourceTypedElement typedContent = (LocalResourceTypedElement) element;
			return typedContent.getResource().getName();
		}
		return element.getName();
	}

	private String getLongName(ITypedElement element) {
		if (element instanceof FileRevisionTypedElement){
			FileRevisionTypedElement fileRevisionElement = (FileRevisionTypedElement) element;
			return fileRevisionElement.getPath();
		}
		else if (element instanceof LocalResourceTypedElement){
			LocalResourceTypedElement typedContent = (LocalResourceTypedElement) element;
			return typedContent.getResource().getFullPath().toString();
		}
		return element.getName();
	}

	private String getContentIdentifier(ITypedElement element){
		if (element instanceof FileRevisionTypedElement){
			FileRevisionTypedElement fileRevisionElement = (FileRevisionTypedElement) element;
			Object fileObject = fileRevisionElement.getFileRevision();
			if (fileObject instanceof LocalFileRevision){
				try {
					IStorage storage = ((LocalFileRevision) fileObject).getStorage(new NullProgressMonitor());
					if (CompareUtils.getAdapter(storage, IFileState.class) != null){
						//local revision
						return UIText.GitCompareFileRevisionEditorInput_LocalRevision;
					} else if (CompareUtils.getAdapter(storage, IFile.class) != null) {
						//current revision
						return UIText.GitCompareFileRevisionEditorInput_CurrentRevision;
					}
				} catch (CoreException e) {
					Activator
							.logError(
									UIText.GitCompareFileRevisionEditorInput_contentIdentifier,
									e);
				}
			} else {
				return fileRevisionElement.getContentIdentifier();
			}
		}
		return UIText.GitCompareFileRevisionEditorInput_CurrentTitle;
	}

	@Override
	protected void fireInputChange() {
		// nothing
	}

	private LocalResourceTypedElement getLocalElement() {
		if (left instanceof LocalResourceTypedElement) {
			return (LocalResourceTypedElement) left;
		}
		return null;
	}

	@Override
	protected ICompareInput prepareCompareInput(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
		ICompareInput input = createCompareInput();
		getCompareConfiguration().setLeftEditable(isLeftEditable(input));
		getCompareConfiguration().setRightEditable(isRightEditable(input));
		ensureContentsCached(getLeftRevision(), getRightRevision(), monitor);
		initLabels(input);
		setTitle(NLS.bind(UIText.GitCompareFileRevisionEditorInput_CompareInputTitle, new String[] { input.getName() }));

		// The compare editor (Structure Compare) will show the diff filenames
		// with their project relative path. So, no need to also show directory entries.
		DiffNode flatDiffNode = new DiffNode(null,Differencer.CHANGE,null,left,right);
		flatDiffView(flatDiffNode, (DiffNode) input);

		return flatDiffNode;
	}

	private void flatDiffView(DiffNode rootNode, DiffNode currentNode) {
		if(currentNode != null) {
			IDiffElement[] dElems = currentNode.getChildren();
			if(dElems != null) {
				for(IDiffElement dElem : dElems) {
					DiffNode dNode = (DiffNode) dElem;
					if(dNode.getChildren() != null && dNode.getChildren().length > 0) {
						flatDiffView(rootNode, dNode);
					} else {
						rootNode.add(dNode);
					}
				}
			}
		}
	}
	/**
	 * ITypedElement without content. May be used to indicate that a file is not
	 * available.
	 */
	public static class EmptyTypedElement implements ITypedElement{

		private String name;

		/**
		 * @param name the name used for display
		 */
		public EmptyTypedElement(String name) {
			this.name = name;
		}

		public Image getImage() {
			return null;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return ITypedElement.UNKNOWN_TYPE;
		}

	}


}
