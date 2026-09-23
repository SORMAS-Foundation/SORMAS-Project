package de.symeda.sormas.api.personaldata;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.location.LocationDto;

public interface PersonalDataProviderFacade {

	@Nullable
	PersonalDataDto findDataByNationalHealthId(@NotNull String nationalHealthId);

	@Nullable
	PersonalDataForDisplayDto findDisplayDataByNationalHealthId(@NotNull String nationalHealthId);

	@Nullable
	LocationDto findAppropriateDocumentAddressFor(@NotNull PersonalDataAddressRequestDto request);

	@NotEmpty
	PersonalDataSearchResponse search(@NotNull PersonalDataSearchRequest request);
}
