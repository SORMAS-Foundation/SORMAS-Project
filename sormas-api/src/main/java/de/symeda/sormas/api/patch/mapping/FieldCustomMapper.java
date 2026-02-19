package de.symeda.sormas.api.patch.mapping;

import java.util.Optional;

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
	Optional<DataPatchFailure> map(String fieldName, Object value);

}
