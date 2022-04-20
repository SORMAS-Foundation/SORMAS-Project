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

package de.symeda.sormas.backend.sormastosormas.data.received;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;

/**
 * @author Alex Vidrean
 * @since 11-Oct-21
 */
@RunWith(MockitoJUnitRunner.class)
public class ReceivedEntitiesProcessorTest extends SormasToSormasTest {

	@Test
	public void testIgnoredPropertiesAreNotOverwrittenWithNewValuesForCase() throws CloneNotSupportedException {
		PersonDto personDto = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto existingCaseDto = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(personDto.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setAdditionalDetails("oldAdditionalDetails");
			dto.setExternalID("oldExternalId");
			dto.setExternalToken("oldExternalToken");
			dto.setInternalToken("oldInternalToken");
		});

		CaseDataDto receivedCaseDto = (CaseDataDto) existingCaseDto.clone();
		receivedCaseDto.setAdditionalDetails("newAdditionalDetails");
		receivedCaseDto.setExternalID("newExternalId");
		receivedCaseDto.setExternalToken("newExternalToken");
		receivedCaseDto.setInternalToken("newInternalToken");

		getReceivedCaseProcessor().handleIgnoredProperties(receivedCaseDto, existingCaseDto);

		assertThat(receivedCaseDto.getAdditionalDetails(), is("oldAdditionalDetails"));
		assertThat(receivedCaseDto.getExternalID(), is("oldExternalId"));
		assertThat(receivedCaseDto.getExternalToken(), is("oldExternalToken"));
		assertThat(receivedCaseDto.getInternalToken(), is("oldInternalToken"));

	}

	@Test
	public void testIgnoredPropertiesAreOverwrittenWithNewValuesForCase() throws CloneNotSupportedException {

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

		PersonDto personDto = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto existingCaseDto = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(personDto.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setAdditionalDetails("oldAdditionalDetails");
			dto.setExternalID("oldExternalId");
			dto.setExternalToken("oldExternalToken");
			dto.setInternalToken("oldInternalToken");
		});

		CaseDataDto receivedCaseDto = (CaseDataDto) existingCaseDto.clone();
		receivedCaseDto.setAdditionalDetails("newAdditionalDetails");
		receivedCaseDto.setExternalID("newExternalId");
		receivedCaseDto.setExternalToken("newExternalToken");
		receivedCaseDto.setInternalToken("newInternalToken");

		getReceivedCaseProcessor().handleIgnoredProperties(receivedCaseDto, existingCaseDto);

		assertThat(receivedCaseDto.getAdditionalDetails(), is("newAdditionalDetails"));
		assertThat(receivedCaseDto.getExternalID(), is("newExternalId"));
		assertThat(receivedCaseDto.getExternalToken(), is("newExternalToken"));
		assertThat(receivedCaseDto.getInternalToken(), is("newInternalToken"));

	}

	@Test
	public void testIgnoredPropertiesAreNotOverwrittenWithNewValuesForContact() throws CloneNotSupportedException {

		PersonReferenceDto personReferenceDto = creator.createPerson().toReference();
		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		ContactDto existingContactDto = creator.createContact(officerReferenceDto, personReferenceDto);
		existingContactDto.setAdditionalDetails("oldAdditionalDetails");
		existingContactDto.setExternalID("oldExternalId");
		existingContactDto.setExternalToken("oldExternalToken");
		existingContactDto.setInternalToken("oldInternalToken");

		ContactDto receivedContactDto = (ContactDto) existingContactDto.clone();
		receivedContactDto.setAdditionalDetails("newAdditionalDetails");
		receivedContactDto.setExternalID("newExternalId");
		receivedContactDto.setExternalToken("newExternalToken");
		receivedContactDto.setInternalToken("newInternalToken");

		getReceivedContactProcessor().handleIgnoredProperties(receivedContactDto, existingContactDto);

		assertThat(receivedContactDto.getAdditionalDetails(), is("oldAdditionalDetails"));
		assertThat(receivedContactDto.getExternalID(), is("oldExternalId"));
		assertThat(receivedContactDto.getExternalToken(), is("oldExternalToken"));
		assertThat(receivedContactDto.getInternalToken(), is("oldInternalToken"));
	}

	@Test
	public void testIgnoredPropertiesAreOverwrittenWithNewValuesForContact() throws CloneNotSupportedException {

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

		PersonReferenceDto personReferenceDto = creator.createPerson().toReference();
		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		ContactDto existingContactDto = creator.createContact(officerReferenceDto, personReferenceDto);
		existingContactDto.setAdditionalDetails("oldAdditionalDetails");
		existingContactDto.setExternalID("oldExternalId");
		existingContactDto.setExternalToken("oldExternalToken");
		existingContactDto.setInternalToken("oldInternalToken");

		ContactDto receivedContactDto = (ContactDto) existingContactDto.clone();
		receivedContactDto.setAdditionalDetails("newAdditionalDetails");
		receivedContactDto.setExternalID("newExternalId");
		receivedContactDto.setExternalToken("newExternalToken");
		receivedContactDto.setInternalToken("newInternalToken");

		getReceivedContactProcessor().handleIgnoredProperties(receivedContactDto, existingContactDto);

		assertThat(receivedContactDto.getAdditionalDetails(), is("newAdditionalDetails"));
		assertThat(receivedContactDto.getExternalID(), is("newExternalId"));
		assertThat(receivedContactDto.getExternalToken(), is("newExternalToken"));
		assertThat(receivedContactDto.getInternalToken(), is("newInternalToken"));
	}

	@Test
	public void testIgnoredPropertiesAreNotOverwrittenWithNewValuesForEvent() throws CloneNotSupportedException {

		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		EventDto existingEventDto = creator.createEvent(officerReferenceDto);
		existingEventDto.setDisease(Disease.CORONAVIRUS);
		existingEventDto.setExternalId("oldExternalId");
		existingEventDto.setExternalToken("oldExternalToken");
		existingEventDto.setInternalToken("oldInternalToken");

		EventDto receivedEventDto = (EventDto) existingEventDto.clone();
		receivedEventDto.setExternalId("newExternalId");
		receivedEventDto.setExternalToken("newExternalToken");
		receivedEventDto.setInternalToken("newInternalToken");

		getReceivedEventProcessor().handleIgnoredProperties(receivedEventDto, existingEventDto);

		assertThat(receivedEventDto.getExternalId(), is("oldExternalId"));
		assertThat(receivedEventDto.getExternalToken(), is("oldExternalToken"));
		assertThat(receivedEventDto.getInternalToken(), is("oldInternalToken"));
	}

	@Test
	public void testIgnoredPropertiesAreOverwrittenWithNewValuesForEvent() throws CloneNotSupportedException {

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		EventDto existingEventDto = creator.createEvent(officerReferenceDto);
		existingEventDto.setDisease(Disease.CORONAVIRUS);
		existingEventDto.setExternalId("oldExternalId");
		existingEventDto.setExternalToken("oldExternalToken");
		existingEventDto.setInternalToken("oldInternalToken");

		EventDto receivedEventDto = (EventDto) existingEventDto.clone();
		receivedEventDto.setExternalId("newExternalId");
		receivedEventDto.setExternalToken("newExternalToken");
		receivedEventDto.setInternalToken("newInternalToken");

		getReceivedEventProcessor().handleIgnoredProperties(receivedEventDto, existingEventDto);

		assertThat(receivedEventDto.getExternalId(), is("newExternalId"));
		assertThat(receivedEventDto.getExternalToken(), is("newExternalToken"));
		assertThat(receivedEventDto.getInternalToken(), is("newInternalToken"));
	}

	@Test
	public void testIgnoredPropertiesAreNotOverwrittenWithNewValuesForPerson() throws CloneNotSupportedException {

		PersonDto existingPersonDto = creator.createPerson();
		existingPersonDto.setAdditionalDetails("oldAdditionalDetails");
		existingPersonDto.setExternalId("oldExternalId");
		existingPersonDto.setExternalToken("oldExternalToken");
		existingPersonDto.setInternalToken("oldInternalToken");

		PersonDto receivedPersonDto = (PersonDto) existingPersonDto.clone();
		existingPersonDto.setAdditionalDetails("newAdditionalDetails");
		existingPersonDto.setExternalId("newExternalId");
		existingPersonDto.setExternalToken("newExternalToken");
		existingPersonDto.setInternalToken("newInternalToken");

		// persons are handled e.g., in case processing
		getReceivedCaseProcessor().handleIgnoredProperties(receivedPersonDto, existingPersonDto);

		assertThat(receivedPersonDto.getAdditionalDetails(), is("newAdditionalDetails"));
		assertThat(receivedPersonDto.getExternalId(), is("newExternalId"));
		assertThat(receivedPersonDto.getExternalToken(), is("newExternalToken"));
		assertThat(receivedPersonDto.getInternalToken(), is("newInternalToken"));
	}

	@Test
	public void testIgnoredPropertiesAreOverwrittenWithNewValuesForPerson() throws CloneNotSupportedException {

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

		PersonDto existingPersonDto = creator.createPerson();
		existingPersonDto.setAdditionalDetails("oldAdditionalDetails");
		existingPersonDto.setExternalId("oldExternalId");
		existingPersonDto.setExternalToken("oldExternalToken");
		existingPersonDto.setInternalToken("oldInternalToken");

		PersonDto receivedPersonDto = (PersonDto) existingPersonDto.clone();
		receivedPersonDto.setAdditionalDetails("newAdditionalDetails");
		receivedPersonDto.setExternalId("newExternalId");
		receivedPersonDto.setExternalToken("newExternalToken");
		receivedPersonDto.setInternalToken("newInternalToken");

		// persons are handled e.g., in case processing
		getReceivedCaseProcessor().handleIgnoredProperties(receivedPersonDto, existingPersonDto);

		assertThat(receivedPersonDto.getAdditionalDetails(), is("newAdditionalDetails"));
		assertThat(receivedPersonDto.getExternalId(), is("newExternalId"));
		assertThat(receivedPersonDto.getExternalToken(), is("newExternalToken"));
		assertThat(receivedPersonDto.getInternalToken(), is("newInternalToken"));
	}

	@Test
	public void testIgnoredPropertiesAreNotOverwrittenWithNewValuesForImmunization() throws CloneNotSupportedException {

		PersonReferenceDto personReferenceDto = creator.createPerson().toReference();
		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		ImmunizationDto existingImmunizationDto = creator.createImmunization(
			Disease.CORONAVIRUS,
			personReferenceDto,
			officerReferenceDto,
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 10),
			DateHelper.subtractDays(new Date(), 5),
			DateHelper.subtractDays(new Date(), 1),
			null);
		existingImmunizationDto.setAdditionalDetails("oldAdditionalDetails");
		existingImmunizationDto.setExternalId("oldExternalId");

		ImmunizationDto receivedImmunizationDto = (ImmunizationDto) existingImmunizationDto.clone();
		receivedImmunizationDto.setAdditionalDetails("newAdditionalDetails");
		receivedImmunizationDto.setExternalId("newExternalId");

		getReceivedImmunizationProcessor().handleIgnoredProperties(receivedImmunizationDto, existingImmunizationDto);

		assertThat(receivedImmunizationDto.getAdditionalDetails(), is("oldAdditionalDetails"));
		assertThat(receivedImmunizationDto.getExternalId(), is("oldExternalId"));

	}

	@Test
	public void testIgnoredPropertiesAreOverwrittenWithNewValuesForImmunization() throws CloneNotSupportedException {

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

		PersonReferenceDto personReferenceDto = creator.createPerson().toReference();
		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		ImmunizationDto existingImmunizationDto = creator.createImmunization(
			Disease.CORONAVIRUS,
			personReferenceDto,
			officerReferenceDto,
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf,
			DateHelper.subtractDays(new Date(), 10),
			DateHelper.subtractDays(new Date(), 5),
			DateHelper.subtractDays(new Date(), 1),
			null);
		existingImmunizationDto.setAdditionalDetails("oldAdditionalDetails");
		existingImmunizationDto.setExternalId("oldExternalId");

		ImmunizationDto receivedImmunizationDto = (ImmunizationDto) existingImmunizationDto.clone();
		receivedImmunizationDto.setAdditionalDetails("newAdditionalDetails");
		receivedImmunizationDto.setExternalId("newExternalId");

		getReceivedImmunizationProcessor().handleIgnoredProperties(receivedImmunizationDto, existingImmunizationDto);

		assertThat(receivedImmunizationDto.getAdditionalDetails(), is("newAdditionalDetails"));
		assertThat(receivedImmunizationDto.getExternalId(), is("newExternalId"));

	}

	@Test
	public void testIgnoredPropertiesAreNotOverwrittenWithNewValuesForPathogenTest() throws CloneNotSupportedException {

		PersonDto personDto = creator.createPerson();
		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caseDataDto = creator.createCase(officerReferenceDto, rdcf, dto -> {
			dto.setPerson(personDto.toReference());
			dto.setSurveillanceOfficer(officerReferenceDto);
			dto.setClassificationUser(officerReferenceDto);
		});

		Region region = creator.createRegion("region");
		District district = creator.createDistrict("district", region);
		Facility laboratoryFacility = creator.createFacility("lab", region, district, null);

		SampleDto sample = creator.createSample(caseDataDto.toReference(), officerReferenceDto, laboratoryFacility);

		PathogenTestDto existingPathogenTestDto = creator.createPathogenTest(sample.toReference(), caseDataDto);
		existingPathogenTestDto.setExternalId("oldExternalId");

		PathogenTestDto receivedPathogenTestDto = (PathogenTestDto) existingPathogenTestDto.clone();
		receivedPathogenTestDto.setExternalId("newExternalId");

		// pathogen tests are handled through samples
		getReceivedSampleProcessor().handleIgnoredProperties(receivedPathogenTestDto, existingPathogenTestDto);

		assertThat(receivedPathogenTestDto.getExternalId(), is("oldExternalId"));

	}

	@Test
	public void testIgnoredPropertiesAreOverwrittenWithNewValuesForPathogenTest() throws CloneNotSupportedException {

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN, Boolean.FALSE.toString());
		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN, Boolean.FALSE.toString());

		PersonDto personDto = creator.createPerson();
		UserReferenceDto officerReferenceDto =
			creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caseDataDto = creator.createCase(officerReferenceDto, rdcf, dto -> {
			dto.setPerson(personDto.toReference());
			dto.setSurveillanceOfficer(officerReferenceDto);
			dto.setClassificationUser(officerReferenceDto);
		});

		Region region = creator.createRegion("region");
		District district = creator.createDistrict("district", region);
		Facility laboratoryFacility = creator.createFacility("lab", region, district, null);

		SampleDto sample = creator.createSample(caseDataDto.toReference(), officerReferenceDto, laboratoryFacility);

		PathogenTestDto existingPathogenTestDto = creator.createPathogenTest(sample.toReference(), caseDataDto);
		existingPathogenTestDto.setExternalId("oldExternalId");

		PathogenTestDto receivedPathogenTestDto = (PathogenTestDto) existingPathogenTestDto.clone();
		receivedPathogenTestDto.setExternalId("newExternalId");

		// pathogen tests are handled through samples
		getReceivedSampleProcessor().handleIgnoredProperties(receivedPathogenTestDto, existingPathogenTestDto);

		assertThat(receivedPathogenTestDto.getExternalId(), is("newExternalId"));

	}
}
