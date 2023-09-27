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
package de.symeda.sormas.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.DiagramType;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.share.ExternalShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;

public class TestDataCreator {

	private final AbstractBeanTest beanTest;
	public final Map<DefaultUserRole, UserRoleReferenceDto> userRoleDtoMap = new HashMap<>();
	public final Map<DefaultUserRole, UserRole> userRoleMap = new HashMap<>();

	public TestDataCreator(AbstractBeanTest beanTest) {
		this.beanTest = beanTest;
	}

	private void createUserRoles() {
		Arrays.stream(DefaultUserRole.values()).forEach(defaultUserRole -> {
			UserRoleDto userRoleDto =
				UserRoleDto.build(defaultUserRole.getDefaultUserRights().toArray(new UserRight[defaultUserRole.getDefaultUserRights().size()]));
			userRoleDto.setCaption(defaultUserRole.toString());
			userRoleDto.setEnabled(true);
			userRoleDto.setPortHealthUser(defaultUserRole.isPortHealthUser());
			userRoleDto.setLinkedDefaultUserRole(defaultUserRole);
			userRoleDto.setHasAssociatedDistrictUser(defaultUserRole.hasAssociatedDistrictUser());
			userRoleDto.setHasOptionalHealthFacility(defaultUserRole.hasOptionalHealthFacility());
			userRoleDto.setEmailNotificationTypes(defaultUserRole.getEmailNotificationTypes());
			userRoleDto.setSmsNotificationTypes(defaultUserRole.getSmsNotificationTypes());
			userRoleDto.setJurisdictionLevel(defaultUserRole.getJurisdictionLevel());
			userRoleDto = beanTest.getUserRoleFacade().saveUserRole(userRoleDto);
			userRoleDtoMap.put(defaultUserRole, userRoleDto.toReference());
			UserRole userRole = beanTest.getEagerUserRole(userRoleDto.getUuid());
			userRoleMap.put(defaultUserRole, userRole);
		});
	}

	public UserRoleReferenceDto getUserRoleReference(DefaultUserRole userRole) {
		if (userRoleDtoMap.isEmpty()) {
			createUserRoles();
		}
		return userRoleDtoMap.get(userRole);
	}

	public UserRole getUserRole(DefaultUserRole userRole) {
		if (userRoleMap.isEmpty()) {
			createUserRoles();
		}
		return userRoleMap.get(userRole);
	}

	public UserDto createNationalUser() {
		return createUser("", "", "", "Nat", "Usr", getUserRoleReference(DefaultUserRole.NATIONAL_USER));
	}

	public UserDto createSurveillanceSupervisor(RDCF rdcf) {
		if (rdcf == null) {
			rdcf = createRDCF("Region", "District", "Community", "Facility");
		}

		return createUser(rdcf.region.getUuid(), null, null, "Surv", "Sup", getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
	}

	public UserDto createSurveillanceOfficer(RDCF rdcf) {
		if (rdcf == null) {
			rdcf = createRDCF("Region", "District", "Community", "Facility");
		}

		return createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Off",
			getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
	}

	public UserDto createContactOfficer(RDCF rdcf) {
		if (rdcf == null) {
			rdcf = createRDCF("Region", "District", "Community", "Facility");
		}

		return createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Cont",
			"Off",
			getUserRoleReference(DefaultUserRole.CONTACT_OFFICER));
	}

	public UserDto createUser(RDCF rdcf, UserRoleReferenceDto userRole, Consumer<UserDto> customConfig) {

		UserDto user = UserDto.build();
		user.setFirstName("User");
		user.setLastName(userRole.getCaption());
		user.setUserName(userRole.buildCaption());
		user.setUserRoles(new HashSet(Arrays.asList(userRole)));
		user.setRegion(rdcf.region);
		user.setDistrict(rdcf.district);
		user.setCommunity(rdcf.community);
		user.setHealthFacility(rdcf.facility);

		if (customConfig != null) {
			customConfig.accept(user);
		}

		return beanTest.getUserFacade().saveUser(user, false);
	}

