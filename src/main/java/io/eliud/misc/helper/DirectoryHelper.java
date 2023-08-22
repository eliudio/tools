package io.eliud.misc.helper;

import java.io.File;

public class DirectoryHelper {

	public static File[] getDirectories(String dir) {
		File[] directories = new File(dir).listFiles(File::isDirectory);
		return directories;
	}
}
