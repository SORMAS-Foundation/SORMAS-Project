package de.symeda.sormas.backend.infrastructure.area;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.area.AreaCriteria;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.area.AreaFacade;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "AreaFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class AreaFacadeEjb extends AbstractInfrastructureFacadeEjb<Area, AreaDto, AreaDto, AreaReferenceDto, AreaService, AreaCriteria>
	implements AreaFacade {

	public AreaFacadeEjb() {
	}

	@Inject
	protected AreaFacadeEjb(AreaService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			Area.class,
			AreaDto.class,
			service,
			featureConfiguration,
			Validations.importAreaAlreadyExists,
			Strings.messageAreaArchivingNotPossible,
			null);
	}

	@Override
	@PermitAll
	public List<AreaReferenceDto> getAllActiveAsReference() {
		return service.getAllActive(Area.NAME, true).stream().map(AreaFacadeEjb::toReferenceDto).collect(Collectors.toList());
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

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case Area.NAME:
				case Area.EXTERNAL_ID:
					expression = cb.lower(areaRoot.get(sortProperty.propertyName));
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
	protected List<Area> findDuplicates(AreaDto dto, boolean includeArchived) {
		return service.getByName(dto.getName(), includeArchived);
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> areaUuids) {
		return service.isUsedInInfrastructureData(areaUuids, Region.AREA, Region.class);
	}

	@Override
	@PermitAll
	public List<AreaReferenceDto> getByName(String name, boolean includeArchived) {
		return service.getByName(name, includeArchived).stream().map(AreaFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public Area fillOrBuildEntity(@NotNull AreaDto source, Area target, boolean checkChangeDate, boolean allowUuidOverwrite) {
		target = DtoHelper.fillOrBuildEntity(source, target, Area::new, checkChangeDate, allowUuidOverwrite);
		target.setName(source.getName());
		target.setExternalId(source.getExternalId());
		applyFillOrBuildEntityInheritance(target, source);

		return target;
	}

	@Override
	@PermitAll
	public List<AreaReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchivedEntities) {

		return service.getByExternalId(externalId, includeArchivedEntities).stream().map(AreaFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public AreaDto toDto(Area entity) {
		if (entity == null) {
			return null;
		}
		AreaDto dto = new AreaDto();
		DtoHelper.fillDto(dto, entity);

		dto.setName(entity.getName());
		dto.setExternalId(entity.getExternalId());
		applyToDtoInheritance(dto, entity);

		return dto;
	}

	@Override
	protected AreaReferenceDto toRefDto(Area area) {
		return toReferenceDto(area);
	}

	public static AreaReferenceDto toReferenceDto(Area entity) {
		if (entity == null) {
			return null;
		}
		return new AreaReferenceDto(entity.getUuid(), entity.getName());
	}

	@Override
	@PermitAll
	public List<AreaReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return getByName(name, includeArchived);
	}

	@LocalBean
	@Stateless
	public static class AreaFacadeEjbLocal extends AreaFacadeEjb {

		public AreaFacadeEjbLocal() {
		}

		@Inject
		protected AreaFacadeEjbLocal(AreaService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
