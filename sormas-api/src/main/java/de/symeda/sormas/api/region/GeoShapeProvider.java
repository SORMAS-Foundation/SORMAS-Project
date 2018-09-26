package de.symeda.sormas.api.region;

import javax.ejb.Remote;

@Remote
public interface GeoShapeProvider {

	/**
	 * @return array of polygons, which are arrays of geo-points
	 */
	GeoLatLon[][] getRegionShape(RegionReferenceDto region);

	RegionReferenceDto getRegionByCoord(GeoLatLon latLon);
	
	GeoLatLon getCenterOfAllRegions();

	GeoLatLon getCenterOfRegion(RegionReferenceDto region);

	GeoLatLon[][] getDistrictShape(DistrictReferenceDto district);
	
	DistrictReferenceDto getDistrictByCoord(GeoLatLon latLon);
	
	GeoLatLon getCenterOfDistrict(DistrictReferenceDto district);
}
