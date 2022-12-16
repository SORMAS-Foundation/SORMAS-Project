package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "More detailed light weight index information on case entries, containing additional personal information, for larger queries")
public class CaseIndexDetailedDto extends CaseIndexDto {

	private static final long serialVersionUID = -3722694511897383913L;

	public static final String RE_INFECTION = "reInfection";
	public static final String SEX = "sex";
	public static final String CITY = "city";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String POSTAL_CODE = "postalCode";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String PHONE = "phone";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EVENT_COUNT = "eventCount";
	public static final String LATEST_EVENT_ID = "latestEventId";
	public static final String LATEST_EVENT_STATUS = "latestEventStatus";
	public static final String LATEST_EVENT_TITLE = "latestEventTitle";
	public static final String LATEST_SAMPLE_DATE_TIME = "latestSampleDateTime";
	public static final String SAMPLE_COUNT = "sampleCount";
	public static final String SYMPTOM_ONSET_DATE = "symptomOnsetDate";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";

	@Schema(description = "Whether the case is a reinfecton with the same disease")
	private YesNoUnknown reInfection;
	@PersonalData
	@SensitiveData
	@Schema(description = "Name of the city the person associated with the case lives in")
	private String city;
	@PersonalData
	@SensitiveData
	@Schema(description = "Name of the street the person associated with the case lives in")
	private String street;
	@PersonalData
	@SensitiveData
	@Schema(description = "Number of the house the person associated with the case lives in")
	private String houseNumber;
	@PersonalData
	@SensitiveData
	@Schema(description = "Additonal address information (e.g. ground floor)")
	private String additionalInformation;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(PostalCodePseudonymizer.class)
	@Schema(description = "Postal code of the person associated with the case")
	private String postalCode;
	@SensitiveData
	@Schema(description = "Phone number of the person associated with the case")
	private String phone;
	@Schema(description = "Number of events the person associated with the case attended")
	private Long eventCount;
	@Schema(description = "UUID of the last event the person associated with the case attended")
	private String latestEventId;
	@Schema(description = "Title of the last event the person associated with the case attended")
	private String latestEventTitle;
	private EventStatus latestEventStatus;
	@Schema(description = "Date and time when the latest sample was taken from the person associated with the case")
	private Date latestSampleDateTime;
	@Schema(description = "Number of samples taken from the person associated with the case")
	private Long sampleCount;
	@Schema(description = "Date when the symptoms of the disease first set on")
	private Date symptomOnsetDate;
	@Schema(description = "Name of the region responsible for the case")
	private String responsibleRegion;
	@Schema(description = "Name of the community responsible for the case")
	private String responsibleCommunity;

	private UserReferenceDto reportingUser;

