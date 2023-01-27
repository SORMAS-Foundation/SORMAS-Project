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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.validation.Valid;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventFacade;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventJoins;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.AbstractSormasToSormasInterface;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SormasToSormasEventFacade")
public class SormasToSormasEventFacadeEjb extends AbstractSormasToSormasInterface<Event, EventDto, SormasToSormasEventDto>
	implements SormasToSormasEventFacade {

	public static final String EVENT_REQUEST_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.EVENT_REQUEST_ENDPOINT;
	public static final String EVENT_REQUEST_GET_DATA_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.EVENT_REQUEST_GET_DATA_ENDPOINT;
	public static final String SAVE_SHARED_EVENTS_ENDPOINT = RESOURCE_PATH + EVENT_ENDPOINT;
	public static final String SYNC_SHARED_EVENTS_ENDPOINT = RESOURCE_PATH + EVENT_SYNC_ENDPOINT;
	public static final String EVENT_SHARES_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.EVENT_SHARES_ENDPOINT;

	@EJB
	private EventService eventService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private SampleService sampleService;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private UserService userService;

	public SormasToSormasEventFacadeEjb() {
		super(
			EVENT_REQUEST_ENDPOINT,
			EVENT_REQUEST_GET_DATA_ENDPOINT,
			SAVE_SHARED_EVENTS_ENDPOINT,
			SYNC_SHARED_EVENTS_ENDPOINT,
			EVENT_SHARES_ENDPOINT,
			Captions.Event,
			ShareRequestDataType.EVENT);
	}

	@Override
	@Transactional(value = Transactional.TxType.REQUIRES_NEW,
		rollbackOn = {
			Exception.class })
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_SHARE)
	public void share(List<String> entityUuids, @Valid SormasToSormasOptionsDto options) throws SormasToSormasException {
		if (!userService.hasRight(UserRight.EVENT_EDIT)
			|| (options.isWithEventParticipants() && !userService.hasRight(UserRight.EVENTPARTICIPANT_EDIT))
			|| (options.isWithSamples() && !userService.hasRight(UserRight.SAMPLE_EDIT))
			|| (options.isWithImmunizations() && !userService.hasRight(UserRight.IMMUNIZATION_EDIT))) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		super.share(entityUuids, options);
	}

	@Override
	protected Class<SormasToSormasEventDto[]> getShareDataClass() {
		return SormasToSormasEventDto[].class;
	}

	@Override
	protected void validateEntitiesBeforeShareInner(
		Event event,
		boolean handOverOwnership,
		boolean isWithSamples,
		String targetOrganizationId,
		List<ValidationErrors> validationErrors) {
		// nothing to do besides the standard validation in super
	}

	@Override
	protected SormasToSormasShareInfo getByTypeAndOrganization(Event event, String targetOrganizationId) {
		return shareInfoService.getByEventAndOrganization(event.getUuid(), targetOrganizationId);
	}

	@Override
	protected ValidationErrorGroup buildEntityValidationGroupNameForAdo(Event event) {
		return buildEventValidationGroupName(event);
	}

	@Override
	protected Event extractFromShareInfo(SormasToSormasShareInfo shareInfo) {
		return shareInfo.getEvent();
	}

	@Override
	protected void validateShareRequestBeforeAccept(SormasToSormasShareRequestDto shareRequest) throws SormasToSormasException {

	}

	@Override
	protected List<SormasToSormasShareInfo> getOrCreateShareInfos(Event event, SormasToSormasOptionsDto options, User user, boolean forSync) {
		String organizationId = options.getOrganization().getId();
		SormasToSormasShareInfo eventShareInfo = event.getSormasToSormasShares()
			.stream()
			.filter(s -> s.getOrganizationId().equals(organizationId))
			.findFirst()
			.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, event, SormasToSormasShareInfo::setEvent, options));

		Stream<SormasToSormasShareInfo> eventParticipantShareInfos = Stream.empty();
		List<EventParticipant> eventParticipants = Collections.emptyList();
		if (options.isWithEventParticipants()) {
			eventParticipants = eventParticipantService.getAllActiveByEvent(event);
			eventParticipantShareInfos = eventParticipants.stream()
				.map(
					ep -> ep.getSormasToSormasShares()
						.stream()
						.filter(share -> share.getOrganizationId().equals(organizationId))
						.findFirst()
						.orElseGet(() -> {
							if (forSync) {
								// do not share new event participants on sync
								return ep.getSormasToSormasOriginInfo() != null
									&& ep.getSormasToSormasOriginInfo().getOrganizationId().equals(organizationId)
										? ShareInfoHelper.createShareInfo(organizationId, ep, SormasToSormasShareInfo::setEventParticipant, options)
										: null;
							} else {
								return ShareInfoHelper.createShareInfo(organizationId, ep, SormasToSormasShareInfo::setEventParticipant, options);
							}
						}))
				.filter(Objects::nonNull);
		}

		Stream<SormasToSormasShareInfo> sampleShareInfos = Stream.empty();
		Stream<SormasToSormasShareInfo> immunizationShareInfos = Stream.empty();
		if (!eventParticipants.isEmpty()) {
			if (options.isWithSamples()) {
				List<String> eventParticipantUuids = eventParticipants.stream().map(EventParticipant::getUuid).collect(Collectors.toList());
				sampleShareInfos = sampleService.getByEventParticipantUuids(eventParticipantUuids)
					.stream()
					.map(
						s -> s.getSormasToSormasShares()
							.stream()
							.filter(share -> share.getOrganizationId().equals(organizationId))
							.findFirst()
							.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, s, SormasToSormasShareInfo::setSample, options)));
			}

			if (options.isWithImmunizations()) {
				immunizationShareInfos = getAssociatedImmunizations(eventParticipants).stream()
					.map(
						i -> i.getSormasToSormasShares()
							.stream()
							.filter(share -> share.getOrganizationId().equals(organizationId))
							.findFirst()
							.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, i, SormasToSormasShareInfo::setImmunization, options)));
			}
		}

		return Stream.of(Stream.of(eventShareInfo), eventParticipantShareInfos, sampleShareInfos, immunizationShareInfos)
			.flatMap(Function.identity())
			.collect(Collectors.toList());
	}

	@Override
	protected AbstractCoreAdoService<Event, EventJoins> getEntityService() {
		return eventService;
	}

	@Override
	protected List<String> getUuidsWithPendingOwnershipHandedOver(List<Event> entities) {
		return shareInfoService.getEventUuidsWithPendingOwnershipHandOver(entities);
	}

	@Override
	protected String getShareInfoAssociatedObjectField() {
		return SormasToSormasShareInfo.EVENT;
	}

	private List<Immunization> getAssociatedImmunizations(List<EventParticipant> eventParticipants) {
		List<Long> personIds = eventParticipants.stream().map(ep -> ep.getPerson().getId()).collect(Collectors.toList());
		return immunizationService.getByPersonIds(personIds);
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasEventFacadeEjbLocal extends SormasToSormasEventFacadeEjb {

	}
}
