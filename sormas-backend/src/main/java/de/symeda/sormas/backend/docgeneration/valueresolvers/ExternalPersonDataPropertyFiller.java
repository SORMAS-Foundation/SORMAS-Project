package de.symeda.sormas.backend.docgeneration.valueresolvers;

import javax.annotation.Nullable;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataAddressRequestDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
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

	private boolean fetched;

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
		if (fetched) {
			return address;
		}

		logger.debug("External address is going to be fetched");

		ExternalPersonDataAddressRequestDto request = new ExternalPersonDataAddressRequestDto();
		request.setCaseData((CaseDataDto) entities.getEntity(RootEntityType.ROOT_CASE));
		request.setContact((ContactReferenceDto) entities.getEntity(RootEntityType.ROOT_CONTACT));
		request.setPersonReference((PersonReferenceDto) entities.getEntity(RootEntityType.ROOT_PERSON));
		request.setEventParticipant((EventParticipantDto) entities.getEntity(RootEntityType.ROOT_EVENT_PARTICIPANT));

		logger.debug("Request.case: [{}]", request.getCaseData());

		address = externalPersonDataProviderFacade.findAppropriateDocumentAddressFor(request);
		fetched = true;

		logger.debug("Retrieved address: [{}]", address);

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
