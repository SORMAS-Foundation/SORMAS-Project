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
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasEventPreview;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.SormasToSormasPseudonymizer;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;

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
	protected SormasToSormasEventDto doBuildShareData(Event data, ShareRequestInfo requestInfo, boolean ownerShipHandedOver) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);
		EventDto eventDto = getDto(data, pseudonymizer);
		return new SormasToSormasEventDto(eventDto);
	}

	@Override
	public void doBusinessValidation(SormasToSormasEventDto sormasToSormasEventDto) throws ValidationRuntimeException {
		eventFacade.validate(sormasToSormasEventDto.getEntity());
	}

	@Override
	public SormasToSormasEventPreview doBuildShareDataPreview(Event event, ShareRequestInfo requestInfo) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);
		return getEventPreview(event, pseudonymizer);
	}

	@Override
	protected EventDto getDto(Event event, SormasToSormasPseudonymizer pseudonymizer) {

		EventDto eventDto = eventFacade.toPseudonymizedDto(event, pseudonymizer.getPseudonymizer());
		// reporting user is not set to null here as it would not pass the validation
		// the receiver appears to set it to SORMAS2SORMAS Client anyway
		eventDto.setSormasToSormasOriginInfo(null);
		dataBuilderHelper.clearIgnoredProperties(eventDto);
		return eventDto;
	}

	public SormasToSormasEventPreview getEventPreview(Event event, SormasToSormasPseudonymizer pseudonymizer) {
		SormasToSormasEventPreview preview = new SormasToSormasEventPreview();

		preview.setUuid(event.getUuid());
		preview.setReportDateTime(event.getReportDateTime());
		preview.setEventTitle(event.getEventTitle());
		preview.setEventDesc(event.getEventDesc());
		preview.setDisease(event.getDisease());
		preview.setDiseaseDetails(event.getDiseaseDetails());
		preview.setEventLocation(LocationFacadeEjb.toDto(event.getEventLocation()));

		pseudonymizer.<SormasToSormasEventPreview> getPseudonymizer().pseudonymizeDto(SormasToSormasEventPreview.class, preview, false, null);

		return preview;
	}
}
