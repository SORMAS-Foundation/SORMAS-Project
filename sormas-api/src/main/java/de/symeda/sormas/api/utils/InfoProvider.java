package de.symeda.sormas.api.utils;

import java.io.IOException;
import java.io.InputStream;

public class InfoProvider {

	private static InfoProvider instance;
	
	private final String version;

	InfoProvider() {
		try {
			InputStream stream = InfoProvider.class.getResourceAsStream("/version.txt");
			String version = DataHelper.convertStreamToString(stream);
			this.version = version.trim();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static synchronized InfoProvider get() {
		if (instance == null) {
			instance = new InfoProvider();
		}
		return instance;
	}

	
	public String getMinimumRequiredVersion() {
		return "0.23.0";
	}
	
	/**
	 * Reads the version from the version.txt where it is written by maven.
	 * We are doing it this way, because all other version information (manifest, pom) will be removed in the android app by gradle.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Checks if the app version is compatible with the api version. This is true when the version is at least as high as the
	 * MINIMUM_REQUIRED_VERSION and lower or equal to the version returned by getVersion().
	 */
	public CompatibilityCheckResponse isCompatibleToApi(String appVersionInput) {

		return isCompatibleToApi(VersionHelper.extractVersion(appVersionInput));
	}
	
	/**
	 * Checks if the app version is compatible with the api version. This is true when the version is at least as high as the
	 * MINIMUM_REQUIRED_VERSION and lower or equal to the version returned by getVersion().
	 */
	public CompatibilityCheckResponse isCompatibleToApi(int[] appVersion) {

		if (!VersionHelper.isVersion(appVersion)) {
			throw new IllegalArgumentException("No proper app version provided");
		}
		
		int[] minVersion = VersionHelper.extractVersion(getMinimumRequiredVersion());

		if (VersionHelper.isBefore(appVersion, minVersion)) {
			return CompatibilityCheckResponse.TOO_OLD;
		}

		int[] serverVersion = VersionHelper.extractVersion(getVersion());
		if (VersionHelper.isAfter(appVersion, serverVersion)) {
			return CompatibilityCheckResponse.TOO_NEW;
		}

		return CompatibilityCheckResponse.COMPATIBLE;
	}
}
