package de.symeda.sormas.api.infrastructure.subcontinent;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;

@Remote
public interface SubcontinentFacade
	extends InfrastructureBaseFacade<SubcontinentDto, SubcontinentIndexDto, SubcontinentReferenceDto, SubcontinentCriteria> {

	List<SubcontinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	boolean isUsedInOtherInfrastructureData(Collection<String> subcontinentUuids);

	boolean hasArchivedParentInfrastructure(Collection<String> subcontinentUuids);

	SubcontinentReferenceDto getByCountry(CountryReferenceDto countryDto);

	List<SubcontinentReferenceDto> getAllActiveByContinent(String uuid);

	List<SubcontinentReferenceDto> getAllActiveAsReference();
}
