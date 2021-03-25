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

package de.symeda.sormas.backend.sormastosormas;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class ShareDataBuilderHelper {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private ServerAccessDataService serverAccessDataService;
	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal additionalTestFacade;

	public Pseudonymizer createPseudonymizer(SormasToSormasOptionsDto options) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultNoCheckers(false);

		if (options.isPseudonymizePersonalData()) {
			pseudonymizer.addFieldAccessChecker(PersonalDataFieldAccessChecker.forcedNoAccess(), PersonalDataFieldAccessChecker.forcedNoAccess());
		}
		if (options.isPseudonymizeSensitiveData()) {
			pseudonymizer.addFieldAccessChecker(SensitiveDataFieldAccessChecker.forcedNoAccess(), SensitiveDataFieldAccessChecker.forcedNoAccess());
		}

		return pseudonymizer;
	}

	public PersonDto getPersonDto(Person person, Pseudonymizer pseudonymizer, SormasToSormasOptionsDto options) {
		PersonDto personDto = personFacade.convertToDto(person, pseudonymizer, true);

		pseudonymiePerson(options, personDto);

		return personDto;
	}

	public void pseudonymiePerson(SormasToSormasOptionsDto options, PersonDto personDto) {
		if (options.isPseudonymizePersonalData() || options.isPseudonymizeSensitiveData()) {
			personDto.setFirstName(I18nProperties.getCaption(Captions.inaccessibleValue));
			personDto.setLastName(I18nProperties.getCaption(Captions.inaccessibleValue));
		}
	}

	public ContactDto getContactDto(Contact contact, Pseudonymizer pseudonymizer) {
		ContactDto contactDto = contactFacade.convertToDto(contact, pseudonymizer);

		contactDto.setReportingUser(null);
		contactDto.setContactOfficer(null);
		contactDto.setResultingCaseUser(null);
		contactDto.setSormasToSormasOriginInfo(null);

		return contactDto;
	}

	public SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(User user, SormasToSormasOptionsDto options) throws SormasToSormasException {
		OrganizationServerAccessData serverAccessData = getServerAccessData();

		SormasToSormasOriginInfoDto sormasToSormasOriginInfo = new SormasToSormasOriginInfoDto();
		sormasToSormasOriginInfo.setOrganizationId(serverAccessData.getId());
		sormasToSormasOriginInfo.setSenderName(String.format("%s %s", user.getFirstName(), user.getLastName()));
		sormasToSormasOriginInfo.setSenderEmail(user.getUserEmail());
		sormasToSormasOriginInfo.setSenderPhoneNumber(user.getPhone());
		sormasToSormasOriginInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		sormasToSormasOriginInfo.setComment(options.getComment());

		return sormasToSormasOriginInfo;
	}

	public List<SormasToSormasSampleDto> getSampleDtos(List<Sample> samples, Pseudonymizer pseudonymizer) {
		return samples.stream().map(s -> {
			SampleDto sampleDto = sampleFacade.convertToDto(s, pseudonymizer);
			sampleDto.setSormasToSormasOriginInfo(null);

			return new SormasToSormasSampleDto(
				sampleDto,
				s.getPathogenTests().stream().map(t -> pathogenTestFacade.convertToDto(t, pseudonymizer)).collect(Collectors.toList()),
				s.getAdditionalTests().stream().map(t -> additionalTestFacade.convertToDto(t, pseudonymizer)).collect(Collectors.toList()));
		}).collect(Collectors.toList());
	}

	private OrganizationServerAccessData getServerAccessData() throws SormasToSormasException {
		return serverAccessDataService.getServerAccessData()
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasCertNotGenerated)));
	}
}
