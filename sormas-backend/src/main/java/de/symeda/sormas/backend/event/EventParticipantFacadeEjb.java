/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "EventParticipantFacade")
public class EventParticipantFacadeEjb implements EventParticipantFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private EventService eventService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private PersonService personService;
	@EJB
	private CaseService caseService;
	@EJB
	private UserService userService;
	@EJB
	private EventJurisdictionChecker eventJurisdictionChecker;

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

		Pseudonymizer pseudonymizer = new Pseudonymizer(userService::hasRight);
		return eventParticipantService.getAllByEventAfter(date, event).stream().map(e -> convertToDto(e, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return eventParticipantService.getAllActiveUuids(user);
	}

	@Override
	public List<EventParticipantDto> getAllActiveEventParticipantsAfter(Date date) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = new Pseudonymizer(userService::hasRight);
		return eventParticipantService.getAllActiveEventParticipantsAfter(date, user)
			.stream()
			.map(c -> convertToDto(c, pseudonymizer))
			.collect(Collectors.toList());
	}

	@Override
	public List<EventParticipantDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = new Pseudonymizer(userService::hasRight);
		return eventParticipantService.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public EventParticipantDto getEventParticipantByUuid(String uuid) {
		return convertToDto(eventParticipantService.getByUuid(uuid), new Pseudonymizer(userService::hasRight));
	}

	@Override
	public EventParticipantDto saveEventParticipant(EventParticipantDto dto) {
		EventParticipant existingParticipant = dto.getUuid() != null ? eventParticipantService.getByUuid(dto.getUuid()) : null;
		EventParticipantDto existingDto = toDto(existingParticipant);

		Pseudonymizer pseudonymizer = new Pseudonymizer(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingParticipant, pseudonymizer);

		validate(dto);

		EventParticipant entity = fromDto(dto);
		eventParticipantService.ensurePersisted(entity);

		return convertToDto(entity, pseudonymizer);
	}

	@Override
	public void validate(EventParticipantDto eventParticipant) throws ValidationRuntimeException {

		// Check whether any required field that does not have a not null constraint in the database is empty
		if (eventParticipant.getPerson() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPerson));
		}
	}

	@Override
	public void deleteEventParticipant(EventParticipantReferenceDto eventParticipantRef) {

		if (!userService.hasRight(UserRight.EVENTPARTICIPANT_DELETE)) {
			throw new UnsupportedOperationException("Your user is not allowed to delete event participants");
		}

		EventParticipant eventParticipant = eventParticipantService.getByReferenceDto(eventParticipantRef);
		eventParticipantService.delete(eventParticipant);
	}

	@Override
	public List<EventParticipantIndexDto> getIndexList(
		EventParticipantCriteria eventParticipantCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		if (eventParticipantCriteria == null || eventParticipantCriteria.getEvent() == null) {
			return new ArrayList<>(); // Retrieving an index list independent of an event is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipantIndexDto> cq = cb.createQuery(EventParticipantIndexDto.class);
		Root<EventParticipant> eventParticipant = cq.from(EventParticipant.class);

		Join<EventParticipant, Person> person = eventParticipant.join(EventParticipant.PERSON, JoinType.LEFT);
		Join<EventParticipant, Case> resultingCase = eventParticipant.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);
		Join<EventParticipant, Event> event = eventParticipant.join(EventParticipant.EVENT, JoinType.LEFT);
		Join<Event, Location> eventLocation = event.join(Event.EVENT_LOCATION, JoinType.LEFT);

		cq.multiselect(
			eventParticipant.get(EventParticipant.UUID),
			person.get(Person.UUID),
			resultingCase.get(Case.UUID),
			event.get(Event.UUID),
			person.get(Person.FIRST_NAME),
			person.get(Person.LAST_NAME),
			person.get(Person.SEX),
			person.get(Person.APPROXIMATE_AGE),
			person.get(Person.APPROXIMATE_AGE_TYPE),
			eventParticipant.get(EventParticipant.INVOLVEMENT_DESCRIPTION),
			event.join(Event.REPORTING_USER, JoinType.LEFT).get(User.UUID),
			event.join(Event.SURVEILLANCE_OFFICER, JoinType.LEFT).get(User.UUID),
			eventLocation.join(Location.REGION, JoinType.LEFT).get(Region.UUID),
			eventLocation.join(Location.DISTRICT, JoinType.LEFT).get(District.UUID),
			eventLocation.join(Location.COMMUNITY, JoinType.LEFT).get(Community.UUID));

		Predicate filter = eventParticipantService.buildCriteriaFilter(eventParticipantCriteria, cb, eventParticipant);
		cq.where(filter);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EventParticipantIndexDto.UUID:
				case EventParticipantIndexDto.INVOLVEMENT_DESCRIPTION:
					expression = eventParticipant.get(sortProperty.propertyName);
					break;
				case EventParticipantIndexDto.PERSON_UUID:
					expression = person.get(Person.UUID);
					break;
				case EventParticipantIndexDto.APPROXIMATE_AGE:
				case EventParticipantIndexDto.SEX:
					expression = person.get(sortProperty.propertyName);
					break;
				case EventParticipantIndexDto.NAME:
					expression = person.get(Person.LAST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = person.get(Person.FIRST_NAME);
					break;
				case EventParticipantIndexDto.CASE_UUID:
					expression = resultingCase.get(Case.UUID);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(person.get(Person.LAST_NAME)));
		}

		List<EventParticipantIndexDto> indexList;
		if (first != null && max != null) {
			indexList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			indexList = em.createQuery(cq).getResultList();
		}

		Pseudonymizer pseudonymizer = new Pseudonymizer(userService::hasRight);
		pseudonymizer.pseudonymizeDtoCollection(
			EventParticipantIndexDto.class,
			indexList,
			p -> eventJurisdictionChecker.isInJurisdiction(p.getEventJurisdiction()),
			null);

		return indexList;
	}

	@Override
	public long count(EventParticipantCriteria eventParticipantCriteria) {
		if (eventParticipantCriteria == null || eventParticipantCriteria.getEvent() == null) {
			return 0L; // Retrieving a count independent of an event is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EventParticipant> root = cq.from(EventParticipant.class);
		Predicate filter = eventParticipantService.buildCriteriaFilter(eventParticipantCriteria, cb, root);
		cq.where(filter);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	public EventParticipant fromDto(@NotNull EventParticipantDto source) {

		EventParticipant target = eventParticipantService.getByUuid(source.getUuid());
		if (target == null) {
			target = new EventParticipant();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setEvent(eventService.getByReferenceDto(source.getEvent()));
		target.setPerson(personService.getByUuid(source.getPerson().getUuid()));
		target.setInvolvementDescription(source.getInvolvementDescription());
		target.setResultingCase(caseService.getByReferenceDto(source.getResultingCase()));

		return target;
	}

	private EventParticipantDto convertToDto(EventParticipant source, Pseudonymizer pseudonymizer) {
		EventParticipantDto dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizer);

		return dto;
	}

	private void pseudonymizeDto(EventParticipant source, EventParticipantDto dto, Pseudonymizer pseudonymizer) {

		if (source != null) {
			boolean inJurisdiction = eventJurisdictionChecker.isInJurisdiction(source.getEvent());

			pseudonymizer.pseudonymizeDto(EventParticipantDto.class, dto, inJurisdiction, p -> {
				pseudonymizer.pseudonymizeDto(PersonDto.class, p.getPerson(), inJurisdiction, null);
				pseudonymizer.pseudonymizeDto(LocationDto.class, p.getPerson().getAddress(), inJurisdiction, null);
			});
		}
	}

	private void restorePseudonymizedDto(
		EventParticipantDto dto,
		EventParticipantDto originalDto,
		EventParticipant originalEventParticipant,
		Pseudonymizer pseudonymizer) {

		if (originalDto != null) {
			pseudonymizer.restorePseudonymizedValues(
				EventParticipantDto.class,
				dto,
				originalDto,
				eventJurisdictionChecker.isInJurisdiction(originalEventParticipant.getEvent()));
		}
	}

	public static EventParticipantReferenceDto toReferenceDto(EventParticipant entity) {

		if (entity == null) {
			return null;
		}

		EventParticipantReferenceDto dto = new EventParticipantReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static EventParticipantDto toDto(EventParticipant source) {

		if (source == null) {
			return null;
		}
		EventParticipantDto target = new EventParticipantDto();
		DtoHelper.fillDto(target, source);

		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		target.setPerson(PersonFacadeEjb.toDto(source.getPerson()));
		target.setInvolvementDescription(source.getInvolvementDescription());
		target.setResultingCase(CaseFacadeEjb.toReferenceDto(source.getResultingCase()));

		return target;
	}

	@LocalBean
	@Stateless
	public static class EventParticipantFacadeEjbLocal extends EventParticipantFacadeEjb {

	}
}
