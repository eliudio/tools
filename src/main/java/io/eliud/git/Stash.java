package io.eliud.git;



import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class Stash {
	
	static boolean reportedDir;

	public static void main(String[] args) {
		try {
			String sourceDir = IsEliudDirectory.getCurrentDirectory();

			File[] directories = DirectoryHelper.getDirectories(sourceDir);
			for (File dir : directories) {
				reportedDir = false;
				Repository localRepo = new FileRepository(dir.getAbsolutePath() + "/.git");
				Git git = new Git(localRepo);        
				git.stashCreate().call();
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
