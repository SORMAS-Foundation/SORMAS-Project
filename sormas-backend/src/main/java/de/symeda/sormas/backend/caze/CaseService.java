package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class CaseService extends AbstractAdoService<Case> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public CaseService() {
		super(Case.class);
	}

	public Case createCase(Person person) {
		
		Case caze = new Case();
		caze.setPerson(person);
		return caze;
	}
	
	public List<Case> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, from, user);
		
		if (date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS);
			dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.get(AbstractDomainObject.CHANGE_DATE), date));
			Join<Case, Hospitalization> hospitalization = from.join(Case.HOSPITALIZATION);
			dateFilter = cb.or(dateFilter, cb.greaterThan(hospitalization.get(AbstractDomainObject.CHANGE_DATE), date));
			Join<Case, EpiData> epiData = from.join(Case.EPI_DATA);
			dateFilter = cb.or(dateFilter, cb.greaterThan(epiData.get(AbstractDomainObject.CHANGE_DATE), date));

			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(from.get(Case.REPORT_DATE)));
		cq.distinct(true);

		List<Case> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<Case> getAllBetween(Date fromDate, Date toDate, Disease disease, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());
		
		Predicate filter = createUserFilter(cb, from, user);
		Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS);
		Predicate dateFilter = cb.isNotNull(symptoms.get(Symptoms.ONSET_DATE));
		dateFilter = cb.and(dateFilter, cb.greaterThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), fromDate));
		dateFilter = cb.and(dateFilter, cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), toDate));
		
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}
		
		if (filter != null && disease != null) {
			filter = cb.and(filter, cb.equal(from.get(Case.DISEASE), disease));
		}
		
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.distinct(true);
		List<Case> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public Predicate createUserFilter(CriteriaBuilder cb, Path<Case> casePath, User user) {
		// whoever created the case or is assigned to it is allowed to access it
		Predicate filter = cb.equal(casePath.get(Case.REPORTING_USER), user);
		filter = cb.or(filter, cb.equal(casePath.get(Case.SURVEILLANCE_OFFICER), user));
		filter = cb.or(filter, cb.equal(casePath.get(Case.CONTACT_OFFICER), user));
		filter = cb.or(filter, cb.equal(casePath.get(Case.CASE_OFFICER), user));
		
		// allow case access based on user role
		for (UserRole userRole : user.getUserRoles()) {
			switch (userRole) {
			case SURVEILLANCE_SUPERVISOR:
			case CONTACT_SUPERVISOR:
			case CASE_SUPERVISOR:
				// supervisors see all cases of their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(casePath.get(Case.REGION), user.getRegion()));
				}
				break;
			case SURVEILLANCE_OFFICER:
			case CONTACT_OFFICER:
			case CASE_OFFICER:
				// officers see all cases of their district
				if (user.getDistrict() != null) {
					filter = cb.or(filter, cb.equal(casePath.get(Case.DISTRICT), user.getDistrict()));
				}
				break;
			case INFORMANT:
				// informants see all cases of their facility
				if (user.getHealthFacility() != null) {
					filter = cb.or(filter, cb.equal(casePath.get(Case.HEALTH_FACILITY), user.getHealthFacility()));
				}
				break;
			default:
				break;
			}
		}
		
//		// add cases for assigned tasks of the user
//		Join<Case, Task> tasksJoin = from.join(Case.TASKS, JoinType.LEFT);
//		filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));

		return filter;
	}
	
	 
	// TODO #69 create some date filter for finding the right case (this is implemented in CaseDao.java too)
	public Case getByPersonAndDisease(Disease disease, Person person, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Case.REPORTING_USER), user);
		if (user.getUserRoles().contains(UserRole.SURVEILLANCE_OFFICER)) {
			filter = cb.or(filter, cb.equal(from.get(Case.SURVEILLANCE_OFFICER), user));
		}
		
		filter = cb.and(filter, cb.equal(from.get(Case.DISEASE), disease));
		filter = cb.and(filter, cb.equal(from.get(Case.PERSON), person));
		
		if(filter != null) {
			cq.where(filter);
		}
		cq.distinct(true);
		
		try {
			Case result = em.createQuery(cq).getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.info(e.getMessage(), e);
			return null;
		}
	}	
}
