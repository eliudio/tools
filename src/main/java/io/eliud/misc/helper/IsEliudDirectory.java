package io.eliud.misc.helper;

public class IsEliudDirectory {
	public static String getCurrentDirectory() {
		String sourceDir = System.getProperty("user.dir");
		if (!sourceDir.endsWith("eliud")) {
			System.out.println("Current directory must be eliud");

			System.exit(1);
		} else {
			System.out.println("Current directory: " + sourceDir);
		}
		return sourceDir;
	}
}	
