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

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedEventDataPersister extends ProcessedDataPersister<EventDto, SormasToSormasEventDto, Event> {

	@EJB
	private EventFacadeEjbLocal eventFacade;

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@EJB
	private SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	protected SormasToSormasOriginInfoFacadeEjb getOriginInfoFacade() {
		return originInfoFacade;
	}

	@Override
	public void persistSharedData(SormasToSormasEventDto processedData, Event existingEvent, boolean isSync)
		throws SormasToSormasValidationException {
		EventDto event = processedData.getEntity();

		handleValidationError(() -> eventFacade.save(event, false, false), Captions.CaseData, buildCaseValidationGroupName(event), event);
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(EventDto entity, String organizationId) {
		return shareInfoService.getByEventAndOrganization(entity.getUuid(), organizationId);
	}
}
