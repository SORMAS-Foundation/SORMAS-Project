package de.symeda.sormas.backend.caze.porthealthinfo;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class PortHealthInfoService extends AdoServiceWithUserFilterAndJurisdiction<PortHealthInfo> {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public PortHealthInfoService() {
		super(PortHealthInfo.class);
	}

	public PortHealthInfo getByCaseUuid(String caseUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PortHealthInfo> cq = cb.createQuery(PortHealthInfo.class);
		Root<Case> root = cq.from(Case.class);
		Join<Case, PortHealthInfo> portHealthJoin = root.join(Case.PORT_HEALTH_INFO, JoinType.LEFT);

		cq.select(portHealthJoin);
		cq.where(cb.equal(root.get(Case.UUID), caseUuid));
		return QueryHelper.getSingleResult(em, cq);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, PortHealthInfo> from) {
		return null;
	}
}
