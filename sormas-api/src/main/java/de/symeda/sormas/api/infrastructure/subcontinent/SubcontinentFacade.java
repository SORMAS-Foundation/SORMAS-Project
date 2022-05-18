package de.symeda.sormas.api.infrastructure.subcontinent;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.GeoLocationFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface SubcontinentFacade extends GeoLocationFacade<SubcontinentDto, SubcontinentIndexDto, SubcontinentReferenceDto, SubcontinentCriteria> {

	List<SubcontinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	boolean isUsedInOtherInfrastructureData(Collection<String> subcontinentUuids);

	boolean hasArchivedParentInfrastructure(Collection<String> subcontinentUuids);

	SubcontinentReferenceDto getByCountry(CountryReferenceDto countryDto);

	List<SubcontinentReferenceDto> getAllActiveByContinent(String uuid);

	Page<SubcontinentIndexDto> getIndexPage(SubcontinentCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<SubcontinentReferenceDto> getAllActiveAsReference();

}
