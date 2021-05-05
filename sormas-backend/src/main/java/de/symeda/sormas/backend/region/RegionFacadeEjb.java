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
package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.region.RegionCriteria;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.region.RegionIndexDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.region.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "RegionFacade")
public class RegionFacadeEjb implements RegionFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private RegionService regionService;
	@EJB
	private UserService userService;
	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;
	@EJB
	private AreaService areaService;
	@EJB
	private CountryService countryService;
	@EJB
	private CountryFacadeEjbLocal countryFacade;

	@Override
	public List<RegionReferenceDto> getAllActiveByServerCountry() {
		CountryReferenceDto serverCountry = countryFacade.getServerCountry();

		return getAllActiveByPredicate((cb, root) -> {
			if (serverCountry != null) {
				Path<Object> countryUuid = root.join(Region.COUNTRY, JoinType.LEFT).get(Country.UUID);
				return CriteriaBuilderHelper.or(cb, cb.isNull(countryUuid), cb.equal(countryUuid, serverCountry.getUuid()));
			}

			return null;
		});
	}

	@Override
	public List<RegionReferenceDto> getAllActiveByCountry(String countryUuid) {
		return getAllActiveByPredicate((cb, root) -> cb.equal(root.get(Region.COUNTRY).get(Country.UUID), countryUuid));
	}

	@Override
	public List<RegionReferenceDto> getAllActiveByArea(String areaUuid) {
		return getAllActiveByPredicate((cb, root) -> cb.equal(root.get(Region.AREA).get(Area.UUID), areaUuid));
	}

	@Override
	public List<RegionReferenceDto> getAllActiveAsReference() {
		return regionService.getAllActive(Region.NAME, true).stream().map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<RegionDto> getAllAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RegionDto> cq = cb.createQuery(RegionDto.class);
		Root<Region> region = cq.from(Region.class);

		selectDtoFields(cq, region);

		Predicate filter = regionService.createChangeDateFilter(cb, region, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	// Need to be in the same order as in the constructor
	private void selectDtoFields(CriteriaQuery<RegionDto> cq, Root<Region> root) {

		Join<Region, Country> country = root.join(Region.COUNTRY, JoinType.LEFT);
		Join<Region, Area> area = root.join(Region.AREA, JoinType.LEFT);

		cq.multiselect(
			root.get(Region.CREATION_DATE),
			root.get(Region.CHANGE_DATE),
			root.get(Region.UUID),
			root.get(Region.ARCHIVED),
			root.get(Region.NAME),
			root.get(Region.EPID_CODE),
			root.get(Region.GROWTH_RATE),
			root.get(Region.EXTERNAL_ID),
			country.get(Country.UUID),
			country.get(Country.DEFAULT_NAME),
			country.get(Country.ISO_CODE),
			area.get(Area.UUID));
	}

	@Override
	public List<RegionIndexDto> getIndexList(RegionCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(Region.class);
		Root<Region> region = cq.from(Region.class);
		Join<Region, Area> area = region.join(Region.AREA, JoinType.LEFT);
		Join<Region, Country> country = region.join(Region.COUNTRY, JoinType.LEFT);

		Predicate filter = regionService.buildCriteriaFilter(criteria, cb, region);

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Region.NAME:
				case Region.EPID_CODE:
				case Region.GROWTH_RATE:
				case Region.EXTERNAL_ID:
					expression = region.get(sortProperty.propertyName);
					break;
				case Region.AREA:
					expression = area.get(Area.NAME);
					break;
				case RegionIndexDto.COUNTRY:
					expression = country.get(Country.DEFAULT_NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(region.get(Region.NAME)));
		}

		cq.select(region);

		if (first != null && max != null) {
			return em.createQuery(cq)
				.setFirstResult(first)
				.setMaxResults(max)
				.getResultList()
				.stream()
				.map(f -> toIndexDto(f))
				.collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(f -> toIndexDto(f)).collect(Collectors.toList());
		}
	}

	@Override
	public long count(RegionCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Region> root = cq.from(Region.class);

		Predicate filter = regionService.buildCriteriaFilter(criteria, cb, root);

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return regionService.getAllUuids();
	}

	@Override
	public RegionDto getRegionByUuid(String uuid) {
		return toDto(regionService.getByUuid(uuid));
	}

	@Override
	public List<RegionDto> getByUuids(List<String> uuids) {
		return regionService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public RegionReferenceDto getRegionReferenceByUuid(String uuid) {
		return toReferenceDto(regionService.getByUuid(uuid));
	}

	@Override
	public RegionReferenceDto getRegionReferenceById(int id) {
		return toReferenceDto(regionService.getById(id));
	}

	@Override
	public List<String> getNamesByIds(List<Long> regionIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Region> root = cq.from(Region.class);

		Predicate filter = root.get(Region.ID).in(regionIds);
		cq.where(filter);
		cq.select(root.get(Region.NAME));
		return em.createQuery(cq).getResultList();
	}

	@Override
	public void archive(String regionUuid) {
		Region region = regionService.getByUuid(regionUuid);
		region.setArchived(true);
		regionService.ensurePersisted(region);
	}

	@Override
	public void dearchive(String regionUuid) {
		Region region = regionService.getByUuid(regionUuid);
		region.setArchived(false);
		regionService.ensurePersisted(region);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> regionUuids) {

		return regionService.isUsedInInfrastructureData(regionUuids, District.REGION, District.class)
			|| regionService.isUsedInInfrastructureData(regionUuids, Facility.REGION, Facility.class)
			|| regionService.isUsedInInfrastructureData(regionUuids, PointOfEntry.REGION, PointOfEntry.class);
	}

	public static RegionReferenceDto toReferenceDto(Region entity) {

		if (entity == null) {
			return null;
		}
		return new RegionReferenceDto(entity.getUuid(), entity.toString(), entity.getExternalID());
	}

	public RegionDto toDto(Region entity) {

		if (entity == null) {
			return null;
		}
		RegionDto dto = new RegionDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setArchived(entity.isArchived());
		dto.setExternalID(entity.getExternalID());
		dto.setArea(AreaFacadeEjb.toReferenceDto(entity.getArea()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));

		return dto;
	}

	public RegionIndexDto toIndexDto(Region entity) {

		if (entity == null) {
			return null;
		}
		RegionIndexDto dto = new RegionIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setPopulation(populationDataFacade.getRegionPopulation(dto.getUuid()));
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setExternalID(entity.getExternalID());
		dto.setArea(AreaFacadeEjb.toReferenceDto(entity.getArea()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));

		return dto;
	}

	@Override
	public void saveRegion(RegionDto dto) throws ValidationRuntimeException {
		saveRegion(dto, false);
	}

	@Override
	public void saveRegion(RegionDto dto, boolean allowMerge) throws ValidationRuntimeException {

		Region region = regionService.getByUuid(dto.getUuid());

		if (region == null) {
			List<Region> duplicates = regionService.getByName(dto.getName(), true);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					region = duplicates.get(0);
					RegionDto dtoToMerge = getRegionByUuid(region.getUuid());
					dto = DtoHelper.copyDtoValues(dtoToMerge, dto, true);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importRegionAlreadyExists));
				}
			}
		}

		region = fillOrBuildEntity(dto, region, true);
		regionService.ensurePersisted(region);
	}

	@Override
	public List<RegionReferenceDto> getReferencesByName(String name, boolean includeArchivedEntities) {
		return regionService.getByName(name, includeArchivedEntities).stream().map(RegionFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	public List<RegionDto> getByName(String name, boolean includeArchivedEntities) {
		return regionService.getByName(name, includeArchivedEntities).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<RegionReferenceDto> getByExternalId(String externalId, boolean includeArchivedEntities) {
		return regionService.getByExternalId(externalId, includeArchivedEntities)
			.stream()
			.map(RegionFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	private List<RegionReferenceDto> getAllActiveByPredicate(BiFunction<CriteriaBuilder, Root<Region>, Predicate> buildPredicate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Region> cq = cb.createQuery(Region.class);
		Root<Region> root = cq.from(Region.class);

		Predicate basicFilter = regionService.createBasicFilter(cb, root);
		cq.where(CriteriaBuilderHelper.and(cb, basicFilter, buildPredicate.apply(cb, root)));

		cq.orderBy(cb.asc(root.get(Region.NAME)));

		return em.createQuery(cq).getResultList().stream().map(RegionFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	private Region fillOrBuildEntity(@NotNull RegionDto source, Region target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, Region::new, checkChangeDate);

		target.setName(source.getName());
		target.setEpidCode(source.getEpidCode());
		target.setGrowthRate(source.getGrowthRate());
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());
		target.setArea(areaService.getByReferenceDto(source.getArea()));
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));

		return target;
	}

	@LocalBean
	@Stateless
	public static class RegionFacadeEjbLocal extends RegionFacadeEjb {

	}
}
