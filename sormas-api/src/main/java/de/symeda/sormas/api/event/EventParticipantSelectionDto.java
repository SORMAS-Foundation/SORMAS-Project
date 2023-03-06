package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class EventParticipantSelectionDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String EVENT_UUID = "eventUuid";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_PARTICIPANT_UUID = "eventParticipantUuid";
	public static final String PERSON_UUID = "personUuid";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String SEX = "sex";
	public static final String DISTRICT_NAME = "districtName";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE_UUID = "resultingCaseUuid";
	public static final String CONTACT_COUNT = "contactCount";
	public static final String I18N_PREFIX = "EventParticipantSelection";

	private final EventReferenceDto event;
	private final String personUuid;
	private final String firstName;
	private final String lastName;
	private final String ageAndBirthDate;
	private final Integer approximateAge;
	private final ApproximateAgeType approximateAgeType;
	private final Integer birthdateDD;
	private final Integer birthdateMM;
	private final Integer birthdateYYYY;
	private final Sex sex;
	private final String districtName;
	private final String involvementDescription;
	private final String resultingCaseUuid;
	private long contactCount;

	public EventParticipantSelectionDto(
		String eventUuid,
		String eventTitle,
		String eventParticipantUuid,
		String personUuid,
		String firstName,
		String lastName,
		Integer approximateAge,
		ApproximateAgeType approximateAgeType,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		Sex sex,
		String districtName,
		String involvementDescription,
		String resultingCaseUuid

	) {
		super(eventParticipantUuid);
		this.event = new EventReferenceDto(eventUuid, eventTitle);
		this.personUuid = personUuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.approximateAge = approximateAge;
		this.approximateAgeType = approximateAgeType;
		this.birthdateDD = birthdateDD;
		this.birthdateMM = birthdateMM;
		this.birthdateYYYY = birthdateYYYY;
		this.sex = sex;
		this.districtName = districtName;
		this.involvementDescription = involvementDescription;
		this.resultingCaseUuid = resultingCaseUuid;
		this.ageAndBirthDate = PersonHelper.getAgeAndBirthdateString(approximateAge, approximateAgeType, birthdateDD, birthdateMM, birthdateYYYY);
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public Integer getApproximateAge() {
		return approximateAge;
	}

	public ApproximateAgeType getApproximateAgeType() {
		return approximateAgeType;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}

	public Sex getSex() {
		return sex;
	}

	public String getDistrictName() {
		return districtName;
	}

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public String getResultingCaseUuid() {
		return resultingCaseUuid;
	}

	public long getContactCount() {
		return contactCount;
	}

	public void setContactCount(long contactCount) {
		this.contactCount = contactCount;
	}
}
