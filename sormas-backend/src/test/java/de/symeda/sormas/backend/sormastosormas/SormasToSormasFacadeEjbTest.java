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

package de.symeda.sormas.backend.sormastosormas;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.SormasToSormasConfig;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.common.StartupShutdownService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@RunWith(MockitoJUnitRunner.class)
public class SormasToSormasFacadeEjbTest extends AbstractBeanTest {

	// values are set in server-list.csv located in serveraccessdefault and serveraccesssecond
	public static final String DEFAULT_SERVER_ACCESS_CN = "default";
	public static final String DEFAULT_SERVER_ACCESS_DATA_CSV = "default-server-access-data.csv";
	public static final String SECOND_SERVER_ACCESS_CN = "second";
	public static final String SECOND_SERVER_ACCESS_DATA_CSV = "second-server-access-data.csv";
	public static final String SECOND_SERVER_REST_URL = "second.sormas.com";
	public static final String SECOND_SERVER_REST_PASSWORD = "RestPasswoRdish";

	private ObjectMapper objectMapper;

	@Override
	public void init() {
		super.init();

		getFacilityService().createConstantFacilities();
		getPointOfEntryService().createConstantPointsOfEntry();
	}

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();

		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		mockDefaultServerAccess();
	}

	@Test
	public void testSaveSharedCaseWithInfrastructureName()
		throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);

		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createRemoteCaseDto(rdcf.remoteRdcf, person);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalContactType(AnimalContactType.TOUCH);
		caze.getEpiData().getExposures().add(exposure);
		caze.getClinicalCourse().getHealthConditions().setAsplenia(YesNoUnknown.YES);
		caze.getMaternalHistory().setChildrenNumber(2);

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));

		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.localRdcf.facility));
		assertThat(savedCase.getHospitalization().getAdmittedToHealthFacility(), is(YesNoUnknown.YES));
		assertThat(savedCase.getSymptoms().getAgitation(), is(SymptomState.YES));
		assertThat(savedCase.getEpiData().getExposures().get(0).getAnimalContactType(), is(AnimalContactType.TOUCH));
		assertThat(savedCase.getClinicalCourse().getHealthConditions().getAsplenia(), is(YesNoUnknown.YES));
		assertThat(savedCase.getMaternalHistory().getChildrenNumber(), is(2));

		assertThat(savedCase.getSormasToSormasOriginInfo().getOrganizationId(), is("testHealthDep"));
		assertThat(savedCase.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(savedCase.getPerson().getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedPerson.getFirstName(), is("James"));
		assertThat(savedPerson.getLastName(), is("Smith"));
	}

	@Test
	public void testSaveSharedCaseWithInfrastructureExternalId()
		throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(true);

		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createRemoteCaseDto(rdcf.remoteRdcf, person);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalContactType(AnimalContactType.TOUCH);
		caze.getEpiData().getExposures().add(exposure);
		caze.getClinicalCourse().getHealthConditions().setAsplenia(YesNoUnknown.YES);
		caze.getMaternalHistory().setChildrenNumber(2);

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));

		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.localRdcf.facility));
		assertThat(savedCase.getHospitalization().getAdmittedToHealthFacility(), is(YesNoUnknown.YES));
		assertThat(savedCase.getSymptoms().getAgitation(), is(SymptomState.YES));
		assertThat(savedCase.getEpiData().getExposures().get(0).getAnimalContactType(), is(AnimalContactType.TOUCH));
		assertThat(savedCase.getClinicalCourse().getHealthConditions().getAsplenia(), is(YesNoUnknown.YES));
		assertThat(savedCase.getMaternalHistory().getChildrenNumber(), is(2));

		assertThat(savedCase.getSormasToSormasOriginInfo().getOrganizationId(), is("testHealthDep"));
		assertThat(savedCase.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(savedCase.getPerson().getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedPerson.getFirstName(), is("James"));
		assertThat(savedPerson.getLastName(), is("Smith"));
	}

	@Test
	public void testSaveSharedPointOfEntryCaseWithInfrastructureName()
		throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setPointOfEntry(rdcf.remoteRdcf.pointOfEntry);
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		portHealthInfo.setAirlineName("Test Airline");
		caze.setPortHealthInfo(portHealthInfo);

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));
		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getPointOfEntry(), is(rdcf.localRdcf.pointOfEntry));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveSharedPointOfEntryCaseWithInfrastructureExternalId()
		throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(true);

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setPointOfEntry(rdcf.remoteRdcf.pointOfEntry);
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		portHealthInfo.setAirlineName("Test Airline");
		caze.setPortHealthInfo(portHealthInfo);

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));
		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getPointOfEntry(), is(rdcf.localRdcf.pointOfEntry));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveSharedCaseWithContacts() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = createRemoteCaseDto(rdcf.remoteRdcf, person);

		ContactDto contact = createRemoteContactDto(rdcf.remoteRdcf, caze);

		PersonDto contactPerson = createPersonDto(rdcf);
		contact.setPerson(contactPerson.toReference());

		SormasToSormasCaseDto shareData = new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo());
		shareData.setAssociatedContacts(Collections.singletonList(new SormasToSormasCaseDto.AssociatedContactDto(contactPerson, contact)));

		byte[] encryptedData = encryptShareData(shareData);
		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		ContactDto savedContact = getContactFacade().getContactByUuid(contact.getUuid());

		assertThat(savedContact, is(notNullValue()));
		assertThat(savedContact.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedContact.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedContact.getCommunity(), is(rdcf.localRdcf.community));

		assertThat(savedCase.getSormasToSormasOriginInfo().getUuid(), is(savedContact.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testSaveSharedCaseWithSamples() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = createRemoteCaseDto(rdcf.remoteRdcf, person);
		SormasToSormasSampleDto sample = createRemoteSampleDtoWithTests(rdcf.remoteRdcf, caze.toReference(), null);

		SormasToSormasCaseDto shareData = new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo());
		shareData.setSamples(Collections.singletonList(sample));

		byte[] encryptedData = encryptShareData(shareData);
		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		SampleDto savedSample = getSampleFacade().getSampleByUuid(sample.getSample().getUuid());

		assertThat(savedSample, is(notNullValue()));
		assertThat(savedSample.getLab(), is(rdcf.localRdcf.facility));

		assertThat(getPathogenTestFacade().getAllBySample(savedSample.toReference()), hasSize(1));
		assertThat(getAdditionalTestFacade().getAllBySample(savedSample.getUuid()), hasSize(1));

		assertThat(savedCase.getSormasToSormasOriginInfo().getUuid(), is(savedSample.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testSaveSharedContact() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);

		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		ContactDto contact = createRemoteContactDto(rdcf.remoteRdcf, person);

		byte[] encryptedData = encryptShareData(new SormasToSormasContactDto(person, contact, createSormasToSormasOriginInfo()));
		getSormasToSormasFacade().saveSharedContacts(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		ContactDto savedContact = getContactFacade().getContactByUuid(contact.getUuid());

		assertThat(savedContact, is(notNullValue()));
		assertThat(savedContact.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedContact.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedContact.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedContact.getEpiData().getExposures().get(0).getAnimalCondition(), is(AnimalCondition.PROCESSED));

		assertThat(savedContact.getSormasToSormasOriginInfo().getOrganizationId(), is("testHealthDep"));
		assertThat(savedContact.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(savedContact.getPerson().getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedPerson.getFirstName(), is("James"));
		assertThat(savedPerson.getLastName(), is("Smith"));

	}

	@Test
	public void testSaveSharedContactWithSamples() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);
		PersonDto person = createPersonDto(rdcf);

		ContactDto contact = createRemoteContactDto(rdcf.remoteRdcf, person);
		SormasToSormasSampleDto sample = createRemoteSampleDtoWithTests(rdcf.remoteRdcf, null, contact.toReference());

		SormasToSormasContactDto shareData = new SormasToSormasContactDto(person, contact, createSormasToSormasOriginInfo());
		shareData.setSamples(Collections.singletonList(sample));

		byte[] encryptedData = encryptShareData(shareData);
		getSormasToSormasFacade().saveSharedContacts(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		ContactDto savedContact = getContactFacade().getContactByUuid(contact.getUuid());
		SampleDto savedSample = getSampleFacade().getSampleByUuid(sample.getSample().getUuid());

		assertThat(savedSample, is(notNullValue()));
		assertThat(savedSample.getLab(), is(rdcf.localRdcf.facility));

		assertThat(getPathogenTestFacade().getAllBySample(savedSample.toReference()), hasSize(1));
		assertThat(getAdditionalTestFacade().getAllBySample(savedSample.getUuid()), hasSize(1));

		assertThat(savedContact.getSormasToSormasOriginInfo().getUuid(), is(savedSample.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testShareCase() throws SormasToSormasException, JsonProcessingException, NoSuchAlgorithmException, KeyManagementException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setComment("Test comment");

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_REST_URL));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases"));

				String authToken = invocation.getArgument(2, String.class);
				assertThat(authToken, startsWith("Basic "));
				String credentials = new String(Base64.getDecoder().decode(authToken.replace("Basic ", "")), StandardCharsets.UTF_8);
				// uses password from server-list.csv from `serveraccessdefault` package
				assertThat(credentials, is(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + SECOND_SERVER_REST_PASSWORD));

				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				assertThat(encryptedData.getOrganizationId(), is(DEFAULT_SERVER_ACCESS_CN));

				SormasToSormasCaseDto[] sharedCases = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto[].class);
				SormasToSormasCaseDto sharedCase = sharedCases[0];

				assertThat(sharedCase.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedCase.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedCase.getCaze().getUuid(), is(caze.getUuid()));
				// users should be cleaned up
				assertThat(sharedCase.getCaze().getReportingUser(), is(nullValue()));
				assertThat(sharedCase.getCaze().getSurveillanceOfficer(), is(nullValue()));
				assertThat(sharedCase.getCaze().getClassificationUser(), is(nullValue()));

				// share information
				assertThat(sharedCase.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ACCESS_CN));
				assertThat(sharedCase.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(sharedCase.getOriginInfo().getComment(), is("Test comment"));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCases(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTarget().getUuid(), is(SECOND_SERVER_ACCESS_CN));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithContacts()
		throws SormasToSormasException, JsonProcessingException, NoSuchAlgorithmException, KeyManagementException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
		});

		ContactDto contact = creator.createContact(officer, creator.createPerson().toReference(), caze);

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setComment("Test comment");
		options.setWithAssociatedContacts(true);

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				SormasToSormasCaseDto[] sharedCases = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto[].class);

				assertThat(sharedCases[0].getAssociatedContacts().size(), is(1));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCases(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo =
			shareInfoList.stream().filter(i -> DataHelper.isSame(i.getContact(), contact)).findFirst().get();
		assertThat(contactShareInfo.getTarget().getUuid(), is(SECOND_SERVER_ACCESS_CN));
		assertThat(contactShareInfo.getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(contactShareInfo.getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithSamples() throws SormasToSormasException, JsonProcessingException, NoSuchAlgorithmException, KeyManagementException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER);
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
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setComment("Test comment");
		options.setWithSamples(true);
		options.setWithAssociatedContacts(true);

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				SormasToSormasCaseDto[] sharedCases = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto[].class);

				assertThat(sharedCases[0].getSamples().size(), is(2));

				SormasToSormasSampleDto sharedCaseSample = sharedCases[0].getSamples().get(0);
				assertThat(sharedCaseSample.getSample().getSampleDateTime(), is(sampleDateTime));
				assertThat(sharedCaseSample.getSample().getComment(), is("Test case sample"));
				assertThat(sharedCaseSample.getPathogenTests(), hasSize(1));
				assertThat(sharedCaseSample.getAdditionalTests(), hasSize(1));

				SormasToSormasSampleDto sharedContactSample = sharedCases[0].getSamples().get(1);
				assertThat(sharedContactSample.getSample().getSampleDateTime(), is(sampleDateTime));
				assertThat(sharedContactSample.getSample().getComment(), is("Test contact sample"));
				assertThat(sharedContactSample.getPathogenTests(), hasSize(1));
				assertThat(sharedContactSample.getAdditionalTests(), hasSize(1));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCases(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().sample(caseSample.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo =
			shareInfoList.stream().filter(i -> DataHelper.isSame(i.getSample(), caseSample)).findFirst().get();
		assertThat(contactShareInfo.getTarget().getUuid(), is(SECOND_SERVER_ACCESS_CN));
		assertThat(contactShareInfo.getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(contactShareInfo.getComment(), is("Test comment"));
	}

	@Test
	public void testShareContact() throws SormasToSormasException, JsonProcessingException, NoSuchAlgorithmException, KeyManagementException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		ContactDto contact = creator.createContact(officer, officer, person.toReference(), null, new Date(), null, null, rdcf, dto -> {
			dto.setResultingCaseUser(officer);
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setComment("Test comment");

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_REST_URL));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/contacts"));

				String authToken = invocation.getArgument(2, String.class);
				assertThat(authToken, startsWith("Basic "));
				String credentials = new String(Base64.getDecoder().decode(authToken.replace("Basic ", "")), StandardCharsets.UTF_8);
				// uses password from server-list.csv from `serveraccessdefault` package
				assertThat(credentials, is(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + SECOND_SERVER_REST_PASSWORD));

				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				SormasToSormasContactDto[] sharedContacts = decryptSharesData(encryptedData.getData(), SormasToSormasContactDto[].class);
				SormasToSormasContactDto sharedContact = sharedContacts[0];

				assertThat(sharedContact.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedContact.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedContact.getContact().getUuid(), is(contact.getUuid()));
				// users should be cleaned up
				assertThat(sharedContact.getContact().getReportingUser(), is(nullValue()));
				assertThat(sharedContact.getContact().getContactOfficer(), is(nullValue()));
				assertThat(sharedContact.getContact().getResultingCaseUser(), is(nullValue()));

				// share information
				assertThat(sharedContact.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ACCESS_CN));
				assertThat(sharedContact.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(sharedContact.getOriginInfo().getComment(), is("Test comment"));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareContacts(Collections.singletonList(contact.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);
		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTarget().getUuid(), is(SECOND_SERVER_ACCESS_CN));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareContactWithSamples()
		throws SormasToSormasException, JsonProcessingException, NoSuchAlgorithmException, KeyManagementException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		ContactDto contact = creator.createContact(officer, officer, person.toReference(), null, new Date(), null, null, rdcf);

		Date sampleDateTime = new Date();
		SampleDto sample = creator.createSample(contact.toReference(), officer, rdcf.facility, s -> {
			s.setSampleDateTime(sampleDateTime);
			s.setComment("Test sample");
		});
		creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.RAPID_TEST,
			Disease.CORONAVIRUS,
			sampleDateTime,
			rdcf.facility,
			officer,
			PathogenTestResultType.INDETERMINATE,
			"result",
			true,
			null);
		creator.createAdditionalTest(sample.toReference());

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setComment("Test comment");
		options.setWithSamples(true);

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				SormasToSormasContactDto[] sharedContacts = decryptSharesData(encryptedData.getData(), SormasToSormasContactDto[].class);

				assertThat(sharedContacts[0].getSamples().size(), is(1));

				SormasToSormasSampleDto sharedSample = sharedContacts[0].getSamples().get(0);
				assertThat(sharedSample.getSample().getSampleDateTime(), is(sampleDateTime));
				assertThat(sharedSample.getSample().getComment(), is("Test sample"));
				assertThat(sharedSample.getPathogenTests(), hasSize(1));
				assertThat(sharedSample.getAdditionalTests(), hasSize(1));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareContacts(Collections.singletonList(contact.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().sample(sample.toReference()), 0, 100);

		SormasToSormasShareInfoDto sampleShareInfoList =
			shareInfoList.stream().filter(i -> DataHelper.isSame(i.getSample(), sample)).findFirst().get();
		assertThat(sampleShareInfoList.getTarget().getUuid(), is(SECOND_SERVER_ACCESS_CN));
		assertThat(sampleShareInfoList.getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(sampleShareInfoList.getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithPseudonymizePersonalData()
		throws SormasToSormasException, JsonProcessingException, NoSuchAlgorithmException, KeyManagementException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setAdditionalDetails("Test additional details");
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setPseudonymizePersonalData(true);

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				SormasToSormasCaseDto[] sharedCases = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto[].class);
				SormasToSormasCaseDto sharedCase = sharedCases[0];

				assertThat(sharedCase.getPerson().getFirstName(), is("Confidential"));
				assertThat(sharedCase.getPerson().getLastName(), is("Confidential"));
				assertThat(sharedCase.getCaze().getAdditionalDetails(), is("Test additional details"));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCases(Collections.singletonList(caze.getUuid()), options);
	}

	@Test
	public void testShareCaseWithPseudonymizeSensitiveData()
		throws SormasToSormasException, JsonProcessingException, NoSuchAlgorithmException, KeyManagementException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);

			dto.setAdditionalDetails("Test additional details");
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setPseudonymizeSensitiveData(true);

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				SormasToSormasCaseDto[] sharedCases = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto[].class);
				SormasToSormasCaseDto sharedCase = sharedCases[0];

				assertThat(sharedCase.getPerson().getFirstName(), is("Confidential"));
				assertThat(sharedCase.getPerson().getLastName(), is("Confidential"));
				assertThat(sharedCase.getCaze().getAdditionalDetails(), isEmptyString());

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCases(Collections.singletonList(caze.getUuid()), options);
	}

	@Test
	public void testReturnCase() throws JsonProcessingException, SormasToSormasException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);

			SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
			originInfo.setSenderName("Test Name");
			originInfo.setSenderEmail("test@email.com");
			originInfo.setOrganizationId(DEFAULT_SERVER_ACCESS_CN);
			originInfo.setOwnershipHandedOver(true);
			dto.setSormasToSormasOriginInfo(originInfo);
		});

		ContactDto sharedContact = creator
			.createContact(officer, officer, creator.createPerson().toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf, c -> {
				c.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo());
			});
		ContactDto newContact = creator.createContact(officer, creator.createPerson().toReference(), caze);
		SampleDto sharedSample = creator.createSample(caze.toReference(), officer, rdcf.facility, s -> {
			s.setSormasToSormasOriginInfo(caze.getSormasToSormasOriginInfo());
		});

		SampleDto newSample = creator.createSample(caze.toReference(), officer, rdcf.facility);

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setHandOverOwnership(true);
		options.setWithAssociatedContacts(true);
		options.setWithSamples(true);
		options.setComment("Test comment");

		Mockito.when(MockProducer.getSormasToSormasClient().put(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> Response.noContent().build());

		getSormasToSormasFacade().returnCase(caze.getUuid(), options);

		// case ownership should be lost
		CaseDataDto sharedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(sharedCase.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// contact ownership should be lost
		sharedContact = getContactFacade().getContactByUuid(sharedContact.getUuid());
		assertThat(sharedContact.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// new contacts should have share info with ownership handed over
		List<SormasToSormasShareInfoDto> newContactShareInfos =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(newContact.toReference()), 0, 100);
		assertThat(newContactShareInfos, hasSize(1));
		assertThat(newContactShareInfos.get(0).isOwnershipHandedOver(), is(true));

		// sample ownership should be lost
		sharedSample = getSampleFacade().getSampleByUuid(sharedSample.getUuid());
		assertThat(sharedSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// new samples should have share info with ownership handed over
		List<SormasToSormasShareInfoDto> newSampleShareInfos =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().sample(newSample.toReference()), 0, 100);
		assertThat(newSampleShareInfos, hasSize(1));
		assertThat(newSampleShareInfos.get(0).isOwnershipHandedOver(), is(true));
	}

	@Test
	public void testReturnContact() throws JsonProcessingException, SormasToSormasException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();

		ContactDto contact =
			creator.createContact(officer, officer, person.toReference(), null, new Date(), new Date(), Disease.CORONAVIRUS, rdcf, c -> {
				SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
				originInfo.setSenderName("Test Name");
				originInfo.setSenderEmail("test@email.com");
				originInfo.setOrganizationId(DEFAULT_SERVER_ACCESS_CN);
				originInfo.setOwnershipHandedOver(true);

				c.setSormasToSormasOriginInfo(originInfo);
			});

		SampleDto sharedSample = creator.createSample(contact.toReference(), officer, rdcf.facility, s -> {
			s.setSormasToSormasOriginInfo(contact.getSormasToSormasOriginInfo());
		});

		SampleDto newSample = creator.createSample(contact.toReference(), officer, rdcf.facility, null);

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new ServerAccessDataReferenceDto(SECOND_SERVER_ACCESS_CN));
		options.setHandOverOwnership(true);
		options.setWithSamples(true);
		options.setComment("Test comment");

		Mockito.when(MockProducer.getSormasToSormasClient().put(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> Response.noContent().build());

		getSormasToSormasFacade().returnContact(contact.getUuid(), options);

		// contact ownership should be lost
		ContactDto sharedContact = getContactFacade().getContactByUuid(contact.getUuid());
		assertThat(sharedContact.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// sample ownership should be lost
		sharedSample = getSampleFacade().getSampleByUuid(sharedSample.getUuid());
		assertThat(sharedSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// new samples should have share info with ownership handed over
		List<SormasToSormasShareInfoDto> newSampleShareInfos =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().sample(newSample.toReference()), 0, 100);
		assertThat(newSampleShareInfos, hasSize(1));
		assertThat(newSampleShareInfos.get(0).isOwnershipHandedOver(), is(true));
	}

	@Test
	public void testSaveReturnedCase() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF(false);

		UserReferenceDto officer = creator.createUser(rdcf.localRdcf, UserRole.SURVEILLANCE_OFFICER).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, person.toReference(), rdcf.localRdcf);

		PersonDto sharedContactPerson = creator.createPerson();
		ContactDto sharedContact = creator.createContact(officer, sharedContactPerson.toReference(), caze);
		PersonDto newContactPerson = creator.createPerson();
		ContactDto newContact = creator.createContact(officer, newContactPerson.toReference(), caze);
		ContactDto newContact2 = creator.createContact(officer, newContactPerson.toReference(), caze);
		SampleDto sharedSample = creator.createSample(caze.toReference(), officer, rdcf.localRdcf.facility);
		SampleDto newSample = creator.createSample(caze.toReference(), officer, rdcf.localRdcf.facility);
		SampleDto newSample2 = creator.createSample(caze.toReference(), officer, rdcf.localRdcf.facility);

		User officerUser = getUserService().getByReferenceDto(officer);
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(officerUser, i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference()))));
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(officerUser, i -> i.setContact(getContactService().getByReferenceDto(sharedContact.toReference()))));
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(officerUser, i -> i.setSample(getSampleService().getByReferenceDto(sharedSample.toReference()))));

		caze.setQuarantine(QuarantineType.HOTEL);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo();
		originInfo.setOwnershipHandedOver(true);

		SormasToSormasCaseDto shareData = new SormasToSormasCaseDto(person, caze, originInfo);
		shareData.setAssociatedContacts(
			Arrays.asList(
				new SormasToSormasCaseDto.AssociatedContactDto(sharedContactPerson, sharedContact),
				new SormasToSormasCaseDto.AssociatedContactDto(newContactPerson, newContact),
				new SormasToSormasCaseDto.AssociatedContactDto(newContactPerson, newContact2)));
		shareData.setSamples(
			Arrays.asList(
				new SormasToSormasSampleDto(sharedSample, Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample, Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample2, Collections.emptyList(), Collections.emptyList())));

		byte[] encryptedData = encryptShareData(shareData);

		try {
			getSormasToSormasFacade().saveReturnedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));
		} catch (Exception e) {
			e.printStackTrace();
		}

		CaseDataDto returnedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(returnedCase.getQuarantine(), is(QuarantineType.HOTEL));

		List<SormasToSormasShareInfoDto> caseShares =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);
		assertThat(caseShares.get(0).isOwnershipHandedOver(), is(false));

		List<SormasToSormasShareInfoDto> contactShares =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(sharedContact.toReference()), 0, 100);
		assertThat(contactShares.get(0).isOwnershipHandedOver(), is(false));

		ContactDto returnedNewContact = getContactFacade().getContactByUuid(newContact.getUuid());
		assertThat(returnedNewContact.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));

		ContactDto returnedNewContact2 = getContactFacade().getContactByUuid(newContact.getUuid());
		assertThat(returnedNewContact2.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));

		List<SormasToSormasShareInfoDto> sampleShares =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().sample(sharedSample.toReference()), 0, 100);
		assertThat(sampleShares.get(0).isOwnershipHandedOver(), is(false));

		SampleDto returnedNewSample = getSampleFacade().getSampleByUuid(newSample.getUuid());
		assertThat(returnedNewSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));

		SampleDto returnedNewSample2 = getSampleFacade().getSampleByUuid(newSample.getUuid());
		assertThat(returnedNewSample2.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));
	}

	@Test
	public void testSaveReturnedContact() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);

		UserReferenceDto officer = creator.createUser(rdcf.localRdcf, UserRole.SURVEILLANCE_OFFICER).toReference();

		PersonDto contactPerson = creator.createPerson();
		ContactDto contact = creator.createContact(officer, contactPerson.toReference());
		SampleDto sharedSample = creator.createSample(contact.toReference(), officer, rdcf.localRdcf.facility, null);
		SampleDto newSample = creator.createSample(contact.toReference(), officer, rdcf.localRdcf.facility, null);

		User officerUser = getUserService().getByReferenceDto(officer);
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(officerUser, i -> i.setContact(getContactService().getByReferenceDto(contact.toReference()))));
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(officerUser, i -> i.setSample(getSampleService().getByReferenceDto(sharedSample.toReference()))));

		contact.setQuarantine(QuarantineType.HOTEL);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(contact.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		contact.setChangeDate(calendar.getTime());

		SormasToSormasContactDto shareData = new SormasToSormasContactDto(contactPerson, contact, createSormasToSormasOriginInfo());
		shareData.setSamples(
			Arrays.asList(
				new SormasToSormasSampleDto(sharedSample, Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample, Collections.emptyList(), Collections.emptyList())));

		byte[] encryptedData = encryptShareData(shareData);

		getSormasToSormasFacade().saveReturnedContact(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		ContactDto returnedContact = getContactFacade().getContactByUuid(contact.getUuid());
		assertThat(returnedContact.getQuarantine(), is(QuarantineType.HOTEL));

		List<SormasToSormasShareInfoDto> contactShares =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);
		assertThat(contactShares.get(0).isOwnershipHandedOver(), is(false));

		List<SormasToSormasShareInfoDto> sampleShares =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().sample(sharedSample.toReference()), 0, 100);
		assertThat(sampleShares.get(0).isOwnershipHandedOver(), is(false));

		SampleDto returnedNewSample = getSampleFacade().getSampleByUuid(newSample.getUuid());
		assertThat(returnedNewSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));
	}

	@Test
	public void testSaveSharedCaseWithUnknownFacility() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);

		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createRemoteCaseDto(rdcf.remoteRdcf, person);
		caze.setHealthFacility(new FacilityReferenceDto("unknown", "Unknown facility", "unknown"));

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));

		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getHealthFacility().getUuid(), is(FacilityDto.OTHER_FACILITY_UUID));
		assertThat(savedCase.getHealthFacilityDetails(), is("Unknown facility"));
	}

	@Test
	public void testSaveSharedWithUnknownPoint() throws JsonProcessingException, SormasToSormasException, SormasToSormasValidationException {
		MappableRdcf rdcf = createRDCF(false);

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setPointOfEntry(new PointOfEntryReferenceDto("unknown", "Unknown POE", PointOfEntryType.AIRPORT, null));
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		portHealthInfo.setAirlineName("Test Airline");
		caze.setPortHealthInfo(portHealthInfo);

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));
		getSormasToSormasFacade().saveSharedCases(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getPointOfEntry().getUuid(), is(PointOfEntryDto.OTHER_AIRPORT_UUID));
		assertThat(savedCase.getPointOfEntryDetails(), is("Unknown POE"));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveReturnedCaseWithKnownOtherFacility() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF(false);

		UserReferenceDto officer = creator.createUser(rdcf.localRdcf, UserRole.SURVEILLANCE_OFFICER).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf.localRdcf, c -> {
			c.setPerson(person.toReference());
			c.setHealthFacility(getFacilityFacade().getByUuid(FacilityDto.OTHER_FACILITY_UUID).toReference());
			c.setHealthFacilityDetails("Test HF details");
		});

		User officerUser = getUserService().getByReferenceDto(officer);
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(officerUser, i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference()))));

		caze.setHealthFacilityDetails(rdcf.localRdcf.facility.getCaption());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo();
		originInfo.setOwnershipHandedOver(true);

		SormasToSormasCaseDto shareData = new SormasToSormasCaseDto(person, caze, originInfo);

		byte[] encryptedData = encryptShareData(shareData);

		try {
			getSormasToSormasFacade().saveReturnedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));
		} catch (Exception e) {
			e.printStackTrace();
		}

		CaseDataDto returnedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(returnedCase.getHealthFacility(), is(rdcf.localRdcf.facility));
		assertThat(returnedCase.getHealthFacilityDetails(), is(nullValue()));
	}

	@Test
	public void testSaveReturnedCaseWithKnownOtherPointOfEntry() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF(false);

		UserReferenceDto officer = creator.createUser(rdcf.localRdcf, UserRole.SURVEILLANCE_OFFICER).toReference();

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf.localRdcf, c -> {
			c.setPerson(person.toReference());
			c.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			c.setPointOfEntry(new PointOfEntryReferenceDto(PointOfEntryDto.OTHER_SEAPORT_UUID, null, null, null));
			c.setPointOfEntryDetails("Test Seaport");
		});

		User officerUser = getUserService().getByReferenceDto(officer);
		getSormasToSormasShareInfoService()
			.persist(createShareInfo(officerUser, i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference()))));

		caze.setPointOfEntryDetails(rdcf.localRdcf.pointOfEntry.getCaption());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caze.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		caze.setChangeDate(calendar.getTime());

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo();
		originInfo.setOwnershipHandedOver(true);

		SormasToSormasCaseDto shareData = new SormasToSormasCaseDto(person, caze, originInfo);

		byte[] encryptedData = encryptShareData(shareData);

		try {
			getSormasToSormasFacade().saveReturnedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));
		} catch (Exception e) {
			e.printStackTrace();
		}

		CaseDataDto returnedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		assertThat(returnedCase.getPointOfEntry(), is(rdcf.localRdcf.pointOfEntry));
		assertThat(returnedCase.getPointOfEntryDetails(), is(nullValue()));
	}

	private PersonDto createPersonDto(MappableRdcf rdcf) {
		PersonDto person = PersonDto.build();
		person.setFirstName("John");
		person.setLastName("Smith");

		person.getAddress().setDistrict(rdcf.remoteRdcf.district);
		person.getAddress().setRegion(rdcf.remoteRdcf.region);
		person.getAddress().setCommunity(rdcf.remoteRdcf.community);

		return person;
	}

	private SormasToSormasOriginInfoDto createSormasToSormasOriginInfo() {
		SormasToSormasOriginInfoDto source = new SormasToSormasOriginInfoDto();
		source.setOrganizationId("testHealthDep");
		source.setSenderName("John doe");

		return source;
	}

	private MappableRdcf createRDCF(boolean withExternalId) {

		String regionName = "Region";
		String districtName = "District";
		String communityName = "Community";
		String facilityName = "Facility";
		String pointOfEntryName = "Point of Entry";

		String regionExternalId = null;
		String districtExternalId = null;
		String communityExternalId = null;
		String facilityExternalId = null;
		String pointOfEntryExternalId = null;

		if (withExternalId) {
			regionExternalId = "RegionExtId";
			districtExternalId = "DistrictExtId";
			communityExternalId = "CommunityExtId";
			facilityExternalId = "FacilityExtId";
			pointOfEntryExternalId = "Point of EntryExtId";
		}

		MappableRdcf rdcf = new MappableRdcf();
		rdcf.remoteRdcf = new RDCF(
			new RegionReferenceDto(DataHelper.createUuid(), withExternalId ? null : regionName, regionExternalId),
			new DistrictReferenceDto(DataHelper.createUuid(), withExternalId ? null : districtName, districtExternalId),
			new CommunityReferenceDto(DataHelper.createUuid(), withExternalId ? null : communityName, communityExternalId),
			new FacilityReferenceDto(DataHelper.createUuid(), withExternalId ? null : facilityName, facilityExternalId),
			new PointOfEntryReferenceDto(
				DataHelper.createUuid(),
				withExternalId ? null : pointOfEntryName,
				PointOfEntryType.AIRPORT,
				pointOfEntryExternalId));

		Region region = creator.createRegion(regionName, regionExternalId);
		District district = creator.createDistrict(districtName, region, districtExternalId);
		Community community = creator.createCommunity(communityName, district, communityExternalId);
		Facility facility = creator.createFacility(facilityName, FacilityType.HOSPITAL, region, district, community, facilityExternalId);
		PointOfEntry pointOfEntry = creator.createPointOfEntry(pointOfEntryName, region, district, pointOfEntryExternalId);

		rdcf.localRdcf = new RDCF(
			new RegionReferenceDto(region.getUuid(), region.getName(), region.getExternalID()),
			new DistrictReferenceDto(district.getUuid(), district.getName(), district.getExternalID()),
			new CommunityReferenceDto(community.getUuid(), community.getName(), community.getExternalID()),
			new FacilityReferenceDto(facility.getUuid(), facility.getName(), facility.getExternalID()),
			new PointOfEntryReferenceDto(pointOfEntry.getUuid(), pointOfEntry.getName(), PointOfEntryType.AIRPORT, pointOfEntry.getExternalID()));

		return rdcf;
	}

	private CaseDataDto createRemoteCaseDto(RDCF remoteRdcf, PersonDto person) {
		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setRegion(remoteRdcf.region);
		caze.setDistrict(remoteRdcf.district);
		caze.setCommunity(remoteRdcf.community);
		caze.setHealthFacility(remoteRdcf.facility);
		caze.setFacilityType(FacilityType.HOSPITAL);
		return caze;
	}

	private ContactDto createRemoteContactDto(RDCF remoteRdcf, CaseDataDto caze) {
		ContactDto contact = ContactDto.build(caze);
		contact.setRegion(remoteRdcf.region);
		contact.setDistrict(remoteRdcf.district);
		contact.setCommunity(remoteRdcf.community);
		return contact;
	}

	private ContactDto createRemoteContactDto(RDCF remoteRdcf, PersonDto person) {
		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null);
		contact.setPerson(person.toReference());
		contact.setRegion(remoteRdcf.region);
		contact.setDistrict(remoteRdcf.district);
		contact.setCommunity(remoteRdcf.community);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalCondition(AnimalCondition.PROCESSED);
		contact.getEpiData().getExposures().add(exposure);
		return contact;
	}

	private SormasToSormasSampleDto createRemoteSampleDtoWithTests(RDCF rdcf, CaseReferenceDto caseRef, ContactReferenceDto contactRef) {
		UserReferenceDto userRef = UserDto.build().toReference();

		SampleDto sample;
		if (caseRef != null) {
			sample = SampleDto.build(userRef, caseRef);
		} else {
			sample = SampleDto.build(userRef, contactRef);
		}

		sample.setLab(rdcf.facility);
		sample.setSampleDateTime(new Date());
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSampleSource(SampleSource.HUMAN);
		sample.setSamplePurpose(SamplePurpose.INTERNAL);

		PathogenTestDto pathogenTest = PathogenTestDto.build(sample.toReference(), userRef);
		pathogenTest.setTestDateTime(new Date());
		pathogenTest.setTestResultVerified(true);
		pathogenTest.setTestType(PathogenTestType.RAPID_TEST);

		AdditionalTestDto additionalTest = AdditionalTestDto.build(sample.toReference());
		additionalTest.setTestDateTime(new Date());

		return new SormasToSormasSampleDto(sample, Collections.singletonList(pathogenTest), Collections.singletonList(additionalTest));
	}

	private SormasToSormasShareInfo createShareInfo(User sender, Consumer<SormasToSormasShareInfo> setTarget) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setOwnershipHandedOver(true);
		shareInfo.setOrganizationId("testHealthDep");
		shareInfo.setSender(sender);

		setTarget.accept(shareInfo);

		return shareInfo;
	}

	private void mockDefaultServerAccess() {

		File file = new File("src/test/java/de/symeda/sormas/backend/sormastosormas/serveraccessdefault");

		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getPath()).thenReturn(file.getAbsolutePath());
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getServerAccessDataFileName()).thenReturn(DEFAULT_SERVER_ACCESS_DATA_CSV);
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystoreName()).thenReturn("default.sormas2sormas.keystore.p12");
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystorePass()).thenReturn("certPass");
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststoreName()).thenReturn("sormas2sormas.truststore.p12");
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststorePass()).thenReturn("truster");
	}

	private void mockSecondServerAccess() {
		File file = new File("src/test/java/de/symeda/sormas/backend/sormastosormas/serveraccesssecond");

		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getPath()).thenReturn(file.getAbsolutePath());
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getServerAccessDataFileName()).thenReturn(SECOND_SERVER_ACCESS_DATA_CSV);
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystoreName()).thenReturn("second.sormas2sormas.keystore.p12");
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getKeystorePass()).thenReturn("certiPass");
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststoreName()).thenReturn("sormas2sormas.truststore.p12");
		Mockito.when(MockSormasToSormasConfigProducer.sormasToSormasConfig.getTruststorePass()).thenReturn("trusteR");
	}

	private byte[] encryptShareData(Object shareData) throws JsonProcessingException, SormasToSormasException {
		mockDefaultServerAccess();

		byte[] data = objectMapper.writeValueAsBytes(Collections.singletonList(shareData));
		byte[] encryptedData = getSormasToSormasEncryptionService().encrypt(data, SECOND_SERVER_ACCESS_CN);

		mockSecondServerAccess();

		return encryptedData;
	}

	private <T> T[] decryptSharesData(byte[] data, Class<T[]> dataType) throws SormasToSormasException, IOException {
		mockSecondServerAccess();

		byte[] decryptData = getSormasToSormasEncryptionService().decrypt(data, DEFAULT_SERVER_ACCESS_CN);
		T[] parsedData = objectMapper.readValue(decryptData, dataType);

		mockDefaultServerAccess();

		return parsedData;
	}

	private static class MappableRdcf {

		private RDCF remoteRdcf;
		private RDCF localRdcf;
	}

	@Specializes
	private static class MockSormasToSormasConfigProducer extends SormasToSormasConfigProducer {

		static SormasToSormasConfig sormasToSormasConfig = mock(SormasToSormasConfig.class);

		@Override
		@Produces
		public SormasToSormasConfig sormas2SormasConfig() {
			return sormasToSormasConfig;
		}
	}
}
