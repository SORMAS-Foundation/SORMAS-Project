/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.vaccination;

import static de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect.AT_END_OF_DAY;
import static de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect.TIMESTAMP_SUBTRACT_DAYS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.immunization.ImmunizationEntityHelper;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;

@Stateless
@LocalBean
public class VaccinationService extends BaseAdoService<Vaccination> {

	public static final int REPORT_DATE_RELEVANT_DAYS = 14;

	public VaccinationService() {
		super(Vaccination.class);
	}

	public Map<String, String> getLastVaccinationType() {
		Map<String, String> result = new HashMap<>();
		String queryString =
			"select v.immunization_id, vaccinetype from vaccination v inner join (select immunization_id, max(vaccinationdate) maxdate from vaccination group by immunization_id) maxdates on v.immunization_id=maxdates.immunization_id and v.vaccinationdate=maxdates.maxdate";
		Query query = em.createNativeQuery(queryString);
		((Stream<Object[]>) query.getResultStream()).forEach(item -> result.put(((Number) item[0]).toString(), (String) item[1]));
		return result;
	}

	public List<Vaccination> getVaccinationsByCriteria(
		VaccinationListCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Vaccination> cq = cb.createQuery(Vaccination.class);
		final Root<Vaccination> root = cq.from(Vaccination.class);
		final Join<Vaccination, Immunization> immunizationJoin = root.join(Vaccination.IMMUNIZATION, JoinType.LEFT);
		final Join<Immunization, Person> personJoin = immunizationJoin.join(Immunization.PERSON, JoinType.LEFT);

		Predicate filter = null;

		if (criteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(personJoin.get(AbstractDomainObject.UUID), criteria.getPerson().getUuid()));
		} else {
			List<String> personUuids = criteria.getPersons().stream().map(PersonReferenceDto::getUuid).collect(Collectors.toList());
			filter = CriteriaBuilderHelper.and(cb, filter, personJoin.get(AbstractDomainObject.UUID).in(personUuids));
		}

		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(immunizationJoin.get(Immunization.DISEASE), criteria.getDisease()));
		}
		cq.where(filter);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Vaccination.VACCINATION_DATE:
					expression = root.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(root.get(Vaccination.VACCINATION_DATE)), cb.desc(root.get(AbstractDomainObject.CHANGE_DATE)));
		}

		cq.distinct(true);

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			return em.createQuery(cq).getResultList();
		}
	}

	/**
	 * HEADS UP! When this method gets changed, most probably the database logic in
	 * {@link de.symeda.sormas.backend.vaccination.VaccinationService#getRelevantVaccinationPredicate(From, CriteriaQuery, CriteriaBuilder, Path)}
	 * {@link de.symeda.sormas.backend.vaccination.VaccinationService#getRelevantVaccinationPredicate(From, CommonAbstractCriteria, CriteriaBuilder, Vaccination)}
	 *
	 * will also need an update.
	 *
	 * @param caze
	 *            to decide whether the vaccination is relevant for
	 * @param vaccination
	 *            to be decided about
	 * @return true when the vaccination is relevant, false otherwise
	 */
	public boolean isVaccinationRelevant(Case caze, Vaccination vaccination) {
		return isVaccinationRelevant(vaccination, caze.getSymptoms().getOnsetDate(), caze.getReportDate());
	}

	/**
	 * HEADS UP! When this method gets changed, most probably the database logic in
	 * {@link de.symeda.sormas.backend.vaccination.VaccinationService#isVaccinationRelevant(Case, Vaccination)}
	 * {@link de.symeda.sormas.backend.vaccination.VaccinationService#getRelevantVaccinationPredicate(From, CommonAbstractCriteria, CriteriaBuilder, Vaccination)}
	 *
	 * will also need an update
	 *
	 * @param casePath
	 * @param cq
	 * @param cb
	 * @param vaccinationPath
	 * @return a predicate that compares the vaccination date to the case symptom onset or reporting date to decide whether it is relevant
	 *         or not
	 */
	public Predicate getRelevantVaccinationPredicate(
		From<?, Case> casePath,
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		Path<Vaccination> vaccinationPath) {
		return getRelevantVaccinationPredicate(cb, vaccinationPath, getCaseSymptomsExpression(casePath, cq, cb), casePath.get(Case.REPORT_DATE));
	}

	/**
	 * HEADS UP! When this method gets changed, most probably the database logic in
	 * {@link de.symeda.sormas.backend.vaccination.VaccinationService#isVaccinationRelevant(Case, Vaccination)}
	 * {@link de.symeda.sormas.backend.vaccination.VaccinationService#getRelevantVaccinationPredicate(From, CriteriaQuery, CriteriaBuilder, Path)}
	 *
	 * will also need an update
	 *
	 * @param casePath
	 * @param cq
	 * @param cb
	 * @param vaccination
	 * @return a predicate that compares the vaccination date to the case symptom onset or reporting date to decide whether it is relevant
	 *         or not
	 */
	public Predicate getRelevantVaccinationPredicate(From<?, Case> casePath, CommonAbstractCriteria cq, CriteriaBuilder cb, Vaccination vaccination) {
		return getRelevantVaccinationPredicate(cb, vaccination, getCaseSymptomsExpression(casePath, cq, cb), casePath.get(Case.REPORT_DATE));
	}

	private Expression<Date> getCaseSymptomsExpression(From<?, Case> casePath, CommonAbstractCriteria cq, CriteriaBuilder cb) {
		Subquery<Symptoms> symptomsSq = cq.subquery(Symptoms.class);
		Root<Symptoms> symptomsSqRoot = symptomsSq.from(Symptoms.class);
		symptomsSq.select(symptomsSqRoot.get(Symptoms.ONSET_DATE));
		symptomsSq.where(cb.equal(symptomsSqRoot, casePath.get(Case.SYMPTOMS)));

		return symptomsSq.getSelection().as(Date.class);
	}

	/**
	 * HEADS UP! When this method gets changed, most probably the database logic in
	 * {@link de.symeda.sormas.backend.contact.ContactService#updateVaccinationStatuses(Long, Disease, Date)}
	 * will also need an update.
	 *
	 * @param contact
	 *            to decide whether the vaccination is relevant for
	 * @param vaccination
	 *            to be decided about
	 * @return true when the vaccination is relevant, false otherwise
	 */
	public boolean isVaccinationRelevant(Contact contact, Vaccination vaccination) {
		return isVaccinationRelevant(vaccination, contact.getLastContactDate(), contact.getReportDateTime());

	}

	/**
	 * HEADS UP! When this method gets changed, most probably the database logic in
	 * {@link de.symeda.sormas.backend.event.EventParticipantService#updateVaccinationStatuses(Long, Disease, Date)}
	 * will also need an update.
	 *
	 * @param event
	 *            to decide whether the vaccination is relevant for
	 * @param vaccination
	 *            to be decided about
	 * @return true when the vaccination is relevant, false otherwise
	 */
	public boolean isVaccinationRelevant(Event event, Vaccination vaccination) {
		return isVaccinationRelevant(vaccination, event.getStartDate(), event.getEndDate(), event.getReportDateTime());
	}

	/*
	 * HEADS UP! If you make changes here, you most probably also need to update the database queries in
	 * ContactService.updateVaccinationStatuses(...) and
	 * EventParticipantService.updateVaccinationStatuses(...).
	 */
	private boolean isVaccinationRelevant(Vaccination vaccination, Date... relevanceFilterDates) {

		Date relevantVaccineDate = getRelevantVaccineDate(vaccination);
		for (Date comparisonDate : relevanceFilterDates) {
			if (comparisonDate != null) {
				return DateHelper.getEndOfDay(relevantVaccineDate).before(comparisonDate);
			}
		}
		return false;
	}

	public List<Vaccination> getRelevantSortedVaccinations(List<Vaccination> vaccinations, Date... relevanceFilterDates) {

		return vaccinations.stream()
			.filter(v -> isVaccinationRelevant(v, relevanceFilterDates))
			.sorted(Comparator.comparing(ImmunizationEntityHelper::getVaccinationDateForComparison))
			.collect(Collectors.toList());
	}

	public Predicate getRelevantVaccinationPredicate(
		CriteriaBuilder cb,
		Path<Vaccination> vaccinationPath,
		Expression<Date> primaryDatePath,
		Expression<Date> fallbackDatePath) {

		Path<Date> vaccinationDate = vaccinationPath.get(Vaccination.VACCINATION_DATE);
		Expression<Date> vaccinationDateExpr = cb.<Date> selectCase()
			.when(
				cb.isNull(vaccinationDate),
				cb.function(TIMESTAMP_SUBTRACT_DAYS, Date.class, vaccinationPath.get(Vaccination.REPORT_DATE), cb.literal(REPORT_DATE_RELEVANT_DAYS)))
			.otherwise(vaccinationDate);

		return getRelevantVaccinationPredicate(cb, vaccinationDateExpr, primaryDatePath, fallbackDatePath);
	}

	public Predicate getRelevantVaccinationPredicate(
		CriteriaBuilder cb,
		Vaccination vaccination,
		Expression<Date> primaryDatePath,
		Expression<Date> fallbackDatePath) {

		return getRelevantVaccinationPredicate(cb, cb.literal(getRelevantVaccineDate(vaccination)), primaryDatePath, fallbackDatePath);
	}

	/**
	 * HEADS UP! When this method gets changed, most probably the database logic in
	 * {@link de.symeda.sormas.backend.vaccination.VaccinationService#isVaccinationRelevant(Vaccination, Date...)}
	 * will also need an update.
	 */
	private Predicate getRelevantVaccinationPredicate(
		CriteriaBuilder cb,
		Expression<Date> vaccinationDate,
		Expression<Date> primaryDatePath,
		Expression<Date> fallbackDatePath) {

		Expression<Date> vaccinationDateEndOfDay = cb.function(AT_END_OF_DAY, Date.class, vaccinationDate);

		return cb.or(
			cb.greaterThan(primaryDatePath, vaccinationDateEndOfDay),
			cb.and(cb.isNull(primaryDatePath), cb.greaterThan(fallbackDatePath, vaccinationDateEndOfDay)));
	}

	/**
	 * * Obtains the date used for calculating the relevance of a vaccination for a case/contact/event participant.
	 * * There is a 14-day buffer when the report date needs to be used.
	 * * There is an identical method for VaccinationDtos below!
	 *
	 * @param vaccination
	 *            to obtain the relevant date from
	 * @return relevant date
	 */
	public Date getRelevantVaccineDate(Vaccination vaccination) {
		return vaccination.getVaccinationDate() != null
			? vaccination.getVaccinationDate()
			: DateHelper.subtractDays(vaccination.getReportDate(), REPORT_DATE_RELEVANT_DAYS);
	}

	/**
	 * Obtains the date used for calculating the relevance of a vaccination for a case/contact/event participant.
	 * There is a 14-day buffer when the report date needs to be used.
	 * There is an identical method for Vaccinations above!
	 *
	 * @param vaccination
	 *            to obtain the relevant date from
	 * @return relevant date
	 */
	public Date getRelevantVaccineDate(VaccinationDto vaccination) {
		return vaccination.getVaccinationDate() != null ? vaccination.getVaccinationDate() : DateHelper.subtractDays(vaccination.getReportDate(), 14);
	}

}
