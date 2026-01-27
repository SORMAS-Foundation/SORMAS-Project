/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api;

/**
 * Authentication provider which can be configured trough the {@link ConfigFacade#getAuthenticationProvider()} property.
 * Once initialized it provides Auth Provider specific authentication configs like:
 * <ul>
 * <li>is user name case sensitive</li>
 * <li>is email required</li>
 * </ul>
 *
 * @author Alex Vidrean
 * @since 13-Aug-20
 */
public class AuthProvider {

	public static final String KEYCLOAK = "KEYCLOAK";

	public static final String SORMAS = "SORMAS";

	private static AuthProvider provider;

	private final boolean isDefaultProvider;

	private final boolean isUserSyncAtStartupEnabled;

	private final String name;

	private AuthProvider(ConfigFacade configFacade) {
		String configuredProvider = configFacade.getAuthenticationProvider();
		isDefaultProvider = SORMAS.equalsIgnoreCase(configuredProvider);
		isUserSyncAtStartupEnabled = KEYCLOAK.equalsIgnoreCase(configuredProvider) && configFacade.isAuthenticationProviderUserSyncAtStartupEnabled();
		name = configuredProvider;
	}

	public static AuthProvider getProvider(ConfigFacade configFacade) {
		if (provider == null) {
			synchronized (AuthProvider.class) {
				if (provider == null) {
					provider = new AuthProvider(configFacade);
				}
			}
		}
		return provider;
	}

	/**
	 * Current Authentication Provider is the SORMAS default one.
	 */
	public boolean isDefaultProvider() {
		return isDefaultProvider;
	}

	/**
	 * Even if the Authentication Provider supports user sync, the user sync at startup might be disabled for startup performance reasons.
	 * If user sync is not supported, this will always return false.
	 */
	public boolean isUserSyncAtStartupEnabled() {
		return isUserSyncAtStartupEnabled;
	}

	/**
	 * Name of the active Authentication Provider.
	 */
	public String getName() {
		return name;
	}
}
