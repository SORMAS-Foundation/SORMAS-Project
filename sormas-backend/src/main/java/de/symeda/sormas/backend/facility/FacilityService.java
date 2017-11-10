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

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class FacilityService extends AbstractAdoService<Facility> {
	
	public FacilityService() {
		super(Facility.class);
	}
	
	public List<Facility> getHealthFacilitiesByCommunity(Community community, boolean includeStaticFacilities) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.or(cb.notEqual(from.get(Facility.TYPE), FacilityType.LABORATORY), cb.isNull(from.get(Facility.TYPE)));
		filter = cb.and(filter, cb.equal(from.get(Facility.COMMUNITY), community));
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();
		
		if (includeStaticFacilities) {			
			facilities.add(getByUuid(FacilityDto.OTHER_FACILITY_UUID));
			facilities.add(getByUuid(FacilityDto.NONE_FACILITY_UUID));
		}
		
		return facilities;
	}
	
	public List<Facility> getHealthFacilitiesByDistrict(District district, boolean includeStaticFacilities) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.or(cb.notEqual(from.get(Facility.TYPE), FacilityType.LABORATORY), cb.isNull(from.get(Facility.TYPE)));
		filter = cb.and(filter, cb.equal(from.get(Facility.DISTRICT), district));
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();
		
		if (includeStaticFacilities) {
			facilities.add(getByUuid(FacilityDto.OTHER_FACILITY_UUID));
			facilities.add(getByUuid(FacilityDto.NONE_FACILITY_UUID));
		}
		
		return facilities;
	}
	
	public List<Facility> getHealthFacilitiesByRegion(Region region, boolean includeStaticFacilities) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.or(cb.notEqual(from.get(Facility.TYPE), FacilityType.LABORATORY), cb.isNull(from.get(Facility.TYPE)));
		filter = cb.and(filter, cb.equal(from.get(Facility.REGION), region));
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();
		
		if (includeStaticFacilities) {
			facilities.add(getByUuid(FacilityDto.OTHER_FACILITY_UUID));
			facilities.add(getByUuid(FacilityDto.NONE_FACILITY_UUID));
		}
		
		return facilities;
	}
	
	public List<Facility> getAllByRegionAfter(Region region, Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Facility.REGION), region);
		if (date != null) {
			filter = cb.and(filter, createDateFilter(cb, cq, from, date));
		}
		cq.where(filter);
		// order by district, community so the app can do caching while reading the data
		cq.orderBy(cb.asc(from.get(Facility.DISTRICT)), cb.asc(from.get(Facility.COMMUNITY)), cb.asc(from.get(Facility.NAME)));

		return em.createQuery(cq).getResultList();
	}
	
	public List<Facility> getAllWithoutRegionAfter(Date date) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		Predicate filter = cb.isNull(from.get(Facility.REGION));
		if (date != null) {
			filter = cb.and(filter, createDateFilter(cb, cq, from, date));
		}
		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));
		
		return em.createQuery(cq).getResultList();
	}
	
	public List<Facility> getAllLaboratories() {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Facility.TYPE), FacilityType.LABORATORY));
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();
		
		return facilities;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Facility, Facility> from, User user) {
		// no fitler by user needed
		return null;
	}
	
}
