package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.IsCase;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class ContactListEntryDto extends PseudonymizableIndexDto implements IsContact, Serializable, Cloneable {

	private static final long serialVersionUID = -3753167578595277556L;
	private String caseUuid;
	private ContactStatus contactStatus;
	private Disease disease;
	private ContactClassification contactClassification;
	private ContactCategory contactCategory;
	private Date reportDate;
	private Date lastContactDate;

	private boolean isInJurisdiction;

	public ContactListEntryDto(
		String uuid,
		String caseUuid,
		ContactStatus contactStatus,
		Disease disease,
		ContactClassification contactClassification,
		ContactCategory contactCategory,
		Date reportDate,
		Date lastContactDate,
		boolean isInJurisdiction) {
		super(uuid);
		this.caseUuid = caseUuid;
		this.contactStatus = contactStatus;
		this.disease = disease;
		this.contactClassification = contactClassification;
		this.contactCategory = contactCategory;
		this.reportDate = reportDate;
		this.lastContactDate = lastContactDate;
		this.isInJurisdiction = isInJurisdiction;
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

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}

	@Override
	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	@Override
	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}

	@Override
	public IsCase getCaze() {
		return new CaseReferenceDto(caseUuid);
	}
}
