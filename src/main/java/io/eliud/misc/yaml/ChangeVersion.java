package io.eliud.misc.yaml;

import java.io.File;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;

import io.eliud.git.Commit;
import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class ChangeVersion {

	public static void main(String[] args) {
		String sourceDir = IsEliudDirectory.getCurrentDirectory();

		Options options = new Options();
		Option packageOption = new Option("p", "package", true,
				"The package of which you want to change it's version, e.g. eliud_pkg_apps");
		packageOption.setRequired(true);
		options.addOption(packageOption);
		Option versionOption = new Option("v", "version", true,
				"The version the package should become (or +1 to bump one up), e.g. 1.4.1, 1.4.1+3 or +1");
		versionOption.setRequired(true);
		options.addOption(versionOption);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		try {
			CommandLine cmd = parser.parse(options, args);
			String packageName = cmd.getOptionValue("package");
			String newVersion = cmd.getOptionValue("version");
			if (packageName.length() == 0) {
				System.out.println("package name is empty");
				formatter.printHelp(ChangeVersion.class.getName(), options);

				System.exit(1);
			}
			if (newVersion.length() == 0) {
				System.out.println("version name is empty");
				formatter.printHelp(ChangeVersion.class.getName(), options);

				System.exit(1);
			}

			change(sourceDir, packageName, newVersion);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp(ChangeVersion.class.getName(), options);

			System.exit(1);

		}
	}

	public static void change(String sourceDir, String packageDir, String newVersion) {
		try {
			String newNewVersion;
			File[] directories = DirectoryHelper.getDirectories(sourceDir);

			File referencedFile = new File(sourceDir + "/" + packageDir + "/pubspec.yaml");
			if (referencedFile.exists()) {
				YAMLFactory myFactory = new YAMLFactory();
				YAMLFactoryBuilder yamlFactoryBuilder = (new YAMLFactoryBuilder(myFactory)).configure(Feature.MINIMIZE_QUOTES, false);
				ObjectMapper referencedObjectMapper = new YAMLMapper(yamlFactoryBuilder.build());
				Map<String, Object> pubspecReferenced = referencedObjectMapper.readValue(referencedFile,
						new TypeReference<Map<String, Object>>() {
						});
				String currentPackageName = pubspecReferenced.get("name").toString();
				String packageName = currentPackageName;
				String currentVersion = pubspecReferenced.get("version").toString();
				if (newVersion.equals("+1")) {
					int pos = currentVersion.indexOf('+');
					if (pos > 0) {
						String remaining = currentVersion.substring(pos);
						int number = Integer.parseInt(remaining);
						newNewVersion = "^" + currentVersion.substring(0, pos) + "+" + (number + 1);
					} else {
						newNewVersion = "^" + currentVersion + "+1";
					}
				} else {
					newNewVersion = "^" + newVersion;
				}

				System.out.println(currentPackageName + " " + currentVersion + " => " + newNewVersion);
				pubspecReferenced.put("version", newNewVersion);
				YAMLFactoryBuilder yamlFactoryBuilderWriter = (new YAMLFactoryBuilder(myFactory)).configure(Feature.MINIMIZE_QUOTES, true);
				ObjectMapper referencedObjectMapperWriter = new YAMLMapper(yamlFactoryBuilderWriter.build());
				referencedObjectMapperWriter.writeValue(referencedFile, pubspecReferenced);

				boolean referencingFound = false;
				for (File dir : directories) {
					File sourceFile = new File(dir.getAbsolutePath() + "/pubspec.yaml");
					if (sourceFile.exists()) {
//						YAMLFactoryBuilder factory = (new YAMLFactory().builder()).stringQuotingChecker(new MyStringQuotingChecker());
						YAMLFactoryBuilder factory = (new YAMLFactoryBuilder(myFactory)).configure(Feature.MINIMIZE_QUOTES, true);
						ObjectMapper objectMapper = new YAMLMapper(factory.build());
					
						Map<String, Object> pubspec = objectMapper.readValue(sourceFile,
								new TypeReference<Map<String, Object>>() {
								});
						currentPackageName = pubspec.get("name").toString();
						if (!currentPackageName.equals(packageName)) {
							// modify the dependencies
							Map<String, Object> dependencies = (Map<String, Object>) pubspec.get("dependencies");
							Object referencingVersion = dependencies.get(packageName);
							if (referencingVersion != null) {
								if (!referencingFound) {
									System.out.println("    Following referring packages found:");
									referencingFound = true;
								}
								System.out.println("    " + currentPackageName + " -> " + packageName + " "
										+ referencingVersion.toString() + "=>" + newNewVersion);
								dependencies.put(packageName, newNewVersion);
								objectMapper.writeValue(sourceFile, pubspec);
							}
						}
					}
				}

				if (!referencingFound) {
					System.out.println("    No referring packages found");
				}

			} else {
				System.out.println("Error: Cannot open file " + referencedFile.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
