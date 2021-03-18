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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.ContinentCriteria;
import de.symeda.sormas.api.region.ContinentDto;
import de.symeda.sormas.api.region.ContinentFacade;
import de.symeda.sormas.api.region.ContinentIndexDto;
import de.symeda.sormas.api.region.ContinentReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ContinentFacade")
public class ContinentFacadeEjb implements ContinentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ContinentService continentService;

	public static ContinentReferenceDto toReferenceDto(Continent entity) {
		if (entity == null) {
			return null;
		}
		return new ContinentReferenceDto(entity.getUuid(), entity.toString(), entity.getExternalId());
	}

	public static ContinentReferenceDto toReferenceDto(ContinentDto dto) {
		if (dto == null) {
			return null;
		}
		return new ContinentReferenceDto(dto.getUuid(), dto.toString(), dto.getExternalId());
	}

	@Override
	public List<ContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities) {
		return continentService.getByDefaultName(name, includeArchivedEntities)
				.stream()
				.map(ContinentFacadeEjb::toReferenceDto)
				.collect(Collectors.toList());
	}

	@Override
	public ContinentDto getByUuid(String uuid) {
		return toDto(continentService.getByUuid(uuid));
	}

	@Override
	public List<ContinentIndexDto> getIndexList(ContinentCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Continent> cq = cb.createQuery(Continent.class);
		Root<Continent> continent = cq.from(Continent.class);

		Predicate filter = continentService.buildCriteriaFilter(criteria, cb, continent);

		if (filter != null) {
			cq.where(filter).distinct(true);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
					case ContinentDto.EXTERNAL_ID:
					case ContinentDto.DEFAULT_NAME:
						expression = continent.get(sortProperty.propertyName);
						break;
					default:
						throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(continent.get(Continent.DEFAULT_NAME)));
		}

		cq.select(continent);

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList().stream().map(this::toIndexDto).collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(this::toIndexDto).collect(Collectors.toList());
		}
	}

	@Override
	public void archive(String uuid) {
		Continent continent = continentService.getByUuid(uuid);
		if (continent != null) {
			continent.setArchived(true);
			continentService.ensurePersisted(continent);
		}
	}

	@Override
	public void dearchive(String uuid) {
		Continent continent = continentService.getByUuid(uuid);
		if (continent != null) {
			continent.setArchived(false);
			continentService.ensurePersisted(continent);
		}
	}

	@Override
	public List<ContinentDto> getAllAfter(Date date) {
		return continentService.getAll((cb, root) -> continentService.createChangeDateFilter(cb, root, date))
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<ContinentDto> getByUuids(List<String> uuids) {
		return continentService.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return continentService.getAllUuids();
	}

	@Override
	public List<ContinentReferenceDto> getAllActiveAsReference() {
		return continentService.getAllActive(Continent.DEFAULT_NAME, true)
				.stream()
				.map(ContinentFacadeEjb::toReferenceDto)
				.sorted(Comparator.comparing(ContinentReferenceDto::getCaption))
				.collect(Collectors.toList());
	}

	@Override
	public void save(ContinentDto dto) {

		Continent continent = continentService.getByUuid(dto.getUuid());

		if (continent == null && !continentService.getByDefaultName(dto.getDefaultName(), true).isEmpty()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importContinentAlreadyExists));
		}

		continent = fillOrBuildEntity(dto, continent, true);
		continentService.ensurePersisted(continent);
	}

	@Override
	public long count(ContinentCriteria criteria) {
		return continentService.count((cb, root) -> continentService.buildCriteriaFilter(criteria, cb, root));
	}

	public ContinentDto toDto(Continent entity) {
		if (entity == null) {
			return null;
		}
		ContinentDto dto = new ContinentDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());

		return dto;
	}

	public ContinentIndexDto toIndexDto(Continent entity) {
		if (entity == null) {
			return null;
		}
		ContinentIndexDto dto = new ContinentIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setDisplayName(I18nProperties.getContinentName(entity.getDefaultName()));
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());

		return dto;
	}

	private Continent fillOrBuildEntity(@NotNull ContinentDto source, Continent target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Continent::new, checkChangeDate);

		target.setDefaultName(source.getDefaultName());
		target.setArchived(source.isArchived());
		target.setExternalId(source.getExternalId());

		return target;
	}

	@LocalBean
	@Stateless
	public static class ContinentFacadeEjbLocal extends ContinentFacadeEjb {

	}
}
