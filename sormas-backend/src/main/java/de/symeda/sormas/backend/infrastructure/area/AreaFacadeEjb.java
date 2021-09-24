package de.symeda.sormas.backend.infrastructure.area;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.area.AreaCriteria;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaFacade;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "AreaFacade")
public class AreaFacadeEjb implements AreaFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private AreaService areaService;

	@Override
	public List<AreaReferenceDto> getAllActiveAsReference() {
		return areaService.getAllActive(Area.NAME, true).stream().map(AreaFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public AreaDto getByUuid(String uuid) {
		return toDto(areaService.getByUuid(uuid));
	}

	@Override
	public List<AreaDto> getIndexList(AreaCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Area> cq = cb.createQuery(Area.class);
		Root<Area> areaRoot = cq.from(Area.class);

		Predicate filter = areaService.buildCriteriaFilter(criteria, cb, areaRoot);
		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Area.NAME:
				case Area.EXTERNAL_ID:
					expression = areaRoot.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(areaRoot.get(Area.NAME)));
		}

		cq.select(areaRoot);

		return QueryHelper.getResultList(em, cq, first, max, this::toDto);
	}

	@Override
	public long count(AreaCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Area> areaRoot = cq.from(Area.class);

		Predicate filter = areaService.buildCriteriaFilter(criteria, cb, areaRoot);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(areaRoot));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public AreaDto save(@Valid AreaDto dto) {
		return save(dto, false);
	}

	@Override
	public AreaDto save(@Valid AreaDto dto, boolean allowMerge) {
		Area area = areaService.getByUuid(dto.getUuid());

		if (area == null) {
			List<Area> duplicates = areaService.getByName(dto.getName(), true);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					area = duplicates.get(0);
					AreaDto dtoToMerge = getByUuid(area.getUuid());
					dto = DtoHelper.copyDtoValues(dtoToMerge, dto, true);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importAreaAlreadyExists));
				}
			}
		}

		area = fromDto(dto, area, true);
		areaService.ensurePersisted(area);
		return toDto(area);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> areaUuids) {
		return areaService.isUsedInInfrastructureData(areaUuids, Region.AREA, Region.class);
	}

	@Override
	public void archive(String areaUuid) {
		Area area = areaService.getByUuid(areaUuid);
		area.setArchived(true);
		areaService.ensurePersisted(area);
	}

	@Override
	public void dearchive(String areaUuid) {
		Area area = areaService.getByUuid(areaUuid);
		area.setArchived(false);
		areaService.ensurePersisted(area);
	}

	@Override
	public List<AreaReferenceDto> getByName(String name, boolean includeArchived) {
		return areaService.getByName(name, includeArchived).stream().map(AreaFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public List<AreaDto> getAllAfter(Date date) {
		return areaService.getAll((cb, root) -> areaService.createChangeDateFilter(cb, root, date))
			.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<AreaDto> getByUuids(List<String> uuids) {
		return areaService.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return areaService.getAllUuids();
	}

	public Area fromDto(@NotNull AreaDto source, Area target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Area::new, checkChangeDate);

		target.setName(source.getName());
		target.setExternalId(source.getExternalId());
		target.setArchived(source.isArchived());

		return target;
	}

	@Override
	public List<AreaReferenceDto> getByExternalId(String externalId, boolean includeArchivedEntities) {

		return areaService.getByExternalId(externalId, includeArchivedEntities)
			.stream()
			.map(AreaFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	public AreaDto toDto(Area source) {
		if (source == null) {
			return null;
		}
		AreaDto target = new AreaDto();
		DtoHelper.fillDto(target, source);

		target.setName(source.getName());
		target.setExternalId(source.getExternalId());
		target.setArchived(source.isArchived());

		return target;
	}

	public static AreaReferenceDto toReferenceDto(Area entity) {
		if (entity == null) {
			return null;
		}
		AreaReferenceDto dto = new AreaReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	@Override
	public List<AreaReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return getByName(name, includeArchived);
	}

	@LocalBean
	@Stateless
	public static class AreaFacadeEjbLocal extends AreaFacadeEjb {

	}

}
