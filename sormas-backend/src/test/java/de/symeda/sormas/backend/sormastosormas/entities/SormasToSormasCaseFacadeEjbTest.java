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

package de.symeda.sormas.backend.sormastosormas.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareTree;
import de.symeda.sormas.api.sormastosormas.entities.SyncDataDto;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.entities.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestAcceptData;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.user.User;

public class SormasToSormasCaseFacadeEjbTest extends SormasToSormasTest {

	@Test
	public void testShareCase() throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getCases().size(), is(1));
				SormasToSormasCaseDto sharedCase = postBody.getCases().get(0);

				assertThat(sharedCase.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedCase.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedCase.getEntity().getUuid(), is(caze.getUuid()));
				// users should be cleaned up
				assertThat(sharedCase.getEntity().getReportingUser(), is(officer));
				assertThat(sharedCase.getEntity().getSurveillanceOfficer(), is(nullValue()));
				assertThat(sharedCase.getEntity().getClassificationUser(), is(nullValue()));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().getComment(), is("Test comment"));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithContacts() throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		ContactDto contact = creator.createContact(officer, creator.createPerson().toReference(), caze);

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");
		options.setWithAssociatedContacts(true);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getCases().size(), is(1));
				assertThat(postBody.getContacts().size(), is(1));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo = shareInfoList.get(0);
		assertThat(contactShareInfo.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(contactShareInfo.getSender().getCaption(), is("Surv OFF"));
		assertThat(contactShareInfo.getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithSamples() throws SormasToSormasException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		ContactDto caseSampleContact = creator.createContact(officer, person.toReference());

		Date sampleDateTime = new Date();
		// Sample gets saved with associatedContact
		SampleDto caseSample = creator.createSample(caze.toReference(), officer, rdcf.facility, s -> {
			s.setSampleDateTime(sampleDateTime);
			s.setComment("Test case sample");
			s.setAssociatedContact(caseSampleContact.toReference());
		});

		creator.createPathogenTest(caseSample.toReference(), caze);
		creator.createAdditionalTest(caseSample.toReference());

		ContactDto contact = creator.createContact(officer, creator.createPerson().toReference(), caze);
		SampleDto contactSampleSample = creator.createSample(contact.toReference(), officer, rdcf.facility, s -> {
			s.setSampleDateTime(sampleDateTime);
			s.setComment("Test contact sample");
		});
		creator.createPathogenTest(contactSampleSample.toReference(), caze);
		creator.createAdditionalTest(contactSampleSample.toReference());

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");
		options.setWithSamples(true);
		options.setWithAssociatedContacts(true);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {

				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getContacts().size(), is(1));

				assertThat(postBody.getSamples().size(), is(2));
				SormasToSormasSampleDto sharedCaseSample = postBody.getSamples().get(0);
				assertThat(sharedCaseSample.getEntity().getSampleDateTime().compareTo(sampleDateTime), is(0)); // use compareTo because getSampleDateTime() returns Timestamp object due to Sample.java using TemporalType.Timestamp
				assertThat(sharedCaseSample.getEntity().getComment(), is("Test case sample"));
				assertThat(sharedCaseSample.getPathogenTests(), hasSize(1));
				assertThat(sharedCaseSample.getAdditionalTests(), hasSize(1));

				SormasToSormasSampleDto sharedContactSample = postBody.getSamples().get(1);
				assertThat(sharedContactSample.getEntity().getSampleDateTime().compareTo(sampleDateTime), is(0));
				assertThat(sharedContactSample.getEntity().getComment(), is("Test contact sample"));
				assertThat(sharedContactSample.getPathogenTests(), hasSize(1));
				assertThat(sharedContactSample.getAdditionalTests(), hasSize(1));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		// If caseSample has associatedContact, sharing is not allowed
		Assertions.assertThrows(
			SormasToSormasException.class,
			() -> getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options));

		// Removing the associatedContact allows sharing
		SampleDto savedCaseSample = getSampleFacade().getByCaseUuids(Collections.singletonList(caze.getUuid())).get(0);
		savedCaseSample.setAssociatedContact(null);
		getSampleFacade().saveSample(savedCaseSample);

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().sample(caseSample.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo = shareInfoList.get(0);
		assertThat(contactShareInfo.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(contactShareInfo.getSender().getCaption(), is("ad MIN"));
		assertThat(contactShareInfo.getComment(), is("Test comment"));
	}

	@Test
	public void testSaveSharedCaseWithInfrastructureName() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createCaseDto(rdcf, person);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalContactType(AnimalContactType.TOUCH);
		caze.getEpiData().getExposures().add(exposure);
		caze.getHealthConditions().setAsplenia(YesNoUnknown.YES);
		caze.getMaternalHistory().setChildrenNumber(2);

		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false), person, caze);

		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getResponsibleRegion(), is(rdcf.region));
		assertThat(savedCase.getResponsibleDistrict(), is(rdcf.district));
		assertThat(savedCase.getResponsibleCommunity(), is(rdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.facility));
		assertThat(savedCase.getHospitalization().getAdmittedToHealthFacility(), is(YesNoUnknown.YES));
		assertThat(savedCase.getSymptoms().getAgitation(), is(SymptomState.YES));
		assertThat(savedCase.getEpiData().getExposures().get(0).getAnimalContactType(), is(AnimalContactType.TOUCH));
		assertThat(savedCase.getHealthConditions().getAsplenia(), is(YesNoUnknown.YES));
		assertThat(savedCase.getMaternalHistory().getChildrenNumber(), is(2));

		assertThat(savedCase.getSormasToSormasOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
		assertThat(savedCase.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));

		PersonDto savedPerson = getPersonFacade().getByUuid(savedCase.getPerson().getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.community));
		assertThat(savedPerson.getFirstName(), is("James"));
		assertThat(savedPerson.getLastName(), is("Smith"));
	}

	@Test
	public void testSaveSharedCaseWithInfrastructureExternalId() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createCaseDto(rdcf, person);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalContactType(AnimalContactType.TOUCH);
		caze.getEpiData().getExposures().add(exposure);
		caze.getHealthConditions().setAsplenia(YesNoUnknown.YES);
		caze.getMaternalHistory().setChildrenNumber(2);

		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false), person, caze);

		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getResponsibleRegion(), is(rdcf.region));
		assertThat(savedCase.getResponsibleDistrict(), is(rdcf.district));
		assertThat(savedCase.getResponsibleCommunity(), is(rdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.facility));
		assertThat(savedCase.getHospitalization().getAdmittedToHealthFacility(), is(YesNoUnknown.YES));
		assertThat(savedCase.getSymptoms().getAgitation(), is(SymptomState.YES));
		assertThat(savedCase.getEpiData().getExposures().get(0).getAnimalContactType(), is(AnimalContactType.TOUCH));
		assertThat(savedCase.getHealthConditions().getAsplenia(), is(YesNoUnknown.YES));
		assertThat(savedCase.getMaternalHistory().getChildrenNumber(), is(2));

		assertThat(savedCase.getSormasToSormasOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
		assertThat(savedCase.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));

		PersonDto savedPerson = getPersonFacade().getByUuid(savedCase.getPerson().getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.community));
		assertThat(savedPerson.getFirstName(), is("James"));
		assertThat(savedPerson.getLastName(), is("Smith"));
	}

	@Test
	public void testSaveSharedPointOfEntryCaseWithInfrastructureName() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setResponsibleRegion(rdcf.region);
		caze.setResponsibleDistrict(rdcf.district);
		caze.setResponsibleCommunity(rdcf.community);
		caze.setPointOfEntry(rdcf.pointOfEntry);
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		portHealthInfo.setAirlineName("Test Airline");
		caze.setPortHealthInfo(portHealthInfo);

		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false), person, caze);

		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getResponsibleRegion(), is(rdcf.region));
		assertThat(savedCase.getResponsibleDistrict(), is(rdcf.district));
		assertThat(savedCase.getResponsibleCommunity(), is(rdcf.community));
		assertThat(savedCase.getPointOfEntry(), is(rdcf.pointOfEntry));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveSharedPointOfEntryCaseWithInfrastructureExternalId() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setResponsibleRegion(rdcf.region);
		caze.setResponsibleDistrict(rdcf.district);
		caze.setResponsibleCommunity(rdcf.community);
		caze.setPointOfEntry(rdcf.pointOfEntry);
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		portHealthInfo.setAirlineName("Test Airline");
		caze.setPortHealthInfo(portHealthInfo);

		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false), person, caze);

		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getResponsibleRegion(), is(rdcf.region));
		assertThat(savedCase.getResponsibleDistrict(), is(rdcf.district));
		assertThat(savedCase.getResponsibleCommunity(), is(rdcf.community));
		assertThat(savedCase.getPointOfEntry(), is(rdcf.pointOfEntry));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveSharedCaseWithContacts() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = createCaseDto(rdcf, person);

		ContactDto contact = createRemoteContactDto(rdcf, caze);

		PersonDto contactPerson = createPersonDto(rdcf);
		contact.setPerson(contactPerson.toReference());

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(contactPerson, contact)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		ContactDto savedContact = getContactFacade().getByUuid(contact.getUuid());

		assertThat(savedContact, is(notNullValue()));
		assertThat(savedContact.getRegion(), is(rdcf.region));
		assertThat(savedContact.getDistrict(), is(rdcf.district));
		assertThat(savedContact.getCommunity(), is(rdcf.community));

		assertThat(savedCase.getSormasToSormasOriginInfo().getUuid(), is(savedContact.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testSaveSharedCaseWithSamples() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = createCaseDto(rdcf, person);
		SormasToSormasSampleDto sample = createRemoteSampleDtoWithTests(rdcf, caze.toReference(), null);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		shareData.setSamples(Collections.singletonList(sample));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		SampleDto savedSample = getSampleFacade().getSampleByUuid(sample.getEntity().getUuid());

		assertThat(savedSample, is(notNullValue()));
		assertThat(savedSample.getLab(), is(rdcf.facility));

		assertThat(getPathogenTestFacade().getAllBySample(savedSample.toReference()), hasSize(1));
		assertThat(getAdditionalTestFacade().getAllBySample(savedSample.getUuid()), hasSize(1));

		assertThat(savedCase.getSormasToSormasOriginInfo().getUuid(), is(savedSample.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testShareCaseWithPseudonymizeData() throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setAdditionalDetails("Test additional details");
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setPseudonymizeData(true);

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getCases().size(), is(1));
				SormasToSormasCaseDto sharedCase = postBody.getCases().get(0);

				assertThat(sharedCase.getPerson().getFirstName(), is("Confidential"));
				assertThat(sharedCase.getPerson().getLastName(), is("Confidential"));
				assertThat(sharedCase.getEntity().getAdditionalDetails(), isEmptyString());

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);
	}

	@Test
	public void testReturnCase() throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		SormasToSormasOriginInfoDto originInfo = createAndSaveSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true, null);

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setSormasToSormasOriginInfo(originInfo);
		});

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);

		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		ContactDto sharedContact = creator.createContact(
			officer,
			officer,
			creator.createPerson().toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf,
			c -> c.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo()));

		SampleDto sharedSample =
			creator.createSample(caze.toReference(), officer, rdcf.facility, s -> s.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo()));

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setHandOverOwnership(true);
		options.setWithAssociatedContacts(true);
		options.setWithSamples(true);
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(new ShareRequestAcceptData(null, null)));

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		// case ownership should be lost
		CaseDataDto sharedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(sharedCase.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// contact ownership should be lost
		sharedContact = getContactFacade().getByUuid(sharedContact.getUuid());
		assertThat(sharedContact.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// sample ownership should be lost
		sharedSample = getSampleFacade().getSampleByUuid(sharedSample.getUuid());
		assertThat(sharedSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));
	}

	@Test
	public void testSaveReturnedCase() throws SormasToSormasException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, person.toReference(), rdcf);

		PersonDto sharedContactPerson = creator.createPerson();
		ContactDto sharedContact = creator.createContact(officer, sharedContactPerson.toReference(), caze);
		PersonDto newContactPerson = creator.createPerson();
		ContactDto newContact = createContactRemoteContact(officer, newContactPerson.toReference(), caze);
		ContactDto newContact2 = createContactRemoteContact(officer, newContactPerson.toReference(), caze);
		SampleDto sharedSample = creator.createSample(caze.toReference(), officer, rdcf.facility);
		SampleDto newSample = createRemoteSample(caze.toReference(), officer, rdcf.facility);
		SampleDto newSample2 = createRemoteSample(caze.toReference(), officer, rdcf.facility);

		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			officerUser,
			DEFAULT_SERVER_ID,
			true,
			i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference())));
		shareRequestInfo.getShares()
			.add(createShareInfo(DEFAULT_SERVER_ID, true, i -> i.setContact(getContactService().getByReferenceDto(sharedContact.toReference()))));
		shareRequestInfo.getShares()
			.add(createShareInfo(DEFAULT_SERVER_ID, true, i -> i.setSample(getSampleService().getByReferenceDto(sharedSample.toReference()))));
		getShareRequestInfoService().persist(shareRequestInfo);

		caze.setQuarantine(QuarantineType.HOTEL);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		shareData.setContacts(
			Arrays.asList(
				new SormasToSormasContactDto(sharedContactPerson, sharedContact),
				new SormasToSormasContactDto(newContactPerson, newContact),
				new SormasToSormasContactDto(newContactPerson, newContact2)));
		shareData.setSamples(
			Arrays.asList(
				new SormasToSormasSampleDto(sharedSample, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList())));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

		try {
			getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		CaseDataDto returnedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(returnedCase.getQuarantine(), is(QuarantineType.HOTEL));
		assertThat(returnedCase.getReportingUser(), is(officer));

		List<SormasToSormasShareInfoDto> caseShares =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);
		assertThat(caseShares.get(0).isOwnershipHandedOver(), is(false));

		List<SormasToSormasShareInfoDto> contactShares =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().contact(sharedContact.toReference()), 0, 100);
		assertThat(contactShares.get(0).isOwnershipHandedOver(), is(false));

		ContactDto returnedNewContact = getContactFacade().getByUuid(newContact.getUuid());
		assertThat(returnedNewContact.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));

		ContactDto returnedNewContact2 = getContactFacade().getByUuid(newContact.getUuid());
		assertThat(returnedNewContact2.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));

		List<SormasToSormasShareInfoDto> sampleShares =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().sample(sharedSample.toReference()), 0, 100);
		assertThat(sampleShares.get(0).isOwnershipHandedOver(), is(false));

		SampleDto returnedNewSample = getSampleFacade().getSampleByUuid(newSample.getUuid());
		assertThat(returnedNewSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));

		SampleDto returnedNewSample2 = getSampleFacade().getSampleByUuid(newSample.getUuid());
		assertThat(returnedNewSample2.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));
	}

	@Test
	public void testSaveSharedCaseWithUnknownFacility() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createCaseDto(rdcf, person);
		caze.setHealthFacility(new FacilityReferenceDto("unknown", "Unknown facility", "unknown"));

		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false), person, caze);

		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getHealthFacility().getUuid(), is(FacilityDto.OTHER_FACILITY_UUID));
		assertThat(savedCase.getHealthFacilityDetails(), is("Unknown facility"));
	}

	@Test
	public void testSaveSharedCaseWithUnknownPoint() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setResponsibleRegion(rdcf.region);
		caze.setResponsibleDistrict(rdcf.district);
		caze.setResponsibleCommunity(rdcf.community);
		caze.setPointOfEntry(new PointOfEntryReferenceDto("unknown", "Unknown POE", PointOfEntryType.AIRPORT, null));
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		portHealthInfo.setAirlineName("Test Airline");
		caze.setPortHealthInfo(portHealthInfo);

		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false), person, caze);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getPointOfEntry().getUuid(), is(PointOfEntryDto.OTHER_AIRPORT_UUID));
		assertThat(savedCase.getPointOfEntryDetails(), is("Unknown POE"));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveReturnedCaseWithKnownOtherFacility() throws SormasToSormasException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, c -> {
			c.setPerson(person.toReference());
			c.setHealthFacility(getFacilityFacade().getByUuid(FacilityDto.OTHER_FACILITY_UUID).toReference());
			c.setHealthFacilityDetails("Test HF details");
		});

		User officerUser = getUserService().getByReferenceDto(officer);
		getShareRequestInfoService().persist(
			createShareRequestInfo(
				ShareRequestDataType.CASE,
				officerUser,
				DEFAULT_SERVER_ID,
				true,
				i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference()))));

		caze.setHealthFacilityDetails(rdcf.facility.getCaption());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(originInfo, person, caze);

		try {
			getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		CaseDataDto returnedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(returnedCase.getHealthFacility(), is(rdcf.facility));
		assertThat(returnedCase.getHealthFacilityDetails(), is(nullValue()));
	}

	@Test
	public void testSaveReturnedCaseWithKnownOtherPointOfEntry() throws SormasToSormasException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, c -> {
			c.setPerson(person.toReference());
			c.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			c.setPointOfEntry(new PointOfEntryReferenceDto(PointOfEntryDto.OTHER_SEAPORT_UUID, null, null, null));
			c.setPointOfEntryDetails("Test Seaport");
		});

		User officerUser = getUserService().getByReferenceDto(officer);
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(DEFAULT_SERVER_ID, true, i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference()))));

		caze.setPointOfEntryDetails(rdcf.pointOfEntry.getCaption());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);
		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(originInfo, person, caze);

		try {
			getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		CaseDataDto returnedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(returnedCase.getPointOfEntry(), is(rdcf.pointOfEntry));
		assertThat(returnedCase.getPointOfEntryDetails(), is(nullValue()));
	}

	@Test
	public void testSyncCases() throws SormasToSormasException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		SormasToSormasOriginInfoDto originInfo =
			createAndSaveSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true, o -> o.setWithAssociatedContacts(true));

		PersonDto casePerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, casePerson.toReference(), rdcf, c -> {
			c.setSormasToSormasOriginInfo(originInfo);
		});

		PersonDto contactPerson = creator.createPerson();
		ContactDto contact = creator.createContact(
			officer,
			officer,
			contactPerson.toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf,
			(c) -> c.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo()));

		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			getUserService().getByUuid(officer.getUuid()),
			SECOND_SERVER_ID,
			false,
			ShareRequestStatus.ACCEPTED,
			i -> i.setCaze(getCaseService().getByUuid(caze.getUuid())));
		shareRequestInfo.setWithAssociatedContacts(true);
		shareRequestInfo.getShares()
			.add(createShareInfo(SECOND_SERVER_ID, false, i -> i.setContact(getContactService().getByUuid(contact.getUuid()))));
		getShareRequestInfoService().persist(shareRequestInfo);

		PersonDto newContactPerson = creator.createPerson();
		ContactDto newContact = creator.createContact(
			officer,
			officer,
			newContactPerson.toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf,
			(c) -> c.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo()));

		caze.setAdditionalDetails("Test updated details");
		contact.setContactStatus(ContactStatus.DROPPED);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());

		Mockito
			.when(
				MockProducer.getManagedScheduledExecutorService()
					.schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
			.then(invocation -> {
				((Runnable) invocation.getArgument(0)).run();
				return null;
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(caze.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(syncData.getCriteria().isForwardOnly(), is(false));

				assertThat(syncData.getShareData().getCases().get(0).getEntity().getUuid(), is(caze.getUuid()));
				assertThat(syncData.getShareData().getCases().get(0).getEntity().getAdditionalDetails(), is("Test updated details"));
				assertThat(syncData.getShareData().getContacts(), hasSize(2));

				return Response.noContent().build();
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(caze.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(nullValue()));
				assertThat(syncData.getCriteria().isForwardOnly(), is(true));

				assertThat(syncData.getShareData().getCases().get(0).getEntity().getUuid(), is(caze.getUuid()));
				assertThat(syncData.getShareData().getCases().get(0).getEntity().getAdditionalDetails(), is("Test updated details"));
				// new contact should not be shared
				assertThat(syncData.getShareData().getContacts(), hasSize(1));
				assertThat(syncData.getShareData().getContacts().get(0).getEntity().getUuid(), is(contact.getUuid()));

				return Response.noContent().build();
			});

		// save contact without syncing shares (eg. internal = false)
		getContactFacade().save(contact, true, false, false, false);
		// case save should trigger sync
		getCaseFacade().save(caze);

		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());
	}

	@Test
	public void testSyncSharesWithSampleAddedOnCaseHandedOver() throws SormasToSormasException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		SormasToSormasOriginInfoDto originInfo = createAndSaveSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true, o -> o.setWithSamples(true));

		PersonDto casePerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, casePerson.toReference(), rdcf, c -> {
			c.setSormasToSormasOriginInfo(originInfo);
		});

		SampleDto sample = creator.createSample(caze.toReference(), officer, rdcf.facility);

		getSormasToSormasCaseFacade().syncShares(new ShareTreeCriteria(caze.getUuid()));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getShareData().getCases().get(0).getEntity().getUuid(), is(caze.getUuid()));
				assertThat(syncData.getShareData().getSamples().get(0).getEntity().getUuid(), is(sample.getUuid()));

				return Response.noContent().build();
			});

		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());

		List<SormasToSormasShareInfoDto> sampleShareInfos =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().sample(sample.toReference()), null, null);
		assertThat(sampleShareInfos, hasSize(1));

		// no share info should be created for the case because it has an origin info so it's already shared 
		List<SormasToSormasShareInfoDto> caseShareInfos =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), null, null);
		assertThat(caseShareInfos, hasSize(0));
	}

	@Test
	public void testGetAllShares() throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		CaseDataDto caze = creator.createCase(officer, creator.createPerson().toReference(), rdcf);

		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			officerUser,
			SECOND_SERVER_ID,
			true,
			ShareRequestStatus.ACCEPTED,
			i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference())));
		getShareRequestInfoService().persist(shareRequestInfo);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(SECOND_SERVER_ID), eq("/sormasToSormas/cases/shares"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasShareInfoDto shareInfo = new SormasToSormasShareInfoDto();
				shareInfo.setTargetDescriptor(new SormasServerDescriptor("dummy SORMAS"));
				shareInfo.setOwnershipHandedOver(true);
				shareInfo.setComment("re-shared");

				return encryptShareDataAsArray(new SormasToSormasShareTree(null, shareInfo, Collections.emptyList(), false));
			});

		List<SormasToSormasShareTree> shares = getSormasToSormasCaseFacade().getAllShares(caze.getUuid());

		assertThat(shares, hasSize(1));
		SormasToSormasShareInfoDto fistShare = shares.get(0).getShare();
		assertThat(fistShare.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(fistShare.isOwnershipHandedOver(), is(true));

		List<SormasToSormasShareTree> reShares = shares.get(0).getReShares();
		assertThat(reShares, hasSize(1));
		SormasToSormasShareInfoDto reShare = reShares.get(0).getShare();
		assertThat(reShare.getTargetDescriptor().getId(), is("dummy SORMAS"));
		assertThat(reShare.isOwnershipHandedOver(), is(true));
		assertThat(reShare.getComment(), is("re-shared"));
		assertThat(reShares.get(0).getReShares(), hasSize(0));
	}

	@Test
	public void testGetAllSharesOnMiddleLevel() throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		SormasToSormasOriginInfoDto originInfo = createAndSaveSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true, o -> o.setComment("first share"));

		CaseDataDto caze = creator.createCase(officer, creator.createPerson().toReference(), rdcf, c -> {
			c.setSormasToSormasOriginInfo(originInfo);
		});

		// initial share by the creator of case
		SormasToSormasShareInfoDto shareToSecond = createShareInfoDto(officer, SECOND_SERVER_ID, true);
		// another share by the creator
		SormasToSormasShareInfoDto anotherShareFromDefault = createShareInfoDto(officer, "anotherShareFromDefault", false);

		// forwarded by the second system
		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareFromSecond = createShareRequestInfo(
			ShareRequestDataType.CASE,
			officerUser,
			"shareFromSecond",
			false,
			ShareRequestStatus.ACCEPTED,
			i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference())));
		getShareRequestInfoService().persist(shareFromSecond);

		// The share tree for the above code is:
		// |- default
		//   |- second
		//     |- shareFromSecond
		//   |- another from default

		mockSecondServerAccess();

		// mock share tree on the case creator system
		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(DEFAULT_SERVER_ID), eq("/sormasToSormas/cases/shares"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {

				List<SormasToSormasShareTree> shareTrees = new ArrayList<>();
				shareTrees.add(new SormasToSormasShareTree(null, shareToSecond, Collections.emptyList(), false));
				shareTrees.add(new SormasToSormasShareTree(null, anotherShareFromDefault, Collections.emptyList(), false));

				return encryptShareData(shareTrees);
			});

		// Mock shares from "anotherShareFromDefault" -> no more shares
		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq("anotherShareFromDefault"), eq("/sormasToSormas/cases/shares"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(Collections.emptyList()));

		// Mock shares from "shareFromSecond" server -> no more shares
		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq("shareFromSecond"), eq("/sormasToSormas/cases/shares"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(Collections.emptyList()));

		List<SormasToSormasShareTree> shares = getSormasToSormasCaseFacade().getAllShares(caze.getUuid());

		assertThat(shares, hasSize(2));
		SormasToSormasShareInfoDto fistShare = shares.get(0).getShare();

		assertThat(fistShare.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(fistShare.isOwnershipHandedOver(), is(true));

		List<SormasToSormasShareTree> reShares = shares.get(0).getReShares();
		assertThat(reShares, hasSize(1));

		SormasToSormasShareInfoDto reShare = reShares.get(0).getShare();
		assertThat(reShare.getTargetDescriptor().getId(), is("shareFromSecond"));
		assertThat(reShare.isOwnershipHandedOver(), is(false));

		assertThat(reShares.get(0).getReShares(), hasSize(0));

		SormasToSormasShareTree secondReShareFromDefault = shares.get(1);
		assertThat(secondReShareFromDefault.getShare().getTargetDescriptor().getId(), is("anotherShareFromDefault"));
		assertThat(secondReShareFromDefault.getShare().isOwnershipHandedOver(), is(false));

		assertThat(secondReShareFromDefault.getReShares(), hasSize(0));
	}

	@Test
	public void testGetReShares() throws SormasToSormasException {
		UserReferenceDto officer = useSurveillanceOfficerLogin(rdcf).toReference();

		CaseDataDto caze = creator.createCase(officer, creator.createPerson().toReference(), rdcf);

		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			officerUser,
			SECOND_SERVER_ID,
			true,
			ShareRequestStatus.ACCEPTED,
			i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference())));
		getShareRequestInfoService().persist(shareRequestInfo);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(SECOND_SERVER_ID), eq("/sormasToSormas/cases/shares"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasShareInfoDto shareInfo = new SormasToSormasShareInfoDto();
				shareInfo.setTargetDescriptor(new SormasServerDescriptor("dummy SORMAS"));
				shareInfo.setOwnershipHandedOver(false);
				shareInfo.setComment("re-shared");

				return encryptShareDataAsArray(new SormasToSormasShareTree(null, shareInfo, Collections.emptyList(), false));
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq("dummy SORMAS"), eq("/sormasToSormas/cases/shares"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(Collections.emptyList()));
		mockS2Snetwork();
		SormasToSormasEncryptedDataDto encryptedCriteria = encryptShareData(new ShareTreeCriteria(caze.getUuid(), null, false));

		SormasToSormasEncryptedDataDto encryptedShares = getSormasToSormasCaseFacade().getShareTrees(encryptedCriteria);

		mockDefaultServerAccess();

		SormasToSormasShareTree[] shares = getSormasToSormasEncryptionFacade()
			.decryptAndVerify(new SormasToSormasEncryptedDataDto(SECOND_SERVER_ID, encryptedShares.getData()), SormasToSormasShareTree[].class);

		assertThat(shares, arrayWithSize(1));
		assertThat(shares[0].getShare().getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shares[0].getShare().isOwnershipHandedOver(), is(true));
		assertThat(shares[0].getReShares().get(0).getShare().getTargetDescriptor().getId(), is("dummy SORMAS"));
		assertThat(shares[0].getReShares().get(0).getShare().isOwnershipHandedOver(), is(false));
		assertThat(shares[0].getReShares().get(0).getReShares(), hasSize(0));
	}

	@Test
	public void testReportingUserIsIncludedButUpdated() throws SormasToSormasException {
		UserDto officer = useSurveillanceOfficerLogin(rdcf);

		final PersonReferenceDto person = creator.createPerson().toReference();
		CaseDataDto caze = creator.createCase(officer.toReference(), person, rdcf);

		creator.createImmunization(Disease.CORONAVIRUS, person, officer.toReference(), rdcf);

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setWithImmunizations(true);
		options.setComment("Test comment");

		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));

		final String uuid = DataHelper.createUuid();

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);

				// make sure that no entities are found
				final CaseDataDto entity = postBody.getCases().get(0).getEntity();

				entity.setUuid(uuid);
				entity.getHospitalization().setUuid(DataHelper.createUuid());
				entity.getSymptoms().setUuid(DataHelper.createUuid());
				entity.getEpiData().setUuid(DataHelper.createUuid());
				entity.getTherapy().setUuid(DataHelper.createUuid());
				entity.getHealthConditions().setUuid(DataHelper.createUuid());
				entity.getPortHealthInfo().setUuid(DataHelper.createUuid());
				entity.getClinicalCourse().setUuid(DataHelper.createUuid());
				entity.getMaternalHistory().setUuid(DataHelper.createUuid());

				postBody.getImmunizations().get(0).getEntity().setUuid(uuid);

				SormasToSormasEncryptedDataDto encryptedData = encryptShareData(new ShareRequestAcceptData(null, null));
				loginWith(s2sClientUser);
				getSormasToSormasCaseFacade().saveSharedEntities(encryptShareData(postBody));
				loginWith(officer);
				return encryptedData;
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(uuid);
		assertThat(savedCase.getReportingUser(), is(s2sClientUser.toReference()));

		ImmunizationDto savedImmunization = getImmunizationFacade().getByUuid(uuid);
		assertThat(savedImmunization.getReportingUser(), is(s2sClientUser.toReference()));
	}

	@Test
	public void testSaveSyncedCase() throws SormasToSormasException, SormasToSormasValidationException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		final PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, person.toReference(), rdcf, c -> {
			c.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
			c.setSormasToSormasOriginInfo(creator.createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false, null));
		});

		caze.setFollowUpStatus(FollowUpStatus.LOST);
		person.setBirthName("Test birth name");

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));

		SormasToSormasEncryptedDataDto encryptedData =
			encryptShareData(new SyncDataDto(shareData, new ShareTreeCriteria(caze.getUuid(), null, false)));

		getSormasToSormasCaseFacade().saveSyncedEntity(encryptedData);

		CaseDataDto syncedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(syncedCase.getFollowUpStatus(), is(FollowUpStatus.LOST));
		assertThat(syncedCase.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		PersonDto syncedPerson = getPersonFacade().getByUuid(person.getUuid());
		assertThat(syncedPerson.getBirthName(), is("Test birth name"));
	}

	@Test
	public void testSyncNotUpdateOwnedPerson() throws SormasToSormasException, SormasToSormasValidationException {
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();

		final PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, person.toReference(), rdcf, c -> {
			c.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
			c.setSormasToSormasOriginInfo(creator.createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false, null));
		});

		// owned contact with same person should make person not be synced
		creator.createContact(rdcf, officer, person.toReference());

		caze.setFollowUpStatus(FollowUpStatus.LOST);
		person.setBirthName("Test birth name");

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));

		SormasToSormasEncryptedDataDto encryptedData =
			encryptShareData(new SyncDataDto(shareData, new ShareTreeCriteria(caze.getUuid(), null, false)));

		getSormasToSormasCaseFacade().saveSyncedEntity(encryptedData);

		CaseDataDto syncedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(syncedCase.getFollowUpStatus(), is(FollowUpStatus.LOST));

		PersonDto syncedPerson = getPersonFacade().getByUuid(person.getUuid());
		assertThat(syncedPerson.getBirthName(), is(nullValue()));
	}

	@Test
	public void testShareWithSurveillanceReports() throws SormasToSormasException {
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = user.toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		SurveillanceReportDto surveillanceReport = creator.createSurveillanceReport(officer, ReportingType.LABORATORY, caze.toReference(), (r) -> {
			r.setReportDate(new Date());
			r.setFacilityRegion(rdcf.region);
			r.setFacilityDistrict(rdcf.district);
			r.setFacility(rdcf.facility);
			r.setNotificationDetails("Test lab report notification");
		});

		SurveillanceReportDto surveillanceReport2 = creator.createSurveillanceReport(officer, ReportingType.DOCTOR, caze.toReference(), (r) -> {
			r.setReportDate(new Date());
			r.setFacilityRegion(rdcf.region);
			r.setFacilityDistrict(rdcf.district);
			r.setFacility(rdcf.facility);
			r.setNotificationDetails("Test doctor report notification");
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");
		options.setWithSurveillanceReports(true);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {

				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);

				assertThat(postBody.getSurveillanceReports().size(), is(2));
				SormasToSormasSurveillanceReportDto sharedSurveillanceReport = postBody.getSurveillanceReports()
					.stream()
					.filter(r -> r.getEntity().getUuid().equals(surveillanceReport.getUuid()))
					.findFirst()
					.get();
				assertThat(sharedSurveillanceReport.getEntity().getReportDate().compareTo(surveillanceReport.getReportDate()), is(0)); // use compareTo because getReportDate() returns Timestamp object due to SurveillanceReport.java using TemporalType.Timestamp
				assertThat(sharedSurveillanceReport.getEntity().getReportingType(), is(surveillanceReport.getReportingType()));
				assertThat(sharedSurveillanceReport.getEntity().getFacilityRegion(), is(surveillanceReport.getFacilityRegion()));
				assertThat(sharedSurveillanceReport.getEntity().getFacilityDistrict(), is(surveillanceReport.getFacilityDistrict()));
				assertThat(sharedSurveillanceReport.getEntity().getFacility(), is(surveillanceReport.getFacility()));
				assertThat(sharedSurveillanceReport.getEntity().getNotificationDetails(), is(surveillanceReport.getNotificationDetails()));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> surveillanceReportShareInfoList = getSormasToSormasShareInfoFacade()
			.getIndexList(new SormasToSormasShareInfoCriteria().surveillanceReport(surveillanceReport.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo = surveillanceReportShareInfoList.get(0);
		assertThat(contactShareInfo.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(contactShareInfo.getSender().getCaption(), is("ad MIN"));
		assertThat(contactShareInfo.getComment(), is("Test comment"));

		List<SormasToSormasShareInfoDto> surveillanceReport2ShareInfoList = getSormasToSormasShareInfoFacade()
			.getIndexList(new SormasToSormasShareInfoCriteria().surveillanceReport(surveillanceReport2.toReference()), 0, 100);

		assertThat(surveillanceReport2ShareInfoList, hasSize(1));
	}

	@Test
	public void testSharePseudonymizedWithSurveillanceReport() throws SormasToSormasException {
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = user.toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		SurveillanceReportDto surveillanceReport = creator.createSurveillanceReport(officer, ReportingType.LABORATORY, caze.toReference(), (r) -> {
			r.setReportDate(new Date());
			r.setFacilityRegion(rdcf.region);
			r.setFacilityDistrict(rdcf.district);
			r.setFacility(rdcf.facility);
			r.setFacilityDetails("Test facility details");
			r.setNotificationDetails("Test lab report notification");
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");
		options.setWithSurveillanceReports(true);
		options.setPseudonymizeData(true);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {

				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);

				SormasToSormasSurveillanceReportDto sharedSurveillanceReport = postBody.getSurveillanceReports().get(0);
				assertThat(sharedSurveillanceReport.getEntity().getFacility(), is(surveillanceReport.getFacility()));
				assertThat(sharedSurveillanceReport.getEntity().getFacilityDetails(), is(""));
				assertThat(sharedSurveillanceReport.getEntity().getNotificationDetails(), is(""));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);
	}

	@Test
	public void testShareWithSurveillanceReportAndExternalMessage() throws SormasToSormasException {
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = user.toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		SurveillanceReportDto report = creator.createSurveillanceReport(officer, ReportingType.LABORATORY, caze.toReference(), (r) -> {
			r.setReportDate(new Date());
			r.setFacilityRegion(rdcf.region);
			r.setFacilityDistrict(rdcf.district);
			r.setFacility(rdcf.facility);
			r.setNotificationDetails("Test lab report notification");
		});

		ExternalMessageDto externalMessage = creator.createExternalMessage(m -> {
			m.setStatus(ExternalMessageStatus.PROCESSED);
			m.setSurveillanceReport(report.toReference());
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");
		options.setWithSurveillanceReports(true);
		options.setHandOverOwnership(true);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {

				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);

				SormasToSormasExternalMessageDto sharedExternalMessage = postBody.getSurveillanceReports().get(0).getExternalMessage();
				assertThat(sharedExternalMessage.getEntity().getUuid(), is(externalMessage.getUuid()));
				assertThat(sharedExternalMessage.getEntity().getStatus(), is(externalMessage.getStatus()));
				assertThat(sharedExternalMessage.getEntity().getSurveillanceReport(), is(report.toReference()));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		assertThat(getExternalMessageFacade().getByUuid(externalMessage.getUuid()).getStatus(), is(ExternalMessageStatus.FORWARDED));
	}

	@Test
	public void testSaveSharedWithSurveillanceReports() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);
		CaseDataDto caze = createCaseDto(rdcf, person);
		SurveillanceReportDto report = SurveillanceReportDto.build(caze.toReference(), null);
		report.setReportDate(new Date());
		report.setReportingType(ReportingType.LABORATORY);
		report.setFacilityRegion(rdcf.region);
		report.setFacilityDistrict(rdcf.district);
		report.setFacility(rdcf.facility);
		report.setNotificationDetails("Test notification details");

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		shareData.setSurveillanceReports(Collections.singletonList(new SormasToSormasSurveillanceReportDto(report, null)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		SurveillanceReportDto savedReport = getSurveillanceReportFacade().getByUuid(report.getUuid());

		assertThat(savedReport, is(notNullValue()));
		assertThat(savedReport.getReportDate().compareTo(report.getReportDate()), is(0));
		assertThat(savedReport.getReportingType(), is(report.getReportingType()));
		assertThat(savedReport.getFacilityRegion(), is(report.getFacilityRegion()));
		assertThat(savedReport.getFacilityDistrict(), is(report.getFacilityDistrict()));
		assertThat(savedReport.getFacility(), is(report.getFacility()));
		assertThat(savedReport.getNotificationDetails(), is(report.getNotificationDetails()));

		assertThat(savedCase.getSormasToSormasOriginInfo().getUuid(), is(savedReport.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testSaveSharedCaseWithSurveillanceReportAndExternalMessage() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);
		CaseDataDto caze = createCaseDto(rdcf, person);

		SurveillanceReportDto report = SurveillanceReportDto.build(caze.toReference(), null);
		report.setReportDate(new Date());
		report.setReportingType(ReportingType.LABORATORY);

		ExternalMessageDto externalMessage = ExternalMessageDto.build();
		externalMessage.setSurveillanceReport(report.toReference());
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		shareData.setSurveillanceReports(
			Collections.singletonList(new SormasToSormasSurveillanceReportDto(report, new SormasToSormasExternalMessageDto(externalMessage))));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		ExternalMessage savedExternalMessage = getSurveillanceReportService().getByUuid(report.getUuid()).getExternalMessage();
		assertThat(savedExternalMessage.getUuid(), is(externalMessage.getUuid()));
		assertThat(savedExternalMessage.getStatus(), is(externalMessage.getStatus()));
	}

	@Test
	public void testSetResponsibleDistrictOnAccept() throws SormasToSormasException, SormasToSormasValidationException {
		TestDataCreator.RDCF s2sRdcf = createRDCF("S2SExtId").centralRdcf;
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_DISTRICT_EXTERNAL_ID, s2sRdcf.district.getExternalId());

		// sharing without ownership should not change responsible district
		PersonDto person = createPersonDto(rdcf);
		CaseDataDto caze = createCaseDto(rdcf, person);
		SormasToSormasEncryptedDataDto encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, false), person, caze);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getResponsibleRegion(), is(rdcf.region));
		assertThat(savedCase.getResponsibleDistrict(), is(rdcf.district));
		assertThat(savedCase.getResponsibleCommunity(), is(rdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.facility));

		// sharing with ownership should change responsible district
		person = createPersonDto(rdcf);
		caze = createCaseDto(rdcf, person);
		encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true), person, caze);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getResponsibleRegion(), is(s2sRdcf.region));
		assertThat(savedCase.getResponsibleDistrict(), is(s2sRdcf.district));
		assertThat(savedCase.getResponsibleCommunity(), is(nullValue()));
		assertThat(savedCase.getRegion(), is(rdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.facility));

		// case with s2s district should not be changed
		person = createPersonDto(s2sRdcf);
		caze = createCaseDto(s2sRdcf, person);
		encryptedData = createCaseShareData(createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true), person, caze);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getResponsibleRegion(), is(s2sRdcf.region));
		assertThat(savedCase.getResponsibleDistrict(), is(s2sRdcf.district));
		assertThat(savedCase.getResponsibleCommunity(), is(s2sRdcf.community));
		assertThat(savedCase.getRegion(), is(nullValue()));
		assertThat(savedCase.getDistrict(), is(nullValue()));
		assertThat(savedCase.getCommunity(), is(nullValue()));
		assertThat(savedCase.getHealthFacility(), is(s2sRdcf.facility));

	}

	private SormasToSormasEncryptedDataDto createCaseShareData(SormasToSormasOriginInfoDto originInfo, PersonDto person, CaseDataDto caze)
		throws SormasToSormasException {
		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		return encryptedData;
	}

	private ContactDto createRemoteContactDto(TestDataCreator.RDCF remoteRdcf, CaseDataDto caze) {
		ContactDto contact = ContactDto.build(caze);
		contact.setRegion(remoteRdcf.region);
		contact.setDistrict(remoteRdcf.district);
		contact.setCommunity(remoteRdcf.community);
		return contact;
	}

	public ContactDto createContactRemoteContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, CaseDataDto caze) {
		ContactDto contact = ContactDto.build(caze);
		contact.setReportingUser(reportingUser);
		contact.setPerson(contactPerson);
		contact.setEpiData(EpiDataDto.build());

		return contact;
	}

	private SampleDto createRemoteSample(CaseReferenceDto associatedCase, UserReferenceDto reportingUser, FacilityReferenceDto lab) {

		SampleDto sample = SampleDto.build(reportingUser, associatedCase);
		sample.setSampleDateTime(new Date());
		sample.setReportDateTime(new Date());
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(getFacilityFacade().getReferenceByUuid(lab.getUuid()));

		return sample;
	}
}
