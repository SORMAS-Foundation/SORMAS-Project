package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;

public class ContactIndexDetailedDto extends ContactIndexDto {

	private static final long serialVersionUID = 577830364406605991L;

	public static final String SEX = "sex";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String DISTRICT_NAME = "districtName";
	public static final String CITY = "city";
	public static final String ADDRESS = "address";
	public static final String POSTAL_CODE = "postalCode";
	public static final String PHONE = "phone";
	public static final String REPORTING_USER = "reportingUser";

	private Sex sex;
	private String approximateAge;
	private String districtName;
	@PersonalData
	private String city;
	@PersonalData
	private String address;
	@PersonalData
	private String postalCode;
	private String phone;
	private UserReferenceDto reportingUser;

	//@formatter:off
	public ContactIndexDetailedDto(String uuid, String personFirstName, String personLastName, String cazeUuid, Disease disease, String diseaseDetails,
								   String caseFirstName, String caseLastName, String regionUuid, String districtUuid,
								   Date lastContactDate, ContactCategory contactCategory, ContactProximity contactProximity,
								   ContactClassification contactClassification, ContactStatus contactStatus, FollowUpStatus followUpStatus,
								   Date followUpUntil, String contactOfficerUuid, String reportingUserUuid, Date reportDateTime,
								   CaseClassification caseClassification,
								   String caseReportingUserUid, String caseRegionUuid, String caseDistrictUud, String caseCommunityUuid,
								   String caseHealthFacilityUuid, String casePointOfEntryUuid,
								   Sex sex, Integer approximateAge, ApproximateAgeType approximateAgeType,
								   String districtName, String city, String address, String postalCode, String phone,
								   String reportingUserFirstName, String reportingUserLastName,
								   int visitCount) {
	//@formatter:on

		//@formatter:off
		super(uuid, personFirstName, personLastName, cazeUuid, disease, diseaseDetails, caseFirstName, caseLastName, regionUuid, districtUuid,
				lastContactDate, contactCategory, contactProximity, contactClassification, contactStatus, followUpStatus, followUpUntil,
				contactOfficerUuid, reportingUserUuid, reportDateTime, caseClassification,
				caseReportingUserUid, caseRegionUuid, caseDistrictUud, caseCommunityUuid, caseHealthFacilityUuid, casePointOfEntryUuid, visitCount);
		//@formatter:on

		this.sex = sex;
		this.approximateAge = ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.districtName = districtName;
		this.city = city;
		this.address = address;
		this.postalCode = postalCode;
		this.phone = phone;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName, null);
	}

	public Sex getSex() {
		return sex;
	}

	public String getApproximateAge() {
		return approximateAge;
	}

	public String getDistrictName() {
		return districtName;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
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
}
