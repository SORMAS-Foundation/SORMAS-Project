package de.symeda.sormas.api.utils;

import java.io.IOException;
import java.io.InputStream;

public final class InfoProvider {

	public static final String MINIMUM_REQUIRED_VERSION = "0.16.0";
	
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

	/**
	 * Checks if the app version is compatible with the api version. This is true when the version is at least as high as the
	 * MINIMUM_REQUIRED_VERSION and lower or equal to the version returned by getVersion().
	 */
	public static CompatibilityCheckResponse isCompatibleToApi(String appVersion) {
		if (appVersion == null) {
			return CompatibilityCheckResponse.ERROR;
		}

		try {
			String serverVersion = InfoProvider.getVersion();
			if (serverVersion.contains("-")) {
				serverVersion = serverVersion.substring(0, serverVersion.indexOf("-"));
			}
			if (appVersion.contains("-")) {
				appVersion = appVersion.substring(0, appVersion.indexOf("-"));
			}
			String[] minReqVersionDigits = MINIMUM_REQUIRED_VERSION.split("\\.");
			String[] serverVersionDigits = serverVersion.split("\\.");
			String[] appVersionDigits = appVersion.split("\\.");

			if (Integer.parseInt(appVersionDigits[0]) < Integer.parseInt(minReqVersionDigits[0])) {
				return CompatibilityCheckResponse.TOO_OLD;
			} else if (Integer.parseInt(appVersionDigits[1]) < Integer.parseInt(minReqVersionDigits[1])) {
				return CompatibilityCheckResponse.TOO_OLD;
			} else if (Integer.parseInt(appVersionDigits[2]) < Integer.parseInt(minReqVersionDigits[2])) {
				return CompatibilityCheckResponse.TOO_OLD;
			}

			if (Integer.parseInt(appVersionDigits[0]) > Integer.parseInt(serverVersionDigits[0])) {
				return CompatibilityCheckResponse.TOO_NEW;
			} else if (Integer.parseInt(appVersionDigits[1]) > Integer.parseInt(serverVersionDigits[1])) {
				return CompatibilityCheckResponse.TOO_NEW;
			} else if (Integer.parseInt(appVersionDigits[2]) > Integer.parseInt(serverVersionDigits[2])) {
				return CompatibilityCheckResponse.TOO_NEW;
			}
		} catch (Exception e) {
			return CompatibilityCheckResponse.ERROR;
		}

		return CompatibilityCheckResponse.COMPATIBLE;
	}

}
