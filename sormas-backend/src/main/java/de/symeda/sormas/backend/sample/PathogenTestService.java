/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PathogenTestService extends AbstractCoreAdoService<PathogenTest> {

	@EJB
	private SampleService sampleService;

	public PathogenTestService() {
		super(PathogenTest.class);
	}

	public List<PathogenTest> getAllActivePathogenTestsAfter(Date date, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());

		Predicate filter = createActiveTestsFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
			filter = AbstractAdoService.and(cb, filter, dateFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(PathogenTest.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PathogenTest> from = cq.from(getElementClass());

		Predicate filter = createActiveTestsFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = AbstractAdoService.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(PathogenTest.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<PathogenTest> getAllBySample(Sample sample) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		if (sample != null) {
			filter = cb.and(filter, cb.equal(from.get(PathogenTest.SAMPLE), sample));
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(PathogenTest.TEST_DATE_TIME)));

		return em.createQuery(cq).getResultList();
	}

	public boolean hasPathogenTest(Sample sample) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());

		cq.where(cb.and(createDefaultFilter(cb, from), cb.equal(from.get(PathogenTest.SAMPLE), sample)));
		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

	public List<PathogenTest> getAllByCase(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		if (caze != null) {
			Join<Object, Object> sampleJoin = from.join(PathogenTest.SAMPLE);
			filter = cb.and(filter, cb.equal(sampleJoin.get(Sample.ASSOCIATED_CASE), caze));
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(PathogenTest.TEST_DATE_TIME)));

		return em.createQuery(cq).getResultList();
	}

	public List<PathogenTest> getBySampleUuids(List<String> sampleUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(PathogenTest.class);
		Root<PathogenTest> pathogenTestRoot = cq.from(PathogenTest.class);
		Join<PathogenTest, Sample> sampleJoin = pathogenTestRoot.join(PathogenTest.SAMPLE, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, pathogenTestRoot), sampleJoin.get(AbstractDomainObject.UUID).in(sampleUuids));

		cq.where(filter);
		return em.createQuery(cq).getResultList();
	}

	public List<String> getDeletedUuidsSince(User user, Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PathogenTest> pathogenTest = cq.from(PathogenTest.class);

		Predicate filter = createUserFilter(cb, cq, pathogenTest);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(pathogenTest.get(PathogenTest.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(pathogenTest.get(PathogenTest.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(pathogenTest.get(PathogenTest.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<PathogenTestResultType> getPathogenTestResultsForCase(long caseId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTestResultType> cq = cb.createQuery(PathogenTestResultType.class);
		Root<PathogenTest> root = cq.from(getElementClass());
		cq.where(cb.and(createDefaultFilter(cb, root), cb.equal(root.get(PathogenTest.SAMPLE).get(Sample.ASSOCIATED_CASE).get(Case.ID), caseId)));
		cq.select(root.get(PathogenTest.TEST_RESULT));
		List<PathogenTestResultType> result = em.createQuery(cq).getResultList();
		return result;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<PathogenTest, PathogenTest> sampleTestPath) {

		// whoever created the sample the sample test is associated with is allowed to
		// access it
		Join<Sample, Sample> samplePath = sampleTestPath.join(PathogenTest.SAMPLE);
		Predicate filter = sampleService.createUserFilter(cb, cq, samplePath);

		return filter;
	}

	@Override
	public void delete(PathogenTest pathogenTest) {
		super.delete(pathogenTest);
	}

	/**
	 * Creates a filter that excludes all pathogen tests that are either {@link CoreAdo#deleted} or associated with
	 * samples whose case is {@link Case#archived}.
	 */
	public Predicate createActiveTestsFilter(CriteriaBuilder cb, Root<PathogenTest> root) {

		Join<PathogenTest, Sample> sample = root.join(PathogenTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		return cb.and(cb.isFalse(caze.get(Case.ARCHIVED)), cb.isFalse(root.get(PathogenTest.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries in this service..
	 * This essentially removes {@link CoreAdo#deleted} pathogen tests from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<PathogenTest> root) {
		return cb.isFalse(root.get(PathogenTest.DELETED));
	}
}
