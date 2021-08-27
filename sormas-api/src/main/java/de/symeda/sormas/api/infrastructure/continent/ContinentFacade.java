package de.symeda.sormas.api.infrastructure.continent;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;

@Remote
public interface ContinentFacade extends BaseFacade<ContinentDto, ContinentIndexDto, ContinentReferenceDto, ContinentCriteria> {

	List<ContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	boolean isUsedInOtherInfrastructureData(Collection<String> continentUuids);

	ContinentDto save(@Valid ContinentDto dto, boolean allowMerge);

    ContinentReferenceDto getBySubcontinent(SubcontinentReferenceDto subcontinentReferenceDto);

    ContinentReferenceDto getByCountry(CountryReferenceDto countryReferenceDto);

	List<ContinentReferenceDto> getAllActiveAsReference();
}
