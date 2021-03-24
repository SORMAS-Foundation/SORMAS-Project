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
package de.symeda.sormas.backend.geocoding;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.nimbusds.jose.util.StandardCharset;

import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.util.ClientHelper;

@Stateless
@LocalBean
public class GeocodingService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String HOUSE_NUMBER_PLACEHOLDER = "houseNumber";
	private static final String STREET_PLACEHOLDER = "street";
	private static final String POSTAL_CODE_PLACEHOLDER = "postalCode";
	private static final String CITY_PLACEHOLDER = "city";

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public boolean isEnabled() {
		return configFacade.getGeocodingServiceUrlTemplate() != null;
	}

	public GeoLatLon getLatLon(Location location) {

		String street = Objects.toString(location.getStreet(), "");
		String houseNumber = Objects.toString(location.getHouseNumber(), "");
		String city = Objects.toString(location.getCity(), "");
		String postalCode = Objects.toString(location.getPostalCode(), "");
		if (StringUtils.isNotBlank(street) && (StringUtils.isNotBlank(city) || StringUtils.isNotBlank(postalCode))) {
			return getLatLon(new LocationQuery(houseNumber, street, postalCode, city));
		}
		return null;
	}

	public GeoLatLon getLatLon(LocationQuery query) {

		String urlTemplate = configFacade.getGeocodingServiceUrlTemplate();
		if (DataHelper.isNullOrEmpty(urlTemplate)
			|| DataHelper.isNullOrEmpty(configFacade.getGeocodingLatitudeJsonPath())
			|| DataHelper.isNullOrEmpty(configFacade.getGeocodingLongitudeJsonPath())) {
			return null;
		}

		return getLatLon(query, urlTemplate);
	}

	private GeoLatLon getLatLon(LocationQuery query, String urlTemplate) {

		StringSubstitutor substitutor = new StringSubstitutor(buildQuerySubstitutions(query));
		String url = substitutor.replace(urlTemplate);

		URI targetUrl;
		try {
			targetUrl = new URIBuilder(url).build();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}

		Client client = ClientHelper.newBuilderWithProxy().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
		WebTarget target = client.target(targetUrl);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
		String responseText = readResponseAsText(response);

		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			if (logger.isErrorEnabled()) {
				logger.error("geosearch query '{}' returned {} - {}:\n{}", query, response.getStatus(), response.getStatusInfo(), responseText);
			}
			return null;
		}

		try {
			Double latitude = JsonPath.read(responseText, configFacade.getGeocodingLatitudeJsonPath());
			Double longitude = JsonPath.read(responseText, configFacade.getGeocodingLongitudeJsonPath());

			return new GeoLatLon(latitude, longitude);
		} catch (PathNotFoundException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("geosearch coordinates not found in '{}'" + responseText);
			}

			return null;
		}
	}

	private String readResponseAsText(Response response) {
		try {
			return response.readEntity(String.class).trim();
		} catch (RuntimeException e) {
			return "(Exception when retrieving body: " + e + ")";
		}
	}

	public Map<String, String> buildQuerySubstitutions(LocationQuery query) {
		Map<String, String> replacement = new HashMap<>();
		replacement.put(STREET_PLACEHOLDER, encodeValue(query.getStreet()));
		replacement.put(HOUSE_NUMBER_PLACEHOLDER, encodeValue(query.getHouseNumber()));
		replacement.put(POSTAL_CODE_PLACEHOLDER, encodeValue(query.getPostalCode()));
		replacement.put(CITY_PLACEHOLDER, encodeValue(query.getCity()));

		return replacement;
	}

	private String encodeValue(String value) {
		try {
			return DataHelper.isNullOrEmpty(value) ? "" : URLEncoder.encode(value, StandardCharset.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Can't encode parameter value [" + value + "]", e);
		}
	}
}
