package de.symeda.sormas.backend.infrastructure;

import java.util.ArrayList;
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

import de.symeda.sormas.api.infrastructure.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.InfrastructureDataImporter;
import de.symeda.sormas.backend.util.InfrastructureDataImporter.PointOfEntryConsumer;

@Stateless
@LocalBean
public class PointOfEntryService extends AbstractAdoService<PointOfEntry> {

	@EJB
	private RegionService regionService;
	
	public PointOfEntryService() {
		super(PointOfEntry.class);
	}
	
	public List<PointOfEntry> getAllByDistrict(District district, boolean includeOthers) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(getElementClass());
		Root<PointOfEntry> pointOfEntry = cq.from(getElementClass());
		
		Predicate filter = cb.and(
				cb.equal(pointOfEntry.get(PointOfEntry.DISTRICT), district),
				cb.equal(pointOfEntry.get(PointOfEntry.ACTIVE), true));

		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(pointOfEntry.get(PointOfEntry.NAME)));
		
		List<PointOfEntry> pointsOfEntry = em.createQuery(cq).getResultList();
		
		if (includeOthers) {
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_AIRPORT_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_SEAPORT_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_POE_UUID));
		}
		
		return pointsOfEntry;
	}

	public void importPointsOfEntry(String countryName) {
		InfrastructureDataImporter.importPointsOfEntry(countryName, new PointOfEntryConsumer() {
			
			private Region cachedRegion = null;
			private District cachedDistrict = null;
			
			@Override
			public void consume(String regionName, String districtName, String pointOfEntryName, PointOfEntryType type, boolean active, Double latitude, Double longitude) {
				if (cachedRegion == null || !cachedRegion.getName().equals(regionName)) {
					Optional<Region> regionResult = regionService.getAll().stream()
							.filter(r -> r.getName().toLowerCase().trim().equals(regionName.toLowerCase().trim()))
							.findFirst();

					if (regionResult.isPresent()) {
						cachedRegion = regionResult.get();
					} else {
						logger.warn("Could not find region '" + regionName + "' for district '" + districtName + "' in point of entry '" + pointOfEntryName + "'");
						return;
					}
				}
				
				if (cachedDistrict == null || !cachedDistrict.getName().equals(districtName)) {
					Optional<District> districtResult = cachedRegion.getDistricts().stream()
							.filter(r -> r.getName().toLowerCase().trim().equals(districtName.toLowerCase().trim()))
							.findFirst();

					if (districtResult.isPresent()) {
						cachedDistrict = districtResult.get();
					} else {
						logger.warn("Could not find district '" + districtName + "' for point of entry '" + pointOfEntryName + "'");
						return;
					}
					
					if (cachedDistrict.getPointsOfEntry() == null) {
						cachedDistrict.setPointsOfEntry(new ArrayList<PointOfEntry>());
					}
				}
				
				Optional<PointOfEntry> pointOfEntryResult = cachedDistrict.getPointsOfEntry().stream()
						.filter(p -> p.getName().equals(pointOfEntryName))
						.findFirst();
				
				PointOfEntry pointOfEntry;
				if (pointOfEntryResult.isPresent()) {
					pointOfEntry = pointOfEntryResult.get();
				} else {
					pointOfEntry = new PointOfEntry();
					cachedDistrict.getPointsOfEntry().add(pointOfEntry);
					pointOfEntry.setName(pointOfEntryName);
					pointOfEntry.setRegion(cachedRegion);
					pointOfEntry.setDistrict(cachedDistrict);
				}
				
				pointOfEntry.setPointOfEntryType(type);
				pointOfEntry.setActive(active);
				pointOfEntry.setLatitude(latitude);
				pointOfEntry.setLongitude(longitude);
				
				persist(pointOfEntry);
			}
		});
		
		if (getByUuid(PointOfEntryDto.OTHER_AIRPORT_UUID) == null) {
			PointOfEntry otherAirportPoe = new PointOfEntry();
			otherAirportPoe.setName(PointOfEntryDto.OTHER_AIRPORT);
			otherAirportPoe.setUuid(PointOfEntryDto.OTHER_AIRPORT_UUID);
			otherAirportPoe.setPointOfEntryType(PointOfEntryType.AIRPORT);
			otherAirportPoe.setActive(true);
			persist(otherAirportPoe);
		}
		
		if (getByUuid(PointOfEntryDto.OTHER_SEAPORT_UUID) == null) {
			PointOfEntry otherAirportPoe = new PointOfEntry();
			otherAirportPoe.setName(PointOfEntryDto.OTHER_SEAPORT);
			otherAirportPoe.setUuid(PointOfEntryDto.OTHER_SEAPORT_UUID);
			otherAirportPoe.setPointOfEntryType(PointOfEntryType.SEAPORT);
			otherAirportPoe.setActive(true);
			persist(otherAirportPoe);
		}
		
		if (getByUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID) == null) {
			PointOfEntry otherAirportPoe = new PointOfEntry();
			otherAirportPoe.setName(PointOfEntryDto.OTHER_GROUND_CROSSING);
			otherAirportPoe.setUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID);
			otherAirportPoe.setPointOfEntryType(PointOfEntryType.GROUND_CROSSING);
			otherAirportPoe.setActive(true);
			persist(otherAirportPoe);
		}
		
		if (getByUuid(PointOfEntryDto.OTHER_POE_UUID) == null) {
			PointOfEntry otherAirportPoe = new PointOfEntry();
			otherAirportPoe.setName(PointOfEntryDto.OTHER_POE);
			otherAirportPoe.setUuid(PointOfEntryDto.OTHER_POE_UUID);
			otherAirportPoe.setPointOfEntryType(PointOfEntryType.OTHER);
			otherAirportPoe.setActive(true);
			persist(otherAirportPoe);
		}
	}
	
	public Predicate buildCriteriaFilter(PointOfEntryCriteria criteria, CriteriaBuilder cb, Root<PointOfEntry> pointOfEntry) {
		Predicate filter = null;
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(pointOfEntry.join(PointOfEntry.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(pointOfEntry.join(PointOfEntry.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getType() != null) {
			filter = and(cb, filter, cb.equal(pointOfEntry.get(PointOfEntry.POINT_OF_ENTRY_TYPE), criteria.getType()));
		}
		if (criteria.getActive() != null) {
			filter = and(cb, filter, cb.equal(pointOfEntry.get(PointOfEntry.ACTIVE), criteria.getActive()));
		}
		if (criteria.getNameLike() != null) {
			String[] textFilters = criteria.getNameLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.like(cb.lower(pointOfEntry.get(PointOfEntry.NAME)), textFilter);
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		
		return filter;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<PointOfEntry, PointOfEntry> from, User user) {
		return null;
	}

}
