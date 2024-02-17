package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.IsCase;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SensitiveData;

public class ContactFollowUpDto extends FollowUpDto implements IsContact {

	private static final long serialVersionUID = -1257025719012862417L;

	public static final String I18N_PREFIX = "Contact";

	public static final String CONTACT_OFFICER = "contactOfficer";
	public static final String LAST_CONTACT_DATE = "lastContactDate";

	private String caseUuid;
	@SensitiveData
	private UserReferenceDto contactOfficer;
	private Date lastContactDate;

	private Boolean isInJurisdiction;
	private SymptomJournalStatus symptomJournalStatus;

	//@formatter:off
	public ContactFollowUpDto(String uuid, String caseUuid, Date changeDate, String personFirstName, String personLastName,
							  String contactOfficerUuid, String contactOfficerFirstName, String contactOfficerLastName,
							  Date lastContactDate, Date reportDate, Date followUpUntil, SymptomJournalStatus symptomJournalStatus, Disease disease,
							  boolean isInJurisdiction) {
	//formatter:on

		super(uuid, personFirstName, personLastName, reportDate, followUpUntil, disease);
		this.caseUuid = caseUuid;
		this.contactOfficer = new UserReferenceDto(contactOfficerUuid, contactOfficerFirstName, contactOfficerLastName);
		this.lastContactDate = lastContactDate;
		this.symptomJournalStatus = symptomJournalStatus;
		this.isInJurisdiction = isInJurisdiction;
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

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}
	
	@Override 
	public IsCase getCaze() {
    	return new CaseReferenceDto(caseUuid);
    }
}
