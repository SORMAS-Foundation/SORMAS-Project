package de.symeda.sormas.backend.infrastructure;

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

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "PointOfEntryFacade")
public class PointOfEntryFacadeEjb implements PointOfEntryFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private PointOfEntryService service;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private UserService userService;

	public static PointOfEntryReferenceDto toReferenceDto(PointOfEntry entity) {
		if (entity == null) {
			return null;
		}

		PointOfEntryReferenceDto ref = new PointOfEntryReferenceDto(entity.getUuid(), entity.toString());
		return ref;
	}

	@Override
	public List<PointOfEntryReferenceDto> getAllActiveByDistrict(String districtUuid, boolean includeOthers) {
		District district = districtService.getByUuid(districtUuid);
		return service.getAllByDistrict(district, includeOthers).stream()
				.filter(p -> !p.isArchived())
				.map(p -> toReferenceDto(p))
				.collect(Collectors.toList());
	}

	@Override
	public PointOfEntryDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<PointOfEntryDto> getAllAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntryDto> cq = cb.createQuery(PointOfEntryDto.class);
		Root<PointOfEntry> pointOfEntry = cq.from(PointOfEntry.class);

		selectDtoFields(cq, pointOfEntry);

		Predicate filter = service.createChangeDateFilter(cb, pointOfEntry, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	// Need to be in the same order as in the constructor
	private void selectDtoFields(CriteriaQuery<PointOfEntryDto> cq, Root<PointOfEntry> root) {
		Join<PointOfEntry, District> district = root.join(Facility.DISTRICT, JoinType.LEFT);
		Join<PointOfEntry, Region> region = root.join(Facility.REGION, JoinType.LEFT);

		cq.multiselect(root.get(PointOfEntry.CREATION_DATE), root.get(PointOfEntry.CHANGE_DATE), root.get(PointOfEntry.UUID), 
				root.get(PointOfEntry.ARCHIVED), root.get(PointOfEntry.POINT_OF_ENTRY_TYPE), root.get(PointOfEntry.NAME),
				region.get(Region.UUID), region.get(Region.NAME), district.get(District.UUID),
				district.get(District.NAME), root.get(PointOfEntry.LATITUDE), root.get(PointOfEntry.LONGITUDE),
				root.get(PointOfEntry.ACTIVE), root.get(PointOfEntry.EXTERNAL_ID));
	}

	@Override
	public List<String> getAllUuids() {
		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}
		return service.getAllUuids(user);
	}

	@Override
	public List<PointOfEntryDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<PointOfEntryReferenceDto> getByName(String name, DistrictReferenceDto district) {
		return service.getByName(name, districtService.getByReferenceDto(district)).stream().map(p -> toReferenceDto(p))
				.collect(Collectors.toList());
	}

	@Override
	public void save(PointOfEntryDto dto) throws ValidationRuntimeException {
		PointOfEntry pointOfEntry = service.getByUuid(dto.getUuid());
		
		if (pointOfEntry == null && !getByName(dto.getName(), dto.getDistrict()).isEmpty()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importPointOfEntryAlreadyExists));
		}

		validate(dto);

		pointOfEntry = fillOrBuildEntity(dto, pointOfEntry);
		service.ensurePersisted(pointOfEntry);
	}

	@Override
	public void validate(PointOfEntryDto pointOfEntry) throws ValidationRuntimeException {
		if (StringUtils.isEmpty(pointOfEntry.getName())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required,
					I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.NAME)));
		}
		if (pointOfEntry.getPointOfEntryType() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.required,
					I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.POINT_OF_ENTRY_TYPE)));
		}
		if (pointOfEntry.getRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
		if (pointOfEntry.getDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}
		if (!districtFacade.getDistrictByUuid(pointOfEntry.getDistrict().getUuid()).getRegion()
				.equals(pointOfEntry.getRegion())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noDistrictInRegion));
		}
	}

	@Override
	public List<PointOfEntryDto> getIndexList(PointOfEntryCriteria criteria, Integer first, Integer max,
			List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(PointOfEntry.class);
		Root<PointOfEntry> pointOfEntry = cq.from(PointOfEntry.class);
		Join<PointOfEntry, Region> region = pointOfEntry.join(PointOfEntry.REGION, JoinType.LEFT);
		Join<PointOfEntry, District> district = pointOfEntry.join(PointOfEntry.DISTRICT, JoinType.LEFT);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, pointOfEntry);
		Predicate excludeFilter = cb.and(
				cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_AIRPORT_UUID),
				cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_SEAPORT_UUID),
				cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_GROUND_CROSSING_UUID),
				cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_POE_UUID));

		if (filter != null) {
			filter = AbstractAdoService.and(cb, filter, excludeFilter);
		} else {
			filter = excludeFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case PointOfEntry.NAME:
				case PointOfEntry.POINT_OF_ENTRY_TYPE:
				case PointOfEntry.LATITUDE:
				case PointOfEntry.LONGITUDE:
				case PointOfEntry.ACTIVE:
					expression = pointOfEntry.get(sortProperty.propertyName);
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
			cq.orderBy(cb.asc(region.get(Region.NAME)), cb.asc(district.get(District.NAME)),
					cb.asc(pointOfEntry.get(PointOfEntry.NAME)));
		}

		cq.select(pointOfEntry);

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList().stream().map(f -> toDto(f)).collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(f -> toDto(f)).collect(Collectors.toList());
		}
	}

	@Override
	public long count(PointOfEntryCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PointOfEntry> root = cq.from(PointOfEntry.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		Predicate excludeFilter = cb.and(cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_AIRPORT_UUID),
				cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_SEAPORT_UUID),
				cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_GROUND_CROSSING_UUID),
				cb.notEqual(root.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_POE_UUID));

		if (filter != null) {
			filter = AbstractAdoService.and(cb, filter, excludeFilter);
		} else {
			filter = excludeFilter;
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public void archive(String pointOfEntryUuid) {
		PointOfEntry pointOfEntry = service.getByUuid(pointOfEntryUuid);
		pointOfEntry.setArchived(true);
		service.ensurePersisted(pointOfEntry);
	}

	@Override
	public void dearchive(String pointOfEntryUuid) {
		PointOfEntry pointOfEntry = service.getByUuid(pointOfEntryUuid);
		pointOfEntry.setArchived(false);
		service.ensurePersisted(pointOfEntry);
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
						cb.or(
								cb.isTrue(districtJoin.get(District.ARCHIVED)),
								cb.isTrue(regionJoin.get(Region.ARCHIVED))
								),
						root.get(PointOfEntry.UUID).in(pointOfEntryUuids)
						)
				);

		cq.select(root.get(PointOfEntry.ID));

		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	private PointOfEntry fillOrBuildEntity(@NotNull PointOfEntryDto source, PointOfEntry target) {
		if (target == null) {
			target = new PointOfEntry();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setName(source.getName());
		target.setPointOfEntryType(source.getPointOfEntryType());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setActive(source.isActive());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setArchived(source.isArchived());
		target.setExternalID(source.getExternalID());

		return target;
	}

	private PointOfEntryDto toDto(PointOfEntry entity) {
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
		dto.setArchived(entity.isArchived());
		dto.setExternalID(entity.getExternalID());

		return dto;
	}

	@LocalBean
	@Stateless
	public static class PointOfEntryFacadeEjbLocal extends PointOfEntryFacadeEjb {
	}

}
