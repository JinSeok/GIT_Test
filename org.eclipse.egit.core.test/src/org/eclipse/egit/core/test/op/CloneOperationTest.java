/*******************************************************************************
 * Copyright (C) 2011, Stefan Lay <stefan.lay@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.egit.core.test.op;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.core.op.CloneOperation.PostCloneTask;
import org.eclipse.egit.core.op.ConfigurePushAfterCloneTask;
import org.eclipse.egit.core.test.DualRepositoryTestCase;
import org.eclipse.egit.core.test.TestRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class CloneOperationTest extends DualRepositoryTestCase {

	File workdir;

	File workdir2;

	@Before
	public void setUp() throws Exception {
		workdir = testUtils.createTempDir("Repository1");
		workdir2 = testUtils.createTempDir("Repository2");

		repository1 = new TestRepository(new File(workdir, Constants.DOT_GIT));

		File file = new File(workdir, "file1.txt");
		FileUtils.createNewFile(file);
		Git git = new Git(repository1.getRepository());
		git.add().addFilepattern("file1.txt").call();

		git.commit().setMessage("first commit").call();
	}

	@Test
	public void testClone() throws Exception {
		URIish uri = new URIish("file:///"
				+ repository1.getRepository().getDirectory().toString());
		CloneOperation clop = new CloneOperation(uri, true, null, workdir2,
				"refs/heads/master", "origin", 0);
		clop.run(null);

		Repository clonedRepo = new FileRepository(new File(workdir2,
				Constants.DOT_GIT));
		assertEquals(
				"",
				uri.toString(),
				clonedRepo.getConfig().getString(
						ConfigConstants.CONFIG_REMOTE_SECTION, "origin", "url"));
		assertEquals(
				"",
				"+refs/heads/*:refs/remotes/origin/*",
				clonedRepo.getConfig().getString(
						ConfigConstants.CONFIG_REMOTE_SECTION, "origin",
						"fetch"));

		File file = new File(workdir2, "file1.txt");
		assertTrue(file.exists());
	}

	@Test
	public void testSimplePostCloneTask() throws Exception {
		URIish uri = new URIish("file:///"
				+ repository1.getRepository().getDirectory().toString());
		CloneOperation clop = new CloneOperation(uri, true, null, workdir2,
				"refs/heads/master", "origin", 0);

		final File[] repoDir = new File[1];
		clop.addPostCloneTask(new PostCloneTask() {

			public void execute(Repository repository, IProgressMonitor monitor)
					throws CoreException {
				repoDir[0] = repository.getDirectory();

			}
		});
		clop.run(null);
		File newRepoDir = new File(workdir2, Constants.DOT_GIT);
		assertEquals(newRepoDir, repoDir[0]);
	}

	@Test
	public void testConfigurePushAfterCloneTask() throws Exception {
		URIish uri = new URIish("file:///"
				+ repository1.getRepository().getDirectory().toString());
		CloneOperation clop = new CloneOperation(uri, true, null, workdir2,
				"refs/heads/master", "origin", 0);

		clop.addPostCloneTask(new ConfigurePushAfterCloneTask("origin",
				"HEAD:refs/for/master", new URIish("file:///pushtarget")));
		clop.run(null);
		Repository clonedRepo = new FileRepository(new File(workdir2,
				Constants.DOT_GIT));
		assertEquals(
				"",
				"HEAD:refs/for/master",
				clonedRepo.getConfig()
				.getString(ConfigConstants.CONFIG_REMOTE_SECTION,
						"origin", "push"));
		assertEquals(
				"",
				"file:///pushtarget",
				clonedRepo.getConfig().getString(
						ConfigConstants.CONFIG_REMOTE_SECTION, "origin",
						"pushurl"));
	}

}
