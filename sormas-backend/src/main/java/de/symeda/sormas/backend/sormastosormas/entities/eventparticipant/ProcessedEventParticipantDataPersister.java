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
import javax.transaction.Transactional;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedEventParticipantDataPersister implements ProcessedDataPersister<SormasToSormasEventParticipantDto> {

	@EJB
	private EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal eventParticipantFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSharedData(SormasToSormasEventParticipantDto processedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {
		processedData.getEntity().setSormasToSormasOriginInfo(originInfo);

		persistEventParticipant(processedData);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistReturnedData(SormasToSormasEventParticipantDto processedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {
		EventParticipantDto eventParticipant = processedData.getEntity();
		SormasToSormasShareInfo eventParticipantShareInfo =
			shareInfoService.getByEventParticipantAndOrganization(eventParticipant.getUuid(), originInfo.getOrganizationId());

		if (eventParticipantShareInfo != null) {
			eventParticipantShareInfo.setOwnershipHandedOver(false);
			shareInfoService.persist(eventParticipantShareInfo);
		} else {
			eventParticipant.setSormasToSormasOriginInfo(originInfo);
		}

		persistEventParticipant(processedData);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSyncData(
		SormasToSormasEventParticipantDto processedData,
		SormasToSormasOriginInfoDto originInfo,
		ShareTreeCriteria shareTreeCriteria)
		throws SormasToSormasValidationException {
		EventParticipantDto eventParticipant = processedData.getEntity();
		SormasToSormasShareInfo eventParticipantShareInfo =
			shareInfoService.getByEventParticipantAndOrganization(eventParticipant.getUuid(), originInfo.getOrganizationId());

		if (eventParticipantShareInfo == null) {
			eventParticipant.setSormasToSormasOriginInfo(originInfo);
		}

		persistEventParticipant(processedData);
	}

	private void persistEventParticipant(SormasToSormasEventParticipantDto processedData) throws SormasToSormasValidationException {
		EventParticipantDto eventParticipant = processedData.getEntity();

		handleValidationError(
			() -> personFacade.savePerson(eventParticipant.getPerson(), false, false),
			Captions.EventParticipant,
			buildEventParticipantValidationGroupName(eventParticipant));

		handleValidationError(
			() -> eventParticipantFacade.saveEventParticipant(eventParticipant, false, false),
			Captions.EventParticipant,
			buildEventParticipantValidationGroupName(eventParticipant));
	}
}
