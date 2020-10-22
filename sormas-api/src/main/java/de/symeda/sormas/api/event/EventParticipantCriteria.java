package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class EventParticipantCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 5981720569585071845L;

	public static final String FREE_TEXT = "freeText";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String CONTACT_COUNT_ONLY_WITH_SOURCE_CASE_IN_EVENT = "contactCountOnlyWithSourceCaseInEvent";

	private EventReferenceDto event;
	private String freeText;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private Boolean contactCountOnlyWithSourceCaseInEvent;

	@IgnoreForUrl
	public EventReferenceDto getEvent() {
		return event;
	}

	public EventParticipantCriteria event(EventReferenceDto event) {
		this.event = event;
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

	public Boolean getContactCountOnlyWithSourceCaseInEvent() {
		return contactCountOnlyWithSourceCaseInEvent;
	}

	public void setContactCountOnlyWithSourceCaseInEvent(Boolean contactCountOnlyWithSourceCaseInEvent) {
		this.contactCountOnlyWithSourceCaseInEvent = contactCountOnlyWithSourceCaseInEvent;
	}
}
