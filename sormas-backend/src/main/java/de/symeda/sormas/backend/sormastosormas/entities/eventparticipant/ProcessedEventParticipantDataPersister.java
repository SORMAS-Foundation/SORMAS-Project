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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventParticipantValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedEventParticipantDataPersister
	extends ProcessedDataPersister<EventParticipantDto, SormasToSormasEventParticipantDto, EventParticipant> {

	@EJB
	private EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal eventParticipantFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	public void persistSharedData(SormasToSormasEventParticipantDto processedData, EventParticipant existingEventParticipant)
		throws SormasToSormasValidationException {
		EventParticipantDto eventParticipant = processedData.getEntity();

		handleValidationError(
			() -> personFacade.savePerson(eventParticipant.getPerson(), false, false, false),
			Captions.EventParticipant,
			buildEventParticipantValidationGroupName(eventParticipant));

		handleValidationError(
			() -> eventParticipantFacade.saveEventParticipant(eventParticipant, false, false),
			Captions.EventParticipant,
			buildEventParticipantValidationGroupName(eventParticipant));
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(EventParticipantDto entity, String organizationId) {
		return shareInfoService.getByEventParticipantAndOrganization(entity.getUuid(), organizationId);
	}
}
