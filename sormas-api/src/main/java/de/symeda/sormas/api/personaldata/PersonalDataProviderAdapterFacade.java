package de.symeda.sormas.api.personaldata;

import java.util.List;

import javax.annotation.Nullable;
import javax.ejb.Remote;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.location.LocationDto;

/**
 * Closely linked to @{@link PersonalDataProviderFacade} this is the one to be implemented by external adapters.
 */
@Remote
public interface PersonalDataProviderAdapterFacade {

	@Nullable
	PersonalDataSummaryDto findDataByNationalHealthId(@NotNull String nationalHealthId);

	// TODO: might extend this to use: getCreatableEntityDtos() results
	@Nullable
	PersonalDataForDisplayDto findDisplayDataByNationalHealthId(@NotNull String nationalHealthId);

	@Nullable
	LocationDto findAppropriateDocumentAddressFor(@NotNull PersonalDataAddressRequestDto request);

	@NotNull
	PersonalDataSearchResponse search(@NotNull PersonalDataSearchRequest request);

	@NotEmpty
	List<CreatableEntityDto> getCreatableEntityDtos();

	<T extends EntityDto> T createDtoFrom(@NotNull String nationalHealthId, @NotNull Class<T> clazzToCreate);
}