	public UserDto createUser(RDCF rdcf, UserRoleReferenceDto... roles) {
		return createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			roles.length > 0 ? roles[0].getCaption() : "First",
			"User",
			roles);
	}

	public UserDto createUser(RDCF rdcf, String firstName, String lastName, UserRoleReferenceDto... roles) {
		return createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.community.getUuid(),
			rdcf.facility.getUuid(),
			firstName,
			lastName,
			roles);
	}

	public UserDto createUser(
		String regionUuid,
		String districtUuid,
		String facilityUuid,
		String firstName,
		String lastName,
		UserRoleReferenceDto... roles) {
		return createUser(regionUuid, districtUuid, null, facilityUuid, firstName, lastName, roles);
	}

	public UserDto createUser(
		String regionUuid,
		String districtUuid,
		String communityUuid,
		String facilityUuid,
		String firstName,
		String lastName,
		UserRoleReferenceDto... roles) {
		return createUser(regionUuid, districtUuid, communityUuid, facilityUuid, firstName, lastName, null, roles);
	}

	public UserDto createUser(RDCF rdcf, String firstName, String lastName, Disease limitedDisease, UserRoleReferenceDto... roles) {
		return createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), null, rdcf.facility.getUuid(), firstName, lastName, limitedDisease, roles);
	}

	public UserDto createUser(
		String regionUuid,
		String districtUuid,
		String facilityUuid,
		String firstName,
		String lastName,
		String caption,
		JurisdictionLevel jurisdictionLevel,
		UserRight... userRights) {
		UserRoleReferenceDto userRole = createUserRole(caption, jurisdictionLevel, userRights);
		return createUser(regionUuid, districtUuid, facilityUuid, firstName, lastName, userRole);
	}

	private UserDto createUser(
		String regionUuid,
		String districtUuid,
		String communityUuid,
		String facilityUuid,
		String firstName,
		String lastName,
		Disease limitedDisease,
		UserRoleReferenceDto... roles) {

		UserDto user = UserDto.build();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserName(firstName + lastName);
		user.setUserRoles(new HashSet<>(Arrays.asList(roles)));
		user.setLimitedDisease(limitedDisease);
		user.setRegion(beanTest.getRegionFacade().getReferenceByUuid(regionUuid));
		user.setDistrict(beanTest.getDistrictFacade().getReferenceByUuid(districtUuid));
		user.setCommunity(beanTest.getCommunityFacade().getReferenceByUuid(communityUuid));
		user.setHealthFacility(beanTest.getFacilityFacade().getReferenceByUuid(facilityUuid));
		return beanTest.getUserFacade().saveUser(user, false);
	}

	public UserDto createPointOfEntryUser(RDP rdp) {
		return createUser(
			new RDCF(rdp.region, rdp.district, null, null),
			userRoleDtoMap.get(DefaultUserRole.POE_INFORMANT),
			user -> user.setPointOfEntry(rdp.pointOfEntry));
	}

	public UserRoleReferenceDto createUserRole(String caption, JurisdictionLevel jurisdictionLevel, UserRight... userRights) {
		UserRoleDto userRole = new UserRoleDto();
		userRole.setCaption(caption);
		userRole.setJurisdictionLevel(jurisdictionLevel);
		userRole.setUserRights(Arrays.stream(userRights).collect(Collectors.toSet()));
		return beanTest.getUserRoleFacade().saveUserRole(userRole).toReference();
	}

	public UserRoleReferenceDto createUserRoleWithRequiredRights(String caption, JurisdictionLevel jurisdictionLevel, UserRight... userRights) {
		UserRoleDto userRole = new UserRoleDto();
		userRole.setCaption(caption);
		userRole.setJurisdictionLevel(jurisdictionLevel);
		userRole.setUserRights(UserRight.getWithRequiredUserRights(userRights));
		return beanTest.getUserRoleFacade().saveUserRole(userRole).toReference();
	}

	public PersonDto createPerson() {
		return createPerson("FirstName", "LastName");
	}

	public PersonDto createPerson(String firstName, String lastName) {
		return createPerson(firstName, lastName, Sex.UNKNOWN, null);
	}

	public PersonDto createPerson(String firstName, String lastName, Sex sex) {
		return createPerson(firstName, lastName, sex, null);
	}

	public PersonDto createPerson(String firstName, String lastName, Sex sex, Consumer<PersonDto> customConfig) {

		PersonDto person = PersonDto.build();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setSex(sex);

		if (customConfig != null) {
			customConfig.accept(person);
		}

		person = beanTest.getPersonFacade().save(person);

		return person;
	}

	public PersonDto createPerson(String firstName, String lastName, Consumer<PersonDto> customConfig) {

		PersonDto person = PersonDto.build();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setSex(Sex.UNKNOWN);

		if (customConfig != null) {
			customConfig.accept(person);
		}

		person = beanTest.getPersonFacade().save(person);

		return person;
	}

	public PersonDto createPerson(String firstName, String lastName, Sex sex, Integer birthdateYYYY, Integer birthdateMM, Integer birthdateDD) {
		return createPerson(firstName, lastName, sex, birthdateYYYY, birthdateMM, birthdateDD, null, null, null);
	}

	public PersonDto createPerson(
		String firstName,
		String lastName,
		Sex sex,
		Integer birthdateYYYY,
		Integer birthdateMM,
		Integer birthdateDD,
		String passportNr,
		String nationalHealthId) {
		return createPerson(firstName, lastName, sex, birthdateYYYY, birthdateMM, birthdateDD, null, passportNr, nationalHealthId);
	}

	private PersonDto createPerson(
		String firstName,
		String lastName,
		Sex sex,
		Integer birthdateYYYY,
		Integer birthdateMM,
		Integer birthdateDD,
		LocationDto address,
		String passportNr,
		String nationalHealthId) {

		PersonDto person = PersonDto.build();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setSex(sex);
		person.setBirthdateYYYY(birthdateYYYY);
		person.setBirthdateMM(birthdateMM);
		person.setBirthdateDD(birthdateDD);
		person.setPassportNumber(passportNr);
		person.setNationalHealthId(nationalHealthId);

		if (address != null) {
			person.setAddress(address);
		}

		person = beanTest.getPersonFacade().save(person);

		return person;
	}

	public PersonDto createPerson(
		String firstName,
		String lastName,
		Sex sex,
		Integer birthdateYYYY,
		Integer birthdateMM,
		Integer birthdateDD,
		Consumer<PersonDto> customConfig) {

		PersonDto person = PersonDto.build();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setSex(sex);
		person.setBirthdateYYYY(birthdateYYYY);
		person.setBirthdateMM(birthdateMM);
		person.setBirthdateDD(birthdateDD);

		if (customConfig != null) {
			customConfig.accept(person);
		}

		person = beanTest.getPersonFacade().save(person);

		return person;
	}

	public PersonContactDetailDto createPersonContactDetail(
		PersonReferenceDto person,
		boolean primaryContact,
		PersonContactDetailType personContactDetailType,
		String contactInformation) {
		PersonContactDetailDto contactDetails =
			PersonContactDetailDto.build(person, primaryContact, personContactDetailType, null, null, contactInformation, null, false, null, null);
		return contactDetails;
	}

	public CaseDataDto createUnclassifiedCase(Disease disease) {

		RDCF rdcf = createRDCF("Region", "District", "Community", "Facility");
		UserDto user = beanTest.getUserFacade().getByUserName("SurvSup");
		if (user == null) {
			user = createSurveillanceSupervisor(rdcf);
		}

		PersonDto cazePerson = createPerson("Case", "Person", Sex.UNKNOWN);
		return createCase(
			user.toReference(),
			cazePerson.toReference(),
			disease,
			CaseClassification.NOT_CLASSIFIED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
	}

	public CaseDataDto createCase(UserReferenceDto user, RDCF rdcf, Consumer<CaseDataDto> setCustomFields) {
		return createCase(
			user,
			createPerson().toReference(),
			Disease.EVD,
			CaseClassification.SUSPECT,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			setCustomFields);
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto person, RDCF rdcf) {
		return createCase(user, person, Disease.EVD, CaseClassification.SUSPECT, InvestigationStatus.PENDING, new Date(), rdcf);
	}

	public CaseDataDto createCase(UserReferenceDto user, PersonReferenceDto person, RDCF rdcf, Consumer<CaseDataDto> setCustomFields) {
		return createCase(user, person, Disease.EVD, CaseClassification.SUSPECT, InvestigationStatus.PENDING, new Date(), rdcf, setCustomFields);
	}

	public CaseDataDto createCase(
		UserReferenceDto user,
		PersonReferenceDto cazePerson,
		Disease disease,
		CaseClassification caseClassification,
		InvestigationStatus investigationStatus,
		Date reportAndOnsetDate,
		RDCF rdcf) {

		return createCase(user, cazePerson, disease, caseClassification, investigationStatus, reportAndOnsetDate, rdcf, null);
	}

	public CaseDataDto createCase(
		UserReferenceDto user,
		PersonReferenceDto cazePerson,
		Disease disease,
		CaseClassification caseClassification,
		InvestigationStatus investigationStatus,
		Date reportAndOnsetDate,
		RDCF rdcf,
		Consumer<CaseDataDto> setCustomFields) {

		CaseDataDto caze = CaseDataDto.build(cazePerson, disease);
		caze.setReportDate(reportAndOnsetDate);
		caze.setReportingUser(user);
		caze.getSymptoms().setOnsetDate(reportAndOnsetDate);
		caze.setCaseClassification(caseClassification);
		caze.setInvestigationStatus(investigationStatus);
		caze.setResponsibleRegion(rdcf.region);
		caze.setResponsibleDistrict(rdcf.district);
		caze.setResponsibleCommunity(rdcf.community);
		caze.setFacilityType(beanTest.getFacilityFacade().getByUuid(rdcf.facility.getUuid()).getType());
		caze.setHealthFacility(rdcf.facility);
		caze.setPointOfEntry(rdcf.pointOfEntry);

		if (setCustomFields != null) {
			setCustomFields.accept(caze);
		}

		caze = beanTest.getCaseFacade().save(caze);

		return caze;
	}

	public ImmunizationDto createImmunization(
		Disease disease,
		PersonReferenceDto person,
		UserReferenceDto reportingUser,
		ImmunizationStatus immunizationStatus,
		MeansOfImmunization meansOfImmunization,
		ImmunizationManagementStatus immunizationManagementStatus,
		RDCF rdcf,
		Date startDate,
		Date endDate,
		Date validFromDate,
		Date validUntilDate) {
		ImmunizationDto immunization =
			createImmunizationDto(disease, person, reportingUser, immunizationStatus, meansOfImmunization, immunizationManagementStatus, rdcf);
		immunization.setStartDate(startDate);
		immunization.setEndDate(endDate);
		immunization.setValidFrom(validFromDate);
		immunization.setValidUntil(validUntilDate);

		return beanTest.getImmunizationFacade().save(immunization);
	}

	public ImmunizationDto createImmunization(
		Disease disease,
		PersonReferenceDto person,
		UserReferenceDto reportingUser,
		ImmunizationStatus immunizationStatus,
		MeansOfImmunization meansOfImmunization,
		ImmunizationManagementStatus immunizationManagementStatus,
		RDCF rdcf) {
		ImmunizationDto immunization =
			createImmunizationDto(disease, person, reportingUser, immunizationStatus, meansOfImmunization, immunizationManagementStatus, rdcf);

		return beanTest.getImmunizationFacade().save(immunization);
	}

	public ImmunizationDto createImmunization(Disease disease, PersonReferenceDto person, UserReferenceDto reportingUser, RDCF rdcf) {

		return createImmunization(disease, person, reportingUser, rdcf, null);
	}

	public ImmunizationDto createImmunization(
		Disease disease,
		PersonReferenceDto person,
		UserReferenceDto reportingUser,
		RDCF rdcf,
		Consumer<ImmunizationDto> extraConfig) {

		ImmunizationDto immunization = createImmunizationDto(
			disease,
			person,
			reportingUser,
			ImmunizationStatus.PENDING,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.ONGOING,
			rdcf);

		if (extraConfig != null) {
			extraConfig.accept(immunization);
		}

		return beanTest.getImmunizationFacade().save(immunization);
	}

	@NotNull
	public ImmunizationDto createImmunizationDto(
		Disease disease,
		PersonReferenceDto person,
		UserReferenceDto reportingUser,
		ImmunizationStatus immunizationStatus,
		MeansOfImmunization meansOfImmunization,
		ImmunizationManagementStatus immunizationManagementStatus,
		RDCF rdcf) {
		ImmunizationDto immunization = new ImmunizationDto();
		immunization.setUuid(DataHelper.createUuid());
		immunization.setDisease(disease);
		immunization.setPerson(person);
		immunization.setReportingUser(reportingUser);
		immunization.setImmunizationStatus(immunizationStatus);
		immunization.setMeansOfImmunization(meansOfImmunization);
		immunization.setImmunizationManagementStatus(immunizationManagementStatus);
		immunization.setResponsibleRegion(rdcf.region);
		immunization.setResponsibleDistrict(rdcf.district);
		immunization.setResponsibleCommunity(rdcf.community);

		immunization.setReportDate(new Date());
		return immunization;
	}

	public VaccinationDto createVaccination(
		UserReferenceDto reportingUser,
		ImmunizationReferenceDto immunization,
		HealthConditionsDto healthConditions) {

		return createVaccination(reportingUser, immunization, healthConditions, new Date(), null, null);
	}

	public VaccinationDto createVaccination(UserReferenceDto reportingUser, ImmunizationReferenceDto immunization) {
		return createVaccination(reportingUser, immunization, new HealthConditionsDto());
	}

	@NotNull
	public VaccinationDto createVaccinationDto(
		UserReferenceDto reportingUser,
		ImmunizationReferenceDto immunization,
		HealthConditionsDto healthConditions) {
		VaccinationDto vaccination = new VaccinationDto();
		vaccination.setUuid(DataHelper.createUuid());
		vaccination.setReportingUser(reportingUser);
		vaccination.setReportDate(new Date());

		vaccination.setImmunization(immunization);
		vaccination.setHealthConditions(healthConditions);
		return vaccination;
	}

	public VaccinationDto createVaccinationWithDetails(
		UserReferenceDto reportingUser,
		ImmunizationReferenceDto immunization,
		HealthConditionsDto healthConditions,
		Date vaccinationDate,
		Vaccine vaccine,
		VaccineManufacturer vaccineManufacturer,
		VaccinationInfoSource infoSource,
		String vaccineInn,
		String vaccineBatchNumber,
		String vaccineAtcCode,
		String vaccineDose) {

		VaccinationDto vaccinationDto =
			createVaccination(reportingUser, immunization, healthConditions, vaccinationDate, vaccine, vaccineManufacturer);

		vaccinationDto.setVaccinationInfoSource(infoSource);
		vaccinationDto.setVaccineInn(vaccineInn);
		vaccinationDto.setVaccineBatchNumber(vaccineBatchNumber);
		vaccinationDto.setVaccineAtcCode(vaccineAtcCode);
		vaccinationDto.setVaccineDose(vaccineDose);

		return beanTest.getVaccinationFacade().save(vaccinationDto);
	}

	public VaccinationDto createVaccination(
		UserReferenceDto reportingUser,
		ImmunizationReferenceDto immunization,
		HealthConditionsDto healthConditions,
		Date vaccinationDate,
		Vaccine vaccine,
		VaccineManufacturer vaccineManufacturer) {

		VaccinationDto vaccination = new VaccinationDto();
		vaccination.setUuid(DataHelper.createUuid());
		vaccination.setReportingUser(reportingUser);
		vaccination.setReportDate(new Date());
		vaccination.setVaccinationDate(new Date());
		vaccination.setVaccineName(vaccine);
		vaccination.setVaccineManufacturer(vaccineManufacturer);

		vaccination.setVaccinationDate(vaccinationDate);
		vaccination.setImmunization(immunization);
		vaccination.setHealthConditions(healthConditions);

		return beanTest.getVaccinationFacade().save(vaccination);
	}

	public TravelEntryDto createTravelEntry(PersonReferenceDto person, UserReferenceDto reportingUser, RDCF rdcf, Consumer<TravelEntryDto> config) {

		TravelEntryDto travelEntry = TravelEntryDto.build(person);
		travelEntry.setDisease(Disease.EVD);
		travelEntry.setReportingUser(reportingUser);
		travelEntry.setResponsibleRegion(rdcf.region);
		travelEntry.setResponsibleDistrict(rdcf.district);
		travelEntry.setPointOfEntry(rdcf.pointOfEntry);
		travelEntry.setDateOfArrival(new Date());

		if (config != null) {
			config.accept(travelEntry);
		}

		return beanTest.getTravelEntryFacade().save(travelEntry);
	}

	public TravelEntryDto createTravelEntry(
		PersonReferenceDto person,
		UserReferenceDto reportingUser,
		Disease disease,
		RegionReferenceDto responsibleRegion,
		DistrictReferenceDto responsibleDistrict,
		PointOfEntryReferenceDto pointOfEntry) {

		TravelEntryDto travelEntry = TravelEntryDto.build(person);
		travelEntry.setDisease(disease);
		travelEntry.setReportingUser(reportingUser);
		travelEntry.setDateOfArrival(new Date());
		travelEntry.setResponsibleRegion(responsibleRegion);
		travelEntry.setResponsibleDistrict(responsibleDistrict);
		travelEntry.setPointOfEntry(pointOfEntry);

		return beanTest.getTravelEntryFacade().save(travelEntry);
	}

	public ClinicalVisitDto createClinicalVisit(CaseDataDto caze) {
		return createClinicalVisit(caze, null);
	}

	public ClinicalVisitDto createClinicalVisit(CaseDataDto caze, Consumer<ClinicalVisitDto> extraConfig) {

		ClinicalVisitDto clinicalVisit = ClinicalVisitDto.build(caze.getClinicalCourse().toReference(), caze.getDisease());

		if (extraConfig != null) {
			extraConfig.accept(clinicalVisit);
		}

		clinicalVisit = beanTest.getClinicalVisitFacade().saveClinicalVisit(clinicalVisit, caze.getUuid());
		return clinicalVisit;
	}

	public PrescriptionDto createPrescription(CaseDataDto caze) {
		return createPrescription(caze, null);
	}

	public PrescriptionDto createPrescription(CaseDataDto caze, Consumer<PrescriptionDto> customConfig) {

		PrescriptionDto prescription = PrescriptionDto.buildPrescription(caze.getTherapy().toReference());
		prescription.setPrescriptionType(TreatmentType.BLOOD_TRANSFUSION);

		if (customConfig != null) {
			customConfig.accept(prescription);
		}

		prescription = beanTest.getPrescriptionFacade().savePrescription(prescription);

		return prescription;
	}

	public TreatmentDto createTreatment(CaseDataDto caze) {
		return createTreatment(caze, null);
	}

	public TreatmentDto createTreatment(CaseDataDto caze, Consumer<TreatmentDto> customConfig) {

		TreatmentDto treatment = TreatmentDto.build(caze.getTherapy().toReference());
		treatment.setTreatmentType(TreatmentType.BLOOD_TRANSFUSION);

		if (customConfig != null) {
			customConfig.accept(treatment);
		}

		treatment = beanTest.getTreatmentFacade().saveTreatment(treatment);

		return treatment;
	}

	public ContactDto createContact(RDCF rdcf, UserReferenceDto reportingUser, PersonReferenceDto contactPerson) {
		return createContact(reportingUser, null, contactPerson, null, new Date(), null, null, rdcf);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson) {
		return createContact(reportingUser, null, contactPerson, null, new Date(), null, null, null);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, Disease disease) {
		return createContact(reportingUser, null, contactPerson, null, new Date(), null, disease, null);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, Disease disease, RDCF rdcf) {
		return createContact(reportingUser, null, contactPerson, null, new Date(), null, disease, null);
	}

	public ContactDto createContact(
		UserReferenceDto reportingUser,
		PersonReferenceDto contactPerson,
		Disease disease,
		Consumer<ContactDto> customConfig) {
		return createContact(reportingUser, null, contactPerson, null, new Date(), null, disease, null, customConfig);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, Date reportDateTime) {
		return createContact(reportingUser, null, contactPerson, null, reportDateTime, null, null, null);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, CaseDataDto caze) {
		return createContact(reportingUser, null, contactPerson, caze, new Date(), null, null, null);
	}

	public ContactDto createContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, CaseDataDto caze, RDCF rdcf) {
		return createContact(reportingUser, null, contactPerson, caze, new Date(), null, null, rdcf);
	}

	public ContactDto createContact(
		UserReferenceDto reportingUser,
		UserReferenceDto contactOfficer,
		PersonReferenceDto contactPerson,
		CaseDataDto caze,
		Date reportDateTime,
		Date lastContactDate,
		Disease disease) {

		return createContact(reportingUser, contactOfficer, contactPerson, caze, reportDateTime, lastContactDate, disease, null);
	}

	public ContactDto createContact(
		UserReferenceDto reportingUser,
		UserReferenceDto contactOfficer,
		PersonReferenceDto contactPerson,
		CaseDataDto caze,
		Date reportDateTime,
		Date lastContactDate,
		Disease disease,
		RDCF rdcf) {
		return createContact(reportingUser, contactOfficer, contactPerson, caze, reportDateTime, lastContactDate, disease, rdcf, null);
	}

	public ContactDto createContact(
		UserReferenceDto reportingUser,
		UserReferenceDto contactOfficer,
		PersonReferenceDto contactPerson,
		CaseDataDto caze,
		Date reportDateTime,
		Date lastContactDate,
		Disease disease,
		RDCF rdcf,
		Consumer<ContactDto> customConfig) {
		ContactDto contact;

		if (caze != null) {
			contact = ContactDto.build(caze);
		} else {
			contact = ContactDto.build(null, disease != null ? disease : Disease.EVD, null, null);
			if (rdcf == null) {
				rdcf = createRDCF();
			}
			contact.setRegion(rdcf.region);
			contact.setDistrict(rdcf.district);
		}
		contact.setReportingUser(reportingUser);
		contact.setContactOfficer(contactOfficer);
		contact.setPerson(contactPerson);
		contact.setReportDateTime(reportDateTime);
		contact.setLastContactDate(lastContactDate);
		contact.setEpiData(EpiDataDto.build());

		if (customConfig != null) {
			customConfig.accept(contact);
		}

		contact = beanTest.getContactFacade().save(contact);

		return contact;
	}

	public TaskDto createTask(UserReferenceDto assigneeUser) {
		return createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, null, new Date(), assigneeUser);
	}

	public TaskDto createTask(TaskContext context, ReferenceDto entityRef, Consumer<TaskDto> customConfig) {

		TaskDto task = TaskDto.build(context, entityRef);

		if (customConfig != null) {
			customConfig.accept(task);
		}

		task = beanTest.getTaskFacade().saveTask(task);

		return task;
	}

	public TaskDto createTask(
		TaskContext context,
		TaskType type,
		TaskStatus status,
		CaseReferenceDto caze,
		ContactReferenceDto contact,
		EventReferenceDto event,
		Date dueDate,
		UserReferenceDto assigneeUser) {

		ReferenceDto entityRef;
		switch (context) {
		case CASE:
			entityRef = caze;
			break;
		case CONTACT:
			entityRef = contact;
			break;
		case EVENT:
			entityRef = event;
			break;
		case GENERAL:
			entityRef = null;
			break;
		default:
			throw new IllegalArgumentException(context.toString());
		}

		TaskDto task = TaskDto.build(context, entityRef);
		task.setTaskType(type);
		task.setTaskStatus(status);
		task.setDueDate(dueDate);
		task.setAssigneeUser(assigneeUser);

		task = beanTest.getTaskFacade().saveTask(task);

		return task;
	}

	public VisitDto createVisit(PersonReferenceDto person) {
		return createVisit(Disease.EVD, person);
	}

	public VisitDto createVisit(Disease disease, PersonReferenceDto person) {
		return createVisit(disease, person, new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
	}

	public VisitDto createVisit(Disease disease, PersonReferenceDto person, Date visitDateTime) {
		return createVisit(disease, person, visitDateTime, VisitStatus.COOPERATIVE, VisitOrigin.USER);
	}

	public VisitDto createVisit(Disease disease, PersonReferenceDto person, Date visitDateTime, VisitStatus visitStatus, VisitOrigin visitOrigin) {
		return createVisit(disease, person, visitDateTime, visitStatus, visitOrigin, null);
	}

	public VisitDto createVisit(
		Disease disease,
		PersonReferenceDto person,
		Date visitDateTime,
		VisitStatus visitStatus,
		VisitOrigin visitOrigin,
		Consumer<VisitDto> customConfig) {

		VisitDto visit = VisitDto.build(person, disease, visitOrigin);
		visit.setVisitDateTime(visitDateTime);
		visit.setVisitStatus(visitStatus);

		if (customConfig != null) {
			customConfig.accept(visit);
		}

		visit = beanTest.getVisitFacade().save(visit);

		return visit;
	}

	public EventDto createEvent(UserReferenceDto reportingUser) {

		return createEvent(reportingUser, new Date());
	}

	public EventDto createEvent(UserReferenceDto reportingUser, EventStatus status) {

		return createEvent(status, EventInvestigationStatus.PENDING, "eventTitle", "description", reportingUser, null, null);
	}

	public EventDto createEvent(UserReferenceDto reportingUser, Disease disease) {
		return createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"title",
			"description",
			"firstname",
			"lastname",
			null,
			null,
			new Date(),
			new Date(),
			reportingUser,
			null,
			disease,
			null);
	}

	public EventDto createEvent(UserReferenceDto reportingUser, Disease disease, RDCF rdcf) {

		return createEvent(EventStatus.SIGNAL, EventInvestigationStatus.PENDING, "title", "description", reportingUser, rdcf, (event) -> {
			event.setReportDateTime(new Date());
			event.setReportingUser(reportingUser);
			event.setDisease(disease);
		});
	}

	public EventDto createEvent(UserReferenceDto reportingUser, Disease disease, Consumer<EventDto> customConfig) {

		return createEvent(EventStatus.SIGNAL, EventInvestigationStatus.PENDING, "title", "description", reportingUser, null, (event) -> {
			event.setReportDateTime(new Date());
			event.setReportingUser(reportingUser);
			event.setDisease(disease);

			customConfig.accept(event);
		});
	}

	public EventDto createEvent(UserReferenceDto reportingUser, Date eventDate) {

		return createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"title",
			"Description",
			"FirstName",
			"LastName",
			null,
			null,
			eventDate,
			new Date(),
			reportingUser,
			null,
			null,
			null);
	}

	public EventDto createEvent(
		EventStatus eventStatus,
		EventInvestigationStatus eventInvestigationStatus,
		String eventTitle,
		String eventDesc,
		String srcFirstName,
		String srcLastName,
		String srcTelNo,
		TypeOfPlace typeOfPlace,
		Date eventDate,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		UserReferenceDto responsibleUser,
		Disease disease,
		RDCF rdcf) {

		return createEvent(eventStatus, eventInvestigationStatus, eventTitle, eventDesc, reportingUser, rdcf, (event) -> {
			event.setSrcFirstName(srcFirstName);
			event.setSrcLastName(srcLastName);
			event.setSrcTelNo(srcTelNo);
			event.setTypeOfPlace(typeOfPlace);
			event.setStartDate(eventDate);
			event.setReportDateTime(reportDateTime);
			event.setReportingUser(reportingUser);
			event.setResponsibleUser(responsibleUser);
			event.setDisease(disease);

		});
	}

	public EventDto createEvent(
		EventStatus eventStatus,
		EventInvestigationStatus eventInvestigationStatus,
		String eventTitle,
		String eventDesc,
		UserReferenceDto reportingUser,
		RDCF rdcf,
		Consumer<EventDto> customSettings) {

		EventDto event = EventDto.build();
		event.setEventStatus(eventStatus);
		event.setEventInvestigationStatus(eventInvestigationStatus);
		event.setEventTitle(eventTitle);
		event.setEventDesc(eventDesc);
		event.setReportingUser(reportingUser);

		if (rdcf == null) {
			rdcf = createRDCF();
		}

		event.getEventLocation().setRegion(rdcf.region);
		event.getEventLocation().setDistrict(rdcf.district);

		if (customSettings != null) {
			customSettings.accept(event);
		}

		event = beanTest.getEventFacade().save(event);

		return event;
	}

	public EventParticipantDto createEventParticipant(EventReferenceDto event, PersonDto eventPerson, UserReferenceDto reportingUser) {
		return createEventParticipant(event, eventPerson, "Description", reportingUser, null, null);
	}

	public EventParticipantDto createEventParticipant(
		EventReferenceDto event,
		PersonDto eventPerson,
		String involvementDescription,
		UserReferenceDto reportingUser) {
		return createEventParticipant(event, eventPerson, involvementDescription, reportingUser, null, null);
	}

	public EventParticipantDto createEventParticipant(
		EventReferenceDto event,
		PersonDto eventPerson,
		String involvementDescription,
		UserReferenceDto reportingUser,
		RDCF rdcf) {
		return createEventParticipant(event, eventPerson, involvementDescription, reportingUser, null, rdcf);
	}

	public EventParticipantDto createEventParticipant(
		EventReferenceDto event,
		PersonDto eventPerson,
		String involvementDescription,
		UserReferenceDto reportingUser,
		Consumer<EventParticipantDto> customSettings,
		RDCF rdcf) {

		EventParticipantDto eventParticipant = EventParticipantDto.build(event, reportingUser);
		eventParticipant.setPerson(eventPerson);
		eventParticipant.setInvolvementDescription(involvementDescription);

		if (customSettings != null) {
			customSettings.accept(eventParticipant);
		}
		if (rdcf != null) {
			eventParticipant.setRegion(rdcf.region);
			eventParticipant.setDistrict(rdcf.district);
		}

		eventParticipant = beanTest.getEventParticipantFacade().save(eventParticipant);
		return eventParticipant;
	}

	public SurveillanceReportDto createSurveillanceReport(UserReferenceDto reportingUser, CaseReferenceDto caseReference) {
		return createSurveillanceReport(reportingUser, ReportingType.DOCTOR, caseReference);
	}

	public SurveillanceReportDto createSurveillanceReport(
		UserReferenceDto reportingUser,
		ReportingType reportingType,
		CaseReferenceDto caseReference) {
		return createSurveillanceReport(reportingUser, reportingType, caseReference, null);
	}

	public SurveillanceReportDto createSurveillanceReport(
		UserReferenceDto reportingUser,
		ReportingType reportingType,
		CaseReferenceDto caseReference,
		Consumer<SurveillanceReportDto> extraConfig) {
		SurveillanceReportDto surveillanceReport = SurveillanceReportDto.build(caseReference, reportingUser);
		surveillanceReport.setReportingType(reportingType);
		surveillanceReport.setReportDate(new Date());

		if (extraConfig != null) {
			extraConfig.accept(surveillanceReport);
		}

		surveillanceReport = beanTest.getSurveillanceReportFacade().save(surveillanceReport);

		return surveillanceReport;
	}

	public ActionDto createAction(EventReferenceDto event) {

		ActionDto action = ActionDto.build(ActionContext.EVENT, event);

		action = beanTest.getActionFacade().saveAction(action);
		return action;
	}

	public SampleDto createSample(CaseReferenceDto associatedCase, UserReferenceDto reportingUser, Facility lab) {
		return createSample(associatedCase, reportingUser, lab, null);
	}

	public SampleDto createSample(CaseReferenceDto associatedCase, UserReferenceDto reportingUser, Facility lab, Consumer<SampleDto> customConfig) {
		return createSample(associatedCase, new Date(), new Date(), reportingUser, SampleMaterial.BLOOD, lab, customConfig);
	}

	public SampleDto createSample(CaseReferenceDto associatedCase, UserReferenceDto reportingUser, FacilityReferenceDto lab) {
		return createSample(associatedCase, reportingUser, lab, null);
	}

	public SampleDto createSample(
		CaseReferenceDto associatedCase,
		UserReferenceDto reportingUser,
		FacilityReferenceDto lab,
		Consumer<SampleDto> customSettings) {

		SampleDto sample = SampleDto.build(reportingUser, associatedCase);
		sample.setSampleDateTime(new Date());
		sample.setReportDateTime(new Date());
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(beanTest.getFacilityFacade().getReferenceByUuid(lab.getUuid()));

		if (customSettings != null) {
			customSettings.accept(sample);
		}

		sample = beanTest.getSampleFacade().saveSample(sample);

		return sample;
	}

	public SampleDto createSample(
		CaseReferenceDto associatedCase,
		Date sampleDateTime,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		SampleMaterial sampleMaterial,
		Facility lab) {
		return createSample(associatedCase, sampleDateTime, reportDateTime, reportingUser, sampleMaterial, lab, null);
	}

	public SampleDto createSample(
		CaseReferenceDto associatedCase,
		Date sampleDateTime,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		SampleMaterial sampleMaterial,
		Facility lab,
		Consumer<SampleDto> customConfig) {
		SampleDto sample = SampleDto.build(reportingUser, associatedCase);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(beanTest.getFacilityFacade().getReferenceByUuid(lab.getUuid()));

		if (customConfig != null) {
			customConfig.accept(sample);
		}

		sample = beanTest.getSampleFacade().saveSample(sample);

		return sample;
	}

	public SampleDto createSample(
		CaseReferenceDto associatedCase,
		Date sampleDateTime,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		SampleMaterial sampleMaterial,
		FacilityReferenceDto lab) {

		SampleDto sample = SampleDto.build(reportingUser, associatedCase);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(lab);

		sample = beanTest.getSampleFacade().saveSample(sample);
		return sample;
	}

	@Deprecated
	public SampleDto createSample(
		ContactReferenceDto associatedContact,
		Date sampleDateTime,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		SampleMaterial sampleMaterial,
		Facility lab) {

		SampleDto sample = SampleDto.build(reportingUser, associatedContact);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(beanTest.getFacilityFacade().getReferenceByUuid(lab.getUuid()));

		sample = beanTest.getSampleFacade().saveSample(sample);
		return sample;
	}

	public SampleDto createSample(
		ContactReferenceDto associatedContact,
		Date sampleDateTime,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		SampleMaterial sampleMaterial,
		FacilityReferenceDto lab) {

		SampleDto sample = SampleDto.build(reportingUser, associatedContact);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(lab);

		sample = beanTest.getSampleFacade().saveSample(sample);
		return sample;
	}

	public SampleDto createSample(
		ContactReferenceDto associatedContact,
		UserReferenceDto reportingUser,
		FacilityReferenceDto lab,
		Consumer<SampleDto> customSettings) {

		SampleDto sample = SampleDto.build(reportingUser, associatedContact);
		sample.setSampleDateTime(new Date());
		sample.setReportDateTime(new Date());
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(beanTest.getFacilityFacade().getReferenceByUuid(lab.getUuid()));

		if (customSettings != null) {
			customSettings.accept(sample);
		}

		sample = beanTest.getSampleFacade().saveSample(sample);

		return sample;
	}

	public SampleDto createSample(EventParticipantReferenceDto associatedEventParticipant, UserReferenceDto reportingUser, FacilityReferenceDto lab) {
		return createSample(associatedEventParticipant, new Date(), new Date(), reportingUser, SampleMaterial.BLOOD, lab);
	}

	public SampleDto createSample(
		EventParticipantReferenceDto associatedEventParticipant,
		UserReferenceDto reportingUser,
		FacilityReferenceDto lab,
		Consumer<SampleDto> customConfig) {
		return createSample(associatedEventParticipant, new Date(), new Date(), reportingUser, SampleMaterial.BLOOD, lab, customConfig);
	}

	public SampleDto createSample(
		EventParticipantReferenceDto associatedEventParticipant,
		Date sampleDateTime,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		SampleMaterial sampleMaterial,
		FacilityReferenceDto lab) {

		SampleDto sample = SampleDto.build(reportingUser, associatedEventParticipant);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(lab);

		sample = beanTest.getSampleFacade().saveSample(sample);
		return sample;
	}

	public SampleDto createSample(
		EventParticipantReferenceDto associatedEventParticipant,
		Date sampleDateTime,
		Date reportDateTime,
		UserReferenceDto reportingUser,
		SampleMaterial sampleMaterial,
		FacilityReferenceDto lab,
		Consumer<SampleDto> customConfig) {

		SampleDto sample = SampleDto.build(reportingUser, associatedEventParticipant);
		sample.setSampleDateTime(sampleDateTime);
		sample.setReportDateTime(reportDateTime);
		sample.setSampleMaterial(sampleMaterial);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(lab);

		if (customConfig != null) {
			customConfig.accept(sample);
		}

		sample = beanTest.getSampleFacade().saveSample(sample);
		return sample;
	}

	public PathogenTestDto createPathogenTest(
		SampleReferenceDto sample,
		PathogenTestType testType,
		Disease testedDisease,
		Date testDateTime,
		FacilityReferenceDto lab,
		UserReferenceDto labUser,
		PathogenTestResultType testResult,
		String testResultText,
		boolean verified) {
		return createPathogenTest(sample, testType, testedDisease, testDateTime, lab, labUser, testResult, testResultText, verified, null);
	}

	public PathogenTestDto createPathogenTest(SampleReferenceDto sample, UserReferenceDto labUser, Consumer<PathogenTestDto> extraConfig) {
		return createPathogenTest(
			sample,
			PathogenTestType.ANTIGEN_DETECTION,
			null,
			new Date(),
			(FacilityReferenceDto) null,
			labUser,
			PathogenTestResultType.PENDING,
			null,
			true,
			extraConfig);
	}

	public PathogenTestDto createPathogenTest(
		SampleReferenceDto sample,
		PathogenTestType testType,
		Disease testedDisease,
		Date testDateTime,
		Facility lab,
		UserReferenceDto labUser,
		PathogenTestResultType testResult,
		String testResultText,
		boolean verified,
		Consumer<PathogenTestDto> extraConfig) {

		PathogenTestDto sampleTest = PathogenTestDto.build(sample, labUser);
		sampleTest.setTestedDisease(testedDisease);
		sampleTest.setTestType(testType);
		sampleTest.setTestDateTime(testDateTime);
		sampleTest.setLab(lab != null ? beanTest.getFacilityFacade().getReferenceByUuid(lab.getUuid()) : null);
		sampleTest.setTestResult(testResult);
		sampleTest.setTestResultText(testResultText);
		sampleTest.setTestResultVerified(verified);

		if (extraConfig != null) {
			extraConfig.accept(sampleTest);
		}

		sampleTest = beanTest.getPathogenTestFacade().savePathogenTest(sampleTest);
		return sampleTest;
	}

	public PathogenTestDto createPathogenTest(
		EnvironmentSampleReferenceDto sample,
		PathogenTestType testType,
		Pathogen testedPathogen,
		FacilityReferenceDto lab,
		UserReferenceDto labUser,
		PathogenTestResultType testResult,
		Consumer<PathogenTestDto> extraConfig) {

		PathogenTestDto sampleTest = PathogenTestDto.build(sample, labUser);
		sampleTest.setTestType(testType);
		sampleTest.setLab(lab);
		sampleTest.setTestedPathogen(testedPathogen);
		sampleTest.setTestResult(testResult);
		sampleTest.setTestResultVerified(true);
		sampleTest.setTestDateTime(new Date());

		if (extraConfig != null) {
			extraConfig.accept(sampleTest);
		}

		sampleTest = beanTest.getPathogenTestFacade().savePathogenTest(sampleTest);
		return sampleTest;
	}

	public PathogenTestDto createPathogenTest(
		SampleReferenceDto sample,
		PathogenTestType testType,
		Disease testedDisease,
		Date testDateTime,
		FacilityReferenceDto lab,
		UserReferenceDto labUser,
		PathogenTestResultType testResult,
		String testResultText,
		boolean verified,
		Consumer<PathogenTestDto> extraConfig) {

		PathogenTestDto sampleTest = PathogenTestDto.build(sample, labUser);
		sampleTest.setTestedDisease(testedDisease);
		sampleTest.setTestType(testType);
		sampleTest.setTestDateTime(testDateTime);
		sampleTest.setLab(lab);
		sampleTest.setTestResult(testResult);
		sampleTest.setTestResultText(testResultText);
		sampleTest.setTestResultVerified(verified);

		if (extraConfig != null) {
			extraConfig.accept(sampleTest);
		}

		sampleTest = beanTest.getPathogenTestFacade().savePathogenTest(sampleTest);
		return sampleTest;
	}

	public PathogenTestDto createPathogenTest(CaseDataDto associatedCase, PathogenTestType testType, PathogenTestResultType resultType) {
		return createPathogenTest(associatedCase, Disease.CORONAVIRUS, testType, resultType);
	}

	public PathogenTestDto createPathogenTest(SampleReferenceDto sample, CaseDataDto associatedCase) {
		RDCF rdcf = createRDCF("LabRegion", "LabDistrict", "LabCommunity", "LabFacilty");

		return createPathogenTest(
			sample,
			PathogenTestType.ANTIGEN_DETECTION,
			associatedCase.getDisease(),
			new Date(),
			rdcf.facility,
			associatedCase.getReportingUser(),
			PathogenTestResultType.PENDING,
			"",
			false);
	}

	public PathogenTestDto createPathogenTest(
		CaseDataDto associatedCase,
		Disease testedDisease,
		PathogenTestType testType,
		PathogenTestResultType resultType) {

		RDCF rdcf = createRDCF("Region", "District", "Community", "Facility");
		SampleDto sample = createSample(
			new CaseReferenceDto(associatedCase.getUuid()),
			new Date(),
			new Date(),
			associatedCase.getReportingUser(),
			SampleMaterial.BLOOD,
			rdcf.facility);
		return createPathogenTest(
			new SampleReferenceDto(sample.getUuid()),
			testType,
			testedDisease,
			new Date(),
			rdcf.facility,
			associatedCase.getReportingUser(),
			resultType,
			"",
			true);
	}

	public PathogenTestDto buildPathogenTestDto(RDCF rdcf, UserDto user, SampleDto sample, Disease disease, Date testDateTime) {

		final PathogenTestDto newPathogenTest = new PathogenTestDto();

		newPathogenTest.setSample(sample.toReference());
		newPathogenTest.setTestedDisease(disease);
		newPathogenTest.setTestType(PathogenTestType.ISOLATION);

		newPathogenTest.setTestDateTime(testDateTime);
		newPathogenTest.setLab(new FacilityReferenceDto(rdcf.facility.getUuid(), null, null));
		newPathogenTest.setLabUser(user.toReference());
		newPathogenTest.setTestResult(PathogenTestResultType.PENDING);
		newPathogenTest.setTestResultText("all bad!");
		newPathogenTest.setTestResultVerified(false);
		return newPathogenTest;
	}

	public AdditionalTestDto createAdditionalTest(SampleReferenceDto sample) {

		AdditionalTestDto test = AdditionalTestDto.build(sample);
		test.setTestDateTime(new Date());

		test = beanTest.getAdditionalTestFacade().saveAdditionalTest(test);
		return test;
	}

	public ExposureDto buildAnimalContactExposure(TypeOfAnimal typeOfAnimal) {

		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setTypeOfAnimal(typeOfAnimal);
		return exposure;
	}

	public CampaignDto createCampaign(UserDto user) {

		CampaignDto campaign = CampaignDto.build();
		campaign.setCreatingUser(user.toReference());
		campaign.setName("CampaignName");
		campaign.setDescription("Campaign description");

		campaign = beanTest.getCampaignFacade().save(campaign);

		return campaign;
	}

	public CampaignDiagramDefinitionDto createCampaignDiagramDefinition(String diagramId, String diagramCaption) {
		CampaignDiagramDefinitionDto campaignDiagramDefinition = CampaignDiagramDefinitionDto.build();
		campaignDiagramDefinition.setDiagramType(DiagramType.COLUMN);
		campaignDiagramDefinition.setDiagramId(diagramId);
		campaignDiagramDefinition.setDiagramCaption(diagramCaption);

		return campaignDiagramDefinition;
	}

	public CampaignFormMetaDto createCampaignForm(CampaignDto campaign) throws IOException {

		CampaignFormMetaDto campaignForm;

		String schema =
			"[{\"type\": \"text\",\"id\": \"teamNumber\",\"caption\": \"Team number\",\"styles\": [\"first\"]},{\"type\": \"text\",\"id\": "
				+ "\"namesOfTeamMembers\",\"caption\": \"Names of team members\",\"styles\": [\"col-8\"]},{\"type\": \"text\",\"id\": "
				+ "\"monitorName\",\"caption\": \"Name of monitor\",\"styles\": [\"first\"]},{\"type\": \"text\",\"id\": \"agencyName\",\"caption\": "
				+ "\"Agency\"},{\"type\": \"section\",\"id\": \"questionsSection\"},{\"type\": \"label\",\"id\": \"questionsLabel\",\"caption\": \"<h2>Questions</h2>\"}"
				+ ",{\"type\": \"yes-no\",\"id\": \"oneMemberResident\",\"caption\": \"1) At least one team member is resident of same area (villages)?\"},{\"type\": "
				+ "\"yes-no\",\"id\": \"vaccinatorsTrained\",\"caption\": \"2) Both vaccinators trained before this campaign?\"},{\"type\": \"section\","
				+ " \"id\": \"questionsSection2\"},{\"type\": \"label\",\"id\": \"q8To12Label\",\"caption\": \"Q 8-12: Based on observation of team only.\"},"
				+ "{\"type\": \"yes-no\",\"id\": \"askingAboutMonthOlds\",\"caption\": \"8) Is team specially asking about 0-11 months children?\"},"
				+ "{\"type\": \"section\", \"id\": \"questionsSection3\"},{\"type\": \"yes-no\",\"id\": \"atLeastOneMemberChw\","
				+ "\"caption\": \"13) Is at least one member of the team CHW?\"},{\"type\": \"number\",\"id\": "
				+ "\"numberOfChw\",\"caption\": \"No. of CHW\",\"styles\": [\"row\"],\"dependingOn\": \"atLeastOneMemberChw\",\"dependingOnValues\": [\"YES\"]},"
				+ "{\"type\": \"yes-no\",\"id\": \"anyMemberFemale\",\"caption\": \"14) Is any member of the team female?\"},{\"type\": \"yes-no\","
				+ "\"id\": \"accompaniedBySocialMobilizer\",\"caption\": \"15) Does social mobilizer accompany the vaccination team in the field?\"},"
				+ "{\"type\": \"text\",\"id\": \"comments\",\"caption\": \"Comments\",\"styles\": [\"col-12\"]}]";
		String translations =
			"[{\"languageCode\": \"de-DE\", \"translations\": [{\"elementId\": \"teamNumber\", \"caption\": \"Teamnummer\"}, {\"elementId\": \"namesOfTeamMembers\","
				+ " \"caption\": \"Namen der Teammitglieder\"}]}, {\"languageCode\": \"fr-FR\", \"translations\": [{\"elementId\": \"teamNumber\", "
				+ "\"caption\": \"Numéro de l'équipe\"}]}]";

		campaignForm = beanTest.getCampaignFormFacade().buildCampaignFormMetaFromJson("testForm", null, schema, translations);

		campaignForm = beanTest.getCampaignFormFacade().saveCampaignFormMeta(campaignForm);

		return campaignForm;
	}

	public String getCampaignFormData() {
		return "[{\"id\": \"teamNumber\",\"value\": \"12\"},{\"id\": \"namesOfTeamMembers\", \"value\": \"Waldemar Stricker\"},"
			+ "{\"id\": \"monitorName\", \"value\": \"Josef Saks\"},{\"id\": \"agencyName\",\"value\": \"HZI Institut\"},"
			+ "{\"id\": \"oneMemberResident\", \"value\": \"yes\"},{\"id\": \"vaccinatorsTrained\",\"value\": \"no\"},"
			+ "{\"id\": \"askingAboutMonthOlds\",\"value\": \"yes\"},{\"id\": \"atLeastOneMemberChw\",\"value\": \"yes\"},"
			+ "{\"id\": \"numberOfChw\",\"value\": \"7\"},{\"id\": \"anyMemberFemale\",\"value\": \"yes\"},{\"id\": \"accompaniedBySocialMobilizer\",\"value\": \"no\"},"
			+ "{\"id\": \"comments\",\"value\": \"other comments\"}]";
	}

	public CampaignFormDataDto buildCampaignFormDataDto(CampaignDto campaign, CampaignFormMetaDto campaignForm, RDCF rdcf, String formData) {
		CampaignReferenceDto campaignReferenceDto = new CampaignReferenceDto(campaign.getUuid());
		CampaignFormMetaReferenceDto campaignFormMetaReferenceDto = new CampaignFormMetaReferenceDto(campaignForm.getUuid());

		CampaignFormDataDto campaignFormData =
			CampaignFormDataDto.build(campaignReferenceDto, campaignFormMetaReferenceDto, rdcf.region, rdcf.district, rdcf.community);

		try {
			ObjectMapper mapper = new ObjectMapper();
			campaignFormData.setFormValues(Arrays.asList(mapper.readValue(formData, CampaignFormDataEntry[].class)));
			return campaignFormData;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public CampaignFormDataDto createCampaignFormData(CampaignDto campaign, CampaignFormMetaDto campaignForm, RDCF rdcf, String formData) {

		CampaignFormDataDto campaignFormData = buildCampaignFormDataDto(campaign, campaignForm, rdcf, formData);

		campaignFormData = beanTest.getCampaignFormDataFacade().saveCampaignFormData(campaignFormData);

		return campaignFormData;
	}

	public RDCF createRDCF() {
		return createRDCF("Region", "District", "Community", "Facility", "PointOfEntry");
	}

	public RDCF createRDCF(String regionName, String districtName, String communityName, String facilityName) {
		return createRDCF(regionName, districtName, communityName, facilityName, null);
	}

	public RDCF createRDCF(String regionName, String districtName, String communityName, String facilityName, String pointOfEntryName) {

		Region region = createRegion(regionName);
		District district = createDistrict(districtName, region);
		Community community = createCommunity(communityName, district);
		Facility facility = createFacility(facilityName, region, district, community);

		PointOfEntry pointOfEntry = null;
		if (pointOfEntryName != null) {
			pointOfEntry = createPointOfEntry(pointOfEntryName, region, district);
		}

		return new RDCF(
			new RegionReferenceDto(region.getUuid(), region.getName(), region.getExternalID()),
			new DistrictReferenceDto(district.getUuid(), district.getName(), district.getExternalID()),
			new CommunityReferenceDto(community.getUuid(), community.getName(), community.getExternalID()),
			new FacilityReferenceDto(facility.getUuid(), facility.getName(), facility.getExternalID()),
			pointOfEntry != null
				? new PointOfEntryReferenceDto(
					pointOfEntry.getUuid(),
					pointOfEntry.getName(),
					pointOfEntry.getPointOfEntryType(),
					pointOfEntry.getExternalID())
				: null);
	}

	public RDCFEntities createRDCFEntities() {
		return createRDCFEntities("Region", "District", "Community", "Facility");
	}

	public RDCFEntities createRDCFEntities(String regionName, String districtName, String communityName, String facilityName) {

		Region region = createRegion(regionName);
		District district = createDistrict(districtName, region);
		Community community = createCommunity(communityName, district);
		Facility facility = createFacility(facilityName, region, district, community);

		return new RDCFEntities(region, district, community, facility);
	}

	public RDP createRDP() {

		var region = createRegion("Region");
		var district = createDistrict("District", region);
		var pointOfEntry = createPointOfEntry("POE", region, district);

		return new RDP(
			new RegionReferenceDto(region.getUuid(), region.getName(), region.getExternalID()),
			new DistrictReferenceDto(district.getUuid(), district.getName(), district.getExternalID()),
			new PointOfEntryReferenceDto(
				pointOfEntry.getUuid(),
				pointOfEntry.getName(),
				pointOfEntry.getPointOfEntryType(),
				pointOfEntry.getExternalID()));
	}

	public Continent createContinent(String name) {
		Continent continent = new Continent();
		continent.setUuid(DataHelper.createUuid());
		continent.setDefaultName(name);
		beanTest.getContinentService().persist(continent);

		return continent;
	}

	public Country createCountry(String countryName, String isoCode, String unoCode) {
		Country country = new Country();
		country.setUuid(DataHelper.createUuid());
		country.setDefaultName(countryName);
		country.setIsoCode(isoCode);
		country.setUnoCode(unoCode);
		beanTest.getCountryService().persist(country);

		return country;
	}

	public Region createRegion(String regionName) {
		return createRegion(regionName, null);
	}

	public Region createRegion(String regionName, String externalId) {
		Region region = getRegion(regionName, externalId);
		beanTest.getRegionService().persist(region);
		return region;
	}

	public Region createRegionCentrally(String regionName, String externalId) {
		Region region = getRegion(regionName, externalId);
		region.setCentrallyManaged(true);
		beanTest.getRegionService().persist(region);
		return region;
	}

	@NotNull
	private Region getRegion(String regionName, String externalId) {
		Region region = new Region();
		region.setUuid(DataHelper.createUuid());
		region.setName(regionName);
		region.setEpidCode("COU-REG");
		region.setExternalID(externalId);
		return region;
	}

	public District createDistrict(String districtName, Region region) {
		return createDistrict(districtName, region, null);
	}

	public District createDistrict(String districtName, Region region, String externalId) {
		District district = getDistrict(districtName, region, externalId);
		beanTest.getDistrictService().persist(district);
		return district;
	}

	public District createDistrictCentrally(String districtName, Region region, String externalId) {
		District district = getDistrict(districtName, region, externalId);
		district.setCentrallyManaged(true);
		beanTest.getDistrictService().persist(district);
		return district;
	}

	@NotNull
	private District getDistrict(String districtName, Region region, String externalId) {
		District district = new District();
		district.setUuid(DataHelper.createUuid());
		district.setName(districtName);
		district.setRegion(region);
		district.setEpidCode("DIS");
		district.setExternalID(externalId);
		return district;
	}

	public Community createCommunity(String communityName, District district) {
		return createCommunity(communityName, district, null);
	}

	public Community createCommunity(String communityName, District district, String externalId) {
		Community community = getCommunity(communityName, district, externalId);
		beanTest.getCommunityService().persist(community);
		return community;
	}

	public Community createCommunityCentrally(String communityName, District district, String externalId) {
		Community community = getCommunity(communityName, district, externalId);
		community.setCentrallyManaged(true);
		beanTest.getCommunityService().persist(community);
		return community;

	}

	@NotNull
	private Community getCommunity(String communityName, District district, String externalId) {
		Community community = new Community();
		community.setUuid(DataHelper.createUuid());
		community.setName(communityName);
		community.setDistrict(district);
		community.setExternalID(externalId);
		return community;
	}

	public CommunityDto createCommunity(String communityName, DistrictReferenceDto district) {
		CommunityDto community = CommunityDto.build();
		community.setName(communityName);
		community.setDistrict(district);
		beanTest.getCommunityFacade().save(community);
		return community;
	}

	public Facility createFacility(String facilityName, Region region, District district, Community community) {
		return createFacility(facilityName, FacilityType.HOSPITAL, region, district, community);
	}

	public Facility createFacility(String facilityName, FacilityType type, Region region, District district, Community community) {
		return createFacility(facilityName, type, region, district, community, null);
	}

	public Facility createFacility(String facilityName, FacilityType type, Region region, District district, Community community, String externalId) {
		Facility facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setName(facilityName);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		facility.setType(type);
		facility.setExternalID(externalId);

		beanTest.getFacilityService().persist(facility);

		return facility;
	}

	public FacilityDto createFacility(
		String facilityName,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community) {
		return createFacility(facilityName, region, district, community, null);
	}

	public FacilityDto createFacility(
		String facilityName,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community,
		FacilityType type) {

		FacilityDto facility = FacilityDto.build();
		facility.setName(facilityName);
		facility.setType(type == null ? FacilityType.HOSPITAL : type);
		facility.setCommunity(community);
		facility.setDistrict(district);
		facility.setRegion(region);
		beanTest.getFacilityFacade().save(facility);
		return facility;
	}

	public FacilityDto createFacility(
		String facilityName,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Consumer<FacilityDto> extraConfig) {

		FacilityDto facility = FacilityDto.build();
		facility.setName(facilityName);
		facility.setRegion(region);
		facility.setDistrict(district);
		facility.setType(FacilityType.HOSPITAL);

		if (extraConfig != null) {
			extraConfig.accept(facility);
		}

		beanTest.getFacilityFacade().save(facility);
		return facility;
	}

	public PointOfEntry createPointOfEntry(String pointOfEntryName, Region region, District district) {
		return createPointOfEntry(pointOfEntryName, region, district, null);
	}

	public PointOfEntry createPointOfEntry(String pointOfEntryName, Region region, District district, String externalId) {
		PointOfEntry pointOfEntry = new PointOfEntry();
		pointOfEntry.setUuid(DataHelper.createUuid());
		pointOfEntry.setPointOfEntryType(PointOfEntryType.AIRPORT);
		pointOfEntry.setName(pointOfEntryName);
		pointOfEntry.setDistrict(district);
		pointOfEntry.setRegion(region);
		pointOfEntry.setExternalID(externalId);

		beanTest.getPointOfEntryService().persist(pointOfEntry);

		return pointOfEntry;
	}

	public PointOfEntryDto createPointOfEntry(String pointOfEntryName, RegionReferenceDto region, DistrictReferenceDto district) {
		PointOfEntryDto pointOfEntry = PointOfEntryDto.build();
		pointOfEntry.setUuid(DataHelper.createUuid());
		pointOfEntry.setPointOfEntryType(PointOfEntryType.AIRPORT);
		pointOfEntry.setName(pointOfEntryName);
		pointOfEntry.setDistrict(district);
		pointOfEntry.setRegion(region);
		beanTest.getPointOfEntryFacade().save(pointOfEntry);

		return pointOfEntry;
	}

	public PopulationDataDto createPopulationData(RegionReferenceDto region, DistrictReferenceDto district, Integer population, Date collectionDate) {
		PopulationDataDto populationData = PopulationDataDto.build(collectionDate);
		populationData.setRegion(region);
		populationData.setDistrict(district);
		populationData.setPopulation(population);
		beanTest.getPopulationDataFacade().savePopulationData(Arrays.asList(populationData));
		return populationData;
	}

	public void updateDiseaseConfiguration(
		Disease disease,
		Boolean active,
		Boolean primary,
		Boolean caseSurveillance,
		Boolean aggregateReporting,
		List<String> ageGroups) {
		DiseaseConfigurationDto config =
			DiseaseConfigurationFacadeEjbLocal.toDto(beanTest.getDiseaseConfigurationService().getDiseaseConfiguration(disease));
		config.setActive(active);
		config.setPrimaryDisease(primary);
		config.setCaseSurveillanceEnabled(caseSurveillance);
		config.setAggregateReportingEnabled(aggregateReporting);
		config.setAgeGroups(ageGroups);
		beanTest.getDiseaseConfigurationFacade().saveDiseaseConfiguration(config);
	}

	public DocumentDto createDocument(
		UserReferenceDto uploadingUser,
		String name,
		String contentType,
		long size,
		EventReferenceDto event,
		byte[] content)
		throws IOException {
		return createDocument(uploadingUser, name, contentType, size, DocumentRelatedEntityType.EVENT, event.getUuid(), content);
	}

	public DocumentDto createDocument(
		UserReferenceDto uploadingUser,
		String name,
		String contentType,
		long size,
		DocumentRelatedEntityType relatedEntityType,
		String relatedEntityUuid,
		byte[] content)
		throws IOException {
		DocumentDto document = DocumentDto.build();
		document.setUploadingUser(uploadingUser);
		document.setName(name);
		document.setMimeType(contentType);
		document.setSize(size);
		document.setRelatedEntityType(relatedEntityType);
		document.setRelatedEntityUuid(relatedEntityUuid);

		return beanTest.getDocumentFacade().saveDocument(document, content);
	}

	public ExportConfigurationDto createExportConfiguration(String name, ExportType exportType, Set<String> properites, UserReferenceDto user) {
		ExportConfigurationDto exportConfiguration = ExportConfigurationDto.build(user, exportType);
		exportConfiguration.setName(name);
		exportConfiguration.setProperties(properites);

		beanTest.getExportFacade().saveExportConfiguration(exportConfiguration);

		return exportConfiguration;
	}

	public SystemEventDto createSystemEvent(SystemEventType type, Date startDate, SystemEventStatus status) {
		return createSystemEvent(type, startDate, new Date(startDate.getTime() + 1000), status, "Generated for test purposes");
	}

	public SystemEventDto createSystemEvent(SystemEventType type, Date startDate, Date endDate, SystemEventStatus status, String additionalInfo) {
		SystemEventDto systemEvent = SystemEventDto.build();
		systemEvent.setType(type);
		systemEvent.setStartDate(startDate);
		systemEvent.setEndDate(endDate);
		systemEvent.setStatus(status);
		systemEvent.setAdditionalInfo(additionalInfo);
		return systemEvent;
	}

	public ExternalMessageDto createExternalMessage(Consumer<ExternalMessageDto> customSettings) {
		ExternalMessageDto message = ExternalMessageDto.build();

		if (customSettings != null) {
			customSettings.accept(message);
		}

		beanTest.getExternalMessageFacade().save(message);

		return message;
	}

	public ExternalMessageDto createLabMessageWithTestReportAndSurveillanceReport(
		UserReferenceDto user,
		CaseReferenceDto caze,
		SampleReferenceDto sample) {
		SurveillanceReportDto surveillanceReportDto = createSurveillanceReport(user, ReportingType.LABORATORY, caze);
		ExternalMessageDto labMessage = createExternalMessage(lm -> {
			lm.setType(ExternalMessageType.LAB_MESSAGE);
			lm.setSurveillanceReport(surveillanceReportDto.toReference());
			lm.setStatus(ExternalMessageStatus.PROCESSED);
		});
		SampleReportDto sampleReport = createSampleReport(labMessage, sample);
		createTestReport(sampleReport);
		return labMessage;

	}

	public ExternalMessageDto createLabMessageWithSurveillanceReport(UserReferenceDto user, CaseReferenceDto caze) {
		SurveillanceReportDto surveillanceReportDto = createSurveillanceReport(user, ReportingType.LABORATORY, caze);
		ExternalMessageDto labMessage = createExternalMessage(lm -> {
			lm.setType(ExternalMessageType.LAB_MESSAGE);
			lm.setSurveillanceReport(surveillanceReportDto.toReference());
			lm.setStatus(ExternalMessageStatus.PROCESSED);
		});

		return labMessage;

	}

	public ExternalMessageDto createLabMessageWithTestReport(SampleReferenceDto sample) {
		ExternalMessageDto labMessage = createExternalMessage(lm -> lm.setType(ExternalMessageType.LAB_MESSAGE));
		SampleReportDto sampleReport = createSampleReport(labMessage, sample);
		createTestReport(sampleReport);
		return labMessage;

	}

	public SampleReportDto createSampleReport(ExternalMessageDto labMessage, SampleReferenceDto sample) {
		SampleReportDto sampleReport = createSampleReport(labMessage);
		sampleReport.setSample(sample);
		sampleReport.setChangeDate(new Date());
		beanTest.getSampleReportFacade().saveSampleReport(sampleReport);
		return sampleReport;
	}

	public SampleReportDto createSampleReport(ExternalMessageDto labMessage) {
		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);
		beanTest.getSampleReportFacade().saveSampleReport(sampleReport);
		labMessage.setChangeDate(new Date());
		beanTest.getExternalMessageFacade().save(labMessage);
		return sampleReport;
	}

	public TestReportDto createTestReport(SampleReportDto sampleReport) {
		TestReportDto testReport = TestReportDto.build();
		sampleReport.addTestReport(testReport);

		beanTest.getTestReportFacade().saveTestReport(testReport);
		sampleReport.setChangeDate(new Date());
		beanTest.getSampleReportFacade().saveSampleReport(sampleReport);

		return testReport;
	}

	public DiseaseVariant createDiseaseVariant(String name, Disease disease) {

		CustomizableEnumValue diseaseVariant = new CustomizableEnumValue();
		diseaseVariant.setDataType(CustomizableEnumType.DISEASE_VARIANT);
		diseaseVariant.setValue("BF.1.2");
		diseaseVariant.setDiseases(Collections.singletonList(disease));
		diseaseVariant.setCaption(name + " variant");

		beanTest.getCustomizableEnumValueService().ensurePersisted(diseaseVariant);

		return beanTest.getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, name);
	}

	public Pathogen createPathogen(String value, String caption) {

		CustomizableEnumValue pathogen = new CustomizableEnumValue();
		pathogen.setDataType(CustomizableEnumType.PATHOGEN);
		pathogen.setValue(value);
		pathogen.setCaption(caption);

		beanTest.getCustomizableEnumValueService().ensurePersisted(pathogen);

		beanTest.getCustomizableEnumFacade().loadData();

		return beanTest.getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.PATHOGEN, value);
	}

	public ExternalShareInfo createExternalShareInfo(
		CaseReferenceDto caze,
		UserReferenceDto sender,
		ExternalShareStatus status,
		Consumer<ExternalShareInfo> customConfig) {
		ExternalShareInfo shareInfo = new ExternalShareInfo();
		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCaze(beanTest.getCaseService().getByUuid(caze.getUuid()));
		shareInfo.setSender(beanTest.getUserService().getByUuid(sender.getUuid()));
		shareInfo.setStatus(status);

		if (customConfig != null) {
			customConfig.accept(shareInfo);
		}

		beanTest.getExternalShareInfoService().ensurePersisted(shareInfo);

		return shareInfo;
	}

	public ExternalShareInfo createExternalShareInfo(EventReferenceDto event, UserReferenceDto sender, ExternalShareStatus status) {
		ExternalShareInfo shareInfo = new ExternalShareInfo();
		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setEvent(beanTest.getEventService().getByUuid(event.getUuid()));
		shareInfo.setSender(beanTest.getUserService().getByUuid(sender.getUuid()));
		shareInfo.setStatus(status);

		beanTest.getExternalShareInfoService().ensurePersisted(shareInfo);

		return shareInfo;
	}

	/**
	 * Creates a list with {@code count} values of type {@code T}.
	 * The list index is given to the {@code valueSupplier} for each value to create.
	 */
	public static <T> List<T> createValuesList(int count, Function<Integer, T> valueSupplier) {

		List<T> values = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			values.add(valueSupplier.apply(i));
		}
		return values;
	}

	public SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(
		String serverId,
		boolean ownershipHandedOver,
		Consumer<SormasToSormasOriginInfoDto> extraConfig) {
		SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setSenderName("Test Name");
		originInfo.setSenderEmail("test@email.com");
		originInfo.setOrganizationId(serverId);
		originInfo.setOwnershipHandedOver(ownershipHandedOver);

		if (extraConfig != null) {
			extraConfig.accept(originInfo);
		}

		return beanTest.getSormasToSormasOriginInfoFacade().saveOriginInfo(originInfo);
	}

	public CaseDataDto createReceivedCase(UserReferenceDto user, PersonReferenceDto person, RDCF rdcf, boolean ownershipHandedOver) {
		return createCase(
			user,
			person,
			rdcf,
			(c) -> c.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo("source_id", ownershipHandedOver, null)));
	}

	public CaseDataDto createSharedCase(UserReferenceDto user, PersonReferenceDto person, RDCF rdcf, boolean ownershipHandedOver) {
		CaseDataDto caze = createCase(user, person, rdcf, null);

		createShareRequestInfo(
			ShareRequestDataType.CASE,
			beanTest.getUserService().getByReferenceDto(user),
			"target_id",
			ownershipHandedOver,
			ShareRequestStatus.ACCEPTED,
			(s) -> s.setCaze(beanTest.getCaseService().getByReferenceDto(caze.toReference())));

		return caze;
	}

	public ContactDto createReceivedContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, boolean ownershipHandedOver) {
		return createContact(
			reportingUser,
			contactPerson,
			Disease.CORONAVIRUS,
			(c) -> c.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo("source_id", ownershipHandedOver, null)));
	}

	public ContactDto createSharedContact(UserReferenceDto reportingUser, PersonReferenceDto contactPerson, boolean ownershipHandedOver) {
		ContactDto contact = createContact(reportingUser, contactPerson, Disease.CORONAVIRUS);
		createShareRequestInfo(
			ShareRequestDataType.CONTACT,
			beanTest.getUserService().getByReferenceDto(reportingUser),
			"target_id",
			ownershipHandedOver,
			ShareRequestStatus.ACCEPTED,
			(s) -> s.setContact(beanTest.getContactService().getByReferenceDto(contact.toReference())));

		return contact;
	}

	public EventParticipantDto createReceivedEventParticipant(PersonDto eventPerson, UserReferenceDto reportingUser, RDCF rdcf) {
		return createEventParticipant(createEvent(reportingUser).toReference(), eventPerson, "Test involvment", reportingUser, e -> {
			e.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo("source_id", true, null));
		}, rdcf);
	}

	public ImmunizationDto createReceivedImmunization(PersonReferenceDto person, UserReferenceDto reportingUser, RDCF rdcf) {
		return createImmunization(Disease.CORONAVIRUS, person, reportingUser, rdcf, i -> {
			i.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo("source_id", true, null));
		});
	}

	public ShareRequestInfo createShareRequestInfo(
		ShareRequestDataType dataType,
		User sender,
		String serverId,
		boolean ownershipHandedOver,
		ShareRequestStatus status,
		Consumer<SormasToSormasShareInfo> setTarget) {

		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();
		shareInfo.setOwnershipHandedOver(ownershipHandedOver);
		shareInfo.setOrganizationId(serverId);
		setTarget.accept(shareInfo);

		ShareRequestInfo requestInfo = new ShareRequestInfo();
		requestInfo.setUuid(DataHelper.createUuid());
		requestInfo.setDataType(dataType);
		requestInfo.setSender(sender);
		requestInfo.setRequestStatus(status);
		requestInfo.setShares(new ArrayList<>());
		requestInfo.getShares().add(shareInfo);

		beanTest.getShareRequestInfoService().persist(requestInfo);

		return requestInfo;
	}

	public EnvironmentDto createEnvironment(
		String name,
		EnvironmentMedia environmentMedia,
		UserReferenceDto reportingUser,
		RDCF rdcf,
		Consumer<EnvironmentDto> extraConfig) {
		EnvironmentDto environment = EnvironmentDto.build();
		environment.setEnvironmentName(name);
		environment.setEnvironmentMedia(environmentMedia);
		environment.setReportDate(new Date());
		environment.setReportingUser(reportingUser);

		if (rdcf != null) {
			LocationDto location = environment.getLocation();
			location.setRegion(rdcf.region);
			location.setDistrict(rdcf.district);
			location.setCommunity(rdcf.community);
		}

		if (extraConfig != null) {
			extraConfig.accept(environment);
		}

		environment = beanTest.getEnvironmentFacade().save(environment);

		return environment;

	}

	public EnvironmentDto createEnvironment(String name, EnvironmentMedia environmentMedia, UserReferenceDto reportingUser, RDCF rdcf) {
		return createEnvironment(name, environmentMedia, reportingUser, rdcf, null);
	}

	public EnvironmentSampleDto createEnvironmentSample(
		EnvironmentReferenceDto environment,
		UserReferenceDto reportingUser,
		FacilityReferenceDto lab,
		@Nullable Consumer<EnvironmentSampleDto> extraConfig) {
		EnvironmentSampleDto sample = EnvironmentSampleDto.build(environment, reportingUser);
		sample.setSampleMaterial(EnvironmentSampleMaterial.WATER);
		sample.setLaboratory(lab);

		if (extraConfig != null) {
			extraConfig.accept(sample);
		}

		return beanTest.getEnvironmentSampleFacade().save(sample);
	}

	/**
	 * @author MartinWahnschaffe
	 * @deprecated Use RDCF instead
	 */
	@Deprecated
	public static class RDCFEntities {

		public Region region;
		public District district;
		public Community community;
		public Facility facility;

		public RDCFEntities(Region region, District district, Community community, Facility facility) {
			this.region = region;
			this.district = district;
			this.community = community;
			this.facility = facility;
		}
	}

	public static class RDCF {

		public RegionReferenceDto region;
		public DistrictReferenceDto district;
		public CommunityReferenceDto community;
		public FacilityReferenceDto facility;
		public PointOfEntryReferenceDto pointOfEntry;

		public RDCF(RegionReferenceDto region, DistrictReferenceDto district, CommunityReferenceDto community, FacilityReferenceDto facility) {
			this(region, district, community, facility, null);
		}

		public RDCF(
			RegionReferenceDto region,
			DistrictReferenceDto district,
			CommunityReferenceDto community,
			FacilityReferenceDto facility,
			PointOfEntryReferenceDto pointOfEntry) {
			this.region = region;
			this.district = district;
			this.community = community;
			this.facility = facility;
			this.pointOfEntry = pointOfEntry;
		}

		public RDCF(RDCFEntities rdcfEntities) {
			this.region = new RegionReferenceDto(rdcfEntities.region.getUuid(), rdcfEntities.region.getName(), rdcfEntities.region.getExternalID());
			this.district =
				new DistrictReferenceDto(rdcfEntities.district.getUuid(), rdcfEntities.district.getName(), rdcfEntities.district.getExternalID());
			this.community =
				new CommunityReferenceDto(rdcfEntities.community.getUuid(), rdcfEntities.community.getName(), rdcfEntities.community.getExternalID());
			this.facility =
				new FacilityReferenceDto(rdcfEntities.facility.getUuid(), rdcfEntities.facility.getName(), rdcfEntities.facility.getExternalID());
		}
	}

	public static class RDP {

		public RegionReferenceDto region;
		public DistrictReferenceDto district;
		public PointOfEntryReferenceDto pointOfEntry;

		public RDP(RegionReferenceDto region, DistrictReferenceDto district, PointOfEntryReferenceDto pointOfEntry) {
			this.region = region;
			this.district = district;
			this.pointOfEntry = pointOfEntry;
		}
	}
}
