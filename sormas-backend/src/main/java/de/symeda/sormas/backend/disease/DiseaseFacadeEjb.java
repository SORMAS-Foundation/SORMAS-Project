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
			String userUuid) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DiseaseBurdenDto> cq = cb.createQuery(DiseaseBurdenDto.class);

		
		//diseases
		EnumSet<Disease> _diseases = EnumSet.allOf(Disease.class);
		List<DiseaseBurdenDto> diseases = new ArrayList<>();
		for (Disease disease : _diseases) {
			diseases.add(new DiseaseBurdenDto(disease, 0L));
		}
				
		//cases
		Root<Case> caze = cq.from(Case.class);
		Join<Case, District> district = caze.join(Case.DISTRICT, JoinType.LEFT);
		cq.multiselect(caze.get(Case.DISEASE), cb.count(caze), cb.count(district));
		cq.groupBy(caze.get(Case.DISEASE));
		
		Predicate filter = null;
		if (from != null || to != null) {
			filter = createActiveCaseFilter(cb, caze, from, to);
		}
		if (districtRef != null) {
			Predicate districtFilter = cb.equal(district.get(District.UUID), districtRef.getUuid());
			filter = filter != null ? cb.and(filter, districtFilter) : districtFilter;
		}
		else if (regionRef != null) {
			Predicate regionFilter = cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid());
			filter = filter != null ? cb.and(filter, regionFilter) : regionFilter;
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<DiseaseBurdenDto> cases = em.createQuery(cq).getResultList();
		
		//previous cases
		caze = cq.from(Case.class);
		district = caze.join(Case.DISTRICT, JoinType.LEFT);
		cq.multiselect(caze.get(Case.DISEASE), cb.count(caze), cb.count(district));
		cq.groupBy(caze.get(Case.DISEASE));
		
		filter = null;
		if (from != null || to != null) {
			filter = createActiveCaseFilter(cb, caze, from, to);
		}
		if (districtRef != null) {
			Predicate districtFilter = cb.equal(district.get(District.UUID), districtRef.getUuid());
			filter = filter != null ? cb.and(filter, districtFilter) : districtFilter;
		}
		else if (regionRef != null) {
			Predicate regionFilter = cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid());
			filter = filter != null ? cb.and(filter, regionFilter) : regionFilter;
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<DiseaseBurdenDto> previousCases = em.createQuery(cq).getResultList();
				
		//events
		Root<Event> event = cq.from(Event.class);
		Join<Event, Location> eventLocation = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		cq.multiselect(event.get(Event.DISEASE), cb.count(event));
		cq.groupBy(event.get(Event.DISEASE));
		
		filter = null;
		if (from != null || to != null) {
			filter = cb.between(event.get(Event.REPORT_DATE_TIME), from, to);
		}
		if (districtRef != null) {
			Predicate districtFilter = cb.equal(eventLocation.join(Location.DISTRICT, JoinType.LEFT).get(District.UUID), districtRef.getUuid());
			filter = filter != null ? cb.and(filter, districtFilter) : districtFilter;
		}
		else if (regionRef != null) {
			Predicate regionFilter = cb.equal(eventLocation.join(Location.REGION, JoinType.LEFT).get(Region.UUID), regionRef.getUuid());
			filter = filter != null ? cb.and(filter, regionFilter) : regionFilter;
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		List<DiseaseBurdenDto> events = em.createQuery(cq).getResultList();
		
		//build diseases
		for (DiseaseBurdenDto disease : diseases) {
			DiseaseBurdenDto _case = cases.stream().filter(p -> p.getDisease() == disease.getDisease()).findFirst().orElse(null);
			if (_case != null) {
				disease.setCaseCount(_case.getCaseCount());
				disease.setOutbreakDistrictCount(_case.getEventCount());
			}
			
			_case = previousCases.stream().filter(p -> p.getDisease() == disease.getDisease()).findFirst().orElse(null);
			if (_case != null) 
				disease.setPreviousCaseCount(_case.getCaseCount());
			
			DiseaseBurdenDto _event = events.stream().filter(p -> p.getDisease() == disease.getDisease()).findFirst().orElse(null);
			if (_event != null)
				disease.setEventCount(_event.getCaseCount());
		}
		
		return diseases;
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