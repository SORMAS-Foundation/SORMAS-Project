package de.symeda.sormas.api.patch.mapping;

import java.util.Optional;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.patch.DataPatchFailure;

/**
 * Allows to patch a single SORMAS fields in a specific manner, because default type mapping doesn't fit.
 * Example: a field is displayed a single field in UI but is stored as multiple values in DTO / entities.
 */
public interface FieldCustomMapper {

	/*
	 * In case of failure returns the triggered failure otherwise successfully patch the value on the specific object.
	 */
	Optional<DataPatchFailure> map(FieldPatchRequest request);

	/**
	 * Warn each field must be unique among all {@link FieldCustomMapper} implementations.
	 * 
	 * @return fields supported by this specific mapper.
	 */
	Set<String> supportedFields();

	/**
	 * Some fields are specific to some diseases.
	 * 
	 * @return set of supported diseases.
	 */
	default Set<Disease> supportedDisease() {
		return Disease.ALL_DISEASES;
	}

}
