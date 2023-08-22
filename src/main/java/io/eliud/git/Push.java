package io.eliud.git;



import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class Push {

	public static void main(String[] args) {
		try {
			String userName = System.getenv("git.username");
			String password = System.getenv("git.password");
			if (userName == null) {
				System.out.println("Environment variable git.username is missing");
				System.exit(-1);
			}
			if (password == null) {
				System.out.println("Environment variable git.password is missing");
				System.exit(-1);
			}
			CredentialsProvider cp = new UsernamePasswordCredentialsProvider(userName, password);
			
			String sourceDir = IsEliudDirectory.getCurrentDirectory();

			File[] directories = DirectoryHelper.getDirectories(sourceDir);
			for (File dir : directories) {

				FileRepositoryBuilder x;
				Repository localRepo = new FileRepository(dir.getAbsolutePath() + "/.git");
				Git git = new Git(localRepo);   
				org.eclipse.jgit.api.Status status = git.status().call();
				System.out.println(dir);
				git.push().setCredentialsProvider(cp).call();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
