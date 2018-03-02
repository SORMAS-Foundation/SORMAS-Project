package de.symeda.sormas.backend.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;

@Entity
@Audited
public class EventParticipant extends AbstractDomainObject {

	private static final long serialVersionUID = -9006001699517297107L;

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE = "resultingCase";
	
	private Event event;
	private Person person;
	private String involvementDescription;
	private Case resultingCase;

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

	@Column(length=512, nullable=false)
	public String getInvolvementDescription() {
		return involvementDescription;
	}
	
	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}
	
	@Override
	public String toString() {
		return getPerson().toString();
	}

	@ManyToOne(cascade = {})
	@JoinColumn
	public Case getResultingCase() {
		return resultingCase;
	}

	public void setResultingCase(Case resultingCase) {
		this.resultingCase = resultingCase;
	}

}
