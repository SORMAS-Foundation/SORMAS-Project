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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
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
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.user.DefaultUserRole;
import de.symeda.sormas.backend.user.User;

@RunWith(MockitoJUnitRunner.class)
public class SormasToSormasCaseFacadeEjbTest extends SormasToSormasTest {

	@Test
	public void testShareCase() throws SormasToSormasException {

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
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
				assertThat(sharedCase.getEntity().getReportingUser(), is(nullValue()));
				assertThat(sharedCase.getEntity().getSurveillanceOfficer(), is(nullValue()));
				assertThat(sharedCase.getEntity().getClassificationUser(), is(nullValue()));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().getComment(), is("Test comment"));

				return Response.noContent().build();
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithContacts() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
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

				return Response.noContent().build();
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo = shareInfoList.get(0);
		assertThat(contactShareInfo.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(contactShareInfo.getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(contactShareInfo.getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithSamples() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER));
		UserReferenceDto officer = user.toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		Date sampleDateTime = new Date();
		SampleDto caseSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSampleDateTime(sampleDateTime);
			s.setComment("Test case sample");
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

				return Response.noContent().build();
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().sample(caseSample.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo = shareInfoList.get(0);
		assertThat(contactShareInfo.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(contactShareInfo.getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(contactShareInfo.getComment(), is("Test comment"));
	}

	@Test
	public void testSaveSharedCaseWithInfrastructureName() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalContactType(AnimalContactType.TOUCH);
		caze.getEpiData().getExposures().add(exposure);
		caze.getHealthConditions().setAsplenia(YesNoUnknown.YES);
		caze.getMaternalHistory().setChildrenNumber(2);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

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

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(savedCase.getPerson().getUuid());
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

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalContactType(AnimalContactType.TOUCH);
		caze.getEpiData().getExposures().add(exposure);
		caze.getHealthConditions().setAsplenia(YesNoUnknown.YES);
		caze.getMaternalHistory().setChildrenNumber(2);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

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

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(savedCase.getPerson().getUuid());
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

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

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

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));
		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

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

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);

		ContactDto contact = createRemoteContactDto(rdcf, caze);