	//@formatter:off
	public CaseIndexDetailedDto(long id, String uuid, String epidNumber, String externalID, String externalToken, String internalToken, String personUuid, String personFirstName, String personLastName,
								Disease disease, DiseaseVariant diseaseVariant, String diseaseDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus,
								PresentCondition presentCondition, Date reportDate, Date creationDate,
								String regionUuid, String districtUuid,
								String healthFacilityUuid, String healthFacilityName, String healthFacilityDetails,
								String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails, String surveillanceOfficerUuid, CaseOutcome outcome,
								Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Sex sex,
								Date quarantineTo, Float completeness, FollowUpStatus followUpStatus, Date followUpUntil, SymptomJournalStatus symptomJournalStatus, VaccinationStatus vaccinationStatus, Date changeDate, Long facilityId,
								String responsibleRegionUuid, String responsibleDistrictUuid, String responsibleDistrictName, boolean isInJurisdiction,
								//detailed fields
								YesNoUnknown reInfection, String city, String street, String houseNumber, String additionalInformation, String postalCode, String phone,
								String reportingUserUuid, String reportingUserFirstName, String reportingUserLastName, Date symptomOnsetDate,
								String responsibleRegion, String responsibleCommunity,
								int visitCount, long eventCount, Date latestSampleDateTime, long sampleCount, Date latestChangedDate) {
		super(id, uuid, epidNumber, externalID, externalToken, internalToken, personUuid, personFirstName, personLastName,
				disease, diseaseVariant, diseaseDetails, caseClassification, investigationStatus,
				presentCondition, reportDate, creationDate,
				regionUuid, districtUuid, healthFacilityUuid,
				healthFacilityName, healthFacilityDetails, pointOfEntryUuid, pointOfEntryName, pointOfEntryDetails, surveillanceOfficerUuid, outcome,
				age, ageType, birthdateDD, birthdateMM, birthdateYYYY, sex,
				quarantineTo, completeness, followUpStatus, followUpUntil, symptomJournalStatus, vaccinationStatus, changeDate, facilityId,
				responsibleRegionUuid, responsibleDistrictUuid, responsibleDistrictName, isInJurisdiction, visitCount, latestChangedDate);
		//@formatter:on

		this.reInfection = reInfection;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.postalCode = postalCode;
		this.phone = phone;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName);
		this.eventCount = eventCount;
		this.latestSampleDateTime = latestSampleDateTime;
		this.sampleCount = sampleCount;
		this.symptomOnsetDate = symptomOnsetDate;
		this.responsibleRegion = responsibleRegion;
		this.responsibleCommunity = responsibleCommunity;
	}

	public CaseIndexDetailedDto(
		long id,
		String uuid,
		String epidNumber,
		String externalID,
		String externalToken,
		String internalToken,
		String personUuid,
		String personFirstName,
		String personLastName,
		Disease disease,
		DiseaseVariant diseaseVariant,
		String diseaseDetails,
		CaseClassification caseClassification,
		InvestigationStatus investigationStatus,
		PresentCondition presentCondition,
		Date reportDate,
		Date creationDate,
		String regionUuid,
		String districtUuid,
		String healthFacilityUuid,
		String healthFacilityName,
		String healthFacilityDetails,
		String pointOfEntryUuid,
		String pointOfEntryName,
		String pointOfEntryDetails,
		String surveillanceOfficerUuid,
		CaseOutcome outcome,
		Integer age,
		ApproximateAgeType ageType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		Sex sex,
		Date quarantineTo,
		Float completeness,
		FollowUpStatus followUpStatus,
		Date followUpUntil,
		SymptomJournalStatus symptomJournalStatus,
		VaccinationStatus vaccinationStatus,
		Date changeDate,
		Long facilityId,
		String responsibleRegionUuid,
		String responsibleDistrictUuid,
		String responsibleDistrictName,
		boolean isInJurisdiction,
		//detailed fields
		YesNoUnknown reInfection,
		String city,
		String street,
		String houseNumber,
		String additionalInformation,
		String postalCode,
		String phone,
		String reportingUserUuid,
		String reportingUserFirstName,
		String reportingUserLastName,
		Date symptomOnsetDate,
		String responsibleRegion,
		String responsibleCommunity,
		int visitCount,
		Date latestSampleDateTime,
		long sampleCount,
		Date latestChangedDate) {
		this(
			id,
			uuid,
			epidNumber,
			externalID,
			externalToken,
			internalToken,
			personUuid,
			personFirstName,
			personLastName,
			disease,
			diseaseVariant,
			diseaseDetails,
			caseClassification,
			investigationStatus,
			presentCondition,
			reportDate,
			creationDate,
			regionUuid,
			districtUuid,
			healthFacilityUuid,
			healthFacilityName,
			healthFacilityDetails,
			pointOfEntryUuid,
			pointOfEntryName,
			pointOfEntryDetails,
			surveillanceOfficerUuid,
			outcome,
			age,
			ageType,
			birthdateDD,
			birthdateMM,
			birthdateYYYY,
			sex,
			quarantineTo,
			completeness,
			followUpStatus,
			followUpUntil,
			symptomJournalStatus,
			vaccinationStatus,
			changeDate,
			facilityId,
			responsibleRegionUuid,
			responsibleDistrictUuid,
			responsibleDistrictName,
			isInJurisdiction,
			reInfection,
			city,
			street,
			houseNumber,
			additionalInformation,
			postalCode,
			phone,
			reportingUserUuid,
			reportingUserFirstName,
			reportingUserLastName,
			symptomOnsetDate,
			responsibleRegion,
			responsibleCommunity,
			visitCount,
			0,
			latestSampleDateTime,
			sampleCount,
			latestChangedDate);
	}

	public YesNoUnknown getReInfection() {
		return reInfection;
	}

	public void setReInfection(YesNoUnknown reInfection) {
		this.reInfection = reInfection;
	}

	public String getCity() {
		return city;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getPhone() {
		return phone;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Long getEventCount() {
		return eventCount;
	}

	public String getLatestEventId() {
		return latestEventId;
	}

	public void setLatestEventId(String latestEventId) {
		this.latestEventId = latestEventId;
	}

	public String getLatestEventTitle() {
		return latestEventTitle;
	}

	public void setLatestEventTitle(String latestEventTitle) {
		this.latestEventTitle = latestEventTitle;
	}

	public EventStatus getLatestEventStatus() {
		return latestEventStatus;
	}

	public void setLatestEventStatus(EventStatus latestEventStatus) {
		this.latestEventStatus = latestEventStatus;
	}

	public Date getLatestSampleDateTime() {
		return latestSampleDateTime;
	}

	public Long getSampleCount() {
		return sampleCount;
	}

	public Date getSymptomOnsetDate() {
		return symptomOnsetDate;
	}

	public String getResponsibleRegion() {
		return responsibleRegion;
	}

	public String getResponsibleCommunity() {
		return responsibleCommunity;
	}
}
