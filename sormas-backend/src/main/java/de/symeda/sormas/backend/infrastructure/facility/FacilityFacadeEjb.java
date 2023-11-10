/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityExportDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "FacilityFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class FacilityFacadeEjb
	extends AbstractInfrastructureFacadeEjb<Facility, FacilityDto, FacilityIndexDto, FacilityReferenceDto, FacilityService, FacilityCriteria>
	implements FacilityFacade {

	@EJB
	private CommunityService communityService;
	@EJB
	private DistrictService districtService;
	@EJB
	private RegionService regionService;

	public FacilityFacadeEjb() {
	}

	@Inject
	protected FacilityFacadeEjb(FacilityService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			Facility.class,
			FacilityDto.class,
			service,
			featureConfiguration,
			Validations.importFacilityAlreadyExists,
			null,
			Strings.messageFacilityDearchivingNotPossible);
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getActiveFacilitiesByCommunityAndType(
		CommunityReferenceDto communityRef,
		FacilityType type,
		boolean includeOtherFacility,
		boolean includeNoneFacility) {

		Community community = communityService.getByUuid(communityRef.getUuid());
		List<Facility> facilities = service.getActiveFacilitiesByCommunityAndType(community, type, includeOtherFacility, includeNoneFacility);
		return facilities.stream().map(FacilityFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getActiveFacilitiesByDistrictAndType(
		DistrictReferenceDto districtRef,
		FacilityType type,
		boolean includeOtherFacility,
		boolean includeNoneFacility) {

		District district = districtService.getByUuid(districtRef.getUuid());
		List<Facility> facilities = service.getActiveFacilitiesByDistrictAndType(district, type, includeOtherFacility, includeNoneFacility);
		return facilities.stream().map(FacilityFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getActiveHospitalsByCommunity(CommunityReferenceDto communityRef, boolean includeOtherFacility) {
		Community community = communityService.getByUuid(communityRef.getUuid());
		List<Facility> facilities = service.getActiveFacilitiesByCommunityAndType(community, FacilityType.HOSPITAL, includeOtherFacility, false);
		return facilities.stream().map(FacilityFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getActiveHospitalsByDistrict(DistrictReferenceDto districtRef, boolean includeOtherFacility) {
		District district = districtService.getByUuid(districtRef.getUuid());
		List<Facility> facilities = service.getActiveFacilitiesByDistrictAndType(district, FacilityType.HOSPITAL, includeOtherFacility, false);
		return facilities.stream().map(FacilityFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getAllActiveLaboratories(boolean includeOtherFacility) {

		List<Facility> laboratories = service.getAllActiveLaboratories(includeOtherFacility);
		return laboratories.stream().map(FacilityFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<FacilityDto> getAllByRegionAfter(String regionUuid, Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FacilityDto> cq = cb.createQuery(FacilityDto.class);
		Root<Facility> facility = cq.from(Facility.class);

		selectDtoFields(cq, facility);

		Predicate filter = service.createChangeDateFilter(cb, facility, date);

		if (regionUuid != null) {
			Predicate regionFilter = cb.equal(facility.get(Facility.REGION), regionService.getByUuid(regionUuid));
			filter = CriteriaBuilderHelper.and(cb, filter, regionFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	@Override
	@PermitAll
	public List<FacilityDto> getAllWithoutRegionAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FacilityDto> cq = cb.createQuery(FacilityDto.class);
		Root<Facility> facility = cq.from(Facility.class);

		selectDtoFields(cq, facility);

		Predicate filter = service.createChangeDateFilter(cb, facility, date);

		Predicate regionFilter = cb.isNull(facility.get(Facility.REGION));
		filter = CriteriaBuilderHelper.and(cb, filter, regionFilter);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {

		return service.getByExternalId(externalId, includeArchivedEntities)
			.stream()
			.map(FacilityFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Deprecated
	private void selectDtoFields(CriteriaQuery<FacilityDto> cq, Root<Facility> root) {
		// todo with #10927 this will move to the service
		Join<Facility, Community> community = root.join(Facility.COMMUNITY, JoinType.LEFT);
		Join<Facility, District> district = root.join(Facility.DISTRICT, JoinType.LEFT);
		Join<Facility, Region> region = root.join(Facility.REGION, JoinType.LEFT);
		// Need to be in the same order as in the constructor
		cq.multiselect(
			root.get(Facility.CREATION_DATE),
			root.get(Facility.CHANGE_DATE),
			root.get(Facility.UUID),
			root.get(Facility.ARCHIVED),
			root.get(Facility.NAME),
			region.get(Region.UUID),
			region.get(Region.NAME),
			region.get(Region.EXTERNAL_ID),
			district.get(District.UUID),
			district.get(District.NAME),
			district.get(District.EXTERNAL_ID),
			community.get(Community.UUID),
			community.get(Community.NAME),
			community.get(Community.EXTERNAL_ID),
			root.get(Facility.CITY),
			root.get(Facility.POSTAL_CODE),
			root.get(Facility.STREET),
			root.get(Facility.HOUSE_NUMBER),
			root.get(Facility.ADDITIONAL_INFORMATION),
			root.get(Facility.AREA_TYPE),
			root.get(Facility.CONTACT_PERSON_FIRST_NAME),
			root.get(Facility.CONTACT_PERSON_LAST_NAME),
			root.get(Facility.CONTACT_PERSON_PHONE),
			root.get(Facility.CONTACT_PERSON_EMAIL),
			root.get(Facility.LATITUDE),
			root.get(Facility.LONGITUDE),
			root.get(Facility.TYPE),
			root.get(Facility.PUBLIC_OWNERSHIP),
			root.get(Facility.EXTERNAL_ID));
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public FacilityReferenceDto getFacilityReferenceById(long id) {
		return toReferenceDto(service.getById(id));
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public Map<String, String> getDistrictUuidsForFacilities(List<FacilityReferenceDto> facilities) {

		if (facilities.isEmpty()) {
			return new HashMap<>();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Facility> root = cq.from(Facility.class);
		Join<Facility, District> districtJoin = root.join(Facility.DISTRICT, JoinType.LEFT);

		Predicate filter = root.get(Facility.UUID).in(facilities.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
		cq.where(filter);
		cq.multiselect(root.get(Facility.UUID), districtJoin.get(District.UUID));

		return em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(e -> (String) e[0], e -> (String) e[1]));
	}

	@Override
	@RightsAllowed(UserRight._STATISTICS_ACCESS)
	public Map<String, String> getCommunityUuidsForFacilities(List<FacilityReferenceDto> facilities) {

		if (facilities.isEmpty()) {
			return new HashMap<>();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Facility> root = cq.from(Facility.class);
		Join<Facility, Community> communityJoin = root.join(Facility.COMMUNITY, JoinType.LEFT);

		Predicate filter = cb.and(
			cb.isNotNull(root.get(Facility.COMMUNITY)),
			root.get(Facility.UUID).in(facilities.stream().map(ReferenceDto::getUuid).collect(Collectors.toList())));
		cq.where(filter);
		cq.multiselect(root.get(Facility.UUID), communityJoin.get(Community.UUID));

		return em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(e -> (String) e[0], e -> (String) e[1]));
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getByExternalIdAndType(String id, FacilityType type, boolean includeArchivedEntities) {
		return service.getFacilitiesByExternalIdAndType(id, type, includeArchivedEntities)
			.stream()
			.map(FacilityFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public Page<FacilityIndexDto> getIndexPage(FacilityCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<FacilityIndexDto> facilityIndexList = getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(facilityIndexList, offset, size, totalElementCount);
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getByNameAndType(
		String name,
		DistrictReferenceDto districtRef,
		CommunityReferenceDto communityRef,
		FacilityType type,
		boolean includeArchivedEntities) {

		return service
			.getFacilitiesByNameAndType(
				name,
				districtService.getByReferenceDto(districtRef),
				communityService.getByReferenceDto(communityRef),
				type,
				includeArchivedEntities)
			.stream()
			.map(FacilityFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public FacilityReferenceDto getByAddress(String street, String postalCode, String city) {

		return toReferenceDto(service.getByAddress(street, postalCode, city));
	}

	@Override
	@PermitAll
	public List<FacilityReferenceDto> getLaboratoriesByName(String name, boolean includeArchivedEntities) {
		return service.getFacilitiesByNameAndType(name, null, null, FacilityType.LABORATORY, includeArchivedEntities)
			.stream()
			.map(FacilityFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> facilityUuids) {

		if (CollectionUtils.isEmpty(facilityUuids)) {
			// Avoid empty IN clause
			return false;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Facility> root = cq.from(Facility.class);
		Join<Facility, Community> communityJoin = root.join(Facility.COMMUNITY);
		Join<Facility, District> districtJoin = root.join(Facility.DISTRICT);
		Join<Facility, Region> regionJoin = root.join(Facility.REGION);

		cq.where(
			cb.and(
				cb.or(
					cb.isTrue(communityJoin.get(Community.ARCHIVED)),
					cb.isTrue(districtJoin.get(District.ARCHIVED)),
					cb.isTrue(regionJoin.get(Region.ARCHIVED))),
				root.get(Facility.UUID).in(facilityUuids)));

		cq.select(root.get(Facility.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	public static FacilityReferenceDto toReferenceDto(Facility entity) {

		if (entity == null) {
			return null;
		}

		return new FacilityReferenceDto(
			entity.getUuid(),
			FacilityHelper.buildFacilityString(entity.getUuid(), entity.getName()),
			entity.getExternalID());
	}

	@Override
	public FacilityDto toDto(Facility entity) {

		if (entity == null) {
			return null;
		}

		FacilityDto dto = new FacilityDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setType(entity.getType());
		dto.setPublicOwnership(entity.isPublicOwnership());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setCommunity(CommunityFacadeEjb.toReferenceDto(entity.getCommunity()));
		dto.setCity(entity.getCity());
		dto.setPostalCode(entity.getPostalCode());
		dto.setStreet(entity.getStreet());
		dto.setHouseNumber(entity.getHouseNumber());
		dto.setAdditionalInformation(entity.getAdditionalInformation());
		dto.setAreaType(entity.getAreaType());
		dto.setContactPersonFirstName(entity.getContactPersonFirstName());
		dto.setContactPersonLastName(entity.getContactPersonLastName());
		dto.setContactPersonPhone(entity.getContactPersonPhone());
		dto.setContactPersonEmail(entity.getContactPersonEmail());
		dto.setLatitude(entity.getLatitude());
		dto.setLongitude(entity.getLongitude());
		dto.setExternalID(entity.getExternalID());
		applyToDtoInheritance(dto, entity);

		return dto;
	}

	@Override
	protected FacilityReferenceDto toRefDto(Facility facility) {
		return toReferenceDto(facility);
	}

	@Override
	public List<FacilityIndexDto> getIndexList(FacilityCriteria facilityCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FacilityIndexDto> cq = cb.createQuery(FacilityIndexDto.class);
		Root<Facility> facility = cq.from(Facility.class);
		Join<Facility, Region> region = facility.join(Facility.REGION, JoinType.LEFT);
		Join<Facility, District> district = facility.join(Facility.DISTRICT, JoinType.LEFT);
		Join<Facility, Community> community = facility.join(Facility.COMMUNITY, JoinType.LEFT);

		Predicate filter = service.buildCriteriaFilter(facilityCriteria, cb, facility);

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Facility.NAME:
				case Facility.POSTAL_CODE:
				case Facility.CITY:
				case Facility.STREET:
				case Facility.HOUSE_NUMBER:
				case Facility.ADDITIONAL_INFORMATION:
				case Facility.LATITUDE:
				case Facility.LONGITUDE:
				case Facility.EXTERNAL_ID:
				case Facility.TYPE:
					expression = facility.get(sortProperty.propertyName);
					break;
				case Facility.REGION:
					expression = region.get(Region.NAME);
					break;
				case Facility.DISTRICT:
					expression = district.get(District.NAME);
					break;
				case Facility.COMMUNITY:
					expression = community.get(Community.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(
				cb.asc(region.get(Region.NAME)),
				cb.asc(district.get(District.NAME)),
				cb.asc(community.get(Community.NAME)),
				cb.asc(facility.get(Facility.NAME)));
		}

		cq.multiselect(
			facility.get(Facility.UUID),
			facility.get(Facility.NAME),
			facility.get(Facility.TYPE),
			region.get(Region.UUID),
			region.get(Region.NAME),
			district.get(District.UUID),
			district.get(District.NAME),
			community.get(Community.UUID),
			community.get(Community.NAME),
			facility.get(Facility.POSTAL_CODE),
			facility.get(Facility.CITY),
			facility.get(Facility.STREET),
			facility.get(Facility.HOUSE_NUMBER),
			facility.get(Facility.ADDITIONAL_INFORMATION),
			facility.get(Facility.LATITUDE),
			facility.get(Facility.LONGITUDE),
			facility.get(Facility.EXTERNAL_ID));

		return QueryHelper.getResultList(em, cq, first, max);
	}

	@Override
	@RightsAllowed(UserRight._INFRASTRUCTURE_EXPORT)
	public List<FacilityExportDto> getExportList(FacilityCriteria facilityCriteria, Collection<String> selectedRows, Integer first, Integer max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FacilityExportDto> cq = cb.createQuery(FacilityExportDto.class);
		Root<Facility> facility = cq.from(Facility.class);
		Join<Facility, Region> region = facility.join(Facility.REGION, JoinType.LEFT);
		Join<Facility, District> district = facility.join(Facility.DISTRICT, JoinType.LEFT);
		Join<Facility, Community> community = facility.join(Facility.COMMUNITY, JoinType.LEFT);

		cq.multiselect(
			facility.get(Facility.UUID),
			facility.get(Facility.NAME),
			facility.get(Facility.TYPE),
			region.get(Region.NAME),
			district.get(District.NAME),
			community.get(Community.NAME),
			facility.get(Facility.CITY),
			facility.get(Facility.POSTAL_CODE),
			facility.get(Facility.STREET),
			facility.get(Facility.HOUSE_NUMBER),
			facility.get(Facility.ADDITIONAL_INFORMATION),
			facility.get(Facility.AREA_TYPE),
			facility.get(Facility.CONTACT_PERSON_FIRST_NAME),
			facility.get(Facility.CONTACT_PERSON_LAST_NAME),
			facility.get(Facility.CONTACT_PERSON_PHONE),
			facility.get(Facility.CONTACT_PERSON_EMAIL),
			facility.get(Facility.LATITUDE),
			facility.get(Facility.LONGITUDE),
			facility.get(Facility.EXTERNAL_ID));

		Predicate filter = service.buildCriteriaFilter(facilityCriteria, cb, facility);

		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, facility.get(Facility.UUID));

		cq.where(filter);
		cq.orderBy(
			cb.asc(region.get(Region.NAME)),
			cb.asc(district.get(District.NAME)),
			cb.asc(community.get(Community.NAME)),
			cb.asc(facility.get(Facility.NAME)));

		return QueryHelper.getResultList(em, cq, first, max);
	}

	@Override
	@RightsAllowed({
		UserRight._INFRASTRUCTURE_CREATE,
		UserRight._INFRASTRUCTURE_EDIT })
	public FacilityDto save(FacilityDto dto, boolean allowMerge) {
		validate(dto);
		return super.save(dto, allowMerge);
	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	public FacilityDto saveFromCentral(FacilityDto dto) {
		return save(dto);
	}

	@Override
	protected List<Facility> findDuplicates(FacilityDto dto, boolean includeArchived) {
		return service.getFacilitiesByNameAndType(
			dto.getName(),
			districtService.getByReferenceDto(dto.getDistrict()),
			communityService.getByReferenceDto(dto.getCommunity()),
			dto.getType(),
			includeArchived);
	}

	@Override
	protected void checkInfraDataLocked() {
		// facilities are excluded from infra. data locking for now...
	}

	@Override
	public void validate(@Valid FacilityDto dto) {
		if (dto.getType() == null
			&& !FacilityDto.OTHER_FACILITY_UUID.equals(dto.getUuid())
			&& !FacilityDto.NONE_FACILITY_UUID.equals(dto.getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validFacilityType));
		}

		if (!FacilityType.LABORATORY.equals(dto.getType())) {
			if (dto.getRegion() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
			}
			if (dto.getDistrict() == null) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
			}
		}
	}

	@Override
	protected Facility fillOrBuildEntity(@NotNull FacilityDto source, Facility target, boolean checkChangeDate, boolean allowUuidOverwrite) {

		target = DtoHelper.fillOrBuildEntity(source, target, Facility::new, checkChangeDate, allowUuidOverwrite);

		target.setName(source.getName());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setCity(source.getCity());
		target.setPostalCode(source.getPostalCode());
		target.setStreet(source.getStreet());
		target.setHouseNumber(source.getHouseNumber());
		target.setAdditionalInformation(source.getAdditionalInformation());
		target.setAreaType(source.getAreaType());
		target.setContactPersonFirstName(source.getContactPersonFirstName());
		target.setContactPersonLastName(source.getContactPersonLastName());
		target.setContactPersonPhone(source.getContactPersonPhone());
		target.setContactPersonEmail(source.getContactPersonEmail());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setType(source.getType());
		target.setExternalID(source.getExternalID());
		applyFillOrBuildEntityInheritance(target, source);

		return target;
	}

	@LocalBean
	@Stateless
	public static class FacilityFacadeEjbLocal extends FacilityFacadeEjb {

		public FacilityFacadeEjbLocal() {
		}

		@Inject
		protected FacilityFacadeEjbLocal(FacilityService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
