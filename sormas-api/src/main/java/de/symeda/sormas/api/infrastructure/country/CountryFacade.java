package de.symeda.sormas.api.infrastructure.country;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.GeoLocationFacade;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CountryFacade extends GeoLocationFacade<CountryDto, CountryIndexDto, CountryReferenceDto, CountryCriteria> {

	List<CountryReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	List<CountryReferenceDto> getAllActiveBySubcontinent(String uuid);

	List<CountryReferenceDto> getAllActiveByContinent(String uuid);

	Page<CountryIndexDto> getIndexPage(CountryCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<CountryReferenceDto> getAllActiveAsReference();

	CountryReferenceDto getServerCountry();

	boolean hasArchivedParentInfrastructure(Collection<String> countryUuids);
}
