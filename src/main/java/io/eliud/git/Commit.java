package io.eliud.git;



import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import io.eliud.misc.helper.DirectoryHelper;


public class Commit {

	public static void main(String[] args) {
		try {
			// current directory
			String sourceDir = System.getProperty("user.dir");
			System.out.println("Current directory: " + sourceDir);

	        Options options = new Options();
	        Option messageOption = new Option("m", "message", true, "The commit message");
	        messageOption.setRequired(true);
	        options.addOption(messageOption);
	        CommandLineParser parser = new DefaultParser();
	        HelpFormatter formatter = new HelpFormatter();

	        try {
		        CommandLine cmd = parser.parse(options, args);
		        String message = cmd.getOptionValue("message");

				File[] directories = DirectoryHelper.getDirectories(sourceDir);
				for (File dir : directories) {

					FileRepositoryBuilder x;
					Repository localRepo = new FileRepository(dir.getAbsolutePath() + "/.git");
					Git git = new Git(localRepo);   
					System.out.println(dir);
					git.commit().setMessage(message).call();
				}
	        } catch (ParseException e) {
	            System.out.println(e.getMessage());
	            formatter.printHelp(Commit.class.getName(), options);

	            System.exit(1);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}


