package de.symeda.sormas.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionHelper {

	/**
	 * Extract the version (X.Y.Z) from any string. 
	 * If multiple matching patterns are found the last one is returned.
	 * @return null when no version is found.
	 */
	public static int[] extractVersion(String input) {
		
		if (DataHelper.isNullOrEmpty(input)) {
			return null;
		}
		
		Pattern pattern = Pattern.compile("[-\\.]*(\\d+)\\.(\\d+)\\.(\\d+)[-\\.]*");
		int[] version = null;
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			if (version == null) {
				version = new int[3];
			}
			version[0] = Integer.parseInt(matcher.group(1));
			version[1] = Integer.parseInt(matcher.group(2));
			version[2] = Integer.parseInt(matcher.group(3));
			// go on, because we are looking for the last one.
		}
		return version;
	}
	
	public static boolean isVersion(int[] version) {
		return version != null && version.length == 3;
	}

	public static boolean isBefore(int[] version, int[] referenceVersion) {
		
		for (int i=0; i<3; i++) {
			if (version[i] < referenceVersion[i]) {
				return true;
			}
			else if (version[i] > referenceVersion[i]) {
				return false;
			}
		}
		return false;
	}

	public static boolean isAfter(int[] version, int[] referenceVersion) {
		
		for (int i=0; i<3; i++) {
			if (version[i] > referenceVersion[i]) {
				return true;
			}
			else if (version[i] < referenceVersion[i]) {
				return false;
			}
		}
		return false;
	}
	
	public static boolean isEqual(int[] version, int[] referenceVersion) {
		
		for (int i=0; i<3; i++) {
			if (version[i] != referenceVersion[i]) {
				return false;
			}
		}
		return true;
	}
}
