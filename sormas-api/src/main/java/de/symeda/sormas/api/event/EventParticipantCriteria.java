package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class EventParticipantCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 5981720569585071845L;

	public static final String FREE_TEXT = "freeText";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String ONLY_COUNT_CONTACT_WITH_SOURCE_CASE_IN_EVENT = "onlyCountContactsWithSourceCaseInEvent";
	public static final String DISEASE = "disease";

	private EventReferenceDto event;
	private PersonReferenceDto person;
	private String freeText;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private Boolean onlyCountContactsWithSourceCaseInEvent;
	private Disease disease;

	@IgnoreForUrl
	public EventReferenceDto getEvent() {
		return event;
	}

	public EventParticipantCriteria event(EventReferenceDto event) {
		this.event = event;
		return this;
	}

	@IgnoreForUrl
	public PersonReferenceDto getPerson() {
		return person;
	}


	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public EventParticipantCriteria person(PersonReferenceDto person) {
		this.person = person;
		return this;
	}

	public EventParticipantCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public void setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public void setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public Boolean getOnlyCountContactsWithSourceCaseInEvent() {
		return onlyCountContactsWithSourceCaseInEvent;
	}

	public void setOnlyCountContactsWithSourceCaseInEvent(Boolean onlyCountContactsWithSourceCaseInEvent) {
		this.onlyCountContactsWithSourceCaseInEvent = onlyCountContactsWithSourceCaseInEvent;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
}
