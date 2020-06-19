package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.AreaCriteria;
import de.symeda.sormas.api.region.AreaDto;
import de.symeda.sormas.api.region.AreaFacade;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "AreaFacade")
public class AreaFacadeEjb implements AreaFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private AreaService service;

	@Override
	public AreaDto getAreaByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<AreaDto> getIndexList(AreaCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Area> cq = cb.createQuery(Area.class);
		Root<Area> areaRoot = cq.from(Area.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, areaRoot);
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

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList().stream().map(this::toDto).collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(this::toDto).collect(Collectors.toList());
		}
	}

	@Override
	public long count(AreaCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Area> areaRoot = cq.from(Area.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, areaRoot);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(areaRoot));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public void saveArea(AreaDto area) {
		Area entity = service.getByUuid(area.getUuid());

		if (entity == null && !service.getByName(area.getName(), true).isEmpty()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importAreaAlreadyExists));
		}

		entity = fromDto(area, entity);
		service.ensurePersisted(entity);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> areaUuids) {
		return false;
//		return service.isUsedInInfrastructureData(areaUuids, Region.AREA, Region.class);
	}

	@Override
	public void archive(String areaUuid) {
		Area area = service.getByUuid(areaUuid);
		area.setArchived(true);
		service.ensurePersisted(area);
	}

	@Override
	public void deArchive(String areaUuid) {
		Area area = service.getByUuid(areaUuid);
		area.setArchived(false);
		service.ensurePersisted(area);
	}

	public Area fromDto(@NotNull AreaDto source, Area target) {
		if (target == null) {
			target = new Area();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setName(source.getName());
		target.setExternalId(source.getExternalId());
		target.setArchived(source.isArchived());

		return target;
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

	@LocalBean
	@Stateless
	public static class AreaFacadeEjbLocal extends AreaFacadeEjb {

	}

}
