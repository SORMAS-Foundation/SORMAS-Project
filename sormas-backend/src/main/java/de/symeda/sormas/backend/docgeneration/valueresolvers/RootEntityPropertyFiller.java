package de.symeda.sormas.backend.docgeneration.valueresolvers;

import java.util.Properties;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.docgeneneration.RootEntityType;

/**
 * Allows for some "indirect" (not directly within SORMAS - entity model) entities to resolve properties for document generation.
 * Naming inspired from {@link de.symeda.sormas.api.EntityDtoAccessHelper.IReferenceDtoResolver}.
 */
public interface RootEntityPropertyFiller {

	/**
	 * For the given propertyKey will set the value into the properties
	 * 
	 * @param propertyKey
	 *            key for which a value should be computed / extracted.
	 * @param properties
	 *            will be mutated with property
	 */
	void fillValueInto(@NotNull String propertyKey, @NotNull Properties properties);

	RootEntityType supportedType();
}
