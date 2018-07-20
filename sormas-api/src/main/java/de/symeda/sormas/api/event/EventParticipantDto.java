package de.symeda.sormas.api.event;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.Required;

public class EventParticipantDto extends EntityDto {

	private static final long serialVersionUID = -8725734604520880084L;

	public static final String I18N_PREFIX = "EventParticipant";

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE = "resultingCase";

	@Required
	private EventReferenceDto event;
	@Required
	private PersonDto person;
	private String involvementDescription;
	private CaseReferenceDto resultingCase; // read-only

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	public EventParticipantReferenceDto toReference() {
		return new EventParticipantReferenceDto(getUuid());
	}

	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}

	/**
	 * This should only be called when filling the DTO in the server backend!
	 */
	public void setResultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
	}

}
