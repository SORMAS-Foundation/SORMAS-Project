package de.symeda.sormas.api.region;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface SubcontinentFacade extends BaseFacade<SubcontinentDto, SubcontinentIndexDto, SubcontinentReferenceDto, SubcontinentCriteria> {

	List<SubcontinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	boolean isUsedInOtherInfrastructureData(Collection<String> subcontinentUuids);

	boolean hasArchivedParentInfrastructure(Collection<String> subcontinentUuids);

	SubcontinentDto save(SubcontinentDto dto, boolean allowMerge);

	SubcontinentReferenceDto getByCountry(CountryReferenceDto countryDto);

    List<SubcontinentReferenceDto> getAllActiveByContinent(String uuid);

	List<SubcontinentReferenceDto> getAllActiveAsReference();
}

