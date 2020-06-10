/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.event;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;

@Entity(name = EventParticipant.TABLE_NAME)
@DatabaseTable(tableName = EventParticipant.TABLE_NAME)
public class EventParticipant extends AbstractDomainObject {

	private static final long serialVersionUID = -9006001699517297107L;

	public static final String TABLE_NAME = "eventParticipants";
	public static final String I18N_PREFIX = "EventParticipant";

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE_UUID = "resultingCaseUuid";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Event event;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Person person;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String involvementDescription;

	@DatabaseField
	private String resultingCaseUuid;

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

	public String getResultingCaseUuid() {
		return resultingCaseUuid;
	}

	public void setResultingCaseUuid(String resultingCaseUuid) {
		this.resultingCaseUuid = resultingCaseUuid;
	}
}
