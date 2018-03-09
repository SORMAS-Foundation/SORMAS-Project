package de.symeda.sormas.backend.event;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class EventParticipantService extends AbstractAdoService<EventParticipant> {

	@EJB
	private EventService eventService;
	@EJB
	private CaseService caseService;
	
	public EventParticipantService() {
		super(EventParticipant.class);
	}
	
	public List<EventParticipant> getAllByEventAfter(Date date, Event event) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(EventParticipant.EVENT), event);
		if (date != null) {
			filter = cb.and(filter, createDateFilter(cb, cq, from, date));
		}
		cq.where(filter);		
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));
		
		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<EventParticipant, EventParticipant> eventParticipantPath, User user) {
		// can see the participants of all accessible events
		Predicate filter = eventService.createUserFilter(cb, cq, eventParticipantPath.join(EventParticipant.EVENT, JoinType.LEFT), user);
	
		return filter;
	}
	
	/**
	 * Calculates resultingCase based on: 
	 * - existing disease cases (and classification) of the person and
	 * - the incubation period of the event
	 * 
	 * TODO what if no disease is defined or disease doesn't have an incubation period
	 */
	public void udpateResultingCase(EventParticipant eventParticipant) {

		// calculate the incubation period relative to the event
		// make sure to get the maximum time span based on report date time and event date
		Date incubationPeriodStart = eventParticipant.getEvent().getReportDateTime();
		Date incubationPeriodEnd = eventParticipant.getEvent().getReportDateTime();
		if (eventParticipant.getEvent().getEventDate() != null) {
			if (eventParticipant.getEvent().getEventDate().before(incubationPeriodStart)) { // whatever is earlier
				incubationPeriodStart = eventParticipant.getEvent().getEventDate();
			}
			if (eventParticipant.getEvent().getEventDate().after(incubationPeriodEnd)) { // whatever is later
				incubationPeriodEnd = eventParticipant.getEvent().getEventDate();
			}
		}
		incubationPeriodEnd = DateHelper.addDays(incubationPeriodEnd, 
				DiseaseHelper.getIncubationPeriodDays(eventParticipant.getEvent().getDisease(), null));
		
		// see if any case was reported or has symptom onset within the period
		Case resultingCase = caseService.getFirstByPersonDiseaseAndOnset(
				eventParticipant.getEvent().getDisease(), eventParticipant.getPerson(), 
				incubationPeriodStart, incubationPeriodEnd);
		
		if (resultingCase == null) {
			// or any case that may have "caused" the event
			resultingCase = caseService.getLastActiveByPersonDiseaseAtDate(
				eventParticipant.getEvent().getDisease(), eventParticipant.getPerson(), 
				incubationPeriodStart);
		}

		eventParticipant.setResultingCase(resultingCase);
		ensurePersisted(eventParticipant);
	}

	public List<EventParticipant> getAllByPerson(Person person) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventParticipant> cq = cb.createQuery(getElementClass());
		Root<EventParticipant> from = cq.from(getElementClass());

		cq.where(cb.equal(from.get(EventParticipant.PERSON), person));
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));

		List<EventParticipant> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
}
