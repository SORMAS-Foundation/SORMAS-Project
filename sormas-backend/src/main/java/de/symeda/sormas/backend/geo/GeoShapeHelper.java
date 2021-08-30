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

package de.symeda.sormas.backend.geo;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.geo.GeoLatLon;

public class GeoShapeHelper {

	private static final Logger logger = LoggerFactory.getLogger(GeoShapeHelper.class);

	/**
	 * Returns the specific features/settings for the given shapefile.
	 * 
	 * @param countryName
	 *            Load the shapefile for this country.
	 * @param filename
	 *            The filename (e.g., regions.shp or districts.shp).
	 * @return An object containing all the settings of the loaded shapefile.
	 * @throws IOException
	 *             Throws in case the file could not be loaded.
	 */
	public static ContentFeatureSource featureSourceOfShapefile(String countryName, String filename) throws IOException {
		String filepath = "shapefiles/" + countryName + "/" + filename;
		URL filepathUrl = GeoShapeHelper.class.getClassLoader().getResource(filepath);
		if (filepathUrl == null || !filepath.endsWith(".shp")) {
			logger.warn("Invalid shapefile filepath: " + filepath);
			return null;
		}

		ShapefileDataStore dataStore = new ShapefileDataStore(filepathUrl);
		ContentFeatureSource source = dataStore.getFeatureSource();
		dataStore.dispose();
		return source;
	}

	/**
	 * Returns an object which allows to transform the coordinates of the given shapefile into GPS coordinates
	 * (i.e., latitude and longitude). There are dozens of different mathematical foundations, but this functions
	 * allows to transform all of them into a convenient format.
	 * 
	 * @param featureSource
	 *            The settings (including the reference coordinate system) of a shapefile.
	 * @return
	 *         A transformer which returns GPS coordinates for any shapefile,
	 * @throws FactoryException
	 *             Throws if the requested math transformer could not be build.
	 */
	public static MathTransform getLatLonMathTransform(ContentFeatureSource featureSource, String wkt) throws FactoryException {
		// The CRS of the source file. There are tons of different schemas
		CoordinateReferenceSystem sourceCRS = featureSource.getSchema().getCoordinateReferenceSystem();

		// The coordinates system you want to reproject the data to
		// EPSG:4326 is the coordinate reference system GPS uses. Now you know :)
		CoordinateReferenceSystem targetCRS = CRS.parseWKT(wkt);

		return CRS.findMathTransform(sourceCRS, targetCRS, true);
	}

	/**
	 * Extract the polygon for the given feature and transform it into GPS coordinates.
	 * 
	 * @param feature
	 *            A feature containing a polygon.
	 * @param transform
	 *            The transformer which reprojects the features CRS to EPSG4326.
	 * @return
	 *         The polygon contained in the feature in with latitude and longitude.
	 * @throws TransformException
	 *             Throws in case the transformation to EPSG4326 cannot be computed.
	 */
	public static MultiPolygon getPolygon(SimpleFeature feature, MathTransform transform) throws TransformException {
		// get the geometry of the actual feature
		Geometry sourceGeometry = (Geometry) feature.getDefaultGeometry();
		if (sourceGeometry == null) {
			logger.warn("Could not get the default geometry for " + feature.toString());
			return null;
		}

		// transform the geometry and save it in a new variable
		Geometry reprojectedGeometry = JTS.transform(sourceGeometry, transform);

		// set the reprojected geometry as the geometry of the actual feature
		feature.setDefaultGeometry(reprojectedGeometry);

		return (MultiPolygon) feature.getDefaultGeometryProperty().getValue();
	}

