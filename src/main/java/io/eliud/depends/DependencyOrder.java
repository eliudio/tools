package io.eliud.depends;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

import io.eliud.misc.helper.IsEliudDirectory;
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

import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class DependencyOrder {
	public static boolean hasPubSpec(File dir) {
		File sourceFile = new File(dir.getAbsolutePath() + "/pubspec.yaml");
		return (sourceFile.exists());
	}

	static YAMLFactory myFactory = new YAMLFactory();
	static YAMLFactoryBuilder factory = (new YAMLFactoryBuilder(myFactory)).configure(Feature.MINIMIZE_QUOTES, true);
	
	static class Project {
		public String name;
		public String version;
		public Vector<String> dependencies;
		
		public Project(String name, String version, Vector<String> dependencies) {
			this.name = name;
			this.version = version;
			this.dependencies = dependencies;
		}
	}

	public static Project getDependencies(File dir) {
		if (!hasPubSpec(dir)) return null;

		File sourceFile = new File(dir.getAbsoluteFile() + "/pubspec.yaml");
		if (sourceFile.exists()) {

			ObjectMapper objectMapper = new YAMLMapper(factory.build());
		
			Map<String, Object> pubspec;
			try {
				pubspec = objectMapper.readValue(sourceFile,
						new TypeReference<Map<String, Object>>() {
						});

				String name = pubspec.get("name").toString();
				String version = pubspec.get("version").toString();
				Vector<String> vectorRep = new Vector<>();
				Map<String, Object> dependencies = (Map<String, Object>) pubspec.get("dependencies");
				for (Map.Entry<String, Object> entry : dependencies.entrySet()) {
				  String dep = entry.getKey().toString();
				  if (dep.startsWith("eliud_")) {
					  vectorRep.add(dep);
				  }
			    }
				
				return new Project(name, version, vectorRep);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	static Map<String, String> projectAndVersion = new HashMap<>();
	static Map<String, Project> projectsWithDependencies = new HashMap<>();
	static Vector<String> orderedProjects = new Vector<>();
	
	static boolean hasDependencyOutsideOrderedProjects(Vector<String> orderedProjects, Vector<String> dependencies) {
		for (String entry: dependencies) {
			if (!orderedProjects.contains(entry)) return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		String sourceDir = IsEliudDirectory.getCurrentDirectory();
		File[] directories = DirectoryHelper.getDirectories(sourceDir);
				
		// retrieve all projects and their dependencies
		for (int i = 0; i < directories.length; i++) {
			File directory = directories[i];
			if (hasPubSpec(directory)) {
				Project project = getDependencies(directory);
				projectsWithDependencies.put(directory.getName(), project);
			}
		}
		
		// run algo to order
		while (orderedProjects.size() < projectsWithDependencies.size()) {
			for (Map.Entry<String, Project> entry : projectsWithDependencies.entrySet()) {
				if (!orderedProjects.contains(entry.getKey())) {
					if (hasDependencyOutsideOrderedProjects(orderedProjects, entry.getValue().dependencies)) {
						orderedProjects.add(entry.getKey());
						break;
					}
				}
			}
		}
		
		System.out.println("Packages ordered so that no package depends on anything but one or more of packages above itself in this list:");
		for (int i = 0; i < orderedProjects.size(); i++) {
			System.out.println(orderedProjects.get(i) + " " + projectsWithDependencies.get(orderedProjects.get(i)).version);
		}
		
	}
}
