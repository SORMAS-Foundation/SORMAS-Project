package de.symeda.sormas.api.region;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ContinentFacade extends BaseFacade<ContinentDto, ContinentIndexDto, ContinentReferenceDto, ContinentCriteria> {

	List<ContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	boolean isUsedInOtherInfrastructureData(Collection<String> continentUuids);

}
