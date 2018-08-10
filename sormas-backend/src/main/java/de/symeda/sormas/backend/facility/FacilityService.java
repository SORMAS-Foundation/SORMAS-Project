package de.symeda.sormas.backend.facility;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.InfrastructureDataImporter;
import de.symeda.sormas.backend.util.InfrastructureDataImporter.FacilityConsumer;

/**
 * @author Christopher Riedel
 *
 */
@Stateless
@LocalBean
public class FacilityService extends AbstractAdoService<Facility> {

	@EJB
	private RegionService regionService;

	public FacilityService() {
		super(Facility.class);
	}

	public List<Facility> getHealthFacilitiesByCommunity(Community community, boolean includeStaticFacilities) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.or(cb.notEqual(from.get(Facility.TYPE), FacilityType.LABORATORY),
				cb.isNull(from.get(Facility.TYPE)));
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

		Predicate filter = cb.or(cb.notEqual(from.get(Facility.TYPE), FacilityType.LABORATORY),
				cb.isNull(from.get(Facility.TYPE)));
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

		Predicate filter = cb.or(cb.notEqual(from.get(Facility.TYPE), FacilityType.LABORATORY),
				cb.isNull(from.get(Facility.TYPE)));
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

		Predicate filter = null;
		if (region != null) {
			filter = cb.equal(from.get(Facility.REGION), region);
		}
		if (date != null) {
			if (filter != null) {
				filter = cb.and(filter, createDateFilter(cb, cq, from, date));
			} else {
				filter = createDateFilter(cb, cq, from, date);
			}
		}
		if (filter != null) {
			cq.where(filter);
		}
		
		// order by district, community so the app can do caching while reading the data
		cq.orderBy(cb.asc(from.get(Facility.DISTRICT)), cb.asc(from.get(Facility.COMMUNITY)),
				cb.asc(from.get(Facility.NAME)));

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

	public List<Facility> getAllLaboratories(boolean includeOtherLaboratory) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		cq.where(cb.and(cb.equal(from.get(Facility.TYPE), FacilityType.LABORATORY),
				cb.notEqual(from.get(Facility.UUID), FacilityDto.OTHER_LABORATORY_UUID)));
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();

		if (includeOtherLaboratory) {
			facilities.add(getByUuid(FacilityDto.OTHER_LABORATORY_UUID));
		}

