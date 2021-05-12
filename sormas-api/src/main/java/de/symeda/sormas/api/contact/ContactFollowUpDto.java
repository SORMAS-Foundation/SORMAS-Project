package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.caze.ResponsibleJurisdictionDto;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SensitiveData;

public class ContactFollowUpDto extends FollowUpDto {

	private static final long serialVersionUID = -1257025719012862417L;

	public static final String I18N_PREFIX = "Contact";

	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String LAST_CONTACT_DATE = "lastContactDate";

	@SensitiveData
	private UserReferenceDto contactOfficer;
	private Date lastContactDate;

	private ContactJurisdictionDto jurisdiction;
	private SymptomJournalStatus symptomJournalStatus;

	//@formatter:off
	public ContactFollowUpDto(String uuid, String personFirstName, String personLastName,
							  String contactOfficerUuid, String contactOfficerFirstName, String contactOfficerLastName,
							  Date lastContactDate, Date reportDate, Date followUpUntil, SymptomJournalStatus symptomJournalStatus, Disease disease,
							  String reportingUserUuid, String regionUuid, String districtUuid, String communityUuid,
							  String caseReportingUserUuid,
							  String caseResponsibleRegionUuid, String caseResponsibleDistrictUid, String caseResponsibleCommunityUid,
							  String caseRegionUuid, String caseDistrictUuid, String caseCommunityUud, String caseHealthFacilityUuid, String casePointOfEntryUuid
	) {
	//formatter:on

		super(uuid, personFirstName, personLastName, reportDate, followUpUntil, disease);
		this.contactOfficer = new UserReferenceDto(contactOfficerUuid, contactOfficerFirstName, contactOfficerLastName, null);
		this.lastContactDate = lastContactDate;
		this.symptomJournalStatus = symptomJournalStatus;

		CaseJurisdictionDto caseJurisdiction = caseReportingUserUuid == null
			? null
			: new CaseJurisdictionDto(
				caseReportingUserUuid,
				ResponsibleJurisdictionDto.of(caseResponsibleRegionUuid, caseResponsibleDistrictUid, caseResponsibleCommunityUid),
				caseRegionUuid,
				caseDistrictUuid,
				caseCommunityUud,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
		jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, communityUuid, caseJurisdiction);
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

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}
}
