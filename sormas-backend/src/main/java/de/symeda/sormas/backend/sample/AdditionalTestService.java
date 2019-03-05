package de.symeda.sormas.backend.sample;

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
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class AdditionalTestService extends AbstractAdoService<AdditionalTest> {

	@EJB
	private SampleService sampleService;
	
	public AdditionalTestService() {
		super(AdditionalTest.class);
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
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<AdditionalTest, AdditionalTest> additionalTestPath,
			User user) {
		// whoever created the sample the sample test is associated with is allowed to
		// access it
		Path<Sample> samplePath = additionalTestPath.get(AdditionalTest.SAMPLE);
		@SuppressWarnings("unchecked")
		Predicate filter = sampleService.createUserFilter(cb, cq, (From<Sample, Sample>) samplePath, user);

		return filter;
	}
	
}
