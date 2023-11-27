package de.symeda.sormas.backend.infrastructure.pointofentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "PointOfEntryFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class PointOfEntryFacadeEjb
	extends
	AbstractInfrastructureFacadeEjb<PointOfEntry, PointOfEntryDto, PointOfEntryDto, PointOfEntryReferenceDto, PointOfEntryService, PointOfEntryCriteria>
	implements PointOfEntryFacade {

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;

	public PointOfEntryFacadeEjb() {
	}

	@Inject
	protected PointOfEntryFacadeEjb(PointOfEntryService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			PointOfEntry.class,
			PointOfEntryDto.class,
			service,
			featureConfiguration,
			Validations.importPointOfEntryAlreadyExists,
			null,
			Strings.messagePointOfEntryDearchivingNotPossible);
	}

	public static PointOfEntryReferenceDto toReferenceDto(PointOfEntry entity) {
		if (entity == null) {
			return null;
		}

		return new PointOfEntryReferenceDto(
			entity.getUuid(),
			InfrastructureHelper.buildPointOfEntryString(entity.getUuid(), entity.getName()),
			entity.getPointOfEntryType(),
			entity.getExternalID());
	}

	@Override
	@PermitAll
	public List<PointOfEntryReferenceDto> getAllActiveByDistrict(String districtUuid, boolean includeOthers) {

		District district = districtService.getByUuid(districtUuid);
		return service.getAllByDistrict(district, includeOthers)
			.stream()
			.filter(p -> !p.isArchived())
			.map(PointOfEntryFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<PointOfEntryReferenceDto> getByName(String name, DistrictReferenceDto district, boolean includeArchivedEntities) {
		return service.getByName(name, districtService.getByReferenceDto(district), includeArchivedEntities)
			.stream()
			.map(PointOfEntryFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public Page<PointOfEntryDto> getIndexPage(PointOfEntryCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<PointOfEntryDto> pointOfEntryList = getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(pointOfEntryList, offset, size, totalElementCount);
	}

	@Override
	@PermitAll
	public List<PointOfEntryReferenceDto> getReferencesByExternalId(String name, boolean includeArchivedEntities) {
		return service.getByExternalId(name, includeArchivedEntities)
			.stream()
			.map(PointOfEntryFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@RightsAllowed({
		UserRight._INFRASTRUCTURE_CREATE,
		UserRight._INFRASTRUCTURE_EDIT })
	public PointOfEntryDto save(PointOfEntryDto dto, boolean allowMerge) {
		validate(dto);
		return super.save(dto, allowMerge);
	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	public PointOfEntryDto saveFromCentral(PointOfEntryDto dto) {
		return save(dto);
	}

	@Override
	protected List<PointOfEntry> findDuplicates(PointOfEntryDto dto, boolean includeArchived) {
		return service.getByName(dto.getName(), districtService.getByReferenceDto(dto.getDistrict()), includeArchived);
	}

	@Override
	protected void checkInfraDataLocked() {
		// poe are excluded from infra. data locking for now...
	}

	@Override
	public void validate(@Valid PointOfEntryDto pointOfEntry) throws ValidationRuntimeException {

		if (StringUtils.isEmpty(pointOfEntry.getName())) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.NAME)));
		}
		if (pointOfEntry.getPointOfEntryType() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.POINT_OF_ENTRY_TYPE)));
		}
		if (pointOfEntry.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
		if (pointOfEntry.getDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}
		if (!districtFacade.getByUuid(pointOfEntry.getDistrict().getUuid()).getRegion().equals(pointOfEntry.getRegion())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noDistrictInRegion));
		}
	}

	@Override
	public List<PointOfEntryDto> getIndexList(PointOfEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(PointOfEntry.class);
		Root<PointOfEntry> root = cq.from(PointOfEntry.class);
		Join<PointOfEntry, Region> region = root.join(PointOfEntry.REGION, JoinType.LEFT);
		Join<PointOfEntry, District> district = root.join(PointOfEntry.DISTRICT, JoinType.LEFT);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case PointOfEntry.NAME:
				case PointOfEntry.POINT_OF_ENTRY_TYPE:
				case PointOfEntry.LATITUDE:
				case PointOfEntry.LONGITUDE:
				case PointOfEntry.ACTIVE:
				case PointOfEntry.EXTERNAL_ID:
					expression = root.get(sortProperty.propertyName);
					break;
				case Facility.REGION:
					expression = region.get(Region.NAME);
					break;
				case Facility.DISTRICT:
					expression = district.get(District.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)), cb.asc(root.get(PointOfEntry.NAME)));
		}

		cq.select(root);

		return QueryHelper.getResultList(em, cq, first, max, this::toDto);
	}

	@Override
	@RightsAllowed(UserRight._CASE_VIEW)
	public PointOfEntryDto getByCaseUuid(String caseUuid) {

		return toPseudonymizedDto(service.getByCaseUuid(caseUuid));
	}

	@Override
	@RightsAllowed(UserRight._CASE_VIEW)
	public boolean existsForCase(String caseUuid) {

		return service.existsForCase(caseUuid);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> pointOfEntryUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PointOfEntry> root = cq.from(PointOfEntry.class);
		Join<PointOfEntry, District> districtJoin = root.join(PointOfEntry.DISTRICT);
		Join<PointOfEntry, Region> regionJoin = root.join(PointOfEntry.REGION);

		cq.where(
			cb.and(
				cb.or(cb.isTrue(districtJoin.get(District.ARCHIVED)), cb.isTrue(regionJoin.get(Region.ARCHIVED))),
				root.get(PointOfEntry.UUID).in(pointOfEntryUuids)));

		cq.select(root.get(PointOfEntry.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	@Override
	protected PointOfEntry fillOrBuildEntity(
		@NotNull PointOfEntryDto source,
		PointOfEntry target,
		boolean checkChangeDate,
		boolean allowUuidOverwrite) {

		target = DtoHelper.fillOrBuildEntity(source, target, PointOfEntry::new, checkChangeDate, allowUuidOverwrite);

		target.setName(source.getName());
		target.setPointOfEntryType(source.getPointOfEntryType());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setActive(source.isActive());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setExternalID(source.getExternalID());
		applyFillOrBuildEntityInheritance(target, source);

		return target;
	}

	@Override
	public PointOfEntryDto toDto(PointOfEntry entity) {

		if (entity == null) {
			return null;
		}
		PointOfEntryDto dto = new PointOfEntryDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setPointOfEntryType(entity.getPointOfEntryType());
		dto.setActive(entity.isActive());
		dto.setLatitude(entity.getLatitude());
		dto.setLongitude(entity.getLongitude());
		dto.setRegion(RegionFacadeEjb.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DistrictFacadeEjb.toReferenceDto(entity.getDistrict()));
		dto.setExternalID(entity.getExternalID());
		applyToDtoInheritance(dto, entity);

		return dto;
	}

	@Override
	protected PointOfEntryReferenceDto toRefDto(PointOfEntry pointOfEntry) {
		return toReferenceDto(pointOfEntry);
	}

	@LocalBean
	@Stateless
	public static class PointOfEntryFacadeEjbLocal extends PointOfEntryFacadeEjb {

		public PointOfEntryFacadeEjbLocal() {
		}

		@Inject
		protected PointOfEntryFacadeEjbLocal(PointOfEntryService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
