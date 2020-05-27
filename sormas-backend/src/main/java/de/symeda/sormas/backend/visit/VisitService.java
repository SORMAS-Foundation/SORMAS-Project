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
package de.symeda.sormas.backend.visit;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class VisitService extends AbstractAdoService<Visit> {

	@EJB
	ContactService contactService;
	@EJB
	CaseService caseService;

	public VisitService() {
		super(Visit.class);
	}

	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> visitsQuery = cb.createQuery(String.class);
		Root<Contact> contactRoot = visitsQuery.from(Contact.class);
		Join<Contact, Visit> visitJoin = contactRoot.join(Contact.VISITS, JoinType.LEFT);
		
		visitsQuery.where(and(cb, 
				contactService.createUserFilter(cb, visitsQuery, contactRoot), 
				contactService.createActiveContactsFilter(cb, contactRoot),
				cb.isNotEmpty(contactRoot.get(Contact.VISITS))));
		visitsQuery.select(visitJoin.get(Visit.UUID));
		visitsQuery.distinct(true);

		List<String> resultList = em.createQuery(visitsQuery).getResultList();
		return resultList;	
	}

	public List<Visit> getAllActiveVisitsAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> visitsQuery = cb.createQuery(Visit.class);
		Root<Contact> contactRoot = visitsQuery.from(Contact.class);
		Join<Contact, Visit> visitJoin = contactRoot.join(Contact.VISITS, JoinType.LEFT);
		
		Predicate filter = and(cb, 
				contactService.createUserFilter(cb, visitsQuery, contactRoot), 
				contactService.createActiveContactsFilter(cb, contactRoot));
		
		if (date != null) {
			filter = and(cb, filter, createChangeDateFilter(cb, visitJoin, DateHelper.toTimestampUpper(date)));
		}

		visitsQuery.select(visitJoin);
		visitsQuery.where(filter);
		visitsQuery.distinct(true);
		visitsQuery.orderBy(cb.asc(visitJoin.get(AbstractDomainObject.ID)));

		return em.createQuery(visitsQuery).getResultList();
	}

	// Used only for testing; directly retrieve the visits from the contact instead
	public List<Visit> getAllByContact(Contact contact) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(Visit.class);
		Root<Contact> contactRoot = cq.from(Contact.class);

		cq.where(cb.equal(contactRoot.get(Contact.ID), contact.getId()));
		cq.select(contactRoot.get(Contact.VISITS));
		
		return em.createQuery(cq).getResultList();
	}
	
	public Set<Visit> getAllRelevantVisits(Person person, Disease disease, Date startDate, Date endDate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Visit> cq = cb.createQuery(Visit.class);
        Root<Visit> visitRoot = cq.from(Visit.class);
        
        cq.where(buildRelevantVisitsFilter(person, disease, startDate, endDate, cb, visitRoot));
        
        return new HashSet<>(em.createQuery(cq).getResultList());
	}

	/**
	 * Returns a filter that can be used to retrieve all visits with the specified person and disease
	 * whose visit date is after the start date and before the end date.
	 */
	public Predicate buildRelevantVisitsFilter(Person person, Disease disease, Date startDate, Date endDate, CriteriaBuilder cb, Root<Visit> from) {
		startDate = DateHelper.getStartOfDay(startDate);
		endDate = DateHelper.getEndOfDay(endDate);
		
		Predicate filter = cb.and(
				cb.equal(from.get(Visit.PERSON), person),
				cb.equal(from.get(Visit.DISEASE), disease)
				);
		
		filter = and(cb, filter,
				cb.greaterThanOrEqualTo(from.get(Visit.VISIT_DATE_TIME), DateHelper.subtractDays(startDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET)),
				cb.lessThanOrEqualTo(from.get(Visit.VISIT_DATE_TIME), DateHelper.addDays(endDate, ContactLogic.ALLOWED_CONTACT_DATE_OFFSET)));
		
		return filter;
	}
	
	public Predicate buildCriteriaFilter(VisitCriteria criteria, CriteriaBuilder cb, Root<Visit> from) {
		Predicate filter = null;
		if (criteria.getContact() != null) {
			Contact contact = contactService.getByUuid(criteria.getContact().getUuid());
			filter = and(cb, filter, buildRelevantVisitsFilter(contact.getPerson(), contact.getDisease(),
					ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime()),
					ContactLogic.getEndDate(contact.getLastContactDate(), contact.getReportDateTime(), contact.getFollowUpUntil()), cb, from));
		}

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Visit, Visit> from) {
		// getAllUuids and getAllAfter have custom implementations
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Visit> visitPath, Timestamp date) {
		Predicate dateFilter = cb.greaterThan(visitPath.get(Visit.CHANGE_DATE), date);

		Join<Visit, Symptoms> symptoms = visitPath.join(Visit.SYMPTOMS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.get(AbstractDomainObject.CHANGE_DATE), date));

		return dateFilter;
	}
	
}
