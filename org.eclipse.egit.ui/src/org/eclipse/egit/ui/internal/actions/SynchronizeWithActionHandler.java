/*******************************************************************************
 * Copyright (C) 2010, Dariusz Luksza <dariusz@luksza.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.actions;

import static org.eclipse.jgit.lib.Constants.R_HEADS;
import static org.eclipse.jgit.lib.Constants.R_REFS;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.core.synchronize.dto.GitSynchronizeData;
import org.eclipse.egit.core.synchronize.dto.GitSynchronizeDataSet;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.synchronize.GitModelSynchronize;
import org.eclipse.egit.ui.internal.synchronize.SelectSynchronizeResourceDialog;
import org.eclipse.egit.ui.internal.synchronize.SyncRepoEntity;
import org.eclipse.egit.ui.internal.synchronize.SyncRepoEntity.SyncRefEntity;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;

/**
 * An action that launch synchronization with selected repository
 */
public class SynchronizeWithActionHandler extends RepositoryActionHandler {

	private static final Pattern PATTERN = Pattern
			.compile("^("  //$NON-NLS-1$
					+ R_HEADS + ")|("  //$NON-NLS-1$
					+ R_REMOTES + ")|("  //$NON-NLS-1$
					+ R_TAGS + ")|("  //$NON-NLS-1$
					+ R_REFS + ")"); //$NON-NLS-1$

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Repository[] repos = getRepositories(event);

		if (repos.length == 0)
			return null;

		GitSynchronizeDataSet gsdSet = new GitSynchronizeDataSet();
		for (Repository repo : repos) {
			try {
				List<SyncRepoEntity> syncRepoEntitys = createSyncRepoEntitys(repo);
				SelectSynchronizeResourceDialog dialog = new SelectSynchronizeResourceDialog(
						getShell(event), repo.getDirectory(), syncRepoEntitys);

				if (dialog.open() != IDialogConstants.OK_ID)
					return null;

				gsdSet.add(new GitSynchronizeData(repo, dialog.getSrcRef(),
						dialog.getDstRef(), dialog.shouldIncludeLocal()));
			} catch (URISyntaxException e) {
				Activator.handleError(e.getMessage(), e, true);
			} catch (IOException e) {
				Activator.handleError(e.getMessage(), e, true);
			}
		}

		GitModelSynchronize.launch(gsdSet, getSelectedResources(event));

		return null;
	}

	private List<SyncRepoEntity> createSyncRepoEntitys(Repository repo)
			throws URISyntaxException, IOException {
		RefDatabase refDatabase = repo.getRefDatabase();
		List<RemoteConfig> remoteConfigs = getRemoteConfigs(repo);
		List<SyncRepoEntity> syncRepoEntitys = new ArrayList<SyncRepoEntity>();

		syncRepoEntitys.add(getLocalSyncRepo(repo));
		syncRepoEntitys.add(getTagsSyncRepo(repo));
		for (RemoteConfig rc : remoteConfigs)
			syncRepoEntitys.add(getRemoteSyncRepo(refDatabase, rc));

		return syncRepoEntitys;
	}

	private List<RemoteConfig> getRemoteConfigs(Repository repo)
			throws URISyntaxException {
		return RemoteConfig.getAllRemoteConfigs(repo.getConfig());
	}

	private SyncRepoEntity getLocalSyncRepo(Repository repo) {
		Set<String> allRefs = repo.getAllRefs().keySet();
		SyncRepoEntity local = new SyncRepoEntity(
				UIText.SynchronizeWithAction_localRepoName);
		for (String ref : allRefs)
			if (!ref.startsWith(Constants.R_REMOTES)
					&& !ref.startsWith(Constants.R_TAGS)) {
				local.addRef(createSyncRepoEntity(ref));
			}

		return local;
	}

	private SyncRepoEntity getRemoteSyncRepo(RefDatabase refDatabase,
			RemoteConfig rc) throws IOException {
		String name = rc.getName();
		SyncRepoEntity syncRepoEnt = new SyncRepoEntity(name);
		Collection<Ref> remoteRefs = getRemoteRef(refDatabase, name);

		for (Ref ref : remoteRefs)
			syncRepoEnt.addRef(createSyncRepoEntity(name, ref.getName()));

		return syncRepoEnt;
	}

	private SyncRepoEntity getTagsSyncRepo(Repository repo) {
		Set<String> allRefs = repo.getAllRefs().keySet();
		SyncRepoEntity local = new SyncRepoEntity(
				UIText.SynchronizeWithAction_tagsName);
		for (String ref : allRefs)
			if (ref.startsWith(Constants.R_TAGS))
				local.addRef(createSyncRepoEntity(ref));

		return local;
	}

	private SyncRefEntity createSyncRepoEntity(String ref) {
		return createSyncRepoEntity("", ref); //$NON-NLS-1$
	}

	private SyncRefEntity createSyncRepoEntity(String repoName, String ref) {
		String name = PATTERN.matcher(ref).replaceFirst(""); //$NON-NLS-1$

		if (name.startsWith(repoName + "/")) //$NON-NLS-1$
			name = name.substring(repoName.length() + 1);

		return new SyncRefEntity(name, ref);
	}

	private Collection<Ref> getRemoteRef(RefDatabase refDb, String remoteName)
			throws IOException {
		return refDb.getRefs(Constants.R_REMOTES + remoteName + "/").values(); //$NON-NLS-1$
	}

}
