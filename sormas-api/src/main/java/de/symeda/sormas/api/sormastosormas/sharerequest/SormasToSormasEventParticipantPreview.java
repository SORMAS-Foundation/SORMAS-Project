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

package de.symeda.sormas.api.sormastosormas.sharerequest;

import java.io.Serializable;

import de.symeda.sormas.api.HasUuid;

public class SormasToSormasEventParticipantPreview implements HasUuid, Serializable {

	private static final long serialVersionUID = 430061021316700295L;

	public static final String I18N_PREFIX = "EventParticipant";

	public static final String UUID = "uuid";

	private String uuid;

	private SormasToSormasPersonPreview person;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public SormasToSormasPersonPreview getPerson() {
		return person;
	}

	public void setPerson(SormasToSormasPersonPreview person) {
		this.person = person;
	}
}
