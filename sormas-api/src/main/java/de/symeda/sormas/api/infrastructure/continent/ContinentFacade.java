package de.symeda.sormas.api.infrastructure.continent;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.GeoLocationFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface ContinentFacade extends GeoLocationFacade<ContinentDto, ContinentIndexDto, ContinentReferenceDto, ContinentCriteria> {

	List<ContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	boolean isUsedInOtherInfrastructureData(Collection<String> continentUuids);

	ContinentReferenceDto getBySubcontinent(SubcontinentReferenceDto subcontinentReferenceDto);

	ContinentReferenceDto getByCountry(CountryReferenceDto countryReferenceDto);

	Page<ContinentIndexDto> getIndexPage(ContinentCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<ContinentReferenceDto> getAllActiveAsReference();
}
