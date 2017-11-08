package de.symeda.sormas.backend.sample;

import java.util.Collections;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.DashboardTestResult;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleTestService extends AbstractAdoService<SampleTest> {

	@EJB
	private SampleService sampleService;
	
	public SampleTestService() {
		super(SampleTest.class);
	}
	
	public List<SampleTest> getAllBySample(Sample sample) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleTest> cq = cb.createQuery(getElementClass());
		Root<SampleTest> from = cq.from(getElementClass());
		
		if(sample != null) {
			cq.where(cb.equal(from.get(SampleTest.SAMPLE), sample));
		}
		cq.orderBy(cb.desc(from.get(SampleTest.TEST_DATE_TIME)));
		
		List<SampleTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<DashboardTestResult> getNewTestResultsForDashboard(District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardTestResult> cq = cb.createQuery(DashboardTestResult.class);
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
		
		List<DashboardTestResult> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					caze.get(Case.DISEASE),
					sample.get(Sample.SHIPPED),
					sample.get(Sample.RECEIVED),
					sampleTest.get(SampleTest.TEST_RESULT)
			);
			
			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}
		
		return result;
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */	
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<SampleTest,SampleTest> sampleTestPath, User user) {
		// whoever created the sample the sample test is associated with is allowed to access it
		Path<Sample> samplePath = sampleTestPath.get(SampleTest.SAMPLE);
		@SuppressWarnings("unchecked")
		Predicate filter = sampleService.createUserFilter(cb, cq, (From<Sample,Sample>)samplePath, user);
	
		return filter;
	}
}
