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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictCriteria;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.DistrictIndexDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "DistrictFacade")
public class DistrictFacadeEjb implements DistrictFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private DistrictService districtService;
	@EJB
	private UserService userService;
	@EJB
	private RegionService regionService;
	@EJB
	private PopulationDataFacadeEjbLocal populationDataFacade;

	@Override
	public List<DistrictReferenceDto> getAllActiveAsReference() {
		return districtService.getAllActive(District.NAME, true).stream()
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public List<DistrictReferenceDto> getAllActiveByRegion(String regionUuid) {
		Region region = regionService.getByUuid(regionUuid);

		return region.getDistricts().stream()
				.filter(d -> !d.isArchived())
				.map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public List<DistrictDto> getAllAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DistrictDto> cq = cb.createQuery(DistrictDto.class);
		Root<District> district = cq.from(District.class);

		selectDtoFields(cq, district);

		Predicate filter = districtService.createChangeDateFilter(cb, district, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	// Need to be in the same order as in the constructor
	private void selectDtoFields(CriteriaQuery<DistrictDto> cq, Root<District> root) {
		Join<District, Region> region = root.join(District.REGION, JoinType.LEFT);

		cq.multiselect(root.get(District.CREATION_DATE), root.get(District.CHANGE_DATE), root.get(District.UUID), root.get(District.ARCHIVED),
				root.get(District.NAME), root.get(District.EPID_CODE), root.get(District.GROWTH_RATE),
				region.get(Region.UUID), region.get(Region.NAME), root.get(District.EXTERNAL_ID));
	}

	@Override
	public List<DistrictIndexDto> getIndexList(DistrictCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(District.class);
		Root<District> district = cq.from(District.class);
		Join<District, Region> region = district.join(District.REGION, JoinType.LEFT);

		Predicate filter = districtService.buildCriteriaFilter(criteria, cb, district);

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case District.NAME:
				case District.EPID_CODE:
				case District.GROWTH_RATE:
				case District.EXTERNAL_ID:
					expression = district.get(sortProperty.propertyName);
					break;
				case District.REGION:
					expression = region.get(Region.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)));
		}

		cq.select(district);

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList().stream().map(f -> toIndexDto(f)).collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(f -> toIndexDto(f)).collect(Collectors.toList());
		}
	}

	@Override
	public long count(DistrictCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<District> root = cq.from(District.class);

		Predicate filter = districtService.buildCriteriaFilter(criteria, cb, root);

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

		return districtService.getAllUuids();
	}

	@Override	
	public int getCountByRegion(String regionUuid) {
		Region region = regionService.getByUuid(regionUuid);

		return districtService.getCountByRegion(region);
	}

	@Override
	public DistrictDto getDistrictByUuid(String uuid) {
		return toDto(districtService.getByUuid(uuid));
	}	

	@Override
	public List<DistrictDto> getByUuids(List<String> uuids) {
		return districtService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}


	@Override
	public DistrictReferenceDto getDistrictReferenceByUuid(String uuid) {
		return toReferenceDto(districtService.getByUuid(uuid));
	}

	@Override
	public DistrictReferenceDto getDistrictReferenceById(long id) {
		return toReferenceDto(districtService.getById(id));
	}

	@Override
	public void saveDistrict(DistrictDto dto) throws ValidationRuntimeException {
		District district = districtService.getByUuid(dto.getUuid());
		
		if (district == null && !getByName(dto.getName(), dto.getRegion()).isEmpty()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importDistrictAlreadyExists));
		}

		if (dto.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}

		district = fillOrBuildEntity(dto, district);
		districtService.ensurePersisted(district);
	}

	@Override
	public List<DistrictReferenceDto> getByName(String name, RegionReferenceDto regionRef) {
		return districtService.getByName(name, regionService.getByReferenceDto(regionRef)).stream().map(d -> toReferenceDto(d)).collect(Collectors.toList());
	}

	@Override
	public List<String> getNamesByIds(List<Long> districtIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<District> root = cq.from(District.class);

		Predicate filter = root.get(District.ID).in(districtIds);
		cq.where(filter);
		cq.select(root.get(District.NAME));
		return em.createQuery(cq).getResultList();
	}

	@Override
	public void archive(String districtUuid) {
		District district = districtService.getByUuid(districtUuid);
		district.setArchived(true);
		districtService.ensurePersisted(district);
	}

	@Override
	public void dearchive(String districtUuid) {
		District district = districtService.getByUuid(districtUuid);
		district.setArchived(false);
		districtService.ensurePersisted(district);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> districtUuids) {
		return districtService.isUsedInInfrastructureData(districtUuids, Community.DISTRICT, Community.class) ||
				districtService.isUsedInInfrastructureData(districtUuids, Facility.DISTRICT, Facility.class) ||
				districtService.isUsedInInfrastructureData(districtUuids, PointOfEntry.DISTRICT, PointOfEntry.class);
	}	

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> districtUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<District> root = cq.from(District.class);
		Join<District, Region> regionJoin = root.join(District.REGION);

		cq.where(
				cb.and(
						cb.isTrue(regionJoin.get(Region.ARCHIVED)),
						root.get(District.UUID).in(districtUuids)
						)
				);

		cq.select(root.get(District.ID));

		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	public static DistrictReferenceDto toReferenceDto(District entity) {
		if (entity == null) {
			return null;
		}
		DistrictReferenceDto dto = new DistrictReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public DistrictDto toDto(District entity) {
		if (entity == null) {
			return null;
		}
		DistrictDto dto = new DistrictDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setArchived(entity.isArchived());
		dto.setExternalID(dto.getExternalID());

		return dto;
	}	

	public DistrictIndexDto toIndexDto(District entity) {
		if (entity == null) {
			return null;
		}
		DistrictIndexDto dto = new DistrictIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setEpidCode(entity.getEpidCode());
		dto.setGrowthRate(entity.getGrowthRate());
		dto.setPopulation(populationDataFacade.getDistrictPopulation(dto.getUuid()));
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setExternalID(entity.getExternalID());

		return dto;
	}	

	private District fillOrBuildEntity(@NotNull DistrictDto source, District target) {
		if (target == null) {
			target = new District();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setName(source.getName());
		target.setEpidCode(source.getEpidCode());
		target.setGrowthRate(source.getGrowthRate());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());

		return target;
	}

	@Override
	public String getFullEpidCodeForDistrict(String districtUuid) {
		District district = districtService.getByUuid(districtUuid);
		String fullEpidCode = (district.getRegion().getEpidCode() != null ? district.getRegion().getEpidCode() : "")
				+ "-" + (district.getEpidCode() != null ? district.getEpidCode() : "");
		return fullEpidCode;
	}

	@LocalBean
	@Stateless
	public static class DistrictFacadeEjbLocal extends DistrictFacadeEjb	 {
	}
}
