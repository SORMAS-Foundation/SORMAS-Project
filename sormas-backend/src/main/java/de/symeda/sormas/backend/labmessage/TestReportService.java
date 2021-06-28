package de.symeda.sormas.backend.labmessage;

import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class TestReportService extends BaseAdoService<TestReport> {

	public TestReportService() {
		super(TestReport.class);
	}

	/**
	 * Creates a default filter that should be used as the basis of queries in this service..
	 * This essentially removes {@link CoreAdo#deleted} pathogen tests from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<TestReport> root) {
		return cb.isFalse(root.get(TestReport.DELETED));
	}

	public List<TestReport> getByPathogenTestUuids(List<String> pathogenTestUuids, boolean ordered) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestReport> cq = cb.createQuery(TestReport.class);
		Root<TestReport> testReportRoot = cq.from(TestReport.class);
		Join<TestReport, PathogenTest> testReportJoin = testReportRoot.join(TestReport.PATHOGEN_TEST, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, testReportRoot), testReportJoin.get(AbstractDomainObject.UUID).in(pathogenTestUuids));

		cq.where(filter);

		if (ordered) {
			cq.orderBy(cb.desc(testReportRoot.get(TestReport.CREATION_DATE)));
		}

		return em.createQuery(cq).getResultList();
	}

	public List<TestReport> getByPathogenTestUuid(String uuid, boolean ordered) {
		ArrayList uuidList = new ArrayList();
		uuidList.add(uuid);
		return getByPathogenTestUuids(uuidList, ordered);
	}

}
