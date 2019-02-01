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
package de.symeda.sormas.backend.disease;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.disease.DiseaseFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.outbreak.Outbreak;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Provides the application configuration settings
 */
@Stateless(name="DiseaseFacade")
public class DiseaseFacadeEjb implements DiseaseFacade {
	
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	//@Override
	public List<DiseaseBurdenDto> getDiseaseBurdenForDashboard(
			RegionReferenceDto regionRef,
			DistrictReferenceDto districtRef, 
			Date from, 
			Date to,
			Date previousFrom,
			Date previousTo,
			String userUuid) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = null;
		
		//diseases
		List<Disease> diseases = Stream.of(Disease.values()).collect(Collectors.toList());
				
		//cases
		cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);
		cq.multiselect(caze.get(Case.DISEASE), cb.count(caze));
		cq.groupBy(caze.get(Case.DISEASE));
		
		Predicate filter = null;
		if (from != null || to != null) {
			filter = AbstractAdoService.and(cb, filter, createActiveCaseFilter(cb, caze, from, to));
		}
		if (districtRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(caze.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID), districtRef.getUuid()));
		}
		else if (regionRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid()));
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Object[]> cases = em.createQuery(cq).getResultList();
		
		//previous cases
		cq = cb.createQuery(Object[].class);
		caze = cq.from(Case.class);
		cq.multiselect(caze.get(Case.DISEASE), cb.count(caze));
		cq.groupBy(caze.get(Case.DISEASE));
		
		filter = null;
		if (previousFrom != null || to != null) {
			filter = AbstractAdoService.and(cb, filter, createActiveCaseFilter(cb, caze, previousFrom, previousTo));
		}
		if (districtRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(caze.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID), districtRef.getUuid()));
		}
		else if (regionRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid()));
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Object[]> previousCases = em.createQuery(cq).getResultList();
				
		//events
		cq = cb.createQuery(Object[].class);
		Root<Event> event = cq.from(Event.class);
		Join<Event, Location> eventLocation = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		cq.multiselect(event.get(Event.DISEASE), cb.count(event));
		cq.groupBy(event.get(Event.DISEASE));
		
		filter = null;
		if (from != null || to != null) {
			filter = AbstractAdoService.and(cb, filter, cb.between(event.get(Event.REPORT_DATE_TIME), from, to));
		}
		if (districtRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(eventLocation.join(Location.DISTRICT, JoinType.LEFT).get(District.UUID), districtRef.getUuid()));
		}
		else if (regionRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(eventLocation.join(Location.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid()));
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Object[]> events = em.createQuery(cq).getResultList();
				
		//outbreaks
		cq = cb.createQuery(Object[].class);
		Root<Outbreak> outbreak = cq.from(Outbreak.class);
		cq.multiselect(outbreak.get(Outbreak.DISEASE), cb.countDistinct(outbreak.get(Outbreak.DISTRICT)));
		cq.groupBy(outbreak.get(Outbreak.DISEASE));
		
		filter = null;
		if (from != null || to != null) {
			filter = AbstractAdoService.and(cb, filter, cb.between(outbreak.get(Outbreak.REPORT_DATE), from, to));
		}
		if (districtRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(outbreak.join(Outbreak.DISTRICT, JoinType.LEFT).get(District.UUID), districtRef.getUuid()));
		}
		else if (regionRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(outbreak.join(Outbreak.DISTRICT, JoinType.LEFT).join(District.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid()));
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Object[]> outbreaks = em.createQuery(cq).getResultList();
		
		//case fatalities
		cq = cb.createQuery(Object[].class);
		Root<Person> deceasedPerson = cq.from(Person.class);
		cq.multiselect(deceasedPerson.get(Person.CAUSE_OF_DEATH_DISEASE), cb.count(deceasedPerson));
		cq.groupBy(deceasedPerson.get(Person.CAUSE_OF_DEATH_DISEASE));

		filter = cb.isNotNull(deceasedPerson.get(Person.CAUSE_OF_DEATH_DISEASE));
		if (from != null || to != null) {
			filter = AbstractAdoService.and(cb, filter, cb.between(deceasedPerson.get(Person.DEATH_DATE), from, to));
		}
		if (districtRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(deceasedPerson.join(Person.ADDRESS, JoinType.LEFT).join(Location.DISTRICT, JoinType.LEFT).get(District.UUID), districtRef.getUuid()));
		}
		else if (regionRef != null) {
			filter = AbstractAdoService.and(cb, filter, cb.equal(deceasedPerson.join(Person.ADDRESS, JoinType.LEFT).join(Location.DISTRICT, JoinType.LEFT).join(District.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid()));
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Object[]> caseFatalities = em.createQuery(cq).getResultList();
		
		//build diseases
		List<DiseaseBurdenDto> diseasesBurden = diseases.stream().map(disease -> {
			Long caseCount = cases.stream().filter(o -> o[0] == disease).map(o -> (Long)o[1]).findFirst().orElse(0L);
			Long previousCaseCount = previousCases.stream().filter(o -> o[0] == disease).map(o -> (Long)o[1]).findFirst().orElse(0L);
			Long eventCount = events.stream().filter(o -> o[0] == disease).map(o -> (Long)o[1]).findFirst().orElse(0L);
			Long outbreakDistrictCount = outbreaks.stream().filter(o -> o[0] == disease).map(o -> (Long)o[1]).findFirst().orElse(0L);
			Long caseFatalityCount = caseFatalities.stream().filter(o -> o[0] == disease).map(o -> (Long)o[1]).findFirst().orElse(0L);
			
			return new DiseaseBurdenDto(disease, caseCount, previousCaseCount, eventCount, outbreakDistrictCount, caseFatalityCount);
			
		}).collect(Collectors.toList());
		
		return diseasesBurden;
	}
	
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
	
	@LocalBean
	@Stateless
	public static class DiseaseFacadeEjbLocal extends DiseaseFacadeEjb {
	}
	
}