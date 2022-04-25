/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.symeda.sormas.backend.caze;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseIdentificationSource;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.EmbeddedSampleExportDto;
import de.symeda.sormas.api.caze.InfectionSetting;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.ReinfectionDetail;
import de.symeda.sormas.api.caze.ReinfectionStatus;
import de.symeda.sormas.api.caze.ScreeningType;
import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventStatus;
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
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;

public class CaseExportMapperDto implements Serializable {

	private String country;
	private long id;
	private long personId;
	private long epiDataId;
	private long hospitalizationId;
	private long healthConditionsId;
	private String uuid;
	private String epidNumber;
	private Disease disease;
	private String diseaseDetails;
	private DiseaseVariant diseaseVariant;
	private String diseaseVariantDetails;
	private String personUuid;

	private String firstName;

	private String lastName;
	private Salutation salutation;

	private String otherSalutation;
	private Sex sex;
	private YesNoUnknown pregnant;
	private String approximateAge;
	private String ageGroup;
	private BirthDateDto birthdate;
	private Date reportDate;
	private String region;
	private String district;

	private String community;
	private FacilityType facilityType;

	private String healthFacility;

	private String healthFacilityDetails;

	private String pointOfEntry;

	private String pointOfEntryDetails;
	private CaseClassification caseClassification;
	private YesNoUnknown clinicalConfirmation;
	private YesNoUnknown epidemiologicalConfirmation;
	private YesNoUnknown laboratoryDiagnosticConfirmation;
	private Boolean notACaseReasonNegativeTest;
	private Boolean notACaseReasonPhysicianInformation;
	private Boolean notACaseReasonDifferentPathogen;
	private Boolean notACaseReasonOther;
	private String notACaseReasonDetails;
	private CaseIdentificationSource caseIdentificationSource;
	private ScreeningType screeningType;
	private InvestigationStatus investigationStatus;
	private Date investigatedDate;
	private CaseClassification maxSourceCaseClassification;
	private CaseOutcome outcome;
	private Date outcomeDate;
	private YesNoUnknown sequelae;

	private String sequelaeDetails;
	private YesNoUnknown bloodOrganOrTissueDonated;
	private String associatedWithOutbreak;
	private YesNoUnknown admittedToHealthFacility;
	private Date admissionDate;
	private Date dischargeDate;
	private YesNoUnknown leftAgainstAdvice;

	private String initialDetectionPlace;
	private PresentCondition presentCondition;
	private Date deathDate;
	private BurialInfoDto burialInfo;
	private String addressRegion;
	private String addressDistrict;

	private String addressCommunity;

	private String city;

	private String street;

	private String houseNumber;

	private String additionalInformation;

	private String postalCode;

	private String addressGpsCoordinates;

	private String facility;

	private String facilityDetails;

	private String phone;

	private String phoneOwner;

	private String emailAddress;

	private String otherContactDetails;
	private OccupationType occupationType;

	private String occupationDetails;
	private ArmedForcesRelationType armedForcesRelationType;
	private EducationType educationType;

	private String educationDetails;
	private String travelHistory;
	private boolean traveled;
	private boolean burialAttended;
	private YesNoUnknown contactWithSourceCaseKnown;
	private SymptomsDto symptoms;
	//	private Date onsetDate;
//	private String symptoms;
	private VaccinationStatus vaccinationStatus;
	private String numberOfDoses;
	private VaccinationInfoSource vaccinationInfoSource;
	private Date firstVaccinationDate;
	private Date lastVaccinationDate;
	private Vaccine vaccineName;
	private String otherVaccineName;
	private VaccineManufacturer vaccineManufacturer;
	private String otherVaccineManufacturer;
	private String vaccineInn;
	private String vaccineBatchNumber;
	private String vaccineUniiCode;
	private String vaccineAtcCode;
	private HealthConditionsDto healthConditions;
	private int numberOfPrescriptions;
	private int numberOfTreatments;
	private int numberOfClinicalVisits;
	private EmbeddedSampleExportDto sample1 = new EmbeddedSampleExportDto();
	private EmbeddedSampleExportDto sample2 = new EmbeddedSampleExportDto();
	private EmbeddedSampleExportDto sample3 = new EmbeddedSampleExportDto();
	private List<EmbeddedSampleExportDto> otherSamples = new ArrayList<>();

