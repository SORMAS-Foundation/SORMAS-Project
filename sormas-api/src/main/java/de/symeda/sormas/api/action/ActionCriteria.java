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
package de.symeda.sormas.api.action;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.event.EventReferenceDto;

public class ActionCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -9174165215694877624L;

	private ActionStatus actionStatus;
	private EventReferenceDto event;

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public ActionCriteria actionStatus(ActionStatus actionStatus) {
		setActionStatus(actionStatus);
		return this;
	}

	public ActionCriteria event(EventReferenceDto event) {
		this.event = event;
		return this;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public boolean hasContextCriteria() {
		return getEvent() != null;
	}
}
