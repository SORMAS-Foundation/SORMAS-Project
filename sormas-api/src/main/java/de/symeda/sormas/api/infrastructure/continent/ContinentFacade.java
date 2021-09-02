package de.symeda.sormas.api.infrastructure.continent;

import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ContinentFacade extends InfrastructureBaseFacade<ContinentDto, ContinentIndexDto, ContinentReferenceDto, ContinentCriteria> {

	List<ContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	boolean isUsedInOtherInfrastructureData(Collection<String> continentUuids);

	ContinentReferenceDto getBySubcontinent(SubcontinentReferenceDto subcontinentReferenceDto);

	ContinentReferenceDto getByCountry(CountryReferenceDto countryReferenceDto);

	List<ContinentReferenceDto> getAllActiveAsReference();
}
