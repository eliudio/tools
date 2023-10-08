package io.eliud.misc.yaml;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.eliud.misc.helper.DirectoryHelper;
import io.eliud.misc.helper.IsEliudDirectory;

public class BumpAllVersions {

	public static void main(String[] args) {
		String sourceDir = IsEliudDirectory.getCurrentDirectory();
		File[] directories = DirectoryHelper.getDirectories(sourceDir);
		for (File dir : directories) {
			File referencedFile = new File(dir.getAbsolutePath() + "/pubspec.yaml");
			if (referencedFile.exists()) {
				ObjectMapper referencedObjectMapper = new YAMLMapper();
				Map<String, Object> pubspecReferenced;
				try {
					pubspecReferenced = referencedObjectMapper.readValue(referencedFile,
					        new TypeReference<Map<String, Object>>() { });
					String currentPackageName = pubspecReferenced.get("name").toString();
					String currentVersion = pubspecReferenced.get("version").toString();
					if ((currentPackageName != null) && (currentVersion != null)) {
						System.out.println("Bumping version " + currentVersion.toString() + " of " + currentPackageName + " +1");
						ChangeVersion.change(directories, sourceDir, dir.getName(), "+1", false);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
    }
}
