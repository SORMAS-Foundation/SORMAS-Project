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

package de.symeda.sormas.backend.sormastosormas.datapersister;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.function.Consumer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.ProcessedEventData;

@Stateless
@LocalBean
public class ProcessedEventDataPersister implements ProcessedDataPersister<ProcessedEventData> {

	@EJB
	private EventFacadeEjbLocal eventFacade;

	@Override
	public void persistSharedData(ProcessedEventData prcessedData) throws SormasToSormasValidationException {
		persistProcessedData(prcessedData, null);
	}

	@Override
	public void persistReturnedData(ProcessedEventData prcessedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {

	}

	@Override
	public void persistSyncData(ProcessedEventData prcessedData) throws SormasToSormasValidationException {

	}

	private void persistProcessedData(ProcessedEventData eventData, Consumer<EventDto> afterSaveCase) throws SormasToSormasValidationException {
		EventDto event = eventData.getEntity();

		final EventDto savedEvent;
		savedEvent = handleValidationError(() -> eventFacade.saveEvent(event), Captions.CaseData, buildCaseValidationGroupName(event));
		if (afterSaveCase != null) {
			afterSaveCase.accept(savedEvent);
		}

//		if (caseData.getSamples() != null) {
//			dataPersisterHelper.persistSamples(caseData.getSamples(), beforeSaveSample != null ? (s) -> beforeSaveSample.accept(savedEvent, s) : null);
//		}
	}
}
