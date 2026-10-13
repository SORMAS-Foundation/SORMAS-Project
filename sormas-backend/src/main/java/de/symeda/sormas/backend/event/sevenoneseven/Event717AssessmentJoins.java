/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.backend.event.sevenoneseven;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventJoins;

public class Event717AssessmentJoins extends QueryJoins<Event717Assessment> {

	private Join<Event717Assessment, Event> event;

	private EventJoins eventJoins;

	public Event717AssessmentJoins(From<?, Event717Assessment> root) {
		super(root);
	}

	public Join<Event717Assessment, Event> getEvent() {

		// every assessment belongs to an event
		return getOrCreate(event, Event717Assessment.EVENT, JoinType.INNER, this::setEvent);
	}

	private void setEvent(Join<Event717Assessment, Event> event) {
		this.event = event;
	}

	public EventJoins getEventJoins() {
		return getOrCreate(eventJoins, () -> new EventJoins(getEvent()), this::setEventJoins);
	}

	private void setEventJoins(EventJoins eventJoins) {
		this.eventJoins = eventJoins;
	}
}
