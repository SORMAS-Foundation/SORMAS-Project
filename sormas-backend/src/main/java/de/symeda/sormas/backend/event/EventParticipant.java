package de.symeda.sormas.backend.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.event.KindOfInvolvement;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;

@Entity
public class EventParticipant extends AbstractDomainObject {

	private static final long serialVersionUID = -9006001699517297107L;

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String KIND_OF_INVOLVEMENT = "kindOfInvolvement";
	
	private Event event;
	private Person person;
	private KindOfInvolvement kindOfInvolvement;
	
	@ManyToOne(cascade = {})
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public KindOfInvolvement getKindOfInvolvement() {
		return kindOfInvolvement;
	}
	
	public void setKindOfInvolvement(KindOfInvolvement kindOfInvolvement) {
		this.kindOfInvolvement = kindOfInvolvement;
	}
	
	@Override
	public String toString() {
		return getPerson().toString();
	}
	
}
