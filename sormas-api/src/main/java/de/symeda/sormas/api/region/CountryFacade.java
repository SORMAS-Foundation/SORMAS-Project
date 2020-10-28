package de.symeda.sormas.api.region;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CountryFacade {

	CountryDto getCountryByUuid(String uuid);

	List<CountryReferenceDto> getByName(String name, boolean includeArchivedEntities);

	List<CountryDto> getIndexList(CountryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CountryCriteria criteria);

	void saveCountry(CountryDto dto);

	void archive(String countryUuid);

	void dearchive(String countryUuid);
}
