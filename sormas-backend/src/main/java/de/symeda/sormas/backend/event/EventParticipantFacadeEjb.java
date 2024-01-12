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

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.EmbeddedSampleExportDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantExportDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantListEntryDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventParticipantSelectionDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationEntityHelper;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.importexport.ExportHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationService;

@Stateless(name = "EventParticipantFacade")
@RightsAllowed(UserRight._EVENTPARTICIPANT_VIEW)
public class EventParticipantFacadeEjb
	extends
	AbstractCoreFacadeEjb<EventParticipant, EventParticipantDto, EventParticipantIndexDto, EventParticipantReferenceDto, EventParticipantService, EventParticipantCriteria>
	implements EventParticipantFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private EventService eventService;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private EventParticipantService service;
	@EJB
	private PersonService personService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private NotificationService notificationService;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
	@EJB
	private VaccinationFacadeEjb.VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private VaccinationService vaccinationService;

	public EventParticipantFacadeEjb() {
	}

	@Inject
	public EventParticipantFacadeEjb(EventParticipantService service) {
		super(EventParticipant.class, EventParticipantDto.class, service);
	}

	public static EventParticipantReferenceDto toReferenceDto(EventParticipant entity) {

		if (entity == null) {
			return null;
		}

		Person person = entity.getPerson();

		return new EventParticipantReferenceDto(entity.getUuid(), person.getFirstName(), person.getFirstName());
	}

	@Override
	public List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, String eventUuid) {

		User user = userService.getCurrentUser();
		Event event = eventService.getByUuid(eventUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		if (event == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(service.getAllByEventAfter(date, event));
	}

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getArchivedUuidsSince(since);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._SYSTEM)
	public void archiveAllArchivableEventParticipants(int daysAfterEventParticipantGetsArchived) {
		archiveAllArchivableEventParticipants(daysAfterEventParticipantGetsArchived, LocalDate.now());
	}

	private void archiveAllArchivableEventParticipants(int daysAfterEventParticipantGetsArchived, @NotNull LocalDate referenceDate) {
		LocalDate notChangedSince = referenceDate.minusDays(daysAfterEventParticipantGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<EventParticipant> from = cq.from(EventParticipant.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(
			cb.equal(from.get(EventParticipant.ARCHIVED), false),
			cb.equal(from.get(EventParticipant.DELETED), false),
			cb.not(service.createChangeDateFilter(cb, from, notChangedTimestamp)));
		cq.select(from.get(EventParticipant.UUID)).distinct(true);
		List<String> eventParticipantUuids = em.createQuery(cq).getResultList();

		if (!eventParticipantUuids.isEmpty()) {
			archive(eventParticipantUuids);
		}
	}

	private String getNumberOfDosesFromVaccinations(Vaccination vaccination) {
		return vaccination != null ? vaccination.getVaccineDose() : "";
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return service.getDeletedUuidsSince(since, user);
	}

	@Override
	public EventParticipantDto getEventParticipantByUuid(String uuid) {
		// todo plainly duplicated from AbstractCoreFacadeEjb.getByUuid
		return toPseudonymizedDto(service.getByUuid(uuid));
	}

	@Override
	public Page<EventParticipantIndexDto> getIndexPage(
		EventParticipantCriteria eventParticipantCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<EventParticipantIndexDto> eventParticipantIndexList = getIndexList(eventParticipantCriteria, offset, size, sortProperties);
		long totalElementCount = count(eventParticipantCriteria);
		return new Page<>(eventParticipantIndexList, offset, size, totalElementCount);
	}

	@Override
	@RightsAllowed({
		UserRight._EVENTPARTICIPANT_CREATE,
		UserRight._EVENTPARTICIPANT_EDIT })
	public EventParticipantDto save(@Valid @NotNull EventParticipantDto dto) {
		return saveEventParticipant(dto, true, true);
	}

	@RightsAllowed({
		UserRight._EVENTPARTICIPANT_CREATE,
		UserRight._EVENTPARTICIPANT_EDIT })
	public EventParticipantDto saveEventParticipant(@Valid EventParticipantDto dto, boolean checkChangeDate, boolean internal) {
		EventParticipant existingParticipant = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;
		FacadeHelper.checkCreateAndEditRights(existingParticipant, userService, UserRight.EVENTPARTICIPANT_CREATE, UserRight.EVENTPARTICIPANT_EDIT);

		if (internal && existingParticipant != null && !service.isEditAllowed(existingParticipant)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorEventParticipantNotEditable));
		}

		EventParticipantDto existingDto = toDto(existingParticipant);

		User user = userService.getCurrentUser();

		EventReferenceDto eventReferenceDto = dto.getEvent();
		Event event = eventService.getByUuid(eventReferenceDto.getUuid());

		Pseudonymizer pseudonymizer = createPseudonymizer();
		restorePseudonymizedDto(dto, existingDto, existingParticipant, pseudonymizer);

		validate(dto);

		EventParticipant entity = fillOrBuildEntity(dto, existingParticipant, checkChangeDate);
		// Create newly event participants with the same archiving status as the Event
		entity.setArchived(existingParticipant == null ? event.isArchived() : existingParticipant.isArchived());
		service.ensurePersisted(entity);

		if (existingParticipant == null) {
			// The Event Participant is newly created, let's check if the related person is related to other events
			// In that case, let's notify corresponding responsible Users of this relation
			notifyEventResponsibleUsersOfCommonEventParticipant(entity, event);
		}

		onEventParticipantChanged(eventFacade.toDto(entity.getEvent()), existingDto, entity, internal);

		return toPseudonymizedDto(entity, pseudonymizer);
	}

	/**
	 * returns a list containing event participants of the given two persons in the same event
	 * 
	 * @param firstPersonUuid
	 * @param secondPersonUuid
	 * @return
	 */
	@RightsAllowed(UserRight._PERSON_MERGE)
	public List<EventParticipantSelectionDto> getEventParticipantsWithSameEvent(String firstPersonUuid, String secondPersonUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipantSelectionDto> cq = cb.createQuery(EventParticipantSelectionDto.class);
		Root<Event> eventRoot = cq.from(Event.class);
		Join<Event, EventParticipant> eventParticipantJoin = eventRoot.join(Event.EVENT_PARTICIPANTS, JoinType.INNER);
		Join<EventParticipant, Person> personJoin = eventParticipantJoin.join(EventParticipant.PERSON, JoinType.INNER);
		Join<EventParticipant, Case> caseJoin = eventParticipantJoin.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);
		Join<EventParticipant, District> districtJoin = eventParticipantJoin.join(EventParticipant.DISTRICT, JoinType.LEFT);

		cq.multiselect(
			eventRoot.get(Event.UUID),
			eventRoot.get(Event.EVENT_TITLE),
			eventParticipantJoin.get(EventParticipantDto.UUID),
			personJoin.get(PersonDto.UUID),
			personJoin.get(PersonDto.FIRST_NAME),
			personJoin.get(PersonDto.LAST_NAME),
			personJoin.get(PersonDto.APPROXIMATE_AGE),
			personJoin.get(PersonDto.APPROXIMATE_AGE_TYPE),
			personJoin.get(PersonDto.BIRTH_DATE_DD),
			personJoin.get(PersonDto.BIRTH_DATE_MM),
			personJoin.get(PersonDto.BIRTH_DATE_YYYY),
			personJoin.get(PersonDto.SEX),
			districtJoin.get(District.NAME),
			eventParticipantJoin.get(EventParticipantDto.INVOLVEMENT_DESCRIPTION),
			caseJoin.get(Case.UUID));

		cq.where(
			cb.and(
				isPersonParticipantInEvent(firstPersonUuid, eventRoot, cq, cb),
				isPersonParticipantInEvent(secondPersonUuid, eventRoot, cq, cb),
				cb.in(personJoin.get(Person.UUID)).value(Arrays.asList(firstPersonUuid, secondPersonUuid)),
				cb.isFalse(eventParticipantJoin.get(EventParticipant.DELETED))));

		List<EventParticipantSelectionDto> resultList = em.createQuery(cq).getResultList();

		if (!resultList.isEmpty()) {
			EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
			Map<String, Long> eventParticipantContactCount = getContactCountPerEventParticipant(
				resultList.stream().map(EventParticipantSelectionDto::getUuid).collect(Collectors.toList()),
				eventParticipantCriteria);

			for (EventParticipantSelectionDto eventParticipantSelectionDto : resultList) {
				Optional.ofNullable(eventParticipantContactCount.get(eventParticipantSelectionDto.getUuid()))
					.ifPresent(eventParticipantSelectionDto::setContactCount);
			}
		}
		return resultList;
	}

	private Predicate isPersonParticipantInEvent(
		String personUuid,
		Root<Event> event,
		CriteriaQuery<EventParticipantSelectionDto> cq,
		CriteriaBuilder cb) {

		final Subquery<Long> personSubquery = cq.subquery(Long.class);
		final Root<Event> eventRoot = personSubquery.from(Event.class);
		Join<Event, EventParticipant> eventParticipantJoin = eventRoot.join(Event.EVENT_PARTICIPANTS, JoinType.INNER);
		Join<EventParticipant, Person> personJoin = eventParticipantJoin.join(EventParticipant.PERSON, JoinType.INNER);

		personSubquery.select(eventRoot.get(Event.UUID));
		personSubquery.where(
			cb.and(
				cb.equal(personJoin.get(Person.UUID), personUuid),
				cb.equal(eventRoot.get(Event.UUID), event.get(Event.UUID)),
				cb.isFalse(eventParticipantJoin.get(EventParticipant.DELETED))));
		return cb.exists(personSubquery);
	}

	@PermitAll
	public void onEventParticipantChanged(
		EventDto event,
		EventParticipantDto existingEventParticipant,
		EventParticipant newEventParticipant,
		boolean syncShares) {

		if (existingEventParticipant == null) {
			vaccinationFacade.updateVaccinationStatuses(newEventParticipant);
		}

		eventFacade.onEventChange(event, syncShares);
	}

	private void notifyEventResponsibleUsersOfCommonEventParticipant(EventParticipant eventParticipant, Event event) {
		try {

			notificationService.sendNotifications(
				NotificationType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				MessageSubject.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS,
				() -> {

					final Date fromDate = Date.from(Instant.now().minus(Duration.ofDays(30)));
					final Map<String, Optional<User>> responsibleUserByEventUuid =
						eventService.getAllEventUuidsWithResponsibleUserByPersonAndDiseaseAfterDateForNotification(
							eventParticipant.getPerson().getUuid(),
							event.getDisease(),
							fromDate);
					if (responsibleUserByEventUuid.size() == 1 && responsibleUserByEventUuid.containsKey(event.getUuid())) {
						// it means the event participant is only appearing into the current event
						return new HashMap<>();
					}

					final Map<User, String> mapToReturn = new HashMap<>();
					for (Map.Entry<String, Optional<User>> entry : responsibleUserByEventUuid.entrySet()) {
						entry.getValue().filter(user -> StringUtils.isNotEmpty(user.getUserEmail())).ifPresent(user -> {
							String message = String.format(
								I18nProperties.getString(MessageContents.CONTENT_EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS),
								DataHelper.getShortUuid(eventParticipant.getPerson().getUuid()),
								DataHelper.getShortUuid(eventParticipant.getUuid()),
								DataHelper.getShortUuid(event.getUuid()),
								User.buildCaptionForNotification(event.getResponsibleUser()),
								User.buildCaptionForNotification(userService.getCurrentUser()),
								buildEventListContentForNotification(responsibleUserByEventUuid));
							mapToReturn.put(user, message);
						});
					}
					return mapToReturn;
				});
		} catch (NotificationDeliveryFailedException e) {
			logger.error(
				"NotificationDeliveryFailedException when trying to notify event responsible user about a newly created EventParticipant related to other events.");
		}
	}

	private String buildEventListContentForNotification(Map<String, Optional<User>> responsibleUserByEventUuid) {
		return responsibleUserByEventUuid.entrySet()
			.stream()
			.map(entry -> buildEventListContentForNotification(entry.getKey(), entry.getValue()))
			.collect(Collectors.joining("\n* ", "* ", ""));
	}

	private String buildEventListContentForNotification(String eventUuid, Optional<User> responsibleUser) {
		return String.format(
			I18nProperties.getString(Strings.notificationEventWithResponsibleUserLine),
			DataHelper.getShortUuid(eventUuid),
			User.buildCaptionForNotification(responsibleUser.orElse(null)));
	}

	@Override
	public void validate(@Valid EventParticipantDto eventParticipant) throws ValidationRuntimeException {

		// Check whether any required field that does not have a not null constraint in the database is empty
		if (eventParticipant.getPerson() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPerson));
		}

		if (eventParticipant.getReportingUser() == null && !eventParticipant.isPseudonymized()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
		}

	}

	@Override
	@RightsAllowed(UserRight._EVENTPARTICIPANT_DELETE)
	public void delete(String uuid, DeletionDetails deletionDetails) throws ExternalSurveillanceToolRuntimeException {
		EventParticipant eventParticipant = service.getByUuid(uuid);

		if (!service.inJurisdictionOrOwned(eventParticipant)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageEventParticipantOutsideJurisdictionDeletionDenied));
		}

		service.delete(eventParticipant, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._EVENTPARTICIPANT_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<ProcessedEntity> processedEventParticipants = new ArrayList<>();
		List<EventParticipant> eventParticipantsToBeDeleted = service.getByUuids(uuids);

		if (eventParticipantsToBeDeleted != null) {
			eventParticipantsToBeDeleted.forEach(eventParticipantToBeDeleted -> {

				try {
					if (!eventParticipantToBeDeleted.isDeleted()) {
						delete(eventParticipantToBeDeleted.getUuid(), deletionDetails);
						processedEventParticipants.add(new ProcessedEntity(eventParticipantToBeDeleted.getUuid(), ProcessedEntityStatus.SUCCESS));
					} else {
						processedEventParticipants
							.add(new ProcessedEntity(eventParticipantToBeDeleted.getUuid(), ProcessedEntityStatus.NOT_ELIGIBLE));
					}
				} catch (AccessDeniedException e) {
					processedEventParticipants
						.add(new ProcessedEntity(eventParticipantToBeDeleted.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
					logger.error(
						"The event participant with uuid {} could not be deleted due to a AccessDeniedException",
						eventParticipantToBeDeleted.getUuid(),
						e);
				} catch (Exception e) {
					processedEventParticipants
						.add(new ProcessedEntity(eventParticipantToBeDeleted.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error(
						"The event participant with uuid:" + eventParticipantToBeDeleted.getUuid() + "could not be deleted due to an Exception");
				}
			});
		}

		return processedEventParticipants;
	}

	@Override
	@RightsAllowed(UserRight._EVENTPARTICIPANT_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._EVENTPARTICIPANT_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		List<ProcessedEntity> processedEventParticipants = new ArrayList<>();
		List<EventParticipant> eventParticipantsToBeRestored = service.getByUuids(uuids);

		if (eventParticipantsToBeRestored != null) {
			eventParticipantsToBeRestored.forEach(eventParticipantToBeRestored -> {
				try {
					restore(eventParticipantToBeRestored.getUuid());
					processedEventParticipants.add(new ProcessedEntity(eventParticipantToBeRestored.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedEventParticipants
						.add(new ProcessedEntity(eventParticipantToBeRestored.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error(
						"The event participant with uuid {} could not be restored due to an Exception",
						eventParticipantToBeRestored.getUuid(),
						e);
				}
			});
		}

		return processedEventParticipants;
	}

	@Override
	public List<EventParticipantIndexDto> getIndexList(
		EventParticipantCriteria eventParticipantCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		if ((eventParticipantCriteria == null) || (eventParticipantCriteria.getEvent() == null && eventParticipantCriteria.getPerson() == null)) {
			// Retrieving an index list independent of an event is not possible
			return new ArrayList<>();
		}

		List<Long> indexListIds = getIndexListIds(eventParticipantCriteria, first, max, sortProperties);

		List<EventParticipantIndexDto> indexList = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			final Root<EventParticipant> eventParticipant = cq.from(EventParticipant.class);
			final EventParticipantQueryContext queryContext = new EventParticipantQueryContext(cb, cq, eventParticipant);
			EventParticipantJoins joins = queryContext.getJoins();

			Join<EventParticipant, Person> person = joins.getPerson();
			Join<EventParticipant, Case> resultingCase = joins.getResultingCase();
			Join<EventParticipant, Event> event = joins.getEvent();
			final Join<EventParticipant, Sample> samples = queryContext.getSamplesJoin();

			Subquery<Number> labResultSq = cq.subquery(Number.class);
			Root<Sample> labResultsSqRoot = labResultSq.from(Sample.class);
			labResultSq.where(
				cb.and(
					cb.equal(labResultsSqRoot.get(Sample.ASSOCIATED_EVENT_PARTICIPANT), eventParticipant),
					cb.isFalse(labResultsSqRoot.get(Sample.DELETED))));
			labResultSq.distinct(true);
			labResultSq.select(cb.max(labResultsSqRoot.get(Sample.PATHOGEN_TEST_RESULT)));

			Subquery<Number> sampleDateSq = cq.subquery(Number.class);
			Root<Sample> sampleSqRoot = sampleDateSq.from(Sample.class);
			sampleDateSq.where(
				cb.and(
					cb.equal(sampleSqRoot.get(Sample.ASSOCIATED_EVENT_PARTICIPANT), eventParticipant),
					cb.isFalse(sampleSqRoot.get(Sample.DELETED))));
			sampleDateSq.distinct(true);
			sampleDateSq.select(cb.max(sampleSqRoot.get(Sample.SAMPLE_DATE_TIME)));

			Expression<Object> inJurisdictionSelector = JurisdictionHelper.booleanSelector(cb, service.inJurisdiction(queryContext));
			Expression<Object> inJurisdictionOrOwnedSelector = JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(queryContext));

			cq.multiselect(
				Stream
					.concat(
						Stream.of(
							eventParticipant.get(EventParticipant.UUID),
							person.get(Person.UUID),
							resultingCase.get(Case.UUID),
							event.get(Event.UUID),
							person.get(Person.FIRST_NAME),
							person.get(Person.LAST_NAME),
							person.get(Person.SEX),
							person.get(Person.APPROXIMATE_AGE),
							eventParticipant.get(EventParticipant.INVOLVEMENT_DESCRIPTION),
							labResultSq,
							sampleDateSq,
							eventParticipant.get(EventParticipant.VACCINATION_STATUS),
							eventParticipant.get(EventParticipant.DELETION_REASON),
							eventParticipant.get(EventParticipant.OTHER_DELETION_REASON),
							inJurisdictionSelector,
							inJurisdictionOrOwnedSelector),
						sortBy(sortProperties, queryContext).stream())
					.collect(Collectors.toList()));
			cq.where(eventParticipant.get(EventParticipant.ID).in(batchedIds));
			cq.distinct(true);

			indexList.addAll(QueryHelper.getResultList(em, cq, new EventParticipantIndexDtoResultTransformer(), null, null));
		});

		if (!indexList.isEmpty()) {
			Map<String, Long> eventParticipantContactCount = getContactCountPerEventParticipant(
				indexList.stream().map(EventParticipantIndexDto::getUuid).collect(Collectors.toList()),
				eventParticipantCriteria);

			for (EventParticipantIndexDto eventParticipantIndexDto : indexList) {
				Optional.ofNullable(eventParticipantContactCount.get(eventParticipantIndexDto.getUuid()))
					.ifPresent(eventParticipantIndexDto::setContactCount);
			}
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(EventParticipantIndexDto.class, indexList, p -> p.getInJurisdictionOrOwned(), null);

		return indexList;
	}

	private List<Long> getIndexListIds(
		EventParticipantCriteria eventParticipantCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		if ((eventParticipantCriteria == null) || (eventParticipantCriteria.getEvent() == null && eventParticipantCriteria.getPerson() == null)) {
			// Retrieving an index list independent of an event is not possible
			return new ArrayList<>();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<EventParticipant> eventParticipant = cq.from(EventParticipant.class);
		final EventParticipantQueryContext queryContext = new EventParticipantQueryContext(cb, cq, eventParticipant);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(eventParticipant.get(Person.ID));
		selections.addAll(sortBy(sortProperties, queryContext));

		cq.multiselect(selections);

		Predicate filter = service.buildCriteriaFilter(eventParticipantCriteria, queryContext);

		Join<EventParticipant, Sample> samples = queryContext.getSamplesJoin();

		if (eventParticipantCriteria.getPathogenTestResult() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(samples.get(Sample.PATHOGEN_TEST_RESULT), eventParticipantCriteria.getPathogenTestResult()));
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.distinct(true);

		List<Tuple> persons = QueryHelper.getResultList(em, cq, first, max);
		return persons.stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, EventParticipantQueryContext eventParticipantQueryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = eventParticipantQueryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = eventParticipantQueryContext.getQuery();
		From<?, EventParticipant> eventParticipantRoot = eventParticipantQueryContext.getRoot();
		EventParticipantJoins joins = eventParticipantQueryContext.getJoins();

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EventParticipantIndexDto.UUID:
				case EventParticipantIndexDto.VACCINATION_STATUS:
					expression = eventParticipantRoot.get(sortProperty.propertyName);
					break;
				case EventParticipantIndexDto.INVOLVEMENT_DESCRIPTION:
					expression = cb.lower(eventParticipantRoot.get(sortProperty.propertyName));
					break;
				case EventParticipantIndexDto.PERSON_UUID:
					expression = joins.getPerson().get(Person.UUID);
					break;
				case EventParticipantIndexDto.APPROXIMATE_AGE:
				case EventParticipantIndexDto.SEX:
					expression = joins.getPerson().get(sortProperty.propertyName);
					break;
				case EventParticipantIndexDto.LAST_NAME:
				case EventParticipantIndexDto.FIRST_NAME:
					expression = cb.lower(joins.getPerson().get(sortProperty.propertyName));
					break;
				case EventParticipantIndexDto.CASE_UUID:
					expression = cb.lower(joins.getResultingCase().get(Case.UUID));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Expression<?> changeDate = eventParticipantRoot.get(EventParticipant.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	@Override
	public List<EventParticipantListEntryDto> getListEntries(EventParticipantCriteria eventParticipantCriteria, Integer first, Integer max) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipantListEntryDto> cq = cb.createQuery(EventParticipantListEntryDto.class);
		Root<EventParticipant> eventParticipant = cq.from(EventParticipant.class);

		Join<EventParticipant, Event> event = eventParticipant.join(EventParticipant.EVENT, JoinType.LEFT);

		EventParticipantQueryContext queryContext = new EventParticipantQueryContext(cb, cq, eventParticipant);
		cq.multiselect(
			eventParticipant.get(EventParticipant.UUID),
			event.get(Event.UUID),
			event.get(Event.EVENT_STATUS),
			event.get(Event.DISEASE),
			event.get(Event.EVENT_TITLE),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(queryContext)));

		Predicate filter =
			CriteriaBuilderHelper.and(cb, service.buildCriteriaFilter(eventParticipantCriteria, queryContext), cb.isFalse(event.get(Event.DELETED)));

		cq.where(filter);
		cq.orderBy(cb.desc(eventParticipant.get(EventParticipant.CREATION_DATE)));

		List<EventParticipantListEntryDto> result = QueryHelper.getResultList(em, cq, first, max);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(EventParticipantListEntryDto.class, result, p -> p.getInJurisdiction(), null);

		return result;
	}

	@Override
	public List<EventParticipantExportDto> getExportList(
		EventParticipantCriteria eventParticipantCriteria,
		Collection<String> selectedRows,
		int first,
		int max,
		Language userLanguage,
		ExportConfigurationDto exportConfiguration) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipantExportDto> cq = cb.createQuery(EventParticipantExportDto.class);
		Root<EventParticipant> eventParticipant = cq.from(EventParticipant.class);
		EventParticipantQueryContext eventParticipantQueryContext = new EventParticipantQueryContext(cb, cq, eventParticipant);
		EventParticipantJoins joins = eventParticipantQueryContext.getJoins();

		Join<EventParticipant, Person> person = joins.getPerson();

		PersonQueryContext personQueryContext = new PersonQueryContext(cb, cq, joins.getPersonJoins());

		Join<Person, Location> address = joins.getAddress();
		Join<Person, Country> birthCountry = person.join(Person.BIRTH_COUNTRY, JoinType.LEFT);
		Join<Person, Country> citizenship = person.join(Person.CITIZENSHIP, JoinType.LEFT);

		Join<EventParticipant, Event> event = joins.getEvent();
		Join<Event, Location> eventLocation = joins.getEventAddress();

		Join<EventParticipant, Case> resultingCase = joins.getResultingCase();

		cq.multiselect(
			eventParticipant.get(EventParticipant.ID),
			person.get(Person.ID),
			person.get(Person.UUID),
			eventParticipant.get(EventParticipant.UUID),
			person.get(Person.NATIONAL_HEALTH_ID),
			person.get(Location.ID),
			JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(eventParticipantQueryContext)),

			event.get(Event.UUID),
			event.get(Event.REPORT_DATE_TIME),

			event.get(Event.EVENT_STATUS),
			event.get(Event.EVENT_INVESTIGATION_STATUS),
			event.get(Event.DISEASE),
			event.get(Event.TYPE_OF_PLACE),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			event.get(Event.EVENT_TITLE),
			event.get(Event.EVENT_DESC),
			eventLocation.join(Location.REGION, JoinType.LEFT).get(Region.NAME),
			eventLocation.join(Location.DISTRICT, JoinType.LEFT).get(District.NAME),
			eventLocation.join(Location.COMMUNITY, JoinType.LEFT).get(Community.NAME),
			eventLocation.get(Location.CITY),
			eventLocation.get(Location.STREET),
			eventLocation.get(Location.HOUSE_NUMBER),

			person.get(Person.FIRST_NAME),
			person.get(Person.LAST_NAME),
			person.get(Person.SALUTATION),
			person.get(Person.OTHER_SALUTATION),
			person.get(Person.SEX),
			eventParticipant.get(EventParticipant.INVOLVEMENT_DESCRIPTION),
			person.get(Person.APPROXIMATE_AGE),
			person.get(Person.APPROXIMATE_AGE_TYPE),
			person.get(Person.BIRTHDATE_DD),
			person.get(Person.BIRTHDATE_MM),
			person.get(Person.BIRTHDATE_YYYY),
			person.get(Person.PRESENT_CONDITION),
			person.get(Person.DEATH_DATE),
			person.get(Person.BURIAL_DATE),
			person.get(Person.BURIAL_CONDUCTOR),
			person.get(Person.BURIAL_PLACE_DESCRIPTION),

			joins.getAddressRegion().get(Region.NAME),
			joins.getAddressDistrict().get(District.NAME),
			joins.getAddressCommunity().get(Community.NAME),
			address.get(Location.CITY),
			address.get(Location.STREET),
			address.get(Location.HOUSE_NUMBER),
			address.get(Location.ADDITIONAL_INFORMATION),
			address.get(Location.POSTAL_CODE),
			personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY),
			personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY),

			resultingCase.get(Case.UUID),

			person.get(Person.BIRTH_NAME),
			birthCountry.get(Country.ISO_CODE),
			birthCountry.get(Country.DEFAULT_NAME),
			citizenship.get(Country.ISO_CODE),
			citizenship.get(Country.DEFAULT_NAME),
			eventParticipant.get(EventParticipant.VACCINATION_STATUS));

		Predicate filter = service.buildCriteriaFilter(eventParticipantCriteria, eventParticipantQueryContext);
		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, eventParticipant.get(EventParticipant.UUID));
		cq.where(filter);

		List<EventParticipantExportDto> eventParticipantResultList = QueryHelper.getResultList(em, cq, first, max);

		if (!eventParticipantResultList.isEmpty()) {
			Map<String, Long> eventParticipantContactCount = getContactCountPerEventParticipant(
				eventParticipantResultList.stream().map(EventParticipantExportDto::getEventParticipantUuid).collect(Collectors.toList()),
				eventParticipantCriteria);

			Map<Long, Location> personAddresses = null;
			if (ExportHelper.shouldExportFields(exportConfiguration, PersonDto.ADDRESS, CaseExportDto.ADDRESS_GPS_COORDINATES)) {
				CriteriaQuery<Location> personAddressesCq = cb.createQuery(Location.class);
				Root<Location> personAddressesRoot = personAddressesCq.from(Location.class);
				Expression<String> personAddressesIdsExpr = personAddressesRoot.get(Location.ID);
				personAddressesCq.where(
					personAddressesIdsExpr
						.in(eventParticipantResultList.stream().map(EventParticipantExportDto::getPersonAddressId).collect(Collectors.toList())));
				List<Location> personAddressesList = em.createQuery(personAddressesCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				personAddresses = personAddressesList.stream().collect(Collectors.toMap(Location::getId, Function.identity()));
			}

			Map<Long, List<Sample>> samples = null;
			if (ExportHelper.shouldExportFields(exportConfiguration, EventParticipantExportDto.SAMPLE_INFORMATION)) {
				List<Sample> samplesList = null;
				CriteriaQuery<Sample> samplesCq = cb.createQuery(Sample.class);
				Root<Sample> samplesRoot = samplesCq.from(Sample.class);
				Join<Sample, EventParticipant> samplesEventParticipantJoin = samplesRoot.join(Sample.ASSOCIATED_EVENT_PARTICIPANT, JoinType.LEFT);
				Expression<String> eventParticipantIdsExpr = samplesEventParticipantJoin.get(EventParticipant.ID);
				samplesCq.where(
					eventParticipantIdsExpr
						.in(eventParticipantResultList.stream().map(EventParticipantExportDto::getId).collect(Collectors.toList())));
				samplesList = em.createQuery(samplesCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				samples = samplesList.stream().collect(Collectors.groupingBy(s -> s.getAssociatedEventParticipant().getId()));
			}

			Map<Long, List<Immunization>> immunizations = null;
			if (exportConfiguration == null
				|| exportConfiguration.getProperties()
					.stream()
					.anyMatch(p -> StringUtils.equalsAny(p, ExportHelper.getVaccinationExportProperties()))) {
				List<Immunization> immunizationList;
				CriteriaQuery<Immunization> immunizationsCq = cb.createQuery(Immunization.class);
				Root<Immunization> immunizationsCqRoot = immunizationsCq.from(Immunization.class);
				Join<Immunization, Person> personJoin = immunizationsCqRoot.join(Immunization.PERSON, JoinType.LEFT);
				Expression<String> personIdsExpr = personJoin.get(Person.ID);
				immunizationsCq.where(
					CriteriaBuilderHelper.and(
						cb,
						cb.or(
							cb.equal(immunizationsCqRoot.get(Immunization.MEANS_OF_IMMUNIZATION), MeansOfImmunization.VACCINATION),
							cb.equal(immunizationsCqRoot.get(Immunization.MEANS_OF_IMMUNIZATION), MeansOfImmunization.VACCINATION_RECOVERY)),
						personIdsExpr
							.in(eventParticipantResultList.stream().map(EventParticipantExportDto::getPersonId).collect(Collectors.toList()))));
				immunizationList = em.createQuery(immunizationsCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				immunizations = immunizationList.stream().collect(Collectors.groupingBy(i -> i.getPerson().getId()));
			}

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
			for (EventParticipantExportDto exportDto : eventParticipantResultList) {
				final boolean inJurisdiction = exportDto.getInJurisdiction();

				if (personAddresses != null) {
					Optional.ofNullable(personAddresses.get(exportDto.getPersonAddressId()))
						.ifPresent(personAddress -> exportDto.setAddressGpsCoordinates(personAddress.buildGpsCoordinatesCaption()));
				}

				if (samples != null) {
					Optional.ofNullable(samples.get(exportDto.getId())).ifPresent(eventParticipantSamples -> {
						int count = 0;
						for (Sample sample : eventParticipantSamples) {
							EmbeddedSampleExportDto sampleDto = new EmbeddedSampleExportDto(
								sample.getUuid(),
								sample.getSampleDateTime(),
								sample.getLab() != null
									? FacilityHelper.buildFacilityString(sample.getLab().getUuid(), sample.getLab().getName(), sample.getLabDetails())
									: null,
								sample.getPathogenTestResult());

							exportDto.addEventParticipantSample(sampleDto);
						}
					});
				}

				if (immunizations != null) {
					Optional.ofNullable(immunizations.get(exportDto.getPersonId())).ifPresent(epImmunizations -> {
						List<Immunization> filteredImmunizations =
							epImmunizations.stream().filter(i -> i.getDisease() == exportDto.getEventDisease()).collect(Collectors.toList());
						filteredImmunizations.sort(Comparator.comparing(i -> ImmunizationEntityHelper.getDateForComparison(i, false)));
						Immunization mostRecentImmunization = filteredImmunizations.get(filteredImmunizations.size() - 1);
						Integer numberOfDoses = mostRecentImmunization.getNumberOfDoses();

						List<Vaccination> relevantSortedVaccinations = vaccinationService.getRelevantSortedVaccinations(
							filteredImmunizations.stream().flatMap(i -> i.getVaccinations().stream()).collect(Collectors.toList()),
							exportDto.getEventStartDate(),
							exportDto.getEventEndDate(),
							exportDto.getEventReportDateTime());
						Vaccination firstVaccination = null;
						Vaccination lastVaccination = null;

						if (CollectionUtils.isNotEmpty(relevantSortedVaccinations)) {
							firstVaccination = relevantSortedVaccinations.get(0);
							lastVaccination = relevantSortedVaccinations.get(relevantSortedVaccinations.size() - 1);
							exportDto.setFirstVaccinationDate(firstVaccination.getVaccinationDate());
							exportDto.setLastVaccinationDate(lastVaccination.getVaccinationDate());
							exportDto.setVaccineName(lastVaccination.getVaccineName());
							exportDto.setOtherVaccineName(lastVaccination.getOtherVaccineName());
							exportDto.setVaccineManufacturer(lastVaccination.getVaccineManufacturer());
							exportDto.setOtherVaccineManufacturer(lastVaccination.getOtherVaccineManufacturer());
							exportDto.setVaccinationInfoSource(lastVaccination.getVaccinationInfoSource());
							exportDto.setVaccineAtcCode(lastVaccination.getVaccineAtcCode());
							exportDto.setVaccineBatchNumber(lastVaccination.getVaccineBatchNumber());
							exportDto.setVaccineUniiCode(lastVaccination.getVaccineUniiCode());
							exportDto.setVaccineInn(lastVaccination.getVaccineInn());
						}

						exportDto.setVaccinationDoses(
							numberOfDoses != null ? String.valueOf(numberOfDoses) : getNumberOfDosesFromVaccinations(lastVaccination));
					});
				}

				Optional.ofNullable(eventParticipantContactCount.get(exportDto.getEventParticipantUuid())).ifPresent(exportDto::setContactCount);

				pseudonymizer.pseudonymizeDto(EventParticipantExportDto.class, exportDto, inJurisdiction, (c) -> {
					pseudonymizer.pseudonymizeDto(BirthDateDto.class, c.getBirthdate(), inJurisdiction, null);
					pseudonymizer.pseudonymizeDtoCollection(EmbeddedSampleExportDto.class, c.getEventParticipantSamples(), s -> inJurisdiction, null);
					pseudonymizer.pseudonymizeDto(BurialInfoDto.class, c.getBurialInfo(), inJurisdiction, null);
				});
			}
		}

		return eventParticipantResultList;
	}

	@Override
	public long count(EventParticipantCriteria eventParticipantCriteria) {
		if (eventParticipantCriteria == null || eventParticipantCriteria.getEvent() == null) {
			return 0L; // Retrieving a count independent of an event is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EventParticipant> root = cq.from(EventParticipant.class);
		Predicate filter = service.buildCriteriaFilter(eventParticipantCriteria, new EventParticipantQueryContext(cb, cq, root));
		cq.where(filter);
		cq.select(cb.countDistinct(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Map<String, Long> getContactCountPerEventParticipant(
		List<String> eventParticipantUuids,
		EventParticipantCriteria eventParticipantCriteria) {

		Map<String, Long> contactCountMap = new HashMap<>();

		IterableHelper.executeBatched(eventParticipantUuids, ModelConstants.PARAMETER_LIMIT, batchedEventParticipantUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> contactCount = cb.createQuery(Object[].class);

			Root<EventParticipant> epRoot = contactCount.from(EventParticipant.class);
			Root<Contact> contactRoot = contactCount.from(Contact.class);

			Predicate participantPersonEqualsContactPerson = cb.equal(epRoot.get(EventParticipant.PERSON), contactRoot.get(Contact.PERSON));
			Predicate notDeleted = cb.isFalse(epRoot.get(EventParticipant.DELETED));
			Predicate contactNotDeleted = cb.isFalse(contactRoot.get(Contact.DELETED));
			Predicate isInEvent = epRoot.get(EventParticipant.UUID).in(batchedEventParticipantUuids);

			if (Boolean.TRUE.equals(eventParticipantCriteria.getOnlyCountContactsWithSourceCaseInEvent())) {
				Subquery<EventParticipant> sourceCaseSubquery = contactCount.subquery(EventParticipant.class);
				Root<EventParticipant> epr2 = sourceCaseSubquery.from(EventParticipant.class);
				sourceCaseSubquery.select(epr2);
				sourceCaseSubquery.where(
					cb.equal(epr2.get(EventParticipant.RESULTING_CASE), contactRoot.get(Contact.CAZE)),
					cb.equal(epr2.get(EventParticipant.EVENT), epRoot.get(EventParticipant.EVENT)));

				contactCount.multiselect(
					epRoot.get(EventParticipant.UUID),
					cb.sum(cb.selectCase().when(cb.exists(sourceCaseSubquery), 1).otherwise(0).as(Long.class)));
			} else {
				contactCount.multiselect(epRoot.get(EventParticipant.UUID), cb.count(epRoot));
			}
			contactCount.where(participantPersonEqualsContactPerson, notDeleted, contactNotDeleted, isInEvent);
			contactCount.groupBy(epRoot.get(EventParticipant.UUID));

			List<Object[]> resultList = em.createQuery(contactCount).getResultList();
			resultList.forEach(r -> contactCountMap.put((String) r[0], (Long) r[1]));
		});

		return contactCountMap;
	}

	@Override
	public boolean exists(String personUuid, String eventUuid) {
		return service.exists(
			(cb, root, cq) -> cb.and(
				cb.isFalse(root.get(EventParticipant.DELETED)),
				cb.equal(root.get(EventParticipant.PERSON).get(AbstractDomainObject.UUID), personUuid),
				cb.equal(root.get(EventParticipant.EVENT).get(AbstractDomainObject.UUID), eventUuid)));
	}

	@Override
	public EventParticipantReferenceDto getReferenceByUuid(String uuid) {
		EventParticipant eventParticipant = service.getByUuid(uuid);
		return new EventParticipantReferenceDto(eventParticipant.getUuid());
	}

	@Override
	public EventParticipantReferenceDto getReferenceByEventAndPerson(String eventUuid, String personUuid) {
		return Optional.ofNullable(service.getByEventAndPerson(eventUuid, personUuid))
			.map(eventParticipant -> new EventParticipantReferenceDto(eventParticipant.getUuid()))
			.orElse(null);
	}

	@Override
	public EventParticipantDto getFirst(EventParticipantCriteria criteria) {

		if (criteria.getEvent() == null) {
			return null;
		}

		return service.getFirst(criteria)
			.map(e -> toPseudonymizedDto(e, Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue))))
			.orElse(null);
	}

	public EventParticipant fillOrBuildEntity(@NotNull EventParticipantDto source, EventParticipant target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, EventParticipant::new, checkChangeDate);

		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setEvent(eventService.getByReferenceDto(source.getEvent()));
		target.setPerson(personService.getByUuid(source.getPerson().getUuid()));
		target.setInvolvementDescription(source.getInvolvementDescription());
		target.setResultingCase(caseService.getByReferenceDto(source.getResultingCase()));
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));

		target.setVaccinationStatus(source.getVaccinationStatus());

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoService.getByUuid(source.getSormasToSormasOriginInfo().getUuid()));
		}

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected void pseudonymizeDto(EventParticipant source, EventParticipantDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		if (source != null) {
			validate(dto);

			pseudonymizer.pseudonymizeDto(EventParticipantDto.class, dto, inJurisdiction, (ep) -> {
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), userService.getCurrentUser(), ep::setReportingUser);
			});
			dto.getPerson().getAddresses().forEach(l -> pseudonymizer.pseudonymizeDto(LocationDto.class, l, inJurisdiction, null));
		}
	}

	protected void restorePseudonymizedDto(
		EventParticipantDto dto,
		EventParticipantDto originalDto,
		EventParticipant originalEventParticipant,
		Pseudonymizer pseudonymizer) {

		if (originalDto != null) {
			pseudonymizer
				.restorePseudonymizedValues(EventParticipantDto.class, dto, originalDto, service.inJurisdictionOrOwned(originalEventParticipant));
			pseudonymizer.restoreUser(originalEventParticipant.getReportingUser(), userService.getCurrentUser(), dto, dto::setReportingUser);
		}
	}

	public EventParticipantDto toDto(EventParticipant source) {
		return toEventParticipantDto(source);
	}

	public static EventParticipantDto toEventParticipantDto(EventParticipant source) {

		if (source == null) {
			return null;
		}
		EventParticipantDto target = new EventParticipantDto();
		DtoHelper.fillDto(target, source);

		target.setReportingUser(source.getReportingUser().toReference());
		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		target.setPerson(PersonFacadeEjb.toPersonDto(source.getPerson()));
		target.setInvolvementDescription(source.getInvolvementDescription());
		target.setResultingCase(CaseFacadeEjb.toReferenceDto(source.getResultingCase()));
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setVaccinationStatus(source.getVaccinationStatus());

		target.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.getSormasToSormasShares().stream().anyMatch(ShareInfoHelper::isOwnerShipHandedOver));

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected EventParticipantReferenceDto toRefDto(EventParticipant eventParticipant) {
		return toReferenceDto(eventParticipant);
	}

	@Override
	public List<EventParticipantDto> getAllActiveEventParticipantsByEvent(String eventUuid) {

		Event event = eventService.getByUuid(eventUuid);

		if (userService.getCurrentUser() == null || event == null) {
			return Collections.emptyList();
		}
		return toDtos(service.getAllActiveByEvent(event).stream());
	}

	@Override
	public List<EventParticipantDto> getByEventUuids(List<String> eventUuids) {
		return toPseudonymizedDtos(service.getByEventUuids(eventUuids));
	}

	@Override
	public List<SimilarEventParticipantDto> getMatchingEventParticipants(EventParticipantCriteria criteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<SimilarEventParticipantDto> cq = cb.createQuery(SimilarEventParticipantDto.class);
		final Root<EventParticipant> eventParticipantRoot = cq.from(EventParticipant.class);

		EventParticipantQueryContext eventParticipantQueryContext = new EventParticipantQueryContext(cb, cq, eventParticipantRoot);
		Join<EventParticipant, Person> personJoin = eventParticipantQueryContext.getJoins().getPerson();
		Join<EventParticipant, Event> eventJoin = eventParticipantQueryContext.getJoins().getEvent();

		Expression<Object> jurisdictionSelector = JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(eventParticipantQueryContext));
		cq.multiselect(
			eventParticipantRoot.get(EventParticipant.UUID),
			personJoin.get(Person.FIRST_NAME),
			personJoin.get(Person.LAST_NAME),
			eventParticipantRoot.get(EventParticipant.INVOLVEMENT_DESCRIPTION),
			eventJoin.get(Event.UUID),
			eventJoin.get(Event.EVENT_STATUS),
			eventJoin.get(Event.EVENT_TITLE),
			eventJoin.get(Event.START_DATE),
			jurisdictionSelector);
		cq.distinct(true);

		final Predicate defaultFilter = service.createDefaultFilter(cb, eventParticipantRoot);
		final Predicate userFilter = service.createUserFilter(eventParticipantQueryContext);

		final PersonReferenceDto person = criteria.getPerson();
		final Predicate samePersonFilter = person != null ? cb.equal(personJoin.get(Person.UUID), person.getUuid()) : null;

		final Disease disease = criteria.getDisease();
		final Predicate diseaseFilter = disease != null ? cb.equal(eventJoin.get(Event.DISEASE), disease) : null;

		final Date relevantDate = criteria.getRelevantDate();
		final Predicate relevantDateFilter = CriteriaBuilderHelper.or(
			cb,
			contactService.recentDateFilter(cb, relevantDate, eventJoin.get(Event.START_DATE), 30),
			contactService.recentDateFilter(cb, relevantDate, eventJoin.get(Event.END_DATE), 30),
			contactService.recentDateFilter(cb, relevantDate, eventJoin.get(Event.REPORT_DATE_TIME), 30));

		final Predicate noResulingCaseFilter =
			Boolean.TRUE.equals(criteria.getNoResultingCase()) ? cb.isNull(eventParticipantRoot.get(EventParticipant.RESULTING_CASE)) : null;

		cq.where(CriteriaBuilderHelper.and(cb, defaultFilter, userFilter, samePersonFilter, diseaseFilter, relevantDateFilter, noResulingCaseFilter));

		List<SimilarEventParticipantDto> participants = em.createQuery(cq).getResultList();

		Pseudonymizer pseudonymizer = createPseudonymizer();
		pseudonymizer.pseudonymizeDtoCollection(SimilarEventParticipantDto.class, participants, SimilarEventParticipantDto::getInJurisdiction, null);

		if (Boolean.TRUE.equals(criteria.getExcludePseudonymized())) {
			participants = participants.stream().filter(e -> !e.isPseudonymized()).collect(Collectors.toList());
		}

		return participants;
	}

	@Override
	public List<EventParticipantDto> getByPersonUuids(List<String> personUuids) {
		return toDtos(service.getByPersonUuids(personUuids).stream());
	}

	@Override
	public List<EventParticipantDto> getByEventAndPersons(String eventUuid, List<String> personUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(EventParticipant.class);
		Root<EventParticipant> eventParticipantRoot = cq.from(EventParticipant.class);
		Join<EventParticipant, Person> personJoin = eventParticipantRoot.join(Contact.PERSON, JoinType.LEFT);
		Join<EventParticipant, Event> eventJoin = eventParticipantRoot.join(EventParticipant.EVENT, JoinType.INNER);

		cq.where(
			cb.and(
				cb.equal(eventJoin.get(Event.UUID), eventUuid),
				cb.in(personJoin.get(EventParticipant.UUID)).value(personUuids),
				service.createDefaultFilter(cb, eventParticipantRoot)));

		return toDtos(em.createQuery(cq).getResultList().stream());
	}

	@Override
	@RightsAllowed(UserRight._EVENTPARTICIPANT_ARCHIVE)
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) throws ExternalSurveillanceToolRuntimeException {
		return super.archive(entityUuid, endOfProcessingDate);
	}

	@Override
	@RightsAllowed(UserRight._EVENTPARTICIPANT_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {
		return super.dearchive(entityUuids, dearchiveReason);
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.EVENT_PARTICIPANT;
	}

	@LocalBean
	@Stateless
	public static class EventParticipantFacadeEjbLocal extends EventParticipantFacadeEjb {

		public EventParticipantFacadeEjbLocal() {
		}

		@Inject
		public EventParticipantFacadeEjbLocal(EventParticipantService service) {
			super(service);
		}
	}
}
