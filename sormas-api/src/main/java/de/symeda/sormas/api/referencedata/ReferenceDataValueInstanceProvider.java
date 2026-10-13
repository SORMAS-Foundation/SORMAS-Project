package de.symeda.sormas.api.referencedata;

import java.util.List;
import java.util.Optional;

import javax.ejb.Remote;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.ReferenceDto;

/**
 * This provider can be used to help find appropriate Reference types when only the type is known.
 */
@Remote
public interface ReferenceDataValueInstanceProvider {

	/**
	 * 
	 * @param referenceType
	 *            class to fetch.
	 * @return
	 * @param <T>
	 */
	<T extends ReferenceDto> List<T> getAll(@NotNull Class<T> referenceType);

	/**
	 * 
	 * @param caption
	 *            used candidate to find the adequate value.
	 * @param referenceType
	 *            actual referenceDto class
	 * @return optional reference DTO.
	 * @param <T>
	 *            exact {@link ReferenceDto} type.
	 */
	<T extends ReferenceDto> Optional<T> getOne(@NotNull String caption, @NotNull Class<T> referenceType);
}
