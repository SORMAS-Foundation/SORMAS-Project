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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.facility;

import java.util.List;
import java.util.function.BiFunction;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class FacilityService extends AbstractInfrastructureAdoService<Facility> {

	@EJB
	private RegionService regionService;

	@EJB
	private UserService userService;

	public FacilityService() {
		super(Facility.class);
	}

	public List<Facility> getActiveFacilitiesByCommunityAndType(
		Community community,
		FacilityType type,
		boolean includeOtherFacility,
		boolean includeNoneFacility) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = createBasicFilter(cb, from);
		if (type != null) {
			filter = cb.and(filter, cb.equal(from.get(Facility.TYPE), type));
		}
		filter = cb.and(filter, cb.equal(from.get(Facility.COMMUNITY), community));
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();

		if (includeOtherFacility) {
			facilities.add(getByUuid(FacilityDto.OTHER_FACILITY_UUID));
		}
		if (includeNoneFacility) {
			facilities.add(getByUuid(FacilityDto.NONE_FACILITY_UUID));
		}

		return facilities;
	}

	public List<Facility> getActiveFacilitiesByDistrictAndType(
		District district,
		FacilityType type,
		boolean includeOtherFacility,
		boolean includeNoneFacility) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = createBasicFilter(cb, from);
		if (type != null) {
			filter = cb.and(filter, cb.equal(from.get(Facility.TYPE), type));
		}
		filter = cb.and(filter, cb.equal(from.get(Facility.DISTRICT), district));
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();

		if (includeOtherFacility) {
			facilities.add(getByUuid(FacilityDto.OTHER_FACILITY_UUID));
		}
		if (includeNoneFacility) {
			facilities.add(getByUuid(FacilityDto.NONE_FACILITY_UUID));
		}

		return facilities;
	}

	public List<Facility> getAllActiveLaboratories(boolean includeOtherFacility) {
		return getAllActiveLaboratories(includeOtherFacility, null);
	}

	private List<Facility> getAllActiveLaboratories(
		boolean includeOtherFacility,
		BiFunction<CriteriaBuilder, Root<Facility>, Predicate> createExtraFilters) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.and(
			createBasicFilter(cb, from),
			cb.equal(from.get(Facility.TYPE), FacilityType.LABORATORY),
			cb.notEqual(from.get(Facility.UUID), FacilityDto.OTHER_FACILITY_UUID)

		);

		if (createExtraFilters != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createExtraFilters.apply(cb, from));
		}

		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Facility.NAME)));

		List<Facility> facilities = em.createQuery(cq).getResultList();

		if (includeOtherFacility) {
			facilities.add(getByUuid(FacilityDto.OTHER_FACILITY_UUID));
		}

		return facilities;
	}

	public List<Facility> getFacilitiesByNameAndType(
		String name,
		District district,
		Community community,
		FacilityType type,
		boolean includeArchivedEntities) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.or(
			cb.equal(cb.trim(from.get(Facility.NAME)), name.trim()),
			cb.equal(cb.lower(cb.trim(from.get(Facility.NAME))), name.trim().toLowerCase()));
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		// Don't check for district and community equality or type equality when searching for constant facilities
		if (!FacilityDto.OTHER_FACILITY.equals(name.trim()) && !FacilityDto.NO_FACILITY.equals(name.trim())) {
			if (community != null) {
				filter = cb.and(filter, cb.equal(from.get(Facility.COMMUNITY), community));
			} else if (district != null) {
				filter = cb.and(filter, cb.equal(from.get(Facility.DISTRICT), district));
			}

			if (type != null) {
				filter = cb.and(filter, cb.equal(from.get(Facility.TYPE), type));
			}
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Facility> from) {
		// no filter by user needed
		return null;
	}

	public Predicate buildCriteriaFilter(FacilityCriteria facilityCriteria, CriteriaBuilder cb, Root<Facility> from) {
		Predicate filter = null;
		if (facilityCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.join(Facility.REGION, JoinType.LEFT).get(Region.UUID), facilityCriteria.getRegion().getUuid()));
		}
		if (facilityCriteria.getDistrict() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.join(Facility.DISTRICT, JoinType.LEFT).get(District.UUID), facilityCriteria.getDistrict().getUuid()));
		}
		if (facilityCriteria.getCommunity() != null) {
			filter =
					CriteriaBuilderHelper.and(cb, filter, cb.equal(from.join(Facility.COMMUNITY, JoinType.LEFT).get(District.UUID), facilityCriteria.getCommunity().getUuid()));
		}
		if (facilityCriteria.getNameCityLike() != null) {
			String[] textFilters = facilityCriteria.getNameCityLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters =
						cb.or(cb.like(cb.lower(from.get(Facility.NAME)), textFilter), cb.like(cb.lower(from.get(Facility.CITY)), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}
		if (facilityCriteria.getType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Facility.TYPE), facilityCriteria.getType()));
		} else {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(from.get(Facility.TYPE)));
		}
		if (facilityCriteria.getRelevanceStatus() != null) {
			if (facilityCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Facility.ARCHIVED), false), cb.isNull(from.get(Facility.ARCHIVED))));
			} else if (facilityCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Facility.ARCHIVED), true));
			}
		}
		return filter;
	}

	public void createConstantFacilities() {

		if (getByUuid(FacilityDto.OTHER_FACILITY_UUID) == null) {
			Facility otherFacility = new Facility();
			otherFacility.setName(FacilityDto.OTHER_FACILITY);
			otherFacility.setUuid(FacilityDto.OTHER_FACILITY_UUID);
			persist(otherFacility);
		}
		if (getByUuid(FacilityDto.NONE_FACILITY_UUID) == null) {
			Facility noneFacility = new Facility();
			noneFacility.setName(FacilityDto.NO_FACILITY);
			noneFacility.setUuid(FacilityDto.NONE_FACILITY_UUID);
			persist(noneFacility);
		}
	}
}
