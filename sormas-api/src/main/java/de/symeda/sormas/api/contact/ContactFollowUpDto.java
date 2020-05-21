package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.visit.VisitResult;

public class ContactFollowUpDto implements Serializable {

	private static final long serialVersionUID = -1257025719012862417L;

	public static final String I18N_PREFIX = "Contact";

	public static final String UUID = "uuid";
	public static final String PERSON = "person";
	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String DAY_1_RESULT = "day1Result";
	public static final String DAY_2_RESULT = "day2Result";
	public static final String DAY_3_RESULT = "day3Result";
	public static final String DAY_4_RESULT = "day4Result";
	public static final String DAY_5_RESULT = "day5Result";
	public static final String DAY_6_RESULT = "day6Result";
	public static final String DAY_7_RESULT = "day7Result";
	public static final String DAY_8_RESULT = "day8Result";

	private String uuid;
	private PersonReferenceDto person;
	private UserReferenceDto contactOfficer;
	private Date lastContactDate;
	private Date reportDateTime;
	private Date followUpUntil;
	private Disease disease;
	private VisitResult[] visitResults;

	private String reportingUserUuid;
	private String regionUuid;
	private String districtUuid;
	private ContactJurisdictionDto jurisdiction;

	public ContactFollowUpDto(String uuid, String personUuid, String personFirstName, String personLastName,
							  String contactOfficerUuid, String contactOfficerFirstName, String contactOfficerLastName,
							  Date lastContactDate, Date reportDateTime, Date followUpUntil, Disease disease,
							  String reportingUserUuid, String regionUuid, String districtUuid,
							  String caseReportingUserUuid, String caseRegionUuid, String caseDistrictUuid, String caseCommunityUud, String caseHealthFacilityUuid, String casePointOfEntryUuid
	) {
		this.uuid = uuid;
		this.person = new PersonReferenceDto(personUuid, personFirstName, personLastName);
		this.contactOfficer = new UserReferenceDto(contactOfficerUuid, contactOfficerFirstName, contactOfficerLastName, null);
		this.lastContactDate = lastContactDate;
		this.reportDateTime = reportDateTime;
		this.followUpUntil = followUpUntil;
		this.disease = disease;
		visitResults = new VisitResult[8];
		Arrays.fill(visitResults, VisitResult.NOT_PERFORMED);

		this.reportingUserUuid = reportingUserUuid;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid,
				new CaseJurisdictionDto(caseReportingUserUuid, caseRegionUuid, caseDistrictUuid, caseCommunityUud, caseHealthFacilityUuid, casePointOfEntryUuid));
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public UserReferenceDto getContactOfficer() {
		return contactOfficer;
	}

	public void setContactOfficer(UserReferenceDto contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public VisitResult[] getVisitResults() {
		return visitResults;
	}

	public void setVisitResults(VisitResult[] visitResults) {
		this.visitResults = visitResults;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
