package de.symeda.sormas.backend.docgeneration.valueresolvers;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.backend.externalpersondata.ExternalPersonDataProviderFacadeEJB;

/**
 * {@link RootEntityPropertyFiller} must not be used directly but through this factory.
 * One instance of a {@link RootEntityPropertyFiller} must be used for each document, which allows caching.
 */
@ApplicationScoped
public class RootEntityPropertyFillersFactory {

	@EJB
	private ExternalPersonDataProviderFacadeEJB.ExternalPersonDataProviderFacadeEJBLocal externalPersonDataProviderFacadeEJB;

	public RootEntityPropertyFiller createPropertyFiller(
		@NotNull RootEntityType entityType,
		@NotNull DocumentTemplateEntities entities,
		@NotNull EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver) {
		if (entityType.isDataModelEntity()) {
			throw new IllegalStateException(String.format("Only 'non-data-model' RootEntityType are supported, was: [%s]", entityType));
		}

		if (entityType == RootEntityType.ROOT_EXTERNAL_PERSON_DATA) {
			return new ExternalPersonPropertyFiller(referenceDtoResolver, entities, externalPersonDataProviderFacadeEJB);
		}
		throw new IllegalStateException(String.format("RootEntityType: [%s] is not supported", entityType));
	}
}
