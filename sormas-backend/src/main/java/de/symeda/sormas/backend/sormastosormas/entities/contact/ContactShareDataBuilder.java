/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.sormastosormas.entities.contact;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasContactPreview;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.SormasToSormasPseudonymizer;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;

@Stateless
@LocalBean
public class ContactShareDataBuilder
	extends ShareDataBuilder<ContactDto, Contact, SormasToSormasContactDto, SormasToSormasContactPreview, SormasToSormasContactDtoValidator> {

	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@Inject
	public ContactShareDataBuilder(SormasToSormasContactDtoValidator validator) {
		super(validator);
	}

	public ContactShareDataBuilder() {
	}

	@Override
	protected SormasToSormasContactDto doBuildShareData(Contact contact, ShareRequestInfo requestInfo, boolean ownerShipHandedOver) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);

		PersonDto personDto = dataBuilderHelper.getPersonDto(contact.getPerson(), pseudonymizer, requestInfo);
		ContactDto contactDto = getDto(contact, pseudonymizer);

		return new SormasToSormasContactDto(personDto, contactDto);
	}

	@Override
	protected ContactDto getDto(Contact contact, SormasToSormasPseudonymizer pseudonymizer) {

		ContactDto contactDto = contactFacade.toPseudonymizedDto(contact, pseudonymizer.getPseudonymizer());
		// reporting user is not set to null here as it would not pass the validation
		// the receiver appears to set it to SORMAS2SORMAS Client anyway
		contactDto.setContactOfficer(null);
		contactDto.setResultingCaseUser(null);
		contactDto.setSormasToSormasOriginInfo(null);
		dataBuilderHelper.clearIgnoredProperties(contactDto);

		return contactDto;
	}

	@Override
	public void doBusinessValidation(SormasToSormasContactDto dto) throws ValidationRuntimeException {
		personFacade.validate(dto.getPerson());
		contactFacade.validate(dto.getEntity());
	}

	@Override
	public SormasToSormasContactPreview doBuildShareDataPreview(Contact contact, ShareRequestInfo requestInfo) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);

		return getContactPreview(contact, pseudonymizer);

	}

	public SormasToSormasContactPreview getContactPreview(Contact contact, SormasToSormasPseudonymizer pseudonymizer) {
		SormasToSormasContactPreview contactPreview = new SormasToSormasContactPreview();

		contactPreview.setUuid(contact.getUuid());
		contactPreview.setReportDateTime(contact.getReportDateTime());
		contactPreview.setDisease(contact.getDisease());
		contactPreview.setDiseaseDetails(contact.getDiseaseDetails());
		contactPreview.setLastContactDate(contact.getLastContactDate());
		contactPreview.setContactClassification(contact.getContactClassification());
		contactPreview.setContactCategory(contact.getContactCategory());
		contactPreview.setContactStatus(contact.getContactStatus());

		contactPreview.setRegion(RegionFacadeEjb.toReferenceDto(contact.getRegion()));
		contactPreview.setDistrict(DistrictFacadeEjb.toReferenceDto(contact.getDistrict()));
		contactPreview.setCommunity(CommunityFacadeEjb.toReferenceDto(contact.getCommunity()));

		contactPreview.setPerson(dataBuilderHelper.getPersonPreview(contact.getPerson()));

		contactPreview.setCaze(CaseFacadeEjb.toReferenceDto(contact.getCaze()));

		pseudonymizer.<SormasToSormasContactPreview>getPseudonymizer().pseudonymizeDto(SormasToSormasContactPreview.class, contactPreview, false, null);

		return contactPreview;
	}
}