	private Boolean nosocomialOutbreak;
	private InfectionSetting infectionSetting;

	private YesNoUnknown prohibitionToWork;
	private Date prohibitionToWorkFrom;
	private Date prohibitionToWorkUntil;

	private YesNoUnknown reInfection;
	private Date previousInfectionDate;
	private ReinfectionStatus reinfectionStatus;
	private String reinfectionDetails;

	private QuarantineType quarantine;

	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;

	private String quarantineHelpNeeded;
	private boolean quarantineOrderedVerbally;
	private boolean quarantineOrderedOfficialDocument;
	private Date quarantineOrderedVerballyDate;
	private Date quarantineOrderedOfficialDocumentDate;
	private boolean quarantineExtended;
	private boolean quarantineReduced;
	private boolean quarantineOfficialOrderSent;
	private Date quarantineOfficialOrderSentDate;

	private YesNoUnknown postpartum;
	private Trimester trimester;

	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private int numberOfVisits;
	private YesNoUnknown lastCooperativeVisitSymptomatic;
	private Date lastCooperativeVisitDate;
	private String lastCooperativeVisitSymptoms;

	private Long eventCount;
	private String latestEventId;
	private String latestEventTitle;
	private EventStatus latestEventStatus;
	private String externalID;
	private String externalToken;
	private String internalToken;

	private String birthName;
	private String birthCountry;
	private String citizenship;

	private String responsibleRegion;
	private String responsibleDistrict;
	private String responsibleCommunity;

	private String clinicianName;

	private String clinicianPhone;

	private String clinicianEmail;

	private Long reportingUserId;
	private Long followUpStatusChangeUserId;

	private String reportingUserName;
	private String reportingUserRoles;
	private String followUpStatusChangeUserName;
	private String followUpStatusChangeUserRoles;
	private Date previousQuarantineTo;

	private String quarantineChangeComment;

	private Boolean isInJurisdiction;

