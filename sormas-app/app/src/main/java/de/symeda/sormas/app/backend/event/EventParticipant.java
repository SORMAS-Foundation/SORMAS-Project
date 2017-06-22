package de.symeda.sormas.app.backend.event;

import android.support.annotation.Nullable;

import com.googlecode.openbeans.PropertyDescriptor;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;

@Entity(name=EventParticipant.TABLE_NAME)
@DatabaseTable(tableName = EventParticipant.TABLE_NAME)
public class EventParticipant extends AbstractDomainObject {

	private static final long serialVersionUID = -9006001699517297107L;

	public static final String TABLE_NAME = "eventParticipants";
	public static final String I18N_PREFIX = "EventParticipant";

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false)
	private Event event;

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false)
	private Person person;

	@Column(length=512)
	private String involvementDescription;
	
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

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

	@Override
	public boolean isModifiedOrChildModified() {
		boolean modified = super.isModifiedOrChildModified();
		return person.isModifiedOrChildModified() || modified;
	}

	@Override
	public boolean isUnreadOrChildUnread() {
		boolean unread = super.isUnreadOrChildUnread();
		return person.isUnreadOrChildUnread() || unread;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

}
