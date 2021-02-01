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

package de.symeda.sormas.backend.util;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * Helper class for creating REST clients injects supported properties from system properties.
 *
 * @author Alex Vidrean
 * @since 04-Nov-20
 */
public class ClientHelper {

	public static final List<String> SUPPORTED_PROXY_PROPERTIES = Arrays.asList(
		ResteasyClientBuilder.PROPERTY_PROXY_HOST,
		ResteasyClientBuilder.PROPERTY_PROXY_PORT,
		ResteasyClientBuilder.PROPERTY_PROXY_SCHEME);

	public static ClientBuilder newBuilderWithProxy() {
		ClientBuilder clientBuilder = ClientBuilder.newBuilder();

		if (clientBuilder instanceof ResteasyClientBuilder) {
			System.getProperties()
				.entrySet()
				.stream()
				.filter(property -> SUPPORTED_PROXY_PROPERTIES.contains(property.getKey().toString()))
				.forEach(property -> clientBuilder.property(property.getKey().toString(), property.getValue()));

		}
		return clientBuilder;
	}

}
