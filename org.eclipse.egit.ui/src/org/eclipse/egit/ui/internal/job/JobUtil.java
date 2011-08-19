/*******************************************************************************
 * Copyright (C) 2010, Jens Baumgart <jens.baumgart@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.job;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.op.IEGitOperation;

/**
 * Utility class for scheduling jobs
 */
public class JobUtil {

	/**
	 * Schedule a user job that executes an EGit operation
	 *
	 * @param op
	 * @param jobName
	 * @param jobFamily
	 */
	public static void scheduleUserJob(final IEGitOperation op, String jobName,
			final Object jobFamily) {
		scheduleUserJob(op, jobName, jobFamily, null);
	}

	/**
	 * Schedule a user job that executes an EGit operation
	 *
	 * @param op
	 * @param jobName
	 * @param jobFamily
	 * @param jobChangeListener
	 */
	public static void scheduleUserJob(final IEGitOperation op, String jobName,
			final Object jobFamily, IJobChangeListener jobChangeListener) {
		Job job = new Job(jobName) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					op.execute(monitor);
				} catch (CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}

			@Override
			public boolean belongsTo(Object family) {
				if (jobFamily != null && family.equals(jobFamily))
					return true;
				return super.belongsTo(family);
			}
		};
		job.setRule(op.getSchedulingRule());
		job.setUser(true);
		if (jobChangeListener != null)
			job.addJobChangeListener(jobChangeListener);
		job.schedule();
	}
}
