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

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sormastosormas.HealthDepartmentServerAccessData;
import de.symeda.sormas.api.sormastosormas.HealthDepartmentServerReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.common.StartupShutdownService;

@RunWith(MockitoJUnitRunner.class)
public class SormasToSormasFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testSaveSharedCase() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setHealthFacility(rdcf.remoteRdcf.facility);
		caze.setFacilityType(FacilityType.HOSPITAL);

		caze.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(person.getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.localRdcf.community));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.localRdcf.facility));
		assertThat(savedCase.getHospitalization().getUuid(), is(caze.getHospitalization().getUuid()));
		assertThat(savedCase.getSymptoms().getUuid(), is(caze.getSymptoms().getUuid()));
		assertThat(savedCase.getEpiData().getUuid(), is(caze.getEpiData().getUuid()));
		assertThat(savedCase.getTherapy().getUuid(), is(caze.getTherapy().getUuid()));
		assertThat(savedCase.getClinicalCourse().getUuid(), is(caze.getClinicalCourse().getUuid()));
		assertThat(savedCase.getMaternalHistory().getUuid(), is(caze.getMaternalHistory().getUuid()));

		assertThat(savedCase.getSormasToSormasOriginInfo().getHealthDepartment().getUuid(), is("testHealthDep"));
		assertThat(savedCase.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));
	}

	/**
	 * Test that it doesnt throw de.symeda.sormas.api.utils.OutdatedEntityException
	 * To fix OutdatedEntityException generate new uuid for the outdated object in
	 * {@link de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb#processCaseData(CaseDataDto, PersonDto)}
	 */
	@Test
	public void testRecreateEmbeddedUuidsOfCase() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setHealthFacility(rdcf.remoteRdcf.facility);
		caze.setFacilityType(FacilityType.HOSPITAL);

		caze.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo());

		caze.getHospitalization().getPreviousHospitalizations().add(PreviousHospitalizationDto.build(caze));
		caze.getEpiData().getBurials().add(EpiDataBurialDto.build());
		caze.getEpiData().getTravels().add(EpiDataTravelDto.build());
		caze.getEpiData().getGatherings().add(EpiDataGatheringDto.build());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

		caze.setUuid(DataHelper.createUuid());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

	}

	@Test
	public void testSaveSharedPointOfEntryCase() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setPointOfEntry(rdcf.remoteRdcf.pointOfEntry);
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		caze.setPortHealthInfo(portHealthInfo);
		caze.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getPointOfEntry(), is(rdcf.localRdcf.pointOfEntry));
		assertThat(savedCase.getPortHealthInfo().getUuid(), is(portHealthInfo.getUuid()));
	}

	@Test
	public void testSaveSharedContact() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null);
		contact.setPerson(person.toReference());
		contact.setRegion(rdcf.remoteRdcf.region);
		contact.setDistrict(rdcf.remoteRdcf.district);
		contact.setCommunity(rdcf.remoteRdcf.community);

		contact.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo());

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasContactDto(person, contact));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(person.getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.localRdcf.community));

		ContactDto savedContact = getContactFacade().getContactByUuid(contact.getUuid());

		assertThat(savedContact, is(notNullValue()));
		assertThat(savedContact.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedContact.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedContact.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedContact.getEpiData().getUuid(), is(contact.getEpiData().getUuid()));

		assertThat(savedContact.getSormasToSormasOriginInfo().getHealthDepartment().getUuid(), is("testHealthDep"));
		assertThat(savedContact.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));
	}

	/**
	 * Test that it doesnt throw de.symeda.sormas.api.utils.OutdatedEntityException
	 * To fix OutdatedEntityException generate new uuid for the outdated object in
	 * {@link de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb#processContactData(ContactDto, PersonDto)}
	 */
	@Test
	public void testRecreateEmbeddedUuidsOfContact() {
		useNationalUserLogin();
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null);
		contact.setPerson(person.toReference());
		contact.setRegion(rdcf.remoteRdcf.region);
		contact.setDistrict(rdcf.remoteRdcf.district);
		contact.setCommunity(rdcf.remoteRdcf.community);

		contact.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo());

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasContactDto(person, contact));

		contact.setUuid(DataHelper.createUuid());

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasContactDto(person, contact));
	}

	@Test
	public void testShareCase() throws SormasToSormasException, JsonProcessingException {
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
		options.setHealthDepartment(
			new HealthDepartmentServerAccessData("healtsDep1", "Gesundheitsamt Charlottenburg (A)", "http://mock-sormas/sormas-rest"));
		options.setComment("Test comment");

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				assertThat(
					invocation.getArgumentAt(0, String.class),
					is("http://localhost:8080/sormas-rest" + SormasToSormasApiConstants.SAVE_SHARED_CASE_ENDPOINT));

				assertThat(
					new String(Base64.getDecoder().decode(invocation.getArgumentAt(1, String.class))),
					startsWith(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME));

				SormasToSormasCaseDto sharedCase = invocation.getArgumentAt(2, SormasToSormasCaseDto.class);

				assertThat(sharedCase.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedCase.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedCase.getCaze().getUuid(), is(caze.getUuid()));
				// users should be cleaned up
				assertThat(sharedCase.getCaze().getReportingUser(), is(nullValue()));
				assertThat(sharedCase.getCaze().getSurveillanceOfficer(), is(nullValue()));
				assertThat(sharedCase.getCaze().getClassificationUser(), is(nullValue()));

				// share information
				assertThat(sharedCase.getCaze().getSormasToSormasOriginInfo().getHealthDepartment().getUuid(), is("healthDepMain"));
				assertThat(sharedCase.getCaze().getSormasToSormasOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(sharedCase.getCaze().getSormasToSormasOriginInfo().getComment(), is("Test comment"));

				return Response.ok().build();
			});

		getSormasToSormasFacade().shareCase(caze.getUuid(), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getHealthDepartment().getUuid(), is("healtsDep1"));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareContact() throws SormasToSormasException, JsonProcessingException {
		RDCF rdcf = creator.createRDCF();

		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER).toReference();
		ContactDto contact = creator.createContact(officer, officer, person.toReference(), null, new Date(), null, null, rdcf, dto -> {
			dto.setResultingCaseUser(officer);
		});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setHealthDepartment(
			new HealthDepartmentServerAccessData("healtsDep1", "Gesundheitsamt Charlottenburg (A)", "http://mock-sormas/sormas-rest"));
		options.setComment("Test comment");

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				assertThat(
					invocation.getArgumentAt(0, String.class),
					is("http://localhost:8080/sormas-rest" + SormasToSormasApiConstants.SAVE_SHARED_CONTACT_ENDPOINT));

				assertThat(
					new String(Base64.getDecoder().decode(invocation.getArgumentAt(1, String.class))),
					startsWith(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME));

				SormasToSormasContactDto sharedContact = invocation.getArgumentAt(2, SormasToSormasContactDto.class);

				assertThat(sharedContact.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedContact.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedContact.getContact().getUuid(), is(contact.getUuid()));
				// users should be cleaned up
				assertThat(sharedContact.getContact().getReportingUser(), is(nullValue()));
				assertThat(sharedContact.getContact().getContactOfficer(), is(nullValue()));
				assertThat(sharedContact.getContact().getResultingCaseUser(), is(nullValue()));

				// share information
				assertThat(sharedContact.getContact().getSormasToSormasOriginInfo().getHealthDepartment().getUuid(), is("healthDepMain"));
				assertThat(sharedContact.getContact().getSormasToSormasOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(sharedContact.getContact().getSormasToSormasOriginInfo().getComment(), is("Test comment"));

				return Response.ok().build();
			});

		getSormasToSormasFacade().shareContact(contact.getUuid(), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasFacade().getShareInfoIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);
		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getHealthDepartment().getUuid(), is("healtsDep1"));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareCaseWithPseudonymizePersonalData() throws SormasToSormasException, JsonProcessingException {
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
		options.setHealthDepartment(
			new HealthDepartmentServerAccessData("healtsDep1", "Gesundheitsamt Charlottenburg (A)", "http://mock-sormas/sormas-rest"));
		options.setPseudonymizePersonalData(true);

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				assertThat(
					invocation.getArgumentAt(0, String.class),
					is("http://localhost:8080/sormas-rest" + SormasToSormasApiConstants.SAVE_SHARED_CASE_ENDPOINT));

				assertThat(
					new String(Base64.getDecoder().decode(invocation.getArgumentAt(1, String.class))),
					startsWith(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME));

				SormasToSormasCaseDto sharedCase = invocation.getArgumentAt(2, SormasToSormasCaseDto.class);

				assertThat(sharedCase.getPerson().getFirstName(), is("Confidential"));
				assertThat(sharedCase.getPerson().getLastName(), is("Confidential"));
				assertThat(sharedCase.getCaze().getAdditionalDetails(), is("Test additional details"));

				return Response.ok().build();
			});

		getSormasToSormasFacade().shareCase(caze.getUuid(), options);
	}

	@Test
	public void testShareCaseWithPseudonymizeSensitiveData() throws SormasToSormasException, JsonProcessingException {
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
		options.setHealthDepartment(
			new HealthDepartmentServerAccessData("healtsDep1", "Gesundheitsamt Charlottenburg (A)", "http://mock-sormas/sormas-rest"));
		options.setPseudonymizeSensitiveData(true);

		Mockito.when(MockProducer.getSormasToSormasClient().post(Matchers.anyString(), Matchers.anyString(), Matchers.any()))
			.thenAnswer(invocation -> {
				assertThat(
					invocation.getArgumentAt(0, String.class),
					is("http://localhost:8080/sormas-rest" + SormasToSormasApiConstants.SAVE_SHARED_CASE_ENDPOINT));

				assertThat(
					new String(Base64.getDecoder().decode(invocation.getArgumentAt(1, String.class))),
					startsWith(StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME));

				SormasToSormasCaseDto sharedCase = invocation.getArgumentAt(2, SormasToSormasCaseDto.class);

				assertThat(sharedCase.getPerson().getFirstName(), is("Confidential"));
				assertThat(sharedCase.getPerson().getLastName(), is("Confidential"));
				assertThat(sharedCase.getCaze().getAdditionalDetails(), isEmptyString());

				return Response.ok().build();
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
		source.setHealthDepartment(new HealthDepartmentServerReferenceDto("testHealthDep", "Test Department"));
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

	private static class MappableRdcf {

		private RDCF remoteRdcf;
		private RDCF localRdcf;
	}
}
