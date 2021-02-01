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

package de.symeda.sormas.backend.sormastosormas;

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.SORMAS_REST_PATH;

import javax.enterprise.inject.Alternative;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.backend.util.ClientHelper;

@Alternative
public class SormasToSormasRestClient {

	public static final String SORMAS_REST_URL_TEMPLATE = "https://%s" + SORMAS_REST_PATH + "%s";

	private final ObjectMapper mapper;

	public SormasToSormasRestClient() {
		mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	}

	public Response post(String host, String endpoint, String authToken, Object entity) throws JsonProcessingException, ProcessingException {

		return buildRestClient(host, endpoint, authToken).post(Entity.entity(mapper.writeValueAsString(entity), MediaType.APPLICATION_JSON_TYPE));
	}

	public Response put(String host, String endpoint, String authToken, Object entity) throws JsonProcessingException, ProcessingException {

		return buildRestClient(host, endpoint, authToken).put(Entity.entity(mapper.writeValueAsString(entity), MediaType.APPLICATION_JSON_TYPE));
	}

	private Invocation.Builder buildRestClient(String host, String endpoint, String authToken) {
		return ClientHelper.newBuilderWithProxy()
			.build()
			.target(String.format(SORMAS_REST_URL_TEMPLATE, host, endpoint))
			.request()
			.header("Authorization", authToken);
	}
}
