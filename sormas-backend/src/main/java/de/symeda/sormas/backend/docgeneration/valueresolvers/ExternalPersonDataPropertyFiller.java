package de.symeda.sormas.backend.docgeneration.valueresolvers;

import javax.annotation.Nullable;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataAddressRequestDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.uuid.HasUuid;

/**
 * The call to the external person system might be expensive, allows caching and a single entry point to get those values.
 * Will use the {@link RootEntityType#name()} as trigger to be initialized / reused.
 */
public class ExternalPersonDataPropertyFiller extends AbstractRootValuePropertyFiller {

	/**
	 * Lazy-loaded
	 */
	@Nullable
	private LocationDto address;

	private final DocumentTemplateEntities entities;

	private final ExternalPersonDataProviderFacade externalPersonDataProviderFacade;

	public ExternalPersonDataPropertyFiller(
		EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver,
		DocumentTemplateEntities entities,
		ExternalPersonDataProviderFacade externalPersonDataProviderFacade) {
		super(referenceDtoResolver);
		this.entities = entities;
		this.externalPersonDataProviderFacade = externalPersonDataProviderFacade;
	}

	private LocationDto getAddress() {
		if (address != null) {
			return address;
		}

		ExternalPersonDataAddressRequestDto request = new ExternalPersonDataAddressRequestDto();
		request.setCaseData((CaseDataDto) entities.getEntity(RootEntityType.ROOT_CASE));
		request.setPerson((PersonDto) entities.getEntity(RootEntityType.ROOT_PERSON));
		address = externalPersonDataProviderFacade.findAppropriateDocumentAddressFor(request);

		return address;
	}

	@Override
	protected HasUuid getEntity() {
		return getAddress();
	}

	@Override
	public RootEntityType supportedType() {
		return RootEntityType.ROOT_EXTERNAL_PERSON_DATA;
	}
}
