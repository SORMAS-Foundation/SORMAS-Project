package de.symeda.sormas.backend.facility;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;

@Stateless
@LocalBean
public class FacilityService extends AbstractAdoService<Facility> {
	
	public FacilityService() {
		super(Facility.class);
	}
	
	public List<Facility> getAllByCommunity(Community community) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.LOCATION).get(Location.COMMUNITY), community));
		
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		return em.createQuery(cq).getResultList();
	}
	
	public List<Facility> getAllByDistrict(District district) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.LOCATION).get(Location.DISTRICT), district));
		
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		return em.createQuery(cq).getResultList();
	}
}
