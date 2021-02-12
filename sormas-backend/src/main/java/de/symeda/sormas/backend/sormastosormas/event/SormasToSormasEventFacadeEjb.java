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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventFacade;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.event.Event;
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

	public SormasToSormasEventFacadeEjb() {
		super(SAVE_SHARED_EVENTS, Captions.Event);
	}

	@Override
	protected BaseAdoService<Event> getEntityService() {
		return eventService;
	}

	@EJB
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Override
	protected ShareDataBuilder<Event, SormasToSormasEventDto> getShareDataBuilder() {
		return shareDataBuilder;
	}

	@EJB
	public void setShareDataBuilder(EventShareDataBuilder shareDataBuilder) {
		this.shareDataBuilder = shareDataBuilder;
	}

	@Override
	protected SharedEventProcessor getSharedDataProcessor() {
		return sharedEventProcessor;
	}

	@EJB
	public void setSharedEventProcessor(SharedEventProcessor sharedEventProcessor) {
		this.sharedEventProcessor = sharedEventProcessor;
	}

	@Override
	protected ProcessedDataPersister<ProcessedEventData> getProcessedDataPersister() {
		return processedEventDataPersister;
	}

	@EJB
	public void setProcessedEventDataPersister(ProcessedEventDataPersister processedEventDataPersister) {
		this.processedEventDataPersister = processedEventDataPersister;
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

	@Override
	protected Class<SormasToSormasEventDto[]> getShareDataClass() {
		return SormasToSormasEventDto[].class;
	}

	@Override
	protected void validateEntityBeforeSend(List<Event> entities) throws SormasToSormasException {

	}

	@Override
	protected ValidationErrors validateSharedEntity(EventDto entity) {
		return new ValidationErrors();
	}

	@Override
	protected void setEntityShareInfoAssociatedObject(SormasToSormasShareInfo sormasToSormasShareInfo, Event entity) {
		sormasToSormasShareInfo.setEvent(entity);
	}

	@Override
	protected void setAssociatedObjectShareInfoAssociatedObject(SormasToSormasShareInfo sormasToSormasShareInfo, Object t) {

	}
}
