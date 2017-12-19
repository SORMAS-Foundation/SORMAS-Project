package de.symeda.sormas.backend.caze;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.StatisticsCaseDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataBurial;
import de.symeda.sormas.backend.epidata.EpiDataGathering;
import de.symeda.sormas.backend.epidata.EpiDataTravel;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
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
	@EJB
	PersonFacadeEjbLocal personFacade;

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

	public List<Case> getAllBetween(Date onsetFromDate, Date onsetToDate, District district, Disease disease, User user) {
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
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}
		if (district != null) {
			Predicate districtFilter = cb.equal(from.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
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

	public List<DashboardCaseDto> getNewCasesForDashboard(District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardCaseDto> cq = cb.createQuery(DashboardCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, caze, user);

		// Use the onset date if available and the report date otherwise
		Predicate dateFilter = cb.or(
				cb.between(symptoms.get(Symptoms.ONSET_DATE), from, to), 
				cb.and(
						cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), 
						cb.between(caze.get(Case.REPORT_DATE), from, to)
						)
				);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		List<DashboardCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					caze.get(Case.REPORT_DATE),
					symptoms.get(Symptoms.ONSET_DATE),
					caze.get(Case.CASE_CLASSIFICATION),
					caze.get(Case.DISEASE),
					caze.get(Case.INVESTIGATION_STATUS),
					person.get(Person.PRESENT_CONDITION)
					);

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public List<MapCaseDto> getCasesForMap(District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapCaseDto> cq = cb.createQuery(MapCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Facility> facility = caze.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Person, Location> casePersonAddress = person.join(Person.ADDRESS, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, caze, user);

		// Use the onset date if available and the report date otherwise
		// TODO Add date of outcome to the date filter once it is built in
		Predicate dateFilter = cb.or(
				cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), to), 
				cb.and(
						cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), 
						cb.lessThanOrEqualTo(caze.get(Case.REPORT_DATE), to)
						)
				);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		List<MapCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					caze.get(Case.UUID),
					caze.get(Case.REPORT_DATE),
					caze.get(Case.CASE_CLASSIFICATION),
					caze.get(Case.DISEASE),
					facility.get(Facility.UUID),
					person.get(Person.UUID),
					caze.get(Case.REPORT_LAT),
					caze.get(Case.REPORT_LON),
					casePersonAddress.get(Location.LATITUDE),
					casePersonAddress.get(Location.LONGITUDE)
					);

			result = em.createQuery(cq).getResultList();
			for (MapCaseDto mapCaseDto : result) {
				mapCaseDto.setPerson(personFacade.getReferenceByUuid(mapCaseDto.getPersonUuid()));
			}
		} else {
			result = Collections.emptyList();
		}

		return result;
	}
	
	// TODO use aggregating query instead of a list of DTOs
	public List<StatisticsCaseDto> getCasesForStatistics(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticsCaseDto> cq = cb.createQuery(StatisticsCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);	
		
		Predicate filter = createUserFilter(cb, cq, caze, user);

		// Use the onset date if available and the report date otherwise
		// TODO Add date of outcome to the date filter once it is built in
		Predicate dateFilter = cb.or(
				cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), to), 
				cb.and(
						cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), 
						cb.lessThanOrEqualTo(caze.get(Case.REPORT_DATE), to)
						)
				);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}
		
		if (region != null) {
			Predicate regionFilter = cb.equal(caze.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		List<StatisticsCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					person.get(Person.APPROXIMATE_AGE),
					person.get(Person.SEX)
					);

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Case,Case> casePath, User user) {
		// National users can access all cases in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)
				|| user.getUserRoles().contains(UserRole.NATIONAL_OBSERVER)) {
			return null;
		}

		// whoever created the case or is assigned to it is allowed to access it
		Predicate filter = cb.equal(casePath.join(Case.REPORTING_USER, JoinType.LEFT), user);
		filter = cb.or(filter, cb.equal(casePath.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT), user));
		filter = cb.or(filter, cb.equal(casePath.join(Case.CASE_OFFICER, JoinType.LEFT), user));

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
	
	public Predicate buildCriteriaFilter(CaseCriteria caseCriteria, CriteriaBuilder cb, Root<Case> from) {
		Predicate filter = null;
		if (caseCriteria.getReportingUserRole() != null) {
			filter = and(cb, filter, cb.isMember(
					caseCriteria.getReportingUserRole(), 
					from.join(Case.REPORTING_USER, JoinType.LEFT).get(User.USER_ROLES)));
		}
		if (caseCriteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.DISEASE), caseCriteria.getDisease()));
		}
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
			return null;
		}
	}
}
