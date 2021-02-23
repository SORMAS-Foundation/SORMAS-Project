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

package de.symeda.sormas.backend.sormastosormas.databuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.sormastosormas.AssociatedEntityWrapper;
import de.symeda.sormas.backend.sormastosormas.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.event.EventShareData;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class EventShareDataBuilder implements ShareDataBuilder<Event, SormasToSormasEventDto> {

	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;
	@EJB
	private EventParticipantService eventParticipantService;

	@Override
	public EventShareData buildShareData(Event data, User user, SormasToSormasOptionsDto options) throws SormasToSormasException {
		Pseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(options);

		EventDto eventDto = getEventDto(data, pseudonymizer);

		SormasToSormasEventDto entity = new SormasToSormasEventDto(eventDto, dataBuilderHelper.createSormasToSormasOriginInfo(user, options));

		EventShareData eventShareData = new EventShareData(entity);

		List<EventParticipant> eventParticipants = Collections.emptyList();
		if (options.isWithEventParticipants()) {
			eventParticipants = eventParticipantService.getByEventUuids(Collections.singletonList(eventDto.getUuid()));
		}

		entity.setEventParticipants(getEventParticipantDtos(eventParticipants, options, pseudonymizer));
		eventShareData.addAssociatedEntities(AssociatedEntityWrapper.forEventParticipants(eventParticipants));

		return eventShareData;
	}

	private EventDto getEventDto(Event event, Pseudonymizer pseudonymizer) {
		EventDto eventDto = eventFacade.convertToDto(event, pseudonymizer);

		eventDto.setReportingUser(null);
		eventDto.setSormasToSormasOriginInfo(null);

		return eventDto;
	}

	private List<EventParticipantDto> getEventParticipantDtos(
		List<EventParticipant> eventParticipants,
		SormasToSormasOptionsDto options,
		Pseudonymizer pseudonymizer) {
		return eventParticipants.stream().map(eventParticipant -> {
			EventParticipantDto dto = eventParticipantFacade.convertToDto(eventParticipant, pseudonymizer);

			dto.setReportingUser(null);
			dto.setSormasToSormasOriginInfo(null);

			dataBuilderHelper.pseudonymiePerson(options, dto.getPerson());

			return dto;
		}).collect(Collectors.toList());
	}
}
