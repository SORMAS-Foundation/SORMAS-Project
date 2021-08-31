package de.symeda.sormas.api.infrastructure.area;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;

@Remote
public interface AreaFacade extends InfrastructureBaseFacade<AreaDto, AreaDto, AreaReferenceDto, AreaCriteria> {

	List<AreaReferenceDto> getAllActiveAsReference();

	boolean isUsedInOtherInfrastructureData(Collection<String> areaUuids);

	List<AreaReferenceDto> getByName(String name, boolean includeArchived);
}
