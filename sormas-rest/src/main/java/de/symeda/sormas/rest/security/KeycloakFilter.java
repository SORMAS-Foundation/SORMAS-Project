/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.rest.security;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.servlet.KeycloakOIDCFilter;

import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.rest.security.config.KeycloakConfigResolver;

/**
 * Filter which takes care of validating the Authorization header based on the configuration provided by {@link KeycloakConfigResolver}.
 *
 * @author Alex Vidrean
 * @see KeycloakConfigResolver
 * @since 07-Aug-20
 */
public class KeycloakFilter extends KeycloakOIDCFilter {

	@Inject
	private KeycloakFilter(KeycloakConfigResolver keycloakConfigResolver) {
		super(keycloakConfigResolver);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		boolean s2sRequest = false;
		if (req instanceof HttpServletRequest) {
			final String urlString = ((HttpServletRequest) req).getRequestURL().toString();
			final URL url = new URL(urlString);
			// S2S auth will be handled by S2SAuthFilter
			String s2sResourcePath = SormasToSormasApiConstants.SORMAS_REST_PATH + SormasToSormasApiConstants.RESOURCE_PATH;
			if (url.getPath().startsWith(s2sResourcePath)) {
				s2sRequest = true;
			}
		}
		// if we received an S2S request we skip the local Keycloak auth and
		// resort to central Keycloak via S2SAuthFilter later on
		if (s2sRequest) {
			chain.doFilter(req, res);
		} else {
			super.doFilter(req, res, chain);
		}
	}
}
