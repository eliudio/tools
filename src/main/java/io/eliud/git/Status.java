package io.eliud.git;



import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import io.eliud.misc.helper.DirectoryHelper;

public class Status {
	
	static boolean reportedDir;

	public static void main(String[] args) {
		try {
			// current directory
			String sourceDir = System.getProperty("user.dir");
			System.out.println("Current directory: " + sourceDir);

			File[] directories = DirectoryHelper.getDirectories(sourceDir);
			for (File dir : directories) {
				reportedDir = false;
				Repository localRepo = new FileRepository(dir.getAbsolutePath() + "/.git");
				Git git = new Git(localRepo);        
				org.eclipse.jgit.api.Status status = git.status().call();
				for (String added : status.getAdded()) {
					reportDir(dir.getName());
					System.out.println("  added: " + added);
				}

				for (String added : status.getRemoved()) {
					reportDir(dir.getName());
					System.out.println("  removed: " + added);
				}

				for (String changed : status.getChanged()) {
					reportDir(dir.getName());
					System.out.println("  changed: " + changed);
				}

				for (String changed : status.getModified()) {
					reportDir(dir.getName());
					System.out.println("  modified: " + changed);
				}
				if (reportedDir) {
					System.out.println();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void reportDir(String dir) {
		if (!reportedDir) {
			reportedDir = true;
			System.out.println(dir);
		}
	}
	
}
