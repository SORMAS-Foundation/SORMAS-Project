/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.api;

/**
 * Authentication provider which can be configured trough the {@link ConfigFacade#getAuthenticationProvider()} property.
 * Once initialized it provides Auth Provider specific authentication configs like:
 * <ul>
 *     <li>is user name case sensitive</li>
 *     <li>is email required</li>
 * </ul>
 *
 * @author Alex Vidrean
 * @since 13-Aug-20
 */
public class AuthProvider {

    public static final String KEYCLOAK = "KEYCLOAK";

    public static final String SORMAS = "SORMAS";

    private static AuthProvider provider;

    private final boolean isUsernameCaseSensitive;

    private final boolean isDefaultProvider;

    private final boolean isUserSyncSupported;

    private AuthProvider() {
        String configuredProvider = FacadeProvider.getConfigFacade().getAuthenticationProvider();
        isUsernameCaseSensitive = SORMAS.equalsIgnoreCase(configuredProvider);
        isDefaultProvider = SORMAS.equalsIgnoreCase(configuredProvider);
        isUserSyncSupported = KEYCLOAK.equalsIgnoreCase(configuredProvider);
    }

    public static AuthProvider getProvider() {
        if (provider == null) {
            synchronized (AuthProvider.class) {
                if (provider == null) {
                    provider = new AuthProvider();
                }
            }
        }
        return provider;
    }

    /**
     * Authentication Provider requires usernames to be case sensitive or insensitive
     */
    public boolean isUsernameCaseSensitive() {
        return isUsernameCaseSensitive;
    }

    /**
     * Current Authentication Provider is the SORMAS default one.
     */
    public boolean isDefaultProvider() {
        return isDefaultProvider;
    }

    /**
     * Authentication Provider enables users to be synced from the default provider.
     */
    public boolean isUserSyncSupported() {
        return isUserSyncSupported;
    }

}
