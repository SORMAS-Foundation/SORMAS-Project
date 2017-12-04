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
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EventService extends AbstractAdoService<Event> {

	@EJB
	DistrictFacadeEjbLocal districtFacade;

	public EventService() {
		super(Event.class);
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

	public List<DashboardEventDto> getNewEventsForDashboard(District district, Disease disease, Date from, Date to, User user) {
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

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
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
				// supervisors see all events of their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.REGION), user.getRegion()));
				}
				break;
			case SURVEILLANCE_OFFICER:
			case CONTACT_OFFICER:
			case CASE_OFFICER:
				// officers see all events of their district
				if (user.getDistrict() != null) {
					filter = cb.or(filter, cb.equal(eventPath.join(Event.EVENT_LOCATION, JoinType.LEFT).get(Location.DISTRICT), user.getDistrict()));
				}
				break;
			case INFORMANT:
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
	public Predicate createDateFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Event, Event> eventPath, Date date) {

		Predicate dateFilter = cb.greaterThan(eventPath.get(AbstractDomainObject.CHANGE_DATE), date);

		Join<Event, Location> address = eventPath.join(Event.EVENT_LOCATION);
		dateFilter = cb.or(dateFilter, cb.greaterThan(address.get(AbstractDomainObject.CHANGE_DATE), date));

		return dateFilter;
	}
}
