package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.Comparator;
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
import de.symeda.sormas.api.region.SubContinentCriteria;
import de.symeda.sormas.api.region.SubContinentDto;
import de.symeda.sormas.api.region.SubContinentFacade;
import de.symeda.sormas.api.region.SubContinentIndexDto;
import de.symeda.sormas.api.region.SubContinentReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "SubContinentFacade")
public class SubContinentFacadeEjb implements SubContinentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SubContinentService subContinentService;
	@EJB
	private ContinentService continentService;

	public static SubContinentReferenceDto toReferenceDto(SubContinent entity) {
		if (entity == null) {
			return null;
		}
		return new SubContinentReferenceDto(entity.getUuid(), entity.toString(), entity.getExternalId());
	}

	public static SubContinentReferenceDto toReferenceDto(SubContinentDto dto) {
		if (dto == null) {
			return null;
		}
		return new SubContinentReferenceDto(dto.getUuid(), dto.toString(), dto.getExternalId());
	}

	@Override
	public List<SubContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities) {
		return subContinentService.getByDefaultName(name, includeArchivedEntities)
			.stream()
			.map(SubContinentFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public SubContinentDto getByUuid(String uuid) {
		return toDto(subContinentService.getByUuid(uuid));
	}

	@Override
	public List<SubContinentIndexDto> getIndexList(SubContinentCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SubContinent> cq = cb.createQuery(SubContinent.class);
		Root<SubContinent> subContinent = cq.from(SubContinent.class);
		Join<Object, Object> continent = subContinent.join(SubContinent.CONTINENT, JoinType.LEFT);

		Predicate filter = subContinentService.buildCriteriaFilter(criteria, cb, subContinent);

		if (filter != null) {
			cq.where(filter).distinct(true);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case SubContinentDto.CONTINENT:
					expression = continent.get(Continent.DEFAULT_NAME);
				case SubContinentDto.EXTERNAL_ID:
				case SubContinentDto.DEFAULT_NAME:
					expression = subContinent.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(subContinent.get(SubContinent.DEFAULT_NAME)));
		}

		cq.select(subContinent);

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList().stream().map(this::toIndexDto).collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(this::toIndexDto).collect(Collectors.toList());
		}
	}

	@Override
	public void archive(String uuid) {
		SubContinent subContinent = subContinentService.getByUuid(uuid);
		if (subContinent != null) {
			subContinent.setArchived(true);
			subContinentService.ensurePersisted(subContinent);
		}
	}

	@Override
	public void dearchive(String uuid) {
		SubContinent subContinent = subContinentService.getByUuid(uuid);
		if (subContinent != null) {
			subContinent.setArchived(false);
			subContinentService.ensurePersisted(subContinent);
		}
	}

	@Override
	public List<SubContinentDto> getAllAfter(Date date) {
		return subContinentService.getAll((cb, root) -> subContinentService.createChangeDateFilter(cb, root, date))
			.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<SubContinentDto> getByUuids(List<String> uuids) {
		return subContinentService.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return subContinentService.getAllUuids();
	}

	@Override
	public List<SubContinentReferenceDto> getAllActiveAsReference() {
		return subContinentService.getAllActive(SubContinent.DEFAULT_NAME, true)
			.stream()
			.map(SubContinentFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(SubContinentReferenceDto::getCaption))
			.collect(Collectors.toList());
	}

	@Override
	public void save(SubContinentDto dto) {

		SubContinent subContinent = subContinentService.getByUuid(dto.getUuid());

		if (subContinent == null && !subContinentService.getByDefaultName(dto.getDefaultName(), true).isEmpty()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importSubContinentAlreadyExists));
		}

		subContinent = fillOrBuildEntity(dto, subContinent, true);
		subContinentService.ensurePersisted(subContinent);
	}

	@Override
	public long count(SubContinentCriteria criteria) {
		return subContinentService.count((cb, root) -> subContinentService.buildCriteriaFilter(criteria, cb, root));
	}

	public SubContinentDto toDto(SubContinent entity) {
		if (entity == null) {
			return null;
		}
		SubContinentDto dto = new SubContinentDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());
		dto.setContinent(ContinentFacadeEjb.toReferenceDto(entity.getContinent()));

		return dto;
	}

	public SubContinentIndexDto toIndexDto(SubContinent entity) {
		if (entity == null) {
			return null;
		}
		SubContinentIndexDto dto = new SubContinentIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setDisplayName(I18nProperties.getSubContinentName(entity.getDefaultName()));
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());
		dto.setContinent(ContinentFacadeEjb.toReferenceDto(entity.getContinent()));

		return dto;
	}

	private SubContinent fillOrBuildEntity(@NotNull SubContinentDto source, SubContinent target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, SubContinent::new, checkChangeDate);

		target.setDefaultName(source.getDefaultName());
		target.setArchived(source.isArchived());
		target.setExternalId(source.getExternalId());
		target.setContinent(continentService.getByReferenceDto(source.getContinent()));

		return target;
	}

	@LocalBean
	@Stateless
	public static class SubContinentFacadeEjbLocal extends SubContinentFacadeEjb {

	}
}
