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
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang.mutable.MutableBoolean;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.ProcessedEventData;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedEventDataPersister implements ProcessedDataPersister<ProcessedEventData> {

	@EJB
	private EventFacadeEjbLocal eventFacade;

	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;

	@EJB
	private PersonFacadeEjbLocal personFacade;

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@EJB
	private SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal oriInfoFacade;

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSharedData(ProcessedEventData processedData) throws SormasToSormasValidationException {
		persistProcessedData(processedData, null, (event, eventParticipant) -> {
			eventParticipant.setSormasToSormasOriginInfo(event.getSormasToSormasOriginInfo());
		});
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistReturnedData(ProcessedEventData processedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {
		final MutableBoolean originInfoSaved = new MutableBoolean();

		persistProcessedData(processedData, event -> {
			SormasToSormasShareInfo eventShareInfo = shareInfoService.getByEventAndOrganization(event.getUuid(), originInfo.getOrganizationId());
			eventShareInfo.setOwnershipHandedOver(false);
			shareInfoService.persist(eventShareInfo);
		}, (event, eventParticipant) -> {
			SormasToSormasShareInfo eventParticipantShareInfo =
				shareInfoService.getByEventParticipantAndOrganization(eventParticipant.getUuid(), originInfo.getOrganizationId());
			if (eventParticipantShareInfo == null) {
				if (!originInfoSaved.booleanValue()) {
					oriInfoFacade.saveOriginInfo(originInfo);
					originInfoSaved.setValue(true);
				}

				eventParticipant.setSormasToSormasOriginInfo(originInfo);
			} else {
				eventParticipantShareInfo.setOwnershipHandedOver(false);
				shareInfoService.persist(eventParticipantShareInfo);
			}
		});
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSyncData(ProcessedEventData processedData) throws SormasToSormasValidationException {
		SormasToSormasOriginInfoDto originInfo = processedData.getOriginInfo();

		persistProcessedData(processedData, (event) -> {
			SormasToSormasOriginInfoDto contactOriginInfo = event.getSormasToSormasOriginInfo();
			contactOriginInfo.setOwnershipHandedOver(originInfo.isOwnershipHandedOver());
			contactOriginInfo.setComment(originInfo.getComment());

			oriInfoFacade.saveOriginInfo(contactOriginInfo);
		}, (event, eventParticipant) -> {
			if (eventParticipant.getSormasToSormasOriginInfo() == null) {
				eventParticipant.setSormasToSormasOriginInfo(event.getSormasToSormasOriginInfo());
			}
		});
	}

	private void persistProcessedData(
		ProcessedEventData eventData,
		Consumer<EventDto> afterSaveEvent,
		BiConsumer<EventDto, EventParticipantDto> beforeSaveEventParticipant)
		throws SormasToSormasValidationException {
		EventDto event = eventData.getEntity();

		final EventDto savedEvent;
		savedEvent = handleValidationError(() -> eventFacade.saveEvent(event, false), Captions.CaseData, buildCaseValidationGroupName(event));
		if (afterSaveEvent != null) {
			afterSaveEvent.accept(savedEvent);
		}

		if (eventData.getEventParticipants() != null) {
			persistEventParticipants(
				eventData.getEventParticipants(),
				beforeSaveEventParticipant != null ? (ep) -> beforeSaveEventParticipant.accept(savedEvent, ep) : null);
		}
	}

	private void persistEventParticipants(List<EventParticipantDto> eventParticipants, Consumer<EventParticipantDto> beforeSave)
		throws SormasToSormasValidationException {
		for (EventParticipantDto ep : eventParticipants) {
			handleValidationError(
				() -> personFacade.savePerson(ep.getPerson(), false),
				Captions.EventParticipant,
				buildEventValidationGroupName(ep.getEvent()));

			if (beforeSave != null) {
				beforeSave.accept(ep);
			}

			handleValidationError(
				() -> eventParticipantFacade.saveEventParticipant(ep, false),
				Captions.EventParticipant,
				buildEventValidationGroupName(ep.getEvent()));
		}

	}
}
