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
package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.region.CommunityCriteria;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.InfrastructureDataImporter;
import de.symeda.sormas.backend.util.InfrastructureDataImporter.CommunityConsumer;

@Stateless
@LocalBean
public class CommunityService extends AbstractAdoService<Community> {
	
	public CommunityService() {
		super(Community.class);
	}

	public List<Community> getByName(String name, District district) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Community> cq = cb.createQuery(getElementClass());
		Root<Community> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Community.NAME), name);
		if (district != null) {
			filter = cb.and(filter, cb.equal(from.get(Community.DISTRICT), district));
		}
		
		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Community, Community> from, User user) {
		// no filter by user needed
		return null;
	}

	public void importCommunities(String countryName, List<Region> regions) {
		
		InfrastructureDataImporter.importCommunities(countryName, new CommunityConsumer() {
			
			private Region cachedRegion = null;
			private District cachedDistrict = null;
			
			@Override
			public void consume(String regionName, String districtName, String communityName) {
					
					if (cachedRegion == null || !cachedRegion.getName().equals(regionName)) {
						Optional<Region> regionResult = regions.stream()
								.filter(r -> r.getName().equals(regionName))
								.findFirst();

						if (regionResult.isPresent()) {
							cachedRegion = regionResult.get();
						} else {
							logger.warn("Could not find region '" + regionName + "' for district '" + districtName + "' in community '" + communityName + "'");
							return;
						}
					}
					
					if (cachedDistrict == null || !cachedDistrict.getName().equals(districtName)) {
						Optional<District> districtResult = cachedRegion.getDistricts().stream()
								.filter(r -> r.getName().equals(districtName))
								.findFirst();

						if (districtResult.isPresent()) {
							cachedDistrict = districtResult.get();
						} else {
							logger.warn("Could not find district '" + districtName + "' for community '" + communityName + "'");
							return;
						}
						
						if (cachedDistrict.getCommunities() == null) {
							cachedDistrict.setCommunities(new ArrayList<Community>());
						}
					}

					Optional<Community> communityResult = cachedDistrict.getCommunities().stream()
							.filter(r -> r.getName().equals(communityName))
							.findFirst();
					
					Community community;
					if (communityResult.isPresent()) {
						community = communityResult.get();
					} else {
						community = new Community();
						cachedDistrict.getCommunities().add(community);
						community.setName(communityName);
						community.setDistrict(cachedDistrict);
					}
					
					persist(community);
			}
		});
	}

	public Predicate buildCriteriaFilter(CommunityCriteria communityCriteria, CriteriaBuilder cb, Root<Community> from) {
		Join<Community, District> district = from.join(Community.DISTRICT, JoinType.LEFT);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);
		Predicate filter = null;
		if (communityCriteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(region.get(Region.UUID), communityCriteria.getRegion().getUuid()));
		}
		if (communityCriteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(district.get(District.UUID), communityCriteria.getDistrict().getUuid()));
		}
		return filter;
	}
}
