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

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

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

	@Override
	public List<FeatureConfigurationDto> getAllAfter(Date date) {

		User user = userService.getCurrentUser();
		return service.getAllAfter(date, user).stream().map(d -> toDto(d)).collect(Collectors.toList());
	}

	@Override
	public List<FeatureConfigurationDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(d -> toDto(d)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids();
	}

	@Override
	public List<String> getDeletedUuids(Date since) {

		User user = userService.getCurrentUser();
		return service.getDeletedUuids(since, user);
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
	public void saveFeatureConfigurations(Collection<FeatureConfigurationIndexDto> configurations, FeatureType featureType) {

		for (FeatureConfigurationIndexDto config : configurations) {
			saveFeatureConfiguration(config, featureType);
		}
	}

	@Override
	public void saveFeatureConfiguration(FeatureConfigurationIndexDto configuration, FeatureType featureType) {

		// Delete an existing configuration that was set inactive and is not a server feature
		if (!featureType.isServerFeature() && Boolean.FALSE.equals(configuration.isEnabled())) {
			FeatureConfiguration existingConfiguration = service.getByUuid(configuration.getUuid());
			if (existingConfiguration != null) {
				service.delete(existingConfiguration);
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

		FeatureConfiguration entity = fromDto(configurationDto, true);
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
		resultList.forEach(result -> service.delete(result));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteAllExpiredFeatureConfigurations(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureConfiguration> cq = cb.createQuery(FeatureConfiguration.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		cq.where(cb.lessThan(root.get(FeatureConfiguration.END_DATE), date));
		List<FeatureConfiguration> resultList = em.createQuery(cq).getResultList();
		resultList.forEach(result -> service.delete(result));
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
	public boolean isFeatureEnabled(FeatureType featureType) {
		return !isFeatureDisabled(featureType);
	}

	@Override
	public boolean isAnySurveillanceEnabled() {
		return isFeatureEnabled(FeatureType.CASE_SURVEILANCE)
			|| isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
			|| isFeatureEnabled(FeatureType.AGGREGATE_REPORTING);
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
	public List<FeatureType> getActiveServerFeatureTypes() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FeatureType> cq = cb.createQuery(FeatureType.class);
		Root<FeatureConfiguration> root = cq.from(FeatureConfiguration.class);

		List<FeatureType> serverFeatures = FeatureType.getAllServerFeatures();
		if (serverFeatures.isEmpty()) {
			return null;
		}

		cq.where(cb.and(root.get(FeatureConfiguration.FEATURE_TYPE).in(serverFeatures), cb.isTrue(root.get(FeatureConfiguration.ENABLED))));
		cq.select(root.get(FeatureConfiguration.FEATURE_TYPE));

		return em.createQuery(cq).getResultList();
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

		return target;
	}

	public FeatureConfiguration fromDto(@NotNull FeatureConfigurationDto source, boolean checkChangeDate) {

		FeatureConfiguration target =
			DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), FeatureConfiguration::new, checkChangeDate);

		target.setFeatureType(source.getFeatureType());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setDisease(source.getDisease());
		target.setEndDate(source.getEndDate());
		target.setEnabled(source.isEnabled());

		return target;
	}

	@LocalBean
	@Stateless
	public static class FeatureConfigurationFacadeEjbLocal extends FeatureConfigurationFacadeEjb {

	}
}
