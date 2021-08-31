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

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.EVENT_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.EVENT_SYNC_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventValidationGroupName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventFacade;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.sormastosormas.AbstractSormasToSormasInterface;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareInfoEvent;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless(name = "SormasToSormasEventFacade")
public class SormasToSormasEventFacadeEjb
	extends AbstractSormasToSormasInterface<Event, EventDto, SormasToSormasEventDto, SormasToSormasEventPreview, ProcessedEventData>
	implements SormasToSormasEventFacade {

	public static final String EVENT_REQUEST_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.EVENT_REQUEST_ENDPOINT;
	public static final String EVENT_REQUEST_REJECT_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.EVENT_REQUEST_REJECT_ENDPOINT;
	public static final String EVENT_REQUEST_GET_DATA_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.EVENT_REQUEST_GET_DATA_ENDPOINT;
	public static final String SAVE_SHARED_EVENTS_ENDPOINT = RESOURCE_PATH + EVENT_ENDPOINT;
	public static final String SYNC_SHARED_EVENTS_ENDPOINT = RESOURCE_PATH + EVENT_SYNC_ENDPOINT;
	public static final String EVENT_SHARES_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.EVENT_SHARES_ENDPOINT;

	@EJB
	private EventService eventService;
	@EJB
	private EventShareDataBuilder shareDataBuilder;
	@EJB
	private ReceivedEventProcessor receivedEventProcessor;
	@EJB
	private ProcessedEventDataPersister processedEventDataPersister;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	public SormasToSormasEventFacadeEjb() {
		super(
			EVENT_REQUEST_ENDPOINT,
			EVENT_REQUEST_REJECT_ENDPOINT,
			EVENT_REQUEST_GET_DATA_ENDPOINT,
			SAVE_SHARED_EVENTS_ENDPOINT,
			SYNC_SHARED_EVENTS_ENDPOINT,
			EVENT_SHARES_ENDPOINT,
			Captions.Event,
			ShareRequestDataType.EVENT,
			EventShareRequestData.class,
			EventSyncData.class);
	}

	@Override
	protected Class<SormasToSormasEventDto[]> getShareDataClass() {
		return SormasToSormasEventDto[].class;
	}

	@Override
	protected void validateEntitiesBeforeShare(List<Event> entities, boolean handOverOwnership) throws SormasToSormasException {
		List<ValidationErrors> validationErrors = new ArrayList<>();
		for (Event event : entities) {
			if (!eventService.isEventEditAllowed(event)) {
				validationErrors.add(new ValidationErrors(
						buildEventValidationGroupName(event),
						ValidationErrors
								.create(new ValidationErrorGroup(Captions.Event), new ValidationErrorMessage(Validations.sormasToSormasNotEditable))));
			}
		}

		if (validationErrors.size() > 0) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}
	}

	@Override
	protected ValidationErrors validateSharedEntity(EventDto entity) {
		return validateSharedUuid(entity.getUuid());
	}

	@Override
	protected ValidationErrors validateSharedPreview(SormasToSormasEventPreview preview) {
		return validateSharedUuid(preview.getUuid());
	}

	@Override
	protected void addEntityToShareInfo(SormasToSormasShareInfo shareInfo, List<Event> events) {
		shareInfo.getEvents().addAll(events.stream().map(e -> new ShareInfoEvent(shareInfo, e)).collect(Collectors.toList()));
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(String entityUuid, String receiverId) {
		return shareInfoService.getByEventAndOrganization(entityUuid, receiverId);
	}

	@Override
	protected BaseAdoService<Event> getEntityService() {
		return eventService;
	}

	@Override
	protected ShareDataBuilder<Event, SormasToSormasEventDto, SormasToSormasEventPreview> getShareDataBuilder() {
		return shareDataBuilder;
	}

	@Override
	protected ReceivedEventProcessor getReceivedDataProcessor() {
		return receivedEventProcessor;
	}

	@Override
	protected ProcessedDataPersister<ProcessedEventData> getProcessedDataPersister() {
		return processedEventDataPersister;
	}

	@Override
	protected List<EventDto> loadExistingEntities(List<String> uuids) {
		return eventFacade.getByUuids(uuids);
	}

	@Override
	protected void setShareRequestPreviewData(SormasToSormasShareRequestDto request, List<SormasToSormasEventPreview> previews) {
		request.setEvents(previews);
	}

	@Override
	protected List<SormasToSormasShareInfo> getEntityShares(Event event) {
		return event.getShareInfoEvents().stream().map(ShareInfoEvent::getShareInfo).collect(Collectors.toList());
	}

	private ValidationErrors validateSharedUuid(String uuid) {
		ValidationErrors errors = new ValidationErrors();

		if (eventFacade.exists(uuid)) {
			errors.add(new ValidationErrorGroup(Captions.Event), new ValidationErrorMessage(Validations.sormasToSormasEventExists));
		}

		return errors;
	}

	@Override
	protected List<String> getUuidsWithPendingOwnershipHandedOver(List<Event> entities) {
		return shareInfoService.getEventUuidsWithPendingOwnershipHandOver(entities);
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasEventFacadeEjbLocal extends SormasToSormasEventFacadeEjb {

	}
}
