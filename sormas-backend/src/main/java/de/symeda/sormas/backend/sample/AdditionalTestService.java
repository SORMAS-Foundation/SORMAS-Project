package de.symeda.sormas.backend.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class AdditionalTestService extends AbstractAdoService<AdditionalTest> {

	@EJB
	private SampleService sampleService;

	public AdditionalTestService() {
		super(AdditionalTest.class);
	}

	public List<AdditionalTest> getAllActiveAdditionalTestsAfter(Date date, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdditionalTest> cq = cb.createQuery(getElementClass());
		Root<AdditionalTest> from = cq.from(getElementClass());
		Join<AdditionalTest, Sample> sample = from.join(AdditionalTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.or(cb.equal(caze.get(Case.ARCHIVED), false), cb.isNull(caze.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = cb.and(filter, dateFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(AdditionalTest.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AdditionalTest> from = cq.from(getElementClass());
		Join<AdditionalTest, Sample> sample = from.join(AdditionalTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.or(cb.equal(caze.get(Case.ARCHIVED), false), cb.isNull(caze.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(AdditionalTest.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<AdditionalTest> getAllBySample(Sample sample) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdditionalTest> cq = cb.createQuery(getElementClass());
		Root<AdditionalTest> from = cq.from(getElementClass());

		if (sample != null) {
			cq.where(cb.equal(from.get(AdditionalTest.SAMPLE), sample));
		}
		cq.orderBy(cb.desc(from.get(AdditionalTest.TEST_DATE_TIME)));

		List<AdditionalTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<AdditionalTest, AdditionalTest> additionalTestPath) {

		// whoever created the sample the additional test is associated with is allowed to access it
		Join<Sample, Sample> sampleJoin = additionalTestPath.join(AdditionalTest.SAMPLE);
		Predicate filter = sampleService.createUserFilter(cb, cq, sampleJoin);

		return filter;
	}
}
