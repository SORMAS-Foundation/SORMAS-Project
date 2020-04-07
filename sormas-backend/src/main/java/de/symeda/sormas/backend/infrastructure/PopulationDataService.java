package de.symeda.sormas.backend.infrastructure;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Stateless
@LocalBean
public class PopulationDataService extends AbstractAdoService<PopulationData> {

	public PopulationDataService() {
		super(PopulationData.class);
	}

	public Predicate buildCriteriaFilter(PopulationDataCriteria criteria, CriteriaBuilder cb, From<PopulationData, PopulationData> from) {
		Predicate filter = null;
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(PopulationData.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.isDistrictIsNull()) {
			filter = and(cb, filter, cb.isNull(from.get(PopulationData.DISTRICT)));
		} else if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(PopulationData.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.isAgeGroupIsNull()) {
			filter = and(cb, filter, cb.isNull(from.get(PopulationData.AGE_GROUP)));
		} else if (criteria.getAgeGroup() != null) {
			filter = and(cb, filter, cb.equal(from.get(PopulationData.AGE_GROUP), criteria.getAgeGroup()));
		}
		if (criteria.isSexIsNull()) {
			filter = and(cb, filter, cb.isNull(from.get(PopulationData.SEX)));
		} else if (criteria.getSex() != null) {
			filter = and(cb, filter, cb.equal(from.get(PopulationData.SEX), criteria.getSex()));
		}

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<PopulationData, PopulationData> from) {
		return null;
	}

}