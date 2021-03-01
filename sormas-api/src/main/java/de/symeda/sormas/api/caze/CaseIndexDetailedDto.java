package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;

public class CaseIndexDetailedDto extends CaseIndexDto {

	private static final long serialVersionUID = -3722694511897383913L;

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
	@SensitiveData
	private String phone;
	private Long eventCount;
	private String latestEventId;
	private String latestEventTitle;
	private EventStatus latestEventStatus;
	private Date latestSampleDateTime;
	private Long sampleCount;
	private Date symptomOnsetDate;

	private UserReferenceDto reportingUser;

	//@formatter:off
	public CaseIndexDetailedDto(long id, String uuid, String epidNumber, String externalID, String externalToken, String personFirstName, String personLastName,
								Disease disease, String diseaseVariantUuid, String diseaseVariantName, String diseaseDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus,
								PresentCondition presentCondition, Date reportDate, String reportingUserUuid, Date creationDate,
								String regionUuid, String districtUuid, String districtName, String communityUuid,
								String healthFacilityUuid, String healthFacilityName, String healthFacilityDetails,
								String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails, String surveillanceOfficerUuid, CaseOutcome outcome,
								Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Sex sex,
								Date quarantineTo, Float completeness, FollowUpStatus followUpStatus, Date followUpUntil, SymptomJournalStatus symptomJournalStatus, Date changeDate, Long facilityId,
								String city, String street, String houseNumber, String additionalInformation, String postalCode, String phone,
								String reportingUserFirstName, String reportingUserLastName, Date symptomOnsetDate,
								int visitCount, long eventCount, Date latestSampleDateTime, long sampleCount) {

		super(id, uuid, epidNumber, externalID, externalToken, personFirstName, personLastName, disease, diseaseVariantUuid, diseaseVariantName, diseaseDetails, caseClassification, investigationStatus,
				presentCondition, reportDate, reportingUserUuid, creationDate, regionUuid, districtUuid, districtName, communityUuid,
				healthFacilityUuid, healthFacilityName, healthFacilityDetails, pointOfEntryUuid, pointOfEntryName, pointOfEntryDetails, surveillanceOfficerUuid, outcome,
				age, ageType, birthdateDD, birthdateMM, birthdateYYYY, sex,
				quarantineTo, completeness, followUpStatus, followUpUntil, symptomJournalStatus, changeDate, facilityId, visitCount);
		//@formatter:on

		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.postalCode = postalCode;
		this.phone = phone;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName, null);
		this.eventCount = eventCount;
		this.latestSampleDateTime = latestSampleDateTime;
		this.sampleCount = sampleCount;
		this.symptomOnsetDate = symptomOnsetDate;
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
}
