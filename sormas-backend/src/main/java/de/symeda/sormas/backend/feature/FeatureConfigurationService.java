package de.symeda.sormas.backend.feature;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class FeatureConfigurationService extends AdoServiceWithUserFilter<FeatureConfiguration> {

	public FeatureConfigurationService() {
		super(FeatureConfiguration.class);
	}

	public List<String> getDeletedUuids(Date since, User user) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT ")
			.append(AbstractDomainObject.UUID)
			.append(" FROM ")
			.append(FeatureConfiguration.TABLE_NAME)
			.append(AbstractDomainObject.HISTORY_TABLE_SUFFIX)
			.append(" h WHERE sys_period @> CAST (?1 AS timestamptz) ");

		if (user.getRegion() != null) {
			queryBuilder.append(" AND h.").append(FeatureConfiguration.REGION).append("_id = ").append(user.getRegion().getId()).append(" ");
		}
		if (user.getDistrict() != null) {
			queryBuilder.append(" AND h.").append(FeatureConfiguration.DISTRICT).append("_id = ").append(user.getDistrict().getId()).append(" ");
		}

		queryBuilder.append(" AND NOT EXISTS (SELECT FROM ")
			.append(FeatureConfiguration.TABLE_NAME)
			.append(" WHERE ")
			.append(AbstractDomainObject.ID)
			.append(" = h.")
			.append(AbstractDomainObject.ID)
			.append(")");
		Query nativeQuery = em.createNativeQuery(queryBuilder.toString());
		nativeQuery.setParameter(1, since);
		@SuppressWarnings("unchecked")
		List<String> results = (List<String>) nativeQuery.getResultList();
		return results;
	}

	public Predicate createCriteriaFilter(
		FeatureConfigurationCriteria criteria,
		CriteriaBuilder cb,
		CriteriaQuery<?> cq,
		From<FeatureConfiguration, FeatureConfiguration> from) {

		Predicate filter = null;
		if (ArrayUtils.isNotEmpty(criteria.getFeatureTypes())) {
			filter = CriteriaBuilderHelper.and(cb, filter, from.get(FeatureConfiguration.FEATURE_TYPE).in(criteria.getFeatureTypes()));
		}
		if (criteria.getRegion() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.join(FeatureConfiguration.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(FeatureConfiguration.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(FeatureConfiguration.DISEASE), criteria.getDisease()));
		}
		if (criteria.getEnabled() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(FeatureConfiguration.ENABLED), criteria.getEnabled()));
		}
		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, FeatureConfiguration> from) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		Predicate filter = null;
		if (currentUser.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(cb.isNull(from.get(FeatureConfiguration.REGION)), cb.equal(from.get(FeatureConfiguration.REGION), currentUser.getRegion())));
		}
		if (currentUser.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.isNull(from.get(FeatureConfiguration.DISTRICT)),
					cb.equal(from.get(FeatureConfiguration.DISTRICT), currentUser.getDistrict())));
		}

		return filter;
	}

	public void createMissingFeatureConfigurations() {

		List<FeatureConfiguration> featureConfigurations = getAll();
		Map<FeatureType, FeatureConfiguration> existingListOfConfigurations =
			featureConfigurations.stream().collect(Collectors.toMap(FeatureConfiguration::getFeatureType, Function.identity()));

		FeatureType.getAllServerFeatures().forEach(featureType -> {
			FeatureConfiguration savedConfiguration = existingListOfConfigurations.get(featureType);
			if (savedConfiguration == null) {
				FeatureConfiguration configuration = FeatureConfiguration.build(featureType, featureType.isEnabledDefault());
				ensurePersisted(configuration);
			}
		});
	}

	public void updateFeatureConfigurations() {

		List<FeatureConfiguration> featureConfigurations = getAll();
		Map<FeatureType, FeatureConfiguration> featureConfigurationMap =
			featureConfigurations.stream().collect(Collectors.toMap(FeatureConfiguration::getFeatureType, Function.identity()));

		FeatureType.getAllServerFeatures().forEach(featureType -> {
			if (featureType.isDependent()) {
				boolean hasEnabledDependentFeature = hasEnabledDependentFeature(featureType, featureConfigurationMap);

				if (!hasEnabledDependentFeature) {
					FeatureConfiguration configuration = featureConfigurationMap.get(featureType);
					configuration.setEnabled(false);
					ensurePersisted(configuration);
				}
			}
		});
	}

	private boolean hasEnabledDependentFeature(FeatureType featureType, Map<FeatureType, FeatureConfiguration> featureConfigurationMap) {

		for (FeatureType dependentFeatureType : featureType.getDependentFeatures()) {
			if (dependentFeatureType.isDependent()) {
				return hasEnabledDependentFeature(dependentFeatureType, featureConfigurationMap);
			} else if (featureConfigurationMap.get(dependentFeatureType).isEnabled()) {
				return true;
			}
		}

		return false;
	}
}
