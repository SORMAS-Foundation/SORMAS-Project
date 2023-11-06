package de.symeda.sormas.app.backend.region;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class CountryDtoHelper extends AdoDtoHelper<Country, CountryDto> {

	@Override
	protected Class<Country> getAdoClass() {
		return Country.class;
	}

	@Override
	protected Class<CountryDto> getDtoClass() {
		return CountryDto.class;
	}

	@Override
	protected Call<List<CountryDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getCountryFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<CountryDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getCountryFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<CountryDto> countryDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected void fillInnerFromDto(Country ado, CountryDto dto) {
		ado.setName(dto.getDefaultName());
		ado.setIsoCode(dto.getIsoCode());
		ado.setArchived(dto.isArchived());
		ado.setSubcontinent(DatabaseHelper.getSubcontinentDao().getByReferenceDto(dto.getSubcontinent()));
	}

	@Override
	protected void fillInnerFromAdo(CountryDto dto, Country ado) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}

	public static CountryReferenceDto toReferenceDto(Country ado) {
		if (ado == null) {
			return null;
		}

		return new CountryReferenceDto(ado.getUuid(), ado.getIsoCode());
	}
}
