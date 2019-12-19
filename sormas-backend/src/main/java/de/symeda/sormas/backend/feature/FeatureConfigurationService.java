package de.symeda.sormas.backend.feature;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class FeatureConfigurationService extends AbstractAdoService<FeatureConfiguration> {

	public FeatureConfigurationService() {
		super(FeatureConfiguration.class);
	}

	public List<String> getDeletedUuids(Date since, User user) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT ").append(AbstractDomainObject.UUID).append(" FROM ").append(FeatureConfiguration.TABLE_NAME)
		.append(AbstractDomainObject.HISTORY_TABLE_SUFFIX).append(" h WHERE sys_period @> CAST (?1 AS timestamptz) ");
		
		if (user.getRegion() != null) {
			queryBuilder.append(" AND h.").append(FeatureConfiguration.REGION).append("_id = ").append(user.getRegion().getId()).append(" ");
		}
		if (user.getDistrict() != null) {
			queryBuilder.append(" AND h.").append(FeatureConfiguration.DISTRICT).append("_id = ").append(user.getDistrict().getId()).append(" ");
		}
		
		queryBuilder.append(" AND NOT EXISTS (SELECT FROM ").append(FeatureConfiguration.TABLE_NAME).append(" WHERE ")
		.append(AbstractDomainObject.ID).append(" = h.").append(AbstractDomainObject.ID).append(")");
		Query nativeQuery = em.createNativeQuery(queryBuilder.toString());
		nativeQuery.setParameter(1, since);
		@SuppressWarnings("unchecked")
		List<String> results = (List<String>) nativeQuery.getResultList();
		return results;
	}

	public Predicate createCriteriaFilter(FeatureConfigurationCriteria criteria, CriteriaBuilder cb, CriteriaQuery<?> cq, From<FeatureConfiguration, FeatureConfiguration> from) {
		Predicate filter = null;
		if (criteria.getFeatureType() != null) {
			filter = and(cb, filter, cb.equal(from.get(FeatureConfiguration.FEATURE_TYPE), criteria.getFeatureType()));
		}
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(FeatureConfiguration.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(FeatureConfiguration.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getDisease() != null) {
			filter = and(cb, filter, cb.equal(from.get(FeatureConfiguration.DISEASE), criteria.getDisease()));
		}
		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<FeatureConfiguration, FeatureConfiguration> from, User user) {
		if (user == null) {
			return null;
		}

		Predicate filter = null;
		if (user.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.get(FeatureConfiguration.REGION), user.getRegion()));
		}
		if (user.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.get(FeatureConfiguration.DISTRICT), user.getDistrict()));
		}

		return filter;
	}

}
