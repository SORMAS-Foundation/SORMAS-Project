package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataBurial;
import de.symeda.sormas.backend.epidata.EpiDataGathering;
import de.symeda.sormas.backend.epidata.EpiDataTravel;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class CaseService extends AbstractAdoService<Case> {
	
	@EJB
	ContactService contactService;
	@EJB
	SampleService sampleService;
	
	public CaseService() {
		super(Case.class);
	}

	public Case createCase(Person person) {
		
		Case caze = new Case();
		caze.setPerson(person);
		return caze;
	}

	public List<Case> getAllByDisease(Disease disease, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from, user);
		
		if (filter != null && disease != null) {
			filter = cb.and(filter, cb.equal(from.get(Case.DISEASE), disease));
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(from.get(Case.REPORT_DATE)));
		cq.distinct(true);

		List<Case> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<Case> getAllBetween(Date onsetFromDate, Date onsetToDate, Disease disease, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());
		
		Predicate filter = createUserFilter(cb, cq, from, user);
		if (onsetFromDate != null || onsetToDate != null) {
			Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS);
			Predicate dateFilter = cb.isNotNull(symptoms.get(Symptoms.ONSET_DATE));
			if (onsetFromDate != null) {
				dateFilter = cb.and(dateFilter, cb.greaterThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), onsetFromDate));
			}
			if (onsetToDate != null) {
				dateFilter = cb.and(dateFilter, cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), onsetToDate));
			}
			filter = dateFilter;
		}
		if (disease != null) {
			Predicate diseaseFilter = cb.equal(from.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
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
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Case,Case> casePath, User user) {
		// National users can access all cases in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)) {
			return null;
		}
		
		// whoever created the case or is assigned to it is allowed to access it
		Predicate filter = cb.equal(casePath.get(Case.REPORTING_USER), user);
		filter = cb.or(filter, cb.equal(casePath.get(Case.SURVEILLANCE_OFFICER), user));
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
			case LAB_USER:
				// get all cases based on the user's sample association
				Subquery<Long> sampleCaseSubquery = cq.subquery(Long.class);
				Root<Sample> sampleRoot = sampleCaseSubquery.from(Sample.class);
				sampleCaseSubquery.where(sampleService.createUserFilterWithoutCase(cb, cq, sampleRoot, user));
				sampleCaseSubquery.select(sampleRoot.get(Sample.ASSOCIATED_CASE).get(Case.ID));
				filter = cb.in(casePath.get(Case.ID)).value(sampleCaseSubquery);
				break;
			case ADMIN:
				break;
				
			default:
				throw new IllegalArgumentException(userRole.toString());
			}
		}
		
		// get all cases based on the user's contact association
		Subquery<Long> contactCaseSubquery = cq.subquery(Long.class);
		Root<Contact> contactRoot = contactCaseSubquery.from(Contact.class);
		contactCaseSubquery.where(contactService.createUserFilterWithoutCase(cb, cq, contactRoot, user));
		contactCaseSubquery.select(contactRoot.get(Contact.CAZE).get(Case.ID));
		
		filter = cb.or(filter, cb.in(casePath.get(Case.ID)).value(contactCaseSubquery));
		
		// users can only be assigned to a task when they have also access to the case
		//Join<Case, Task> tasksJoin = from.join(Case.TASKS, JoinType.LEFT);
		//filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));

		return filter;
	}
	
	@Override
	public Predicate createDateFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Case,Case> casePath, Date date) {
		
		Predicate dateFilter = cb.greaterThan(casePath.get(Case.CHANGE_DATE), date);
		
		Join<Case, Symptoms> symptoms = casePath.join(Case.SYMPTOMS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.get(AbstractDomainObject.CHANGE_DATE), date));
		
		Join<Case, Hospitalization> hospitalization = casePath.join(Case.HOSPITALIZATION, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(hospitalization.get(AbstractDomainObject.CHANGE_DATE), date));
		
		Join<Hospitalization, PreviousHospitalization> previousHospitalization 
			= hospitalization.join(Hospitalization.PREVIOUS_HOSPITALIZATIONS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(previousHospitalization.get(AbstractDomainObject.CHANGE_DATE), date));
		
		Join<Case, EpiData> epiData = casePath.join(Case.EPI_DATA, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(epiData.get(AbstractDomainObject.CHANGE_DATE), date));
		
		Join<EpiData, EpiDataTravel> epiDataTravels = epiData.join(EpiData.TRAVELS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(epiDataTravels.get(AbstractDomainObject.CHANGE_DATE), date));
		
		Join<EpiData, EpiDataBurial> epiDataBurials = epiData.join(EpiData.BURIALS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(epiDataBurials.get(AbstractDomainObject.CHANGE_DATE), date));
		dateFilter = cb.or(dateFilter, cb.greaterThan(epiDataBurials.join(EpiDataBurial.BURIAL_ADDRESS, JoinType.LEFT).get(Location.CHANGE_DATE), date));
		
		Join<EpiData, EpiDataGathering> epiDataGatherings = epiData.join(EpiData.GATHERINGS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(epiDataGatherings.get(AbstractDomainObject.CHANGE_DATE), date));
		dateFilter = cb.or(dateFilter, cb.greaterThan(epiDataGatherings.join(EpiDataGathering.GATHERING_ADDRESS, JoinType.LEFT).get(Location.CHANGE_DATE), date));
	
		return dateFilter;
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
			return null;
		}
	}	
	
	public Map<RegionReferenceDto, Long> getCaseCountPerRegion(Date onsetFromDate, Date onsetToDate, Disease disease) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> from = cq.from(getElementClass());
		
		Predicate filter = null;		
		if (onsetFromDate != null || onsetToDate != null) {
			Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS);
			Predicate dateFilter = cb.isNotNull(symptoms.get(Symptoms.ONSET_DATE));
			if (onsetFromDate != null) {
				dateFilter = cb.and(dateFilter, cb.greaterThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), onsetFromDate));
			}
			if (onsetToDate != null) {
				dateFilter = cb.and(dateFilter, cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), onsetToDate));
			}
			filter = dateFilter;
		}
		if (disease != null) {
			Predicate diseaseFilter = cb.equal(from.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}		
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.groupBy(from.get(Case.REGION));
		cq.multiselect(from.get(Case.REGION), cb.count(from));
		List<Object[]> results = em.createQuery(cq).getResultList();
		
		Map<RegionReferenceDto, Long> resultMap = results.stream().collect(
				Collectors.toMap(e -> RegionFacadeEjb.toReferenceDto((Region)e[0]), e -> (Long)e[1]));
		return resultMap;
	}
}
