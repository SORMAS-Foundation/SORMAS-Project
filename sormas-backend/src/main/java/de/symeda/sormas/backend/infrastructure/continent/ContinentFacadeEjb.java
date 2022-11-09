/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.infrastructure.continent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.continent.ContinentCriteria;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentFacade;
import de.symeda.sormas.api.infrastructure.continent.ContinentIndexDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureEjb;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "ContinentFacade")
public class ContinentFacadeEjb extends AbstractInfrastructureEjb<Continent, ContinentService> implements ContinentFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CountryService countryService;
	@EJB
	private SubcontinentService subcontinentService;

	public ContinentFacadeEjb() {
	}

	@Inject
	protected ContinentFacadeEjb(ContinentService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(service, featureConfiguration);
	}

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
		return service.getByDefaultName(name, includeArchivedEntities).stream().map(ContinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> continentUuids) {
		return service.isUsedInInfrastructureData(continentUuids, Subcontinent.CONTINENT, Subcontinent.class);
	}

	@Override
	public ContinentReferenceDto getBySubcontinent(SubcontinentReferenceDto subcontinentReferenceDto) {
		return toReferenceDto(subcontinentService.getByUuid(subcontinentReferenceDto.getUuid()).getContinent());
	}

	@Override
	public ContinentReferenceDto getByCountry(CountryReferenceDto countryReferenceDto) {
		final Country country = countryService.getByUuid(countryReferenceDto.getUuid());
		final Subcontinent subcontinent = country.getSubcontinent();
		return subcontinent != null ? toReferenceDto(subcontinent.getContinent()) : null;
	}

	@Override
	public Page<ContinentIndexDto> getIndexPage(ContinentCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<ContinentIndexDto> continentIndexList = getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(continentIndexList, offset, size, totalElementCount);
	}

	@Override
	public ContinentDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<ContinentIndexDto> getIndexList(ContinentCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Continent> cq = cb.createQuery(Continent.class);
		Root<Continent> continent = cq.from(Continent.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, continent);

		if (filter != null) {
			cq.where(filter).distinct(true);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case ContinentIndexDto.DISPLAY_NAME:
					expression = continent.get(Continent.DEFAULT_NAME);
					break;
				case ContinentDto.EXTERNAL_ID:
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

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	@Override
	public List<ContinentDto> getAllAfter(Date date) {
		return service.getAll((cb, root) -> service.createChangeDateFilter(cb, root, date)).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<ContinentDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids();
	}

	@Override
	public List<ContinentReferenceDto> getAllActiveAsReference() {
		return service.getAllActive(Continent.DEFAULT_NAME, true)
			.stream()
			.map(ContinentFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(ContinentReferenceDto::getCaption))
			.collect(Collectors.toList());
	}

	@Override
	public ContinentDto save(@Valid ContinentDto dto) {
		return save(dto, false);
	}

	@Override
	public ContinentDto save(@Valid ContinentDto dto, boolean allowMerge) {
		checkInfraDataLocked();

		Continent continent = service.getByUuid(dto.getUuid());

		if (continent == null) {
			List<Continent> duplicates = service.getByDefaultName(dto.getDefaultName(), true);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					continent = duplicates.get(0);
					ContinentDto dtoToMerge = getByUuid(continent.getUuid());
					dto = DtoHelper.copyDtoValues(dtoToMerge, dto, true);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importContinentAlreadyExists));
				}
			}
		}

		continent = fillOrBuildEntity(dto, continent, true);
		service.ensurePersisted(continent);

		return toDto(continent);
	}

	@Override
	public long count(ContinentCriteria criteria) {
		return service.count((cb, root) -> service.buildCriteriaFilter(criteria, cb, root));
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

	public List<ContinentReferenceDto> getByExternalId(Long externalId, boolean includeArchived) {
		return service.getByExternalId(externalId, includeArchived).stream().map(ContinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	public List<ContinentReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return service.getByDefaultName(name, includeArchived).stream().map(ContinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
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

		public ContinentFacadeEjbLocal() {

		}

		@Inject
		protected ContinentFacadeEjbLocal(ContinentService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}


}
