/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jsoup.UncheckedIOException;

public class InfoProvider {

	private static InfoProvider instance;

	private final String version;
	private final String commitShortId;
	private final String commitHistoryUrl;

	InfoProvider() {

		try (InputStream fis = InfoProvider.class.getResourceAsStream("/git.properties")) {
			// in case you run test from the IDE and encounter errors, please run `mvn verify -DskipTests` from sormas-base first
			Properties prop = new Properties();
			prop.load(fis);
			this.version = prop.getProperty("git.build.version");
			this.commitShortId = prop.getProperty("git.commit.id.abbrev");
			this.commitHistoryUrl = createLastCommitHistoryUrl(prop.getProperty("git.remote.origin.url"), prop.getProperty("git.commit.id.full"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static synchronized InfoProvider get() {
		if (instance == null) {
			instance = new InfoProvider();
		}
		return instance;
	}

	/**
	 * @param gitRemoteOriginUrl
	 *            URL of the origin repo.
	 * @param commitFullId
	 *            The full/ling id of the last commit.
	 * @return {@code lastCommitHistoryUrl}
	 */
	static String createLastCommitHistoryUrl(String gitRemoteOriginUrl, String commitFullId) {

		String url = gitRemoteOriginUrl;
		if (url.startsWith("git@")) {
			url = url.replaceFirst(":", "/").replace("git@", "https://");
		}

		url = url.replace(".git", "/commits/");
		url = url + commitFullId;
		return url;
	}

	/**
	 * When changing this make sure to check also EXTERNAL_VISITS_API_VERSION.
	 * 
	 * @return
	 */
	public String getMinimumRequiredVersion() {
		return "1.90.0";
	}

	/**
	 * Reads the current version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Reads the current version and replaces the last version number with a 0.
	 */
	public String getBaseVersion() {
		return version.substring(0, version.lastIndexOf(".")) + ".0";
	}

	/**
	 * @return The abbreviated id of the last commit.
	 */
	public String getLastCommitShortId() {
		return commitShortId;
	}

	/**
	 * @return The URL to the commit history starting at the last commit.
	 */
	public String getLastCommitHistoryUrl() {
		return commitHistoryUrl;
	}

	/**
	 * @return {@code true}, if this artifact was built from a SNAPSHOT version.
	 */
	public boolean isSnapshotVersion() {
		return isSnapshot(version);
	}

	/**
	 * @param versionString
	 *            A {@code versionString} to check.
	 * @return {@code true}, if the artifact was built from a SNAPSHOT version.
	 */
	public boolean isSnapshot(String versionString) {

		return versionString.endsWith("SNAPSHOT");
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
