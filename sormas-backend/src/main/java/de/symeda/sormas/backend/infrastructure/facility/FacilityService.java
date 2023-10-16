/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.infrastructure.facility;

import java.util.List;
import java.util.function.BiFunction;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class FacilityService extends AbstractInfrastructureAdoService<Facility, FacilityCriteria> {

	@EJB
	private RegionService regionService;

	@EJB
	private UserService userService;

	@EJB
	private CountryFacadeEjbLocal countryFacade;

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

	public Facility getByAddress(String street, String postalCode, String city) {

		if (StringUtils.isAnyBlank(street, postalCode, city)) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.and(
			createBasicFilter(cb, from),
			cb.equal(cb.lower(cb.trim(from.get(Facility.STREET))), street.trim().toLowerCase()),
			cb.equal(cb.lower(cb.trim(from.get(Facility.POSTAL_CODE))), postalCode.trim().toLowerCase()),
			cb.equal(cb.lower(cb.trim(from.get(Facility.CITY))), city.trim().toLowerCase()));

		cq.where(filter);

		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NonUniqueResultException e) {
			logger.warn("getByAddress returned more than one result for the specified street, postal code, and city");
			return null;
		} catch (NoResultException e) {
			logger.warn("getByAddress returned no result for the specified street, postal code, and city");
			return null;
		}
	}

	public List<Facility> getFacilitiesByExternalIdAndType(@NotNull String externalId, FacilityType type, boolean includeArchivedEntities) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Facility> cq = cb.createQuery(getElementClass());
		Root<Facility> from = cq.from(getElementClass());

		Predicate filter = cb.or(
			cb.equal(cb.trim(from.get(Facility.EXTERNAL_ID)), externalId.trim()),
			cb.equal(cb.lower(cb.trim(from.get(Facility.EXTERNAL_ID))), externalId.trim().toLowerCase()));
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}
		if (type != null) {
			filter = cb.and(filter, cb.equal(from.get(Facility.TYPE), type));
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

	@Override
	public Predicate buildCriteriaFilter(FacilityCriteria facilityCriteria, CriteriaBuilder cb, Root<Facility> root) {

		// these two facilities are constant and created on startup by createConstantFacilities()
		Predicate excludeConstantFacilities = cb.and(
			cb.notEqual(root.get(Facility.UUID), FacilityDto.OTHER_FACILITY_UUID),
			cb.notEqual(root.get(Facility.UUID), FacilityDto.NONE_FACILITY_UUID));

		if (facilityCriteria == null) {
			return excludeConstantFacilities;
		}

		Predicate filter = null;

		CountryReferenceDto country = facilityCriteria.getCountry();
		if (country != null) {
			CountryReferenceDto serverCountry = countryFacade.getServerCountry();

			Path<Object> countryUuid = root.join(Facility.REGION, JoinType.LEFT).join(Region.COUNTRY, JoinType.LEFT).get(Country.UUID);
			Predicate countryFilter = cb.equal(countryUuid, country.getUuid());

			if (country.equals(serverCountry)) {
				filter = CriteriaBuilderHelper.and(cb, filter, CriteriaBuilderHelper.or(cb, countryFilter, countryUuid.isNull()));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, countryFilter);
			}
		}

		if (facilityCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(root.join(Facility.REGION, JoinType.LEFT).get(Region.UUID), facilityCriteria.getRegion().getUuid()));
		}
		if (facilityCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(root.join(Facility.DISTRICT, JoinType.LEFT).get(District.UUID), facilityCriteria.getDistrict().getUuid()));
		}
		if (facilityCriteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(root.join(Facility.COMMUNITY, JoinType.LEFT).get(District.UUID), facilityCriteria.getCommunity().getUuid()));
		}
		if (facilityCriteria.getNameAddressLike() != null) {
			String[] textFilters = facilityCriteria.getNameAddressLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, root.get(Facility.NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, root.get(Facility.POSTAL_CODE), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, root.get(Facility.CITY), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, root.get(Facility.STREET), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, root.get(Facility.HOUSE_NUMBER), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, root.get(Facility.ADDITIONAL_INFORMATION), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (facilityCriteria.getType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(Facility.TYPE), facilityCriteria.getType()));
		} else {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(root.get(Facility.TYPE)));
		}
		if (facilityCriteria.getRelevanceStatus() != null) {
			if (facilityCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(root.get(Facility.ARCHIVED), false), cb.isNull(root.get(Facility.ARCHIVED))));
			} else if (facilityCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(Facility.ARCHIVED), true));
			}
		}
		return CriteriaBuilderHelper.and(cb, filter, excludeConstantFacilities);
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

	@Override
	public List<Facility> getByExternalId(String externalId, boolean includeArchived) {
		return getByExternalId(externalId, Facility.EXTERNAL_ID, includeArchived);
	}
}
