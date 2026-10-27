package de.symeda.sormas.backend.docgeneration;

import java.util.Properties;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;

/**
 * Allows for some "indirect" (not directly within SORMAS - entity model) entities to resolve the values.
 * For example to cache some resolving / computation across each property iteration.
 * Naming inspired from {@link de.symeda.sormas.api.EntityDtoAccessHelper.IReferenceDtoResolver}.
 */
public interface RootEntityValueResolver {

	RootEntityValueResolver build(DocumentTemplateEntities entities);

	void fillValues(Properties properties, String propertyKey);
}
