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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionHelper {

	private VersionHelper() {
		// Hide Utility Class Constructor
	}

	/**
	 * Extract the version (X.Y.Z) from any string.
	 * If multiple matching patterns are found the last one is returned.
	 * 
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

		for (int i = 0; i < 3; i++) {
			if (version[i] < referenceVersion[i]) {
				return true;
			} else if (version[i] > referenceVersion[i]) {
				return false;
			}
		}
		return false;
	}

	public static boolean isAfter(int[] version, int[] referenceVersion) {

		for (int i = 0; i < 3; i++) {
			if (version[i] > referenceVersion[i]) {
				return true;
			} else if (version[i] < referenceVersion[i]) {
				return false;
			}
		}
		return false;
	}

	public static boolean isEqual(int[] version, int[] referenceVersion) {

		for (int i = 0; i < 3; i++) {
			if (version[i] != referenceVersion[i]) {
				return false;
			}
		}
		return true;
	}
}
