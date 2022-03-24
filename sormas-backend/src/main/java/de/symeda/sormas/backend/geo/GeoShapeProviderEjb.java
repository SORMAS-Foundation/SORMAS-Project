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

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.operation.MathTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.geo.GeoShapeProvider;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;

@Stateless(name = "GeoShapeProvider")
public class GeoShapeProviderEjb implements GeoShapeProvider {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private final Map<RegionReferenceDto, MultiPolygon> regionMultiPolygons = new HashMap<>();
	private final Map<RegionReferenceDto, GeoLatLon[][]> regionShapes = new HashMap<>();

	private GeoLatLon regionsCenter;

	private final Map<DistrictReferenceDto, MultiPolygon> districtMultiPolygons = new HashMap<>();
	private final Map<DistrictReferenceDto, GeoLatLon[][]> districtShapes = new HashMap<>();

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
			if (regionMultiPolygon.getValue()
				.contains(
					GeometryFactory.createPointFromInternalCoord(new Coordinate(latLon.getLon(), latLon.getLat()), regionMultiPolygon.getValue()))) {
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
				lon += polygon.getCentroid().getX();
				lat += polygon.getCentroid().getY();
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

		if (regionMultiPolygons.isEmpty() || !regionMultiPolygons.containsKey(region)) {
			return getCenterOfAllRegions();
		}

		Point polygonCenter = regionMultiPolygons.get(region).getCentroid();
		return new GeoLatLon(polygonCenter.getY(), polygonCenter.getX());
	}

	@Override
	public GeoLatLon[][] getDistrictShape(DistrictReferenceDto district) {
		return districtShapes.get(district);
	}

	@Override
	public DistrictReferenceDto getDistrictByCoord(GeoLatLon latLon) {

		for (Entry<DistrictReferenceDto, MultiPolygon> districtMultiPolygon : districtMultiPolygons.entrySet()) {
			if (districtMultiPolygon.getValue()
				.contains(
					GeometryFactory
						.createPointFromInternalCoord(new Coordinate(latLon.getLon(), latLon.getLat()), districtMultiPolygon.getValue()))) {
				return districtMultiPolygon.getKey();
			}
		}
		return null;
	}

	@Override
	public GeoLatLon getCenterOfDistrict(DistrictReferenceDto district) {

		if (districtMultiPolygons.isEmpty() || !districtMultiPolygons.containsKey(district)) {
			return getCenterOfAllRegions();
		}

		Point polygonCenter = districtMultiPolygons.get(district).getCentroid();
		return new GeoLatLon(polygonCenter.getY(), polygonCenter.getX());
	}

	@PostConstruct
	private void loadData() {
		String countryName = configFacade.getCountryName();
		String wkt = configFacade.getGeocodingEPSG4326_WKT();
		if (countryName.isEmpty()) {
			logger.warn("Shape files couldn't be loaded, because no country name is defined in sormas.properties.");
		} else {
			loadRegionData(countryName, wkt);
			loadDistrictData(countryName, wkt);
			buildCountryShape();
		}
	}

	private void loadRegionData(String countryName, String wkt) {

		regionShapes.clear();
		regionMultiPolygons.clear();
		List<RegionReferenceDto> regions = regionFacade.getAllActiveByServerCountry();

		try {
			// load shapefile
			ContentFeatureSource featureSource = GeoShapeHelper.featureSourceOfShapefile(countryName, "regions.shp");
			if (featureSource == null) {
				return;
			}

			MathTransform transform = GeoShapeHelper.getLatLonMathTransform(featureSource, wkt);
			SimpleFeatureIterator iterator = featureSource.getFeatures().features();

			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();

				String shapeRegionName = GeoShapeHelper.sniffShapeAttribute(feature, Arrays.asList("StateName", "REGION", "GEN"));
				if (shapeRegionName == null) {
					continue;
				}

				MultiPolygon multiPolygon = GeoShapeHelper.getPolygon(feature, transform);
				if (multiPolygon == null) {
					// there might me entries without a polygon -> not relevant
					continue;
				}

				Optional<RegionReferenceDto> regionResult = regions.stream().filter(r -> {
					String regionName = r.getCaption().replaceAll("\\W", "").toLowerCase();
					return regionName.contains(shapeRegionName) || shapeRegionName.contains(regionName);
				})
					.reduce(
						(r1, r2) -> {
							// dumb heuristic: take the result that best fits the length
							if (Math.abs(r1.getCaption().length() - shapeRegionName.length())
								<= Math.abs(r2.getCaption().length() - shapeRegionName.length())) {
								return r1;
							} else {
								return r2;
							}
						});

				GeoShapeHelper.storeShape(regionMultiPolygons, regionShapes, multiPolygon, shapeRegionName, regionResult);
			}
			iterator.close();

			GeoShapeHelper.reportNotFound(regionShapes, regions);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		updateCenterOfAllRegions();
	}

