package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

	public Community getByName(String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Community> cq = cb.createQuery(getElementClass());
		Root<Community> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Community.NAME), name));

		return em.createQuery(cq).getResultList().stream()
				.findFirst()
				.orElse(null);
	}
	
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Community, Community> from, User user) {
		// no fitler by user needed
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
}
