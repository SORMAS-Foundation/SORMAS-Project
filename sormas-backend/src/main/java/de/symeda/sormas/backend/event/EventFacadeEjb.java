/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.event;

import static java.util.Objects.isNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDetailedReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventExportDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventGroupsIndexDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasRuntimeException;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.externalsurveillancetool.ExternalSurveillanceToolGatewayFacadeEjb.ExternalSurveillanceToolGatewayFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.share.ExternalShareInfoCountAndLatestDate;
import de.symeda.sormas.backend.share.ExternalShareInfoService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.event.SormasToSormasEventFacadeEjb.SormasToSormasEventFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "EventFacade")
@RightsAllowed(UserRight._EVENT_VIEW)
public class EventFacadeEjb extends AbstractCoreFacadeEjb<Event, EventDto, EventIndexDto, EventReferenceDto, EventService, EventCriteria>
	implements EventFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private UserService userService;
	@EJB
	private EventGroupService eventGroupService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
	@EJB
	private CountryFacadeEjbLocal countryFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private ExternalSurveillanceToolGatewayFacadeEjbLocal externalSurveillanceToolFacade;
	@EJB
	private ExternalShareInfoService externalShareInfoService;
	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@EJB
	private SormasToSormasEventFacadeEjbLocal sormasToSormasEventFacade;
	@EJB
	private EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal eventParticipantFacade;
	@EJB
	private EventService eventService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private ExternalSurveillanceToolGatewayFacadeEjbLocal externalSurveillanceToolGatewayFacade;
	@Resource
	private ManagedScheduledExecutorService executorService;

	public EventFacadeEjb() {
	}

	@Inject
	public EventFacadeEjb(EventService service) {
		super(Event.class, EventDto.class, service);
	}

	public static EventReferenceDto toReferenceDto(Event entity) {

		if (entity == null) {
			return null;
		}

		return new EventReferenceDto(entity.getUuid(), getCaption(entity));
	}

	private static String getCaption(Event entity) {
		return EventReferenceDto.buildCaption(
			entity.getDisease(),
			entity.getDiseaseDetails(),
			entity.getEventStatus(),
			entity.getEventInvestigationStatus(),
			entity.getStartDate());
	}

	public static EventReferenceDto toDetailedReferenceDto(Event entity) {

		if (entity == null) {
			return null;
		}

		return new EventDetailedReferenceDto(
			entity.getUuid(),
			getCaption(entity),
			entity.getEventStatus(),
			entity.getEventTitle(),
			entity.getReportDateTime());
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids();
	}

	@Override
	public List<EventDto> getAllByCase(CaseDataDto caseDataDto) {
		return toDtos(service.getAllByCase(caseDataDto.getUuid()).stream());
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getDeletedUuidsSince(since);
	}

	@PermitAll
	public Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria) {

		return service.getEventCountByDisease(eventCriteria);
	}

	@Override
	public EventDto getEventByUuid(String uuid, boolean detailedReferences) {
		return (detailedReferences)
			? convertToDetailedReferenceDto(service.getByUuid(uuid), createPseudonymizer())
			: toPseudonymizedDto(service.getByUuid(uuid));
	}

	@Override
	public EventReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(service.getByUuid(uuid));
	}

	@Override
	public EventReferenceDto getReferenceByEventParticipant(String uuid) {
		return toReferenceDto(service.getEventReferenceByEventParticipant(uuid));
	}

	@Override
	@RightsAllowed({
		UserRight._EVENT_CREATE,
		UserRight._EVENT_EDIT })
	public EventDto save(@Valid @NotNull EventDto dto) {
		return save(dto, true, true);
	}

	@RightsAllowed({
		UserRight._EVENT_CREATE,
		UserRight._EVENT_EDIT })
	public EventDto save(@NotNull EventDto dto, boolean checkChangeDate, boolean internal) {

		Event existingEvent = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;
		FacadeHelper.checkCreateAndEditRights(existingEvent, userService, UserRight.EVENT_CREATE, UserRight.EVENT_EDIT);

		if (internal && existingEvent != null && !service.isEditAllowed(existingEvent)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorEventNotEditable));
		}

		EventDto existingDto = toDto(existingEvent);

		Pseudonymizer pseudonymizer = createPseudonymizer();
		restorePseudonymizedDto(dto, existingDto, existingEvent, pseudonymizer);

		validate(dto);
		Event event = fillOrBuildEntity(dto, existingEvent, checkChangeDate);
		service.ensurePersisted(event);

		onEventChange(toDto(event), internal);

		return toPseudonymizedDto(event, pseudonymizer);
	}

	@PermitAll
	public void onEventChange(EventDto event, boolean syncShares) {
		if (syncShares && sormasToSormasFacade.isFeatureConfigured()) {
			syncSharesAsync(new ShareTreeCriteria(event.getUuid()));
		}
	}

	@RightsAllowed(UserRight._EVENT_EDIT)
	public void syncSharesAsync(ShareTreeCriteria criteria) {
		executorService.schedule(() -> {
			sormasToSormasEventFacade.syncShares(criteria);
		}, 5, TimeUnit.SECONDS);
	}

	@Override
	@RightsAllowed(UserRight._EVENT_DELETE)
	public void delete(String eventUuid, DeletionDetails deletionDetails)
		throws ExternalSurveillanceToolRuntimeException, SormasToSormasRuntimeException {
		Event event = service.getByUuid(eventUuid);
		deleteEvent(event, deletionDetails);
	}

	private boolean isEventWithoutParticipants(Event event) {
		return eventParticipantService.getAllActiveByEvent(event).size() == 0;
	}

	private void deleteEvent(Event event, DeletionDetails deletionDetails)
		throws ExternalSurveillanceToolRuntimeException, SormasToSormasRuntimeException, AccessDeniedException {

		if (!eventService.inJurisdictionOrOwned(event)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageEventOutsideJurisdictionDeletionDenied));
		}

		try {
			sormasToSormasFacade.revokePendingShareRequests(event.getSormasToSormasShares(), true);
		} catch (SormasToSormasException e) {
			throw new SormasToSormasRuntimeException(e);
		}

		service.delete(event, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._EVENT_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<ProcessedEntity> processedEvents = new ArrayList<>();

		List<Event> eventsToBeDeleted = service.getByUuids(uuids);
		if (eventsToBeDeleted != null) {
			eventsToBeDeleted.forEach(eventToBeDeleted -> {
				if (!eventToBeDeleted.isDeleted() && isEventWithoutParticipants(eventToBeDeleted)) {
					try {
						deleteEvent(eventToBeDeleted, deletionDetails);
						processedEvents.add(new ProcessedEntity(eventToBeDeleted.getUuid(), ProcessedEntityStatus.SUCCESS));
					} catch (ExternalSurveillanceToolRuntimeException e) {
						processedEvents.add(new ProcessedEntity(eventToBeDeleted.getUuid(), ProcessedEntityStatus.EXTERNAL_SURVEILLANCE_FAILURE));
						logger.error(
							"The event with uuid {} could not be deleted due to a ExternalSurveillanceToolRuntimeException",
							eventToBeDeleted.getUuid(),
							e);
					} catch (SormasToSormasRuntimeException e) {
						processedEvents.add(new ProcessedEntity(eventToBeDeleted.getUuid(), ProcessedEntityStatus.SORMAS_TO_SORMAS_FAILURE));
						logger.error(
							"The event with uuid {} could not be deleted due to a SormasToSormasRuntimeException",
							eventToBeDeleted.getUuid(),
							e);
					} catch (AccessDeniedException e) {
						processedEvents.add(new ProcessedEntity(eventToBeDeleted.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
						logger.error("The event with uuid {} could not be deleted due to a AccessDeniedException", eventToBeDeleted.getUuid(), e);
					} catch (Exception e) {
						processedEvents.add(new ProcessedEntity(eventToBeDeleted.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
						logger.error("The event with uuid {} could not be deleted due to an Exception", eventToBeDeleted.getUuid(), e);
					}
				} else {
					processedEvents.add(new ProcessedEntity(eventToBeDeleted.getUuid(), ProcessedEntityStatus.NOT_ELIGIBLE));
				}
			});
		}
		return processedEvents;
	}

	@Override
	@RightsAllowed(UserRight._EVENT_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._EVENT_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		List<ProcessedEntity> processedEvents = new ArrayList<>();
		List<Event> eventsToBeRestored = eventService.getByUuids(uuids);

		if (eventsToBeRestored != null) {
			eventsToBeRestored.forEach(eventToBeRestored -> {
				try {
					restore(eventToBeRestored.getUuid());
					processedEvents.add(new ProcessedEntity(eventToBeRestored.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedEvents.add(new ProcessedEntity(eventToBeRestored.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The event with uuid {} could not be restored due to an Exception", eventToBeRestored.getUuid(), e);
				}
			});
		}
		return processedEvents;
	}

	@RightsAllowed({
		UserRight._EVENT_DELETE,
		UserRight._SYSTEM })
	public void deleteEventInExternalSurveillanceTool(Event event) throws ExternalSurveillanceToolException {

		if (externalSurveillanceToolGatewayFacade.isFeatureEnabled() && StringUtils.isNotBlank(event.getExternalId())) {
			List<Event> eventsWithSameExternalId = service.getByExternalId(event.getExternalId());
			if (eventsWithSameExternalId != null
				&& eventsWithSameExternalId.size() == 1
				&& event.getEventStatus() == EventStatus.CLUSTER
				&& externalSurveillanceToolFacade.isFeatureEnabled()
				&& externalShareInfoService.isEventShared(event.getId())) {
				externalSurveillanceToolGatewayFacade.deleteEventsInternal(Collections.singletonList(toDto(event)));
			}
		}
	}

	@Override
	public long count(EventCriteria eventCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Event> event = cq.from(Event.class);
		EventQueryContext queryContext = new EventQueryContext(cb, cq, event);

		Predicate filter = null;

		if (eventCriteria != null) {
			if (eventCriteria.getUserFilterIncluded()) {
				EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
				eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
				filter = service.createUserFilter(queryContext, eventUserFilterCriteria);
			}

			Predicate criteriaFilter = service.buildCriteriaFilter(eventCriteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(event));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<EventIndexDto> getIndexList(EventCriteria eventCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<Long> indexListIds = getIndexListIds(eventCriteria, first, max, sortProperties);
		List<EventIndexDto> indexList = new ArrayList<>();

		CriteriaBuilder cb = em.getCriteriaBuilder();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {

			CriteriaQuery<EventIndexDto> cq = cb.createQuery(EventIndexDto.class);
			Root<Event> event = cq.from(Event.class);

			EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

			EventJoins eventJoins = eventQueryContext.getJoins();

			Join<Event, Location> location = eventJoins.getLocation();
			Join<Location, Region> region = eventJoins.getRegion();
			Join<Location, District> district = eventJoins.getDistrict();
			Join<Location, Community> community = eventJoins.getCommunity();
			Join<Event, User> reportingUser = eventJoins.getReportingUser();
			Join<Event, User> responsibleUser = eventJoins.getResponsibleUser();

			cq.multiselect(
				event.get(Event.ID),
				event.get(Event.UUID),
				event.get(Event.EXTERNAL_ID),
				event.get(Event.EXTERNAL_TOKEN),
				event.get(Event.INTERNAL_TOKEN),
				event.get(Event.EVENT_STATUS),
				event.get(Event.RISK_LEVEL),
				event.get(Event.SPECIFIC_RISK),
				event.get(Event.EVENT_INVESTIGATION_STATUS),
				event.get(Event.EVENT_MANAGEMENT_STATUS),
				event.get(Event.DISEASE),
				event.get(Event.DISEASE_VARIANT),
				event.get(Event.DISEASE_DETAILS),
				event.get(Event.START_DATE),
				event.get(Event.END_DATE),
				event.get(Event.EVOLUTION_DATE),
				event.get(Event.EVENT_TITLE),
				region.get(Region.UUID),
				region.get(Region.NAME),
				district.get(District.UUID),
				district.get(District.NAME),
				community.get(Community.UUID),
				community.get(Community.NAME),
				location.get(Location.CITY),
				location.get(Location.STREET),
				location.get(Location.HOUSE_NUMBER),
				location.get(Location.ADDITIONAL_INFORMATION),
				event.get(Event.SRC_TYPE),
				event.get(Event.SRC_FIRST_NAME),
				event.get(Event.SRC_LAST_NAME),
				event.get(Event.SRC_TEL_NO),
				event.get(Event.SRC_MEDIA_WEBSITE),
				event.get(Event.SRC_MEDIA_NAME),
				event.get(Event.REPORT_DATE_TIME),
				reportingUser.get(User.UUID),
				reportingUser.get(User.FIRST_NAME),
				reportingUser.get(User.LAST_NAME),
				responsibleUser.get(User.UUID),
				responsibleUser.get(User.FIRST_NAME),
				responsibleUser.get(User.LAST_NAME),
				JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(eventQueryContext)),
				event.get(Event.CHANGE_DATE),
				event.get(Event.EVENT_IDENTIFICATION_SOURCE),
				event.get(Event.DELETION_REASON),
				event.get(Event.OTHER_DELETION_REASON));

			Predicate filter = event.get(Event.ID).in(batchedIds);

			if (eventCriteria != null) {
				if (eventCriteria.getUserFilterIncluded()) {
					EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
					eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
					filter = service.createUserFilter(eventQueryContext, eventUserFilterCriteria);
				}

				Predicate criteriaFilter = service.buildCriteriaFilter(eventCriteria, eventQueryContext);
				filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
			}

			if (filter != null) {
				cq.where(filter);
			}

			sortBy(sortProperties, eventQueryContext);
			cq.distinct(true);

			indexList.addAll(QueryHelper.getResultList(em, cq, null, null));
		});

		Map<String, Long> participantCounts = new HashMap<>();
		Map<String, Long> caseCounts = new HashMap<>();
		Map<String, Long> deathCounts = new HashMap<>();
		Map<String, Long> contactCounts = new HashMap<>();
		Map<String, Long> contactCountsSourceInEvent = new HashMap<>();
		Map<String, EventGroupsIndexDto> eventGroupsByEventId = new HashMap<>();
		Map<String, ExternalShareInfoCountAndLatestDate> survToolShareCountAndDates = new HashMap<>();

		if (CollectionUtils.isNotEmpty(indexList)) {
			List<String> eventUuids = indexList.stream().map(EventIndexDto::getUuid).collect(Collectors.toList());
			List<Long> eventIds = indexList.stream().map(EventIndexDto::getId).collect(Collectors.toList());

			// Participant, Case and Death Count
			List<Object[]> participantQueryList = new ArrayList<>();
			IterableHelper.executeBatched(eventUuids, ModelConstants.PARAMETER_LIMIT, batchedUuids -> {
				CriteriaQuery<Object[]> participantCQ = cb.createQuery(Object[].class);
				Root<EventParticipant> epRoot = participantCQ.from(EventParticipant.class);
				Join<EventParticipant, Case> caseJoin = epRoot.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);
				Predicate notDeleted = cb.isFalse(epRoot.get(EventParticipant.DELETED));
				Predicate isInIndexlist =
					CriteriaBuilderHelper.andInValues(batchedUuids, null, cb, epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));
				participantCQ.multiselect(
					epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID),
					cb.count(epRoot),
					cb.sum(cb.selectCase().when(cb.isNotNull(epRoot.get(EventParticipant.RESULTING_CASE)), 1).otherwise(0).as(Long.class)),
					cb.sum(cb.selectCase().when(cb.equal(caseJoin.get(Case.OUTCOME), CaseOutcome.DECEASED), 1).otherwise(0).as(Long.class)));
				participantCQ.where(notDeleted, isInIndexlist);
				participantCQ.groupBy(epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));

				participantQueryList.addAll(QueryHelper.getResultList(em, participantCQ, null, null));
			});

			if (participantQueryList != null) {
				participantQueryList.forEach(r -> {
					participantCounts.put((String) r[0], (Long) r[1]);
					caseCounts.put((String) r[0], (Long) r[2]);
					deathCounts.put((String) r[0], (Long) r[3]);
				});
			}

			// Contact Count (with and without sourcecase in event) using theta join
			List<Object[]> contactQueryList = new ArrayList<>();
			IterableHelper.executeBatched(eventUuids, ModelConstants.PARAMETER_LIMIT, batchedUuids -> {
				CriteriaQuery<Object[]> contactCQ = cb.createQuery(Object[].class);
				Root<EventParticipant> epRoot = contactCQ.from(EventParticipant.class);
				Root<Contact> contactRoot = contactCQ.from(Contact.class);
				Predicate participantPersonEqualsContactPerson = cb.equal(epRoot.get(EventParticipant.PERSON), contactRoot.get(Contact.PERSON));
				Predicate notDeleted = cb.isFalse(epRoot.get(EventParticipant.DELETED));
				Predicate contactNotDeleted = cb.isFalse(contactRoot.get(Contact.DELETED));
				Predicate isInIndexlist =
					CriteriaBuilderHelper.andInValues(batchedUuids, null, cb, epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));

				Subquery<EventParticipant> sourceCaseSubquery = contactCQ.subquery(EventParticipant.class);
				Root<EventParticipant> epr2 = sourceCaseSubquery.from(EventParticipant.class);
				sourceCaseSubquery.select(epr2);
				sourceCaseSubquery.where(
					cb.equal(epr2.get(EventParticipant.RESULTING_CASE), contactRoot.get(Contact.CAZE)),
					cb.equal(epr2.get(EventParticipant.EVENT), epRoot.get(EventParticipant.EVENT)));

				contactCQ.multiselect(
					epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID),
					cb.count(epRoot),
					cb.sum(cb.selectCase().when(cb.exists(sourceCaseSubquery), 1).otherwise(0).as(Long.class)));
				contactCQ.where(participantPersonEqualsContactPerson, notDeleted, contactNotDeleted, isInIndexlist);
				contactCQ.groupBy(epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));

				contactQueryList.addAll(em.createQuery(contactCQ).getResultList());
			});

			if (contactQueryList != null) {
				contactQueryList.forEach(r -> {
					contactCounts.put((String) r[0], ((Long) r[1]));
					contactCountsSourceInEvent.put((String) r[0], ((Long) r[2]));
				});
			}

			if (featureConfigurationFacade.isFeatureEnabled(FeatureType.EVENT_GROUPS)) {
				// Get latest EventGroup with EventGroup count
				List<Object[]> eventGroupQueryList = new ArrayList<>();
				IterableHelper.executeBatched(eventUuids, ModelConstants.PARAMETER_LIMIT, batchedUuids -> {
					CriteriaQuery<Object[]> latestEventCQ = cb.createQuery(Object[].class);
					Root<Event> eventRoot = latestEventCQ.from(Event.class);
					Join<Event, EventGroup> eventGroupJoin = eventRoot.join(Event.EVENT_GROUPS, JoinType.INNER);
					Predicate isInIndexlist = CriteriaBuilderHelper.andInValues(batchedUuids, null, cb, eventRoot.get(Event.UUID));
					latestEventCQ.where(isInIndexlist);
					latestEventCQ.multiselect(
						eventRoot.get(Event.UUID),
						CriteriaBuilderHelper.windowFirstValueDesc(
							cb,
							eventGroupJoin.get(EventGroup.UUID),
							eventRoot.get(Event.UUID),
							eventGroupJoin.get(EventGroup.CREATION_DATE)),
						CriteriaBuilderHelper.windowFirstValueDesc(
							cb,
							eventGroupJoin.get(EventGroup.NAME),
							eventRoot.get(Event.UUID),
							eventGroupJoin.get(EventGroup.CREATION_DATE)),
						CriteriaBuilderHelper.windowCount(cb, eventGroupJoin.get(EventGroup.ID), eventRoot.get(Event.UUID)));

					eventGroupQueryList.addAll(em.createQuery(latestEventCQ).getResultList());
				});

				if (eventGroupQueryList != null) {
					eventGroupQueryList.forEach(r -> {
						EventGroupReferenceDto eventGroupReference = new EventGroupReferenceDto((String) r[1], (String) r[2]);
						EventGroupsIndexDto eventGroups = new EventGroupsIndexDto(eventGroupReference, ((Number) r[3]).longValue());
						eventGroupsByEventId.put((String) r[0], eventGroups);
					});
				}
			}

			if (externalSurveillanceToolFacade.isFeatureEnabled()) {
				survToolShareCountAndDates = externalShareInfoService.getEventShareCountAndLatestDate(eventIds)
					.stream()
					.collect(Collectors.toMap(ExternalShareInfoCountAndLatestDate::getAssociatedObjectUuid, Function.identity()));
			}
		}

		if (indexList != null) {
			for (EventIndexDto eventDto : indexList) {
				Optional.ofNullable(participantCounts.get(eventDto.getUuid())).ifPresent(eventDto::setParticipantCount);
				Optional.ofNullable(caseCounts.get(eventDto.getUuid())).ifPresent(eventDto::setCaseCount);
				Optional.ofNullable(deathCounts.get(eventDto.getUuid())).ifPresent(eventDto::setDeathCount);
				Optional.ofNullable(contactCounts.get(eventDto.getUuid())).ifPresent(eventDto::setContactCount);
				Optional.ofNullable(contactCountsSourceInEvent.get(eventDto.getUuid())).ifPresent(eventDto::setContactCountSourceInEvent);
				Optional.ofNullable(eventGroupsByEventId.get(eventDto.getUuid())).ifPresent(eventDto::setEventGroups);
				Optional.ofNullable(survToolShareCountAndDates.get(eventDto.getUuid())).ifPresent((c) -> {
					eventDto.setSurveillanceToolStatus(c.getLatestStatus());
					eventDto.setSurveillanceToolLastShareDate(c.getLatestDate());
					eventDto.setSurveillanceToolShareCount(c.getCount());
				});
			}
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(EventIndexDto.class, indexList, EventIndexDto::getInJurisdictionOrOwned, (c, isInJurisdiction) -> {
		});

		return indexList;
	}

	private List<Long> getIndexListIds(EventCriteria eventCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Event> event = cq.from(Event.class);

		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(event.get(Person.ID));
		selections.addAll(sortBy(sortProperties, eventQueryContext));

		cq.multiselect(selections);

		Predicate filter = null;

		if (eventCriteria != null) {
			if (eventCriteria.getUserFilterIncluded()) {
				EventUserFilterCriteria eventUserFilterCriteria = new EventUserFilterCriteria();
				eventUserFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
				filter = service.createUserFilter(eventQueryContext, eventUserFilterCriteria);
			}

			Predicate criteriaFilter = service.buildCriteriaFilter(eventCriteria, eventQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<Tuple> events = QueryHelper.getResultList(em, cq, first, max);
		return events.stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, EventQueryContext eventQueryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = eventQueryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = eventQueryContext.getQuery();

		if (sortProperties != null && !sortProperties.isEmpty()) {
			EventJoins eventJoins = eventQueryContext.getJoins();
			Join<Event, Location> location = eventJoins.getLocation();
			Join<Location, Region> region = eventJoins.getRegion();
			Join<Location, District> district = eventJoins.getDistrict();
			Join<Location, Community> community = eventJoins.getCommunity();
			Join<Event, User> reportingUser = eventJoins.getReportingUser();
			Join<Event, User> responsibleUser = eventJoins.getResponsibleUser();

			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EventIndexDto.UUID:
				case EventIndexDto.EXTERNAL_ID:
				case EventIndexDto.EXTERNAL_TOKEN:
				case EventIndexDto.INTERNAL_TOKEN:
				case EventIndexDto.EVENT_STATUS:
				case EventIndexDto.RISK_LEVEL:
				case EventIndexDto.SPECIFIC_RISK:
				case EventIndexDto.EVENT_INVESTIGATION_STATUS:
				case EventIndexDto.EVENT_MANAGEMENT_STATUS:
				case EventIndexDto.DISEASE:
				case EventIndexDto.DISEASE_VARIANT:
				case EventIndexDto.DISEASE_DETAILS:
				case EventIndexDto.START_DATE:
				case EventIndexDto.EVOLUTION_DATE:
				case EventIndexDto.EVENT_TITLE:
				case EventIndexDto.SRC_FIRST_NAME:
				case EventIndexDto.SRC_LAST_NAME:
				case EventIndexDto.SRC_TEL_NO:
				case EventIndexDto.SRC_TYPE:
				case EventIndexDto.REPORT_DATE_TIME:
				case EventIndexDto.EVENT_IDENTIFICATION_SOURCE:
					expression = eventQueryContext.getRoot().get(sortProperty.propertyName);
					break;
				case EventIndexDto.EVENT_LOCATION:
					expression = region.get(Region.NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = district.get(District.NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = community.get(Community.NAME);
					break;
				case EventIndexDto.REGION:
					expression = region.get(Region.NAME);
					break;
				case EventIndexDto.DISTRICT:
					expression = district.get(District.NAME);
					break;
				case EventIndexDto.COMMUNITY:
					expression = community.get(Community.NAME);
					break;
				case EventIndexDto.ADDRESS:
					expression = location.get(Location.CITY);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = location.get(Location.STREET);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = location.get(Location.HOUSE_NUMBER);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = location.get(Location.ADDITIONAL_INFORMATION);
					break;
				case EventIndexDto.REPORTING_USER:
					expression = reportingUser.get(User.FIRST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = reportingUser.get(User.LAST_NAME);
					break;
				case EventIndexDto.RESPONSIBLE_USER:
					expression = responsibleUser.get(User.FIRST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = responsibleUser.get(User.LAST_NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Path<Object> changeDate = eventQueryContext.getRoot().get(Event.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	@Override
	public Page<EventIndexDto> getIndexPage(EventCriteria eventCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<EventIndexDto> eventIndexList = getIndexList(eventCriteria, offset, size, sortProperties);
		long totalElementCount = count(eventCriteria);
		return new Page<>(eventIndexList, offset, size, totalElementCount);
	}

	@Override
	@RightsAllowed(UserRight._EVENT_EXPORT)
	public List<EventExportDto> getExportList(EventCriteria eventCriteria, Collection<String> selectedRows, Integer first, Integer max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventExportDto> cq = cb.createQuery(EventExportDto.class);
		Root<Event> event = cq.from(Event.class);
		EventQueryContext eventQueryContext = new EventQueryContext(cb, cq, event);
		EventJoins eventJoins = eventQueryContext.getJoins();
		Join<Event, Location> location = eventJoins.getLocation();
		Join<Location, Region> region = eventJoins.getRegion();
		Join<Location, District> district = eventJoins.getDistrict();
		Join<Location, Community> community = eventJoins.getCommunity();
		Join<Event, User> reportingUser = eventJoins.getReportingUser();
		Join<Event, User> responsibleUser = eventJoins.getResponsibleUser();

		cq.multiselect(
			event.get(Event.UUID),
			event.get(Event.EXTERNAL_ID),
			event.get(Event.EXTERNAL_TOKEN),
			event.get(Event.INTERNAL_TOKEN),
			event.get(Event.EVENT_STATUS),
			event.get(Event.RISK_LEVEL),
			event.get(Event.SPECIFIC_RISK),
			event.get(Event.EVENT_INVESTIGATION_STATUS),
			event.get(Event.EVENT_INVESTIGATION_START_DATE),
			event.get(Event.EVENT_INVESTIGATION_END_DATE),
			event.get(Event.DISEASE),
			event.get(Event.DISEASE_VARIANT),
			event.get(Event.DISEASE_DETAILS),
			event.get(Event.DISEASE_VARIANT_DETAILS),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			event.get(Event.EVOLUTION_DATE),
			event.get(Event.EVOLUTION_COMMENT),
			event.get(Event.EVENT_TITLE),
			event.get(Event.EVENT_DESC),
			event.get(Event.DISEASE_TRANSMISSION_MODE),
			event.get(Event.NOSOCOMIAL),
			event.get(Event.TRANSREGIONAL_OUTBREAK),
			event.get(Event.MEANS_OF_TRANSPORT),
			event.get(Event.MEANS_OF_TRANSPORT_DETAILS),
			region.get(Region.UUID),
			region.get(Region.NAME),
			district.get(District.UUID),
			district.get(District.NAME),
			community.get(Community.UUID),
			community.get(Community.NAME),
			location.get(Location.CITY),
			location.get(Location.STREET),
			location.get(Location.HOUSE_NUMBER),
			location.get(Location.ADDITIONAL_INFORMATION),
			event.get(Event.SRC_TYPE),
			event.get(Event.SRC_INSTITUTIONAL_PARTNER_TYPE),
			event.get(Event.SRC_INSTITUTIONAL_PARTNER_TYPE_DETAILS),
			event.get(Event.SRC_FIRST_NAME),
			event.get(Event.SRC_LAST_NAME),
			event.get(Event.SRC_TEL_NO),
			event.get(Event.SRC_EMAIL),
			event.get(Event.SRC_MEDIA_WEBSITE),
			event.get(Event.SRC_MEDIA_NAME),
			event.get(Event.SRC_MEDIA_DETAILS),
			event.get(Event.REPORT_DATE_TIME),
			reportingUser.get(User.UUID),
			reportingUser.get(User.FIRST_NAME),
			reportingUser.get(User.LAST_NAME),
			responsibleUser.get(User.UUID),
			responsibleUser.get(User.FIRST_NAME),
			responsibleUser.get(User.LAST_NAME),
			JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(eventQueryContext)),
			event.get(Event.EVENT_MANAGEMENT_STATUS),
			event.get(Event.EVENT_IDENTIFICATION_SOURCE));

		cq.distinct(true);

		Predicate filter = service.createUserFilter(eventQueryContext);

		if (eventCriteria != null) {
			Predicate criteriaFilter = service.buildCriteriaFilter(eventCriteria, eventQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (CollectionUtils.isNotEmpty(selectedRows)) {
			filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, event.get(Event.UUID));
		}

		cq.where(filter);
		cq.orderBy(cb.desc(event.get(Event.REPORT_DATE_TIME)));

		List<EventExportDto> exportList = QueryHelper.getResultList(em, cq, first, max);

		Map<String, Long> participantCounts = new HashMap<>();
		Map<String, Long> caseCounts = new HashMap<>();
		Map<String, Long> deathCounts = new HashMap<>();
		Map<String, Long> contactCounts = new HashMap<>();
		Map<String, Long> contactCountsSourceInEvent = new HashMap<>();
		Map<String, EventGroupReferenceDto> latestEventGroupByEventId = new HashMap<>();
		Map<String, Long> eventGroupCountByEventId = new HashMap<>();
		if (exportList != null && !exportList.isEmpty()) {
			List<Object[]> objectQueryList = null;
			List<String> eventUuids = exportList.stream().map(EventExportDto::getUuid).collect(Collectors.toList());

			// Participant, Case and Death Count
			CriteriaQuery<Object[]> participantCQ = cb.createQuery(Object[].class);
			Root<EventParticipant> epRoot = participantCQ.from(EventParticipant.class);
			Join<EventParticipant, Case> caseJoin = epRoot.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);
			Predicate notDeleted = cb.isFalse(epRoot.get(EventParticipant.DELETED));
			Predicate isInExportlist =
				CriteriaBuilderHelper.andInValues(eventUuids, null, cb, epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));
			participantCQ.multiselect(
				epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID),
				cb.count(epRoot),
				cb.sum(cb.selectCase().when(cb.isNotNull(epRoot.get(EventParticipant.RESULTING_CASE)), 1).otherwise(0).as(Long.class)),
				cb.sum(cb.selectCase().when(cb.equal(caseJoin.get(Case.OUTCOME), CaseOutcome.DECEASED), 1).otherwise(0).as(Long.class)));
			participantCQ.where(notDeleted, isInExportlist);
			participantCQ.groupBy(epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));

			objectQueryList = em.createQuery(participantCQ).getResultList();

			if (objectQueryList != null) {
				objectQueryList.forEach(r -> {
					participantCounts.put((String) r[0], (Long) r[1]);
					caseCounts.put((String) r[0], (Long) r[2]);
					deathCounts.put((String) r[0], (Long) r[3]);
				});
			}

			// Contact Count (with and without sourcecase in event) using theta join
			CriteriaQuery<Object[]> contactCQ = cb.createQuery(Object[].class);
			epRoot = contactCQ.from(EventParticipant.class);
			Root<Contact> contactRoot = contactCQ.from(Contact.class);
			Predicate participantPersonEqualsContactPerson = cb.equal(epRoot.get(EventParticipant.PERSON), contactRoot.get(Contact.PERSON));
			notDeleted = cb.isFalse(epRoot.get(EventParticipant.DELETED));
			Predicate contactNotDeleted = cb.isFalse(contactRoot.get(Contact.DELETED));
			isInExportlist =
				CriteriaBuilderHelper.andInValues(eventUuids, null, cb, epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));

			Subquery<EventParticipant> sourceCaseSubquery = contactCQ.subquery(EventParticipant.class);
			Root<EventParticipant> epr2 = sourceCaseSubquery.from(EventParticipant.class);
			sourceCaseSubquery.select(epr2);
			sourceCaseSubquery.where(
				cb.equal(epr2.get(EventParticipant.RESULTING_CASE), contactRoot.get(Contact.CAZE)),
				cb.equal(epr2.get(EventParticipant.EVENT), epRoot.get(EventParticipant.EVENT)));

			contactCQ.multiselect(
				epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID),
				cb.count(epRoot),
				cb.sum(cb.selectCase().when(cb.exists(sourceCaseSubquery), 1).otherwise(0).as(Long.class)));
			contactCQ.where(participantPersonEqualsContactPerson, notDeleted, contactNotDeleted, isInExportlist);
			contactCQ.groupBy(epRoot.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID));

			objectQueryList = em.createQuery(contactCQ).getResultList();
			if (objectQueryList != null) {
				objectQueryList.forEach(r -> {
					contactCounts.put((String) r[0], ((Long) r[1]));
					contactCountsSourceInEvent.put((String) r[0], ((Long) r[2]));
				});
			}

			if (featureConfigurationFacade.isFeatureEnabled(FeatureType.EVENT_GROUPS)) {
				// Get latest EventGroup with EventGroup count
				CriteriaQuery<Object[]> latestEventCQ = cb.createQuery(Object[].class);
				Root<Event> eventRoot = latestEventCQ.from(Event.class);
				Join<Event, EventGroup> eventGroupJoin = eventRoot.join(Event.EVENT_GROUPS, JoinType.INNER);
				isInExportlist = CriteriaBuilderHelper.andInValues(eventUuids, null, cb, eventRoot.get(Event.UUID));
				latestEventCQ.where(notDeleted, isInExportlist);
				latestEventCQ.multiselect(
					eventRoot.get(Event.UUID),
					CriteriaBuilderHelper.windowFirstValueDesc(
						cb,
						eventGroupJoin.get(EventGroup.UUID),
						eventRoot.get(Event.UUID),
						eventGroupJoin.get(EventGroup.CREATION_DATE)),
					CriteriaBuilderHelper.windowFirstValueDesc(
						cb,
						eventGroupJoin.get(EventGroup.NAME),
						eventRoot.get(Event.UUID),
						eventGroupJoin.get(EventGroup.CREATION_DATE)),
					CriteriaBuilderHelper.windowCount(cb, eventGroupJoin.get(EventGroup.ID), eventRoot.get(Event.UUID)));

				objectQueryList = em.createQuery(latestEventCQ).getResultList();

				if (objectQueryList != null) {
					objectQueryList.forEach(r -> {
						EventGroupReferenceDto eventGroupReference = new EventGroupReferenceDto((String) r[1], (String) r[2]);
						latestEventGroupByEventId.put((String) r[0], eventGroupReference);
						eventGroupCountByEventId.put((String) r[0], ((Number) r[3]).longValue());
					});
				}
			}
		}

		if (exportList != null) {
			for (EventExportDto exportDto : exportList) {
				Optional.ofNullable(participantCounts.get(exportDto.getUuid())).ifPresent(exportDto::setParticipantCount);
				Optional.ofNullable(caseCounts.get(exportDto.getUuid())).ifPresent(exportDto::setCaseCount);
				Optional.ofNullable(deathCounts.get(exportDto.getUuid())).ifPresent(exportDto::setDeathCount);
				Optional.ofNullable(contactCounts.get(exportDto.getUuid())).ifPresent(exportDto::setContactCount);
				Optional.ofNullable(contactCountsSourceInEvent.get(exportDto.getUuid())).ifPresent(exportDto::setContactCountSourceInEvent);
				Optional.ofNullable(latestEventGroupByEventId.get(exportDto.getUuid())).ifPresent(exportDto::setLatestEventGroup);
				Optional.ofNullable(eventGroupCountByEventId.get(exportDto.getUuid())).ifPresent(exportDto::setEventGroupCount);
			}
		}

		return exportList;
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getArchivedUuidsSince(since);
	}

	@Override
	@RightsAllowed(UserRight._EVENT_ARCHIVE)
	public ProcessedEntity archive(String eventUuid, Date endOfProcessingDate) {
		ProcessedEntity processedEntity = super.archive(eventUuid, endOfProcessingDate);
		List<String> eventParticipantList = eventParticipantService.getAllUuidsByEventUuids(Collections.singletonList(eventUuid));
		eventParticipantService.archive(eventParticipantList);

		return processedEntity;
	}

	@Override
	@RightsAllowed(UserRight._EVENT_ARCHIVE)
	public List<ProcessedEntity> archive(List<String> eventUuids) {
		List<ProcessedEntity> processedEntities = super.archive(eventUuids);

		List<String> eventParticipantList = eventParticipantService.getAllUuidsByEventUuids(eventUuids);
		eventParticipantService.archive(eventParticipantList);
		return processedEntities;
	}

	@Override
	@RightsAllowed(UserRight._EVENT_ARCHIVE)
	public ProcessedEntity dearchive(String entityUuid, String dearchiveReason) {
		ProcessedEntity processedEntity = dearchive(Collections.singletonList(entityUuid), dearchiveReason).get(0);

		return processedEntity;
	}

	@Override
	@RightsAllowed(UserRight._EVENT_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> eventUuids, String dearchiveReason) {
		List<ProcessedEntity> processedEntities = super.dearchive(eventUuids, dearchiveReason);

		List<String> eventParticipantList = eventParticipantService.getAllUuidsByEventUuids(eventUuids);
		eventParticipantService.dearchive(eventParticipantList, dearchiveReason);

		return processedEntities;
	}

	@Override
	public Set<String> getAllSubordinateEventUuids(String eventUuid) {

		Set<String> uuids = new HashSet<>();
		Event superordinateEvent = service.getByUuid(eventUuid);
		addAllSubordinateEventsToSet(superordinateEvent, uuids);

		return uuids;
	}

	private void addAllSubordinateEventsToSet(Event event, Set<String> uuids) {

		uuids.addAll(event.getSubordinateEvents().stream().map(AbstractDomainObject::getUuid).collect(Collectors.toSet()));
		event.getSubordinateEvents().forEach(e -> addAllSubordinateEventsToSet(e, uuids));
	}

	@Override
	public Set<String> getAllSuperordinateEventUuids(String eventUuid) {

		Set<String> uuids = new HashSet<>();
		Event event = service.getByUuid(eventUuid);
		addSuperordinateEventToSet(event, uuids);

		return uuids;
	}

	@Override
	public Set<String> getAllEventUuidsByEventGroupUuid(String eventGroupUuid) {
		EventGroup eventGroup = eventGroupService.getByUuid(eventGroupUuid);
		if (eventGroup == null) {
			return Collections.emptySet();
		}

		return eventGroup.getEvents().stream().map(Event::getUuid).collect(Collectors.toSet());
	}

	@Override
	public List<String> getEventUuidsWithOwnershipHandedOver(List<String> eventUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> eventRoot = cq.from(Event.class);
		Join<Event, SormasToSormasShareInfo> sormasToSormasJoin = eventRoot.join(Event.SORMAS_TO_SORMAS_SHARES, JoinType.LEFT);

		cq.select(eventRoot.get(Event.UUID));
		cq.where(cb.and(eventRoot.get(Event.UUID).in(eventUuids), cb.isTrue(sormasToSormasJoin.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER))));
		cq.orderBy(cb.asc(eventRoot.get(AbstractDomainObject.CREATION_DATE)));

		return QueryHelper.getResultList(em, cq, null, null);
	}

	@Override
	public void validate(@Valid EventDto event) throws ValidationRuntimeException {

		LocationDto location = event.getEventLocation();
		CountryReferenceDto locationCountry = location.getCountry();
		CountryReferenceDto serverCountry = countryFacade.getServerCountry();
		boolean regionAndDistrictRequired = serverCountry == null
			? locationCountry == null
			: locationCountry == null || serverCountry.getIsoCode().equalsIgnoreCase(locationCountry.getIsoCode());

		if (location.getRegion() == null && regionAndDistrictRequired) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
		if (location.getDistrict() == null && regionAndDistrictRequired) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}

		if (event.getReportingUser() == null && !event.isPseudonymized()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
		}

		// Check whether there are any infrastructure errors
		if (location.getDistrict() != null && !districtFacade.getByUuid(location.getDistrict().getUuid()).getRegion().equals(location.getRegion())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noDistrictInRegion));
		}
		if (location.getCommunity() != null
			&& !communityFacade.getByUuid(location.getCommunity().getUuid()).getDistrict().equals(location.getDistrict())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noCommunityInDistrict));
		}
		if (location.getFacility() != null) {
			FacilityDto facility = facilityFacade.getByUuid(location.getFacility().getUuid());

			if (location.getFacilityType() == null && !FacilityDto.NONE_FACILITY_UUID.equals(location.getFacility().getUuid())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityType));
			}
			if (location.getFacilityType() != null
				&& !FacilityDto.OTHER_FACILITY_UUID.equals(location.getFacility().getUuid())
				&& !location.getFacilityType().equals(facility.getType())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityType));
			}
			if (location.getCommunity() == null && facility.getDistrict() != null && !facility.getDistrict().equals(location.getDistrict())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInDistrict));
			}
			if (location.getCommunity() != null && facility.getCommunity() != null && !location.getCommunity().equals(facility.getCommunity())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInCommunity));
			}
			if (facility.getRegion() != null && !location.getRegion().equals(facility.getRegion())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noFacilityInRegion));
			}
		}
	}

	private void addSuperordinateEventToSet(Event event, Set<String> uuids) {

		if (event.getSuperordinateEvent() != null) {
			uuids.add(event.getSuperordinateEvent().getUuid());
			addSuperordinateEventToSet(event.getSuperordinateEvent(), uuids);
		}
	}

	@Override
	public EventDto toDto(Event source) {
		return toEventDto(source);
	}

	public static EventDto toEventDto(Event source) {

		if (source == null) {
			return null;
		}
		EventDto target = new EventDto();
		DtoHelper.fillDto(target, source);

		target.setEventStatus(source.getEventStatus());
		target.setRiskLevel(source.getRiskLevel());
		target.setSpecificRisk(source.getSpecificRisk());
		target.setEventInvestigationStatus(source.getEventInvestigationStatus());
		target.setEventInvestigationStartDate(source.getEventInvestigationStartDate());
		target.setEventInvestigationEndDate(source.getEventInvestigationEndDate());
		target.setExternalId(source.getExternalId());
		target.setExternalToken(source.getExternalToken());
		target.setEventTitle(source.getEventTitle());
		target.setEventDesc(source.getEventDesc());
		target.setNosocomial(source.getNosocomial());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setEvolutionDate(source.getEvolutionDate());
		target.setEvolutionComment(source.getEvolutionComment());
		target.setEventLocation(LocationFacadeEjb.toDto(source.getEventLocation()));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setTravelDate(source.getTravelDate());
		target.setWorkEnvironment(source.getWorkEnvironment());
		target.setSrcType(source.getSrcType());
		target.setSrcInstitutionalPartnerType(source.getSrcInstitutionalPartnerType());
		target.setSrcInstitutionalPartnerTypeDetails(source.getSrcInstitutionalPartnerTypeDetails());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setSrcMediaWebsite(source.getSrcMediaWebsite());
		target.setSrcMediaName(source.getSrcMediaName());
		target.setSrcMediaDetails(source.getSrcMediaDetails());
		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
		target.setResponsibleUser(UserFacadeEjb.toReferenceDto(source.getResponsibleUser()));
		target.setTypeOfPlaceText(source.getTypeOfPlaceText());
		target.setTransregionalOutbreak(source.getTransregionalOutbreak());
		target.setDiseaseTransmissionMode(source.getDiseaseTransmissionMode());
		target.setSuperordinateEvent(EventFacadeEjb.toReferenceDto(source.getSuperordinateEvent()));
		target.setEventManagementStatus(source.getEventManagementStatus());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setInfectionPathCertainty(source.getInfectionPathCertainty());
		target.setHumanTransmissionMode(source.getHumanTransmissionMode());
		target.setParenteralTransmissionMode(source.getParenteralTransmissionMode());
		target.setMedicallyAssociatedTransmissionMode(source.getMedicallyAssociatedTransmissionMode());

		target.setEpidemiologicalEvidence(source.getEpidemiologicalEvidence());
		target.setEpidemiologicalEvidenceDetails(source.getEpidemiologicalEvidenceDetails());
		target.setLaboratoryDiagnosticEvidence(source.getLaboratoryDiagnosticEvidence());
		target.setLaboratoryDiagnosticEvidenceDetails(source.getLaboratoryDiagnosticEvidenceDetails());

		target.setInternalToken(source.getInternalToken());

		target.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.getSormasToSormasShares().stream().anyMatch(ShareInfoHelper::isOwnerShipHandedOver));

		target.setEventIdentificationSource(source.getEventIdentificationSource());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected EventReferenceDto toRefDto(Event event) {
		return toReferenceDto(event);
	}

	public EventDto convertToDetailedReferenceDto(Event source, Pseudonymizer pseudonymizer) {

		EventDto eventDto = toDto(source);
		eventDto.setSuperordinateEvent(EventFacadeEjb.toDetailedReferenceDto(source.getSuperordinateEvent()));
		pseudonymizeDto(source, eventDto, pseudonymizer, service.inJurisdictionOrOwned(source));

		return eventDto;
	}

	@Override
	protected void pseudonymizeDto(Event event, EventDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		if (dto != null) {
			pseudonymizer.pseudonymizeDto(EventDto.class, dto, inJurisdiction, e -> {
				pseudonymizer.pseudonymizeUser(event.getReportingUser(), userService.getCurrentUser(), dto::setReportingUser);
			});
		}
	}

	protected void restorePseudonymizedDto(EventDto dto, EventDto existingDto, Event event, Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(event);
			pseudonymizer.restorePseudonymizedValues(EventDto.class, dto, existingDto, inJurisdiction);
			pseudonymizer.restoreUser(event.getReportingUser(), userService.getCurrentUser(), dto, dto::setReportingUser);
		}
	}

	public Event fillOrBuildEntity(@NotNull EventDto source, Event target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);
		target = DtoHelper.fillOrBuildEntity(source, target, Event::new, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getEventLocation(), source.getEventLocation());
		}

		target.setEventStatus(source.getEventStatus());
		target.setRiskLevel(source.getRiskLevel());
		target.setSpecificRisk(source.getSpecificRisk());
		target.setEventInvestigationStatus(source.getEventInvestigationStatus());
		target.setEventInvestigationStartDate(source.getEventInvestigationStartDate());
		target.setEventInvestigationEndDate(source.getEventInvestigationEndDate());
		target.setExternalId(source.getExternalId());
		target.setExternalToken(source.getExternalToken());
		target.setEventTitle(source.getEventTitle());
		target.setEventDesc(source.getEventDesc());
		target.setNosocomial(source.getNosocomial());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setEvolutionDate(source.getEvolutionDate());
		target.setEvolutionComment(source.getEvolutionComment());
		target.setEventLocation(locationFacade.fillOrBuildEntity(source.getEventLocation(), target.getEventLocation(), checkChangeDate));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setTravelDate(source.getTravelDate());
		target.setWorkEnvironment(source.getWorkEnvironment());
		target.setSrcType(source.getSrcType());
		target.setSrcInstitutionalPartnerType(source.getSrcInstitutionalPartnerType());
		target.setSrcInstitutionalPartnerTypeDetails(source.getSrcInstitutionalPartnerTypeDetails());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setSrcMediaWebsite(source.getSrcMediaWebsite());
		target.setSrcMediaName(source.getSrcMediaName());
		target.setSrcMediaDetails(source.getSrcMediaDetails());
		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
		target.setResponsibleUser(userService.getByReferenceDto(source.getResponsibleUser()));
		target.setTypeOfPlaceText(source.getTypeOfPlaceText());
		target.setTransregionalOutbreak(source.getTransregionalOutbreak());
		target.setDiseaseTransmissionMode(source.getDiseaseTransmissionMode());
		target.setSuperordinateEvent(service.getByReferenceDto(source.getSuperordinateEvent()));
		target.setEventManagementStatus(source.getEventManagementStatus());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setInfectionPathCertainty(source.getInfectionPathCertainty());
		target.setHumanTransmissionMode(source.getHumanTransmissionMode());
		target.setParenteralTransmissionMode(source.getParenteralTransmissionMode());
		target.setMedicallyAssociatedTransmissionMode(source.getMedicallyAssociatedTransmissionMode());

		target.setEpidemiologicalEvidence(source.getEpidemiologicalEvidence());
		target.setEpidemiologicalEvidenceDetails(source.getEpidemiologicalEvidenceDetails());
		target.setLaboratoryDiagnosticEvidence(source.getLaboratoryDiagnosticEvidence());
		target.setLaboratoryDiagnosticEvidenceDetails(source.getLaboratoryDiagnosticEvidenceDetails());

		target.setInternalToken(source.getInternalToken());

		target.setEventIdentificationSource(source.getEventIdentificationSource());

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoService.getByUuid(source.getSormasToSormasOriginInfo().getUuid()));
		}

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	/**
	 * Archives all events that have not been changed for a defined amount of days
	 * 
	 * @param daysAfterEventGetsArchived
	 *            defines the amount of days
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._SYSTEM)
	public void archiveAllArchivableEvents(int daysAfterEventGetsArchived) {

		archiveAllArchivableEvents(daysAfterEventGetsArchived, LocalDate.now());
	}

	@RightsAllowed(UserRight._SYSTEM)
	void archiveAllArchivableEvents(int daysAfterEventGetsArchived, @NotNull LocalDate referenceDate) {

		LocalDate notChangedSince = referenceDate.minusDays(daysAfterEventGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> from = cq.from(Event.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(
			cb.equal(from.get(Event.ARCHIVED), false),
			cb.equal(from.get(Event.DELETED), false),
			cb.not(service.createChangeDateFilter(cb, from, notChangedTimestamp)));
		cq.select(from.get(Event.UUID)).distinct(true);
		List<String> eventUuids = em.createQuery(cq).getResultList();

		if (!eventUuids.isEmpty()) {
			archive(eventUuids);
		}
	}

	@Override
	public boolean doesExternalTokenExist(String externalToken, String eventUuid) {
		return service.exists(
			(cb, eventRoot, cq) -> CriteriaBuilderHelper.and(
				cb,
				cb.equal(eventRoot.get(Event.EXTERNAL_TOKEN), externalToken),
				cb.notEqual(eventRoot.get(Event.UUID), eventUuid),
				cb.notEqual(eventRoot.get(Event.DELETED), Boolean.TRUE)));
	}

	@Override
	public String getUuidByCaseUuidOrPersonUuid(String searchTerm) {
		return service.getUuidByCaseUuidOrPersonUuid(searchTerm);
	}

	@Override
	public Set<RegionReferenceDto> getAllRegionsRelatedToEventUuids(List<String> uuids) {
		Set<RegionReferenceDto> regionReferenceDtos = new HashSet<>();
		IterableHelper.executeBatched(uuids, ModelConstants.PARAMETER_LIMIT, batchedUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Event> eventRoot = cq.from(Event.class);
			Join<Event, Location> locationJoin = eventRoot.join(Event.EVENT_LOCATION, JoinType.INNER);
			Join<Location, Region> regionJoin = locationJoin.join(Location.REGION, JoinType.INNER);

			cq.select(regionJoin.get(Region.UUID)).distinct(true);
			cq.where(eventRoot.get(Event.UUID).in(batchedUuids));

			em.createQuery(cq).getResultList().stream().map(RegionReferenceDto::new).forEach(regionReferenceDtos::add);
		});
		return regionReferenceDtos;
	}

	@Override
	@RightsAllowed(UserRight._EVENT_EDIT)
	public void updateExternalData(@Valid List<ExternalDataDto> externalData) throws ExternalDataUpdateException {
		service.updateExternalData(externalData);
	}

	@Override
	public List<String> getSubordinateEventUuids(List<String> uuids) {

		List<String> subordinateEventUuids = new ArrayList<>();
		IterableHelper.executeBatched(uuids, ModelConstants.PARAMETER_LIMIT, (batchedUuids) -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Event> from = cq.from(Event.class);
			EventQueryContext queryContext = new EventQueryContext(cb, cq, from);

			Predicate filters = CriteriaBuilderHelper.and(
				cb,
				service.createUserFilter(queryContext),
				service.createActiveEventsFilter(cb, from),
				queryContext.getJoins().getSuperordinateEvent().get(Event.UUID).in(batchedUuids));

			cq.where(filters);
			cq.select(from.get(Event.UUID));

			subordinateEventUuids.addAll(em.createQuery(cq).getResultList());
		});

		return subordinateEventUuids;
	}

	@Override
	public boolean hasRegionAndDistrict(String eventUuid) {
		return service.hasRegionAndDistrict(eventUuid);
	}

	@Override
	public boolean hasAnyEventParticipantWithoutJurisdiction(String eventUuid) {
		return service.hasAnyEventParticipantWithoutJurisdiction(eventUuid);
	}

	@Override
	@RightsAllowed(UserRight._EVENT_EDIT)
	public List<ProcessedEntity> saveBulkEvents(
		List<String> eventUuidList,
		EventDto updatedTempEvent,
		boolean eventStatusChange,
		boolean eventInvestigationStatusChange,
		boolean eventManagementStatusChange) {

		List<ProcessedEntity> processedEvents = new ArrayList();

		for (String eventUuid : eventUuidList) {
			Event event = service.getByUuid(eventUuid);

			try {
				if (service.isEditAllowed(event)) {
					EventDto eventDto = toDto(event);
					if (eventStatusChange) {
						eventDto.setEventStatus(updatedTempEvent.getEventStatus());
					}
					if (eventInvestigationStatusChange) {
						eventDto.setEventInvestigationStatus(updatedTempEvent.getEventInvestigationStatus());
					}
					if (eventManagementStatusChange) {
						eventDto.setEventManagementStatus(updatedTempEvent.getEventManagementStatus());
					}

					save(eventDto);
					processedEvents.add(new ProcessedEntity(eventUuid, ProcessedEntityStatus.SUCCESS));
				} else {
					processedEvents.add(new ProcessedEntity(eventUuid, ProcessedEntityStatus.NOT_ELIGIBLE));
				}
			} catch (AccessDeniedException e) {
				processedEvents.add(new ProcessedEntity(eventUuid, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
			} catch (Exception e) {
				processedEvents.add(new ProcessedEntity(eventUuid, ProcessedEntityStatus.INTERNAL_FAILURE));
			}
		}
		return processedEvents;
	}

	@Override
	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		if (deletionReference == DeletionReference.REPORT) {
			return Event.REPORT_DATE_TIME;
		}

		return super.getDeleteReferenceField(deletionReference);
	}

	@Override
	protected CoreEntityType getCoreEntityType() {
		return CoreEntityType.EVENT;
	}

	@Override
	public boolean isInJurisdictionOrOwned(String uuid) {
		return service.inJurisdictionOrOwned(service.getByUuid(uuid));
	}

	@LocalBean
	@Stateless
	public static class EventFacadeEjbLocal extends EventFacadeEjb {

		public EventFacadeEjbLocal() {
		}

		@Inject
		public EventFacadeEjbLocal(EventService service) {
			super(service);
		}
	}
}