    //@formatter:off
    public CaseExportMapperDto(long id, long personId, Double personAddressLatitude, Double personAddressLongitude, Float personAddressLatLonAcc, long epiDataId, Symptoms symptoms,
                               long hospitalizationId, long healthConditionsId, String uuid, String epidNumber,
                               Disease disease, DiseaseVariant diseaseVariant, String diseaseDetails, String diseaseVariantDetails,
                               String personUuid, String firstName, String lastName, Salutation salutation, String otherSalutation, Sex sex, YesNoUnknown pregnant,
                               Integer approximateAge, ApproximateAgeType approximateAgeType, Integer birthdateDD, Integer birthdateMM,
                               Integer birthdateYYYY, Date reportDate, String region, String district, String community,
                               FacilityType facilityType, String healthFacility, String healthFacilityUuid, String healthFacilityDetails, String pointOfEntry,
                               String pointOfEntryUuid, String pointOfEntryDetails, CaseClassification caseClassification,
                               YesNoUnknown clinicalConfirmation, YesNoUnknown epidemiologicalConfirmation, YesNoUnknown laboratoryDiagnosticConfirmation,
                               Boolean notACaseReasonNegativeTest, Boolean notACaseReasonPhysicianInformation, Boolean notACaseReasonDifferentPathogen, Boolean notACaseReasonOther,
                               String notACaseReasonDetails, InvestigationStatus investigationStatus, Date investigatedDate,
                               CaseOutcome outcome, Date outcomeDate,
                               YesNoUnknown sequelae, String sequelaeDetails,
                               YesNoUnknown bloodOrganOrTissueDonated,
                               FollowUpStatus followUpStatus, Date followUpUntil,
                               Boolean nosocomialOutbreak, InfectionSetting infectionSetting,
                               YesNoUnknown prohibitionToWork, Date prohibitionToWorkFrom, Date prohibitionToWorkUntil,
                               YesNoUnknown reInfection, Date previousInfectionDate, ReinfectionStatus reinfectionStatus, Object reinfectionDetails,
                               // Quarantine
                               QuarantineType quarantine, String quarantineTypeDetails, Date quarantineFrom, Date quarantineTo,
                               String quarantineHelpNeeded,
                               boolean quarantineOrderedVerbally, boolean quarantineOrderedOfficialDocument, Date quarantineOrderedVerballyDate,
                               Date quarantineOrderedOfficialDocumentDate, boolean quarantineExtended, boolean quarantineReduced,
                               boolean quarantineOfficialOrderSent, Date quarantineOfficialOrderSentDate,
                               YesNoUnknown admittedToHealthFacility, Date admissionDate, Date dischargeDate, YesNoUnknown leftAgainstAdvice, PresentCondition presentCondition,
                               Date deathDate, Date burialDate, BurialConductor burialConductor, String burialPlaceDescription,
                               String addressRegion, String addressDistrict, String addressCommunity, String city, String street, String houseNumber, String additionalInformation, String postalCode,
                               String facility, String facilityUuid, String facilityDetails,
                               String phone, String phoneOwner, String emailAddress, String otherContactDetails, EducationType educationType, String educationDetails,
                               OccupationType occupationType, String occupationDetails, ArmedForcesRelationType ArmedForcesRelationType, YesNoUnknown contactWithSourceCaseKnown,
                               //Date onsetDate,
                               VaccinationStatus vaccinationStatus, YesNoUnknown postpartum, Trimester trimester,
                               long eventCount,
							   Long prescriptionCount,
							   Long treatmentCount,
							   Long clinicalVisitCount,
							   String externalID, String externalToken, String internalToken,
                               String birthName, String birthCountryIsoCode, String birthCountryName, String citizenshipIsoCode, String citizenshipCountryName,
                               CaseIdentificationSource caseIdentificationSource, ScreeningType screeningType,
                               // responsible jurisdiction
                               String responsibleRegion, String responsibleDistrict, String responsibleCommunity,
                               // clinician
                               String clinicianName, String clinicianPhone, String clinicianEmail,
                               // users
                               Long reportingUserId, Long followUpStatusChangeUserId,
                               Date previousQuarantineTo, String quarantineChangeComment,
                               boolean isInJurisdiction
    ) {
        //@formatter:on

        this.id = id;
        this.personId = personId;
        this.addressGpsCoordinates = Location.buildGpsCoordinatesCaption(personAddressLatitude, personAddressLongitude, personAddressLatLonAcc);
        this.epiDataId = epiDataId;
        this.symptoms = SymptomsFacadeEjb.toDto(symptoms);
        this.hospitalizationId = hospitalizationId;
        this.healthConditionsId = healthConditionsId;
        this.uuid = uuid;
        this.epidNumber = epidNumber;
        this.armedForcesRelationType = ArmedForcesRelationType;
        this.disease = disease;
        this.diseaseDetails = diseaseDetails;
        this.diseaseVariant = diseaseVariant;
        this.diseaseVariantDetails = diseaseVariantDetails;
        this.personUuid = personUuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salutation = salutation;
        this.otherSalutation = otherSalutation;
        this.sex = sex;
        this.pregnant = pregnant;
        this.approximateAge = ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
        this.ageGroup = ApproximateAgeType.ApproximateAgeHelper.getAgeGroupFromAge(approximateAge, approximateAgeType);
        this.birthdate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
        this.reportDate = reportDate;
        this.region = region;
        this.district = district;
        this.community = community;
        this.caseClassification = caseClassification;
        this.clinicalConfirmation = clinicalConfirmation;
        this.epidemiologicalConfirmation = epidemiologicalConfirmation;
        this.laboratoryDiagnosticConfirmation = laboratoryDiagnosticConfirmation;
        this.notACaseReasonNegativeTest = notACaseReasonNegativeTest;
        this.notACaseReasonPhysicianInformation = notACaseReasonPhysicianInformation;
        this.notACaseReasonDifferentPathogen = notACaseReasonDifferentPathogen;
        this.notACaseReasonOther = notACaseReasonOther;
        this.notACaseReasonDetails = notACaseReasonDetails;
        this.investigationStatus = investigationStatus;
        this.investigatedDate = investigatedDate;
        this.outcome = outcome;
        this.outcomeDate = outcomeDate;
        this.sequelae = sequelae;
        this.sequelaeDetails = sequelaeDetails;
        this.bloodOrganOrTissueDonated = bloodOrganOrTissueDonated;
        this.nosocomialOutbreak = nosocomialOutbreak;
        this.infectionSetting = infectionSetting;
        this.prohibitionToWork = prohibitionToWork;
        this.prohibitionToWorkFrom = prohibitionToWorkFrom;
        this.prohibitionToWorkUntil = prohibitionToWorkUntil;
        this.reInfection = reInfection;
        this.previousInfectionDate = previousInfectionDate;
        this.reinfectionStatus = reinfectionStatus;
        this.reinfectionDetails = DataHelper.buildStringFromTrueValues((Map<ReinfectionDetail, Boolean>) reinfectionDetails);
        this.quarantine = quarantine;
        this.quarantineTypeDetails = quarantineTypeDetails;
        this.quarantineFrom = quarantineFrom;
        this.quarantineTo = quarantineTo;
        this.quarantineHelpNeeded = quarantineHelpNeeded;
        this.quarantineOrderedVerbally = quarantineOrderedVerbally;
        this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
        this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
        this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
        this.quarantineExtended = quarantineExtended;
        this.quarantineReduced = quarantineReduced;
        this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
        this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
        this.facilityType = facilityType;
        this.healthFacility = FacilityHelper.buildFacilityString(healthFacilityUuid, healthFacility);
        this.healthFacilityDetails = healthFacilityDetails;
        this.pointOfEntry = InfrastructureHelper.buildPointOfEntryString(pointOfEntryUuid, pointOfEntry);
        this.pointOfEntryDetails = pointOfEntryDetails;
        this.admittedToHealthFacility = admittedToHealthFacility;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.leftAgainstAdvice = leftAgainstAdvice;
        this.presentCondition = presentCondition;
        this.deathDate = deathDate;
        this.burialInfo = new BurialInfoDto(burialDate, burialConductor, burialPlaceDescription);
        this.addressRegion = addressRegion;
        this.addressDistrict = addressDistrict;
        this.addressCommunity = addressCommunity;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.additionalInformation = additionalInformation;
        this.postalCode = postalCode;
        this.facility = FacilityHelper.buildFacilityString(facilityUuid, facility);
        this.facilityDetails = facilityDetails;
        this.phone = phone;
        this.phoneOwner = phoneOwner;
        this.emailAddress = emailAddress;
        this.otherContactDetails = otherContactDetails;
        this.educationType = educationType;
        this.educationDetails = educationDetails;
        this.occupationType = occupationType;
        this.occupationDetails = occupationDetails;
        this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
//		this.onsetDate = onsetDate;
        this.vaccinationStatus = vaccinationStatus;

        this.postpartum = postpartum;
        this.trimester = trimester;
        this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.eventCount = eventCount;
		this.numberOfPrescriptions = prescriptionCount != null ? prescriptionCount.intValue() : 0;
		this.numberOfTreatments = treatmentCount != null ? treatmentCount.intValue() : 0;
		this.numberOfClinicalVisits = clinicalVisitCount != null ? clinicalVisitCount.intValue() : 0;
        this.externalID = externalID;
        this.externalToken = externalToken;
        this.internalToken = internalToken;
        this.birthName = birthName;
        this.birthCountry = I18nProperties.getCountryName(birthCountryIsoCode, birthCountryName);
        this.citizenship = I18nProperties.getCountryName(citizenshipIsoCode, citizenshipCountryName);
        this.caseIdentificationSource = caseIdentificationSource;
        this.screeningType = screeningType;

        this.responsibleRegion = responsibleRegion;
        this.responsibleDistrict = responsibleDistrict;
        this.responsibleCommunity = responsibleCommunity;

        this.clinicianName = clinicianName;
        this.clinicianPhone = clinicianPhone;
        this.clinicianEmail = clinicianEmail;

        this.reportingUserId = reportingUserId;
        this.followUpStatusChangeUserId = followUpStatusChangeUserId;

        this.previousQuarantineTo = previousQuarantineTo;
        this.quarantineChangeComment = quarantineChangeComment;

        this.isInJurisdiction = isInJurisdiction;
    }

