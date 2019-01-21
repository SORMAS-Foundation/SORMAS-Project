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

import java.util.Date;
import java.util.List;

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
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Provides the application configuration settings
 */
@Stateless(name="DiseaseFacade")
public class DiseaseFacadeEjb implements DiseaseFacade {
	
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
	@Override
	public List<DiseaseBurdenDto> getDiseaseBurdenForDashboard(
			RegionReferenceDto regionRef,
			DistrictReferenceDto districtRef, 
			Date from, 
			Date to, 
			String userUuid) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DiseaseBurdenDto> cq = cb.createQuery(DiseaseBurdenDto.class);
		Root<Case> caze = cq.from(Case.class);
//		Join<Event, Location> location = caze.join(Event.EVENT_LOCATION, JoinType.LEFT);
//		Join<Location, Region> region = location.join(Location.REGION, JoinType.LEFT);
//		Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);
//		Join<Location, Community> community = location.join(Location.COMMUNITY, JoinType.LEFT);
		
		//caze.
		
		//caze.get(Case.DISEASE);
		
		cq.multiselect(
			caze.get(Case.DISEASE)
			//cb.count(caze)
			//0, 0, 0, 0.00f
		);
		
		cq.groupBy(caze.get(Case.DISEASE));
		
		Predicate filter = null;
		
		List<DiseaseBurdenDto> resultList = em.createQuery(cq).getResultList();
		
		return resultList;
	}
	
	@LocalBean
	@Stateless
	public static class DiseaseFacadeEjbLocal extends DiseaseFacadeEjb {
	}
	
}