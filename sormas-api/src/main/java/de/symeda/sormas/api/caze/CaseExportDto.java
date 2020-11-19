/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.importexport.ExportTarget;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;

/**
 * A DTO class that contains the properties that are exported during a detailed case export. These
 * properties are also those that users can select when creating a custom export configuration.
 * <p>
 * PLEASE NOTE: When the @ExportProperty value of one of these properties changes, it's necessary
 * to replace all occurrences of the former value in all export configurations in the database.
 * Otherwise, existing export configurations will no longer export the property. Also, it is
 * recommended to remove properties that are removed from this file from existing export configurations.
 */
public class CaseExportDto implements Serializable {

	private static final long serialVersionUID = 8581579464816945555L;

	public static final String I18N_PREFIX = "CaseExport";

	public static final String ID = "id";
	public static final String COUNTRY = "country";
	public static final String AGE_GROUP = "ageGroup";
	public static final String INITIAL_DETECTION_PLACE = "initialDetectionPlace";
	public static final String MAX_SOURCE_CASE_CLASSIFICATION = "maxSourceCaseClassification";
	public static final String ASSOCIATED_WITH_OUTBREAK = "associatedWithOutbreak";
	public static final String BURIAL_INFO = "burialInfo";
	public static final String ADDRESS_DISTRICT = "addressDistrict";
	public static final String ADDRESS_REGION = "addressRegion";
	public static final String ADDRESS_COMMUNITY = "addressCommunity";
	public static final String ADDRESS_GPS_COORDINATES = "addressGpsCoordinates";
	public static final String BURIAL_ATTENDED = "burialAttended";
	public static final String TRAVELED = "traveled";
	public static final String TRAVEL_HISTORY = "travelHistory";
	public static final String NUMBER_OF_PRESCRIPTIONS = "numberOfPrescriptions";
	public static final String NUMBER_OF_TREATMENTS = "numberOfTreatments";
	public static final String NUMBER_OF_CLINICAL_VISITS = "numberOfClinicalVisits";
	public static final String SAMPLE_INFORMATION = "sampleInformation";
	public static final String QUARANTINE_INFORMATION = "quarantineInformation";
	public static final String NUMBER_OF_VISITS = "numberOfVisits";
	public static final String LAST_COOPERATIVE_VISIT_SYMPTOMATIC = "lastCooperativeVisitSymptomatic";
	public static final String LAST_COOPERATIVE_VISIT_DATE = "lastCooperativeVisitDate";
	public static final String LAST_COOPERATIVE_VISIT_SYMPTOMS = "lastCooperativeVisitSymptoms";
	public static final String FACILITY = "facility";
	public static final String EVENT_COUNT = "eventCount";
	public static final String LATEST_EVENT_ID = "latestEventId";
	public static final String LATEST_EVENT_STATUS = "latestEventStatus";
	public static final String LATEST_EVENT_TITLE = "latestEventTitle";

