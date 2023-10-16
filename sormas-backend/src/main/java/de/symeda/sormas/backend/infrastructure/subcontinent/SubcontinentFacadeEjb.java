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

package de.symeda.sormas.backend.infrastructure.subcontinent;

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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentCriteria;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentFacade;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentIndexDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SubcontinentFacade")
@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
public class SubcontinentFacadeEjb
	extends
	AbstractInfrastructureFacadeEjb<Subcontinent, SubcontinentDto, SubcontinentIndexDto, SubcontinentReferenceDto, SubcontinentService, SubcontinentCriteria>
	implements SubcontinentFacade {

	@EJB
	private ContinentService continentService;
	@EJB
	private CountryService countryService;

	public SubcontinentFacadeEjb() {
	}

	@Inject
	protected SubcontinentFacadeEjb(SubcontinentService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(
			Subcontinent.class,
			SubcontinentDto.class,
			service,
			featureConfiguration,
			Validations.importSubcontinentAlreadyExists,
			Strings.messageSubcontinentArchivingNotPossible,
			Strings.messageSubcontinentDearchivingNotPossible);
	}

	public static SubcontinentReferenceDto toReferenceDto(Subcontinent entity) {
		if (entity == null) {
			return null;
		}
		return new SubcontinentReferenceDto(entity.getUuid(), entity.getDefaultName(), entity.getExternalId());
	}

	public static SubcontinentReferenceDto toReferenceDto(SubcontinentDto dto) {
		if (dto == null) {
			return null;
		}
		return new SubcontinentReferenceDto(dto.getUuid(), dto.buildCaption(), dto.getExternalId());
	}

	@Override
	@PermitAll
	public List<SubcontinentReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities) {
		return service.getByDefaultName(name, includeArchivedEntities)
			.stream()
			.map(SubcontinentFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public SubcontinentReferenceDto getByCountry(CountryReferenceDto countryDto) {
		return toReferenceDto(countryService.getByUuid(countryDto.getUuid()).getSubcontinent());
	}

	@Override
	@PermitAll
	public List<SubcontinentReferenceDto> getAllActiveByContinent(String uuid) {
		Continent continent = continentService.getByUuid(uuid);
		return continent.getSubcontinents()
			.stream()
			.filter(d -> !d.isArchived())
			.map(SubcontinentFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public boolean isUsedInOtherInfrastructureData(Collection<String> subcontinentUuids) {
		return service.isUsedInInfrastructureData(subcontinentUuids, Country.SUBCONTINENT, Country.class);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> subcontinentUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Subcontinent> root = cq.from(Subcontinent.class);
		Join<Subcontinent, Continent> continentJoin = root.join(Subcontinent.CONTINENT);

		cq.where(cb.and(cb.isTrue(continentJoin.get(InfrastructureAdo.ARCHIVED)), root.get(AbstractDomainObject.UUID).in(subcontinentUuids)));

		cq.select(root.get(AbstractDomainObject.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	@Override
	public List<SubcontinentIndexDto> getIndexList(SubcontinentCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Subcontinent> cq = cb.createQuery(Subcontinent.class);
		Root<Subcontinent> subcontinent = cq.from(Subcontinent.class);
		Join<Subcontinent, Continent> continent = subcontinent.join(Subcontinent.CONTINENT, JoinType.LEFT);

		Predicate filter = null;
		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, subcontinent);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
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

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	@Override
	public Page<SubcontinentIndexDto> getIndexPage(SubcontinentCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<SubcontinentIndexDto> subcontinentIndexList = getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(subcontinentIndexList, offset, size, totalElementCount);
	}

	@Override
	@PermitAll
	public List<SubcontinentReferenceDto> getAllActiveAsReference() {
		return service.getAllActive(Subcontinent.DEFAULT_NAME, true)
			.stream()
			.map(SubcontinentFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(SubcontinentReferenceDto::getCaption))
			.collect(Collectors.toList());
	}

	@Override
	protected List<Subcontinent> findDuplicates(SubcontinentDto dto, boolean includeArchived) {
		return service.getByDefaultName(dto.getDefaultName(), includeArchived);
	}

	public SubcontinentDto toDto(Subcontinent entity) {
		if (entity == null) {
			return null;
		}
		SubcontinentDto dto = new SubcontinentDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setExternalId(entity.getExternalId());
		dto.setUuid(entity.getUuid());
		dto.setContinent(ContinentFacadeEjb.toReferenceDto(entity.getContinent()));
		applyToDtoInheritance(dto, entity);

		return dto;
	}

	@Override
	protected SubcontinentReferenceDto toRefDto(Subcontinent subcontinent) {
		return toReferenceDto(subcontinent);
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

	@Override
	@PermitAll
	public List<SubcontinentReferenceDto> getReferencesByExternalId(String externalId, boolean includeArchived) {
		return service.getByExternalId(externalId, includeArchived).stream().map(SubcontinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<SubcontinentReferenceDto> getReferencesByName(String caption, boolean includeArchived) {
		return service.getByDefaultName(caption, includeArchived).stream().map(SubcontinentFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	protected Subcontinent fillOrBuildEntity(
		@NotNull SubcontinentDto source,
		Subcontinent target,
		boolean checkChangeDate,
		boolean allowUuidOverwrite) {

		target = DtoHelper.fillOrBuildEntity(source, target, Subcontinent::new, checkChangeDate, allowUuidOverwrite);

		target.setDefaultName(source.getDefaultName());
		target.setExternalId(source.getExternalId());
		target.setContinent(continentService.getByReferenceDto(source.getContinent()));
		applyFillOrBuildEntityInheritance(target, source);

		return target;
	}

	@LocalBean
	@Stateless
	public static class SubcontinentFacadeEjbLocal extends SubcontinentFacadeEjb {

		public SubcontinentFacadeEjbLocal() {
		}

		@Inject
		protected SubcontinentFacadeEjbLocal(SubcontinentService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
