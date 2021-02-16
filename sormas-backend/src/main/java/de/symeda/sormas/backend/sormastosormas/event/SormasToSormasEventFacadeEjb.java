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

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.EVENT_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventValidationGroupName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventFacade;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.sormastosormas.AbstractSormasToSormasInterface;
import de.symeda.sormas.backend.sormastosormas.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.ProcessedEventData;
import de.symeda.sormas.backend.sormastosormas.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.databuilder.EventShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.datapersister.ProcessedEventDataPersister;
import de.symeda.sormas.backend.sormastosormas.dataprocessor.SharedEventProcessor;

@Stateless(name = "SormasToSormasEventFacade")
public class SormasToSormasEventFacadeEjb extends AbstractSormasToSormasInterface<Event, EventDto, SormasToSormasEventDto, ProcessedEventData>
	implements SormasToSormasEventFacade {

	public static final String SAVE_SHARED_EVENTS = RESOURCE_PATH + EVENT_ENDPOINT;

	private EventService eventService;
	private EventShareDataBuilder shareDataBuilder;
	private SharedEventProcessor sharedEventProcessor;
	private ProcessedEventDataPersister processedEventDataPersister;

	@EJB
	private EventFacadeEjbLocal eventFacade;

	@EJB
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@EJB
	public void setShareDataBuilder(EventShareDataBuilder shareDataBuilder) {
		this.shareDataBuilder = shareDataBuilder;
	}

	@EJB
	public void setSharedEventProcessor(SharedEventProcessor sharedEventProcessor) {
		this.sharedEventProcessor = sharedEventProcessor;
	}

	@EJB
	public void setProcessedEventDataPersister(ProcessedEventDataPersister processedEventDataPersister) {
		this.processedEventDataPersister = processedEventDataPersister;
	}

	public SormasToSormasEventFacadeEjb() {
		super(SAVE_SHARED_EVENTS, Captions.Event);
	}

	@Override
	protected Class<SormasToSormasEventDto[]> getShareDataClass() {
		return SormasToSormasEventDto[].class;
	}

	@Override
	protected void validateEntityBeforeSend(List<Event> entities) throws SormasToSormasException {
		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		for (Event event : entities) {
			if (!eventService.isEventEditAllowed(event)) {
				validationErrors.put(
					buildEventValidationGroupName(event),
					ValidationErrors
						.create(I18nProperties.getCaption(Captions.Event), I18nProperties.getString(Strings.errorSormasToSormasNotEditable)));
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasShare), validationErrors);
		}
	}

	@Override
	protected ValidationErrors validateSharedEntity(EventDto entity) {
		ValidationErrors errors = new ValidationErrors();

		if (eventFacade.exists(entity.getUuid())) {
			errors.add(I18nProperties.getCaption(Captions.Event), I18nProperties.getValidationError(Validations.sormasToSormasEventExists));
		}

		return errors;
	}

	@Override
	protected void setEntityShareInfoAssociatedObject(SormasToSormasShareInfo sormasToSormasShareInfo, Event entity) {
		sormasToSormasShareInfo.setEvent(entity);
	}

	@Override
	protected void setAssociatedObjectShareInfoAssociatedObject(SormasToSormasShareInfo sormasToSormasShareInfo, Object t) {

	}

	@Override
	protected BaseAdoService<Event> getEntityService() {
		return eventService;
	}

	@Override
	protected ShareDataBuilder<Event, SormasToSormasEventDto> getShareDataBuilder() {
		return shareDataBuilder;
	}

	@Override
	protected SharedEventProcessor getSharedDataProcessor() {
		return sharedEventProcessor;
	}

	@Override
	protected ProcessedDataPersister<ProcessedEventData> getProcessedDataPersister() {
		return processedEventDataPersister;
	}

	@Override
	public void saveReturnedEntity(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {

	}

	@Override
	public void syncEntity(String eventUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {

	}

	@Override
	public void saveSyncedEntity(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {

	}
}
