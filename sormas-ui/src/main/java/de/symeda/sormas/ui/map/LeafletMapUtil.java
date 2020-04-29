package de.symeda.sormas.ui.map;

import java.util.Arrays;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.GeoLatLon;

public final class LeafletMapUtil {

	private LeafletMapUtil() {
		// Hide Utility Class Constructor
	}

	private static final String OTHER_COUNTRIES_OVERLAY_GROUP_ID = "countryNegative";

	public static void clearOtherCountriesOverlay(LeafletMap map) {
		map.removeGroup(OTHER_COUNTRIES_OVERLAY_GROUP_ID);
	}
	
	public static void addOtherCountriesOverlay(LeafletMap map) {

		LeafletPolygon negativeShape = new LeafletPolygon();
		negativeShape.setLatLons(new double[][] {
			new double[] { -90, -180},
			new double[] {  90, -180},
			new double[] {  90,  180},
			new double[] { -90,  180},
		});

		GeoLatLon[][] countryShape = FacadeProvider.getGeoShapeProvider().getCountryShape();
		negativeShape.setHoleLatLons(countryShape);
		
		negativeShape.setOptions("{\"stroke\": false, \"color\": '#FEFEFE', \"fillOpacity\": 1}");
		map.addPolygonGroup(OTHER_COUNTRIES_OVERLAY_GROUP_ID, Arrays.asList(negativeShape));
	}
}
