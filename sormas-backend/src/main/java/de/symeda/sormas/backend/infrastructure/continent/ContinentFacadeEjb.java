/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.continent.ContinentCriteria;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentFacade;
import de.symeda.sormas.api.infrastructure.continent.ContinentIndexDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ContinentFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class ContinentFacadeEjb
	extends AbstractInfrastructureFacadeEjb<Continent, ContinentDto, ContinentIndexDto, ContinentReferenceDto, ContinentService, ContinentCriteria>
	implements ContinentFacade {

	@EJB
	private CountryService countryService;
	@EJB
	private SubcontinentService subcontinentService;

	public ContinentFacadeEjb() {
	}

	@Inject
	protected ContinentFacadeEjb(ContinentService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			Continent.class,
			ContinentDto.class,
			service,
			featureConfiguration,
			Validations.importContinentAlreadyExists,
			Strings.messageContinentArchivingNotPossible,
			null);
	}

	public static ContinentReferenceDto toReferenceDto(Continent entity) {
		if (entity == null) {
			return null;
		}
		return new ContinentReferenceDto(entity.getUuid(), entity.getDefaultName(), entity.getExternalId());
	}

	public static ContinentReferenceDto toReferenceDto(ContinentDto dto) {
		if (dto == null) {
			return null;
		}
		return new ContinentReferenceDto(dto.getUuid(), dto.buildCaption(), dto.getExternalId());
	}

	@Override
	@PermitAll
	public List<ContinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities) {
		return service.getByDefaultName(name, includeArchivedEntities).stream().map(ContinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> continentUuids) {
		return service.isUsedInInfrastructureData(continentUuids, Subcontinent.CONTINENT, Subcontinent.class);
	}

	@Override
	@PermitAll
	public ContinentReferenceDto getBySubcontinent(SubcontinentReferenceDto subcontinentReferenceDto) {
		return toReferenceDto(subcontinentService.getByUuid(subcontinentReferenceDto.getUuid()).getContinent());
	}

	@Override
	@PermitAll
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
	public List<ContinentIndexDto> getIndexList(ContinentCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Continent> cq = cb.createQuery(Continent.class);
		Root<Continent> continent = cq.from(Continent.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, continent);

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case ContinentIndexDto.DISPLAY_NAME:
					expression = cb.lower(continent.get(Continent.DEFAULT_NAME));
					break;
				case ContinentDto.EXTERNAL_ID:
					expression = cb.lower(continent.get(sortProperty.propertyName));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(cb.lower(continent.get(Continent.DEFAULT_NAME))));
		}

		cq.select(continent);

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	@Override
	@PermitAll
	public List<ContinentReferenceDto> getAllActiveAsReference() {
		return service.getAllActive(Continent.DEFAULT_NAME, true)
			.stream()
			.map(ContinentFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(ContinentReferenceDto::getCaption))
			.collect(Collectors.toList());
	}

	@Override
	protected List<Continent> findDuplicates(ContinentDto dto, boolean includeArchived) {
		return service.getByDefaultName(dto.getDefaultName(), includeArchived);
	}

	@Override
	public ContinentDto toDto(Continent entity) {
		if (entity == null) {
			return null;
		}
		ContinentDto dto = new ContinentDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());
		applyToDtoInheritance(dto, entity);

		return dto;
	}

	@Override
	protected ContinentReferenceDto toRefDto(Continent continent) {
		return toReferenceDto(continent);
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

	@Override
	public List<ContinentReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchived) {
		return service.getByExternalId(externalId, includeArchived).stream().map(ContinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public List<ContinentReferenceDto> getReferencesByName(String name, boolean includeArchived) {
		return service.getByDefaultName(name, includeArchived).stream().map(ContinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	protected Continent fillOrBuildEntity(@NotNull ContinentDto source, Continent target, boolean checkChangeDate, boolean allowUuidOverwrite) {
		target = DtoHelper.fillOrBuildEntity(source, target, Continent::new, checkChangeDate, allowUuidOverwrite);

		target.setDefaultName(source.getDefaultName());
		target.setExternalId(source.getExternalId());
		applyFillOrBuildEntityInheritance(target, source);
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
