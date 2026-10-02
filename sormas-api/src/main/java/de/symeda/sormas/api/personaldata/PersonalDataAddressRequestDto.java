package de.symeda.sormas.api.personaldata;

import java.util.Objects;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;

public class PersonalDataAddressRequestDto {

	private CaseDataDto caseData;
	private PersonDto person;
	private ContactDto contact;

	public CaseDataDto getCaseData() {
		return caseData;
	}

	public void setCaseData(CaseDataDto caseData) {
		this.caseData = caseData;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
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
		PersonalDataAddressRequestDto that = (PersonalDataAddressRequestDto) o;
		return Objects.equals(caseData, that.caseData) && Objects.equals(person, that.person) && Objects.equals(contact, that.contact);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseData, person, contact);
	}
}
