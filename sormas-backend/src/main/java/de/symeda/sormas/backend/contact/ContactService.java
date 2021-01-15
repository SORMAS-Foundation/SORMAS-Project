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
package de.symeda.sormas.backend.contact;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.DashboardContactDto;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.clinicalcourse.HealthConditionsService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.exposure.ExposureService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitService;
import de.symeda.sormas.utils.CaseJoins;

@Stateless
@LocalBean
public class ContactService extends AbstractCoreAdoService<Contact> {

	@EJB
	private CaseService caseService;
	@EJB
	private VisitService visitService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private TaskService taskService;
	@EJB
	private SampleService sampleService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private HealthConditionsService healthConditionsService;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private ContactJurisdictionChecker contactJurisdictionChecker;
	@EJB
	private ExposureService exposureService;

	public ContactService() {
		super(Contact.class);
	}

	public List<Contact> findBy(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());
		ContactJoins joins = new ContactJoins(from);

		Predicate filter = buildCriteriaFilter(contactCriteria, cb, from, joins);

		if (user != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Contact.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<Contact> getAllActiveContactsAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = createActiveContactsFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Contact.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Contact> from, Date date) {
		return createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Contact> from, Timestamp date) {

		Predicate dateFilter = changeDateFilter(cb, date, from);
		dateFilter = cb.or(dateFilter, epiDataService.createChangeDateFilter(cb, from.join(Contact.EPI_DATA, JoinType.LEFT), date));
		dateFilter = cb.or(dateFilter, healthConditionsService.createChangeDateFilter(cb, from.join(Contact.HEALTH_CONDITIONS, JoinType.LEFT), date));
		dateFilter = cb.or(dateFilter, changeDateFilter(cb, date, from, Contact.SORMAS_TO_SORMAS_SHARES));

		return dateFilter;
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = createActiveContactsFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Contact.UUID));

		return em.createQuery(cq).getResultList();
	}

	public int getContactCountByCase(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> from = cq.from(getElementClass());

		cq.select(cb.count(from));
		cq.where(cb.and(createDefaultFilter(cb, from), cb.equal(from.get(Contact.CAZE), caze)));

		return em.createQuery(cq).getSingleResult().intValue();
	}

	public List<Contact> getAllByResultingCase(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		cq.where(cb.and(createDefaultFilter(cb, from), cb.equal(from.get(Contact.RESULTING_CASE), caze)));
		cq.orderBy(cb.desc(from.get(Contact.REPORT_DATE_TIME)));

		return em.createQuery(cq).getResultList();
	}

	public List<Object[]> getSourceCaseClassifications(List<Long> caseIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contactRoot = cq.from(getElementClass());
		Join<Contact, Case> rootCaseJoin = contactRoot.join(Contact.CAZE);
		Join<Contact, Case> resultingCaseJoin = contactRoot.join(Contact.RESULTING_CASE);

		cq.multiselect(resultingCaseJoin.get(Case.ID), rootCaseJoin.get(Case.CASE_CLASSIFICATION));

		Expression<String> caseIdsExpression = resultingCaseJoin.get(Case.ID);
		cq.where(cb.and(createDefaultFilter(cb, contactRoot), caseIdsExpression.in(caseIds), cb.isNotNull(contactRoot.get(Contact.CAZE))));

		return em.createQuery(cq).getResultList();
	}

	public List<Contact> getFollowUpBetween(@NotNull Date fromDate, @NotNull Date toDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = createActiveContactsFilter(cb, from);
		filter = cb.and(filter, cb.isNotNull(from.get(Contact.FOLLOW_UP_UNTIL)));
		filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), fromDate));
		filter = cb.and(
			filter,
			cb.or(
				cb.and(cb.isNotNull(from.get(Contact.LAST_CONTACT_DATE)), cb.lessThan(from.get(Contact.LAST_CONTACT_DATE), toDate)),
				cb.lessThan(from.get(Contact.REPORT_DATE_TIME), toDate)));

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	public List<Contact> getByPersonAndDisease(Person person, Disease disease) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);
		filter = cb.and(filter, cb.equal(from.get(Contact.PERSON), person));
		filter = cb.and(filter, cb.equal(from.get(Contact.DISEASE), disease));
		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	public Set<Contact> getAllRelevantContacts(Person person, Disease disease, Date referenceDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(Contact.class);
		Root<Contact> contactRoot = cq.from(Contact.class);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, createDefaultFilter(cb, contactRoot), buildRelevantContactsFilter(person, disease, referenceDate, cb, contactRoot));
		cq.where(filter);

		return new HashSet<>(em.createQuery(cq).getResultList());
	}

	/**
	 * Returns a filter that can be used to retrieve all contacts with the specified
	 * person and disease whose last contact date or report date (depending on
	 * availability) is before the reference date and, if available, whose follow-up
	 * until date is after the reference date, including an offset to allow some
	 * tolerance.
	 */
	private Predicate buildRelevantContactsFilter(Person person, Disease disease, Date referenceDate, CriteriaBuilder cb, Root<Contact> from) {

		Date referenceDateStart = DateHelper.getStartOfDay(referenceDate);
		Date referenceDateEnd = DateHelper.getEndOfDay(referenceDate);

		Predicate filter = CriteriaBuilderHelper.and(cb, cb.equal(from.get(Contact.PERSON), person), cb.equal(from.get(Contact.DISEASE), disease));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.or(
				cb,
				CriteriaBuilderHelper.and(
					cb,
					cb.isNull(from.get(Contact.LAST_CONTACT_DATE)),
					cb.lessThanOrEqualTo(
						from.get(Contact.REPORT_DATE_TIME),
						DateHelper.addDays(referenceDateEnd, FollowUpLogic.ALLOWED_DATE_OFFSET))),
				cb.lessThanOrEqualTo(from.get(Contact.LAST_CONTACT_DATE), DateHelper.addDays(referenceDateEnd, FollowUpLogic.ALLOWED_DATE_OFFSET))));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.or(
				cb,
				// If the contact does not have a follow-up until date, use the last
				// contact/contact report date as a fallback
				CriteriaBuilderHelper.and(
					cb,
					cb.isNull(from.get(Contact.FOLLOW_UP_UNTIL)),
					CriteriaBuilderHelper.or(
						cb,
						CriteriaBuilderHelper.and(
							cb,
							cb.isNull(from.get(Contact.LAST_CONTACT_DATE)),
							cb.greaterThanOrEqualTo(
								from.get(Contact.REPORT_DATE_TIME),
								DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))),
						cb.greaterThanOrEqualTo(
							from.get(Contact.LAST_CONTACT_DATE),
							DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET)))),
				cb.greaterThanOrEqualTo(
					from.get(Contact.FOLLOW_UP_UNTIL),
					DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))));

		return filter;
	}

	public List<String> getDeletedUuidsSince(User user, Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Contact> contact = cq.from(Contact.class);

		Predicate filter = createUserFilter(cb, cq, contact);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(contact.get(Contact.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(contact.get(Contact.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(contact.get(Contact.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Long countContactsForMap(Region region, District district, Disease disease, List<String> caseUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> contact = cq.from(getElementClass());
		Join<Contact, Case> caze = contact.join(Contact.CAZE, JoinType.LEFT);

		Predicate filter = createMapContactsFilter(cb, cq, contact, caze, region, district, disease, caseUuids);

		if (filter != null) {
			cq.where(filter);
			cq.select(cb.count(caze.get(Case.ID)));

			return em.createQuery(cq).getSingleResult();
		}

		return 0L;
	}

	public List<MapContactDto> getContactsForMap(Region region, District district, Disease disease, List<String> caseUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapContactDto> cq = cb.createQuery(MapContactDto.class);
		Root<Contact> contact = cq.from(getElementClass());
		Join<Contact, Person> person = contact.join(Contact.PERSON, JoinType.LEFT);
		Join<Person, Location> contactPersonAddress = person.join(Person.ADDRESS, JoinType.LEFT);
		Join<Contact, Case> caze = contact.join(Contact.CAZE, JoinType.LEFT);
		Join<Case, Person> casePerson = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);

		Predicate filter = createMapContactsFilter(cb, cq, contact, caze, region, district, disease, caseUuids);

		List<MapContactDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				contact.get(Contact.UUID),
				contact.get(Contact.CONTACT_CLASSIFICATION),
				contact.get(Contact.REPORT_LAT),
				contact.get(Contact.REPORT_LON),
				contactPersonAddress.get(Location.LATITUDE),
				contactPersonAddress.get(Location.LONGITUDE),
				symptoms.get(Symptoms.ONSET_DATE),
				caze.get(Case.REPORT_DATE),
				contact.get(Contact.REPORT_DATE_TIME),
				person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME),
				casePerson.get(Person.FIRST_NAME),
				casePerson.get(Person.LAST_NAME));

			result = em.createQuery(cq).getResultList();
			// #1274 Temporarily disabled because it severely impacts the performance of the
			// Dashboard
			// for (MapContactDto mapContactDto : result) {
			// Visit lastVisit =
			// visitService.getLastVisitByContact(getByUuid(mapContactDto.getUuid()),
			// VisitStatus.COOPERATIVE);
			// if (lastVisit != null) {
			// mapContactDto.setLastVisitDateTime(lastVisit.getVisitDateTime());
			// }
			// }
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	private Predicate createMapContactsFilter(
		CriteriaBuilder cb,
		CriteriaQuery<?> cq,
		Root<Contact> contactRoot,
		Join<Contact, Case> cazeJoin,
		Region region,
		District district,
		Disease disease,
		List<String> caseUuids) {
		Predicate filter = createActiveContactsFilter(cb, contactRoot);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, contactRoot));

		if (!CollectionUtils.isEmpty(caseUuids)) {
			Path<Object> contactCaseUuid = contactRoot.get(Contact.CAZE).get(Case.UUID);
			Predicate caseFilter = cb.or(cb.isNull(contactRoot.get(Contact.CAZE)), contactCaseUuid.in(caseUuids));
			if (filter != null) {
				filter = cb.and(filter, caseFilter);
			} else {
				filter = caseFilter;
			}
		} else {
			Predicate contactWithoutCaseFilter = cb.isNull(contactRoot.get(Contact.CAZE));
			if (filter != null) {
				filter = cb.and(filter, contactWithoutCaseFilter);
			} else {
				filter = contactWithoutCaseFilter;
			}
		}

		filter = CriteriaBuilderHelper.and(cb, filter, getRegionDistrictDiseasePredicate(region, district, disease, cb, contactRoot, cazeJoin));

		// Only retrieve contacts that are currently under follow-up
		Predicate followUpFilter = cb.equal(contactRoot.get(Contact.FOLLOW_UP_STATUS), FollowUpStatus.FOLLOW_UP);
		if (filter != null) {
			filter = cb.and(filter, followUpFilter);
		} else {
			filter = followUpFilter;
		}

		return filter;
	}

	public List<DashboardContactDto> getContactsForDashboard(Region region, District district, Disease disease, Date from, Date to, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardContactDto> cq = cb.createQuery(DashboardContactDto.class);
		Root<Contact> contact = cq.from(getElementClass());
		Join<Contact, Case> caze = contact.join(Contact.CAZE, JoinType.LEFT);

		Predicate filter = createDefaultFilter(cb, contact);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, contact));

		Predicate dateFilter = buildDateFilter(cb, contact, from, to);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		filter = CriteriaBuilderHelper.and(cb, filter, getRegionDistrictDiseasePredicate(region, district, disease, cb, contact, caze));

		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				contact.get(AbstractDomainObject.ID),
				contact.get(Contact.REPORT_DATE_TIME),
				contact.get(Contact.CONTACT_STATUS),
				contact.get(Contact.CONTACT_CLASSIFICATION),
				contact.get(Contact.FOLLOW_UP_STATUS),
				contact.get(Contact.FOLLOW_UP_UNTIL),
				contact.get(Contact.DISEASE));

			List<DashboardContactDto> dashboardContacts = em.createQuery(cq).getResultList();

			if (!dashboardContacts.isEmpty()) {
				List<Long> dashboardContactIds = dashboardContacts.stream().map(d -> d.getId()).collect(Collectors.toList());

				CriteriaQuery<DashboardVisit> visitsCq = cb.createQuery(DashboardVisit.class);
				Root<Contact> visitsCqRoot = visitsCq.from(getElementClass());
				Join<Contact, Visit> visitsJoin = visitsCqRoot.join(Contact.VISITS, JoinType.LEFT);
				Join<Visit, Symptoms> visitSymptomsJoin = visitsJoin.join(Visit.SYMPTOMS, JoinType.LEFT);

				visitsCq.where(
					CriteriaBuilderHelper
						.and(cb, contact.get(AbstractDomainObject.ID).in(dashboardContactIds), cb.isNotEmpty(visitsCqRoot.get(Contact.VISITS))));
				visitsCq.multiselect(
					visitsCqRoot.get(AbstractDomainObject.ID),
					visitSymptomsJoin.get(Symptoms.SYMPTOMATIC),
					visitsJoin.get(Visit.VISIT_STATUS),
					visitsJoin.get(Visit.VISIT_DATE_TIME));

				List<DashboardVisit> contactVisits = em.createQuery(visitsCq).getResultList();

				// Add visit information to the DashboardContactDtos
				for (DashboardContactDto dashboardContact : dashboardContacts) {
					List<DashboardVisit> visits =
						contactVisits.stream().filter(v -> v.getContactId() == dashboardContact.getId()).collect(Collectors.toList());

					DashboardVisit lastVisit = visits.stream().max((v1, v2) -> v1.getVisitDateTime().compareTo(v2.getVisitDateTime())).orElse(null);

					if (lastVisit != null) {
						dashboardContact.setLastVisitDateTime(lastVisit.getVisitDateTime());
						dashboardContact.setLastVisitStatus(lastVisit.getVisitStatus());
						dashboardContact.setSymptomatic(lastVisit.isSymptomatic());
						dashboardContact
							.setVisitStatusMap(visits.stream().collect(Collectors.groupingBy(DashboardVisit::getVisitStatus, Collectors.counting())));
					}
				}

				return dashboardContacts;
			}
		}

		return Collections.emptyList();
	}

	public Predicate getRegionDistrictDiseasePredicate(
		Region region,
		District district,
		Disease disease,
		CriteriaBuilder cb,
		Root<Contact> contact,
		Join<Contact, Case> caze) {

		Predicate filter = null;

		if (region != null) {
			Predicate regionFilter = cb.or(
				cb.equal(contact.get(Contact.REGION), region),
				cb.and(cb.isNull(contact.get(Contact.REGION)), cb.equal(caze.get(Case.REGION), region)));

			filter = CriteriaBuilderHelper.and(cb, filter, regionFilter);
		}

		if (district != null) {
			Predicate districtFilter = cb.or(
				cb.equal(contact.get(Contact.DISTRICT), district),
				cb.and(cb.isNull(contact.get(Contact.DISTRICT)), cb.equal(caze.get(Case.DISTRICT), district)));

			filter = CriteriaBuilderHelper.and(cb, filter, districtFilter);
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(contact.get(Contact.DISEASE), disease);

			filter = CriteriaBuilderHelper.and(cb, filter, diseaseFilter);
		}
		return filter;
	}

	public List<DashboardQuarantineDataDto> getQuarantineDataForDashBoard(
		Region region,
		District district,
		Disease disease,
		Date from,
		Date to,
		User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardQuarantineDataDto> cq = cb.createQuery(DashboardQuarantineDataDto.class);
		Root<Contact> contact = cq.from(getElementClass());
		Join<Contact, Case> caze = contact.join(Contact.CAZE, JoinType.LEFT);

		Predicate filter = createDefaultFilter(cb, contact);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, contact));

		Predicate quarantineDateFilter = buildQuarantineDateFilter(cb, contact, from, to);

		filter = CriteriaBuilderHelper.and(cb, filter, quarantineDateFilter);

		filter = CriteriaBuilderHelper.and(cb, filter, getRegionDistrictDiseasePredicate(region, district, disease, cb, contact, caze));

		if (filter != null) {
			cq.where(filter);
			cq.multiselect(contact.get(AbstractDomainObject.ID), contact.get(Contact.QUARANTINE_FROM), contact.get(Contact.QUARANTINE_TO));

			return em.createQuery(cq).getResultList();

		}

		return Collections.emptyList();
	}

	public Map<ContactStatus, Long> getNewContactCountPerStatus(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contact = cq.from(getElementClass());
		ContactJoins joins = new ContactJoins(contact);

		Predicate filter = createUserFilter(cb, cq, contact);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, cb, contact, joins);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(contact.get(Contact.CONTACT_STATUS));
		cq.multiselect(contact.get(Contact.CONTACT_STATUS), cb.count(contact));
		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (ContactStatus) e[0], e -> (Long) e[1]));
	}

	public Map<ContactClassification, Long> getNewContactCountPerClassification(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contact = cq.from(getElementClass());
		ContactJoins joins = new ContactJoins(contact);

		Predicate filter = createUserFilter(cb, cq, contact);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, cb, contact, joins);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(contact.get(Contact.CONTACT_CLASSIFICATION));
		cq.multiselect(contact.get(Contact.CONTACT_CLASSIFICATION), cb.count(contact));
		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (ContactClassification) e[0], e -> (Long) e[1]));
	}

	public Map<FollowUpStatus, Long> getNewContactCountPerFollowUpStatus(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contact = cq.from(getElementClass());
		ContactJoins joins = new ContactJoins(contact);

		Predicate filter = createUserFilter(cb, cq, contact);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, cb, contact, joins);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(contact.get(Contact.FOLLOW_UP_STATUS));
		cq.multiselect(contact.get(Contact.FOLLOW_UP_STATUS), cb.count(contact));
		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (FollowUpStatus) e[0], e -> (Long) e[1]));
	}

	public int getFollowUpUntilCount(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> contact = cq.from(getElementClass());
		ContactJoins joins = new ContactJoins(contact);

		Predicate filter = createUserFilter(cb, cq, contact);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, cb, contact, joins);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		cq.select(cb.count(contact));

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getSingleResult().intValue();
	}

	/**
	 * Calculates resultingCase and contact status based on: - existing disease
	 * cases (and classification) of the person - the incubation period - the
	 * contact classification - the follow-up status
	 */
	public void udpateContactStatus(Contact contact) {

		ContactClassification contactClassification = contact.getContactClassification();
		if (contactClassification == null) { // Fall-back
			contactClassification = ContactClassification.UNCONFIRMED;
		}

		switch (contactClassification) {
		case UNCONFIRMED:
			contact.setContactStatus(ContactStatus.ACTIVE);
			break;
		case NO_CONTACT:
			contact.setContactStatus(ContactStatus.DROPPED);
			cancelFollowUp(contact, I18nProperties.getString(Strings.messageSystemFollowUpCanceledByDropping));
			break;
		case CONFIRMED:
			if (contact.getResultingCase() != null) {
				contact.setContactStatus(ContactStatus.CONVERTED);
			} else {
				if (contact.getFollowUpStatus() != null) {
					switch (contact.getFollowUpStatus()) {
					case CANCELED:
					case COMPLETED:
					case LOST:
					case NO_FOLLOW_UP:
						contact.setContactStatus(ContactStatus.DROPPED);
						break;
					case FOLLOW_UP:
						contact.setContactStatus(ContactStatus.ACTIVE);
						break;
					default:
						throw new NoSuchElementException(contact.getFollowUpStatus().toString());
					}
				} else {
					contact.setContactStatus(ContactStatus.ACTIVE);
				}
			}
			break;
		default:
			throw new NoSuchElementException(DataHelper.toStringNullable(contactClassification));
		}

		ensurePersisted(contact);
	}

	/**
	 * Calculates and sets the follow-up until date and status of the contact. If
	 * the date has been overwritten by a user, only the status changes and
	 * extensions of the follow-up until date based on missed visits are executed.
	 * <ul>
	 * <li>Disease with no follow-up: Leave empty and set follow-up status to "No
	 * follow-up"</li>
	 * <li>Others: Use follow-up duration of the disease. Reference for calculation
	 * is the reporting date (since this is always later than the last contact date
	 * and we can't be sure the last contact date is correct) If the last visit was
	 * not cooperative and happened at the last date of contact tracing, we need to
	 * do an additional visit.</li>
	 * </ul>
	 */
	public void updateFollowUpUntilAndStatus(Contact contact) {

		Disease disease = contact.getDisease();
		boolean changeStatus = contact.getFollowUpStatus() != FollowUpStatus.CANCELED && contact.getFollowUpStatus() != FollowUpStatus.LOST;

		ContactProximity contactProximity = contact.getContactProximity();
		if (!diseaseConfigurationFacade.hasFollowUp(disease) || (contactProximity != null && !contactProximity.hasFollowUp())) {
			contact.setFollowUpUntil(null);
			if (changeStatus) {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
		} else {
			int followUpDuration = diseaseConfigurationFacade.getFollowUpDuration(disease);
			LocalDate beginDate = DateHelper8.toLocalDate(ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime()));
			LocalDate untilDate = contact.isOverwriteFollowUpUntil()
				|| (contact.getFollowUpUntil() != null
					&& DateHelper8.toLocalDate(contact.getFollowUpUntil()).isAfter(beginDate.plusDays(followUpDuration)))
						? DateHelper8.toLocalDate(contact.getFollowUpUntil())
						: beginDate.plusDays(followUpDuration);

			Visit lastVisit = null;
			boolean additionalVisitNeeded;
			do {
				additionalVisitNeeded = false;
				lastVisit = contact.getVisits().stream().max((v1, v2) -> v1.getVisitDateTime().compareTo(v2.getVisitDateTime())).orElse(null);
				if (lastVisit != null) {
					// if the last visit was not cooperative and happened at the last date of
					// contact tracing ..
					if (lastVisit.getVisitStatus() != VisitStatus.COOPERATIVE
						&& DateHelper8.toLocalDate(lastVisit.getVisitDateTime()).isEqual(untilDate)) {
						// .. we need to do an additional visit
						additionalVisitNeeded = true;
						untilDate = untilDate.plusDays(1);
					}
					// if the last visit was cooperative and happened at the last date of contact tracing,
					// revert the follow-up until date back to the original
					if (!contact.isOverwriteFollowUpUntil()
						&& lastVisit.getVisitStatus() == VisitStatus.COOPERATIVE
						&& DateHelper8.toLocalDate(lastVisit.getVisitDateTime()).isEqual(beginDate.plusDays(followUpDuration))) {
						additionalVisitNeeded = false;
						untilDate = beginDate.plusDays(followUpDuration);
					}
				}
			}
			while (additionalVisitNeeded);

			contact.setFollowUpUntil(DateHelper8.toDate(untilDate));
			if (changeStatus) {
				if (lastVisit != null && DateHelper.isSameDay(lastVisit.getVisitDateTime(), DateHelper8.toDate(untilDate))) {
					contact.setFollowUpStatus(FollowUpStatus.COMPLETED);
				} else {
					contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
				}

				if (contact.getFollowUpStatus() != FollowUpStatus.COMPLETED && contact.getContactStatus() == ContactStatus.CONVERTED) {
					// Cancel follow-up if the contact was converted to a case
					contact.setFollowUpStatus(FollowUpStatus.CANCELED);
					contact.setFollowUpComment(I18nProperties.getString(Strings.messageSystemFollowUpCanceled));
				}
			}
		}

		ensurePersisted(contact);
	}

	public void cancelFollowUp(Contact contact, String comment) {
		contact.setFollowUpStatus(FollowUpStatus.CANCELED);
		contact.setFollowUpComment(comment);
		ensurePersisted(contact);
	}

	// Used only for testing; directly retrieve the contacts from the visit instead
	public List<Contact> getAllByVisit(Visit visit) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(Contact.class);
		Root<Visit> visitRoot = cq.from(Visit.class);

		cq.where(cb.equal(visitRoot.get(Visit.ID), visit.getId()));
		cq.select(visitRoot.get(Visit.CONTACTS));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Contact> contactPath) {
		return createUserFilterForJoin(cb, cq, contactPath);
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilterForJoin(CriteriaBuilder cb, CriteriaQuery cq, From<?, Contact> contactPath) {
		return createUserFilterForJoin(cb, cq, contactPath, null);
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilterForJoin(CriteriaBuilder cb, CriteriaQuery cq, From<?, Contact> contactPath, ContactCriteria contactCriteria) {

		Predicate userFilter = null;

		if (contactCriteria == null || contactCriteria.getIncludeContactsFromOtherJurisdictions()) {
			userFilter = caseService.createUserFilter(cb, cq, contactPath.join(Contact.CAZE, JoinType.LEFT));
		} else {
			CaseUserFilterCriteria userFilterCriteria = new CaseUserFilterCriteria();
			userFilter = caseService.createUserFilter(cb, cq, contactPath.join(Contact.CAZE, JoinType.LEFT), userFilterCriteria);
		}

		Predicate filter;
		if (userFilter != null) {
			filter = cb.or(createUserFilterWithoutCase(cb, cq, contactPath, contactCriteria), userFilter);
		} else {
			filter = createUserFilterWithoutCase(cb, cq, contactPath, contactCriteria);
		}
		return filter;
	}

	public Predicate createUserFilterWithoutCase(CriteriaBuilder cb, CriteriaQuery cq, From<?, Contact> contactPath) {
		return createUserFilterWithoutCase(cb, cq, contactPath, null);
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilterWithoutCase(
		CriteriaBuilder cb,
		CriteriaQuery cq,
		From<?, Contact> contactPath,
		ContactCriteria contactCriteria) {

		// National users can access all contacts in the system
		User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if ((jurisdictionLevel == JurisdictionLevel.NATION && !UserRole.isPortHealthUser(currentUser.getUserRoles()))
			|| currentUser.hasAnyUserRole(UserRole.REST_USER, UserRole.REST_EXTERNAL_VISITS_USER)) {
			if (currentUser.getLimitedDisease() != null) {
				return cb.equal(contactPath.get(Contact.DISEASE), currentUser.getLimitedDisease());
			} else {
				return null;
			}
		}

		Predicate filter = null;
		// whoever created it or is assigned to it is allowed to access it
		if (contactCriteria == null || contactCriteria.getIncludeContactsFromOtherJurisdictions()) {
			filter = cb.equal(contactPath.join(Contact.REPORTING_USER, JoinType.LEFT), currentUser);
			filter = cb.or(filter, cb.equal(contactPath.join(Contact.CONTACT_OFFICER, JoinType.LEFT), currentUser));
		}

		switch (jurisdictionLevel) {
		case REGION:
			final Region region = currentUser.getRegion();
			if (region != null) {
				filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(contactPath.get(Contact.REGION), currentUser.getRegion()));
			}
			break;
		case DISTRICT:
			final District district = currentUser.getDistrict();
			if (district != null) {
				filter = CriteriaBuilderHelper
					.or(cb, filter, cb.equal(contactPath.get(Contact.DISTRICT).get(District.ID), currentUser.getDistrict().getId()));
			}
			break;
		case COMMUNITY:
			final Community community = currentUser.getCommunity();
			if (community != null) {
				filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(contactPath.get(Contact.COMMUNITY), currentUser.getCommunity()));
			}
			break;
		default:
		}

		return filter;
	}

	public Predicate buildCriteriaFilter(ContactCriteria contactCriteria, CriteriaBuilder cb, Root<Contact> from, ContactJoins joins) {

		Predicate filter = null;
		Join<Contact, Case> caze = joins.getCaze();
		Join<Contact, Case> resultingCase = joins.getResultingCase();

		if (contactCriteria.getReportingUserRole() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.isMember(contactCriteria.getReportingUserRole(), joins.getReportingUser().get(User.USER_ROLES)));
		}
		if (contactCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.DISEASE), contactCriteria.getDisease()));
		}
		if (contactCriteria.getCaze() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(caze.get(Case.UUID), contactCriteria.getCaze().getUuid()));
		}
		if (contactCriteria.getResultingCase() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(resultingCase.get(Case.UUID), contactCriteria.getResultingCase().getUuid()));
		}
		if (contactCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.equal(joins.getRegion().get(Region.UUID), contactCriteria.getRegion().getUuid()),
					cb.and(
						cb.isNull(from.get(Contact.REGION)),
						cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID), contactCriteria.getRegion().getUuid()))));
		}
		if (contactCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.equal(joins.getDistrict().get(District.UUID), contactCriteria.getDistrict().getUuid()),
					cb.and(
						cb.isNull(from.get(Contact.DISTRICT)),
						cb.equal(caze.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID), contactCriteria.getDistrict().getUuid()))));
		}
		if (contactCriteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.equal(joins.getCommunity().get(Community.UUID), contactCriteria.getCommunity().getUuid()),
					cb.and(
						cb.isNull(from.get(Contact.COMMUNITY)),
						cb.equal(caze.join(Case.COMMUNITY, JoinType.LEFT).get(Community.UUID), contactCriteria.getCommunity().getUuid()))));
		}
		if (contactCriteria.getContactOfficer() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getContactOfficer().get(User.UUID), contactCriteria.getContactOfficer().getUuid()));
		}
		if (contactCriteria.getContactClassification() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.CONTACT_CLASSIFICATION), contactCriteria.getContactClassification()));
		}
		if (contactCriteria.getContactStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.CONTACT_STATUS), contactCriteria.getContactStatus()));
		}
		if (contactCriteria.getFollowUpStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.FOLLOW_UP_STATUS), contactCriteria.getFollowUpStatus()));
		}
		if (contactCriteria.getReportDateFrom() != null && contactCriteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Contact.REPORT_DATE_TIME), contactCriteria.getReportDateFrom(), contactCriteria.getReportDateTo()));
		} else if (contactCriteria.getReportDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Contact.REPORT_DATE_TIME), contactCriteria.getReportDateFrom()));
		} else if (contactCriteria.getReportDateTo() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Contact.REPORT_DATE_TIME), contactCriteria.getReportDateTo()));
		}
		if (contactCriteria.getLastContactDateFrom() != null && contactCriteria.getLastContactDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Contact.LAST_CONTACT_DATE), contactCriteria.getLastContactDateFrom(), contactCriteria.getLastContactDateTo()));
		}
		if (contactCriteria.getFollowUpUntilFrom() != null && contactCriteria.getFollowUpUntilTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Contact.FOLLOW_UP_UNTIL), contactCriteria.getFollowUpUntilFrom(), contactCriteria.getFollowUpUntilTo()));
		} else if (contactCriteria.getFollowUpUntilFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), contactCriteria.getFollowUpUntilFrom()));
		} else if (contactCriteria.getFollowUpUntilTo() != null) {
			if (!Boolean.TRUE.equals(contactCriteria.getFollowUpUntilToPrecise())) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.lessThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), contactCriteria.getFollowUpUntilTo()));
			} else {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.between(
						from.get(Contact.FOLLOW_UP_UNTIL),
						DateHelper.getStartOfDay(contactCriteria.getFollowUpUntilTo()),
						DateHelper.getEndOfDay(contactCriteria.getFollowUpUntilTo())));
			}
		}
		if (contactCriteria.getSymptomJournalStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS), contactCriteria.getSymptomJournalStatus()));
		}
		if (contactCriteria.getQuarantineTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(
					from.get(Contact.QUARANTINE_TO),
					DateHelper.getStartOfDay(contactCriteria.getQuarantineTo()),
					DateHelper.getEndOfDay(contactCriteria.getQuarantineTo())));
		}
		if (contactCriteria.getQuarantineType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.QUARANTINE), contactCriteria.getQuarantineType()));
		}
		if (Boolean.TRUE.equals(contactCriteria.getOnlyQuarantineHelpNeeded())) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.and(cb.notEqual(from.get(Contact.QUARANTINE_HELP_NEEDED), ""), cb.isNotNull(from.get(Contact.QUARANTINE_HELP_NEEDED))));
		}
		if (Boolean.TRUE.equals(contactCriteria.getQuarantineOrderedVerbally())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_ORDERED_VERBALLY)));
		}
		if (Boolean.TRUE.equals(contactCriteria.getQuarantineOrderedOfficialDocument())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT)));
		}
		if (Boolean.TRUE.equals(contactCriteria.getQuarantineNotOrdered())) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.and(
					cb.isFalse(from.get(Contact.QUARANTINE_ORDERED_VERBALLY)),
					cb.isFalse(from.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT))));
		}
		if (Boolean.TRUE.equals(contactCriteria.getWithExtendedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_EXTENDED)));
		}
		if (Boolean.TRUE.equals(contactCriteria.getWithReducedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_REDUCED)));
		}
		if (contactCriteria.getRelevanceStatus() != null) {
			if (contactCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(caze.get(Case.ARCHIVED), false), cb.isNull(caze.get(Case.ARCHIVED))));
			} else if (contactCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(caze.get(Case.ARCHIVED), true));
			}
		}
		if (contactCriteria.getDeleted() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DELETED), contactCriteria.getDeleted()));
		}
		if (contactCriteria.getNameUuidCaseLike() != null) {
			Join<Contact, Person> person = joins.getPerson();
			Join<Person, Location> location = joins.getAddress();
			Join<Case, Person> casePerson = caze.join(Case.PERSON, JoinType.LEFT);
			String[] textFilters = contactCriteria.getNameUuidCaseLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = formatForLike(textFilters[i].toLowerCase());
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
						cb.like(cb.lower(from.get(Contact.UUID)), textFilter),
						cb.like(cb.lower(person.get(Person.FIRST_NAME)), textFilter),
						cb.like(cb.lower(person.get(Person.LAST_NAME)), textFilter),
						cb.like(cb.lower(caze.get(Case.UUID)), textFilter),
						cb.like(cb.lower(casePerson.get(Person.FIRST_NAME)), textFilter),
						cb.like(cb.lower(casePerson.get(Person.LAST_NAME)), textFilter),
						phoneNumberPredicate(cb, person.get(Person.PHONE), textFilter),
						cb.like(cb.lower(location.get(Location.CITY)), textFilter),
						cb.like(cb.lower(location.get(Location.POSTAL_CODE)), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}
		if (Boolean.TRUE.equals(contactCriteria.getOnlyHighPriorityContacts())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.HIGH_PRIORITY), true));
		}
		if (contactCriteria.getContactCategory() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.CONTACT_CATEGORY), contactCriteria.getContactCategory()));
		}
		if (contactCriteria.getCaseClassification() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(caze.get(Case.CASE_CLASSIFICATION), contactCriteria.getCaseClassification()));
		}
		if (contactCriteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.UUID), contactCriteria.getPerson().getUuid()));
		}
		if (contactCriteria.getBirthdateYYYY() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_YYYY), contactCriteria.getBirthdateYYYY()));
		}
		if (contactCriteria.getBirthdateMM() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_MM), contactCriteria.getBirthdateMM()));
		}
		if (contactCriteria.getBirthdateDD() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_DD), contactCriteria.getBirthdateDD()));
		}
		if (contactCriteria.getReturningTraveler() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.RETURNING_TRAVELER), contactCriteria.getReturningTraveler()));
		}
		boolean hasEventLikeCriteria = StringUtils.isNotBlank(contactCriteria.getEventLike());
		boolean hasOnlyContactsSharingEventWithSourceCase = Boolean.TRUE.equals(contactCriteria.getOnlyContactsSharingEventWithSourceCase());
		if (hasEventLikeCriteria || hasOnlyContactsSharingEventWithSourceCase) {
			Join<Person, EventParticipant> eventParticipant = joins.getEventParticipants();
			Join<EventParticipant, Event> event = joins.getEvent();

			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.isFalse(event.get(Event.DELETED)),
				cb.isFalse(event.get(Event.ARCHIVED)),
				cb.isFalse(eventParticipant.get(EventParticipant.DELETED)));

			if (hasEventLikeCriteria) {
				String[] textFilters = contactCriteria.getEventLike().trim().split("\\s+");
				for (String s : textFilters) {
					String textFilter = formatForLike(s);
					if (!DataHelper.isNullOrEmpty(textFilter)) {
						Predicate likeFilters = cb.or(
							cb.like(cb.lower(event.get(Event.EVENT_DESC)), textFilter),
							cb.like(cb.lower(event.get(Event.EVENT_TITLE)), textFilter),
							cb.like(cb.lower(event.get(Event.UUID)), textFilter));
						filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
					}
				}
			}
			if (hasOnlyContactsSharingEventWithSourceCase) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(event.get(Event.UUID), joins.getCaseEvent().get(Event.UUID)));
			}
		}
		if (contactCriteria.getEventUuid() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEvent().get(Event.UUID), contactCriteria.getEventUuid()));
		}
		if (contactCriteria.getEventParticipant() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getEventParticipants().get(EventParticipant.UUID), contactCriteria.getEventParticipant().getUuid()));
		}
		if (contactCriteria.getOnlyContactsWithSourceCaseInGivenEvent() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(joins.getCaseEvent().get(Event.UUID), contactCriteria.getOnlyContactsWithSourceCaseInGivenEvent().getUuid()));
		}

		return filter;
	}

	@Override
	public void delete(Contact contact) {

		// Delete all tasks associated with this contact
		List<Task> tasks = taskService.findBy(new TaskCriteria().contact(new ContactReferenceDto(contact.getUuid())), true);
		for (Task task : tasks) {
			taskService.delete(task);
		}

		// Delete all samples only associated with this contact
		contact.getSamples()
			.stream()
			.filter(sample -> sample.getAssociatedCase() == null && sample.getAssociatedEventParticipant() == null)
			.forEach(sample -> sampleService.delete(sample));

		// Remove this contact from all exposures that its referenced in
		exposureService.removeContactFromExposures(contact.getId());

		super.delete(contact);
	}

	/**
	 * Creates a filter that excludes all contacts that are either
	 * {@link CoreAdo#isDeleted()} or associated with cases that are
	 * {@link Case#isArchived()}.
	 */
	public Predicate createActiveContactsFilter(CriteriaBuilder cb, Root<Contact> root) {

		Join<Contact, Case> caze = root.join(Contact.CAZE, JoinType.LEFT);
		return cb.and(cb.or(cb.isNull(root.get(Contact.CAZE)), cb.isFalse(caze.get(Case.ARCHIVED))), cb.isFalse(root.get(Contact.DELETED)));
	}

	public Predicate createActiveContactsFilter(CriteriaBuilder cb, Join<?, Contact> contactJoin) {

		Join<Contact, Case> caze = contactJoin.join(Contact.CAZE, JoinType.LEFT);
		return cb
			.and(cb.or(cb.isNull(contactJoin.get(Contact.CAZE)), cb.isFalse(caze.get(Case.ARCHIVED))), cb.isFalse(contactJoin.get(Contact.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do
	 * not use {@link ContactCriteria}. This essentially removes
	 * {@link CoreAdo#isDeleted()} contacts from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<Contact> root) {
		return cb.isFalse(root.get(Contact.DELETED));
	}

	private Predicate buildDateFilter(CriteriaBuilder cb, Root<Contact> contact, Date from, Date to) {

		return cb.and(
			cb.or(
				cb.and(cb.isNotNull(contact.get(Contact.FOLLOW_UP_UNTIL)), cb.greaterThanOrEqualTo(contact.get(Contact.FOLLOW_UP_UNTIL), from)),
				cb.or(
					cb.and(
						cb.isNotNull(contact.get(Contact.LAST_CONTACT_DATE)),
						cb.greaterThanOrEqualTo(contact.get(Contact.LAST_CONTACT_DATE), from)),
					cb.greaterThanOrEqualTo(contact.get(Contact.REPORT_DATE_TIME), from))),
			cb.or(
				cb.and(cb.isNotNull(contact.get(Contact.LAST_CONTACT_DATE)), cb.lessThanOrEqualTo(contact.get(Contact.LAST_CONTACT_DATE), to)),
				cb.lessThanOrEqualTo(contact.get(Contact.REPORT_DATE_TIME), to)));
	}

	private Predicate buildQuarantineDateFilter(CriteriaBuilder cb, Root<Contact> contact, Date from, Date to) {

		return cb.or(
			cb.and(
				cb.greaterThanOrEqualTo(contact.get(Contact.QUARANTINE_FROM), from),
				cb.lessThanOrEqualTo(contact.get(Contact.QUARANTINE_FROM), to)),
			cb.and(cb.greaterThanOrEqualTo(contact.get(Contact.QUARANTINE_TO), from), cb.lessThanOrEqualTo(contact.get(Contact.QUARANTINE_TO), to)),
			cb.and(
				cb.lessThanOrEqualTo(contact.get(Contact.QUARANTINE_FROM), from),
				cb.greaterThanOrEqualTo(contact.get(Contact.QUARANTINE_TO), to)));
	}

	public Predicate isInJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<Long> cq, Root<Contact> contactRoot, ContactJoins joins) {
		final User currentUser = this.getCurrentUser();

		final Subquery<Long> contactCaseJurisdictionSubQuery = cq.subquery(Long.class);
		final Root<Case> contactCaseRoot = contactCaseJurisdictionSubQuery.from(Case.class);
		contactCaseJurisdictionSubQuery.select(contactCaseRoot.get(Contact.ID));
		contactCaseJurisdictionSubQuery.where(
			cb.and(
				cb.equal(contactCaseRoot, joins.getRoot().get(Contact.CAZE)),
				caseService.isInJurisdictionOrOwned(cb, new CaseJoins<>(contactCaseRoot))));

		final Predicate contactCaseInJurisdiction = cb.exists(contactCaseJurisdictionSubQuery);

		final Predicate reportedByCurrentUser =
			cb.and(cb.isNotNull(joins.getReportingUser()), cb.equal(joins.getReportingUser().get(User.UUID), currentUser.getUuid()));

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		final Predicate jurisdictionPredicate;
		switch (jurisdictionLevel) {
		case NATION:
			jurisdictionPredicate = cb.conjunction();
			break;
		case REGION:
			jurisdictionPredicate = cb.equal(joins.getRegion().get(Region.ID), currentUser.getRegion().getId());
			break;
		case DISTRICT:
			jurisdictionPredicate = cb.equal(joins.getDistrict().get(District.ID), currentUser.getDistrict().getId());
			break;
		case LABORATORY:
		case EXTERNAL_LABORATORY:
		case NONE:
		case COMMUNITY:
		case HEALTH_FACILITY:
		case POINT_OF_ENTRY:
		default:
			jurisdictionPredicate = cb.disjunction();
		}
		return cb.or(reportedByCurrentUser, jurisdictionPredicate, cb.and(cb.isNull(contactRoot.get(Contact.REGION)), contactCaseInJurisdiction));
	}

	public boolean isContactEditAllowed(Contact contact) {
		if (contact.getSormasToSormasOriginInfo() != null) {
			return contact.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		return contactJurisdictionChecker.isInJurisdictionOrOwned(contact) && !sormasToSormasShareInfoService.isContactOwnershipHandedOver(contact);
	}
}
