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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedEventDataPersister implements ProcessedDataPersister<SormasToSormasEventDto> {

	@EJB
	private EventFacadeEjbLocal eventFacade;

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSharedData(SormasToSormasEventDto processedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {
		processedData.getEntity().setSormasToSormasOriginInfo(originInfo);

		persistProcessedData(processedData);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistReturnedData(SormasToSormasEventDto processedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {

		EventDto event = processedData.getEntity();
		SormasToSormasShareInfo eventShareInfo = shareInfoService.getByEventAndOrganization(event.getUuid(), originInfo.getOrganizationId());

		if (eventShareInfo != null) {
			eventShareInfo.setOwnershipHandedOver(false);
			shareInfoService.persist(eventShareInfo);
		} else {
			event.setSormasToSormasOriginInfo(originInfo);
		}

		persistProcessedData(processedData);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSyncData(SormasToSormasEventDto processedData, SormasToSormasOriginInfoDto originInfo, ShareTreeCriteria shareTreeCriteria)
		throws SormasToSormasValidationException {
		EventDto event = processedData.getEntity();
		SormasToSormasShareInfo eventShareInfo = shareInfoService.getByEventAndOrganization(event.getUuid(), originInfo.getOrganizationId());

		if (eventShareInfo == null) {
			event.setSormasToSormasOriginInfo(originInfo);
		}

		persistProcessedData(processedData);
	}

	private void persistProcessedData(SormasToSormasEventDto eventData) throws SormasToSormasValidationException {
		EventDto event = eventData.getEntity();

		handleValidationError(() -> eventFacade.saveEvent(event, false, false), Captions.CaseData, buildCaseValidationGroupName(event));
	}
}