		return facilities;
	}

	public List<Facility> getHealthFacilitiesByName(String name, District district, Community community) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(Facility.NAME), name);
		// Additional null check is required because notEqual returns true if one of the
		// values is null
		filter = cb.and(filter, cb.or(cb.isNull(from.get(Facility.TYPE)),
				cb.notEqual(from.get(Facility.TYPE), FacilityType.LABORATORY)));
		if (community != null) {
			filter = cb.and(filter, cb.equal(from.get(Facility.COMMUNITY), community));
		} else if (district != null) {
			filter = cb.and(filter, cb.equal(from.get(Facility.DISTRICT), district));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Facility, Facility> from, User user) {
		// no fitler by user needed
		return null;
	}

	public void importFacilities(String countryName) {

		Timestamp latestChangeDate = getLatestChangeDate();
		if (latestChangeDate == null
				// update needed due to bugfix
				// TODO replace with solution that reads the change date from the file or
				// something else
				|| latestChangeDate.before(DateHelper.getDateZero(2017, 11, 27))) {

			List<Facility> facilities = getAll();
			List<Region> regions = regionService.getAll();

			for (Region region : regions) {

				InfrastructureDataImporter.importFacilities(countryName, region, new FacilityConsumer() {

					private District cachedDistrict = null;
					private Community cachedCommunity = null;

					@Override
					public void consume(String regionName, String districtName, String communityName,
							String facilityName, String city, String address, Double latitude, Double longitude,
							FacilityType type, boolean publicOwnership) {

						if (cachedDistrict == null || !cachedDistrict.getName().equals(districtName)) {
							Optional<District> districtResult = region.getDistricts().stream()
									.filter(r -> r.getName().equals(districtName)).findFirst();

							if (districtResult.isPresent()) {
								cachedDistrict = districtResult.get();
							} else {
								logger.warn("Could not find district '" + districtName + "' for facility '"
										+ facilityName + "'");
								return;
							}
						}

						if (cachedCommunity == null || !cachedCommunity.getName().equals(communityName)) {
							Optional<Community> communityResult = cachedDistrict.getCommunities().stream()
									.filter(r -> r.getName().equals(communityName)).findFirst();

							if (communityResult.isPresent()) {
								cachedCommunity = communityResult.get();
							} else {
								logger.warn("Could not find community '" + communityName + "' for facility '"
										+ facilityName + "'");
								return;
							}
						}

						Optional<Facility> facilityResult = facilities.stream()
								.filter(r -> r.getName().equals(facilityName) && DataHelper.equal(r.getRegion(), region)
										&& DataHelper.equal(r.getDistrict(), cachedDistrict)
										&& DataHelper.equal(r.getCommunity(), cachedCommunity))
								.findFirst();

						Facility facility;
						if (facilityResult.isPresent()) {
							facility = facilityResult.get();
						} else {
							facility = new Facility();
							facility.setName(facilityName);
							facility.setRegion(region);
							facility.setDistrict(cachedDistrict);
							facility.setCommunity(cachedCommunity);
						}

						facility.setCity(city);
						facility.setLatitude(latitude);
						facility.setLongitude(longitude);
						facility.setPublicOwnership(publicOwnership);
						facility.setType(type);

						persist(facility);
					}
				});
			}

			importLaboratories(countryName, regions, facilities);
		}

		// Add 'Other' health facility with a constant UUID that is not
		// associated with a specific region
		if (getByUuid(FacilityDto.OTHER_FACILITY_UUID) == null) {
			Facility otherFacility = new Facility();
			otherFacility.setName("OTHER_FACILITY");
			otherFacility.setUuid(FacilityDto.OTHER_FACILITY_UUID);
			persist(otherFacility);
		}

		// Add 'None' health facility with a constant UUID that is not
		// associated with a specific region
		if (getByUuid(FacilityDto.NONE_FACILITY_UUID) == null) {
			Facility noneFacility = new Facility();
			noneFacility.setName("NO_FACILITY");
			noneFacility.setUuid(FacilityDto.NONE_FACILITY_UUID);
			persist(noneFacility);
		}

		// Add 'Other' laboratory with a constant UUID
		if (getByUuid(FacilityDto.OTHER_LABORATORY_UUID) == null) {
			Facility otherLaboratory = new Facility();
			otherLaboratory.setName("OTHER_LABORATORY");
			otherLaboratory.setUuid(FacilityDto.OTHER_LABORATORY_UUID);
			otherLaboratory.setType(FacilityType.LABORATORY);
			persist(otherLaboratory);
		}
	}

	private void importLaboratories(String countryName, List<Region> regions, List<Facility> facilities) {

		InfrastructureDataImporter.importLaboratories(countryName, new FacilityConsumer() {

			private Region cachedRegion = null;
			private District cachedDistrict = null;
			private Community cachedCommunity = null;

			@Override
			public void consume(String regionName, String districtName, String communityName, String facilityName,
					String city, String address, Double latitude, Double longitude, FacilityType type,
					boolean publicOwnership) {

				if (DataHelper.isNullOrEmpty(regionName)) {
					cachedRegion = null; // no region is ok
				} else if (cachedRegion == null || !cachedRegion.getName().equals(regionName)) {
					Optional<Region> regionResult = regions.stream().filter(r -> r.getName().equals(regionName))
							.findFirst();

					if (regionResult.isPresent()) {
						cachedRegion = regionResult.get();
					} else {
						logger.warn("Could not find region '" + regionName + "' for facility '" + facilityName + "'");
						cachedRegion = null;
					}
				}

				if (cachedRegion == null || DataHelper.isNullOrEmpty(districtName)) {
					cachedDistrict = null; // no district is ok
				} else if (cachedDistrict == null || !cachedDistrict.getName().equals(districtName)) {
					Optional<District> districtResult = cachedRegion.getDistricts().stream()
							.filter(r -> r.getName().equals(districtName)).findFirst();

					if (districtResult.isPresent()) {
						cachedDistrict = districtResult.get();
					} else {
						logger.warn(
								"Could not find district '" + districtName + "' for facility '" + facilityName + "'");
						cachedDistrict = null;
					}
				}

				if (cachedDistrict == null || DataHelper.isNullOrEmpty(communityName)) {
					cachedCommunity = null; // no community is ok
				} else if (cachedCommunity == null || !cachedCommunity.getName().equals(communityName)) {
					Optional<Community> communityResult = cachedDistrict.getCommunities().stream()
							.filter(r -> r.getName().equals(communityName)).findFirst();

					if (communityResult.isPresent()) {
						cachedCommunity = communityResult.get();
					} else {
						logger.warn(
								"Could not find community '" + communityName + "' for facility '" + facilityName + "'");
						cachedCommunity = null;
					}
				}

				Optional<Facility> facilityResult = facilities.stream()
						.filter(r -> r.getName().equals(facilityName) && DataHelper.equal(r.getRegion(), cachedRegion)
								&& DataHelper.equal(r.getDistrict(), cachedDistrict)
								&& DataHelper.equal(r.getCommunity(), cachedCommunity))
						.findFirst();

				Facility facility;
				if (facilityResult.isPresent()) {
					facility = facilityResult.get();
				} else {
					facility = new Facility();
					facility.setName(facilityName);
					facility.setRegion(cachedRegion);
					facility.setDistrict(cachedDistrict);
					facility.setCommunity(cachedCommunity);
				}

				facility.setCity(city);
				facility.setLatitude(latitude);
				facility.setLongitude(longitude);
				facility.setPublicOwnership(publicOwnership);
				facility.setType(type);

				persist(facility);
			}
		});
	}

	public Predicate buildCriteriaFilter(FacilityCriteria facilityCriteria, CriteriaBuilder cb,
			Root<Facility> from) {
		Predicate filter = null;
		if (facilityCriteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(Facility.REGION, JoinType.LEFT).get(Region.UUID), facilityCriteria.getRegion().getUuid()));
		}
		if (facilityCriteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(Facility.DISTRICT, JoinType.LEFT).get(District.UUID), facilityCriteria.getDistrict().getUuid()));
		}
		if (facilityCriteria.getCommunity() != null) {
			filter = and(cb, filter, cb.equal(from.join(Facility.COMMUNITY, JoinType.LEFT).get(District.UUID), facilityCriteria.getCommunity().getUuid()));
		}
		filter = and(cb, filter, cb.equal(from.get(Facility.TYPE), facilityCriteria.getType()));
		return filter;
	}
}
