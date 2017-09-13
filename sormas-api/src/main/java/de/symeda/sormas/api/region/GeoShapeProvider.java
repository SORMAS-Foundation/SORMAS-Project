package de.symeda.sormas.api.region;

import javax.ejb.Remote;

@Remote
public interface GeoShapeProvider {

	GeoLatLon[][] getRegionShape(RegionReferenceDto region);

	RegionReferenceDto getRegionByCoord(GeoLatLon latLon);
}
