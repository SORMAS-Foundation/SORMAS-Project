package de.symeda.sormas.api.externalpersondata;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Remote;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.location.LocationDto;

/**
 * Closely linked to @{@link ExternalPersonDataProviderFacade} this is the one to be implemented by external adapters.
 */
@Remote
public interface ExternalPersonDataProviderAdapterFacade {

	@Nullable
	ExternalPersonDataSummaryDto findDataByNationalHealthId(@NotNull String nationalHealthId);

	// TODO: might extend this to use: getCreatableEntityDtos() results
	@Nullable
	ExternalPersonDataForDisplayDto findDisplayDataByNationalHealthId(@NotNull String nationalHealthId);

	@NotNull
	Map<String, ExternalPersonDataSummaryDto> findSummariesByNationalHealthIds(@NotEmpty List<@NotEmpty String> nationalHealthId);

	@Nullable
	LocationDto findAppropriateDocumentAddressFor(@NotNull ExternalPersonDataAddressRequestDto request);

	@NotNull
	ExternalPersonDataSearchResponse search(@NotNull ExternalPersonDataSearchRequest request);

}
