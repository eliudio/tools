package io.eliud.etc;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DuplicateFilenames {
	
	private static String[] excludes = { ".js", ".class", ".map", ".ts", ".css", ".json", ".xml", ".flat", ".coffee", ".aar", ".proto", ".txt", ".mts", ".cjs", ".ps1", ".at", ".jar"};
	// private static String[] includes = { ".dart", ".jpg", ".jpeg", ".png" };
	//private static String[] includes = { ".dart", ".jpg" };
	private static String[] includes = { ".spec" };
	private static boolean useIncludes = true;
	
	class FileInfo {
		String directory;
		long size;
	}
	
	public void find(Map<String, List<FileInfo>> lists, File dir) {
	    for (File f : dir.listFiles()) {
	        if (f.isDirectory()) {
	            find(lists, f);
	        } else {
	            String hash = f.getName();
	            boolean skipIt = false;
	            if (useIncludes) {
	            	skipIt = true;
		            for (String include: includes) {
		            	if (hash.endsWith(include)) {
		            		skipIt = false;
		            		break;
		            	}
		            }
	            } else {
		            if (hash.charAt('.') == -1) {
		            	skipIt = true;
		            } else {
			            for (String exclude: excludes) {
			            	if (hash.endsWith(exclude)) {
			            		skipIt = true;
			            		break;
			            	}
			            }
		            }
	            }
	            if (!skipIt) {
		            List<FileInfo> list = lists.get(hash);
		            if (list == null) {
		                list = new LinkedList<FileInfo>();
		                lists.put(hash, list);
		            }
		            FileInfo info = new FileInfo();
		            info.directory = f.getAbsolutePath();
		            info.size = f.length();
		            list.add(info);
	            }
	        }
	    }
	}


	public static void main(String[] args) {
		try {
			String sourceDir = System.getProperty("user.dir");
			File dir = new File(sourceDir);
			Map<String, List<FileInfo>> lists = new HashMap<String, List<FileInfo>>();
			new DuplicateFilenames().find(lists, dir);
			for (String key : lists.keySet()) {
				List<FileInfo> list = lists.get(key);
				if (list.size() > 1) {
					System.out.println("****" + key + "****");
					for (FileInfo fileInfo : list ) {
						System.out.println("" + fileInfo.directory + " " + fileInfo.size);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
