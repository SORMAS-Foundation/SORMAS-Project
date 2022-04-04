package de.symeda.sormas.backend.labmessage;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.DeletableAdo;

@Stateless
@LocalBean
public class TestReportService extends BaseAdoService<TestReport> {

	public TestReportService() {
		super(TestReport.class);
	}

	public List<TestReport> getByLabMessage(LabMessage labMessage) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestReport> cq = cb.createQuery(TestReport.class);
		Root<TestReport> root = cq.from(TestReport.class);

		cq.where(cb.equal(root.get(TestReport.LAB_MESSAGE), labMessage));
		cq.distinct(true);
		cq.orderBy(cb.desc(root.get(AbstractDomainObject.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Creates a default filter that should be used as the basis of queries in this service..
	 * This essentially removes {@link DeletableAdo#deleted} test reports from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<TestReport> root) {
		return cb.isFalse(root.join(TestReport.LAB_MESSAGE, JoinType.LEFT).get(DeletableAdo.DELETED));
	}

}
