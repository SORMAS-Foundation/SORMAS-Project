package de.symeda.sormas.backend.patch.partial_retrieval;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.utils.OrderedRegisterable;

/**
 * Allows to specify how a specific value for a type must be "stringified" for display purposes.
 */
public interface TypeToDisplayValueMapper extends OrderedRegisterable<TypeToDisplayValueMapper> {

	/**
	 * Will be used to be displayed as it to an user.
	 * 
	 * @param value
	 *            untyped value that is supported by this mapper.
	 * @return va
	 */
	String toDisplayValue(@NotNull Object value);

}
