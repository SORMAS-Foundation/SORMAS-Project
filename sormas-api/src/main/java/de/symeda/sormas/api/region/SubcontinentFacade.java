package de.symeda.sormas.api.region;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface SubcontinentFacade extends BaseFacade<SubcontinentDto, SubcontinentIndexDto, SubcontinentReferenceDto, SubcontinentCriteria> {

	List<SubcontinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);
}
