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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.region;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;

@Stateless(name = "GeoShapeProvider")
public class GeoShapeProviderEjb implements GeoShapeProvider {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private Map<RegionReferenceDto, MultiPolygon> regionMultiPolygons = new HashMap<>();
	private Map<RegionReferenceDto, GeoLatLon[][]> regionShapes = new HashMap<>();
	
	private GeoLatLon regionsCenter;

	private Map<DistrictReferenceDto, MultiPolygon> districtMultiPolygons = new HashMap<>();
	private Map<DistrictReferenceDto, GeoLatLon[][]> districtShapes = new HashMap<>();
	
	private GeoLatLon[][] countryShape;

	@Override
	public GeoLatLon[][] getRegionShape(RegionReferenceDto region) {
		return regionShapes.get(region);
	}
	
	@Override
	public GeoLatLon[][] getCountryShape() {
		return countryShape;
	}
	
	@Override
	public RegionReferenceDto getRegionByCoord(GeoLatLon latLon) {
		for (Entry<RegionReferenceDto, MultiPolygon> regionMultiPolygon : regionMultiPolygons.entrySet()) {
			if (regionMultiPolygon.getValue().contains(GeometryFactory.createPointFromInternalCoord(
					new Coordinate(latLon.getLon(), latLon.getLat()), regionMultiPolygon.getValue()))) {
				return regionMultiPolygon.getKey();
			}
		}
		return null;
	}

	@Override
	public GeoLatLon getCenterOfAllRegions() {
		return regionsCenter;
	}

	protected void updateCenterOfAllRegions() {
		if (regionMultiPolygons.isEmpty()) {
			regionsCenter = null;
			
		} else {
			
			double lat = 0, lon = 0;
			int count = 0;
			for (MultiPolygon polygon : regionMultiPolygons.values()) {
				lat += polygon.getCentroid().getX();
				lon += polygon.getCentroid().getY();
				count++;
			}
			
			if (count > 0) {
				regionsCenter = new GeoLatLon(lat / count, lon / count);
			} else {
				regionsCenter = null;
			}
		}
	}

	@Override
	public GeoLatLon getCenterOfRegion(RegionReferenceDto region) {
		
		if (regionMultiPolygons.isEmpty()
				|| !regionMultiPolygons.containsKey(region)) {
			return getCenterOfAllRegions();
		}
		
		Point polygonCenter = regionMultiPolygons.get(region).getCentroid();
		return new GeoLatLon(polygonCenter.getX(), polygonCenter.getY());
	}
	

	@Override
	public GeoLatLon[][] getDistrictShape(DistrictReferenceDto district) {
		return districtShapes.get(district);
	}

	@Override
	public DistrictReferenceDto getDistrictByCoord(GeoLatLon latLon) {
		for (Entry<DistrictReferenceDto, MultiPolygon> districtMultiPolygon : districtMultiPolygons.entrySet()) {
			if (districtMultiPolygon.getValue().contains(GeometryFactory.createPointFromInternalCoord(
					new Coordinate(latLon.getLon(), latLon.getLat()), districtMultiPolygon.getValue()))) {
				return districtMultiPolygon.getKey();
			}
		}
		return null;
	}
	
	@Override
	public GeoLatLon getCenterOfDistrict(DistrictReferenceDto district) {
		
		if (districtMultiPolygons.isEmpty()
				|| !districtMultiPolygons.containsKey(district)) {
			return getCenterOfAllRegions();
		}
		
		Point polygonCenter = districtMultiPolygons.get(district).getCentroid();
		return new GeoLatLon(polygonCenter.getX(), polygonCenter.getY());
	}

	@PostConstruct
	private void loadData() {

		String countryName = configFacade.getCountryName();
		if (countryName.isEmpty()) {
			logger.warn("Shape files couldn't be loaded, because no country name is defined in sormas.properties.");
		} else {
			loadRegionData(countryName);
			loadDistrictData(countryName);
		}
			buildCountryShape();
	}

