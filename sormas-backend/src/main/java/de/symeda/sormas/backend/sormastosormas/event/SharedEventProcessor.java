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

package de.symeda.sormas.backend.sormastosormas.event;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventParticipantValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventValidationGroupName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.sormastosormas.SharedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.SharedDataProcessorHelper;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class SharedEventProcessor implements SharedDataProcessor<EventDto, SormasToSormasEventDto, ProcessedEventData> {

	@EJB
	private UserService userService;
	@EJB
	private SharedDataProcessorHelper dataProcessorHelper;

	@Override
	public ProcessedEventData processSharedData(SormasToSormasEventDto sharedData) throws SormasToSormasValidationException {
		Map<String, ValidationErrors> validationErrors = new HashMap<>();

		EventDto event = sharedData.getEntity();
		SormasToSormasOriginInfoDto originInfo = sharedData.getOriginInfo();

		ValidationErrors eventValidationErrors = new ValidationErrors();

		ValidationErrors originInfoErrors = dataProcessorHelper.processOriginInfo(originInfo, Captions.Contact);
		eventValidationErrors.addAll(originInfoErrors);

		ValidationErrors eventErrors = processEventData(event);
		eventValidationErrors.addAll(eventErrors);

		if (eventValidationErrors.hasError()) {
			validationErrors.put(buildEventValidationGroupName(event), eventValidationErrors);
		}

		List<EventParticipantDto> eventParticipants = sharedData.getEventParticipants();
		if (eventParticipants != null) {
			Map<String, ValidationErrors> eventParticipantErrors = processEventParticipants(eventParticipants);
			validationErrors.putAll(eventParticipantErrors);
		}

		List<SormasToSormasSampleDto> samples = sharedData.getSamples();
		if (samples != null) {
			Map<String, ValidationErrors> sampleErrors = dataProcessorHelper.processSamples(samples);
			validationErrors.putAll(sampleErrors);
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return new ProcessedEventData(event, originInfo, eventParticipants, samples);
	}

	private ValidationErrors processEventData(EventDto event) {
		ValidationErrors caseValidationErrors = new ValidationErrors();

		event.setReportingUser(userService.getCurrentUser().toReference());
		event.setResponsibleUser(userService.getCurrentUser().toReference());

		LocationDto eventLocation = event.getEventLocation();
		DataHelper.Pair<SharedDataProcessorHelper.InfrastructureData, List<String>> infrastructureAndErrors =
			dataProcessorHelper.loadLocalInfrastructure(
				eventLocation.getRegion(),
				eventLocation.getDistrict(),
				eventLocation.getCommunity(),
				eventLocation.getFacilityType(),
				eventLocation.getFacility(),
				eventLocation.getFacilityDetails(),
				null,
				null);

		dataProcessorHelper.handleInfraStructure(infrastructureAndErrors, Captions.CaseData, caseValidationErrors, infrastructureData -> {
			eventLocation.setRegion(infrastructureData.getRegion());
			eventLocation.setDistrict(infrastructureData.getDistrict());
			eventLocation.setCommunity(infrastructureData.getCommunity());
			eventLocation.setFacility(infrastructureData.getFacility());
		});

		return caseValidationErrors;
	}

	private Map<String, ValidationErrors> processEventParticipants(List<EventParticipantDto> eventParticipants) {
		Map<String, ValidationErrors> errors = new HashMap<>();

		eventParticipants.forEach(eventParticipant -> {
			dataProcessorHelper.processPerson(eventParticipant.getPerson());

			ValidationErrors validationErrors = new ValidationErrors();

			DataHelper.Pair<SharedDataProcessorHelper.InfrastructureData, List<String>> infraStruvtureAndErrors =
				dataProcessorHelper.loadLocalInfrastructure(eventParticipant.getRegion(), eventParticipant.getDistrict(), null);
			dataProcessorHelper.handleInfraStructure(infraStruvtureAndErrors, Captions.EventParticipant, validationErrors, (infrastructureData -> {
				eventParticipant.setRegion(infrastructureData.getRegion());
				eventParticipant.setDistrict(infrastructureData.getDistrict());
			}));

			if (validationErrors.hasError()) {
				errors.put(buildEventParticipantValidationGroupName(eventParticipant), validationErrors);
			}
		});

		return errors;
	}
}
