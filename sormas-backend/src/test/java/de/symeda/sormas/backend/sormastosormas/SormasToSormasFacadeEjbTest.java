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
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.common.StartupShutdownService;

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

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();

		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		mockDefaultServerAccess();
	}

	@Test
	public void testSaveSharedCase() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);
		caze.getHospitalization().setAdmittedToHealthFacility(YesNoUnknown.YES);
		caze.getSymptoms().setAgitation(SymptomState.YES);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalContactType(AnimalContactType.TOUCH);
		caze.getEpiData().getExposures().add(exposure);
		caze.getClinicalCourse().getHealthConditions().setAsplenia(YesNoUnknown.YES);
		caze.getMaternalHistory().setChildrenNumber(2);

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

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

	/**
	 * Test that it doesnt throw de.symeda.sormas.api.utils.OutdatedEntityException
	 * To fix OutdatedEntityException generate new uuid for the outdated object in
	 * {@link de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb#processCaseData(CaseDataDto, PersonDto)}
	 */
	@Test
	public void testRecreateEmbeddedUuidsOfCase() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);

		caze.getHospitalization().getPreviousHospitalizations().add(PreviousHospitalizationDto.build(caze));
		caze.getEpiData().getExposures().add(ExposureDto.build(ExposureType.TRAVEL));

		byte[] encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));
		getSormasToSormasFacade().saveSharedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		caze.setUuid(DataHelper.createUuid());

		encryptedData = encryptShareData(new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo()));
		getSormasToSormasFacade().saveSharedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		assertThat(getCaseFacade().getCaseDataByUuid(caze.getUuid()), is(notNullValue()));
	}

	@Test
	public void testSaveSharedPointOfEntryCase() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF();

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
		getSormasToSormasFacade().saveSharedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getPointOfEntry(), is(rdcf.localRdcf.pointOfEntry));
		assertThat(savedCase.getPortHealthInfo().getAirlineName(), is("Test Airline"));
	}

	@Test
	public void testSaveSharedCaseWithContacts() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF();
		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = createRemoteCaseDto(rdcf, person);

		ContactDto contact = createRemoteContactDto(rdcf, caze);

		PersonDto contactPerson = createPersonDto(rdcf);
		contact.setPerson(contactPerson.toReference());

		SormasToSormasCaseDto shareData = new SormasToSormasCaseDto(person, caze, createSormasToSormasOriginInfo());
		shareData.setAssociatedContacts(Collections.singletonList(new SormasToSormasCaseDto.AssociatedContactDto(contactPerson, contact)));

		byte[] encryptedData = encryptShareData(shareData);
		getSormasToSormasFacade().saveSharedCase(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		ContactDto savedContact = getContactFacade().getContactByUuid(contact.getUuid());

		assertThat(savedContact, is(notNullValue()));
		assertThat(savedContact.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedContact.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedContact.getCommunity(), is(rdcf.localRdcf.community));

		assertThat(savedCase.getSormasToSormasOriginInfo().getUuid(), is(savedContact.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testSaveSharedContact() throws JsonProcessingException, SormasToSormasException {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null);
		contact.setPerson(person.toReference());
		contact.setRegion(rdcf.remoteRdcf.region);
		contact.setDistrict(rdcf.remoteRdcf.district);
		contact.setCommunity(rdcf.remoteRdcf.community);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalCondition(AnimalCondition.PROCESSED);
		contact.getEpiData().getExposures().add(exposure);

		contact.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo());

		byte[] encryptedData = encryptShareData(new SormasToSormasContactDto(person, contact));
		getSormasToSormasFacade().saveSharedContact(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

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

	/**
	 * Test that it doesnt throw de.symeda.sormas.api.utils.OutdatedEntityException
	 * To fix OutdatedEntityException generate new uuid for the outdated object in
	 * {@link de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb#processContactData(ContactDto, PersonDto)}
	 */
	@Test
	public void testRecreateEmbeddedUuidsOfContact() throws JsonProcessingException, SormasToSormasException {
		useNationalUserLogin();
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null);
		contact.setPerson(person.toReference());
		contact.setRegion(rdcf.remoteRdcf.region);
		contact.setDistrict(rdcf.remoteRdcf.district);
		contact.setCommunity(rdcf.remoteRdcf.community);

		contact.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo());
		byte[] encryptedData = encryptShareData(new SormasToSormasContactDto(person, contact));

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		contact.setUuid(DataHelper.createUuid());
		encryptedData = encryptShareData(new SormasToSormasContactDto(person, contact));

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ACCESS_CN, encryptedData));

		assertThat(getContactFacade().getContactByUuid(contact.getUuid()), is(notNullValue()));
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
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/case"));

				String authToken = invocation.getArgument(2, String.class);
				assertThat(authToken, startsWith("Basic "));
				String credentials = new String(Base64.getDecoder().decode(authToken.replace("Basic ", "")), StandardCharsets.UTF_8);
				// uses password from server-list.csv from `serveraccessdefault` package
				assertThat(credentials, is(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + SECOND_SERVER_REST_PASSWORD));

				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				assertThat(encryptedData.getOrganizationId(), is(DEFAULT_SERVER_ACCESS_CN));

				SormasToSormasCaseDto sharedCase = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto.class);

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

		getSormasToSormasFacade().shareCase(caze.getUuid(), options);

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
				SormasToSormasCaseDto sharedCase = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto.class);

				assertThat(sharedCase.getAssociatedContacts().size(), is(1));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCase(caze.getUuid(), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);

		SormasToSormasShareInfoDto contactShareInfo =
			shareInfoList.stream().filter(i -> DataHelper.isSame(i.getContact(), contact)).findFirst().get();
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
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/contact"));

				String authToken = invocation.getArgument(2, String.class);
				assertThat(authToken, startsWith("Basic "));
				String credentials = new String(Base64.getDecoder().decode(authToken.replace("Basic ", "")), StandardCharsets.UTF_8);
				// uses password from server-list.csv from `serveraccessdefault` package
				assertThat(credentials, is(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + SECOND_SERVER_REST_PASSWORD));

				SormasToSormasEncryptedDataDto encryptedData = invocation.getArgument(3, SormasToSormasEncryptedDataDto.class);
				SormasToSormasContactDto sharedContact = decryptSharesData(encryptedData.getData(), SormasToSormasContactDto.class);

				assertThat(sharedContact.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedContact.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedContact.getContact().getUuid(), is(contact.getUuid()));
				// users should be cleaned up
				assertThat(sharedContact.getContact().getReportingUser(), is(nullValue()));
				assertThat(sharedContact.getContact().getContactOfficer(), is(nullValue()));
				assertThat(sharedContact.getContact().getResultingCaseUser(), is(nullValue()));

				// share information
				assertThat(sharedContact.getContact().getSormasToSormasOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ACCESS_CN));
				assertThat(sharedContact.getContact().getSormasToSormasOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(sharedContact.getContact().getSormasToSormasOriginInfo().getComment(), is("Test comment"));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareContact(contact.getUuid(), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);
		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTarget().getUuid(), is(SECOND_SERVER_ACCESS_CN));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
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
				SormasToSormasCaseDto sharedCase = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto.class);

				assertThat(sharedCase.getPerson().getFirstName(), is("Confidential"));
				assertThat(sharedCase.getPerson().getLastName(), is("Confidential"));
				assertThat(sharedCase.getCaze().getAdditionalDetails(), is("Test additional details"));

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCase(caze.getUuid(), options);
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
				SormasToSormasCaseDto sharedCase = decryptSharesData(encryptedData.getData(), SormasToSormasCaseDto.class);

				assertThat(sharedCase.getPerson().getFirstName(), is("Confidential"));
				assertThat(sharedCase.getPerson().getLastName(), is("Confidential"));
				assertThat(sharedCase.getCaze().getAdditionalDetails(), isEmptyString());

				return Response.noContent().build();
			});

		getSormasToSormasFacade().shareCase(caze.getUuid(), options);
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

	private MappableRdcf createRDCF() {
		String regionName = "Region";
		String districtName = "District";
		String communityName = "Community";
		String facilityName = "Facility";
		String pointOfEntryName = "Point of Entry";

		MappableRdcf rdcf = new MappableRdcf();
		rdcf.remoteRdcf = new RDCF(
			new RegionReferenceDto(DataHelper.createUuid(), regionName),
			new DistrictReferenceDto(DataHelper.createUuid(), districtName),
			new CommunityReferenceDto(DataHelper.createUuid(), communityName),
			new FacilityReferenceDto(DataHelper.createUuid(), facilityName),
			new PointOfEntryReferenceDto(DataHelper.createUuid(), pointOfEntryName));
		rdcf.localRdcf = creator.createRDCF(regionName, districtName, communityName, facilityName, pointOfEntryName);

		return rdcf;
	}

	private CaseDataDto createRemoteCaseDto(MappableRdcf rdcf, PersonDto person) {
		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setHealthFacility(rdcf.remoteRdcf.facility);
		caze.setFacilityType(FacilityType.HOSPITAL);
		return caze;
	}

	private ContactDto createRemoteContactDto(MappableRdcf rdcf, CaseDataDto caze) {
		ContactDto contact = ContactDto.build(caze);
		contact.setRegion(rdcf.remoteRdcf.region);
		contact.setDistrict(rdcf.remoteRdcf.district);
		contact.setCommunity(rdcf.remoteRdcf.community);
		return contact;
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

		byte[] data = objectMapper.writeValueAsBytes(shareData);
		byte[] encryptedData = getSormasToSormasEncryptionService().encrypt(data, SECOND_SERVER_ACCESS_CN);

		mockSecondServerAccess();

		return encryptedData;
	}

	private <T> T decryptSharesData(byte[] data, Class<T> dataType) throws SormasToSormasException, IOException {
		mockSecondServerAccess();

		byte[] decryptData = getSormasToSormasEncryptionService().decrypt(data, DEFAULT_SERVER_ACCESS_CN);
		T parsedData = objectMapper.readValue(decryptData, dataType);

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
