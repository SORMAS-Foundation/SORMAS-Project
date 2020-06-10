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

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class EventParticipantDtoHelper extends AdoDtoHelper<EventParticipant, EventParticipantDto> {

	private PersonDtoHelper personHelper = new PersonDtoHelper();

	@Override
	protected Class<EventParticipant> getAdoClass() {
		return EventParticipant.class;
	}

	@Override
	protected Class<EventParticipantDto> getDtoClass() {
		return EventParticipantDto.class;
	}

	@Override
	protected Call<List<EventParticipantDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getEventParticipantFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<EventParticipantDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getEventParticipantFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<EventParticipantDto> eventParticipantDtos) throws NoConnectionException {
		return RetroProvider.getEventParticipantFacade().pushAll(eventParticipantDtos);
	}

	@Override
	public void fillInnerFromDto(EventParticipant target, EventParticipantDto source) {
		if (source.getEvent() != null) {
			target.setEvent(DatabaseHelper.getEventDao().queryUuid(source.getEvent().getUuid()));
		} else {
			target.setEvent(null);
		}

		if (source.getPerson() != null) {
			target.setPerson(DatabaseHelper.getPersonDao().queryUuid(source.getPerson().getUuid()));
		} else {
			target.setPerson(null);
		}

		target.setInvolvementDescription(source.getInvolvementDescription());
		target.setResultingCaseUuid(source.getResultingCase() != null ? source.getResultingCase().getUuid() : null);
	}

	@Override
	public void fillInnerFromAdo(EventParticipantDto target, EventParticipant source) {
		if (source.getEvent() != null) {
			Event event = DatabaseHelper.getEventDao().queryForId(source.getEvent().getId());
			target.setEvent(EventDtoHelper.toReferenceDto(event));
		} else {
			target.setEvent(null);
		}

		if (source.getPerson() != null) {
			Person person = DatabaseHelper.getPersonDao().queryForId(source.getPerson().getId());
			target.setPerson(personHelper.adoToDto(person));
		} else {
			target.setPerson(null);
		}

		// Resulting case is never set to null from within the app because it
		if (source.getResultingCaseUuid() != null) {
			target.setResultingCase(new CaseReferenceDto(source.getResultingCaseUuid()));
		} else {
			target.setResultingCase(null);
		}

		target.setInvolvementDescription(source.getInvolvementDescription());
	}

	public static EventParticipantReferenceDto toReferenceDto(EventParticipant ado) {
		if (ado == null) {
			return null;
		}
		EventParticipantReferenceDto dto = new EventParticipantReferenceDto(ado.getUuid());

		return dto;
	}
}
