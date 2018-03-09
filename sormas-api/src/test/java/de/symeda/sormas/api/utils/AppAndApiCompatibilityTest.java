package de.symeda.sormas.api.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppAndApiCompatibilityTest {

	@Test
	public void testMatchingVersionCompatibility() {
		String appVersion = InfoProvider.MINIMUM_REQUIRED_VERSION;
		assertEquals(InfoProvider.isCompatibleToApi(appVersion), CompatibilityCheckResponse.COMPATIBLE);
	}
	
	@Test
	public void testTooOldVersionIncompatibility() {
		// Build a version string that is older than the minimum required version
		String minimumRequiredVersion = InfoProvider.MINIMUM_REQUIRED_VERSION;
		String[] digits = minimumRequiredVersion.split("\\.");
		String appVersion = "";
		if (Integer.parseInt(digits[0]) > 0) {
			appVersion = (Integer.parseInt(digits[0]) - 1) + "." + digits[1] + "." + digits[2];
		} else if (Integer.parseInt(digits[1]) > 0) {
			appVersion = digits[0] + "." + (Integer.parseInt(digits[1]) - 1) + "." + digits[2];
		} else {
			appVersion = digits[0] + "." + digits[1] + "." + (Integer.parseInt(digits[2]) - 1);
		}

		assertEquals(InfoProvider.isCompatibleToApi(appVersion), CompatibilityCheckResponse.TOO_OLD);
	}

	@Test
	public void testTooNewVersionIncompatibility() {
		// Build a version string that is newer than the current server version
		String serverVersion = InfoProvider.getVersion();
		if (serverVersion.contains("-")) {
			serverVersion = serverVersion.substring(0, serverVersion.indexOf("-"));
		}
		String[] digits = serverVersion.split("\\.");
		String appVersion = (Integer.parseInt(digits[0]) + 1) + "." + digits[1] + "." + digits[2];

		assertEquals(InfoProvider.isCompatibleToApi(appVersion), CompatibilityCheckResponse.TOO_NEW);
	}
	
	@Test
	public void testMalformedVersionReturnsError() {
		String appVersion = "wrong/format";
		assertEquals(InfoProvider.isCompatibleToApi(appVersion), CompatibilityCheckResponse.ERROR);
		assertEquals(InfoProvider.isCompatibleToApi(null), CompatibilityCheckResponse.ERROR);
	}

}
