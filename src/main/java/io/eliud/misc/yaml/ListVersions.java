package io.eliud.misc.yaml;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class ListVersions {

	public static void main(String[] args) {
		String sourceDir = IsEliudDirectory.getCurrentDirectory();
		try {
			File[] directories = DirectoryHelper.getDirectories(sourceDir);

			for (File dir : directories) {
				File sourceFile = new File(dir.getAbsolutePath() + "/pubspec.yaml");
				if (sourceFile.exists()) {
					YAMLFactory myFactory = new YAMLFactory();
					YAMLFactoryBuilder yamlFactoryBuilder = (new YAMLFactoryBuilder(myFactory)).configure(Feature.MINIMIZE_QUOTES, false);
					ObjectMapper referencedObjectMapper = new YAMLMapper(yamlFactoryBuilder.build());
					Map<String, Object> pubspecReferenced = referencedObjectMapper.readValue(sourceFile,
							new TypeReference<Map<String, Object>>() {
							});
					String currentPackageName = pubspecReferenced.get("name").toString();
					String packageName = currentPackageName;
					String currentVersion = pubspecReferenced.get("version").toString();
					System.out.println(currentPackageName + ": " + currentVersion);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
