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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
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

import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.geocoding.GeocodingConfigurationException;
import de.symeda.sormas.api.geocoding.GeocodingConnectionException;
import de.symeda.sormas.api.geocoding.GeocodingException;
import de.symeda.sormas.api.geocoding.GeocodingInsufficientAddressException;
import de.symeda.sormas.api.geocoding.GeocodingNoResultException;
import de.symeda.sormas.api.geocoding.GeocodingResponseException;
import de.symeda.sormas.api.geocoding.GeocodingResultFormatException;
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

	public GeoLatLon getLatLon(Location location) throws GeocodingException {

		String street = Objects.toString(location.getStreet(), "");
		String houseNumber = Objects.toString(location.getHouseNumber(), "");
		String city = Objects.toString(location.getCity(), "");
		String postalCode = Objects.toString(location.getPostalCode(), "");
		return getLatLon(new LocationQuery(houseNumber, street, postalCode, city));
	}

	public GeoLatLon getLatLon(LocationQuery query) throws GeocodingException {

		validateQuery(query);

		String urlTemplate = configFacade.getGeocodingServiceUrlTemplate();
		String userAgent = configFacade.getGeocodingServiceUserAgent();
		String latitudeJsonPath = configFacade.getGeocodingLatitudeJsonPath();
		String longitudeJsonPath = configFacade.getGeocodingLongitudeJsonPath();
		validateConfiguration(urlTemplate, userAgent, latitudeJsonPath, longitudeJsonPath);

		return getLatLon(query, urlTemplate, userAgent, latitudeJsonPath, longitudeJsonPath);
	}

	public boolean isConfigurationValid() {
		String urlTemplate = configFacade.getGeocodingServiceUrlTemplate();
		String userAgent = configFacade.getGeocodingServiceUserAgent();
		String latitudeJsonPath = configFacade.getGeocodingLatitudeJsonPath();
		String longitudeJsonPath = configFacade.getGeocodingLongitudeJsonPath();
		try {
			validateConfiguration(urlTemplate, userAgent, latitudeJsonPath, longitudeJsonPath);
			return true;
		} catch (GeocodingConfigurationException e) {
			return false;
		}
	}

	private GeoLatLon getLatLon(LocationQuery query, String urlTemplate, String userAgent, String latitudeJsonPath, String longitudeJsonPath)
		throws GeocodingException {

		StringSubstitutor substitutor = new StringSubstitutor(buildQuerySubstitutions(query));
		String url = substitutor.replace(urlTemplate);

		URI targetUrl;
		try {
			targetUrl = new URIBuilder(url).build();
		} catch (URISyntaxException e) {
			throw new GeocodingConfigurationException("Geocoding URL template produces an invalid URI", e);
		}

		Client client = ClientHelper.newBuilderWithProxy().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
		try {
			WebTarget target = client.target(targetUrl);

			// prevent timeouts on invalid addresses from causing errors
			try (Response response = target.request(MediaType.APPLICATION_JSON_TYPE).header("User-Agent", userAgent).get()) {
				String responseText = readResponseAsText(response);
				if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
					if (logger.isErrorEnabled()) {
						logger
							.error("geosearch query '{}' returned {} - {}:\n{}", query, response.getStatus(), response.getStatusInfo(), responseText);
					}
					throw new GeocodingResponseException(
						String.format("Geocoding service returned %d %s", response.getStatus(), response.getStatusInfo()));
				}

				Object jsonLatitude = null;
				Object jsonLongitude = null;
				// read values as object, than parse to double
				// JsonPath.read sometimes returns Integer that can't be casted to double, @see #6506
				try {
					jsonLatitude = JsonPath.read(responseText, latitudeJsonPath);
					Double latitude = jsonLatitude != null ? Double.parseDouble(jsonLatitude.toString()) : null;
					jsonLongitude = JsonPath.read(responseText, longitudeJsonPath);
					Double longitude = jsonLongitude != null ? Double.parseDouble(jsonLongitude.toString()) : null;

					if (latitude == null || longitude == null) {
						throw new GeocodingNoResultException("Geocoding service did not return both latitude and longitude");
					}

					return new GeoLatLon(latitude, longitude);
				} catch (PathNotFoundException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("geosearch coordinates not found in '{}'", responseText);
					}

					throw new GeocodingNoResultException("Geocoding service returned no coordinates for the provided address", e);
				} catch (NumberFormatException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("geosearch coordinates can't be parsed: lat: {}, lon: {}", jsonLatitude, jsonLongitude);
					}

					throw new GeocodingResultFormatException("Geocoding service returned invalid coordinate values", e);
				} catch (RuntimeException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("geosearch response could not be parsed", e);
					}

					throw new GeocodingResultFormatException("Geocoding service returned an invalid response body", e);
				}
			}
		} catch (ProcessingException exception) {
			String causeMessage = exception.getCause() != null ? exception.getCause().toString() : exception.toString();
			if (logger.isWarnEnabled()) {
				logger.warn("geosearch query '{}' threw Exception with cause {}", query, causeMessage);
			}
			throw new GeocodingConnectionException("Geocoding service could not be reached", exception);
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	private void validateConfiguration(String urlTemplate, String userAgent, String latitudeJsonPath, String longitudeJsonPath)
		throws GeocodingConfigurationException {
		if (DataHelper.isNullOrEmpty(urlTemplate)) {
			throw new GeocodingConfigurationException("URL Template");
		}
		if (DataHelper.isNullOrEmpty(userAgent)) {
			throw new GeocodingConfigurationException("User agent");
		}
		if (DataHelper.isNullOrEmpty(latitudeJsonPath)) {
			throw new GeocodingConfigurationException("Latitude JSON path");
		}
		if (DataHelper.isNullOrEmpty(longitudeJsonPath)) {
			throw new GeocodingConfigurationException("Longitude JSON path");
		}
	}

	private void validateQuery(LocationQuery query) throws GeocodingInsufficientAddressException {
		if (StringUtils.isBlank(query.getStreet()) || (StringUtils.isBlank(query.getCity()) && StringUtils.isBlank(query.getPostalCode()))) {
			throw new GeocodingInsufficientAddressException("Geocoding requires a street and either a city or postal code");
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
			return DataHelper.isNullOrEmpty(value) ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Can't encode parameter value [" + value + "]", e);
		}
	}
}
