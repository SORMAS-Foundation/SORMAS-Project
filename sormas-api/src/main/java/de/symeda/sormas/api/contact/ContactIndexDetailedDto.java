package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for Contact info with extended personal information")
public class ContactIndexDetailedDto extends ContactIndexDto {

	private static final long serialVersionUID = 577830364406605991L;

	public static final String SEX = "sex";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String DISTRICT_NAME = "districtName";
	public static final String CITY = "city";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String POSTAL_CODE = "postalCode";
	public static final String PHONE = "phone";
	public static final String REPORTING_USER = "reportingUser";
	public static final String LATEST_EVENT_ID = "latestEventId";
	public static final String LATEST_EVENT_TITLE = "latestEventTitle";
	public static final String RELATION_TO_CASE = "relationToCase";

	private Sex sex;
	@Schema(description = "Approximate age of the person associated with the contact")
	private String approximateAge;
	@PersonalData
	@SensitiveData
	@Schema(description = "Name of the city of the person associated with the contact")
	private String city;
	@PersonalData
	@SensitiveData
	@Schema(description = "Name of the Street of the person associated with the contact")
	private String street;
	@PersonalData
	@SensitiveData
	@Schema(description = "House number of the person associated with the contact")
	private String houseNumber;
	@PersonalData
	@SensitiveData
	@Schema(description = "Additonal address information (e.g. ground floor)")
	private String additionalInformation;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(PostalCodePseudonymizer.class)
	@Schema(description = "Postal code of the person associated with the contact")
	private String postalCode;
	@SensitiveData
	@Schema(description = "Phone number of the person associated with the contact")
	private String phone;
	@Schema(description = "User that reported the contact")
	private UserReferenceDto reportingUser;
	@Schema(description = "ID of the latest event the person attended where a contact occured")
	private String latestEventId;
	@Schema(description = "Title of the latest event the person attended where a contact occured")
	private String latestEventTitle;
	@Schema(description = "Number of events the person attended where contacts occured")
	private Long eventCount;
	private ContactRelation relationToCase;

	//@formatter:off
	public ContactIndexDetailedDto(String uuid, String personUuid, String personFirstName, String personLastName,
								   String cazeUuid,
								   Disease disease, String diseaseDetails, String caseFirstName, String caseLastName, String regionName,
								   String districtName, Date lastContactDate, ContactCategory contactCategory,
								   ContactProximity contactProximity, ContactClassification contactClassification, ContactStatus contactStatus, Float completeness,
								   FollowUpStatus followUpStatus, Date followUpUntil, SymptomJournalStatus symptomJournalStatus, VaccinationStatus vaccinationStatus, String contactOfficerUuid, String reportingUserUuid, Date reportDateTime,
								   CaseClassification caseClassification,
								   String caseRegionName,
								   String caseDistrictName,
								   Date changeDate, // XXX: unused, only here for TypedQuery mapping
								   String externalID, String externalToken, String internalToken, boolean isInJurisdiction, boolean isCaseInJurisdiction,
								   Sex sex, Integer approximateAge, ApproximateAgeType approximateAgeType,
								   String city, String street, String houseNumber, String additionalInformation, String postalCode, String phone,
								   String reportingUserFirstName, String reportingUserLastName, ContactRelation relationToCase, int visitCount,
								   Date latestChangedDate // unused, only here for TypedQuery mapping
	) {
	//@formatter:on

		//@formatter:off
		super(uuid, personUuid, personFirstName, personLastName, cazeUuid, disease, diseaseDetails, caseFirstName, caseLastName,
			regionName, districtName, lastContactDate, contactCategory, contactProximity, contactClassification, contactStatus,
				completeness, followUpStatus, followUpUntil, symptomJournalStatus, vaccinationStatus, contactOfficerUuid, reportingUserUuid, reportDateTime, caseClassification,
			caseRegionName, caseDistrictName, changeDate, externalID, externalToken, internalToken, isInJurisdiction, isCaseInJurisdiction , visitCount, latestChangedDate);

		//@formatter:on

		this.sex = sex;
		this.approximateAge = ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.postalCode = postalCode;
		this.phone = phone;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName);
		this.relationToCase = relationToCase;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(String approximateAge) {
		this.approximateAge = approximateAge;
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

	public void setEventCount(Long eventCount) {
		this.eventCount = eventCount;
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

	public ContactRelation getRelationToCase() {
		return relationToCase;
	}

	public void setRelationToCase(ContactRelation relationToCase) {
		this.relationToCase = relationToCase;
	}
}
