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
package de.symeda.sormas.api.event;

import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Required;
import de.symeda.sormas.api.utils.SensitiveData;

public class EventParticipantDto extends PseudonymizableDto {

	private static final long serialVersionUID = -8725734604520880084L;

	public static final String I18N_PREFIX = "EventParticipant";

	public static final String EVENT = "event";
	public static final String PERSON = "person";
	public static final String INVOLVEMENT_DESCRIPTION = "involvementDescription";
	public static final String RESULTING_CASE = "resultingCase";

	@Required
	private EventReferenceDto event;
	@Required
	private PersonDto person;
	@SensitiveData
	private String involvementDescription;
	private CaseReferenceDto resultingCase; // read-only

	public static EventParticipantDto build(EventReferenceDto event) {
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setUuid(DataHelper.createUuid());
		eventParticipant.setEvent(event);
		return eventParticipant;
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

	public String getInvolvementDescription() {
		return involvementDescription;
	}

	public void setInvolvementDescription(String involvementDescription) {
		this.involvementDescription = involvementDescription;
	}

	public EventParticipantReferenceDto toReference() {
		return new EventParticipantReferenceDto(getUuid());
	}

	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}

	/**
	 * This should only be called when filling the DTO in the server backend!
	 */
	public void setResultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
	}
}
