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

package de.symeda.sormas.backend.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@AuditIgnore(retainWrites = true)
@Stateless(name = "FeatureConfigurationFacade")
public class FeatureConfigurationFacadeEjb implements FeatureConfigurationFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private FeatureConfigurationService service;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private UserService userService;

	@EJB
	private CountryFacadeEjb.CountryFacadeEjbLocal countryFacadeEjb;

	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacadeEjb;

	@Override
	@PermitAll
	public List<FeatureConfigurationDto> getAllAfter(Date date) {
		return service.getAllAfter(date).stream().map(FeatureConfigurationFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<FeatureConfigurationDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(FeatureConfigurationFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids();
	}

	@Override
	@PermitAll
	public List<String> getDeletedUuids(Date since) {

		User user = userService.getCurrentUser();
		return service.getDeletedUuids(since, user);
	}

	public Map<Disease, List<FeatureConfigurationIndexDto>> getEnabledFeatureConfigurations(FeatureConfigurationCriteria criteria) {

		List<FeatureConfigurationIndexDto> featureConfigurations = getFeatureConfigurations(criteria, false);
		Map<Disease, List<FeatureConfigurationIndexDto>> diseaseListMap = new TreeMap<>();
		featureConfigurations.forEach(featureConfigurationIndexDto -> {
			if (!diseaseListMap.containsKey(featureConfigurationIndexDto.getDisease())) {
				diseaseListMap.put(featureConfigurationIndexDto.getDisease(), new ArrayList<>());
			}
			diseaseListMap.get(featureConfigurationIndexDto.getDisease()).add(featureConfigurationIndexDto);
		});
		return diseaseListMap;
	}

	public Page<FeatureConfigurationIndexDto> getIndexPage(
		FeatureConfigurationCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<FeatureConfigurationIndexDto> featureConfigurationIndexList = getIndexList(criteria, offset, size, sortProperties);
		CountryReferenceDto serverCountry = countryFacadeEjb.getServerCountry();
		long totalElementCount = districtFacadeEjb.count(new DistrictCriteria().country(serverCountry).region(criteria.getRegion()));
		return new Page<>(featureConfigurationIndexList, offset, size, totalElementCount);
	}

	private List<FeatureConfigurationIndexDto> getIndexList(
		FeatureConfigurationCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {

		if (criteria == null || criteria.getDisease() == null) {
			throw new IllegalArgumentException("disease field cannot be null!");
		}

		CountryReferenceDto serverCountry = countryFacadeEjb.getServerCountry();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureConfigurationIndexDto> cq = cb.createQuery(FeatureConfigurationIndexDto.class);
		Root<District> root = cq.from(District.class);
		Join<District, Region> regionJoin = root.join(District.REGION, JoinType.LEFT);
		Join<District, FeatureConfiguration> featureConfigurationJoin = root.join(District.FEATURE_CONFIGURATIONS, JoinType.LEFT);
		Predicate filterJoinOnDisease = CriteriaBuilderHelper.or(
			cb,
			cb.and(cb.equal(featureConfigurationJoin.get(FeatureConfiguration.DISEASE), criteria.getDisease())),
			cb.and(cb.isNull(featureConfigurationJoin.get(FeatureConfiguration.DISEASE))));
		featureConfigurationJoin.on(filterJoinOnDisease);

		cq.multiselect(
			featureConfigurationJoin.get(FeatureConfiguration.UUID),
			regionJoin.get(Region.UUID),
			regionJoin.get(Region.NAME),
			root.get(District.UUID),
			root.get(District.NAME),
			featureConfigurationJoin.get(FeatureConfiguration.DISEASE),
			featureConfigurationJoin.get(FeatureConfiguration.ENABLED),
			featureConfigurationJoin.get(FeatureConfiguration.END_DATE));

		Predicate filter = null;

		if (serverCountry != null) {
			Path<Object> countryUuid = regionJoin.join(Region.COUNTRY, JoinType.LEFT).get(Country.UUID);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.isNull(countryUuid), cb.equal(countryUuid, serverCountry.getUuid())));
		}

		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(regionJoin.get(Region.UUID), criteria.getRegion().getUuid()));

		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(District.UUID), criteria.getDistrict().getUuid()));
		}

		if (criteria.getSearchText() != null) {
			Predicate searchText = CriteriaBuilderHelper.unaccentedIlike(cb, root.get(District.NAME), criteria.getSearchText());
			if (criteria.getRegion() == null) {
				searchText = cb.or(searchText, CriteriaBuilderHelper.unaccentedIlike(cb, regionJoin.get(Region.NAME), criteria.getSearchText()));
			}
			filter = CriteriaBuilderHelper.and(cb, filter, searchText);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case FeatureConfiguration.REGION:
					expression = regionJoin.get(Region.NAME);
					break;
				case FeatureConfiguration.DISTRICT:
					expression = root.get(District.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.asc(regionJoin.get(Region.NAME)));
		}
		return QueryHelper.getResultList(em, cq, offset, size);

	}

	@Override
	public List<FeatureConfigurationIndexDto> getFeatureConfigurations(FeatureConfigurationCriteria criteria, boolean includeInactive) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureConfigurationIndexDto> cq = cb.createQuery(FeatureConfigurationIndexDto.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);
		Join<FeatureConfiguration, Region> regionJoin = root.join(FeatureConfiguration.REGION, JoinType.LEFT);
		Join<FeatureConfiguration, District> districtJoin = root.join(FeatureConfiguration.DISTRICT, JoinType.LEFT);

		cq.multiselect(
			root.get(FeatureConfiguration.UUID),
			regionJoin.get(Region.UUID),
			regionJoin.get(Region.NAME),
			districtJoin.get(District.UUID),
			districtJoin.get(District.NAME),
			root.get(FeatureConfiguration.DISEASE),
			root.get(FeatureConfiguration.ENABLED),
			root.get(FeatureConfiguration.END_DATE));

		if (criteria != null) {
			Predicate filter = service.createCriteriaFilter(criteria, cb, cq, root);
			if (filter != null) {
				cq.where(filter);
			}
		}

		List<FeatureConfigurationIndexDto> resultList = em.createQuery(cq).getResultList();

		if (includeInactive) {
			if (criteria.getDistrict() == null) {
				List<District> districts;
				if (criteria.getRegion() != null) {
					Region region = regionService.getByUuid(criteria.getRegion().getUuid());
					districts = districtService.getAllActiveByRegion(region);
				} else {
					districts = districtService.getAllActiveByServerCountry();
				}

				List<String> activeUuids = resultList.stream().map(FeatureConfigurationIndexDto::getDistrictUuid).collect(Collectors.toList());
				districts = districts.stream().filter(district -> !activeUuids.contains(district.getUuid())).collect(Collectors.toList());

				for (District district : districts) {
					resultList.add(
						new FeatureConfigurationIndexDto(
							DataHelper.createUuid(),
							district.getRegion().getUuid(),
							district.getRegion().getName(),
							district.getUuid(),
							district.getName(),
							criteria.getDisease(),
							false,
							null));
				}
			}
		}

		if (criteria.getRegion() != null) {
			resultList.sort(Comparator.comparing(FeatureConfigurationIndexDto::getDistrictName));
		} else {
			resultList.sort((c1, c2) -> {
				if (c1.getRegionName().equals(c2.getRegionName())) {
					return c1.getDistrictName().compareTo(c2.getDistrictName());
				} else {
					return c1.getRegionName().compareTo(c2.getRegionName());
				}
			});
		}
		return resultList;
	}

	@Override
	public void saveFeatureConfigurations(@Valid Collection<FeatureConfigurationIndexDto> configurations, FeatureType featureType) {

		for (FeatureConfigurationIndexDto config : configurations) {
			saveFeatureConfiguration(config, featureType);
		}
	}

	@Override
	public void saveFeatureConfiguration(@Valid FeatureConfigurationIndexDto configuration, FeatureType featureType) {

		// Delete an existing configuration that was set inactive and is not a server feature
		if (!featureType.isServerFeature() && Boolean.FALSE.equals(configuration.isEnabled())) {
			FeatureConfiguration existingConfiguration = service.getByUuid(configuration.getUuid());
			if (existingConfiguration != null) {
				service.deletePermanent(existingConfiguration);
			}

			return;
		}

		// Create or update an active configuration
		FeatureConfigurationDto configurationDto = toDto(service.getByUuid(configuration.getUuid()));
		if (configurationDto == null) {
			configurationDto = FeatureConfigurationDto.build();
			configurationDto.setFeatureType(featureType);
			configurationDto.setDisease(configuration.getDisease());
			configurationDto.setRegion(new RegionReferenceDto(configuration.getRegionUuid(), null, null));
			configurationDto.setDistrict(new DistrictReferenceDto(configuration.getDistrictUuid(), null, null));
			configurationDto.setEnabled(configuration.isEnabled());
		}

		if (configuration.getEndDate() != null) {
			configurationDto.setEndDate(DateHelper.getEndOfDay(configuration.getEndDate()));
		}

		FeatureConfiguration entity = fillOrBuildEntity(configurationDto, service.getByUuid(configuration.getUuid()), true);
		service.ensurePersisted(entity);
	}

	@Override
	public void deleteAllFeatureConfigurations(FeatureConfigurationCriteria criteria) {

		if (criteria == null) {
			return;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureConfiguration> cq = cb.createQuery(FeatureConfiguration.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		Predicate filter = service.createCriteriaFilter(criteria, cb, cq, root);
		if (filter != null) {
			cq.where(filter);
		}

		List<FeatureConfiguration> resultList = em.createQuery(cq).getResultList();
		resultList.forEach(result -> service.deletePermanent(result));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteAllExpiredFeatureConfigurations(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureConfiguration> cq = cb.createQuery(FeatureConfiguration.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		cq.where(cb.lessThan(root.get(FeatureConfiguration.END_DATE), date));
		List<FeatureConfiguration> resultList = em.createQuery(cq).getResultList();
		resultList.forEach(result -> service.deletePermanent(result));
	}

	@Override
	public boolean isFeatureDisabled(FeatureType featureType) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		cq.where(cb.and(cb.equal(root.get(FeatureConfiguration.FEATURE_TYPE), featureType), cb.isFalse(root.get(FeatureConfiguration.ENABLED))));
		cq.select(cb.count(root));

		return em.createQuery(cq).getSingleResult() > 0;
	}

	@Override
	public boolean isFeatureEnabled(FeatureType featureType, DeletableEntityType entityType) {

		if (entityType == null) {
			throw new IllegalArgumentException("Entity type must be specified!");
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		cq.where(
			cb.and(
				cb.equal(root.get(FeatureConfiguration.FEATURE_TYPE), featureType),
				cb.and(cb.isNotNull(root.get(FeatureConfiguration.ENTITY_TYPE)), cb.equal(root.get(FeatureConfiguration.ENTITY_TYPE), entityType)),
				cb.isTrue(root.get(FeatureConfiguration.ENABLED))));
		cq.select(cb.count(root));

		return em.createQuery(cq).getSingleResult() > 0;
	}

	public <T extends Object> T getProperty(
		FeatureType featureType,
		DeletableEntityType entityType,
		FeatureTypeProperty property,
		Class<T> returnType) {

		if (!featureType.getSupportedProperties().contains(property)) {
			throw new IllegalArgumentException("Feature type " + featureType + " does not support property " + property + ".");
		}

		if (!returnType.isAssignableFrom(property.getReturnType())) {
			throw new IllegalArgumentException(
				"Feature type property " + property + " does not have specified return type " + returnType.getSimpleName() + ".");
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<T> cq = cb.createQuery(returnType);
		final Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		Predicate predicate = cb.equal(root.get(FeatureConfiguration.FEATURE_TYPE), featureType);
		if (entityType != null) {
			predicate = cb.and(predicate, cb.equal(root.get(FeatureConfiguration.ENTITY_TYPE), entityType));
		}
		cq.where(predicate);
		cq.select(root.get(FeatureConfiguration.PROPERTIES));

		Map<FeatureTypeProperty, Object> properties = null;
		try {
			properties = (Map<FeatureTypeProperty, Object>) em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			// NOOP
		}

		if (properties != null && properties.containsKey(property)) {
			return (T) properties.get(property);
		} else {
			// Compare the expected property value with the default value
			return (T) featureType.getSupportedPropertyDefaults().get(property);
		}
	}

	@Override
	public boolean isPropertyValueTrue(FeatureType featureType, FeatureTypeProperty property) {

		if (!featureType.getSupportedProperties().contains(property)) {
			throw new IllegalArgumentException("Feature type " + featureType + " does not support property " + property + ".");
		}

		if (!Boolean.class.isAssignableFrom(property.getReturnType())) {
			throw new IllegalArgumentException(
				"Feature type property " + property + " does not have specified return type " + Boolean.class.getSimpleName() + ".");
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object> cq = cb.createQuery(Object.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		cq.where(cb.and(cb.equal(root.get(FeatureConfiguration.FEATURE_TYPE), featureType)));
		cq.select(root.get(FeatureConfiguration.PROPERTIES));

		Map<FeatureTypeProperty, Object> properties = null;
		try {
			properties = (Map<FeatureTypeProperty, Object>) em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			// NOOP
		}

		boolean result;
		if (properties != null && properties.containsKey(property)) {
			result = (boolean) properties.get(property);
		} else {
			// Compare the expected property value with the default value
			result = (boolean) featureType.getSupportedPropertyDefaults().get(property);
		}

		return result;
	}

	@Override
	public boolean isFeatureEnabled(FeatureType featureType) {
		return !isFeatureDisabled(featureType);
	}

	@Override
	public boolean isAnyFeatureEnabled(FeatureType... featureType) {
		return Stream.of(featureType).anyMatch(this::isFeatureEnabled);
	}

	@Override
	public boolean areAllFeatureEnabled(FeatureType... featureType) {
		return Stream.of(featureType).allMatch(this::isFeatureEnabled);
	}

	@Override
	public boolean isAnySurveillanceEnabled() {
		return isAnyFeatureEnabled(FeatureType.CASE_SURVEILANCE, FeatureType.EVENT_SURVEILLANCE, FeatureType.AGGREGATE_REPORTING);
	}

	@Override
	public boolean isCountryEnabled() {
		return isAnySurveillanceEnabled();
	}

	@Override
	public boolean isTaskGenerationFeatureEnabled(TaskType taskType) {

		for (TaskContext context : taskType.getTaskContexts()) {
			if (isFeatureEnabled(context.getFeatureType())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<FeatureConfigurationDto> getActiveServerFeatureConfigurations() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureConfiguration> cq = cb.createQuery(FeatureConfiguration.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		List<FeatureType> serverFeatures = FeatureType.getAllServerFeatures();
		if (serverFeatures.isEmpty()) {
			return null;
		}

		cq.where(cb.and(root.get(FeatureConfiguration.FEATURE_TYPE).in(serverFeatures), cb.isTrue(root.get(FeatureConfiguration.ENABLED))));

		return em.createQuery(cq).getResultList().stream().map(FeatureConfigurationFacadeEjb::toDto).collect(Collectors.toList());
	}

	public static FeatureConfigurationDto toDto(FeatureConfiguration source) {

		if (source == null) {
			return null;
		}

		FeatureConfigurationDto target = new FeatureConfigurationDto();
		DtoHelper.fillDto(target, source);

		target.setFeatureType(source.getFeatureType());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setEndDate(source.getEndDate());
		target.setEnabled(source.isEnabled());
		target.setProperties(source.getProperties());

		return target;
	}

	public FeatureConfiguration fillOrBuildEntity(@NotNull FeatureConfigurationDto source, FeatureConfiguration target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, FeatureConfiguration::new, checkChangeDate);

		target.setFeatureType(source.getFeatureType());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setEndDate(source.getEndDate());
		target.setEnabled(source.isEnabled());
		target.setProperties(source.getProperties());

		return target;
	}

	@LocalBean
	@Stateless
	public static class FeatureConfigurationFacadeEjbLocal extends FeatureConfigurationFacadeEjb {

	}
}
