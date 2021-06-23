package de.symeda.sormas.backend.region;

import java.util.ArrayList;
import java.util.Collection;
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
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.region.SubcontinentCriteria;
import de.symeda.sormas.api.region.SubcontinentDto;
import de.symeda.sormas.api.region.SubcontinentFacade;
import de.symeda.sormas.api.region.SubcontinentIndexDto;
import de.symeda.sormas.api.region.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "SubcontinentFacade")
public class SubcontinentFacadeEjb implements SubcontinentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SubcontinentService subcontinentService;
	@EJB
	private ContinentService continentService;
	@EJB
	private CountryService countryService;

	public static SubcontinentReferenceDto toReferenceDto(Subcontinent entity) {
		if (entity == null) {
			return null;
		}
		return new SubcontinentReferenceDto(entity.getUuid(), entity.toString(), entity.getExternalId());
	}

	public static SubcontinentReferenceDto toReferenceDto(SubcontinentDto dto) {
		if (dto == null) {
			return null;
		}
		return new SubcontinentReferenceDto(dto.getUuid(), dto.toString(), dto.getExternalId());
	}

	@Override
	public List<SubcontinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities) {
		return subcontinentService.getByDefaultName(name, includeArchivedEntities)
			.stream()
			.map(SubcontinentFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public SubcontinentReferenceDto getByCountry(CountryReferenceDto countryDto) {
		return toReferenceDto(countryService.getByUuid(countryDto.getUuid()).getSubcontinent());
	}

	@Override
	public List<SubcontinentReferenceDto> getAllActiveByContinent(String uuid) {
		Continent continent = continentService.getByUuid(uuid);
		return continent.getSubcontinents().stream().filter(d -> !d.isArchived()).map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> subcontinentUuids) {
		return subcontinentService.isUsedInInfrastructureData(subcontinentUuids, Country.SUBCONTINENT, Country.class);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> subcontinentUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Subcontinent> root = cq.from(Subcontinent.class);
		Join<Subcontinent, Continent> continentJoin = root.join(Subcontinent.CONTINENT);

		cq.where(cb.and(cb.isTrue(continentJoin.get(Continent.ARCHIVED)), root.get(Subcontinent.UUID).in(subcontinentUuids)));

		cq.select(root.get(Subcontinent.ID));

		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	@Override
	public SubcontinentDto getByUuid(String uuid) {
		return toDto(subcontinentService.getByUuid(uuid));
	}

	@Override
	public List<SubcontinentIndexDto> getIndexList(SubcontinentCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Subcontinent> cq = cb.createQuery(Subcontinent.class);
		Root<Subcontinent> subcontinent = cq.from(Subcontinent.class);
		Join<Subcontinent, Continent> continent = subcontinent.join(Subcontinent.CONTINENT, JoinType.LEFT);

		Predicate filter = subcontinentService.buildCriteriaFilter(criteria, cb, subcontinent);

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case SubcontinentIndexDto.DISPLAY_NAME:
					expression = subcontinent.get(Subcontinent.DEFAULT_NAME);
					break;
				case SubcontinentDto.CONTINENT:
					expression = continent.get(Continent.DEFAULT_NAME);
					break;
				case SubcontinentDto.EXTERNAL_ID:
					expression = subcontinent.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(subcontinent.get(Subcontinent.DEFAULT_NAME)));
		}

		cq.select(subcontinent);

		if (first != null && max != null) {
			return em.createQuery(cq)
				.setFirstResult(first)
				.setMaxResults(max)
				.getResultList()
				.stream()
				.map(this::toIndexDto)
				.collect(Collectors.toList());
		} else {
			return em.createQuery(cq).getResultList().stream().map(this::toIndexDto).collect(Collectors.toList());
		}
	}

	@Override
	public void archive(String uuid) {
		Subcontinent subcontinent = subcontinentService.getByUuid(uuid);
		if (subcontinent != null) {
			subcontinent.setArchived(true);
			subcontinentService.ensurePersisted(subcontinent);
		}
	}

	@Override
	public void dearchive(String uuid) {
		Subcontinent subcontinent = subcontinentService.getByUuid(uuid);
		if (subcontinent != null) {
			subcontinent.setArchived(false);
			subcontinentService.ensurePersisted(subcontinent);
		}
	}

	@Override
	public List<SubcontinentDto> getAllAfter(Date date) {
		return subcontinentService.getAll((cb, root) -> subcontinentService.createChangeDateFilter(cb, root, date))
			.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<SubcontinentDto> getByUuids(List<String> uuids) {
		return subcontinentService.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return subcontinentService.getAllUuids();
	}

	@Override
	public List<SubcontinentReferenceDto> getAllActiveAsReference() {
		return subcontinentService.getAllActive(Subcontinent.DEFAULT_NAME, true)
			.stream()
			.map(SubcontinentFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(SubcontinentReferenceDto::getCaption))
			.collect(Collectors.toList());
	}

	@Override
	public void save(SubcontinentDto dto) {
		save(dto, false);
	}

	@Override
	public void save(SubcontinentDto dto, boolean allowMerge) {

		Subcontinent subcontinent = subcontinentService.getByUuid(dto.getUuid());

		if (subcontinent == null) {
			List<SubcontinentReferenceDto> duplicates = getByDefaultName(dto.getDefaultName(), true);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					String uuid = duplicates.get(0).getUuid();
					subcontinent = subcontinentService.getByUuid(uuid);
					SubcontinentDto dtoToMerge = getByUuid(uuid);
					dto = DtoHelper.copyDtoValues(dtoToMerge, dto, true);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importSubcontinentAlreadyExists));
				}
			}
		}

		subcontinent = fillOrBuildEntity(dto, subcontinent, true);
		subcontinentService.ensurePersisted(subcontinent);
	}

	@Override
	public long count(SubcontinentCriteria criteria) {
		return subcontinentService.count((cb, root) -> subcontinentService.buildCriteriaFilter(criteria, cb, root));
	}

	public SubcontinentDto toDto(Subcontinent entity) {
		if (entity == null) {
			return null;
		}
		SubcontinentDto dto = new SubcontinentDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());
		dto.setContinent(ContinentFacadeEjb.toReferenceDto(entity.getContinent()));

		return dto;
	}

	public SubcontinentIndexDto toIndexDto(Subcontinent entity) {
		if (entity == null) {
			return null;
		}
		SubcontinentIndexDto dto = new SubcontinentIndexDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setDisplayName(I18nProperties.getSubcontinentName(entity.getDefaultName()));
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());
		dto.setContinent(ContinentFacadeEjb.toReferenceDto(entity.getContinent()));

		return dto;
	}

	public List<SubcontinentReferenceDto> getByExternalId(String externalId, boolean includeArchived) {
		return subcontinentService.getByExternalId(externalId, includeArchived)
				.stream()
				.map(SubcontinentFacadeEjb::toReferenceDto)
				.collect(Collectors.toList());
	}

	public List<SubcontinentReferenceDto> getReferencesByName(String caption, boolean includeArchived) {
		return subcontinentService.getByDefaultName(caption, includeArchived)
				.stream()
				.map(SubcontinentFacadeEjb::toReferenceDto)
				.collect(Collectors.toList());
	}

	private Subcontinent fillOrBuildEntity(@NotNull SubcontinentDto source, Subcontinent target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Subcontinent::new, checkChangeDate);

		target.setDefaultName(source.getDefaultName());
		target.setArchived(source.isArchived());
		target.setExternalId(source.getExternalId());
		target.setContinent(continentService.getByReferenceDto(source.getContinent()));

		return target;
	}

	@LocalBean
	@Stateless
	public static class SubcontinentFacadeEjbLocal extends SubcontinentFacadeEjb {

	}
}
