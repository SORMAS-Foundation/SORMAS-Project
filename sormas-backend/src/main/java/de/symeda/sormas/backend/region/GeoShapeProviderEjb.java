package de.symeda.sormas.backend.region;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.backend.common.ConfigService;

@Singleton(name = "GeoShapeProvider")
public class GeoShapeProviderEjb implements GeoShapeProvider {
	
	final static Logger logger = LoggerFactory.getLogger(GeoShapeProviderEjb.class);

	@EJB
	private ConfigService configService;
	
	private Map<RegionReferenceDto, MultiPolygon> regionMultiPolygons;
	private Map<RegionReferenceDto, GeoLatLon[][]> regionShapes;
	
	public GeoLatLon[][] getRegionShape(RegionReferenceDto region) {
		return regionShapes.get(region);
	}
	
	public RegionReferenceDto getRegionByCoord(GeoLatLon latLon) {
		for (Entry<RegionReferenceDto, MultiPolygon> regionMultiPolygon : regionMultiPolygons.entrySet()) {
			if (regionMultiPolygon.getValue().contains(
					GeometryFactory.createPointFromInternalCoord(
							new Coordinate(latLon.getLon(), latLon.getLat()), regionMultiPolygon.getValue()))) {
				return regionMultiPolygon.getKey();
			}
		}
		return null;
	}
	
	public GeoLatLon getCenterOfAllRegions() {
		
		double lat = 0, lon = 0;
		int count = 0;
		for (MultiPolygon polygon : regionMultiPolygons.values()) {
			lat += polygon.getCentroid().getX();
			lon += polygon.getCentroid().getY();
			count++;
		}
		if (count > 0) {
			return new GeoLatLon(lat/count, lon/count);
		} else {
			return null;
		}		
	}
	
	public GeoLatLon getCenterOfRegion(RegionReferenceDto region) {
		Point polygonCenter = regionMultiPolygons.get(region).getCentroid();
		return new GeoLatLon(polygonCenter.getX(), polygonCenter.getY());
	}

	@PostConstruct
    private void loadRegionData() {

		regionShapes = new HashMap<>();
		regionMultiPolygons = new HashMap<>();
		
    	// load shapefile
		String countryName = configService.getCountryName();
    	String filepath = "shapefiles/" + countryName + "/regions.shp";
    	File file = new File(getClass().getClassLoader().getResource(filepath).getFile());
		if (!file.exists() || !filepath.endsWith(".shp")) {
		    throw new RuntimeException("Invalid shapefile filepath: " + filepath);
		}
			
		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
			ContentFeatureSource featureSource = dataStore.getFeatureSource();
			ContentFeatureCollection featureCollection = featureSource.getFeatures();
			
		    List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllAsReference();

		    SimpleFeatureIterator iterator = featureCollection.features();
			while(iterator.hasNext()) {
			    SimpleFeature feature = iterator.next();
			    GeometryAttribute defaultGeometryProperty = feature.getDefaultGeometryProperty();
			    MultiPolygon multiPolygon = (MultiPolygon) defaultGeometryProperty.getValue();
			    
			    String shapeRegionName = (String)feature.getAttribute("StateName");
			    if (shapeRegionName == null) {
			    	shapeRegionName = (String)feature.getAttribute("REGION");
			    }
			    shapeRegionName = shapeRegionName.replaceAll("\\W", "").toLowerCase();
			    String finalShapeRegionName = shapeRegionName;
			    Optional<RegionReferenceDto> regionResult = regions.stream()
			    		.filter(r -> {
			    			String regionName = r.getCaption().replaceAll("\\W", "").toLowerCase();
			    			return regionName.contains(finalShapeRegionName) || finalShapeRegionName.contains(regionName);
			    		})
			    		.reduce((r1,r2) -> {
			    			// dumb heuristic: take the result that best fits the length
			    			if (Math.abs(r1.getCaption().length()-finalShapeRegionName.length())
			    					<= Math.abs(r2.getCaption().length()-finalShapeRegionName.length())) {
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
			    for (int i=0; i<multiPolygon.getNumGeometries(); i++) {
			    	Polygon polygon = (Polygon)multiPolygon.getGeometryN(i);
			    	regionShape[i] = Arrays
		    			.stream(polygon.getExteriorRing().getCoordinates())
			    		.map(c -> new GeoLatLon(c.y, c.x))
			    		.toArray(size -> new GeoLatLon[size]);
			    }
			    regionShapes.put(region, regionShape);
			}
			iterator.close();
			dataStore.dispose();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}
