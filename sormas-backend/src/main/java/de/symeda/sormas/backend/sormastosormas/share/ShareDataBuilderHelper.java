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

package de.symeda.sormas.backend.sormastosormas.share;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasPersonPreview;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
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
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;
	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal additionalTestFacade;

	public Pseudonymizer createPseudonymizer(boolean pseudonymizePersonalData, boolean pseudonymizeSensitiveData) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultNoCheckers(false);

		if (pseudonymizePersonalData) {
			pseudonymizer.addFieldAccessChecker(PersonalDataFieldAccessChecker.forcedNoAccess(), PersonalDataFieldAccessChecker.forcedNoAccess());
		}
		if (pseudonymizeSensitiveData) {
			pseudonymizer.addFieldAccessChecker(SensitiveDataFieldAccessChecker.forcedNoAccess(), SensitiveDataFieldAccessChecker.forcedNoAccess());
		}

		return pseudonymizer;
	}

	public PersonDto getPersonDto(Person person, Pseudonymizer pseudonymizer, boolean pseudonymizedPersonalData, boolean pseudonymizedSensitiveData) {
		PersonDto personDto = personFacade.convertToDto(person, pseudonymizer, true);

		pseudonymiePerson(personDto, pseudonymizedPersonalData, pseudonymizedSensitiveData);

		resetIgnoredProperties(personDto, personDto.getClass());

		return personDto;
	}

	public void pseudonymiePerson(PersonDto personDto, boolean pseudonymizedPersonalData, boolean pseudonymizedSensitiveData) {
		if (pseudonymizedPersonalData || pseudonymizedSensitiveData) {
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

		resetIgnoredProperties(contactDto, ContactDto.class);

		return contactDto;
	}

	public SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(User user, SormasToSormasShareInfo shareInfo) {
		return createSormasToSormasOriginInfo(user, createOptionsFormShareInfo(shareInfo));
	}

	public SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(User user, SormasToSormasOptionsDto options) {
		SormasToSormasOriginInfoDto sormasToSormasOriginInfo = new SormasToSormasOriginInfoDto();
		sormasToSormasOriginInfo.setOrganizationId(configFacadeEjb.getS2SConfig().getId());
		sormasToSormasOriginInfo.setSenderName(String.format("%s %s", user.getFirstName(), user.getLastName()));
		sormasToSormasOriginInfo.setSenderEmail(user.getUserEmail());
		sormasToSormasOriginInfo.setSenderPhoneNumber(user.getPhone());
		sormasToSormasOriginInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		sormasToSormasOriginInfo.setWithAssociatedContacts(options.isWithAssociatedContacts());
		sormasToSormasOriginInfo.setWithSamples(options.isWithSamples());
		sormasToSormasOriginInfo.setWithEventParticipants(options.isWithEventParticipants());
		sormasToSormasOriginInfo.setComment(options.getComment());

		return sormasToSormasOriginInfo;
	}

	public List<SormasToSormasSampleDto> getSampleDtos(List<Sample> samples, Pseudonymizer pseudonymizer) {
		return samples.stream().map(s -> {
			SampleDto sampleDto = sampleFacade.convertToDto(s, pseudonymizer);
			sampleDto.setSormasToSormasOriginInfo(null);
			resetIgnoredProperties(sampleDto, SampleDto.class);

			return new SormasToSormasSampleDto(
				sampleDto,
				s.getPathogenTests().stream().map(t -> pathogenTestFacade.convertToDto(t, pseudonymizer)).collect(Collectors.toList()),
				s.getAdditionalTests().stream().map(t -> additionalTestFacade.convertToDto(t, pseudonymizer)).collect(Collectors.toList()));
		}).collect(Collectors.toList());
	}

	public SormasToSormasPersonPreview getPersonPreview(Person person) {
		SormasToSormasPersonPreview personPreview = new SormasToSormasPersonPreview();

		personPreview.setFirstName(person.getFirstName());
		personPreview.setLastName(person.getLastName());
		personPreview.setBirthdateDD(person.getBirthdateDD());
		personPreview.setBirthdateMM(person.getBirthdateMM());
		personPreview.setBirthdateYYYY(person.getBirthdateYYYY());
		personPreview.setSex(person.getSex());
		personPreview.setAddress(LocationFacadeEjb.toDto(person.getAddress()));

		return personPreview;
	}

	public SormasToSormasContactPreview getContactPreview(Contact contact, Pseudonymizer pseudonymizer) {
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

		contactPreview.setPerson(getPersonPreview(contact.getPerson()));

		contactPreview.setCaze(CaseFacadeEjb.toReferenceDto(contact.getCaze()));

		pseudonymizer.pseudonymizeDto(SormasToSormasContactPreview.class, contactPreview, false, null);

		return contactPreview;
	}

	public SormasToSormasOptionsDto createOptionsFormShareInfo(SormasToSormasShareInfo shareInfo) {
		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();

		options.setOrganization(new SormasServerDescriptor(shareInfo.getOrganizationId()));
		options.setHandOverOwnership(shareInfo.isOwnershipHandedOver());
		options.setWithAssociatedContacts(shareInfo.isWithAssociatedContacts());
		options.setWithSamples(shareInfo.isWithSamples());
		options.setWithEventParticipants(shareInfo.isWithEventParticipants());
		options.setComment(shareInfo.getComment());
		options.setPseudonymizePersonalData(shareInfo.isPseudonymizedPersonalData());
		options.setPseudonymizeSensitiveData(shareInfo.isPseudonymizedSensitiveData());

		return options;

	}

	public SormasToSormasOptionsDto createOptionsFromOriginInfoDto(SormasToSormasOriginInfo originInfo) {
		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();

		options.setOrganization(new SormasServerDescriptor(originInfo.getOrganizationId()));
		options.setHandOverOwnership(originInfo.isOwnershipHandedOver());
		options.setWithAssociatedContacts(originInfo.isWithAssociatedContacts());
		options.setWithSamples(originInfo.isWithSamples());
		options.setWithEventParticipants(originInfo.isWithEventParticipants());
		options.setComment(originInfo.getComment());

		return options;

	}

	public void resetIgnoredProperties(Object dto, Class<?> dtoType) {
		for (Field field : dtoType.getDeclaredFields()) {
			if (field.isAnnotationPresent(S2SIgnoreProperty.class)) {
				//TODO: check if configuration is deactivated
				try {
					field.set(dto, null);
				} catch (IllegalAccessException e) {
					//TODO: add logger
				}
			}
		}
	}
}
