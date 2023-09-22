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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.sample.PathogenTestCriteria;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDeletableAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleJoins;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleQueryContext;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleService;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class PathogenTestService extends AbstractDeletableAdoService<PathogenTest> {

	@EJB
	private SampleService sampleService;
	@EJB
	private EnvironmentSampleService environmentSampleService;

	public PathogenTestService() {
		super(PathogenTest.class, DeletableEntityType.PATHOGEN_TEST);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, PathogenTest> from) {

		Predicate filter = createActiveTestsFilter(cb, cq, from);

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}

		return filter;
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PathogenTest> from = cq.from(getElementClass());

		Predicate filter = createActiveTestsFilter(cb, cq, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(PathogenTest.UUID));

		final List<String> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<PathogenTest> getIndexList(PathogenTestCriteria pathogenTestCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());
		Predicate filter = null;
		if (pathogenTestCriteria != null) {
			filter = buildCriteriaFilter(pathogenTestCriteria, cb, cq, from);
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(from.get(PathogenTest.CHANGE_DATE)));
		cq.distinct(true);

		List<Order> order = new ArrayList<>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case PathogenTest.UUID:
				case PathogenTest.SAMPLE:
				case PathogenTest.TEST_DATE_TIME:
					expression = from.get(sortProperty.propertyName);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		}
		order.add(cb.desc(from.get(PathogenTest.UUID)));
		cq.orderBy(order);

		return QueryHelper.getResultList(em, cq, first, max);
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
		return QueryHelper.getFirstResult(em, cq) != null;
	}

	public List<PathogenTest> getAllByCase(String caseUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		Join<Object, Object> sampleJoin = from.join(PathogenTest.SAMPLE);
		filter = cb.and(filter, cb.equal(sampleJoin.join(Sample.ASSOCIATED_CASE, JoinType.LEFT).get(Case.UUID), caseUuid));

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(PathogenTest.TEST_DATE_TIME)));

		return em.createQuery(cq).getResultList();
	}

	public Long countByCase(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PathogenTest> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		if (caze != null) {
			Join<Object, Object> sampleJoin = from.join(PathogenTest.SAMPLE);
			filter = cb.and(filter, cb.equal(sampleJoin.get(Sample.ASSOCIATED_CASE), caze));
		}
		cq.where(filter);

		cq.select(cb.count(from.get(PathogenTest.ID)));

		return em.createQuery(cq).getSingleResult();
	}

	public long count(PathogenTestCriteria pathogenTestCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PathogenTest> pathogenTestRoot = cq.from(PathogenTest.class);

		Predicate filter = createDefaultFilter(cb, pathogenTestRoot);

		if (pathogenTestCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(pathogenTestCriteria, cb, cq, pathogenTestRoot);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(pathogenTestRoot));
		return em.createQuery(cq).getSingleResult();
	}

	public List<PathogenTest> getBySampleUuids(List<String> sampleUuids, boolean ordered) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(PathogenTest.class);
		Root<PathogenTest> pathogenTestRoot = cq.from(PathogenTest.class);
		Join<PathogenTest, Sample> sampleJoin = pathogenTestRoot.join(PathogenTest.SAMPLE, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, pathogenTestRoot), sampleJoin.get(AbstractDomainObject.UUID).in(sampleUuids));

		cq.where(filter);

		if (ordered) {
			cq.orderBy(cb.desc(pathogenTestRoot.get(PathogenTest.CREATION_DATE)));
		}

		return em.createQuery(cq).getResultList();
	}

	public List<PathogenTest> getBySampleUuid(String sampleUuid, boolean ordered) {
		return getBySampleUuids(Collections.singletonList(sampleUuid), ordered);
	}

	@SuppressWarnings("rawtypes")
	private Predicate buildCriteriaFilter(PathogenTestCriteria pathogenTestCriteria, CriteriaBuilder cb, CriteriaQuery cq, Root<PathogenTest> from) {

		Predicate filter = createActiveTestsFilter(cb, cq, from);

		if (pathogenTestCriteria.getSample() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(PathogenTest.SAMPLE).get(Sample.UUID), pathogenTestCriteria.getSample().getUuid()));
		}

		return filter;
	}

	public List<String> getDeletedUuidsSince(Date since) {

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

		final List<String> resultList = em.createQuery(cq).getResultList();
		return resultList;
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
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, PathogenTest> sampleTestPath) {

		// whoever created the sample the sample test is associated with is allowed to
		// access it
		Join<PathogenTest, Sample> samplePath = sampleTestPath.join(PathogenTest.SAMPLE, JoinType.LEFT);
		Predicate sampleUserfilter = sampleService.createUserFilter(new SampleQueryContext(cb, cq, samplePath), null);

		Join<PathogenTest, EnvironmentSample> environmentSampleJoin = sampleTestPath.join(PathogenTest.ENVIRONMENT_SAMPLE, JoinType.LEFT);
		Predicate environmentSampleUserFilter = environmentSampleService.createUserFilter(cb, cq, environmentSampleJoin);

		return CriteriaBuilderHelper.or(cb, sampleUserfilter, environmentSampleUserFilter);
	}

	@Override
	public void delete(PathogenTest pathogenTest, DeletionDetails deletionDetails) {
		super.delete(pathogenTest, deletionDetails);
	}

	/**
	 * Creates a filter that excludes all pathogen tests that are {@link DeletableAdo#deleted} or associated with
	 * cases that are {@link Case#archived}, contacts that are {@link Contact#deleted}. or event participants that are
	 * {@link EventParticipant#deleted}
	 */
	@SuppressWarnings("rawtypes")
	private Predicate createActiveTestsFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, PathogenTest> root) {

		Join<PathogenTest, Sample> sample = root.join(PathogenTest.SAMPLE, JoinType.LEFT);
		Join<PathogenTest, EnvironmentSample> environmentSample = root.join(PathogenTest.ENVIRONMENT_SAMPLE, JoinType.LEFT);

		Predicate activeEnvironmentSamplesFilter = environmentSampleService.createActiveEnvironmentSamplesFilter(
			new EnvironmentSampleQueryContext(cb, cq, environmentSample, new EnvironmentSampleJoins(environmentSample)));

		final Predicate activeSamplesFilter = sampleService.createActiveSamplesFilter(new SampleQueryContext(cb, cq, sample));

		return cb.or(activeEnvironmentSamplesFilter, activeSamplesFilter);
	}

	/**
	 * Creates a default filter that should be used as the basis of queries in this service..
	 * This essentially removes {@link DeletableAdo#deleted} pathogen tests from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<PathogenTest> root) {
		return cb.isFalse(root.get(PathogenTest.DELETED));
	}

	/**
	 * @param pathogenTestUuids
	 *            {@link PathogenTest}s identified by {@code uuid} to be deleted.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(List<String> pathogenTestUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<PathogenTest> cu = cb.createCriteriaUpdate(PathogenTest.class);
		Root<PathogenTest> root = cu.from(PathogenTest.class);

		cu.set(PathogenTest.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(PathogenTest.DELETED), true);

		cu.where(root.get(PathogenTest.UUID).in(pathogenTestUuids));

		em.createQuery(cu).executeUpdate();
	}

	@Override
	public boolean inJurisdictionOrOwned(PathogenTest entity) {
		return fulfillsCondition(entity, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	@Override
	public List<Long> getInJurisdictionIds(List<PathogenTest> entities) {
		return getIdList(entities, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, PathogenTest> from) {

		return sampleService.inJurisdictionOrOwned(new SampleQueryContext(cb, query, from.join(PathogenTest.SAMPLE)));
	}
}