	/**
	 * Try to find an attribute of the feature.
	 * 
	 * It is not clear which fields hold the specific attribute of the feature (e.g., region name).
	 * 
	 * @param feature
	 *            The feature we want to learn the attribute of.
	 * @param attributeNames
	 *            A list of attribute-name candidates
	 * @return
	 *         The value of the feature attribute, null if no attribute with a fitting attribute-name could be found.
	 */
	public static String sniffShapeAttribute(SimpleFeature feature, List<String> attributeNames) {
		String shapeAttribute = null;
		// all these attributes can hold the region name
		for (String attr : attributeNames) {
			shapeAttribute = (String) feature.getAttribute(attr);
			if (shapeAttribute != null) {
				break;
			}
		}
		if (shapeAttribute == null) {
			logger.error("No attribute for the shape could be found");
			return null;
		}

		return shapeAttribute.replaceAll("\\W", "").toLowerCase();
	}

	/**
	 * Convert a polygon to an 2D array of Lat/Lon coordinates.
	 * 
	 * @param multiPolygon
	 *            The polygon which Lat/Lon coordinates get extracted.
	 * @return
	 *         2D array of Lat/Lon coordinates of the polygon.
	 */
	private static GeoLatLon[][] polygonToGeoLatLons(MultiPolygon multiPolygon) {
		GeoLatLon[][] shape = new GeoLatLon[multiPolygon.getNumGeometries()][];
		for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
			Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
			shape[i] = Arrays.stream(polygon.getExteriorRing().getCoordinates()).map(c -> new GeoLatLon(c.y, c.x)).toArray(GeoLatLon[]::new);
		}
		return shape;
	}

	/**
	 * Store the polygon and its coordinates for a given region or district in their corresponding caches.
	 * 
	 * @param polygonStore
	 *            The polygon cache of the EJB.
	 * @param shapeStore
	 *            The shape cache of the EJB.
	 * @param multiPolygon
	 *            The polygon being cached.
	 * @param shapeName
	 *            The name of the shape (i.e., region/district name).
	 * @param infrastructure
	 *            The InfrastructureReferenceDto of the current shape.
	 * @param <T>
	 *            Either RegionReferenceDto or DistrictReferenceDto of the given shape.
	 */
	public static <T> void storeShape(
		Map<T, MultiPolygon> polygonStore,
		Map<T, GeoLatLon[][]> shapeStore,
		MultiPolygon multiPolygon,
		String shapeName,
		Optional<T> infrastructure) {
		if (!infrastructure.isPresent()) {
			logger.warn("Shape not found: " + shapeName);
			return;
		}

		T infra = infrastructure.get();
		polygonStore.put(infra, multiPolygon);
		GeoLatLon[][] shape = polygonToGeoLatLons(multiPolygon);
		shapeStore.put(infra, shape);
	}

	/**
	 * Warn about all regions/districts which do not have an associated shape.
	 * 
	 * @param shapeStore
	 *            The shape cache of the EJB.
	 * @param infraList
	 *            The list of all regions/districts stored in the system.
	 * @param <T>
	 *            Either RegionReferenceDto or DistrictReferenceDto.
	 * 
	 */
	public static <T> void reportNotFound(Map<T, GeoLatLon[][]> shapeStore, List<T> infraList) {
		StringBuilder notFound = new StringBuilder();
		for (T infra : infraList) {
			if (!shapeStore.containsKey(infra)) {
				if (notFound.length() > 0) {
					notFound.append(", ");
				}
				notFound.append(((InfrastructureDataReferenceDto) infra).getCaption());
			}
		}
		if (notFound.length() > 0) {
			logger.warn("No shape found for: " + notFound.toString());
		}
	}

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 */
	public static double similarity(String s1, String s2) {

		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater
			// length
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0;
			/* both strings are zero length */ }
		/*
		 * // If you have StringUtils, you can use it to calculate the edit
		 * distance: return (longerLength -
		 * StringUtils.getLevenshteinDistance(longer, shorter)) / (double)
		 * longerLength;
		 */
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	/**
	 * Example implementation of the Levenshtein Edit Distance
	 * See http://rosettacode.org/wiki/Levenshtein_distance#Java
	 *
	 * @return
	 *         The Levenshtein Edit Distance of s1 and s2.
	 */
	public static int editDistance(String s1, String s2) {
		// Fixme(@JonasCir) This is likely duplicate code
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}

}
