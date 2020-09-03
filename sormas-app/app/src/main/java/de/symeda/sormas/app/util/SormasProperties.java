/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SormasProperties {

	private static SormasProperties instance = null;

	private final Properties properties;

	private static final String SERVER_URL_DEFAULT = "server.url.default";
	private static final String USER_NAME_DEFAULT = "user.name.default";
	private static final String USER_PASSWORD_DEFAULT = "user.password.default";

	private static SormasProperties get() {
		if (instance == null)
			instance = new SormasProperties();
		return instance;
	}

	private SormasProperties() {
		properties = loadProperties("/sormas-app.properties");
	}

	public static String getServerUrlDefault() {
		return get().getProperty(SERVER_URL_DEFAULT, null);
	}

	public static String getUserNameDefault() {
		return get().getProperty(USER_NAME_DEFAULT, null);
	}

	public static String getUserPasswordDefault() {
		return get().getProperty(USER_PASSWORD_DEFAULT, null);
	}

	private String getProperty(String name, String defaultValue) {
		String prop = properties.getProperty(name);

		if (prop == null) {
			return defaultValue;
		} else {
			return prop;
		}
	}

	private static Properties loadProperties(@SuppressWarnings("SameParameterValue") String fileName) {
		try (InputStream inputStream = SormasProperties.class.getResourceAsStream(fileName)) {
			Properties properties = new Properties();
			properties.load(inputStream);
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
