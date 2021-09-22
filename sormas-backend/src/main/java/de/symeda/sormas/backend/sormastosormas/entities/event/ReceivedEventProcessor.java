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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventValidationGroupName;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ReceivedEventProcessor
	implements ReceivedDataProcessor<EventDto, SormasToSormasEventDto, ProcessedEventData, SormasToSormasEventPreview> {

	@EJB
	private UserService userService;
	@EJB
	private Sormas2SormasDataValidator dataValidator;
	@EJB
	private InfrastructureValidator infraValidator;

	@Override
	public ProcessedEventData processReceivedData(SormasToSormasEventDto receivedEvent, EventDto existingEvent)
		throws SormasToSormasValidationException {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		EventDto event = receivedEvent.getEntity();
		SormasToSormasOriginInfoDto originInfo = receivedEvent.getOriginInfo();

		ValidationErrors eventValidationErrors = new ValidationErrors();

		ValidationErrors originInfoErrors = dataValidator.validateOriginInfo(originInfo, Captions.Event);
		eventValidationErrors.addAll(originInfoErrors);

		ValidationErrors eventErrors = dataValidator.validateEventData(event, existingEvent);
		eventValidationErrors.addAll(eventErrors);

		if (eventValidationErrors.hasError()) {
			validationErrors.add(new ValidationErrors(buildEventValidationGroupName(event), eventValidationErrors));
		}

		List<EventParticipantDto> eventParticipants = receivedEvent.getEventParticipants();
		if (eventParticipants != null && eventParticipants.size() > 0) {
			List<ValidationErrors> eventParticipantErrors = dataValidator.validateEventParticipants(eventParticipants);
			validationErrors.addAll(eventParticipantErrors);
		}

		List<SormasToSormasSampleDto> samples = receivedEvent.getSamples();
		if (samples != null && samples.size() > 0) {
			List<ValidationErrors> sampleErrors = dataValidator.validateSamples(samples);
			validationErrors.addAll(sampleErrors);
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return new ProcessedEventData(event, originInfo, eventParticipants, samples);
	}

	@Override
	public SormasToSormasEventPreview processReceivedPreview(SormasToSormasEventPreview preview) throws SormasToSormasValidationException {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		ValidationErrors eventValidationErrors = new ValidationErrors();

		dataValidator.validateLocation(preview.getEventLocation(), Captions.Event, eventValidationErrors);

		if (eventValidationErrors.hasError()) {
			validationErrors.add(new ValidationErrors(buildEventValidationGroupName(preview), eventValidationErrors));
		}

		List<SormasToSormasEventParticipantPreview> eventParticipants = preview.getEventParticipants();
		if (eventParticipants != null && eventParticipants.size() > 0) {
			List<ValidationErrors> eventParticipantErrors = dataValidator.validateEventParticipantPreviews(eventParticipants);
			validationErrors.addAll(eventParticipantErrors);
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return preview;
	}
}
