package de.symeda.sormas.api.region;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface CountryFacade {

	CountryDto getCountryByUuid(String uuid);

	List<CountryReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities);

	CountryDto getByIsoCode(String isoCode, boolean includeArchivedEntities);

	List<CountryIndexDto> getIndexList(CountryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CountryCriteria criteria);

	String saveCountry(CountryDto dto);

	String mergeOrSaveCountry(CountryDto dto) throws ValidationRuntimeException;

	void archive(String countryUuid);

	void dearchive(String countryUuid);

	List<CountryDto> getAllAfter(Date date);

	List<CountryDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	List<CountryReferenceDto> getAllActiveAsReference();

	CountryReferenceDto getServerCountry();

	boolean hasArchivedParentInfrastructure(Collection<String> countryUuids);

}
