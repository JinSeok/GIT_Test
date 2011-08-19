/*******************************************************************************
 * Copyright (C) 2010, Chris Aniszczyk <caniszczyk@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.core.test.op;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.op.TagOperation;
import org.eclipse.egit.core.test.DualRepositoryTestCase;
import org.eclipse.egit.core.test.TestRepository;
import org.eclipse.egit.core.test.TestUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.TagBuilder;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.RawParseUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TagOperationTest extends DualRepositoryTestCase {

	File workdir;

	String projectName = "TagTest";

	IProject project;

	@Before
	public void setUp() throws Exception {

		workdir = testUtils.createTempDir("Repository1");

		repository1 = new TestRepository(new File(workdir, Constants.DOT_GIT));

		project = testUtils.createProjectInLocalFileSystem(workdir,
				projectName);
		testUtils.addFileToProject(project, "folder1/file1.txt", "Hello world");

		repository1.connect(project);

		project.accept(new IResourceVisitor() {

			public boolean visit(IResource resource) throws CoreException {
				if (resource instanceof IFile) {
					try {
						repository1
								.track(EFS.getStore(resource.getLocationURI())
										.toLocalFile(0, null));
					} catch (IOException e) {
						throw new CoreException(Activator.error(e.getMessage(),
								e));
					}
				}
				return true;
			}
		});

		repository1.commit("Initial commit");
	}

	@After
	public void tearDown() throws Exception {
		project.close(null);
		project.delete(false, false, null);
		repository1.dispose();
		repository1 = null;
		testUtils.deleteTempDirs();
	}

	@Test
	public void addTag() throws Exception {
		assertTrue("Tags should be empty", repository1.getRepository()
				.getTags().isEmpty());
		TagBuilder newTag = new TagBuilder();
		newTag.setTag("TheNewTag");
		newTag.setMessage("Well, I'm the tag");
		newTag.setTagger(RawParseUtils.parsePersonIdent(TestUtils.AUTHOR));
		newTag.setObjectId(repository1.getRepository()
				.resolve("refs/heads/master"), Constants.OBJ_COMMIT);
		TagOperation top = new TagOperation(repository1.getRepository(),
				newTag, false);
		top.execute(new NullProgressMonitor());
		assertFalse("Tags should not be empty", repository1.getRepository()
				.getTags().isEmpty());

		try {
			top.execute(null);
			fail("Expected Exception not thrown");
		} catch (CoreException e) {
			// expected
		}

		top = new TagOperation(repository1.getRepository(), newTag, true);
		try {
			top.execute(null);
			fail("Expected Exception not thrown");
		} catch (CoreException e) {
			// expected
		}
		Ref tagRef = repository1.getRepository().getTags().get("TheNewTag");
		RevWalk walk = new RevWalk(repository1.getRepository());
		RevTag tag = walk.parseTag(
				repository1.getRepository().resolve(tagRef.getName()));

		newTag.setMessage("Another message");
		assertFalse("Messages should differ", tag.getFullMessage().equals(
				newTag.getMessage()));
		top.execute(null);
		tag = walk.parseTag(
				repository1.getRepository().resolve(tagRef.getName()));
		assertTrue("Messages be same", tag.getFullMessage().equals(
				newTag.getMessage()));
	}

}
