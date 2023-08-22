package io.eliud.git;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class Add {

	public static void main(String[] args) {
		try {
			String sourceDir = IsEliudDirectory.getCurrentDirectory();

			File[] directories = DirectoryHelper.getDirectories(sourceDir);
			for (File dir : directories) {
				Repository localRepo = new FileRepository(dir.getAbsolutePath() + "/.git");
				Git git = new Git(localRepo);
				org.eclipse.jgit.api.Status status = git.status().call();
				if (!status.getAdded().isEmpty() || !status.getChanged().isEmpty()
						|| !status.getConflicting().isEmpty() || !status.getMissing().isEmpty()
						|| !status.getModified().isEmpty() || !status.getRemoved().isEmpty()
						|| !status.getUncommittedChanges().isEmpty() || !status.getUntracked().isEmpty()) {
					System.out.println(dir);
					git.add().addFilepattern(".").call();
					git.add().setUpdate(true).addFilepattern(".").call();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
