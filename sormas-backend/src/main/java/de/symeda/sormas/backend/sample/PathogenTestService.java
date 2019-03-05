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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.sample;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PathogenTestService extends AbstractAdoService<PathogenTest> {

	@EJB
	private SampleService sampleService;

	public PathogenTestService() {
		super(PathogenTest.class);
	}

	@Override
	public void delete(PathogenTest pathogenTest) {
		Sample sample = pathogenTest.getSample();
		if (pathogenTest.equals(sample.getMainSampleTest())) {
			pathogenTest.getSample().setMainSampleTest(null);
		}

		super.delete(pathogenTest);
	}

	public List<PathogenTest> getAllActivePathogenTestsAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());
		Join<PathogenTest, Sample> sample = from.join(PathogenTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.or(cb.equal(caze.get(Case.ARCHIVED), false), cb.isNull(caze.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = cb.and(filter, dateFilter);
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
		Join<PathogenTest, Sample> sample = from.join(PathogenTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.or(cb.equal(caze.get(Case.ARCHIVED), false), cb.isNull(caze.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(PathogenTest.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<PathogenTest> getAllBySample(Sample sample) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());

		if (sample != null) {
			cq.where(cb.equal(from.get(PathogenTest.SAMPLE), sample));
		}
		cq.orderBy(cb.desc(from.get(PathogenTest.TEST_DATE_TIME)));

		List<PathogenTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<PathogenTest> getAllByCase(Case caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTest> cq = cb.createQuery(getElementClass());
		Root<PathogenTest> from = cq.from(getElementClass());

		if (caze != null) {
			Join<Object, Object> sampleJoin = from.join(PathogenTest.SAMPLE);
			cq.where(cb.equal(sampleJoin.get(Sample.ASSOCIATED_CASE), caze));
		}
		cq.orderBy(cb.desc(from.get(PathogenTest.TEST_DATE_TIME)));

		List<PathogenTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<DashboardTestResultDto> getNewTestResultsForDashboard (Region region, District district, Disease disease,
			Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardTestResultDto> cq = cb.createQuery(DashboardTestResultDto.class);
		Root<PathogenTest> pathogenTest = cq.from(getElementClass());
		Join<PathogenTest, Sample> sample = pathogenTest.join(PathogenTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, pathogenTest, user);
		Predicate dateFilter = cb.between(pathogenTest.get(PathogenTest.TEST_DATE_TIME), from, to);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (region != null) {
			Predicate regionFilter = cb.equal(caze.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		List<DashboardTestResultDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(caze.get(Case.DISEASE), pathogenTest.get(PathogenTest.TEST_RESULT));

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public Map<PathogenTestResultType, Long> getTestResultCountByResultType (Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<PathogenTest> sampleTest = cq.from(PathogenTest.class);
		Join<PathogenTest, Sample> sample = sampleTest.join(PathogenTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		
		cq.multiselect(sampleTest.get(PathogenTest.TEST_RESULT), cb.countDistinct(sampleTest));
		cq.groupBy(sampleTest.get(PathogenTest.TEST_RESULT));
		
		Predicate filter = createUserFilter(cb, cq, sampleTest, user);
		Predicate dateFilter = cb.between(sampleTest.get(PathogenTest.TEST_DATE_TIME), from, to);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (region != null) {
			Predicate regionFilter = cb.equal(caze.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}
		
		List<Object[]> results = em.createQuery(cq).getResultList();
		
		Map<PathogenTestResultType, Long> testResults = results.stream().collect(
				Collectors.toMap(e -> (PathogenTestResultType) e[0], e -> (Long) e[1]));
		
		return testResults;
	}

	public List<PathogenTestResultType> getPathogenTestResultsForCase(long caseId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathogenTestResultType> cq = cb.createQuery(PathogenTestResultType.class);
		Root<PathogenTest> root = cq.from(getElementClass());
		cq.where(cb.equal(root.get(PathogenTest.SAMPLE).get(Sample.ASSOCIATED_CASE).get(Case.ID), caseId));
		cq.select(root.get(PathogenTest.TEST_RESULT));
		List<PathogenTestResultType> result = em.createQuery(cq).getResultList();
		return result;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<PathogenTest, PathogenTest> sampleTestPath,
			User user) {
		// whoever created the sample the sample test is associated with is allowed to
		// access it
		Path<Sample> samplePath = sampleTestPath.get(PathogenTest.SAMPLE);
		@SuppressWarnings("unchecked")
		Predicate filter = sampleService.createUserFilter(cb, cq, (From<Sample, Sample>) samplePath, user);

		return filter;
	}
}