	private String country;
	private long id;
	private long personId;
	private long personAddressId;
	private long epiDataId;
	private long symptomsId;
	private long hospitalizationId;
	private long districtId;
	private long healthConditionsId;
	private String uuid;
	private String epidNumber;
	private String diseaseFormatted;
	private Disease disease;
	@PersonalData
	@SensitiveData
	private String firstName;
	@PersonalData
	@SensitiveData
	private String lastName;
	private Sex sex;
	private YesNoUnknown pregnant;
	private String approximateAge;
	private String ageGroup;
	private BirthDateDto birthdate;
	private Date reportDate;
	private String region;
	private String district;
	@PersonalData
	@SensitiveData
	private String community;
	@PersonalData
	@SensitiveData
	private String healthFacility;
	@PersonalData
	@SensitiveData
	private String pointOfEntry;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private CaseClassification maxSourceCaseClassification;
	private CaseOutcome outcome;
	private String associatedWithOutbreak;
	private YesNoUnknown admittedToHealthFacility;
	private Date admissionDate;
	private Date dischargeDate;
	private YesNoUnknown leftAgainstAdvice;
	@SensitiveData
	private String initialDetectionPlace;
	private PresentCondition presentCondition;
	private Date deathDate;
	private BurialInfoDto burialInfo;
	private String addressRegion;
	private String addressDistrict;
	@PersonalData
	@SensitiveData
	private String addressCommunity;
	@PersonalData
	@SensitiveData
	private String city;
	@PersonalData
	@SensitiveData
	private String street;
	@PersonalData
	@SensitiveData
	private String houseNumber;
	@PersonalData
	@SensitiveData
	private String additionalInformation;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(PostalCodePseudonymizer.class)
	private String postalCode;
	@PersonalData
	@SensitiveData
	private String addressGpsCoordinates;
	@PersonalData
	@SensitiveData
	private String facility;
	@SensitiveData
	private String phone;
	@SensitiveData
	private String emailAddress;
	private String occupationType;
	private String educationType;
	private String travelHistory;
	private boolean traveled;
	private boolean burialAttended;
	private YesNoUnknown contactWithSourceCaseKnown;
	private SymptomsDto symptoms;
	//	private Date onsetDate;
//	private String symptoms;
	private Vaccination vaccination;
	private String vaccinationDoses;
	private Date vaccinationDate;
	private VaccinationInfoSource vaccinationInfoSource;
	private HealthConditionsDto healthConditions;
	private int numberOfPrescriptions;
	private int numberOfTreatments;
	private int numberOfClinicalVisits;
	private EmbeddedSampleExportDto sample1 = new EmbeddedSampleExportDto();
	private EmbeddedSampleExportDto sample2 = new EmbeddedSampleExportDto();
	private EmbeddedSampleExportDto sample3 = new EmbeddedSampleExportDto();
	private List<EmbeddedSampleExportDto> otherSamples = new ArrayList<>();

	private QuarantineType quarantine;
	@SensitiveData
	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;

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

	private CaseJurisdictionDto jurisdiction;

	private Long eventCount;
	private String latestEventId;
	private String latestEventTitle;
	private EventStatus latestEventStatus;
	private String externalID;

