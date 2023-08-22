package io.eliud.git;



import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import io.eliud.misc.helper.DirectoryHelper;

public class Add {

	public static void main(String[] args) {
		try {
			// current directory
			String sourceDir = System.getProperty("user.dir");
			System.out.println("Current directory: " + sourceDir);

			File[] directories = DirectoryHelper.getDirectories(sourceDir);
			for (File dir : directories) {

				FileRepositoryBuilder x;
				Repository localRepo = new FileRepository(dir.getAbsolutePath() + "/.git");
				Git git = new Git(localRepo);   
				org.eclipse.jgit.api.Status status = git.status().call();
				if (status.hasUncommittedChanges()) {
					System.out.println(dir);
					git.add().addFilepattern(".").call();
					//git.add().setUpdate(true).addFilepattern(".").call();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
