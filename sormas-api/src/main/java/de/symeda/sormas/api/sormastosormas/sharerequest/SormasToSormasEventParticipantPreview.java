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

import javax.validation.Valid;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class SormasToSormasEventParticipantPreview extends PseudonymizableDto implements HasUuid, Serializable {

	private static final long serialVersionUID = 430061021316700295L;

	public static final String I18N_PREFIX = "EventParticipant";

	public static final String UUID = "uuid";

	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Valid
	private SormasToSormasPersonPreview person;

	private EventReferenceDto event;

	public SormasToSormasPersonPreview getPerson() {
		return person;
	}

	public void setPerson(SormasToSormasPersonPreview person) {
		this.person = person;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}
}
