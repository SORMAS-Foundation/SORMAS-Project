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

package de.symeda.sormas.backend.sormastosormas.entities.event;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class EventShareDataBuilder
	extends ShareDataBuilder<EventDto, Event, SormasToSormasEventDto, SormasToSormasEventPreview, SormasToSormasEventDtoValidator> {

	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;
	@EJB
	private EventFacadeEjbLocal eventFacade;

	@Inject
	public EventShareDataBuilder(SormasToSormasEventDtoValidator validator) {
		super(validator);
	}

	public EventShareDataBuilder() {
	}

	@Override
	protected SormasToSormasEventDto doBuildShareData(Event data, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		EventDto eventDto = getEventDto(data, pseudonymizer);

		return new SormasToSormasEventDto(eventDto);
	}

	@Override
	public SormasToSormasEventPreview doBuildShareDataPreview(Event event, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		return getEventPreview(event, pseudonymizer);
	}

	private EventDto getEventDto(Event event, Pseudonymizer pseudonymizer) {
		EventDto eventDto = eventFacade.convertToDto(event, pseudonymizer);

		eventDto.setReportingUser(null);
		eventDto.setSormasToSormasOriginInfo(null);

		dataBuilderHelper.clearIgnoredProperties(eventDto);

		return eventDto;
	}

	private SormasToSormasEventPreview getEventPreview(Event event, Pseudonymizer pseudonymizer) {
		SormasToSormasEventPreview preview = new SormasToSormasEventPreview();

		preview.setUuid(event.getUuid());
		preview.setReportDateTime(event.getReportDateTime());
		preview.setEventTitle(event.getEventTitle());
		preview.setEventDesc(event.getEventDesc());
		preview.setDisease(event.getDisease());
		preview.setDiseaseDetails(event.getDiseaseDetails());
		preview.setEventLocation(LocationFacadeEjb.toDto(event.getEventLocation()));

		pseudonymizer.pseudonymizeDto(SormasToSormasEventPreview.class, preview, false, null);

		return preview;
	}
}
