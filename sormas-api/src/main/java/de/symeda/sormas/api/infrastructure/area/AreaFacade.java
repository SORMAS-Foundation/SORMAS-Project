package de.symeda.sormas.api.infrastructure.area;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.infrastructure.GeoLocationFacade;


@Remote
public interface AreaFacade extends GeoLocationFacade<AreaDto, AreaDto, AreaReferenceDto, AreaCriteria> {

	List<AreaReferenceDto> getAllActiveAsReference();

	boolean isUsedInOtherInfrastructureData(Collection<String> areaUuids);

	List<AreaReferenceDto> getByName(String name, boolean includeArchived);
	
	List<AreaReferenceDto> getByExternalID(Long ext_id, boolean includeArchived);
	
	
}
