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
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class ContactShareDataBuilder
	extends ShareDataBuilder<ContactDto, Contact, SormasToSormasContactDto, SormasToSormasContactPreview, SormasToSormasContactDtoValidator> {

	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	@Inject
	public ContactShareDataBuilder(SormasToSormasContactDtoValidator validator) {
		super(validator);
	}

	public ContactShareDataBuilder() {
	}

	@Override
	protected SormasToSormasContactDto doBuildShareData(Contact contact, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		PersonDto personDto = dataBuilderHelper
			.getPersonDto(contact.getPerson(), pseudonymizer, requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());
		ContactDto contactDto = dataBuilderHelper.getContactDto(contact, pseudonymizer);

		return new SormasToSormasContactDto(personDto, contactDto);
	}

	@Override
	public SormasToSormasContactPreview doBuildShareDataPreview(Contact contact, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		return dataBuilderHelper.getContactPreview(contact, pseudonymizer);

	}
}