	private void loadRegionData(String countryName) {

		regionShapes.clear();
		regionMultiPolygons.clear();

		// load shapefile
		String filepath = "shapefiles/" + countryName + "/regions.shp";
		URL filepathUrl = getClass().getClassLoader().getResource(filepath);
		if (filepathUrl == null || !filepath.endsWith(".shp")) {
			throw new RuntimeException("Invalid shapefile filepath: " + filepath);
		}

		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(filepathUrl);
			ContentFeatureSource featureSource = dataStore.getFeatureSource();
			ContentFeatureCollection featureCollection = featureSource.getFeatures();

			List<RegionReferenceDto> regions = regionFacade.getAllActiveAsReference();

			SimpleFeatureIterator iterator = featureCollection.features();
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				GeometryAttribute defaultGeometryProperty = feature.getDefaultGeometryProperty();
				MultiPolygon multiPolygon = (MultiPolygon) defaultGeometryProperty.getValue();

				// TODO find better solution to column name defintion
				String shapeRegionName = (String) feature.getAttribute("StateName");
				if (shapeRegionName == null) {
					shapeRegionName = (String) feature.getAttribute("REGION");
				}
				shapeRegionName = shapeRegionName.replaceAll("\\W", "").toLowerCase();
				String finalShapeRegionName = shapeRegionName;
				Optional<RegionReferenceDto> regionResult = regions.stream().filter(r -> {
					String regionName = r.getCaption().replaceAll("\\W", "").toLowerCase();
					return regionName.contains(finalShapeRegionName) || finalShapeRegionName.contains(regionName);
				}).reduce((r1, r2) -> {
					// dumb heuristic: take the result that best fits the length
					if (Math.abs(r1.getCaption().length() - finalShapeRegionName.length()) <= Math
							.abs(r2.getCaption().length() - finalShapeRegionName.length())) {
						return r1;
					} else {
						return r2;
					}
				});

				if (!regionResult.isPresent()) {
					logger.warn("Region not found: " + shapeRegionName);
					continue;
				}
				RegionReferenceDto region = regionResult.get();

				regionMultiPolygons.put(region, multiPolygon);

				GeoLatLon[][] regionShape = new GeoLatLon[multiPolygon.getNumGeometries()][];
				for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
					Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
					regionShape[i] = Arrays.stream(polygon.getExteriorRing().getCoordinates())
							.map(c -> new GeoLatLon(c.y, c.x)).toArray(size -> new GeoLatLon[size]);
				}
				regionShapes.put(region, regionShape);
			}
			iterator.close();
			dataStore.dispose();

			StringBuilder notFoundRegions = new StringBuilder();
			for (RegionReferenceDto region : regions) {
				if (!regionShapes.containsKey(region)) {
					if (notFoundRegions.length() > 0) {
						notFoundRegions.append(", ");
					}
					notFoundRegions.append(region.getCaption());
				}
			}
			if (notFoundRegions.length() > 0) {
				logger.warn("No shape for regions found: " + notFoundRegions.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		updateCenterOfAllRegions();
	}

	private void loadDistrictData(String countryName) {

		districtShapes.clear();
		districtMultiPolygons.clear();

		// load shapefile
		String filepath = "shapefiles/" + countryName + "/districts.shp";
		URL filepathUrl = getClass().getClassLoader().getResource(filepath);
		if (filepathUrl == null || !filepath.endsWith(".shp")) {
			throw new RuntimeException("Invalid shapefile filepath: " + filepath);
		}

		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(filepathUrl);
			ContentFeatureSource featureSource = dataStore.getFeatureSource();
			ContentFeatureCollection featureCollection = featureSource.getFeatures();

			List<DistrictReferenceDto> districts = districtFacade.getAllActiveAsReference();

			SimpleFeatureIterator iterator = featureCollection.features();
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				GeometryAttribute defaultGeometryProperty = feature.getDefaultGeometryProperty();
				MultiPolygon multiPolygon = (MultiPolygon) defaultGeometryProperty.getValue();
				if (multiPolygon == null) {
					// there might me entries without a polygon -> not relevant
					continue;
				}

				String shapeDistrictName = (String) feature.getAttribute("LGAName");
				if (shapeDistrictName == null) {
					shapeDistrictName = (String) feature.getAttribute("DISTRICT");
				}
				shapeDistrictName = shapeDistrictName.replaceAll("\\W", "").toLowerCase();
				String finalShapeDistrictName = shapeDistrictName;
				Optional<DistrictReferenceDto> districtResult = districts.stream().filter(r -> {
					String districtName = r.getCaption().replaceAll("\\W", "").toLowerCase();
					return districtName.contains(finalShapeDistrictName)
							|| finalShapeDistrictName.contains(districtName)
							|| similarity(finalShapeDistrictName, districtName) > 0.7f;
				}).reduce((r1, r2) -> {
					// take the result that best fits
					
					if (r1.getCaption().replaceAll("\\W", "").toLowerCase().equals(finalShapeDistrictName))
						return r1;
					if (r2.getCaption().replaceAll("\\W", "").toLowerCase().equals(finalShapeDistrictName))
						return r2;
					
					return Double.compare(similarity(r1.getCaption(), finalShapeDistrictName),
							similarity(r2.getCaption(), finalShapeDistrictName)) <= 0 ? r1 : r2;
				});

				if (!districtResult.isPresent()) {
					logger.warn("District not found: " + shapeDistrictName);
					continue;
				}
				DistrictReferenceDto district = districtResult.get();

				districtMultiPolygons.put(district, multiPolygon);

				GeoLatLon[][] districtShape = new GeoLatLon[multiPolygon.getNumGeometries()][];
				for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
					Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
					districtShape[i] = Arrays.stream(polygon.getExteriorRing().getCoordinates())
							.map(c -> new GeoLatLon(c.y, c.x)).toArray(size -> new GeoLatLon[size]);
				}
				districtShapes.put(district, districtShape);
			}
			iterator.close();
			dataStore.dispose();

			StringBuilder notFoundDistricts = new StringBuilder();
			for (DistrictReferenceDto district : districts) {
				if (!districtShapes.containsKey(district)) {
					if (notFoundDistricts.length() > 0) {
						notFoundDistricts.append(", ");
					}
					notFoundDistricts.append(district.getCaption());
				}
			}
			if (notFoundDistricts.length() > 0) {
				logger.warn("No shape for districts found: " + notFoundDistricts.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void buildCountryShape() {
		
		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();

		// combine all regions that touch into new polygons
		List<Polygon> polygons = new ArrayList<Polygon>();
		for (GeoLatLon[][] regionShape : regionShapes.values()) {
			
			for (GeoLatLon[] regionPolygon : regionShape) {
				
				// convert region to polygon
				Polygon polygon = factory.createPolygon(Arrays.stream(regionPolygon)
						.map(regionPoint -> new Coordinate(regionPoint.getLon(), regionPoint.getLat()))
						.toArray(Coordinate[]::new));
				
				boolean added = false;
				for (int i=0; i<polygons.size(); i++) {
					if (polygons.get(i).touches(polygon)) { // touch?
						polygons.set(i, (Polygon)polygons.get(i).union(polygon)); // union
						added = true;
						break;
					}
				}
			
				if (!added) {
					polygons.add(polygon);
				}
			}
		}
	
		// go through the polygons again
		for (int i=0; i<polygons.size(); i++) {
			for (int j=0; j<polygons.size(); j++) {
				if (i==j)
					continue;
				if (polygons.get(i).touches(polygons.get(j))) { // touch
					polygons.set(i, (Polygon)polygons.get(i).union(polygons.get(j))); // union
					polygons.remove(j);
					if (i >= j) {
						i--;
						break;
					} else {
						j--;
					}
				}
			}
		}
		
		countryShape = polygons.stream()
				.map(polygon -> Arrays.stream(polygon.getCoordinates())
						.map(coordinate -> new GeoLatLon(coordinate.y, coordinate.x))
						.toArray(GeoLatLon[]::new))
				.toArray(GeoLatLon[][]::new);
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

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	public static int editDistance(String s1, String s2) {
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
	
	@LocalBean
	@Stateless
	public static class GeoShapeProviderEjbLocal extends GeoShapeProviderEjb {
	}
}
