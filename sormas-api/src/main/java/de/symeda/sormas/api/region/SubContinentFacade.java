package de.symeda.sormas.api.region;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface SubContinentFacade extends BaseFacade<SubContinentDto, SubContinentIndexDto, SubContinentReferenceDto, SubContinentCriteria> {
    List<SubContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);
}