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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.person.Person;

@Stateless
@LocalBean
public class VaccinationService extends BaseAdoService<Vaccination> {

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

	public List<Vaccination> getVaccinationsByCriteria(VaccinationListCriteria criteria, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Vaccination> cq = cb.createQuery(Vaccination.class);
		final Root<Vaccination> root = cq.from(Vaccination.class);
		final Join<Vaccination, Immunization> immunizationJoin = root.join(Vaccination.IMMUNIZATION, JoinType.LEFT);
		final Join<Immunization, Person> personJoin = immunizationJoin.join(Immunization.PERSON, JoinType.LEFT);

		Predicate filter = cb.equal(personJoin.get(AbstractDomainObject.UUID), criteria.getPerson().getUuid());
		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(immunizationJoin.get(Immunization.DISEASE), criteria.getDisease()));
		}
		cq.where(filter);
		cq.orderBy(cb.desc(root.get(Vaccination.VACCINATION_DATE)), cb.desc(root.get(AbstractDomainObject.CHANGE_DATE)));
		cq.distinct(true);

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			return em.createQuery(cq).getResultList();
		}
	}

	public static boolean isVaccinationRelevant(Case caze, Vaccination vaccination) {
		return vaccination.getVaccinationDate() != null
			&& (caze.getSymptoms().getOnsetDate() != null
				? DateHelper.getEndOfDay(vaccination.getVaccinationDate()).before(caze.getSymptoms().getOnsetDate())
				: DateHelper.getEndOfDay(vaccination.getVaccinationDate()).before(caze.getReportDate()));
	}

	public static boolean isVaccinationRelevant(Contact contact, Vaccination vaccination) {
		return vaccination.getVaccinationDate() != null
			&& (contact.getLastContactDate() != null
				? DateHelper.getEndOfDay(vaccination.getVaccinationDate()).before(contact.getLastContactDate())
				: DateHelper.getEndOfDay(vaccination.getVaccinationDate()).before(contact.getReportDateTime()));
	}

	public static boolean isVaccinationRelevant(Event event, Vaccination vaccination) {
		if (vaccination.getVaccinationDate() == null) {
			return false;
		}
		if (event.getStartDate() != null) {
			return DateHelper.getEndOfDay(vaccination.getVaccinationDate()).before(event.getStartDate());
		}
		return event.getEndDate() != null
			? DateHelper.getEndOfDay(vaccination.getVaccinationDate()).before(event.getEndDate())
			: DateHelper.getEndOfDay(vaccination.getVaccinationDate()).before(event.getReportDateTime());
	}
}
