/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.databuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class CaseShareDataBuilder
//implements ShareDataBuilder<Case, CaseShareData> 
{

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactService contactService;
	@EJB
	private SampleService sampleService;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	public CaseShareData buildShareData(Case caze, User user, SormasToSormasOptionsDto options) throws SormasToSormasException {
		Pseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(options);

		PersonDto personDto = dataBuilderHelper.getPersonDto(caze.getPerson(), pseudonymizer, options);
		CaseDataDto cazeDto = getCazeDto(caze, pseudonymizer);

		SormasToSormasOriginInfoDto originInfo = dataBuilderHelper.createSormasToSormasOriginInfo(user, options);

		SormasToSormasCaseDto shareData = new SormasToSormasCaseDto(personDto, cazeDto, originInfo);
		List<Contact> associatedContacts = Collections.emptyList();
		if (options.isWithAssociatedContacts()) {
			associatedContacts = contactService.findBy(new ContactCriteria().caze(caze.toReference()), user);
			shareData.setAssociatedContacts(getAssociatedContactDtos(associatedContacts, pseudonymizer, options));
		}

		final List<Sample> samples = new ArrayList<>();
		if (options.isWithSamples()) {
			final List<Sample> caseSamples = sampleService.findBy(new SampleCriteria().caze(caze.toReference()), user);
			samples.addAll(caseSamples);

			associatedContacts.forEach(associatedContact -> {
				List<Sample> contactSamples = sampleService.findBy(new SampleCriteria().contact(associatedContact.toReference()), user)
					.stream()
					.filter(contactSample -> caseSamples.stream().noneMatch(caseSample -> DataHelper.isSame(caseSample, contactSample)))
					.collect(Collectors.toList());

				samples.addAll(contactSamples);
			});
		}

		shareData.setSamples(dataBuilderHelper.getSampleDtos(samples, pseudonymizer));

		return new CaseShareData(shareData, associatedContacts, samples);
	}

	private CaseDataDto getCazeDto(Case caze, Pseudonymizer pseudonymizer) {
		CaseDataDto cazeDto = caseFacade.convertToDto(caze, pseudonymizer);

		cazeDto.setReportingUser(null);
		cazeDto.setClassificationUser(null);
		cazeDto.setSurveillanceOfficer(null);
		cazeDto.setCaseOfficer(null);
		cazeDto.setSormasToSormasOriginInfo(null);

		return cazeDto;
	}

	private List<SormasToSormasCaseDto.AssociatedContactDto> getAssociatedContactDtos(
		List<Contact> associatedContacts,
		Pseudonymizer pseudonymizer,
		SormasToSormasOptionsDto options) {
		return associatedContacts.stream().map(contact -> {
			PersonDto personDto = dataBuilderHelper.getPersonDto(contact.getPerson(), pseudonymizer, options);
			ContactDto contactDto = dataBuilderHelper.getContactDto(contact, pseudonymizer);

			return new SormasToSormasCaseDto.AssociatedContactDto(personDto, contactDto);
		}).collect(Collectors.toList());
	}
}
