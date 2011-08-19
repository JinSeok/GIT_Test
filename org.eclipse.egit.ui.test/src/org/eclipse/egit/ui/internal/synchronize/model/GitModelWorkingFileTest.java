/*******************************************************************************
 * Copyright (C) 2010, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.synchronize.model;

import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.egit.ui.Activator;
import org.junit.BeforeClass;
import org.junit.Test;

public class GitModelWorkingFileTest extends GitModelTestCase {

	@Test public void shouldReturnEqualForSameInstance() throws Exception {
		// given
		GitModelWorkingFile left = createWorkingFile(getFile1Location());

		// when
		boolean actual = left.equals(left);

		// then
		assertTrue(actual);
	}

	@Test public void shouldReturnEqualForSameLocation() throws Exception {
		// given
		GitModelWorkingFile left = createWorkingFile(getFile1Location());
		GitModelWorkingFile right = createWorkingFile(getFile1Location());

		// when
		boolean actual = left.equals(right);

		// then
		assertTrue(actual);
	}

	@Test public void shouldReturnNotEqualForDifferentLocation()
			throws Exception {
		// given
		GitModelWorkingFile left = createWorkingFile(getFile1Location());
		GitModelWorkingFile right = createWorkingFile(getFile2Location());

		// when
		boolean actual = left.equals(right);

		// then
		assertFalse(actual);
	}

	@Test public void shouldReturnNotEqualWhenComparingWorkingFileAndBlob()
			throws Exception {
		// given
		GitModelWorkingFile left = createWorkingFile(getFile1Location());
		GitModelBlob right = new GitModelBlob(createModelCommit(), getCommit(
				leftRepoFile, HEAD), null, null, null, null, getFile1Location());

		// when
		boolean actual = left.equals(right);

		// then
		assertFalse(actual);
	}

	@Test public void shouldReturnNotEqualWhenComparingWorkingFileAndCacheFile()
			throws Exception {
		// given
		GitModelWorkingFile left = createWorkingFile(getFile1Location());
		GitModelCacheFile right = new GitModelCacheFile(createModelCommit(),
				getCommit(leftRepoFile, HEAD), null, null, getFile1Location());

		// when
		boolean actual = left.equals(right);

		// then
		assertFalse(actual);
	}

	@BeforeClass public static void setupEnvironment() throws Exception {
		leftRepoFile = createProjectAndCommitToRepository();

		Activator.getDefault().getRepositoryUtil()
				.addConfiguredRepository(leftRepoFile);
	}

	private GitModelWorkingFile createWorkingFile(IPath location)
			throws Exception {
		return new GitModelWorkingFile(createModelCommit(), getCommit(
				leftRepoFile, HEAD), null, location);
	}

}
