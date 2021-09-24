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

package de.symeda.sormas.backend.sormastosormas.entities.eventparticipant;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessorHelper;

@Stateless
@LocalBean
public class ReceivedEventParticipantProcessor
	implements ReceivedDataProcessor<EventParticipantDto, SormasToSormasEventParticipantDto, SormasToSormasEventParticipantPreview> {

	@EJB
	private ReceivedDataProcessorHelper dataProcessorHelper;
	@EJB
	private InfrastructureValidator infraValidator;

	@Override
	public ValidationErrors processReceivedData(SormasToSormasEventParticipantDto receivedData, EventParticipantDto existingData) {
		ValidationErrors validationErrors = new ValidationErrors();

		EventParticipantDto eventParticipant = receivedData.getEntity();

		ValidationErrors personValidationErrors = dataProcessorHelper.processPerson(eventParticipant.getPerson());
		validationErrors.addAll(personValidationErrors);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(eventParticipant.getRegion(), eventParticipant.getDistrict(), null);
		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.EventParticipant, validationErrors, (infrastructureData -> {
			eventParticipant.setRegion(infrastructureData.getRegion());
			eventParticipant.setDistrict(infrastructureData.getDistrict());
		}));

		return validationErrors;
	}

	@Override
	public ValidationErrors processReceivedPreview(SormasToSormasEventParticipantPreview eventParticipant) {
		return dataProcessorHelper.processPersonPreview(eventParticipant.getPerson());
	}
}
