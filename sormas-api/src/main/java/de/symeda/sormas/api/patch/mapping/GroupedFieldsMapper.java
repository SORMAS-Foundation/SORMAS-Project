package de.symeda.sormas.api.patch.mapping;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;

/**
 * Some complex - fields objects cannot be mapped using the simple 1 key <-> 1 value approach as some fields must be grouped together to
 * make sense.
 * <p>
 * This also allows creating entities that are not accessible from the Mapping context Root (as of 2026-03-05:
 * {@link de.symeda.sormas.api.caze.CaseDataDto})
 */
public interface GroupedFieldsMapper<T extends EntityDto> {

	/**
	 * Attempts to create a response for the mapping.
	 * 
	 * @param request
	 *            to determine what may or may not be aggregated.
	 * @return result dictionary: key path and value the result: patched value or failure.
	 */
	@NotNull
	GroupedFieldsResponse<T> aggregatedPatch(@NotNull GroupedFieldsRequest request);

	/**
	 * Specifies the prefixes that are supported by this mapper instance.
	 * 
	 * @return supported prefixes.
	 */
	@NotNull
	@NotEmpty
	Set<String> aggregatedPrefixes();
}
