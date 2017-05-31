package de.symeda.sormas.api.utils;

import java.io.IOException;
import java.io.InputStream;

public final class InfoProvider {

	/**
	 * Reads the version from the version.txt where it is written by maven.
	 * We are doing it this way, because all other version information (manifest, pom) will be removed in the android app by gradle.
	 */
	public static String getVersion() {
		try {
			InputStream stream = InfoProvider.class.getResourceAsStream("/version.txt");
			String version = DataHelper.convertStreamToString(stream);
			version = version.trim();
			return version;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
