/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseIdentificationSource;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InfectionSetting;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.ReinfectionDetail;
import de.symeda.sormas.api.caze.ReinfectionStatus;
import de.symeda.sormas.api.caze.ScreeningType;
import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ArmedForcesRelationType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.location.Location;

public class CaseExportMapperDto extends CaseExportDto {

	public CaseExportMapperDto(
		long id,
		long personId,
		Double personAddressLatitude,
		Double personAddressLongitude,
		Float personAddressLatLonAcc,
		long epiDataId,
		long symptomsId,
		long hospitalizationId,
		long healthConditionsId,
		String uuid,
		String epidNumber,
		Disease disease,
		DiseaseVariant diseaseVariant,
		String diseaseDetails,
		String diseaseVariantDetails,
		String personUuid,
		String firstName,
		String lastName,
		Salutation salutation,
		String otherSalutation,
		Sex sex,
		YesNoUnknown pregnant,
		Integer approximateAge,
		ApproximateAgeType approximateAgeType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		Date reportDate,
		String region,
		String district,
		String community,
		FacilityType facilityType,
		String healthFacility,
		String healthFacilityUuid,
		String healthFacilityDetails,
		String pointOfEntry,
		String pointOfEntryUuid,
		String pointOfEntryDetails,
		CaseClassification caseClassification,
		YesNoUnknown clinicalConfirmation,
		YesNoUnknown epidemiologicalConfirmation,
		YesNoUnknown laboratoryDiagnosticConfirmation,
		Boolean notACaseReasonNegativeTest,
		Boolean notACaseReasonPhysicianInformation,
		Boolean notACaseReasonDifferentPathogen,
		Boolean notACaseReasonOther,
		String notACaseReasonDetails,
		InvestigationStatus investigationStatus,
		Date investigatedDate,
		CaseOutcome outcome,
		Date outcomeDate,
		YesNoUnknown sequelae,
		String sequelaeDetails,
		YesNoUnknown bloodOrganOrTissueDonated,
		FollowUpStatus followUpStatus,
		Date followUpUntil,
		Boolean nosocomialOutbreak,
		InfectionSetting infectionSetting,
		YesNoUnknown prohibitionToWork,
		Date prohibitionToWorkFrom,
		Date prohibitionToWorkUntil,
		YesNoUnknown reInfection,
		Date previousInfectionDate,
		ReinfectionStatus reinfectionStatus,
		Object reinfectionDetails,
		// Quarantine
		QuarantineType quarantine,
		String quarantineTypeDetails,
		Date quarantineFrom,
		Date quarantineTo,
		String quarantineHelpNeeded,
		boolean quarantineOrderedVerbally,
		boolean quarantineOrderedOfficialDocument,
		Date quarantineOrderedVerballyDate,
		Date quarantineOrderedOfficialDocumentDate,
		boolean quarantineExtended,
		boolean quarantineReduced,
		boolean quarantineOfficialOrderSent,
		Date quarantineOfficialOrderSentDate,
		YesNoUnknown admittedToHealthFacility,
		Date admissionDate,
		Date dischargeDate,
		YesNoUnknown leftAgainstAdvice,
		PresentCondition presentCondition,
		Date deathDate,
		Date burialDate,
		BurialConductor burialConductor,
		String burialPlaceDescription,
		String addressRegion,
		String addressDistrict,
		String addressCommunity,
		String city,
		String street,
		String houseNumber,
		String additionalInformation,
		String postalCode,
		String facility,
		String facilityUuid,
		String facilityDetails,
		String phone,
		String phoneOwner,
		String emailAddress,
		String otherContactDetails,
		EducationType educationType,
		String educationDetails,
		OccupationType occupationType,
		String occupationDetails,
		ArmedForcesRelationType armedForcesRelationType,
		YesNoUnknown contactWithSourceCaseKnown,
		//Date onsetDate,
		VaccinationStatus vaccinationStatus,
		YesNoUnknown postpartum,
		Trimester trimester,
		long eventCount,
		Long prescriptionCount,
		Long treatmentCount,
		Long clinicalVisitCount,
		String externalID,
		String externalToken,
		String internalToken,
		String birthName,
		String birthCountryIsoCode,
		String birthCountryName,
		String citizenshipIsoCode,
		String citizenshipCountryName,
		CaseIdentificationSource caseIdentificationSource,
		ScreeningType screeningType,
		// responsible jurisdiction
		String responsibleRegion,
		String responsibleDistrict,
		String responsibleCommunity,
		// clinician
		String clinicianName,
		String clinicianPhone,
		String clinicianEmail,
		// users
		Long reportingUserId,
		Long followUpStatusChangeUserId,
		Date previousQuarantineTo,
		String quarantineChangeComment,
		String associatedWithOutbreak,
		boolean isInJurisdiction) {

		setId(id);
		setPersonId(personId);
		setAddressGpsCoordinates(Location.buildGpsCoordinatesCaption(personAddressLatitude, personAddressLongitude, personAddressLatLonAcc));
		setEpiDataId(epiDataId);
		setSymptomsId(symptomsId);
		setHospitalizationId(hospitalizationId);
		setHealthConditionsId(healthConditionsId);
		setUuid(uuid);
		setEpidNumber(epidNumber);
		setArmedForcesRelationType(armedForcesRelationType);
		setDisease(disease);
		setDiseaseDetails(diseaseDetails);
		setDiseaseVariant(diseaseVariant);
		setDiseaseVariantDetails(diseaseVariantDetails);
		setPersonUuid(personUuid);
		setFirstName(firstName);
		setLastName(lastName);
		setSalutation(salutation);
		setOtherSalutation(otherSalutation);
		setSex(sex);
		setPregnant(pregnant);
		setApproximateAge(ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType));
		setAgeGroup(ApproximateAgeType.ApproximateAgeHelper.getAgeGroupFromAge(approximateAge, approximateAgeType));
		setBirthdate(new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY));
		setReportDate(reportDate);
		setRegion(region);
		setDistrict(district);
		setCommunity(community);
		setCaseClassification(caseClassification);
		setClinicalConfirmation(clinicalConfirmation);
		setEpidemiologicalConfirmation(epidemiologicalConfirmation);
		setLaboratoryDiagnosticConfirmation(laboratoryDiagnosticConfirmation);
		setNotACaseReasonNegativeTest(notACaseReasonNegativeTest);
		setNotACaseReasonPhysicianInformation(notACaseReasonPhysicianInformation);
		setNotACaseReasonDifferentPathogen(notACaseReasonDifferentPathogen);
		setNotACaseReasonOther(notACaseReasonOther);
		setNotACaseReasonDetails(notACaseReasonDetails);
		setInvestigationStatus(investigationStatus);
		setInvestigatedDate(investigatedDate);
		setOutcome(outcome);
		setOutcomeDate(outcomeDate);
		setSequelae(sequelae);
		setSequelaeDetails(sequelaeDetails);
		setBloodOrganOrTissueDonated(bloodOrganOrTissueDonated);
		setNosocomialOutbreak(nosocomialOutbreak);
		setInfectionSetting(infectionSetting);
		setProhibitionToWork(prohibitionToWork);
		setProhibitionToWorkFrom(prohibitionToWorkFrom);
		setProhibitionToWorkUntil(prohibitionToWorkUntil);
		setReInfection(reInfection);
		setPreviousInfectionDate(previousInfectionDate);
		setReinfectionStatus(reinfectionStatus);
		setReinfectionDetails(DataHelper.buildStringFromTrueValues((Map<ReinfectionDetail, Boolean>) reinfectionDetails));
		setQuarantine(quarantine);
		setQuarantineTypeDetails(quarantineTypeDetails);
		setQuarantineFrom(quarantineFrom);
		setQuarantineTo(quarantineTo);
		setQuarantineHelpNeeded(quarantineHelpNeeded);
		setQuarantineOrderedVerbally(quarantineOrderedVerbally);
		setQuarantineOrderedOfficialDocument(quarantineOrderedOfficialDocument);
		setQuarantineOrderedVerballyDate(quarantineOrderedVerballyDate);
		setQuarantineOrderedOfficialDocumentDate(quarantineOrderedOfficialDocumentDate);
		setQuarantineExtended(quarantineExtended);
		setQuarantineReduced(quarantineReduced);
		setQuarantineOfficialOrderSent(quarantineOfficialOrderSent);
		setQuarantineOfficialOrderSentDate(quarantineOfficialOrderSentDate);
		setFacilityType(facilityType);
		setHealthFacility(FacilityHelper.buildFacilityString(healthFacilityUuid, healthFacility));
		setHealthFacilityDetails(healthFacilityDetails);
		setPointOfEntry(InfrastructureHelper.buildPointOfEntryString(pointOfEntryUuid, pointOfEntry));
		setPointOfEntryDetails(pointOfEntryDetails);
		setAdmittedToHealthFacility(admittedToHealthFacility);
		setAdmissionDate(admissionDate);
		setDischargeDate(dischargeDate);
		setLeftAgainstAdvice(leftAgainstAdvice);
		setPresentCondition(presentCondition);
		setDeathDate(deathDate);
		setBurialInfo(new BurialInfoDto(burialDate, burialConductor, burialPlaceDescription));
		setAddressRegion(addressRegion);
		setAddressDistrict(addressDistrict);
		setAddressCommunity(addressCommunity);
		setCity(city);
		setStreet(street);
		setHouseNumber(houseNumber);
		setAdditionalInformation(additionalInformation);
		setPostalCode(postalCode);
		setFacility(FacilityHelper.buildFacilityString(facilityUuid, facility));
		setFacilityDetails(facilityDetails);
		setPhone(phone);
		setPhoneOwner(phoneOwner);
		setEmailAddress(emailAddress);
		setOtherContactDetails(otherContactDetails);
		setEducationType(educationType);
		setEducationDetails(educationDetails);
		setOccupationType(occupationType);
		setOccupationDetails(occupationDetails);
		setContactWithSourceCaseKnown(contactWithSourceCaseKnown);
		setVaccinationStatus(vaccinationStatus);

		setPostpartum(postpartum);
		setTrimester(trimester);
		setFollowUpStatus(followUpStatus);
		setFollowUpUntil(followUpUntil);
		setEventCount(eventCount);
		setNumberOfPrescriptions(prescriptionCount != null ? prescriptionCount.intValue() : 0);
		setNumberOfTreatments(treatmentCount != null ? treatmentCount.intValue() : 0);
		setNumberOfClinicalVisits(clinicalVisitCount != null ? clinicalVisitCount.intValue() : 0);
		setExternalID(externalID);
		setExternalToken(externalToken);
		setInternalToken(internalToken);
		setBirthName(birthName);
		setBirthCountry(I18nProperties.getCountryName(birthCountryIsoCode, birthCountryName));
		setCitizenship(I18nProperties.getCountryName(citizenshipIsoCode, citizenshipCountryName));
		setCaseIdentificationSource(caseIdentificationSource);
		setScreeningType(screeningType);

		setResponsibleRegion(responsibleRegion);
		setResponsibleDistrict(responsibleDistrict);
		setResponsibleCommunity(responsibleCommunity);

		setClinicianName(clinicianName);
		setClinicianPhone(clinicianPhone);
		setClinicianEmail(clinicianEmail);
		setReportingUserId(reportingUserId);
		setFollowUpStatusChangeUserId(followUpStatusChangeUserId);

		setPreviousQuarantineTo(previousQuarantineTo);
		setQuarantineChangeComment(quarantineChangeComment);

		setAssociatedWithOutbreak(associatedWithOutbreak);

		setInJurisdiction(isInJurisdiction);
	}
}
