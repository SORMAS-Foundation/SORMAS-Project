package de.symeda.sormas.api.externalpersondata;

import java.io.Serializable;
import java.util.Objects;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;

@AuditedClass
public class ExternalPersonDataAddressRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private CaseDataDto caseData;

	private ContactDto contact;

	public CaseDataDto getCaseData() {
		return caseData;
	}

	public void setCaseData(CaseDataDto caseData) {
		this.caseData = caseData;
	}

	public ContactDto getContact() {
		return contact;
	}

	public void setContact(ContactDto contact) {
		this.contact = contact;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalPersonDataAddressRequestDto that = (ExternalPersonDataAddressRequestDto) o;
		return Objects.equals(caseData, that.caseData) && Objects.equals(contact, that.contact);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseData, contact);
	}
}
