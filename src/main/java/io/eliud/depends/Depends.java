package io.eliud.depends;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.*;

import io.eliud.misc.helper.IsEliudDirectory;

public class Depends {
	static String spaces = "                                                                                                                                                                                                   ";
	static String line = "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

	public static void main(String[] args) {
		String sourceDir = IsEliudDirectory.getCurrentDirectory();

		Options options = new Options();
		{
			Option packageOption = new Option("g", "package", true,
					"The package of which you want to run depends on, e.g. -g eliud_pkg_apps");
			packageOption.setRequired(true);
			options.addOption(packageOption);
		}

		{
			Option prefixOption = new Option("p", "prefix", true,
					"The prefix of the packages to cover, probably: -p eliud_");
			prefixOption.setRequired(true);
			options.addOption(prefixOption);
		}

		{
			Option urlOption = new Option("u", "url", true,
					"The url of the eliud packages, e.g. -u https://pub.dev/packages/");
			urlOption.setRequired(true);
			options.addOption(urlOption);
		}

		{
			Option fileOption = new Option("f", "file", true,
					"The filename to add the dependencies, e.g. -f README.md");
			fileOption.setRequired(true);
			options.addOption(fileOption);
		}

		{
			Option directDependenciesOption = new Option("d", "directHeader", true,
					"The header label for the direct dependencies, e.g. -d \"### Direct dependencies\"");
			directDependenciesOption.setRequired(true);
			options.addOption(directDependenciesOption);
		}

		{
			Option indirectDependenciesOption = new Option("i", "indirectHeader", true,
					"The header label for the direct dependencies, e.g. -i \"### Indirect dependencies\"");
			indirectDependenciesOption.setRequired(true);
			options.addOption(indirectDependenciesOption);
		}

		{
			Option devDependenciesOption = new Option("h", "devHeader", true,
					"The header label for the dev dependencies, e.g. -h \"### Development dependencies\"");
			devDependenciesOption.setRequired(true);
			options.addOption(devDependenciesOption);
		}

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		try {
			CommandLine cmd = parser.parse(options, args);
			String packageName = cmd.getOptionValue("package");
			String newPrefix = cmd.getOptionValue("prefix");
			String newURL = cmd.getOptionValue("url");
			String newFile= cmd.getOptionValue("file");
			String directDependenciesHeader = cmd.getOptionValue("directHeader");
			String indirectDependenciesHeader = cmd.getOptionValue("indirectHeader");
			String headerDevDependencies = cmd.getOptionValue("devHeader");
			
			if (packageName.length() == 0) {
				printHelp("package name is empty", formatter, options);
			}
			if (newPrefix.length() == 0) {
				printHelp("prefix is empty", formatter, options);
			}
			if (newURL.length() == 0) {
				System.out.println();
				printHelp("url is empty", formatter, options);
			}
			if (newFile.length() == 0) {
				printHelp("file is empty", formatter, options);
			}
			if (directDependenciesHeader.length() == 0) {
				printHelp("directHeader is empty", formatter, options);
			}
			if (indirectDependenciesHeader.length() == 0) {
				printHelp("indirectHeader is empty", formatter, options);
			}
			if (headerDevDependencies.length() == 0) {
				printHelp("devHeader is empty", formatter, options);
			}
			
			Depends d = new Depends();
			List<String> toAdd = d.runForIt(packageName, newPrefix, newURL, directDependenciesHeader, indirectDependenciesHeader, headerDevDependencies);
			if (toAdd != null) {
				d.insertInFile(toAdd, packageName + "/" + newFile, formatter, options);
			}
		} catch (ParseException e) {
			printHelp(e.getMessage(), formatter, options);
		}
	}

