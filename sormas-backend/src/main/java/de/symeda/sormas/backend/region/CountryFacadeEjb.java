package de.symeda.sormas.backend.region;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.region.CountryCriteria;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.region.CountryFacade;
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Stateless(name = "CountryFacade")
public class CountryFacadeEjb implements CountryFacade {

	@Override
	public CountryDto getCountryByUuid(String uuid) {
		return new CountryDto();
	}

	@Override
	public List<CountryReferenceDto> getByName(String name, boolean includeArchivedEntities) {
		return Collections.emptyList();
	}

	@Override
	public List<CountryDto> getIndexList(CountryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return Collections.emptyList();
	}

	@Override
	public long count(CountryCriteria criteria) {
		return 0;
	}

	@Override
	public void saveCountry(CountryDto dto) throws ValidationRuntimeException {

	}

	@Override
	public void archive(String countryUuid) {

	}

	@Override
	public void dearchive(String countryUuid) {

	}

	@LocalBean
	@Stateless
	public static class CountryFacadeEjbLocal extends CountryFacadeEjb {

	}
}
