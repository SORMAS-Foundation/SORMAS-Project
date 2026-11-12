package de.symeda.sormas.api.externalpersondata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ejb.Remote;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.location.LocationDto;

/**
 * For countries with external systems containing personal data, this interface allows integration.
 * <p>
 * Meant to decouple national specifics for SORMAS-core meant to be generic for all countries.
 * <p>
 * Linked to {@link ExternalPersonDataProviderAdapterFacade}.
 */
@Remote
public interface ExternalPersonDataProviderFacade {

	String EXTERNAL_PERSON_DATA_PROVIDER_ENABLED_KEY = "EXTERNAL_PERSON_DATA_PROVIDER_ENABLED";
	String EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY = "EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_NAME";

	String EXTERNAL_PERSON_DATA_TAB_NAME_KEY = "EXTERNAL_PERSON_DATA_TAB_NAME";

	String EXTERNAL_PERSON_DATA_DIRECTORY_NAME_KEY = "EXTERNAL_PERSON_DATA_DIRECTORY_NAME";

	String EXTERNAL_PERSON_DATA_HTML_SANITIZATION_CONFIG_KEY = "EXTERNAL_PERSON_DATA_HTML_SANITIZATION_CONFIG";

	@Nullable
	ExternalPersonDataSummaryDto findDataByNationalHealthId(@NotNull String nationalHealthId);

	@NotNull
	Map<String, ExternalPersonDataSummaryDto> findSummariesByNationalHealthIds(@NotEmpty List<@NotEmpty String> nationalHealthId);

	@Nullable
	ExternalPersonDataForDisplayDto findDisplayDataByNationalHealthId(@NotNull String nationalHealthId);

	/**
	 * Usable within documents using: {@link de.symeda.sormas.api.docgeneneration.RootEntityType#ROOT_EXTERNAL_PERSON_DATA}
	 * 
	 * @param request
	 *            allowing to match the adequate entity in the current context.
	 * @return most appropriate address.
	 */
	@Nullable
	LocationDto findAppropriateDocumentAddressFor(@NotNull ExternalPersonDataAddressRequestDto request);

	@NotNull
	ExternalPersonDataSearchResponse find(@NotNull ExternalPersonDataSearchRequest request);

	/**
	 * @return dictionary with key being the tag, value being the attribute(s) (if no comma, single attribute, otherwise multiples)
	 */
	@NotNull
	Map<String, Set<String>> getAllowedAttributesByTag();
}
