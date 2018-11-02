package de.symeda.sormas.backend.sample;

import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleTestService extends AbstractAdoService<SampleTest> {

	@EJB
	private SampleService sampleService;
	
	public SampleTestService() {
		super(SampleTest.class);
	}
	
	@Override
	public void delete(SampleTest sampleTest) {
		Sample sample = sampleTest.getSample();
		if (sampleTest.equals(sample.getMainSampleTest())) {
			sampleTest.getSample().setMainSampleTest(null);
		}
		
		super.delete(sampleTest);
	}
	
	public List<SampleTest> getAllActiveSampleTestsAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleTest> cq = cb.createQuery(getElementClass());
		Root<SampleTest> from = cq.from(getElementClass());
		Join<SampleTest, Sample> sample = from.join(SampleTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		
		Predicate filter = cb.or(
				cb.equal(caze.get(Case.ARCHIVED), false),
				cb.isNull(caze.get(Case.ARCHIVED)));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createDateFilter(cb, cq, from, date);
			filter = cb.and(filter, dateFilter);		
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(SampleTest.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}
	
	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<SampleTest> from = cq.from(getElementClass());
		Join<SampleTest, Sample> sample = from.join(SampleTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.or(
				cb.equal(caze.get(Case.ARCHIVED), false),
				cb.isNull(caze.get(Case.ARCHIVED)));
		
		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			filter = cb.and(filter, userFilter);
		}
		
		cq.where(filter);
		cq.select(from.get(SampleTest.UUID));
		
		return em.createQuery(cq).getResultList();
	}
	
	public List<SampleTest> getAllBySample(Sample sample) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleTest> cq = cb.createQuery(getElementClass());
		Root<SampleTest> from = cq.from(getElementClass());
		
		if (sample != null) {
			cq.where(cb.equal(from.get(SampleTest.SAMPLE), sample));
		}
		cq.orderBy(cb.desc(from.get(SampleTest.TEST_DATE_TIME)));
		
		List<SampleTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<SampleTest> getAllByCase(Case caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleTest> cq = cb.createQuery(getElementClass());
		Root<SampleTest> from = cq.from(getElementClass());
		
		if (caze != null) {
			Join<Object, Object> sampleJoin = from.join(SampleTest.SAMPLE);
			cq.where(cb.equal(sampleJoin.get(Sample.ASSOCIATED_CASE), caze));
		}
		cq.orderBy(cb.desc(from.get(SampleTest.TEST_DATE_TIME)));
		
		List<SampleTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<DashboardTestResultDto> getNewTestResultsForDashboard(Region region, District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardTestResultDto> cq = cb.createQuery(DashboardTestResultDto.class);
		Root<SampleTest> sampleTest = cq.from(getElementClass());
		Join<SampleTest, Sample> sample = sampleTest.join(SampleTest.SAMPLE, JoinType.LEFT);
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		
		Predicate filter = createUserFilter(cb, cq, sampleTest, user);
		Predicate dateFilter = cb.between(sampleTest.get(SampleTest.TEST_DATE_TIME), from, to);
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
			cq.multiselect(
					caze.get(Case.DISEASE),
					sampleTest.get(SampleTest.TEST_RESULT)
			);
			
			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}
		
		return result;
	}


	@SuppressWarnings("unchecked")
	public List<SampleTestResultType> getSampleTestResultsForCase(long caseId) {
		return (List<SampleTestResultType>) em.createNativeQuery("SELECT " + SampleTest.TEST_RESULT + " FROM " + SampleTest.TABLE_NAME + " WHERE "
				+ SampleTest.SAMPLE + "_id IN (SELECT " + Sample.ID + " FROM " + Sample.TABLE_NAME + " WHERE "
				+ Sample.ASSOCIATED_CASE + "_id = " + caseId + ");").getResultList().stream()
				.map(e -> SampleTestResultType.valueOf((String) e))
				.collect(Collectors.toList());
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<SampleTest,SampleTest> sampleTestPath, User user) {
		// whoever created the sample the sample test is associated with is allowed to access it
		Path<Sample> samplePath = sampleTestPath.get(SampleTest.SAMPLE);
		@SuppressWarnings("unchecked")
		Predicate filter = sampleService.createUserFilter(cb, cq, (From<Sample,Sample>)samplePath, user);
	
		return filter;
	}
}
