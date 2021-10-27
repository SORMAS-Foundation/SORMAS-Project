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

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;

@Stateless
@LocalBean
public class ReceivedEventProcessor extends ReceivedDataProcessor<EventDto, SormasToSormasEventDto, SormasToSormasEventPreview, Event> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;
	@EJB
	private EventService eventService;

	@Override
	public void handleReceivedData(SormasToSormasEventDto sharedData, Event existingData) {
		dataValidator.handleIgnoredProperties(sharedData.getEntity(), EventFacadeEjb.toDto(existingData));
	}

	@Override
	public ValidationErrors exists(String uuid) {
		ValidationErrors errors = new ValidationErrors();
		if (eventService.exists(
			(cb, eventRoot, cq) -> cb.and(
				cb.equal(eventRoot.get(AbstractDomainObject.UUID), uuid),
				cb.isNull(eventRoot.get(Event.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(eventRoot.get(Event.SORMAS_TO_SORMAS_SHARES))))) {
			errors.add(new ValidationErrorGroup(Captions.Event), new ValidationErrorMessage(Validations.sormasToSormasEventExists));
		}
		return errors;
	}

	@Override
	public ValidationErrors validation(SormasToSormasEventDto sharedData, Event existingData) {
		return dataValidator.validateEventData(sharedData.getEntity(), existingData);
	}

	@Override
	public ValidationErrors validatePreview(SormasToSormasEventPreview preview) {
		ValidationErrors eventValidationErrors = new ValidationErrors();
		dataValidator.validateLocation(preview.getEventLocation(), Captions.Event, eventValidationErrors);
		return eventValidationErrors;
	}
}
