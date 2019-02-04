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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.event;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EventService extends AbstractAdoService<Event> {

	@EJB
	DistrictFacadeEjbLocal districtFacade;

	public EventService() {
		super(Event.class);
	}

	public List<Event> getAllActiveEventsAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(getElementClass());
		Root<Event> from = cq.from(getElementClass());

		Predicate filter = cb.or(
				cb.equal(from.get(Event.ARCHIVED), false),
				cb.isNull(from.get(Event.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = cb.and(filter, dateFilter);		
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Event.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> from = cq.from(getElementClass());

		Predicate filter = cb.or(
				cb.equal(from.get(Event.ARCHIVED), false),
				cb.isNull(from.get(Event.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Event.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<Event> getAllBetween(Date fromDate, Date toDate, District district, Disease disease, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event> cq = cb.createQuery(getElementClass());
		Root<Event> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from, user);
		Predicate dateFilter = cb.greaterThanOrEqualTo(from.get(Event.EVENT_DATE), fromDate);
		dateFilter = cb.and(dateFilter, cb.lessThanOrEqualTo(from.get(Event.EVENT_DATE), toDate));

		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (filter != null && district != null) {
			Join<Event, Location> eventLocation = from.join(Event.EVENT_LOCATION);
			filter = cb.and(filter, cb.equal(eventLocation.get(Location.DISTRICT), district));
		}

		if (filter != null && disease != null) {
			filter = cb.and(filter, cb.isNotNull(from.get(Event.DISEASE)));
			filter = cb.and(filter, cb.equal(from.get(Event.DISEASE), disease));
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<Event> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<DashboardEventDto> getNewEventsForDashboard(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardEventDto> cq = cb.createQuery(DashboardEventDto.class);
		Root<Event> event = cq.from(getElementClass());
		Join<Event, Location> eventLocation = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		Join<Location, District> eventDistrict = eventLocation.join(Location.DISTRICT, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, event, user);
		Predicate dateFilter = cb.between(event.get(Event.REPORT_DATE_TIME), from, to);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (region != null) {
			Predicate regionFilter = cb.equal(eventLocation.get(Location.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(eventLocation.get(Location.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(event.get(Event.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		List<DashboardEventDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					event.get(Event.UUID),
					event.get(Event.EVENT_TYPE),
					event.get(Event.EVENT_STATUS),
					event.get(Event.DISEASE),
					event.get(Event.DISEASE_DETAILS),
					event.get(Event.EVENT_DATE),
					event.get(Event.REPORT_LAT),
					event.get(Event.REPORT_LON),
					eventDistrict.get(District.UUID)
					);

			result = em.createQuery(cq).getResultList();
			for (DashboardEventDto dashboardEventDto : result) {
				dashboardEventDto.setDistrict(districtFacade.getDistrictReferenceByUuid(dashboardEventDto.getDistrictUuid()));
			}
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public List<String> getArchivedUuidsSince(User user, Date since) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> event = cq.from(Event.class);

		Predicate filter = createUserFilter(cb, cq, event, user);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(event.get(Event.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(event.get(Event.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(event.get(Event.UUID));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Event,Event> eventPath, User user) {
		// National users can access all events in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)
				|| user.getUserRoles().contains(UserRole.NATIONAL_OBSERVER)) {
			return null;
		}

		// whoever created the event or is assigned to it is allowed to access it
		Predicate filter = cb.equal(eventPath.join(Event.REPORTING_USER, JoinType.LEFT), user);
		filter = cb.or(filter, cb.equal(eventPath.join(Event.SURVEILLANCE_OFFICER, JoinType.LEFT), user));

		// allow event access based on user role
		for (UserRole userRole : user.getUserRoles()) {
			switch (userRole) {
			case SURVEILLANCE_SUPERVISOR:
			case CONTACT_SUPERVISOR:
			case CASE_SUPERVISOR:
			case EVENT_OFFICER:
			case STATE_OBSERVER:
				// supervisors see all events of their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.REGION), user.getRegion()));
				}
				break;
			case SURVEILLANCE_OFFICER:
			case CONTACT_OFFICER:
			case CASE_OFFICER:
			case DISTRICT_OBSERVER:
				// officers see all events of their district
				if (user.getDistrict() != null) {
					filter = cb.or(filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.DISTRICT), user.getDistrict()));
				}
				break;
			case HOSPITAL_INFORMANT:
			case COMMUNITY_INFORMANT:
			case EXTERNAL_LAB_USER:
				// informants dont see events
				break;
			default:
				break;
			}
		}

		//		// events assigned with task
		//		Join<Event, Task> tasksJoin = from.join(Event.TASKS, JoinType.LEFT);
		//		filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));

		return filter;
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<Event, Event> eventPath, Date date) {

		Predicate dateFilter = cb.greaterThan(eventPath.get(AbstractDomainObject.CHANGE_DATE), date);

		Join<Event, Location> address = eventPath.join(Event.EVENT_LOCATION);
		dateFilter = cb.or(dateFilter, cb.greaterThan(address.get(AbstractDomainObject.CHANGE_DATE), date));

		return dateFilter;
	}


	public Predicate buildCriteriaFilter(EventCriteria eventCriteria, CriteriaBuilder cb, Root<Event> from) {
		Predicate filter = null;
		if (eventCriteria.getReportingUserRole() != null) {
			filter = and(cb, filter, cb.isMember(
					eventCriteria.getReportingUserRole(), 
					from.join(Event.REPORTING_USER, JoinType.LEFT).get(User.USER_ROLES)));
		}
		if (eventCriteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(from.get(Event.DISEASE), eventCriteria.getDisease()));
		}
		if (eventCriteria.getEventStatus() != null) {
			filter = and(cb, filter, cb.equal(from.get(Event.EVENT_STATUS), eventCriteria.getEventStatus()));
		}
		if (eventCriteria.getEventType() != null) {
			filter = and(cb, filter, cb.equal(from.get(Event.EVENT_TYPE), eventCriteria.getEventType()));
		}
		if (Boolean.TRUE.equals(eventCriteria.getArchived())) {
			filter = and(cb, filter, cb.equal(from.get(Event.ARCHIVED), true));
		} else {
			filter = and(cb, filter, cb.or(cb.equal(from.get(Event.ARCHIVED), false), cb.isNull(from.get(Event.ARCHIVED))));
		}
		return filter;
	}
}