	//@formatter:off
	public CaseExportDto(long id, long personId, long personAddressId, long epiDataId, long symptomsId,
						 long hospitalizationId, long districtId, long healthConditionsId, String uuid, String epidNumber,
						 Disease disease, String diseaseDetails, String firstName, String lastName, Sex sex, YesNoUnknown pregnant,
						 Integer approximateAge, ApproximateAgeType approximateAgeType, Integer birthdateDD, Integer birthdateMM,
						 Integer birthdateYYYY, Date reportDate, String reportingUserUuid, String regionUuid, String region,
						 String districtUuid, String district, String communityUuid, String community,
						 String healthFacility, String healthFacilityUuid, String healthFacilityDetails, String pointOfEntry,
						 String pointOfEntryUuid, String pointOfEntryDetails, CaseClassification caseClassification,
						 InvestigationStatus investigationStatus, CaseOutcome outcome,
						 FollowUpStatus followUpStatus, Date followUpUntil,
						 // Quarantine
						 QuarantineType quarantine, String quarantineTypeDetails, Date quarantineFrom, Date quarantineTo,
						 boolean quarantineOrderedVerbally, boolean quarantineOrderedOfficialDocument, Date quarantineOrderedVerballyDate,
						 Date quarantineOrderedOfficialDocumentDate, boolean quarantineExtended, boolean quarantineReduced,
						 boolean quarantineOfficialOrderSent, Date quarantineOfficialOrderSentDate,
						 YesNoUnknown admittedToHealthFacility, Date admissionDate, Date dischargeDate, YesNoUnknown leftAgainstAdvice, PresentCondition presentCondition,
						 Date deathDate, Date burialDate, BurialConductor burialConductor, String burialPlaceDescription,
						 String addressRegion, String addressDistrict, String addressCommunity, String city, String street, String houseNumber, String additionalInformation, String postalCode,
						 String facility, String facilityUuid, String facilityDetails,
						 String phone, String phoneOwner, String emailAddress, EducationType educationType, String educationDetails,
						 OccupationType occupationType, String occupationDetails, YesNoUnknown contactWithSourceCaseKnown,
						 //Date onsetDate,
						 Vaccination vaccination, String vaccinationDoses, Date vaccinationDate,
						 VaccinationInfoSource vaccinationInfoSource, YesNoUnknown postpartum, Trimester trimester,
						 long eventCount, String externalID) {
		//@formatter:on

		this.id = id;
		this.personId = personId;
		this.personAddressId = personAddressId;
		this.epiDataId = epiDataId;
		this.symptomsId = symptomsId;
		this.hospitalizationId = hospitalizationId;
		this.districtId = districtId;
		this.healthConditionsId = healthConditionsId;
		this.uuid = uuid;
		this.epidNumber = epidNumber;
		this.diseaseFormatted = DiseaseHelper.toString(disease, diseaseDetails);
		this.disease = disease;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.pregnant = pregnant;
		this.approximateAge = ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.ageGroup = ApproximateAgeHelper.getAgeGroupFromAge(approximateAge, approximateAgeType);
		this.birthdate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.reportDate = reportDate;
		this.region = region;
		this.district = district;
		this.community = community;
		this.caseClassification = caseClassification;
		this.investigationStatus = investigationStatus;
		this.outcome = outcome;
		this.quarantine = quarantine;
		this.quarantineTypeDetails = quarantineTypeDetails;
		this.quarantineFrom = quarantineFrom;
		this.quarantineTo = quarantineTo;
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
		this.quarantineExtended = quarantineExtended;
		this.quarantineReduced = quarantineReduced;
		this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
		this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
		this.healthFacility = FacilityHelper.buildFacilityString(healthFacilityUuid, healthFacility, healthFacilityDetails);
		this.pointOfEntry = InfrastructureHelper.buildPointOfEntryString(pointOfEntryUuid, pointOfEntry, pointOfEntryDetails);
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
		this.facility = FacilityHelper.buildFacilityString(facilityUuid, facility, facilityDetails);
		this.phone = PersonHelper.buildPhoneString(phone, phoneOwner);
		this.emailAddress = emailAddress;
		this.educationType = PersonHelper.buildEducationString(educationType, educationDetails);
		this.occupationType = PersonHelper.buildOccupationString(occupationType, occupationDetails);
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
//		this.onsetDate = onsetDate;
		this.vaccination = vaccination;
		this.vaccinationDoses = vaccinationDoses;
		this.vaccinationDate = vaccinationDate;
		this.vaccinationInfoSource = vaccinationInfoSource;
		this.postpartum = postpartum;
		this.trimester = trimester;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.eventCount = eventCount;
		this.externalID = externalID;

		jurisdiction = new CaseJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, communityUuid, healthFacilityUuid, pointOfEntryUuid);
	}

	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(uuid, firstName, lastName);
	}

	@Order(0)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(COUNTRY)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getCountry() {
		return country;
	}

	@Order(1)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(ID)
	@ExportGroup(ExportGroupType.CORE)
	public long getId() {
		return id;
	}

	public long getPersonId() {
		return personId;
	}

	public long getPersonAddressId() {
		return personAddressId;
	}

	public long getEpiDataId() {
		return epiDataId;
	}

	public long getSymptomsId() {
		return symptomsId;
	}

	public long getHospitalizationId() {
		return hospitalizationId;
	}

	public long getDistrictId() {
		return districtId;
	}

	public long getHealthConditionsId() {
		return healthConditionsId;
	}

	@Order(2)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.UUID)
	@ExportGroup(ExportGroupType.CORE)
	public String getUuid() {
		return uuid;
	}

	@Order(3)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.EPID_NUMBER)
	@ExportGroup(ExportGroupType.CORE)
	public String getEpidNumber() {
		return epidNumber;
	}

	@Order(4)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.EXTERNAL_ID)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalID() {
		return externalID;
	}

	@Order(5)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.DISEASE)
	@ExportGroup(ExportGroupType.CORE)
	public String getDiseaseFormatted() {
		return diseaseFormatted;
	}

	@Order(10)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.FIRST_NAME)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFirstName() {
		return firstName;
	}

	@Order(11)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.LAST_NAME)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getLastName() {
		return lastName;
	}

	@Order(12)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.SEX)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public Sex getSex() {
		return sex;
	}

	@Order(13)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.PREGNANT)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public YesNoUnknown getPregnant() {
		return pregnant;
	}

	@Order(14)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.TRIMESTER)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public Trimester getTrimester() {
		return trimester;
	}

	@Order(15)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.POSTPARTUM)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public YesNoUnknown getPostpartum() {
		return postpartum;
	}

	@Order(16)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.APPROXIMATE_AGE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getApproximateAge() {
		return approximateAge;
	}

	@Order(17)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(AGE_GROUP)
	@ExportGroup(ExportGroupType.PERSON)
	public String getAgeGroup() {
		return ageGroup;
	}

	@Order(18)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.BIRTH_DATE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public BirthDateDto getBirthdate() {
		return birthdate;
	}

	@Order(20)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.REPORT_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(21)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.REGION)
	@ExportGroup(ExportGroupType.CORE)
	public String getRegion() {
		return region;
	}

	@Order(22)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.DISTRICT)
	@ExportGroup(ExportGroupType.CORE)
	public String getDistrict() {
		return district;
	}

	@Order(23)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.COMMUNITY)
	@ExportGroup(ExportGroupType.CORE)
	public String getCommunity() {
		return community;
	}

	@Order(24)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.HEALTH_FACILITY)
	@ExportGroup(ExportGroupType.CORE)
	public String getHealthFacility() {
		return healthFacility;
	}

	@Order(25)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.POINT_OF_ENTRY)
	@ExportGroup(ExportGroupType.CORE)
	public String getPointOfEntry() {
		return pointOfEntry;
	}

	@Order(26)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(INITIAL_DETECTION_PLACE)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getInitialDetectionPlace() {
		return initialDetectionPlace;
	}

	@Order(30)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseDataDto.CASE_CLASSIFICATION)
	@ExportGroup(ExportGroupType.CORE)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	@Order(31)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseDataDto.INVESTIGATION_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	@Order(32)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.OUTCOME)
	@ExportGroup(ExportGroupType.CORE)
	public CaseOutcome getOutcome() {
		return outcome;
	}

	@Order(33)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public QuarantineType getQuarantine() {
		return quarantine;
	}

	@Order(34)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	@Order(35)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	@Order(36)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getQuarantineTo() {
		return quarantineTo;
	}

	@Order(37)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	@Order(38)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	@Order(39)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	@Order(40)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	@Order(41)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	@Order(42)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	@HideForCountriesExcept(countries = {
		"de",
		"ch" })
	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	@Order(43)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	@Order(44)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(value = QUARANTINE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	@Order(45)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(MAX_SOURCE_CASE_CLASSIFICATION)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public CaseClassification getMaxSourceCaseClassification() {
		return maxSourceCaseClassification;
	}

	@Order(46)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(ASSOCIATED_WITH_OUTBREAK)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getAssociatedWithOutbreak() {
		return associatedWithOutbreak;
	}

	public void setMaxSourceCaseClassification(CaseClassification maxSourceCaseClassification) {
		this.maxSourceCaseClassification = maxSourceCaseClassification;
	}

	@Order(47)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY)
	@ExportGroup(ExportGroupType.HOSPITALIZATION)
	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	@Order(48)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(HospitalizationDto.ADMISSION_DATE)
	@ExportGroup(ExportGroupType.HOSPITALIZATION)
	public Date getAdmissionDate() {
		return admissionDate;
	}

	@Order(49)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(HospitalizationDto.DISCHARGE_DATE)
	@ExportGroup(ExportGroupType.HOSPITALIZATION)
	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	@Order(50)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(HospitalizationDto.LEFT_AGAINST_ADVICE)
	@ExportGroup(ExportGroupType.HOSPITALIZATION)
	public YesNoUnknown getLeftAgainstAdvice() {
		return leftAgainstAdvice;
	}

	public void setLeftAgainstAdvice(YesNoUnknown leftAgainstAdvice) {
		this.leftAgainstAdvice = leftAgainstAdvice;
	}

	@Order(51)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.PRESENT_CONDITION)
	@ExportGroup(ExportGroupType.PERSON)
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	@Order(52)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(PersonDto.DEATH_DATE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public Date getDeathDate() {
		return deathDate;
	}

	@Order(53)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(BURIAL_INFO)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public BurialInfoDto getBurialInfo() {
		return burialInfo;
	}

	@Order(56)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseExportDto.ADDRESS_REGION)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressRegion() {
		return addressRegion;
	}

	@Order(57)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseExportDto.ADDRESS_DISTRICT)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressDistrict() {
		return addressDistrict;
	}

	@Order(58)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseExportDto.ADDRESS_COMMUNITY)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressCommunity() {
		return addressCommunity;
	}

	@Order(59)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(LocationDto.CITY)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getCity() {
		return city;
	}

	@Order(60)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(LocationDto.STREET)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getStreet() {
		return street;
	}

	@Order(61)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(LocationDto.HOUSE_NUMBER)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getHouseNumber() {
		return houseNumber;
	}

	@Order(70)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(LocationDto.ADDITIONAL_INFORMATION)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	@Order(71)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(LocationDto.POSTAL_CODE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPostalCode() {
		return postalCode;
	}

	@Order(72)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(ADDRESS_GPS_COORDINATES)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getAddressGpsCoordinates() {
		return addressGpsCoordinates;
	}

	@Order(73)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(FACILITY)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFacility() {
		return facility;
	}

	@Order(74)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.PHONE)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getPhone() {
		return phone;
	}

	@Order(75)
	@ExportTarget(caseExportTypes = {
			CaseExportType.CASE_SURVEILLANCE,
			CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.EMAIL_ADDRESS)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getEmailAddress() {
		return emailAddress;
	}

	@Order(76)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.EDUCATION_TYPE)
	@ExportGroup(ExportGroupType.PERSON)
	public String getEducationType() {
		return educationType;
	}

	public void setEducationType(String educationType) {
		this.educationType = educationType;
	}

	@Order(77)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(PersonDto.OCCUPATION_TYPE)
	@ExportGroup(ExportGroupType.PERSON)
	public String getOccupationType() {
		return occupationType;
	}

	@Order(78)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(TRAVELED)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public boolean isTraveled() {
		return traveled;
	}

	public void setTraveled(boolean traveled) {
		this.traveled = traveled;
	}

	@Order(79)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(TRAVEL_HISTORY)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public String getTravelHistory() {
		return travelHistory;
	}

	@Order(80)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(BURIAL_ATTENDED)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public boolean isBurialAttended() {
		return burialAttended;
	}

	public void setBurialAttended(boolean burialAttended) {
		this.burialAttended = burialAttended;
	}

	@Order(81)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN)
	@ExportGroup(ExportGroupType.EPIDEMIOLOGICAL)
	public YesNoUnknown getContactWithSourceCaseKnown() {
		return contactWithSourceCaseKnown;
	}

	public void setContactWithSourceCaseKnown(YesNoUnknown contactWithSourceCaseKnown) {
		this.contactWithSourceCaseKnown = contactWithSourceCaseKnown;
	}

	@Order(92)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.VACCINATION)
	@ExportGroup(ExportGroupType.VACCINATION)
	public Vaccination getVaccination() {
		return vaccination;
	}

	@Order(93)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.VACCINATION_DOSES)
	@ExportGroup(ExportGroupType.VACCINATION)
	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	@Order(94)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.VACCINATION_DATE)
	@ExportGroup(ExportGroupType.VACCINATION)
	public Date getVaccinationDate() {
		return vaccinationDate;
	}

	@Order(95)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.VACCINATION_INFO_SOURCE)
	@ExportGroup(ExportGroupType.VACCINATION)
	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

