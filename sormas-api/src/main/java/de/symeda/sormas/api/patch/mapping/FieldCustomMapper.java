package de.symeda.sormas.api.patch.mapping;

import java.util.Optional;
import java.util.Set;

import de.symeda.sormas.api.patch.DataPatchFailure;

/**
 * The patch logic was designed to be generic, nevertheless some SORMAS - fields require specific adaptions.
 */
public interface FieldCustomMapper {

	/*
	 * Implement for:
	 * - Phone number - email
	 * - BirthDate
	 */
	// TODO: missing 'current value' logic: if already exist do nothing.
	Optional<DataPatchFailure> map(FieldPatchRequest request);

	/**
	 * Warn each field must be unique among all {@link FieldCustomMapper} implementations.
	 * 
	 * @return fields supported by this specific mapper.
	 */
	Set<String> supportedFields();

}
