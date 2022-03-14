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

package de.symeda.sormas.backend.infrastructure.country;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import de.symeda.sormas.api.utils.EmptyValueException;
import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.country.CountryIndexDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.continent.ContinentService;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.QueryHelper;
import org.apache.commons.lang3.StringUtils;

@Stateless(name = "CountryFacade")
public class CountryFacadeEjb
	extends AbstractInfrastructureFacadeEjb<Country, CountryDto, CountryIndexDto, CountryReferenceDto, CountryService, CountryCriteria>
	implements CountryFacade {

	@EJB
	private ContinentService continentService;
	@EJB
	private SubcontinentService subcontinentService;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	public CountryFacadeEjb() {
	}

	@Inject
	protected CountryFacadeEjb(CountryService service, FeatureConfigurationFacadeEjbLocal featureConfiguration, UserService userService) {
		super(Country.class, CountryDto.class, service, featureConfiguration, userService, Validations.importCountryAlreadyExists);
	}

	@Override
	protected CountryDto doSave(
		CountryDto dtoToSave,
		boolean allowMerge,
		boolean includeArchived,
		boolean checkChangeDate,
		String duplicateErrorMessageProperty) {
		if (StringUtils.isBlank(dtoToSave.getIsoCode())) {
			throw new EmptyValueException(I18nProperties.getValidationError(Validations.importCountryEmptyIso));
		}
		return super.doSave(dtoToSave, allowMerge, includeArchived, checkChangeDate, duplicateErrorMessageProperty);
	}

	@Override
	public List<CountryReferenceDto> getByDefaultName(String name, boolean includeArchivedEntities) {
		return service.getByDefaultName(name, includeArchivedEntities).stream().map(CountryFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public CountryDto getByIsoCode(String isoCode, boolean includeArchivedEntities) {
		return service.getByIsoCode(isoCode, includeArchivedEntities).map(this::toDto).orElse(null);
	}

	@Override
	public List<CountryReferenceDto> getAllActiveBySubcontinent(String uuid) {
		Subcontinent subcontinent = subcontinentService.getByUuid(uuid);
		return subcontinent.getCountries().stream().filter(d -> !d.isArchived()).map(CountryFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public List<CountryReferenceDto> getAllActiveByContinent(String uuid) {
		Continent continent = continentService.getByUuid(uuid);
		return continent.getSubcontinents()
			.stream()
			.flatMap(subcontinent -> subcontinent.getCountries().stream().filter(d -> !d.isArchived()).map(CountryFacadeEjb::toReferenceDto))
			.collect(Collectors.toList());
	}

	@Override
	public List<CountryIndexDto> getIndexList(CountryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Country> cq = cb.createQuery(Country.class);
		Root<Country> country = cq.from(Country.class);
		Join<Country, Subcontinent> subcontinent = country.join(Country.SUBCONTINENT, JoinType.LEFT);

		Predicate filter = null;
		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, country);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case CountryIndexDto.DISPLAY_NAME:
					expression = country.get(Country.DEFAULT_NAME);
					break;
				case CountryIndexDto.SUBCONTINENT:
					expression = subcontinent.get(Subcontinent.DEFAULT_NAME);
					break;
				case CountryIndexDto.EXTERNAL_ID:
				case CountryIndexDto.ISO_CODE:
				case CountryIndexDto.UNO_CODE:
					expression = country.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(country.get(Country.ISO_CODE)));
		}

		cq.select(country);

		return QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
	}

	@Override
	public Page<CountryIndexDto> getIndexPage(CountryCriteria countryCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<CountryIndexDto> countryIndexList = getIndexList(countryCriteria, offset, size, sortProperties);
		long totalElementCount = count(countryCriteria);
		return new Page<>(countryIndexList, offset, size, totalElementCount);
	}

	@Override
	protected List<Country> findDuplicates(CountryDto dto, boolean includeArchived) {
		Optional<Country> byIsoCode = service.getByIsoCode(dto.getIsoCode(), includeArchived);
		Optional<Country> byUnoCode = service.getByUnoCode(dto.getUnoCode(), includeArchived);
		List<Optional<Country>> tmp = Arrays.asList(byIsoCode, byUnoCode);
		return tmp.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	public static CountryReferenceDto toReferenceDto(Country entity) {
		if (entity == null) {
			return null;
		}
		return new CountryReferenceDto(
			entity.getUuid(),
			I18nProperties.getCountryName(entity.getIsoCode(), entity.getDefaultName()),
			entity.getIsoCode());
	}

	public static CountryReferenceDto toReferenceDto(CountryDto entity) {
		if (entity == null) {
			return null;
		}
		return new CountryReferenceDto(
			entity.getUuid(),
			I18nProperties.getCountryName(entity.getIsoCode(), entity.getDefaultName()),
			entity.getIsoCode());
	}

	@Override
	public CountryDto toDto(Country entity) {
		if (entity == null) {
			return null;
		}
		CountryDto dto = new CountryDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDefaultName(entity.getDefaultName());
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setIsoCode(entity.getIsoCode());
		dto.setUnoCode(entity.getUnoCode());
		dto.setUuid(entity.getUuid());
		dto.setSubcontinent(SubcontinentFacadeEjb.toReferenceDto(entity.getSubcontinent()));
		dto.setCentrallyManaged(entity.isCentrallyManaged());

		return dto;
	}

	@Override
	public CountryReferenceDto toRefDto(Country country) {
		return toReferenceDto(country);
	}

	public CountryIndexDto toIndexDto(Country entity) {
		if (entity == null) {
			return null;
		}
		CountryIndexDto dto = new CountryIndexDto();
		DtoHelper.fillDto(dto, entity);

		String isoCode = entity.getIsoCode();
		String displayName = I18nProperties.getCountryName(isoCode, entity.getDefaultName());
		dto.setDisplayName(displayName);
		dto.setDefaultName(entity.getDefaultName());
		dto.setArchived(entity.isArchived());
		dto.setExternalId(entity.getExternalId());
		dto.setIsoCode(isoCode);
		dto.setUnoCode(entity.getUnoCode());
		dto.setUuid(entity.getUuid());
		dto.setSubcontinent(SubcontinentFacadeEjb.toReferenceDto(entity.getSubcontinent()));

		return dto;
	}

	@Override
	public List<CountryReferenceDto> getByExternalId(String externalId, boolean includeArchived) {
		return service.getByExternalId(externalId, includeArchived).stream().map(CountryFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public List<CountryReferenceDto> getReferencesByName(String caption, boolean includeArchived) {
		return service.getByDefaultName(caption, includeArchived).stream().map(CountryFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	protected Country fillOrBuildEntity(@NotNull CountryDto source, Country target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Country::new, checkChangeDate);

		target.setDefaultName(source.getDefaultName());
		target.setArchived(source.isArchived());
		target.setExternalId(source.getExternalId());
		target.setIsoCode(source.getIsoCode());
		target.setUnoCode(source.getUnoCode());
		target.setCentrallyManaged(source.isCentrallyManaged());
		final SubcontinentReferenceDto subcontinent = source.getSubcontinent();
		if (subcontinent != null) {
			target.setSubcontinent(subcontinentService.getByUuid(subcontinent.getUuid()));
		}
		return target;
	}

	@Override
	public List<CountryReferenceDto> getAllActiveAsReference() {
		return service.getAllActive(Country.ISO_CODE, true)
			.stream()
			.map(CountryFacadeEjb::toReferenceDto)
			.sorted(Comparator.comparing(CountryReferenceDto::getCaption))
			.collect(Collectors.toList());
	}

	@Override
	public CountryReferenceDto getServerCountry() {
		String countryName = configFacadeEjb.getCountryName();
		List<CountryReferenceDto> countryReferenceDtos = getByDefaultName(countryName, false);
		return countryReferenceDtos.isEmpty() ? null : countryReferenceDtos.get(0);
	}

	@Override
	public boolean hasArchivedParentInfrastructure(Collection<String> countryUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Country> root = cq.from(Country.class);
		Join<Country, Subcontinent> subcontinentJoin = root.join(Country.SUBCONTINENT);

		cq.where(cb.and(cb.isTrue(subcontinentJoin.get(InfrastructureAdo.ARCHIVED)), root.get(AbstractDomainObject.UUID).in(countryUuids)));

		cq.select(root.get(AbstractDomainObject.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	@Override
	protected void selectDtoFields(CriteriaQuery<CountryDto> cq, Root<Country> root) {
		Join<Country, Subcontinent> subcontinent = root.join(Country.SUBCONTINENT, JoinType.LEFT);
		// Need to be in the same order as in the constructor
		cq.multiselect(
			root.get(AbstractDomainObject.CREATION_DATE),
			root.get(Country.CHANGE_DATE),
			root.get(AbstractDomainObject.UUID),
			root.get(InfrastructureAdo.ARCHIVED),
			root.get(Country.DEFAULT_NAME),
			root.get(Country.EXTERNAL_ID),
			root.get(Country.ISO_CODE),
			root.get(Country.UNO_CODE),
			subcontinent.get(AbstractDomainObject.UUID),
			subcontinent.get(Subcontinent.DEFAULT_NAME),
			subcontinent.get(Subcontinent.EXTERNAL_ID));
	}

	@LocalBean
	@Stateless
	public static class CountryFacadeEjbLocal extends CountryFacadeEjb {

		public CountryFacadeEjbLocal() {
		}

		@Inject
		protected CountryFacadeEjbLocal(CountryService service, FeatureConfigurationFacadeEjbLocal featureConfiguration, UserService userService) {
			super(service, featureConfiguration, userService);
		}
	}
}
