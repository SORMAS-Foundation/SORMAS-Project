package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import io.swagger.v3.oas.annotations.media.Schema;

public class EventParticipantIndexDto extends PseudonymizableIndexDto implements Serializable {

	private static final long serialVersionUID = 1136399297437006739L;

	public static final String I18N_PREFIX = "EventParticipant";

	public static final String UUID = "uuid";
	public static final String PERSON_UUID = "personUuid";
	public static final String CASE_UUID = "caseUuid";
	public static final String EVENT_UUID = "eventUuid";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String SEX = "sex";
	public static final String APPROXIMATE_AGE = "approximateAge";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String CONTACT_COUNT = "contactCount";
	public static final String VACCINATION_STATUS = "vaccinationStatus";

	@Schema(description = "UUID of the person entry corresponding to the event participant")
	private String personUuid;
	@Schema(description = "UUID of the corresponding case resulting from the event participant")
	private String caseUuid;
	@Schema(description = "UUID of the event that the participant attended")
	private String eventUuid;
	@PersonalData
	@SensitiveData
	@Schema(description = "First name(s) of the participant")
	private String firstName;
	@SensitiveData
	@Schema(description = "Last name of the participant")
	private String lastName;
	@Schema(description = "Sex of the participant")
	private Sex sex;
	@Schema(description = "Age of the participant")
	private Integer approximateAge;
	@SensitiveData
	@Schema(description = "Free text description on how the person was involved in the event")
	private String involvementDescription;
	@Schema(description = "Number of contacts the participant had at this event")
	private long contactCount;

	private PathogenTestResultType pathogenTestResult;
	@Schema(description = "Date and time when the sample was taken from the event participant")
	private Date sampleDateTime;

	private VaccinationStatus vaccinationStatus;

	@Schema(description = "Whether the DTO is in the user's jurisdiction. Used to determine which user right needs to be considered"
		+ "to decide whether sensitive and/or personal data is supposed to be shown.")
	private boolean isInJurisdiction;
	@Schema(
		description = "Whether the DTO is in the jurisdiction of or even owned by the user. Used to determine which user right needs to be considered"
			+ "to decide whether sensitive and/or personal data is supposed to be shown.")
	private boolean isInJurisdictionOrOwned;

	public EventParticipantIndexDto(
		String uuid,
		String personUuid,
		String caseUuid,
		String eventUuid,
		String firstName,
		String lastName,
		Sex sex,
		Integer approximateAge,
		ApproximateAgeType approximateAgeType,
		String involvementDescription,
		PathogenTestResultType pathogenTestResult,
		Date sampleDateTime,
		VaccinationStatus vaccinationStatus,
		String reportingUserUuid,
		boolean isInJurisdiction,
		boolean isInJurisdictionOrOwned) {

		super(uuid);
		this.personUuid = personUuid;
		this.caseUuid = caseUuid;
		this.eventUuid = eventUuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.approximateAge = approximateAge;
		this.involvementDescription = involvementDescription;
		this.pathogenTestResult = pathogenTestResult;
		this.sampleDateTime = sampleDateTime;
		this.vaccinationStatus = vaccinationStatus;
		this.isInJurisdiction = isInJurisdiction;
		this.isInJurisdictionOrOwned = isInJurisdictionOrOwned;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public String getCaseUuid() {
		return caseUuid;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Integer getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(Integer approximateAge) {
		this.approximateAge = approximateAge;
	}

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	public long getContactCount() {
		return contactCount;
	}

	public void setContactCount(long contactCount) {
		this.contactCount = contactCount;
	}

	public PathogenTestResultType getPathogenTestResult() {
		return pathogenTestResult;
	}

	public void setPathogenTestResult(PathogenTestResultType pathogenTestResult) {
		this.pathogenTestResult = pathogenTestResult;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	public boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	public boolean getInJurisdictionOrOwned() {
		return isInJurisdictionOrOwned;
	}

	public EventParticipantReferenceDto toReference() {
		return new EventParticipantReferenceDto(getUuid(), firstName, lastName);
	}
}
