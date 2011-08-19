/*******************************************************************************
 * Copyright (C) 2011, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.history;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.UIText;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevFlag;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Allows to select a single commit
 */
public class CommitSelectionDialog extends TitleAreaDialog {
	private static final int BATCH_SIZE = 256;

	private final Repository repository;

	private CommitGraphTable table;

	private SWTCommitList allCommits;

	private RevFlag highlightFlag;

	private ObjectId commitId;

	/**
	 * @param parentShell
	 * @param repository
	 */
	public CommitSelectionDialog(Shell parentShell, Repository repository) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.SHELL_TRIM);
		this.repository = repository;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);
		table = new CommitGraphTable(main);
		table.getTableView().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						commitId = null;
						IStructuredSelection sel = (IStructuredSelection) event
								.getSelection();
						if (sel.size() == 1)
							commitId = ((SWTCommit) sel.getFirstElement())
									.getId();
						getButton(OK).setEnabled(commitId != null);
					}
				});
		table.getTableView().addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				buttonPressed(OK);
			}
		});
		// allow for some room here
		GridDataFactory.fillDefaults().grab(true, true).minSize(SWT.DEFAULT,
				400).applyTo(table.getControl());
		allCommits = new SWTCommitList(getShell().getDisplay());
		return main;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(UIText.CommitSelectionDialog_WindowTitle);
	}

	@Override
	public void create() {
		super.create();
		getButton(OK).setEnabled(false);
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, true,
					new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								monitor
										.beginTask(
												UIText.CommitSelectionDialog_BuildingCommitListMessage,
												IProgressMonitor.UNKNOWN);
								SWTWalk currentWalk = new SWTWalk(repository);
								currentWalk
										.sort(RevSort.COMMIT_TIME_DESC, true);
								currentWalk.sort(RevSort.BOUNDARY, true);
								highlightFlag = currentWalk
										.newFlag("highlight"); //$NON-NLS-1$
								allCommits.source(currentWalk);

								try {

									if (Activator
											.getDefault()
											.getPreferenceStore()
											.getBoolean(
													UIPreferences.RESOURCEHISTORY_SHOW_ALL_BRANCHES)) {
										markStartAllRefs(currentWalk,
												Constants.R_HEADS);
										markStartAllRefs(currentWalk,
												Constants.R_REMOTES);
									} else
										currentWalk
												.markStart(currentWalk
														.parseCommit(repository
																.resolve(Constants.HEAD)));
									for (;;) {
										final int oldsz = allCommits.size();
										allCommits.fillTo(oldsz + BATCH_SIZE
												- 1);

										if (monitor.isCanceled()
												|| oldsz == allCommits.size())
											break;
										String taskName = NLS
												.bind(
														UIText.CommitSelectionDialog_FoundCommitsMessage,
														Integer
																.valueOf(allCommits
																		.size()));
										monitor.setTaskName(taskName);

									}
								} catch (IOException e) {
									throw new InvocationTargetException(e);
								}
								getShell().getDisplay().asyncExec(
										new Runnable() {
											public void run() {
												updateUi();
											}
										});
								if (monitor.isCanceled())
									throw new InterruptedException();
							} finally {
								monitor.done();
							}
						}
					});
		} catch (InvocationTargetException e) {
			setErrorMessage(e.getCause().getMessage());
		} catch (InterruptedException e) {
			setMessage(UIText.CommitSelectionDialog_IncompleteListMessage,
					IMessageProvider.WARNING);
		}
	}

	/**
	 * @return the commit id
	 */
	public ObjectId getCommitId() {
		return commitId;
	}

	private void updateUi() {
		setTitle(NLS.bind(UIText.CommitSelectionDialog_DialogTitle, Integer
				.valueOf(allCommits.size()), repository.getDirectory()
				.toString()));
		setMessage(UIText.CommitSelectionDialog_DialogMessage);
		table.setInput(highlightFlag, allCommits, allCommits
				.toArray(new SWTCommit[allCommits.size()]), null);
	}

	private void markStartAllRefs(RevWalk currentWalk, String prefix)
			throws IOException, MissingObjectException,
			IncorrectObjectTypeException {
		for (Entry<String, Ref> refEntry : repository.getRefDatabase().getRefs(
				prefix).entrySet()) {
			Ref ref = refEntry.getValue();
			if (ref.isSymbolic())
				continue;
			currentWalk.markStart(currentWalk.parseCommit(ref.getObjectId()));
		}
	}

}
