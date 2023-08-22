package io.eliud.misc.yaml;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.eliud.misc.helper.DirectoryHelper;

public class ChangeVersion {

	public static void main(String[] args) {
		try {
			String sourceDir = "C:\\src\\eliud"; // Pdf files are read from this folder
			String packageName = "eliud_core";
			String newVersion = "1.0.4+7";
			
			File[] directories = DirectoryHelper.getDirectories(sourceDir);
			for (File dir : directories) {
				File sourceFile = new File(dir.getAbsolutePath() + "/pubspec.yaml");
				if (sourceFile.exists()) {
					ObjectMapper objectMapper = new YAMLMapper();
					Map<String, Object> pubspec = objectMapper.readValue(sourceFile,
				            new TypeReference<Map<String, Object>>() { });
					String currentPackageName = pubspec.get("name").toString();
					if (currentPackageName.equals(packageName)) {
						System.out.println("Updating " + currentPackageName);
						pubspec.put("version", newVersion);
					} else {
						System.out.println("Updating " + currentPackageName + " (dep)");
						// modify the dependencies
						Map<String, Object> dependencies = (Map<String, Object>) pubspec.get("dependencies");
						dependencies.put(packageName, newVersion);
					}
					objectMapper.writeValue(sourceFile, pubspec);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
