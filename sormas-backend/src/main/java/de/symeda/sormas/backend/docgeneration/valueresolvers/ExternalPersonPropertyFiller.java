package de.symeda.sormas.backend.docgeneration.valueresolvers;

import javax.annotation.Nullable;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.personaldata.PersonalDataAddressRequestDto;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;
import de.symeda.sormas.api.uuid.HasUuid;

/**
 * The call to the external person system might be expensive, allows caching and a single entry point to get those values.
 * Will use the {@link RootEntityType#name()} as trigger to be initialized / reused.
 */
public class ExternalPersonPropertyFiller extends AbstractRootValuePropertyFiller {

	/**
	 * Lazy-loaded
	 */
	@Nullable
	private LocationDto address;

	private final DocumentTemplateEntities entities;

	private final PersonalDataProviderFacade personalDataProviderFacade;

	public ExternalPersonPropertyFiller(
		EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver,
		DocumentTemplateEntities entities,
		PersonalDataProviderFacade personalDataProviderFacade) {
		super(referenceDtoResolver);
		this.entities = entities;
		this.personalDataProviderFacade = personalDataProviderFacade;
	}

	private LocationDto getAddress() {
		if (address != null) {
			return address;
		}

		PersonalDataAddressRequestDto request = new PersonalDataAddressRequestDto();
		request.setCaseData((CaseDataDto) entities.getEntity(RootEntityType.ROOT_CASE));
		request.setPerson((PersonDto) entities.getEntity(RootEntityType.ROOT_PERSON));
		address = personalDataProviderFacade.findAppropriateDocumentAddressFor(request);

		return address;
	}

	@Override
	protected HasUuid getEntity() {
		return getAddress();
	}

}
