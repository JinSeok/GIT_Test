/*******************************************************************************
 * Copyright (C) 2011, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.synchronize.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.ui.Activator;
import org.eclipse.jgit.lib.Repository;
import org.junit.BeforeClass;
import org.junit.Test;

public class GitModelRepositoryTest extends GitModelTestCase {

	@Test public void shouldReturnNotEqual() throws Exception {
		// given
		Repository leftRepo = lookupRepository(leftRepoFile);
		Repository rightRepo = lookupRepository(rightRepoFile);
		GitModelRepository left = new GitModelRepository(getGSD(leftRepo));
		GitModelRepository right = new GitModelRepository(getGSD(rightRepo));

		// when
		boolean actual = left.equals(right);

		// then
		assertFalse(actual);
	}

	@Test public void shouldReturnEqual() throws Exception {
		// given
		Repository leftRepo = lookupRepository(leftRepoFile);
		Repository rightRepo = lookupRepository(leftRepoFile);
		GitModelRepository left = new GitModelRepository(getGSD(leftRepo));
		GitModelRepository right = new GitModelRepository(getGSD(rightRepo));

		// when
		boolean actual = left.equals(right);

		// then
		assertTrue(actual);
	}

	@Test public void shouldReturnEqualForSameInstance() throws Exception {
		// given
		Repository leftRepo = lookupRepository(leftRepoFile);
		GitModelRepository left = new GitModelRepository(getGSD(leftRepo));

		// when
		boolean actual = left.equals(left);

		// then
		assertTrue(actual);
	}

	@BeforeClass public static void setupEnvironment() throws Exception {
		leftRepoFile = createProjectAndCommitToRepository();
		rightRepoFile = createChildRepository(leftRepoFile);

		Activator.getDefault().getRepositoryUtil()
				.addConfiguredRepository(leftRepoFile);
	}

}
