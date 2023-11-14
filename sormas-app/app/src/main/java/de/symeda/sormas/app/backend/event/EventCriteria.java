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

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.caze.Case;

public class EventCriteria implements Serializable {

	private EventStatus eventStatus;
	private Case caze;
	private Disease disease;

	private String textFilter;

	private EpiWeek epiWeekFrom;

	private EpiWeek epiWeekTo;

	public EventCriteria eventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
		return this;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public Case getCaze() {
		return caze;
	}

	public EventCriteria caze(Case caze) {
		this.caze = caze;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getTextFilter() {
		return textFilter;
	}

	public void setTextFilter(String textFilter) {
		this.textFilter = textFilter;
	}

	public EpiWeek getEpiWeekFrom() {
		return epiWeekFrom;
	}

	public void setEpiWeekFrom(EpiWeek epiWeekFrom) {
		this.epiWeekFrom = epiWeekFrom;
	}

	public EpiWeek getEpiWeekTo() {
		return epiWeekTo;
	}

	public void setEpiWeekTo(EpiWeek epiWeekTo) {
		this.epiWeekTo = epiWeekTo;
	}
}