//	@Order(96)
//	public Date getOnsetDate() {
//		return onsetDate;
//	}
//
//	@Order(97)
//	public String getSymptoms() {
//		return symptoms;
//	}	

	@Order(110)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE,
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(CaseDataDto.SYMPTOMS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	@Order(111)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(ClinicalCourseDto.HEALTH_CONDITIONS)
	@ExportGroup(ExportGroupType.CASE_MANAGEMENT)
	public HealthConditionsDto getHealthConditions() {
		return healthConditions;
	}

	@Order(112)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(NUMBER_OF_PRESCRIPTIONS)
	@ExportGroup(ExportGroupType.CASE_MANAGEMENT)
	public int getNumberOfPrescriptions() {
		return numberOfPrescriptions;
	}

	@Order(113)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(NUMBER_OF_TREATMENTS)
	@ExportGroup(ExportGroupType.CASE_MANAGEMENT)
	public int getNumberOfTreatments() {
		return numberOfTreatments;
	}

	@Order(114)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_MANAGEMENT })
	@ExportProperty(NUMBER_OF_CLINICAL_VISITS)
	@ExportGroup(ExportGroupType.CASE_MANAGEMENT)
	public int getNumberOfClinicalVisits() {
		return numberOfClinicalVisits;
	}

	@Order(120)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getSampleDateTime1() {
		return sample1.getDateTime();
	}

	@Order(121)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getSampleLab1() {
		return sample1.getLab();
	}

	@Order(122)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public PathogenTestResultType getSampleResult1() {
		return sample1.getResult();
	}

	@Order(123)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getSampleDateTime2() {
		return sample2.getDateTime();
	}

	@Order(124)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getSampleLab2() {
		return sample2.getLab();
	}

	@Order(125)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public PathogenTestResultType getSampleResult2() {
		return sample2.getResult();
	}

	@Order(126)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public Date getSampleDateTime3() {
		return sample3.getDateTime();
	}

	@Order(127)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getSampleLab3() {
		return sample3.getLab();
	}

	@Order(128)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public PathogenTestResultType getSampleResult3() {
		return sample3.getResult();
	}

	@Order(130)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(value = SAMPLE_INFORMATION, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getOtherSamplesString() {
		StringBuilder samples = new StringBuilder();
		String separator = ", ";

		for (EmbeddedSampleExportDto sample : otherSamples) {
			samples.append(sample.formatString()).append(separator);
		}

		return samples.length() > 0 ? samples.substring(0, samples.length() - separator.length()) : "";
	}

	@Order(131)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseDataDto.FOLLOW_UP_STATUS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	@Order(132)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseDataDto.FOLLOW_UP_UNTIL)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	@Order(133)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.NUMBER_OF_VISITS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public int getNumberOfVisits() {
		return numberOfVisits;
	}

	@Order(134)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.LAST_COOPERATIVE_VISIT_SYMPTOMATIC)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public YesNoUnknown getLastCooperativeVisitSymptomatic() {
		return lastCooperativeVisitSymptomatic;
	}

	@Order(135)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.LAST_COOPERATIVE_VISIT_DATE)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public Date getLastCooperativeVisitDate() {
		return lastCooperativeVisitDate;
	}

	@Order(136)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.LAST_COOPERATIVE_VISIT_SYMPTOMS)
	@ExportGroup(ExportGroupType.FOLLOW_UP)
	public String getLastCooperativeVisitSymptoms() {
		return lastCooperativeVisitSymptoms;
	}

	@Order(137)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.EVENT_COUNT)
	@ExportGroup(ExportGroupType.EVENT)
	public Long getEventCount() {
		return eventCount;
	}

	@Order(138)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.LATEST_EVENT_ID)
	@ExportGroup(ExportGroupType.EVENT)
	public String getLatestEventId() {
		return latestEventId;
	}

	@Order(139)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.LATEST_EVENT_STATUS)
	@ExportGroup(ExportGroupType.EVENT)
	public EventStatus getLatestEventStatus() {
		return latestEventStatus;
	}

	@Order(140)
	@ExportTarget(caseExportTypes = {
		CaseExportType.CASE_SURVEILLANCE })
	@ExportProperty(CaseExportDto.LATEST_EVENT_TITLE)
	@ExportGroup(ExportGroupType.EVENT)
	public String getLatestEventTitle() {
		return latestEventTitle;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public void setPersonAddressId(long personAddressId) {
		this.personAddressId = personAddressId;
	}

	public void setEpiDataId(long epiDataId) {
		this.epiDataId = epiDataId;
	}

	public void setSymptomsId(long symptomsId) {
		this.symptomsId = symptomsId;
	}

	public void setHospitalizationId(long hospitalizationId) {
		this.hospitalizationId = hospitalizationId;
	}

	public void setDistrictId(long districtId) {
		this.districtId = districtId;
	}

	public void setHealthConditionsId(long healthConditionsId) {
		this.healthConditionsId = healthConditionsId;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	public void setDiseaseFormatted(String diseaseFormatted) {
		this.diseaseFormatted = diseaseFormatted;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public void setPregnant(YesNoUnknown pregnant) {
		this.pregnant = pregnant;
	}

	public void setTrimester(Trimester trimester) {
		this.trimester = trimester;
	}

	public void setPostpartum(YesNoUnknown postpartum) {
		this.postpartum = postpartum;
	}

	public void setApproximateAge(String age) {
		this.approximateAge = age;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public void setHealthFacility(String healthFacility) {
		this.healthFacility = healthFacility;
	}

	public void setPointOfEntry(String pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public void setAssociatedWithOutbreak(boolean associatedWithOutbreak) {
		this.associatedWithOutbreak = associatedWithOutbreak ? I18nProperties.getString(Strings.yes) : I18nProperties.getString(Strings.no);
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public void setAddressGpsCoordinates(String addressGpsCoordinates) {
		this.addressGpsCoordinates = addressGpsCoordinates;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setOccupationType(String occupationType) {
		this.occupationType = occupationType;
	}

	public void setTravelHistory(String travelHistory) {
		this.travelHistory = travelHistory;
	}

	public void setInitialDetectionPlace(String initialDetectionPlace) {
		this.initialDetectionPlace = initialDetectionPlace;
	}

	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public void setHealthConditions(HealthConditionsDto healthConditions) {
		this.healthConditions = healthConditions;
	}

	public void setNumberOfPrescriptions(int numberOfPrescriptions) {
		this.numberOfPrescriptions = numberOfPrescriptions;
	}

	public void setNumberOfTreatments(int numberOfTreatments) {
		this.numberOfTreatments = numberOfTreatments;
	}

	public void setNumberOfClinicalVisits(int numberOfClinicalVisits) {
		this.numberOfClinicalVisits = numberOfClinicalVisits;
	}

	public EmbeddedSampleExportDto getSample1() {
		return sample1;
	}

	public void setSample1(EmbeddedSampleExportDto sample1) {
		this.sample1 = sample1;
	}

	public EmbeddedSampleExportDto getSample2() {
		return sample2;
	}

	public void setSample2(EmbeddedSampleExportDto sample2) {
		this.sample2 = sample2;
	}

	public EmbeddedSampleExportDto getSample3() {
		return sample3;
	}

	public void setSample3(EmbeddedSampleExportDto sample3) {
		this.sample3 = sample3;
	}

	public List<EmbeddedSampleExportDto> getOtherSamples() {
		return otherSamples;
	}

	public void addOtherSample(EmbeddedSampleExportDto otherSample) {
		this.otherSamples.add(otherSample);
	}

	public CaseJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public void setNumberOfVisits(int numberOfVisits) {
		this.numberOfVisits = numberOfVisits;
	}

	public void setLastCooperativeVisitSymptomatic(YesNoUnknown lastCooperativeVisitSymptomatic) {
		this.lastCooperativeVisitSymptomatic = lastCooperativeVisitSymptomatic;
	}

	public void setLastCooperativeVisitDate(Date lastCooperativeVisitDate) {
		this.lastCooperativeVisitDate = lastCooperativeVisitDate;
	}

	public void setLastCooperativeVisitSymptoms(String lastCooperativeVisitSymptoms) {
		this.lastCooperativeVisitSymptoms = lastCooperativeVisitSymptoms;
	}

	public void setLatestEventId(String latestEventId) {
		this.latestEventId = latestEventId;
	}

	public void setLatestEventTitle(String latestEventTitle) {
		this.latestEventTitle = latestEventTitle;
	}

	public void setLatestEventStatus(EventStatus latestEventStatus) {
		this.latestEventStatus = latestEventStatus;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}
}
