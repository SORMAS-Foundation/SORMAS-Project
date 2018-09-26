package de.symeda.sormas.backend.region;

import java.net.URL;
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
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;

@Stateless(name = "GeoShapeProvider")
public class GeoShapeProviderEjb implements GeoShapeProvider {

	final static Logger logger = LoggerFactory.getLogger(GeoShapeProviderEjb.class);

	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private Map<RegionReferenceDto, MultiPolygon> regionMultiPolygons;
	private Map<RegionReferenceDto, GeoLatLon[][]> regionShapes;

	private Map<DistrictReferenceDto, MultiPolygon> districtMultiPolygons;
	private Map<DistrictReferenceDto, GeoLatLon[][]> districtShapes;

	@Override
	public GeoLatLon[][] getRegionShape(RegionReferenceDto region) {
		return regionShapes.get(region);
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

		double lat = 0, lon = 0;
		int count = 0;
		for (MultiPolygon polygon : regionMultiPolygons.values()) {
			lat += polygon.getCentroid().getX();
			lon += polygon.getCentroid().getY();
			count++;
		}
		if (count > 0) {
			return new GeoLatLon(lat / count, lon / count);
		} else {
			return null;
		}
	}

	@Override
	public GeoLatLon getCenterOfRegion(RegionReferenceDto region) {
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
		Point polygonCenter = districtMultiPolygons.get(district).getCentroid();
		return new GeoLatLon(polygonCenter.getX(), polygonCenter.getY());
	}

	@PostConstruct
	private void loadData() {

		loadRegionData();
		loadDistrictData();
	}

	private void loadRegionData() {

		regionShapes = new HashMap<>();
		regionMultiPolygons = new HashMap<>();

		// load shapefile
		String countryName = configFacade.getCountryName();
		String filepath = "shapefiles/" + countryName + "/regions.shp";
		URL filepathUrl = getClass().getClassLoader().getResource(filepath);
		if (filepathUrl == null || !filepath.endsWith(".shp")) {
			throw new RuntimeException("Invalid shapefile filepath: " + filepath);
		}

		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(filepathUrl);
			ContentFeatureSource featureSource = dataStore.getFeatureSource();
			ContentFeatureCollection featureCollection = featureSource.getFeatures();

			List<RegionReferenceDto> regions = regionFacade.getAllAsReference();

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
	}

	private void loadDistrictData() {

		districtShapes = new HashMap<>();
		districtMultiPolygons = new HashMap<>();

		// load shapefile
		String countryName = configFacade.getCountryName();
		String filepath = "shapefiles/" + countryName + "/districts.shp";
		URL filepathUrl = getClass().getClassLoader().getResource(filepath);
		if (filepathUrl == null || !filepath.endsWith(".shp")) {
			throw new RuntimeException("Invalid shapefile filepath: " + filepath);
		}

		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(filepathUrl);
			ContentFeatureSource featureSource = dataStore.getFeatureSource();
			ContentFeatureCollection featureCollection = featureSource.getFeatures();

			List<DistrictReferenceDto> districts = districtFacade.getAllAsReference();

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
