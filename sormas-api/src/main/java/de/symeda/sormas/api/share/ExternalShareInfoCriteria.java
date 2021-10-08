/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.share;

import java.io.Serializable;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;

public class ExternalShareInfoCriteria implements Serializable {

	private static final long serialVersionUID = -4235095390812680609L;

	private CaseReferenceDto caze;

	private EventReferenceDto event;

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public ExternalShareInfoCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;

		return this;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public ExternalShareInfoCriteria event(EventReferenceDto event) {
		this.event = event;

		return this;
	}
}
