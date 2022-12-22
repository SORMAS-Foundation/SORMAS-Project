package de.symeda.sormas.backend.caze.porthealthinfo;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;

@Stateless
@LocalBean
public class PortHealthInfoService extends AdoServiceWithUserFilterAndJurisdiction<PortHealthInfo> {

	public PortHealthInfoService() {
		super(PortHealthInfo.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, PortHealthInfo> from) {
		return null;
	}
}