	public List<String> runForIt(String theProject, String prefix, String url, String headerDirectDependencies, String headerIndirectDependencies, String headerDevDependencies) {
		try {
			File f = new File(theProject);
			if (!f.exists()) {
				System.out.println("Error: " + theProject + " does not exist");
				System.exit(-1);
			}
			if (!f.isDirectory()) {
				System.out.println("Error: " + theProject + " is not a directory");
				System.exit(-1);
			}
			/* Create the ProcessBuilder */
			ProcessBuilder pb = new ProcessBuilder("flutter.bat", "pub", "deps", "--json", "--directory=" + theProject);
			pb.redirectErrorStream(true);

			Process proc = pb.start();

			/* Read the process's output */
			String line;
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuffer jsonContents = new StringBuffer();
			while ((line = in.readLine()) != null) {
				jsonContents.append(line);
				// System.out.println(line);
			}

			String jsonString = jsonContents.toString(); // assign your JSON String here
			JSONObject obj = new JSONObject(jsonString);
			JSONArray arr = obj.getJSONArray("packages"); // notice that `"posts": [...]`
			Vector<String> directDependencies = new Vector<>();
			Vector<String> inDirectDependencies = new Vector<>();
			Vector<String> devDependencies = new Vector<>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject object = arr.getJSONObject(i);
				String name = object.getString("name");
				JSONArray arrDependencies = object.getJSONArray("dependencies");
				String kind = object.getString("kind");
				if ((prefix == null) || (name.startsWith(prefix))) {
					if (kind.equals("dev")) devDependencies.add(name);
					if (kind.equals("transitive")) inDirectDependencies.add(name);
					if (kind.equals("direct")) directDependencies.add(name);
				}
			}

			proc.destroy();

			List<String> returnMe = new ArrayList<>();
			if (directDependencies.size() > 0) {
				returnMe.add(headerDirectDependencies);
				for (String directDependency : directDependencies) {
					returnMe.add("- ["+directDependency+"]("+url+directDependency+")");
				}
				returnMe.add("");
			}
			if (inDirectDependencies.size() > 0) {
				returnMe.add(headerIndirectDependencies);
				for (String inDirectDependency : inDirectDependencies) {
					returnMe.add("- ["+inDirectDependency+"]("+url+inDirectDependency+")");
				}
				returnMe.add("");
			}
			if (devDependencies.size() > 0) {
				returnMe.add(headerDevDependencies);
				for (String devDependency : devDependencies) {
					returnMe.add("- ["+devDependency+"]("+url+devDependency+")");
				}
				returnMe.add("");
			}
			
			return returnMe;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	void insertInFile(List<String> toAdd, String fileName, HelpFormatter formatter, Options options) {
		List<String> newContents = new ArrayList<>();

		boolean dependenciesFound = false;
		boolean dependenciesstopFound = false;
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = reader.readLine();
			while (line != null) {
				newContents.add(line);
				if (checkDebuggedString(line, "dependencies")) {
					dependenciesFound = true;
					line = reader.readLine();
					newContents.add(line); // add the <!-- dependencies --> line
					while (line != null) {
						if (checkDebuggedString(line, "dependenciesstop")) {
							dependenciesstopFound = true;
							for (String addThis : toAdd) {
								newContents.add(addThis);
							}
							newContents.add(line); // add the <!-- dependenciesstop --> line
							break;
						}
						line = reader.readLine();
					}
				}
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (dependenciesstopFound) {
			System.out.println("Writing new version of " + fileName);
			try {
				Files.write(Paths.get(fileName), newContents);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (dependenciesFound) {
				printHelp("Occurance of <!-- dependencies --> found in your file " + fileName + ", but no ocurrance of <!-- dependenciesstop --> found", formatter, options);
			} else {
				printHelp("Occurances of <!-- dependencies --> and <!-- dependenciesstop --> missing in your file " + fileName, formatter, options);
			}
		}
	}

	boolean checkDebuggedString(String line, String value) {
		if (!line.startsWith("<!--")) return false;
		if (!line.endsWith("-->")) return false;
		if (line.contains("-" + value + "-")) return true;
		if (line.contains(" " + value + "-")) return true;
		if (line.contains("-" + value + " ")) return true;
		if (line.contains(" " + value + " ")) return true;
		return false;
	}
	
	static void printHelp(String error, HelpFormatter formatter, Options options) {
		System.out.println(error);
		formatter.printHelp(Depends.class.getName(), options);
		System.exit(1);
	}
}
