package de.symeda.sormas.api.infrastructure.country;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CountryFacade extends InfrastructureBaseFacade<CountryDto, CountryIndexDto, CountryReferenceDto, CountryCriteria> {

	CountryDto getCountryByUuid(String uuid);

	List<CountryReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	CountryDto getByIsoCode(String isoCode, boolean includeArchivedEntities);

	List<CountryReferenceDto> getAllActiveBySubcontinent(String uuid);

	List<CountryReferenceDto> getAllActiveByContinent(String uuid);

	Page<CountryIndexDto> getIndexPage(CountryCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<CountryReferenceDto> getAllActiveAsReference();

	CountryReferenceDto getServerCountry();

	boolean hasArchivedParentInfrastructure(Collection<String> countryUuids);
}
