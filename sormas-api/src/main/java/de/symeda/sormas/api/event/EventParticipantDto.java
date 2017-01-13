package de.symeda.sormas.api.event;

import de.symeda.sormas.api.person.PersonReferenceDto;

public class EventParticipantDto extends EventParticipantReferenceDto {

	private static final long serialVersionUID = -8725734604520880084L;

	public static final String I18N_PREFIX = "EventParticipant";
	
	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String KIND_OF_INVOLVEMENT = "kindOfInvolvement";
	
	private EventReferenceDto event;
	private PersonReferenceDto person;
	private KindOfInvolvement kindOfInvolvement;
	
	public EventReferenceDto getEvent() {
		return event;
	}
	
	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}
	
	public PersonReferenceDto getPerson() {
		return person;
	}
	
	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}
	
	public KindOfInvolvement getKindOfInvolvement() {
		return kindOfInvolvement;
	}
	
	public void setKindOfInvolvement(KindOfInvolvement kindOfInvolvement) {
		this.kindOfInvolvement = kindOfInvolvement;
	}
	
}
