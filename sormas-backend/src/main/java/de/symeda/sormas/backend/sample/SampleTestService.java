package de.symeda.sormas.backend.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleTestService extends AbstractAdoService<SampleTest> {

	@EJB
	private SampleService sampleService;
	
	public SampleTestService() {
		super(SampleTest.class);
	}
	
	public List<SampleTest> getAllAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleTest> cq = cb.createQuery(getElementClass());
		Root<SampleTest> from = cq.from(getElementClass());
		
		Predicate filter = createUserFilter(cb, cq, from, user);
		
		if(date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			if(filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}
		
		if(filter != null) {
			cq.where(filter);
		}
		
		cq.orderBy(cb.desc(from.get(SampleTest.TEST_DATE_TIME)));
		
		List<SampleTest> resultList = em.createQuery(cq).getResultList();
		return resultList;
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
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<SampleTest,SampleTest> sampleTestPath, User user) {
		// whoever created the sample the sample test is associated with is allowed to access it
		Path<Sample> samplePath = sampleTestPath.get(SampleTest.SAMPLE);
		@SuppressWarnings("unchecked")
		Predicate filter = sampleService.createUserFilter(cb, cq, (From<Sample,Sample>)samplePath, user);
	
		return filter;
	}
	
}
