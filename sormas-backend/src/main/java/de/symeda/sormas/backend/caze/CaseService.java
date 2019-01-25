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
package de.symeda.sormas.backend.caze;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataBurial;
import de.symeda.sormas.backend.epidata.EpiDataGathering;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.epidata.EpiDataTravel;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class CaseService extends AbstractAdoService<Case> {

	@EJB
	ContactService contactService;
	@EJB
	SampleService sampleService;
	@EJB
	PersonFacadeEjbLocal personFacade;
	@EJB
	PersonService personService;
	@EJB
	EventParticipantService eventParticipantService;
	@EJB
	HospitalizationService hospitalizationService;
	@EJB
	EpiDataService epiDataService;
	@EJB
	UserService userService;

	public CaseService() {
		super(Case.class);
	}

	public Case createCase(Person person) {

		Case caze = new Case();
		caze.setPerson(person);
		return caze;
	}

	public List<Case> findBy(CaseCriteria caseCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(caseCriteria, cb, from);
		filter = and(cb, filter, createUserFilter(cb, cq, from, user));

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Case.CREATION_DATE)));

		List<Case> resultList = em.createQuery(cq).getResultList();
		return resultList;	
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

	public List<Case> getAllActiveCasesAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = cb.or(
				cb.equal(from.get(Case.ARCHIVED), false),
				cb.isNull(from.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			if (userFilter != null) {
				filter = cb.and(filter, userFilter);
			}
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			if (dateFilter != null) {
				filter = cb.and(filter, dateFilter);	
			}
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Case.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = cb.or(
				cb.equal(from.get(Case.ARCHIVED), false),
				cb.isNull(from.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Case.UUID));

		return em.createQuery(cq).getResultList();
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

	/**
	 * @param to will automatically be set to the end of the day
	 */
	public List<DashboardCaseDto> getNewCasesForDashboard(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardCaseDto> cq = cb.createQuery(DashboardCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, caze, user);

		// Onset date > reception date > report date (use report date as a fallback if none of the other dates is available)
		Predicate dateFilter = createNewCaseFilter(cb, caze, from, to, NewCaseDateType.MOST_RELEVANT);
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

		List<DashboardCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					caze.get(Case.REPORT_DATE),
					symptoms.get(Symptoms.ONSET_DATE),
					caze.get(Case.RECEPTION_DATE),
					caze.get(Case.CASE_CLASSIFICATION),
					caze.get(Case.DISEASE),
					caze.get(Case.INVESTIGATION_STATUS),
					person.get(Person.PRESENT_CONDITION),
					person.get(Person.CAUSE_OF_DEATH_DISEASE)
					);

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public List<MapCaseDto> getCasesForMap(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapCaseDto> cq = cb.createQuery(MapCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());
		Join<Case, Facility> facility = caze.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Person, Location> casePersonAddress = person.join(Person.ADDRESS, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, caze, user);
		Predicate dateFilter = createActiveCaseFilter(cb, caze, from, to);
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

		List<MapCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					caze.get(Case.UUID),
					caze.get(Case.REPORT_DATE),
					caze.get(Case.CASE_CLASSIFICATION),
					caze.get(Case.DISEASE),
					person.get(Person.UUID),
					person.get(Person.FIRST_NAME),
					person.get(Person.LAST_NAME),
					facility.get(Facility.UUID),
					facility.get(Facility.LATITUDE),
					facility.get(Facility.LONGITUDE),
					caze.get(Case.REPORT_LAT),
					caze.get(Case.REPORT_LON),
					casePersonAddress.get(Location.LATITUDE),
					casePersonAddress.get(Location.LONGITUDE)
					);

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public Map<CaseClassification, Long> getNewCaseCountPerClassification(CaseCriteria caseCriteria, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, caze, user);
		Predicate criteriaFilter = buildCriteriaFilter(caseCriteria, cb, caze);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(caze.get(Case.CASE_CLASSIFICATION));
		cq.multiselect(caze.get(Case.CASE_CLASSIFICATION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<CaseClassification, Long> resultMap = results.stream().collect(
				Collectors.toMap(e -> (CaseClassification) e[0], e -> (Long) e[1]));
		return resultMap;
	}

	public Map<PresentCondition, Long> getNewCaseCountPerPersonCondition(CaseCriteria caseCriteria, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(getElementClass());
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, caze, user);
		Predicate criteriaFilter = buildCriteriaFilter(caseCriteria, cb, caze);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(person.get(Person.PRESENT_CONDITION));
		cq.multiselect(person.get(Person.PRESENT_CONDITION), cb.count(caze));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<PresentCondition, Long> resultMap = results.stream().collect(
				Collectors.toMap(e -> (PresentCondition) e[0], e -> (Long) e[1]));
		return resultMap;
	}

	public Case getLatestCaseByPerson(Person person, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = createUserFilter(cb, cq, caze, user);
		Predicate personFilter = cb.equal(caze.get(Case.PERSON), person);
		if (filter != null) {
			filter = cb.and(filter, personFilter);
		} else {
			filter = personFilter;
		}
		cq.where(filter);

		List<Case> cases = em.createQuery(cq).getResultList();
		Optional<Case> latestCase = cases.stream()
				.sorted(new Comparator<Case> () {
					@Override
					public int compare(Case o1, Case o2) {
						if (CaseLogic.getStartDate(o1.getSymptoms().getOnsetDate(), o1.getReceptionDate(), o1.getReportDate()).after(CaseLogic.getStartDate(o2.getSymptoms().getOnsetDate(), o2.getReceptionDate(), o2.getReportDate()))) {
							return -1;
						}
						if (CaseLogic.getStartDate(o2.getSymptoms().getOnsetDate(), o2.getReceptionDate(), o2.getReportDate()).after(CaseLogic.getStartDate(o1.getSymptoms().getOnsetDate(), o1.getReceptionDate(), o1.getReportDate()))) {
							return 1;
						}
						return 0;
					}
				})
				.findFirst();

		return latestCase.isPresent() ? latestCase.get() : null;
	}

	public List<String> getArchivedUuidsSince(User user, Date since) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = createUserFilter(cb, cq, caze, user);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(caze.get(Case.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(caze.get(Case.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(caze.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Case,Case> casePath, User user) {
		// National users can access all cases in the system
		if (user == null
				|| user.getUserRoles().contains(UserRole.NATIONAL_USER)
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
			case EVENT_OFFICER:
			case STATE_OBSERVER:
				// supervisors see all cases of their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(casePath.get(Case.REGION), user.getRegion()));
				}
				break;
			case SURVEILLANCE_OFFICER:
			case CONTACT_OFFICER:
			case CASE_OFFICER:
			case DISTRICT_OBSERVER:
				// officers see all cases of their district
				if (user.getDistrict() != null) {
					filter = cb.or(filter, cb.equal(casePath.get(Case.DISTRICT), user.getDistrict()));
				}
				break;
			case HOSPITAL_INFORMANT:
				// hospital informants see all cases of their facility
				if (user.getHealthFacility() != null) {
					filter = cb.or(filter, cb.equal(casePath.get(Case.HEALTH_FACILITY), user.getHealthFacility()));
				}
				break;
			case COMMUNITY_INFORMANT:
				// community informants see all cases of their community
				if (user.getCommunity() != null) {
					filter = cb.or(filter, cb.equal(casePath.get(Case.COMMUNITY), user.getCommunity()));
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
			case EXTERNAL_LAB_USER:
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
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<Case,Case> casePath, Date date) {

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

	/**
	 * A case is considered active when the time span between onset/reception/report date and outcome date overlaps
	 * the time span defined by the fromDate and toDate.
	 */
	public Predicate createActiveCaseFilter(CriteriaBuilder cb, Root<Case> from, Date fromDate, Date toDate) {
		Predicate dateFromFilter = null;
		Predicate dateToFilter = null;
		if (fromDate != null) {
			dateFromFilter = cb.or(
					cb.isNull(from.get(Case.OUTCOME_DATE)),
					cb.greaterThanOrEqualTo(from.get(Case.OUTCOME_DATE), fromDate)
					);
		}
		if (toDate != null) {
			// Onset date > reception date > report date (use report date as a fallback if none of the other dates is available)
			Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS, JoinType.LEFT);
			dateToFilter = cb.or(
					cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), toDate), 
					cb.and(
							cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), 
							cb.lessThanOrEqualTo(from.get(Case.RECEPTION_DATE), toDate)
							),
					cb.and(
							cb.isNull(symptoms.get(Symptoms.ONSET_DATE)),
							cb.isNull(from.get(Case.RECEPTION_DATE)),
							cb.lessThanOrEqualTo(from.get(Case.REPORT_DATE), toDate)
							)
					);
		}

		if (dateFromFilter != null && dateToFilter != null) {
			return cb.and(dateFromFilter, dateToFilter);			
		} else {
			return dateFromFilter != null ? dateFromFilter : dateToFilter != null ? dateToFilter : null;
		}
	}

	/**
	 * @param toDate will automatically be set to the end of the day
	 */
	public Predicate createNewCaseFilter(CriteriaBuilder cb, Root<Case> caze, Date fromDate, Date toDate, NewCaseDateType newCaseDateType) {
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);

		toDate = DateHelper.getEndOfDay(toDate);

		Predicate onsetDateFilter = cb.between(symptoms.get(Symptoms.ONSET_DATE), fromDate, toDate);
		Predicate receptionDateFilter = cb.between(caze.get(Case.RECEPTION_DATE), fromDate, toDate);
		Predicate reportDateFilter = cb.between(caze.get(Case.REPORT_DATE), fromDate, toDate);

		Predicate newCaseFilter = null;
		if (newCaseDateType == NewCaseDateType.MOST_RELEVANT || newCaseDateType == null) {
			newCaseFilter = cb.or(
					onsetDateFilter,
					cb.and(
							cb.isNull(symptoms.get(Symptoms.ONSET_DATE)),
							receptionDateFilter),
					cb.and(
							cb.isNull(symptoms.get(Symptoms.ONSET_DATE)),
							cb.isNull(caze.get(Case.RECEPTION_DATE)),
							reportDateFilter)
					);
		} else if (newCaseDateType == NewCaseDateType.ONSET) {
			newCaseFilter = onsetDateFilter;
		} else if (newCaseDateType == NewCaseDateType.RECEPTION) {
			newCaseFilter = receptionDateFilter;
		} else {
			newCaseFilter = reportDateFilter;
		}

		return newCaseFilter;
	}

	public Predicate buildCriteriaFilter(CaseCriteria caseCriteria, CriteriaBuilder cb, Root<Case> from) {
		Join<Case, Person> person = from.join(Case.PERSON, JoinType.LEFT);
		Predicate filter = null;
		if (caseCriteria.getReportingUserRole() != null) {
			filter = and(cb, filter, cb.isMember(
					caseCriteria.getReportingUserRole(), 
					from.join(Case.REPORTING_USER, JoinType.LEFT).get(User.USER_ROLES)));
		}
		if (caseCriteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.DISEASE), caseCriteria.getDisease()));
		}
		if (caseCriteria.getOutcome() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.OUTCOME), caseCriteria.getOutcome()));
		}
		if (caseCriteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.REGION, JoinType.LEFT).get(Region.UUID), caseCriteria.getRegion().getUuid()));
		}
		if (caseCriteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID), caseCriteria.getDistrict().getUuid()));
		}
		if (caseCriteria.getHealthFacility() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.HEALTH_FACILITY, JoinType.LEFT).get(Facility.UUID), caseCriteria.getHealthFacility().getUuid()));
		}
		if (caseCriteria.getSurveillanceOfficer() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT).get(User.UUID), caseCriteria.getSurveillanceOfficer().getUuid()));
		}
		if (caseCriteria.getCaseClassification() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.CASE_CLASSIFICATION), caseCriteria.getCaseClassification()));
		}
		if (caseCriteria.getInvestigationStatus() != null) {
			filter = and(cb, filter, cb.equal(from.get(Case.INVESTIGATION_STATUS), caseCriteria.getInvestigationStatus()));
		}
		if (caseCriteria.getPresentCondition() != null) {
			filter = and(cb, filter, cb.equal(person.get(Person.PRESENT_CONDITION), caseCriteria.getPresentCondition()));
		}
		if (caseCriteria.getNewCaseDateFrom() != null && caseCriteria.getNewCaseDateTo() != null) {
			filter = and(cb, filter, createNewCaseFilter(cb, from, caseCriteria.getNewCaseDateFrom(), caseCriteria.getNewCaseDateTo(), caseCriteria.getNewCaseDateType()));
		}
		if (caseCriteria.getPerson() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.PERSON, JoinType.LEFT).get(Person.UUID), caseCriteria.getPerson().getUuid()));
		}
		if (caseCriteria.isMustHaveNoGeoCoordinates() != null && caseCriteria.isMustHaveNoGeoCoordinates() == true) {
			Join<Person, Location> personAddress = person.join(Person.ADDRESS, JoinType.LEFT);
			filter = and(cb, filter, 
					cb.and(
							cb.or(
									cb.isNull(from.get(Case.REPORT_LAT)), 
									cb.isNull(from.get(Case.REPORT_LON))), 
							cb.or(
									cb.isNull(personAddress.get(Location.LATITUDE)), 
									cb.isNull(personAddress.get(Location.LONGITUDE)))
							)
					);
		}
		if (Boolean.TRUE.equals(caseCriteria.getArchived())) {
			filter = and(cb, filter, cb.equal(from.get(Case.ARCHIVED), true));
		} else {
			filter = and(cb, filter, cb.or(cb.equal(from.get(Case.ARCHIVED), false), cb.isNull(from.get(Case.ARCHIVED))));
		}

		if (caseCriteria.getNameUuidEpidNumberLike() != null) {
			String[] textFilters = caseCriteria.getNameUuidEpidNumberLike().split("\\s+");
			for (int i=0; i<textFilters.length; i++)
			{
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(person.get(Person.FIRST_NAME)), textFilter),
							cb.like(cb.lower(person.get(Person.LAST_NAME)), textFilter),
							cb.like(cb.lower(from.get(Case.UUID)), textFilter),
							cb.like(cb.lower(from.get(Case.EPID_NUMBER)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		return filter;
	}

	/**
	 * E.g. to be used to find a resulting case for a contact.
	 * @see ContactService#udpateContactStatusAndResultingCase
	 */
	public Case getFirstByPersonDiseaseAndOnset(Disease disease, Person person, Date onsetBetweenStart, Date onsetBetweenEnd) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter =  cb.equal(from.get(Case.PERSON), person);
		if (disease != null) {
			filter = cb.and(filter, cb.equal(from.get(Case.DISEASE), disease));
		}

		// Onset date > reception date > report date (use report date as a fallback if none of the other dates is available)
		Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS, JoinType.LEFT);
		Predicate onsetFilter = cb.or(
				cb.between(symptoms.get(Symptoms.ONSET_DATE), onsetBetweenStart, onsetBetweenEnd), 
				cb.and(
						cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), 
						cb.between(from.get(Case.RECEPTION_DATE), onsetBetweenStart, onsetBetweenEnd)
						),
				cb.and(
						cb.isNull(symptoms.get(Symptoms.ONSET_DATE)),
						cb.isNull(from.get(Case.RECEPTION_DATE)),
						cb.between(from.get(Case.REPORT_DATE), onsetBetweenStart, onsetBetweenEnd)
						)
				);
		filter = cb.and(filter, onsetFilter);

		cq.where(filter);

		cq.orderBy(cb.asc(symptoms.get(Symptoms.ONSET_DATE)), cb.asc(from.get(Case.RECEPTION_DATE)), cb.asc(from.get(Case.REPORT_DATE)));

		List<Case> resultList = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!resultList.isEmpty())
			return resultList.get(0);
		return null;
	}

	/**
	 * @return Latest case of the person that "starts" before date
	 * and has not outcome yet or and outcome after date 
	 */
	public Case getLastActiveByPersonDiseaseAtDate(Disease disease, Person person, Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter =  cb.equal(from.get(Case.PERSON), person);
		if (disease != null) {
			filter = cb.and(filter, cb.equal(from.get(Case.DISEASE), disease));
		}

		// all cases that have no outcome yet or outcome date after the date
		filter = cb.and(filter, createActiveCaseFilter(cb, from, date, date));

		cq.where(filter);

		Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS, JoinType.LEFT);
		cq.orderBy(cb.desc(symptoms.get(Symptoms.ONSET_DATE)), cb.desc(from.get(Case.RECEPTION_DATE)), cb.desc(from.get(Case.REPORT_DATE)));

		List<Case> resultList = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!resultList.isEmpty())
			return resultList.get(0);
		return null;
	}
}
