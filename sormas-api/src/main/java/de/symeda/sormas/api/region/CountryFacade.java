package de.symeda.sormas.api.region;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface CountryFacade {

	CountryDto getCountryByUuid(String uuid);

	List<CountryDto> getIndexList(CountryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(CountryCriteria criteria);

	void saveCountry(CountryDto dto) throws ValidationRuntimeException;

	void archive(String countryUuid);

	void dearchive(String countryUuid);
}