	public CaseExportDto toCaseExportDto() {
		CaseExportDto caseExportDto = new CaseExportDto(
				id,
				personId,
				addressGpsCoordinates,
				epiDataId,
				symptoms,
				hospitalizationId,
				healthConditionsId,
				uuid,
				epidNumber,
				disease,
				diseaseVariant,
				diseaseDetails,
				diseaseVariantDetails,
				personUuid,
				firstName,
				lastName,
				salutation,
				otherSalutation,
				sex,
				pregnant,
				approximateAge,
				ageGroup,
				birthdate,
				reportDate,
				region,
				district,
				community,
				facilityType,
				healthFacility,
				healthFacilityDetails,
				pointOfEntry,
				pointOfEntryDetails,
				caseClassification,
				clinicalConfirmation,
				epidemiologicalConfirmation,
				laboratoryDiagnosticConfirmation,
				notACaseReasonNegativeTest,
				notACaseReasonPhysicianInformation,
				notACaseReasonDifferentPathogen,
				notACaseReasonOther,
				notACaseReasonDetails,
				investigationStatus,
				investigatedDate,
				outcome,
				outcomeDate,
				sequelae,
				sequelaeDetails,
				bloodOrganOrTissueDonated,
				followUpStatus,
				followUpUntil,
				nosocomialOutbreak,
				infectionSetting,
				prohibitionToWork,
				prohibitionToWorkFrom,
				prohibitionToWorkUntil,
				reInfection,
				previousInfectionDate,
				reinfectionStatus,
				reinfectionDetails,
				// Quarantine
				quarantine,
				quarantineTypeDetails,
				quarantineFrom,
				quarantineTo,
				quarantineHelpNeeded,
				quarantineOrderedVerbally,
				quarantineOrderedOfficialDocument,
				quarantineOrderedVerballyDate,
				quarantineOrderedOfficialDocumentDate,
				quarantineExtended,
				quarantineReduced,
				quarantineOfficialOrderSent,
				quarantineOfficialOrderSentDate,
				admittedToHealthFacility,
				admissionDate,
				dischargeDate,
				leftAgainstAdvice,
				presentCondition,
				deathDate,
				burialInfo,
				addressRegion,
				addressDistrict,
				addressCommunity,
				city,
				street,
				houseNumber,
				additionalInformation,
				postalCode,
				facility,
				facilityDetails,
				phone,
				phoneOwner,
				emailAddress,
				otherContactDetails,
				educationType,
				educationDetails,
				occupationType,
				occupationDetails,
				armedForcesRelationType,
				contactWithSourceCaseKnown,
				//Date onsetDate,
				vaccinationStatus,
				postpartum,
				trimester,
				eventCount,
				externalID,
				externalToken,
				internalToken,
				birthName,
				birthCountry,
				citizenship,
				caseIdentificationSource,
				screeningType,
				// responsible jurisdiction
				responsibleRegion,
				responsibleDistrict,
				responsibleCommunity,
				// clinician
				clinicianName,
				clinicianPhone,
				clinicianEmail,
				// users
				reportingUserId,
				followUpStatusChangeUserId,
				previousQuarantineTo,
				quarantineChangeComment,
				isInJurisdiction);
		caseExportDto.setNumberOfPrescriptions(numberOfPrescriptions);
		caseExportDto.setNumberOfTreatments(numberOfTreatments);
		caseExportDto.setNumberOfClinicalVisits(numberOfClinicalVisits);
		return caseExportDto;
	}
 
}
