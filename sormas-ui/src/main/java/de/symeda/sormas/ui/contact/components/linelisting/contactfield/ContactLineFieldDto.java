package de.symeda.sormas.ui.contact.components.linelisting.contactfield;

import java.io.Serializable;
import java.time.LocalDate;

import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.ui.utils.components.linelisting.person.PersonFieldDto;
import de.symeda.sormas.ui.utils.components.multidayselector.MultiDaySelectorDto;

public class ContactLineFieldDto implements Serializable {

	public static final String DATE_OF_REPORT = "dateOfReport";
	public static final String MULTI_DAY_SELECTOR = "multiDaySelector";
	public static final String TYPE_OF_CONTACT = "typeOfContact";
	public static final String RELATION_TO_CASE = "relationToCase";
	public static final String PERSON = "person";

	private LocalDate dateOfReport;
	private MultiDaySelectorDto multiDaySelector;
	private ContactProximity typeOfContact;
	private ContactRelation relationToCase;
	private PersonFieldDto person;

	public LocalDate getDateOfReport() {
		return dateOfReport;
	}

	public void setDateOfReport(LocalDate dateOfReport) {
		this.dateOfReport = dateOfReport;
	}

	public MultiDaySelectorDto getMultiDaySelector() {
		return multiDaySelector;
	}

	public void setMultiDaySelector(MultiDaySelectorDto multiDaySelector) {
		this.multiDaySelector = multiDaySelector;
	}

	public ContactProximity getTypeOfContact() {
		return typeOfContact;
	}

	public void setTypeOfContact(ContactProximity typeOfContact) {
		this.typeOfContact = typeOfContact;
	}

	public ContactRelation getRelationToCase() {
		return relationToCase;
	}

	public void setRelationToCase(ContactRelation relationToCase) {
		this.relationToCase = relationToCase;
	}

	public PersonFieldDto getPerson() {
		return person;
	}

	public void setPerson(PersonFieldDto person) {
		this.person = person;
	}
}
