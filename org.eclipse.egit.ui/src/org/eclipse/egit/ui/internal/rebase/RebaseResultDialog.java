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
package org.eclipse.egit.ui.internal.rebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.egit.core.op.RebaseOperation;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.merge.GitMergeEditorInput;
import org.eclipse.egit.ui.internal.merge.MergeModeDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.api.RebaseCommand.Operation;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.RebaseResult.Status;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Display the result of a rebase.
 */
public class RebaseResultDialog extends MessageDialog {
	private static final Image INFO = PlatformUI.getWorkbench()
			.getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);

	private final RebaseResult result;

	private final Repository repo;

	private final Set<String> conflictPaths = new HashSet<String>();

	private Button toggleButton;

	private Button startMergeButton;

	private Button skipCommitButton;

	private Button abortRebaseButton;

	private Button doNothingButton;

	private Group nextStepsGroup;

	/**
	 * @param result
	 *            the result to show
	 * @param repository
	 */
	public static void show(final RebaseResult result,
			final Repository repository) {
		boolean shouldShow = result.getStatus() == Status.STOPPED
				|| Activator.getDefault().getPreferenceStore().getBoolean(
						UIPreferences.SHOW_REBASE_CONFIRM);
		if (!shouldShow) {
			Activator.getDefault().getLog().log(
					new org.eclipse.core.runtime.Status(IStatus.INFO, Activator
							.getPluginId(), NLS.bind(
							UIText.RebaseResultDialog_StatusLabel, result
									.getStatus().name())));
			return;
		}
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
				new RebaseResultDialog(shell, repository, result).open();
			}
		});
	}

	private static String getTitle(Status status) {
		switch (status) {
		case OK:
			return UIText.RebaseResultDialog_SuccessfullyFinished;
		case ABORTED:
			return UIText.RebaseResultDialog_Aborted;
		case STOPPED:
			return UIText.RebaseResultDialog_Stopped;
		case FAILED:
			return UIText.RebaseResultDialog_Failed;
		case UP_TO_DATE:
			return UIText.RebaseResultDialog_UpToDate;
		case FAST_FORWARD:
			return UIText.RebaseResultDialog_FastForward;
		default:
			throw new IllegalStateException(status.name());
		}
	}

	/**
	 * @param shell
	 * @param repository
	 * @param result
	 */
	private RebaseResultDialog(Shell shell, Repository repository,
			RebaseResult result) {
		super(shell, UIText.RebaseResultDialog_DialogTitle, INFO,
				getTitle(result.getStatus()),
				result.getStatus() == Status.FAILED ? MessageDialog.ERROR
						: MessageDialog.INFORMATION,
				new String[] { IDialogConstants.OK_LABEL }, 0);
		setShellStyle(getShellStyle() | SWT.SHELL_TRIM);
		this.repo = repository;
		this.result = result;
	}

	@Override
	protected Control createCustomArea(Composite parent) {

		if (result.getStatus() != Status.STOPPED) {
			createToggleButton(parent);
			return null;
		}
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().indent(0, 0).grab(true, true).applyTo(
				main);

		Group commitGroup = new Group(main, SWT.SHADOW_ETCHED_IN);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(commitGroup);
		commitGroup.setText(UIText.RebaseResultDialog_DetailsGroup);
		commitGroup.setLayout(new GridLayout(1, false));

		Label commitIdLabel = new Label(commitGroup, SWT.NONE);
		commitIdLabel.setText(UIText.RebaseResultDialog_CommitIdLabel);
		Text commitId = new Text(commitGroup, SWT.READ_ONLY | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(commitId);

		Label commitMessageLabel = new Label(commitGroup, SWT.NONE);
		commitMessageLabel
				.setText(UIText.RebaseResultDialog_CommitMessageLabel);
		TextViewer commitMessage = new TextViewer(commitGroup, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 60)
				.applyTo(commitMessage.getControl());

		boolean conflictListFailure = false;
		DirCache dc = null;
		RevWalk rw = null;
		try {
			rw = new RevWalk(repo);
			// the commits might not have been fully loaded
			RevCommit commit = rw.parseCommit(result.getCurrentCommit());
			commitMessage.getTextWidget().setText(commit.getFullMessage());
			commitId.setText(commit.name());
			dc = repo.lockDirCache();
			for (int i = 0; i < dc.getEntryCount(); i++) {
				if (dc.getEntry(i).getStage() > 0)
					conflictPaths.add(dc.getEntry(i).getPathString());
			}
			if (conflictPaths.size() > 0) {
				message = NLS.bind(UIText.RebaseResultDialog_Conflicting,
						Integer.valueOf(conflictPaths.size()));
				messageLabel.setText(message);
			}
		} catch (IOException e) {
			// the file list will be empty
			conflictListFailure = true;
		} finally {
			if (rw != null)
				rw.release();
			if (dc != null)
				dc.unlock();
		}

		if (conflictListFailure) {
			Label failureLabel = new Label(main, SWT.NONE);
			failureLabel
					.setText(UIText.RebaseResultDialog_ConflictListFailureMessage);
		} else {
			Label conflictListLabel = new Label(main, SWT.NONE);
			conflictListLabel
					.setText(UIText.RebaseResultDialog_DiffDetailsLabel);
			TableViewer conflictList = new TableViewer(main, SWT.BORDER);
			GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(
					conflictList.getTable());
			conflictList.setContentProvider(ArrayContentProvider.getInstance());
			conflictList.setInput(conflictPaths);
		}

		Group actionGroup = new Group(main, SWT.SHADOW_ETCHED_IN);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(actionGroup);
		actionGroup.setText(UIText.RebaseResultDialog_ActionGroupTitle);
		actionGroup.setLayout(new GridLayout(1, false));

		nextStepsGroup = new Group(main, SWT.SHADOW_ETCHED_IN);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(nextStepsGroup);
		nextStepsGroup.setText(UIText.RebaseResultDialog_NextSteps);
		nextStepsGroup.setLayout(new GridLayout(1, false));
		final TextViewer nextSteps = new TextViewer(nextStepsGroup, SWT.MULTI
				| SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 60)
				.applyTo(nextSteps.getControl());
		nextSteps.getTextWidget().setText(
				UIText.RebaseResultDialog_NextStepsAfterResolveConflicts);

		startMergeButton = new Button(actionGroup, SWT.RADIO);
		startMergeButton.setText(UIText.RebaseResultDialog_StartMergeRadioText);
		startMergeButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (startMergeButton.getSelection()) {
					nextSteps
							.getTextWidget()
							.setText(
									UIText.RebaseResultDialog_NextStepsAfterResolveConflicts);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing
			}

		});

		skipCommitButton = new Button(actionGroup, SWT.RADIO);
		skipCommitButton.setText(UIText.RebaseResultDialog_SkipCommitButton);
		skipCommitButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (skipCommitButton.getSelection())
					nextSteps.getTextWidget().setText(""); //$NON-NLS-1$
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing
			}

		});

		abortRebaseButton = new Button(actionGroup, SWT.RADIO);
		abortRebaseButton
				.setText(UIText.RebaseResultDialog_AbortRebaseRadioText);
		abortRebaseButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (abortRebaseButton.getSelection())
					nextSteps.getTextWidget().setText(""); //$NON-NLS-1$
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing
			}

		});

		doNothingButton = new Button(actionGroup, SWT.RADIO);
		doNothingButton.setText(UIText.RebaseResultDialog_DoNothingRadioText);
		doNothingButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (doNothingButton.getSelection())
					nextSteps.getTextWidget().setText(
							UIText.RebaseResultDialog_NextStepsDoNothing);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing
			}

		});

		startMergeButton.setSelection(true);

		commitGroup.pack();
		applyDialogFont(main);

		return main;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		// store the preference to hide these dialogs
		if (toggleButton != null)
			Activator.getDefault().getPreferenceStore().setValue(
					UIPreferences.SHOW_REBASE_CONFIRM,
					!toggleButton.getSelection());
		if (buttonId == IDialogConstants.OK_ID) {
			if (result.getStatus() != Status.STOPPED) {
				super.buttonPressed(buttonId);
				return;
			}
			if (startMergeButton.getSelection()) {
				super.buttonPressed(buttonId);
				// open the merge tool
				List<IProject> validProjects = new ArrayList<IProject>();
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
						.getProjects();
				for (IProject project : projects) {
					RepositoryMapping mapping = RepositoryMapping
							.getMapping(project);
					if (mapping != null && mapping.getRepository().equals(repo)) {
						validProjects.add(project);
						try {
							// make sure to refresh before opening the merge
							// tool
							project
									.refreshLocal(IResource.DEPTH_INFINITE,
											null);
						} catch (CoreException e) {
							Activator.handleError(e.getMessage(), e, false);
						}
					}
				}
				List<IResource> resourceList = new ArrayList<IResource>();
				IPath repoWorkdirPath = new Path(repo.getWorkTree().getPath());
				for (String repoPath : conflictPaths) {
					IPath filePath = repoWorkdirPath.append(repoPath);
					for (IProject project : validProjects) {
						if (project.getLocation().isPrefixOf(filePath)) {
							IResource res = project.getFile(filePath
									.removeFirstSegments(project.getLocation()
											.segmentCount()));
							resourceList.add(res);
						}
					}
				}
				IResource[] resources = new IResource[resourceList.size()];
				resourceList.toArray(resources);
				int mergeMode = Activator.getDefault().getPreferenceStore()
						.getInt(UIPreferences.MERGE_MODE);
				CompareEditorInput input;
				if (mergeMode == 0) {
					MergeModeDialog dlg = new MergeModeDialog(getParentShell());
					if (dlg.open() != Window.OK)
						return;
					input = new GitMergeEditorInput(dlg.useWorkspace(),
							resources);
				} else {
					boolean useWorkspace = mergeMode == 1;
					input = new GitMergeEditorInput(useWorkspace, resources);
				}
				CompareUI.openCompareEditor(input);
				return;
			} else if (skipCommitButton.getSelection()) {
				// skip the rebase
				try {
					final RebaseOperation op = new RebaseOperation(repo,
							Operation.SKIP);
					op.execute(null);

					show(op.getResult(), repo);
				} catch (CoreException e) {
					Activator.handleError(e.getMessage(), e, true);
				}
			} else if (abortRebaseButton.getSelection()) {
				// abort the rebase
				try {
					final RebaseOperation op = new RebaseOperation(repo,
							Operation.ABORT);
					op.execute(null);

					show(op.getResult(), repo);
				} catch (CoreException e) {
					Activator.handleError(e.getMessage(), e, true);
				}
			} else if (doNothingButton.getSelection()) {
				// nothing
			}
		}
		super.buttonPressed(buttonId);
	}

	private void createToggleButton(Composite parent) {
		boolean toggleState = !Activator.getDefault().getPreferenceStore()
				.getBoolean(UIPreferences.SHOW_REBASE_CONFIRM);
		toggleButton = new Button(parent, SWT.CHECK | SWT.LEFT);
		toggleButton.setText(UIText.RebaseResultDialog_ToggleShowButton);
		toggleButton.setSelection(toggleState);
	}
}