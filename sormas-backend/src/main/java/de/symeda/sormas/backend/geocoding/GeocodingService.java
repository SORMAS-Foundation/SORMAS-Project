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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

/**
 * Geocoding for German addresses using the OpenSearch GeoTemporal Service (OSGTS)
 * 
 * @see https://www.bkg.bund.de/SharedDocs/Produktinformationen/BKG/DE/P-2015/150119-Geokodierung.html
 */
@Stateless
@LocalBean
public class GeocodingService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public boolean isEnabled() {
		return configFacade.getGeocodingOsgtsEndpoint() != null;
	}

	public GeoLatLon getLatLon(String query) {

		String endpoint = configFacade.getGeocodingOsgtsEndpoint();
		if (endpoint == null) {
			return null;
		}

		return getLatLon(query, endpoint);
	}

	GeoLatLon getLatLon(String query, String endpoint) {

		Client client = ClientBuilder.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
		URI url;

		try {
			URIBuilder ub = new URIBuilder(endpoint + "/geosearch.json");
			ub.addParameter("query", query);
			ub.addParameter("filter", "typ:haus");
			ub.addParameter("count", "2");

			url = ub.build();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}

		WebTarget target = client.target(url);
		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();

		if (response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
			if (logger.isErrorEnabled()) {
				logger
					.error("geosearch query '{}' returned {} - {}:\n{}", query, response.getStatus(), response.getStatusInfo(), readAsText(response));
			}
			return null;
		}

		FeatureCollection fc = response.readEntity(FeatureCollection.class);

		return Optional.of(fc)
			.map(FeatureCollection::getFeatures)
			.filter(ArrayUtils::isNotEmpty)
			.map(a -> a[0])
			.map(Feature::getGeometry)
			.map(Geometry::getCoordinates)
			//reverse coordinates
			.map(g -> new GeoLatLon(g[1], g[0]))
			.orElse(null);
	}

	private String readAsText(Response response) {
		try {
			return response.readEntity(String.class).trim();
		} catch (RuntimeException e) {
			return "(Exception when retrieving body: " + e + ")";
		}
	}

	@XmlRootElement
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FeatureCollection implements Serializable {

		private static final long serialVersionUID = -1;
		public String type;
		private Feature[] features;

		@Override
		public String toString() {
			return "type " + type + "\n" + ArrayUtils.toString(getFeatures());
		}

		public Feature[] getFeatures() {
			return features;
		}

		public void setFeatures(Feature[] features) {
			this.features = features;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Feature implements Serializable {

		private static final long serialVersionUID = -1;
		private Geometry geometry;
		private FeatureProperties properties;

		@Override
		public String toString() {
			return "geometry " + getGeometry() + "\n properties " + properties;
		}

		public Geometry getGeometry() {
			return geometry;
		}

		public void setGeometry(Geometry geometry) {
			this.geometry = geometry;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Geometry implements Serializable {

		private static final long serialVersionUID = -1;
		private String type;
		private double[] coordinates;

		@Override
		public String toString() {
			return "\ntype " + getType() + "\n coordinates " + getCoordinates();
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public double[] getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(double[] coordinates) {
			this.coordinates = coordinates;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FeatureProperties implements Serializable {

		private static final long serialVersionUID = -1;

		private String text;
		private String typ;
		private double score;
		private String ags;
		private String rs;
		private String schluessel;
		private String bundesland;
		private String kreis;
		private String verwgem;
		private String gemeinde;
		private String plz;
		private String ort;
		private String ortsteil;
		private String strasse;
		private String haus;
		private String qualitaet;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getTyp() {
			return typ;
		}

		public void setTyp(String typ) {
			this.typ = typ;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		public String getAgs() {
			return ags;
		}

		public void setAgs(String ags) {
			this.ags = ags;
		}

		public String getRs() {
			return rs;
		}

		public void setRs(String rs) {
			this.rs = rs;
		}

		public String getSchluessel() {
			return schluessel;
		}

		public void setSchluessel(String schluessel) {
			this.schluessel = schluessel;
		}

		public String getBundesland() {
			return bundesland;
		}

		public void setBundesland(String bundesland) {
			this.bundesland = bundesland;
		}

		public String getKreis() {
			return kreis;
		}

		public void setKreis(String kreis) {
			this.kreis = kreis;
		}

		public String getVerwgem() {
			return verwgem;
		}

		public void setVerwgem(String verwgem) {
			this.verwgem = verwgem;
		}

		public String getGemeinde() {
			return gemeinde;
		}

		public void setGemeinde(String gemeinde) {
			this.gemeinde = gemeinde;
		}

		public String getPlz() {
			return plz;
		}

		public void setPlz(String plz) {
			this.plz = plz;
		}

		public String getOrt() {
			return ort;
		}

		public void setOrt(String ort) {
			this.ort = ort;
		}

		public String getOrtsteil() {
			return ortsteil;
		}

		public void setOrtsteil(String ortsteil) {
			this.ortsteil = ortsteil;
		}

		public String getStrasse() {
			return strasse;
		}

		public void setStrasse(String strasse) {
			this.strasse = strasse;
		}

		public String getHaus() {
			return haus;
		}

		public void setHaus(String haus) {
			this.haus = haus;
		}

		public String getQualitaet() {
			return qualitaet;
		}

		public void setQualitaet(String qualitaet) {
			this.qualitaet = qualitaet;
		}
	}
}
