package de.symeda.sormas.api.externalpersondata;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Remote;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.location.LocationDto;

@Remote
public interface ExternalPersonDataProviderFacade {

	String EXTERNAL_PERSON_DATA_PROVIDER_ENABLED_KEY = "EXTERNAL_PERSON_DATA_PROVIDER_ENABLED";
	String EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY = "EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER";

	String EXTERNAL_PERSON_DATA_PROVIDER_NAME_KEY = "EXTERNAL_PERSON_DATA_TAB_NAME";

	String EXTERNAL_PERSON_DATA_PROVIDER_MENU_NAME_KEY = "EXTERNAL_PERSON_DATA_DIRECTORY_NAME";

	@Nullable
	ExternalPersonDataSummaryDto findDataByNationalHealthId(@NotNull String nationalHealthId);

	@NotNull
	Map<String, ExternalPersonDataSummaryDto> findSummariesByNationalHealthIds(@NotEmpty List<@NotEmpty String> nationalHealthId);

	@Nullable
	ExternalPersonDataForDisplayDto findDisplayDataByNationalHealthId(@NotNull String nationalHealthId);

	@Nullable
	LocationDto findAppropriateDocumentAddressFor(@NotNull ExternalPersonDataAddressRequestDto request);

	@NotNull
	ExternalPersonDataSearchResponse search(@NotNull ExternalPersonDataSearchRequest request);
}
