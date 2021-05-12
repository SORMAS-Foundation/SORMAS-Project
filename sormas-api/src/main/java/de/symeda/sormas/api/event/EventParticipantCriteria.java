package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class EventParticipantCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 5981720569585071845L;

	public static final String FREE_TEXT = "freeText";
	public static final String BIRTHDATE_YYYY = "birthdateYYYY";
	public static final String BIRTHDATE_MM = "birthdateMM";
	public static final String BIRTHDATE_DD = "birthdateDD";
	public static final String ONLY_COUNT_CONTACT_WITH_SOURCE_CASE_IN_EVENT = "onlyCountContactsWithSourceCaseInEvent";
	public static final String DISEASE = "disease";
	public static final String PATHOGENTESTRESULT = "pathogenTestResult";
	public static final String VACCINATION = "vaccination";

	private EventReferenceDto event;
	private PersonReferenceDto person;
	private String freeText;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private Boolean onlyCountContactsWithSourceCaseInEvent;
	private Disease disease;
	private PathogenTestResultType pathogenTestResult;
	private Vaccination vaccination;
	private Date relevantDate;
	private Boolean excludePseudonymized;
	private Boolean noResultingCase;

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

	public EventParticipantCriteria setPerson(PersonReferenceDto person) {
		this.person = person;
		return this;
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

	public EventParticipantCriteria setBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
		return this;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public EventParticipantCriteria setBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
		return this;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public EventParticipantCriteria setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
		return this;
	}

	public Boolean getOnlyCountContactsWithSourceCaseInEvent() {
		return onlyCountContactsWithSourceCaseInEvent;
	}

	public EventParticipantCriteria setOnlyCountContactsWithSourceCaseInEvent(Boolean onlyCountContactsWithSourceCaseInEvent) {
		this.onlyCountContactsWithSourceCaseInEvent = onlyCountContactsWithSourceCaseInEvent;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public EventParticipantCriteria setDisease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public EventParticipantCriteria setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
		return this;
	}

	public Vaccination getVaccination() {
		return vaccination;
	}

	public EventParticipantCriteria setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
		return this;
	}

	public Date getRelevantDate() {
		return relevantDate;
	}

	public EventParticipantCriteria setRelevantDate(Date relevantDate) {
		this.relevantDate = relevantDate;
		return this;
	}

	public Boolean getExcludePseudonymized() {
		return excludePseudonymized;
	}

	public EventParticipantCriteria setExcludePseudonymized(Boolean excludePseudonymized) {
		this.excludePseudonymized = excludePseudonymized;
		return this;
	}

	public Boolean getNoResultingCase() {
		return noResultingCase;
	}

	public EventParticipantCriteria setNoResultingCase(Boolean noResultingCase) {
		this.noResultingCase = noResultingCase;
		return this;
	}
}
