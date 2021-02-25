package de.symeda.sormas.api.region;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CountryFacade {

	CountryDto getCountryByUuid(String uuid);

	List<CountryReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	CountryDto getByIsoCode(String isoCode, boolean includeArchivedEntities);

	List<CountryIndexDto> getIndexList(CountryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CountryCriteria criteria);

	String saveCountry(CountryDto dto);

	void archive(String countryUuid);

	void dearchive(String countryUuid);

	List<CountryDto> getAllAfter(Date date);

	List<CountryDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	List<CountryReferenceDto> getAllActiveAsReference();

	CountryReferenceDto getServerCountry();
}
