package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;

public class CaseIndexDetailedDto extends CaseIndexDto {

	private static final long serialVersionUID = -3722694511897383913L;

	public static final String SEX = "sex";
	public static final String CITY = "city";
	public static final String ADDRESS = "address";
	public static final String POSTAL_CODE = "postalCode";
	public static final String PHONE = "phone";
	public static final String REPORTING_USER = "reportingUser";

	@PersonalData
	private String city;
	@PersonalData
	private String address;
	@PersonalData
	private String postalCode;
	private String phone;
	private UserReferenceDto reportingUser;

	//@formatter:off
	public CaseIndexDetailedDto(long id, String uuid, String epidNumber, String externalID, String personFirstName, String personLastName,
								Disease disease, String diseaseDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus,
								PresentCondition presentCondition, Date reportDate, String reportingUserUuid, Date creationDate,
								String regionUuid, String districtUuid, String districtName, String communityUuid,
								String healthFacilityUuid, String healthFacilityName, String healthFacilityDetails,
								String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails, String surveillanceOfficerUuid, CaseOutcome outcome,
								Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Sex sex,
								Date quarantineTo, Float completeness,
								String city, String address, String postalCode, String phone,
								String reportingUserFirstName, String reportingUserLastName) {

		super(id, uuid, epidNumber, externalID, personFirstName, personLastName, disease, diseaseDetails, caseClassification, investigationStatus,
				presentCondition, reportDate, reportingUserUuid, creationDate, regionUuid, districtUuid, districtName, communityUuid,
				healthFacilityUuid, healthFacilityName, healthFacilityDetails, pointOfEntryUuid, pointOfEntryName, pointOfEntryDetails, surveillanceOfficerUuid, outcome,
				age, ageType, birthdateDD, birthdateMM, birthdateYYYY, sex,
				quarantineTo, completeness);
		//@formatter:on

		this.city = city;
		this.address = address;
		this.postalCode = postalCode;
		this.phone = phone;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName, null);
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
}
