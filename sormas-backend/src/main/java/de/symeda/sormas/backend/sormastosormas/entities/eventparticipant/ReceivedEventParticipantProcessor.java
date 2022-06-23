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

import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ReceivedEventParticipantProcessor
	extends
	ReceivedDataProcessor<EventParticipant, EventParticipantDto, SormasToSormasEventParticipantDto, SormasToSormasEventParticipantPreview, EventParticipant, EventParticipantService, SormasToSormasEventParticipantDtoValidator> {

	public ReceivedEventParticipantProcessor() {
	}

	@Inject
	protected ReceivedEventParticipantProcessor(
		EventParticipantService service,
		UserService userService,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade,
		SormasToSormasEventParticipantDtoValidator validator) {
		super(service, userService, configFacade, validator);
	}

	@Override
	public void handleReceivedData(SormasToSormasEventParticipantDto sharedData, EventParticipant existingData, SormasToSormasOriginInfoDto originInfo) {
		handleIgnoredProperties(
			sharedData.getEntity().getPerson(),
			Optional.ofNullable(existingData).map(c -> PersonFacadeEjb.toDto(c.getPerson())).orElse(null));
		updateReportingUser(sharedData.getEntity(), existingData);
	}

	@Override
	public ValidationErrors existsNotShared(String uuid) {
		return existsNotShared(
			uuid,
			EventParticipant.SORMAS_TO_SORMAS_ORIGIN_INFO,
			EventParticipant.SORMAS_TO_SORMAS_SHARES,
			Captions.EventParticipant,
			Validations.sormasToSormasEventParticipantExists);
	}

}
