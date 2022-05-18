/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.action;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventJoins;
import de.symeda.sormas.backend.user.User;

public class ActionJoins extends QueryJoins<Action> {

	private Join<Action, Event> event;
	private Join<Action, User> creator;
	private Join<Action, User> lastModifiedBy;

	private EventJoins eventJoins;

	public ActionJoins(From<?, Action> root) {
		super(root);
	}

	public Join<Action, Event> getEvent() {

		// Despite the usual pattern this is intended to be an INNER join (currently Actions only belong to Events)
		return getOrCreate(event, Action.EVENT, JoinType.INNER, this::setEvent);
	}

	private void setEvent(Join<Action, Event> event) {
		this.event = event;
	}

	public EventJoins getEventJoins() {
		return getOrCreate(eventJoins, () -> new EventJoins(getEvent()), this::setEventJoins);
	}

	private void setEventJoins(EventJoins eventJoins) {
		this.eventJoins = eventJoins;
	}

	public Join<Action, User> getCreator() {
		return getOrCreate(creator, Action.CREATOR_USER, JoinType.LEFT, this::setCreator);
	}

	private void setCreator(Join<Action, User> creator) {
		this.creator = creator;
	}

	public Join<Action, User> getLastModifiedBy() {
		return getOrCreate(lastModifiedBy, Action.LAST_MODIFIED_BY, JoinType.LEFT, this::setLastModifiedBy);
	}

	private void setLastModifiedBy(Join<Action, User> lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
}
