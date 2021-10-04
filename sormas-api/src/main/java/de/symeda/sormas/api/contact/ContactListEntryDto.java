package de.symeda.sormas.api.contact;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class ContactListEntryDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	private String uuid;
	private ContactStatus contactStatus;
	private Disease disease;
	private ContactClassification contactClassification;
	private ContactCategory contactCategory;

	private boolean isInJurisdiction;

	public ContactListEntryDto(
		String uuid,
		ContactStatus contactStatus,
		Disease disease,
		ContactClassification contactClassification,
		ContactCategory contactCategory,
		boolean isInJurisdiction) {
		this.uuid = uuid;
		this.contactStatus = contactStatus;
		this.disease = disease;
		this.contactClassification = contactClassification;
		this.contactCategory = contactCategory;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public ContactCategory getContactCategory() {
		return contactCategory;
	}

	public void setContactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
	}

	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