		PersonDto contactPerson = createPersonDto(rdcf);
		contact.setPerson(contactPerson.toReference());

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
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

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);
		SormasToSormasSampleDto sample = createRemoteSampleDtoWithTests(rdcf, caze.toReference(), null);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
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
	public void testShareCaseWithPseudonymizePersonalData() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setAdditionalDetails("Test additional details");
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setPseudonymizePersonalData(true);

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
				assertThat(sharedCase.getEntity().getAdditionalDetails(), is("Test additional details"));

				return Response.noContent().build();
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);
	}

	@Test
	public void testShareCaseWithPseudonymizeSensitiveData() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);

			dto.setAdditionalDetails("Test additional details");
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setPseudonymizeSensitiveData(true);

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

				return Response.noContent().build();
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);
	}

	@Test
	public void testReturnCase() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);

			SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
			originInfo.setSenderName("Test Name");
			originInfo.setSenderEmail("test@email.com");
			originInfo.setOrganizationId(DEFAULT_SERVER_ID);
			originInfo.setOwnershipHandedOver(true);
			dto.setSormasToSormasOriginInfo(originInfo);
		});

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(caze.getSormasToSormasOriginInfo());
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
					.put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> Response.noContent().build());

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
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

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
		getShareRequestInfoService().persist(
			createShareRequestInfo(officerUser, DEFAULT_SERVER_ID, true, i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference()))));
		getShareRequestInfoService().persist(
			createShareRequestInfo(
				officerUser,
				DEFAULT_SERVER_ID,
				true,
				i -> i.setContact(getContactService().getByReferenceDto(sharedContact.toReference()))));
		getShareRequestInfoService().persist(
			createShareRequestInfo(
				officerUser,
				DEFAULT_SERVER_ID,
				true,
				i -> i.setSample(getSampleService().getByReferenceDto(sharedSample.toReference()))));

		caze.setQuarantine(QuarantineType.HOTEL);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true);

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
				new SormasToSormasSampleDto(sharedSample, Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample, Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample2, Collections.emptyList(), Collections.emptyList())));

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

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);
		caze.setHealthFacility(new FacilityReferenceDto("unknown", "Unknown facility", "unknown"));

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

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

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		getSormasToSormasCaseFacade().saveSharedEntities(encryptedData);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getPointOfEntry().getUuid(), is(PointOfEntryDto.OTHER_AIRPORT_UUID));
		assertThat(savedCase.getPointOfEntryDetails(), is("Unknown POE"));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveReturnedCaseWithKnownOtherFacility() throws SormasToSormasException {
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, c -> {
			c.setPerson(person.toReference());
			c.setHealthFacility(getFacilityFacade().getByUuid(FacilityDto.OTHER_FACILITY_UUID).toReference());
			c.setHealthFacilityDetails("Test HF details");
		});

		User officerUser = getUserService().getByReferenceDto(officer);
		getShareRequestInfoService().persist(
			createShareRequestInfo(officerUser, DEFAULT_SERVER_ID, true, i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference()))));

		caze.setHealthFacilityDetails(rdcf.facility.getCaption());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

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
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

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

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true);
		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

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
	public void testSyncCases() throws SormasToSormasValidationException, SormasToSormasException {
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		PersonDto casePerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, casePerson.toReference(), rdcf, c -> {
			SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
			originInfo.setSenderName("Test Name");
			originInfo.setSenderEmail("test@email.com");
			originInfo.setOrganizationId(DEFAULT_SERVER_ID);
			originInfo.setWithAssociatedContacts(true);
			originInfo.setOwnershipHandedOver(true);

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

		SormasToSormasDto shareData = new SormasToSormasDto();
		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true);
		originInfo.setWithAssociatedContacts(true);
		shareData.setOriginInfo(originInfo);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(casePerson, caze)));
		shareData.setContacts(
			Arrays.asList(new SormasToSormasContactDto(contactPerson, contact), new SormasToSormasContactDto(newContactPerson, newContact)));

		SormasToSormasEncryptedDataDto encryptedData =
			encryptShareData(new SyncDataDto(shareData, new ShareTreeCriteria(caze.getUuid(), null, false)));

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
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(SECOND_SERVER_ID));
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

		getSormasToSormasCaseFacade().saveSyncedEntity(encryptedData);

		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/cases/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());
	}

	@Test
	public void testGetAllShares() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, creator.createPerson().toReference(), rdcf);

		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
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
		useSurveillanceOfficerLogin(rdcf);

		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, creator.createPerson().toReference(), rdcf, c -> {
			SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
			originInfo.setSenderName("Test Name");
			originInfo.setOrganizationId(DEFAULT_SERVER_ID);
			originInfo.setOwnershipHandedOver(true);
			originInfo.setComment("first share");

			c.setSormasToSormasOriginInfo(originInfo);
		});

		// initial share by the creator of case
		SormasToSormasShareInfoDto shareToSecond = createShareInfoDto(officer, SECOND_SERVER_ID, true);
		// another share by the creator
		SormasToSormasShareInfoDto anotherShareFromDefault = createShareInfoDto(officer, "anotherShareFromDefault", false);

		// forwarded by the second system
		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareFromSecond = createShareRequestInfo(
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
		useSurveillanceOfficerLogin(rdcf);

		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, creator.createPerson().toReference(), rdcf);

		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
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

	private CaseDataDto createRemoteCaseDto(TestDataCreator.RDCF remoteRdcf, PersonDto person) {
		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setResponsibleRegion(remoteRdcf.region);
		caze.setResponsibleDistrict(remoteRdcf.district);
		caze.setResponsibleCommunity(remoteRdcf.community);
		caze.setHealthFacility(remoteRdcf.facility);
		caze.setFacilityType(FacilityType.HOSPITAL);
		return caze;
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