	private void loadDistrictData(String countryName, String wkt) {

		districtShapes.clear();
		districtMultiPolygons.clear();
		List<DistrictReferenceDto> districts = districtFacade.getAllActiveAsReference();

		try {
			// load shapefile
			ContentFeatureSource featureSource = GeoShapeHelper.featureSourceOfShapefile(countryName, "districts.shp");
			if (featureSource == null) {
				return;
			}

			MathTransform transform = GeoShapeHelper.getLatLonMathTransform(featureSource, wkt);
			SimpleFeatureIterator iterator = featureSource.getFeatures().features();

			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();

				String shapeDistrictName = GeoShapeHelper.sniffShapeAttribute(feature, Arrays.asList("LGAName", "DISTRICT", "GEN"));
				if (shapeDistrictName == null) {
					continue;
				}

				MultiPolygon multiPolygon = GeoShapeHelper.getPolygon(feature, transform);

				if (multiPolygon == null) {
					// there might me entries without a polygon -> not relevant
					continue;
				}

				Optional<DistrictReferenceDto> districtResult;

				// Use IDs in germany (could also be used for other countries, if fitting externalIDs are provided. Those can then be mapped to the externalID in SORMAS
				if (countryName.equals("germany")) {
					String shapeDistrictId = GeoShapeHelper.sniffShapeAttribute(feature, Collections.singletonList("ARS"));
					if (shapeDistrictId == null) {
						continue;
					}
					districtResult = districts.stream().filter(r -> {
						String districtExtID = r.getExternalId();
						if (districtExtID == null) {
							return false;
						}
						return districtExtID.contains(shapeDistrictId) || shapeDistrictId.contains(districtExtID);
					}).reduce((r1, r2) -> {
						// take the result that best fits
						// in germany, the external IDs in SORMAS usually contain a leading '110'
						if (r1.getExternalId().equals(shapeDistrictId) || r1.getExternalId().equals("110" + shapeDistrictId))
							return r1;
						if (r2.getExternalId().equals(shapeDistrictId) || r2.getExternalId().equals("110" + shapeDistrictId))
							return r2;

						return Double.compare(
							GeoShapeHelper.similarity(r1.getExternalId(), shapeDistrictId),
							GeoShapeHelper.similarity(r2.getExternalId(), shapeDistrictId)) <= 0 ? r1 : r2;
					});
				} else {
					districtResult = districts.stream().filter(r -> {
						String districtName = r.getCaption().replaceAll("\\W", "").toLowerCase();
						return districtName.contains(shapeDistrictName)
							|| shapeDistrictName.contains(districtName)
							|| GeoShapeHelper.similarity(shapeDistrictName, districtName) > 0.7f;
					}).reduce((r1, r2) -> {
						// take the result that best fits

						if (r1.getCaption().replaceAll("\\W", "").toLowerCase().equals(shapeDistrictName))
							return r1;
						if (r2.getCaption().replaceAll("\\W", "").toLowerCase().equals(shapeDistrictName))
							return r2;

						return Double.compare(
							GeoShapeHelper.similarity(r1.getCaption(), shapeDistrictName),
							GeoShapeHelper.similarity(r2.getCaption(), shapeDistrictName)) <= 0 ? r1 : r2;
					});

				}

				GeoShapeHelper.storeShape(districtMultiPolygons, districtShapes, multiPolygon, shapeDistrictName, districtResult);
			}
			iterator.close();

			GeoShapeHelper.reportNotFound(districtShapes, districts);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void buildCountryShape() {

		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();

		// combine all regions that touch into new polygons
		List<Polygon> polygons = new ArrayList<>();
		for (GeoLatLon[][] regionShape : regionShapes.values()) {

			for (GeoLatLon[] regionPolygon : regionShape) {
				try {

					// convert region to polygon
					Polygon polygon = factory.createPolygon(
						Arrays.stream(regionPolygon)
							.map(regionPoint -> new Coordinate(regionPoint.getLon(), regionPoint.getLat()))
							.toArray(Coordinate[]::new));

					boolean added = false;
					for (int i = 0; i < polygons.size(); i++) {
						if (polygons.get(i).touches(polygon)) { // touch?
							polygons.set(i, (Polygon) polygons.get(i).union(polygon)); // union
							added = true;
							break;
						}
					}
					if (!added) {
						polygons.add(polygon);
					}

				} catch (TopologyException e) {
					logger.error(e.toString());
				}
			}
		}

		// go through the polygons again
		for (int i = 0; i < polygons.size(); i++) {
			for (int j = 0; j < polygons.size(); j++) {
				if (i == j)
					continue;
				try {
					if (polygons.get(i).touches(polygons.get(j))) { // touch
						polygons.set(i, (Polygon) polygons.get(i).union(polygons.get(j))); // union
						polygons.remove(j);
						if (i >= j) {
							i--;
							break;
						} else {
							j--;
						}
					}
				} catch (TopologyException e) {
					logger.error(e.toString());
				}
			}
		}

		// fixme: Remove when the shapefiles are no longer part of the resources.
		// fixme: In this case the logic to build country shapes will have to be checked again to fix the problem this is a workaround for.
		if (configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			polygons.add(getRegionPolygon(factory, "Bayern"));
			polygons.add(getRegionPolygon(factory, "Berlin"));
			polygons.add(getRegionPolygon(factory, "Bremen"));
		}

		countryShape = polygons.stream()
			.map(
				polygon -> Arrays.stream(polygon.getCoordinates())
					.map(coordinate -> new GeoLatLon(coordinate.y, coordinate.x))
					.toArray(GeoLatLon[]::new))
			.toArray(GeoLatLon[][]::new);
	}

	private Polygon getRegionPolygon(GeometryFactory factory, String regionCaption) {
		Polygon polygon = null;
		for (Entry<RegionReferenceDto, GeoLatLon[][]> entry : regionShapes.entrySet()) {
			RegionReferenceDto regionReferenceDto = entry.getKey();
			GeoLatLon[][] geoLatLons = entry.getValue();
			if (regionReferenceDto.getCaption().contains(regionCaption)) {
				try {
					polygon = factory.createPolygon(
							Arrays.stream(geoLatLons[0])
									.map(regionPoint -> new Coordinate(regionPoint.getLon(), regionPoint.getLat()))
									.toArray(Coordinate[]::new));
					break;
				} catch (TopologyException e) {
					logger.error(e.toString());
				}
			}
		}
		return polygon;
	}

	/**
	 * Load the shapefile attributions for the configured country.
	 *
	 * @return The shapefile attributions for the configured country.
	 */
	@Override
	public String loadShapefileAttributions() {
		String countryName = configFacade.getCountryName();
		if (countryName.isEmpty()) {
			logger.warn("Attribution couldn't be loaded, because no country name is defined in sormas.properties.");
		} else {
			String filepath = "attributions/" + countryName + "/" + "shapefiles.txt";
			URL filepathUrl = GeoShapeProviderEjb.class.getClassLoader().getResource(filepath);
			if (filepathUrl == null || !filepath.endsWith(".txt")) {
				logger.warn("Invalid attribution filepath: " + filepath + ". No shapefile attribution provided for the configured country?");
				return "";
			}

			List<String> content = Collections.singletonList("");
			try {
				content = Files.readAllLines(Paths.get(filepathUrl.toURI()));
			} catch (Exception e) {
				logger.error("Could not read attribution file: " + e.toString());
			}

			return String.join(",", content);

		}
		return "";
	}

	@LocalBean
	@Stateless
	public static class GeoShapeProviderEjbLocal extends GeoShapeProviderEjb {

	}
}
