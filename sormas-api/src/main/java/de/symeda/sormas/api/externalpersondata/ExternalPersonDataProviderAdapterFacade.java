package de.symeda.sormas.api.externalpersondata;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Remote;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.location.LocationDto;

/**
 * Contract meant to be implemented by external SORMAS adapters to return External Person Data.
 * Linked to @{@link ExternalPersonDataProviderFacade}
 *
 * @implNote Might require caching in implementations, to avoid performance penalities.
 */
@Remote
public interface ExternalPersonDataProviderAdapterFacade {

	@Nullable
	ExternalPersonDataSummaryDto findDataByNationalHealthId(@NotNull String nationalHealthId);

	@Nullable
	ExternalPersonDataForDisplayDto findDisplayDataByNationalHealthId(@NotNull String nationalHealthId);

	@NotNull
	Map<String, ExternalPersonDataSummaryDto> findSummariesByNationalHealthIds(@NotEmpty List<@NotEmpty String> nationalHealthId);

	@Nullable
	LocationDto findAppropriateDocumentAddressFor(@NotNull ExternalPersonDataAddressRequestDto request);

	@NotNull
	ExternalPersonDataSearchResponse find(@NotNull ExternalPersonDataSearchRequest request);

}
