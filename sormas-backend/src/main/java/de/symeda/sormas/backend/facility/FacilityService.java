package de.symeda.sormas.backend.facility;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class FacilityService extends AbstractAdoService<Facility> {

	private final String OTHER_FACILITY_UUID = "SORMAS-CONSTID-OTHERS-FACILITY";
	
	public FacilityService() {
		super(Facility.class);
	}
	
	public List<Facility> getAllByCommunity(Community community, boolean includeOther) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.COMMUNITY), community));
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();
		
		if (includeOther) {
			cq.where(cb.equal(from.get(Facility.UUID), OTHER_FACILITY_UUID));
			facilities.add(0, em.createQuery(cq).getSingleResult());
		}
		
		return facilities;
	}
	
	public List<Facility> getAllByDistrict(District district, boolean includeOther) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.DISTRICT), district));
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();
		
		if (includeOther) {
			cq.where(cb.equal(from.get(Facility.UUID), OTHER_FACILITY_UUID));
			facilities.add(0, em.createQuery(cq).getSingleResult());
		}
		
		return facilities;
	}
	
	public List<Facility> getAllByRegionAfter(Region region, Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Facility.REGION), region);
		if (date != null) {
			filter = cb.and(filter, cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date));
		}
		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		return em.createQuery(cq).getResultList();
	}
	
	public List<Facility> getAllByFacilityType(FacilityType type, boolean includeOther) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.TYPE), type));
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();
		
		if (includeOther) {
			cq.where(cb.equal(from.get(Facility.UUID), OTHER_FACILITY_UUID));
			facilities.add(0, em.createQuery(cq).getSingleResult());
		}
		
		return facilities;
	}

	@Override
	protected Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Facility, Facility> from, User user) {
		throw new UnsupportedOperationException();
	}

	public String getOtherFacilityUuid() {
		return OTHER_FACILITY_UUID;
	}
	
}
