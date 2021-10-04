package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class ContactListEntryDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "Contact";

	public static final String LAST_CONTACT_DATE = "lastContactDate";

	private String uuid;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private Date lastContactDate;

	private boolean isInJurisdiction;

	public ContactListEntryDto(
		String uuid,
		ContactClassification contactClassification,
		ContactStatus contactStatus,
		Date lastContactDate,
		boolean isInJurisdiction) {
		this.uuid = uuid;
		this.contactClassification = contactClassification;
		this.contactStatus = contactStatus;
		this.lastContactDate = lastContactDate;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
