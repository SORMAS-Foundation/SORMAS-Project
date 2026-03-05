package de.symeda.sormas.api.patch.mapping;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.patch.SinglePatchResult;

/**
 * Some complex - fields objects cannot be mapped using the simple 1 key <-> 1 value approach as some fields must be grouped together to
 * make sense.
 */
public interface GroupedFieldsMapper {

	/**
	 * Will try to
	 * 
	 * @param request
	 *            to determine what may or may not be aggregated.
	 * @return result dictionary: key path and value the result: patched value or failure.
	 */
	@NotNull
	List<SinglePatchResult> aggregatedPatch(@NotNull GroupedFieldsRequest request);

	/**
	 * Specifies the prefixes that are supported by this grouped mapper.
	 * 
	 * @return supported prefixes.
	 */
	@NotNull
	@NotEmpty
	Set<String> aggregatedPrefixes();
}
