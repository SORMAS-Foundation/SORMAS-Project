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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureEjb;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.country.CountryIndexDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.EmptyValueException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.continent.ContinentService;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "CountryFacade")
public class CountryFacadeEjb extends AbstractInfrastructureEjb<Country, CountryService> implements CountryFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ContinentService continentService;
	@EJB
	private SubcontinentService subcontinentService;

	@EJB
	private UserService userService;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	public CountryFacadeEjb() {
	}

	@Inject
	protected CountryFacadeEjb(CountryService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
		super(service, featureConfiguration);
	}

	@Override
	public CountryDto getCountryByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
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
		return subcontinent.getCountries().stream().filter(d -> !d.isArchived()).map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<CountryReferenceDto> getAllActiveByContinent(String uuid) {
		Continent continent = continentService.getByUuid(uuid);
		return continent.getSubcontinents()
			.stream()
			.flatMap(subcontinent -> subcontinent.getCountries().stream().filter(d -> !d.isArchived()).map(f -> toReferenceDto(f)))
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

		if (sortProperties != null && sortProperties.size() > 0) {
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

	public Page<CountryIndexDto> getIndexPage(CountryCriteria countryCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<CountryIndexDto> countryIndexList = getIndexList(countryCriteria, offset, size, sortProperties);
		long totalElementCount = count(countryCriteria);
		return new Page<>(countryIndexList, offset, size, totalElementCount);
	}

	@Override
	public long count(CountryCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Country> root = cq.from(Country.class);

		Predicate filter = null;

		if (criteria != null) {
			filter = service.buildCriteriaFilter(criteria, cb, root);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public CountryDto save(@Valid CountryDto dto) throws ValidationRuntimeException {
		return save(dto, false);
	}

	@Override
	public CountryDto save(@Valid CountryDto dto, boolean allowMerge) throws ValidationRuntimeException {
		checkInfraDataLocked();
		
		if (StringUtils.isBlank(dto.getIsoCode())) {
			throw new EmptyValueException(I18nProperties.getValidationError(Validations.importCountryEmptyIso));
		}

		Country country = service.getByUuid(dto.getUuid());

		if (country == null) {
			Optional<Country> byIsoCode = service.getByIsoCode(dto.getIsoCode(), true);
			Optional<Country> byUnoCode = service.getByUnoCode(dto.getUnoCode(), true);
			if (byIsoCode.isPresent() || byUnoCode.isPresent()) {
				if (allowMerge) {
					country = byIsoCode.isPresent() ? byIsoCode.get() : byUnoCode.get();
					CountryDto dtoToMerge = getCountryByUuid(country.getUuid());
					dto = DtoHelper.copyDtoValues(dtoToMerge, dto, true);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.importCountryAlreadyExists));
				}
			}
		}

		country = fillOrBuildEntity(dto, country, true);
		service.ensurePersisted(country);
		return toDto(country);
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

		return dto;
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

	public List<CountryReferenceDto> getByExternalId(Long externalId, boolean includeArchived) {
		return service.getByExternalId(externalId, includeArchived).stream().map(CountryFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	public List<CountryReferenceDto> getReferencesByName(String caption, boolean includeArchived) {
		return service.getByDefaultName(caption, includeArchived).stream().map(CountryFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	private Country fillOrBuildEntity(@NotNull CountryDto source, Country target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Country::new, checkChangeDate);

		target.setDefaultName(source.getDefaultName());
		target.setArchived(source.isArchived());
		target.setExternalId(source.getExternalId());
		target.setIsoCode(source.getIsoCode());
		target.setUnoCode(source.getUnoCode());
		final SubcontinentReferenceDto subcontinent = source.getSubcontinent();
		if (subcontinent != null) {
			target.setSubcontinent(subcontinentService.getByUuid(subcontinent.getUuid()));
		}

		return target;
	}

	@Override
	public List<CountryDto> getAllAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CountryDto> cq = cb.createQuery(CountryDto.class);
		Root<Country> country = cq.from(Country.class);

		selectDtoFields(cq, country);

		Predicate filter = service.createChangeDateFilter(cb, country, date);

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	@Override
	public CountryDto getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public List<CountryDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return service.getAllUuids();
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

		cq.where(cb.and(cb.isTrue(subcontinentJoin.get(Subcontinent.ARCHIVED)), root.get(Country.UUID).in(countryUuids)));

		cq.select(root.get(Country.ID));

		return QueryHelper.getFirstResult(em, cq) != null;
	}

	// Need to be in the same order as in the constructor
	private void selectDtoFields(CriteriaQuery<CountryDto> cq, Root<Country> root) {

		Join<Country, Subcontinent> subcontinent = root.join(Country.SUBCONTINENT, JoinType.LEFT);

		cq.multiselect(
			root.get(Country.CREATION_DATE),
			root.get(Country.CHANGE_DATE),
			root.get(Country.UUID),
			root.get(Country.ARCHIVED),
			root.get(Country.DEFAULT_NAME),
			root.get(Country.EXTERNAL_ID),
			root.get(Country.ISO_CODE),
			root.get(Country.UNO_CODE),
			subcontinent.get(Subcontinent.UUID),
			subcontinent.get(Subcontinent.DEFAULT_NAME),
			subcontinent.get(Subcontinent.EXTERNAL_ID));
	}

	@LocalBean
	@Stateless
	public static class CountryFacadeEjbLocal extends CountryFacadeEjb {

		public CountryFacadeEjbLocal() {
		}

		@Inject
		protected CountryFacadeEjbLocal(CountryService service, FeatureConfigurationFacadeEjbLocal featureConfiguration) {
			super(service, featureConfiguration);
		}
	}
}
