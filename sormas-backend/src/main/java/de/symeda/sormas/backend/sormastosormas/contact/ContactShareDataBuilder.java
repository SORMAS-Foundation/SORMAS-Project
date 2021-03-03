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

package de.symeda.sormas.backend.sormastosormas.contact;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.AssociatedEntityWrapper;
import de.symeda.sormas.backend.sormastosormas.ShareData;
import de.symeda.sormas.backend.sormastosormas.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.ShareDataBuilderHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class ContactShareDataBuilder implements ShareDataBuilder<Contact, SormasToSormasContactDto> {

	@EJB
	private SampleService sampleService;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	public ShareData<SormasToSormasContactDto> buildShareData(Contact contact, User user, SormasToSormasOptionsDto options)
		throws SormasToSormasException {
		Pseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(options);

		PersonDto personDto = dataBuilderHelper.getPersonDto(contact.getPerson(), pseudonymizer, options);
		ContactDto contactDto = dataBuilderHelper.getContactDto(contact, pseudonymizer);

		SormasToSormasContactDto contactData =
			new SormasToSormasContactDto(personDto, contactDto, dataBuilderHelper.createSormasToSormasOriginInfo(user, options));
		ShareData<SormasToSormasContactDto> shareData = new ShareData<>(contactData);

		if (options.isWithSamples()) {
			List<Sample> samples = sampleService.findBy(new SampleCriteria().contact(contact.toReference()), user);

			contactData.setSamples(dataBuilderHelper.getSampleDtos(samples, pseudonymizer));
			shareData.addAssociatedEntities(AssociatedEntityWrapper.forSamples(samples));
		}

		return shareData;
	}
}
