package de.symeda.sormas.api.infrastructure.area;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.infrastructure.GeoInfrastructureBaseFacade;

@Remote
public interface AreaFacade extends GeoInfrastructureBaseFacade<AreaDto, AreaDto, AreaReferenceDto, AreaCriteria> {

	List<AreaReferenceDto> getAllActiveAsReference();

	boolean isUsedInOtherInfrastructureData(Collection<String> areaUuids);

	List<AreaReferenceDto> getByName(String name, boolean includeArchived);
}
