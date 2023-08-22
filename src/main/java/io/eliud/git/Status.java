package io.eliud.git;



import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class Status {
	
	static boolean reportedDir;

	public static void main(String[] args) {
		try {
			String sourceDir = IsEliudDirectory.getCurrentDirectory();

			File[] directories = DirectoryHelper.getDirectories(sourceDir);
			for (File dir : directories) {
				reportedDir = false;
				Repository localRepo = new FileRepository(dir.getAbsolutePath() + "/.git");
				Git git = new Git(localRepo);        
				org.eclipse.jgit.api.Status status = git.status().call();
				for (String x : status.getAdded()) {
					reportDir(dir.getName());
					System.out.println("  added: " + x);
				}
				for (String x : status.getChanged()) {
					reportDir(dir.getName());
					System.out.println("  changed: " + x);
				}
				for (String x : status.getConflicting()) {
					reportDir(dir.getName());
					System.out.println("  conflicting: " + x);
				}
/*
				for (String x : status.getIgnoredNotInIndex()) {
					reportDir(dir.getName());
					System.out.println("  ignored not in index: " + x);
				}
*/
				for (String x : status.getMissing()) {
					reportDir(dir.getName());
					System.out.println("  missing: " + x);
				}
				for (String x : status.getModified()) {
					reportDir(dir.getName());
					System.out.println("  modified: " + x);
				}
				for (String x : status.getRemoved()) {
					reportDir(dir.getName());
					System.out.println("  removed: " + x);
				}
				for (String x : status.getUncommittedChanges()) {
					reportDir(dir.getName());
					System.out.println("  uncommitted changes: " + x);
				}
				for (String x : status.getUntracked()) {
					reportDir(dir.getName());
					System.out.println("  untracked: " + x);
				}
/*
				for (String x : status.getUntrackedFolders()) {
					reportDir(dir.getName());
					System.out.println("  untracked folder: " + x);
				}
*/
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
