package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SensitiveData;
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

	private String uuid;
	private PersonReferenceDto person;
	@SensitiveData
	private UserReferenceDto contactOfficer;
	private Date lastContactDate;
	private Date reportDateTime;
	private Date followUpUntil;
	private Disease disease;
	private VisitResult[] visitResults;

	private ContactJurisdictionDto jurisdiction;

	//@formatter:off
	public ContactFollowUpDto(String uuid, String personUuid, String personFirstName, String personLastName,
							  String contactOfficerUuid, String contactOfficerFirstName, String contactOfficerLastName,
							  Date lastContactDate, Date reportDateTime, Date followUpUntil, Disease disease,
							  String reportingUserUuid, String regionUuid, String districtUuid,
							  String caseReportingUserUuid, String caseRegionUuid, String caseDistrictUuid, String caseCommunityUud, String caseHealthFacilityUuid, String casePointOfEntryUuid
	) {
	//formatter:on

		this.uuid = uuid;
		this.person = new PersonReferenceDto(personUuid, personFirstName, personLastName);
		this.contactOfficer = new UserReferenceDto(contactOfficerUuid, contactOfficerFirstName, contactOfficerLastName, null);
		this.lastContactDate = lastContactDate;
		this.reportDateTime = reportDateTime;
		this.followUpUntil = followUpUntil;
		this.disease = disease;

		CaseJurisdictionDto caseJurisdiction = caseReportingUserUuid == null
			? null
			: new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUuid,
				caseDistrictUuid,
				caseCommunityUud,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
		jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, caseJurisdiction);
	}

	public void initVisitSize(int i) {
		visitResults = new VisitResult[i];
		Arrays.fill(visitResults, VisitResult.NOT_PERFORMED);
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

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
