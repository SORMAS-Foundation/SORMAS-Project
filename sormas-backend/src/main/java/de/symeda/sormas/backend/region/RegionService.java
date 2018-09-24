package de.symeda.sormas.backend.region;

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
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.InfrastructureDataImporter;

@Stateless
@LocalBean
public class RegionService extends AbstractAdoService<Region> {
	
	public RegionService() {
		super(Region.class);
	}
	
	public List<Region> getAllWithoutEpidCode() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());
		cq.where(cb.isNull(from.get(Region.EPID_CODE)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	public List<Region> getAllWithoutPopulationData() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());
		cq.where(cb.isNull(from.get(Region.POPULATION)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	public List<Region> getByName(String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(getElementClass());
		Root<Region> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Region.NAME), name));

		return em.createQuery(cq).getResultList();
	}
	
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Region, Region> from, User user) {
		// no fitler by user needed
		return null;
	}
	
	public void importRegions(String countryName, List<Region> regions) {
		
		InfrastructureDataImporter.importRegions(countryName, 
			(regionName, epidCode, population, growthRate) -> {
				
				Optional<Region> regionResult = regions.stream()
						.filter(r -> r.getName().equals(regionName))
						.findFirst();
				
				Region region;
				if (regionResult.isPresent()) {
					region = regionResult.get();
				} else {
					region = new Region();
					regions.add(region);
					region.setName(regionName);
				}
				
				region.setEpidCode(epidCode);
				region.setPopulation(population);
				region.setGrowthRate(growthRate);
	
				persist(region);
			});
	}
}
