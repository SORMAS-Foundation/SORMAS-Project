package de.symeda.sormas.api.event;

import de.symeda.sormas.api.person.PersonDto;

public class EventParticipantDto extends EventParticipantReferenceDto {

	private static final long serialVersionUID = -8725734604520880084L;

	public static final String I18N_PREFIX = "EventParticipant";
	
	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	
	private EventReferenceDto event;
	private PersonDto person;
	private String involvementDescription;
	
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventParticipantDto other = (EventParticipantDto) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (involvementDescription == null) {
			if (other.involvementDescription != null)
				return false;
		} else if (!involvementDescription.equals(other.involvementDescription))
			return false;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if (!person.equals(other.person))
			return false;
		return true;
	}
	
}
