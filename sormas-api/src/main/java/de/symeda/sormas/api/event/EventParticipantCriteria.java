package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.VaccinationStatus;
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
	public static final String VACCINATION_STATUS = "vaccinationStatus";

	private EventReferenceDto event;
	private PersonReferenceDto person;
	private String freeText;
	private Integer birthdateYYYY;
	private Integer birthdateMM;
	private Integer birthdateDD;
	private Boolean onlyCountContactsWithSourceCaseInEvent;
	private Disease disease;
	private PathogenTestResultType pathogenTestResult;
	private VaccinationStatus vaccinationStatus;
	private Date relevantDate;
	private Boolean excludePseudonymized;
	private Boolean noResultingCase;
	private EntityRelevanceStatus relevanceStatus;

	@IgnoreForUrl
	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}

	public EventParticipantCriteria withEvent(EventReferenceDto event) {
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

	public EventParticipantCriteria withPerson(PersonReferenceDto person) {
		this.person = person;
		return this;
	}

	public EventParticipantCriteria withFreeText(String freeText) {
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

	public EventParticipantCriteria withBirthdateYYYY(Integer birthdateYYYY) {
		this.birthdateYYYY = birthdateYYYY;
		return this;
	}

	public EventParticipantCriteria withBirthdateMM(Integer birthdateMM) {
		this.birthdateMM = birthdateMM;
		return this;
	}

	public EventParticipantCriteria withBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
		return this;
	}

	public EventParticipantCriteria withOnlyCountContactsWithSourceCaseInEvent(Boolean onlyCountContactsWithSourceCaseInEvent) {
		this.onlyCountContactsWithSourceCaseInEvent = onlyCountContactsWithSourceCaseInEvent;
		return this;
	}

	public EventParticipantCriteria withDisease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public EventParticipantCriteria withPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
		return this;
	}

	public EventParticipantCriteria withVaccination(VaccinationStatus vaccination) {
		this.vaccinationStatus = vaccination;
		return this;
	}

	public EventParticipantCriteria withRelevantDate(Date relevantDate) {
		this.relevantDate = relevantDate;
		return this;
	}

	public EventParticipantCriteria withExcludePseudonymized(Boolean excludePseudonymized) {
		this.excludePseudonymized = excludePseudonymized;
		return this;
	}

	public EventParticipantCriteria withNoResultingCase(Boolean noResultingCase) {
		this.noResultingCase = noResultingCase;
		return this;
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

	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	public Date getRelevantDate() {
		return relevantDate;
	}

	public void setRelevantDate(Date relevantDate) {
		this.relevantDate = relevantDate;
	}

	public Boolean getExcludePseudonymized() {
		return excludePseudonymized;
	}

	public void setExcludePseudonymized(Boolean excludePseudonymized) {
		this.excludePseudonymized = excludePseudonymized;
	}

	public Boolean getNoResultingCase() {
		return noResultingCase;
	}

	public void setNoResultingCase(Boolean noResultingCase) {
		this.noResultingCase = noResultingCase;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public EventParticipantCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}
}
