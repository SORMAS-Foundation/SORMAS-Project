package de.symeda.sormas.backend.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleService extends AbstractAdoService<Sample> {
	
	@EJB
	private CaseService caseService;	
	
	public SampleService() {
		super(Sample.class);
	}
	
	public List<Sample> getAllAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		
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
		
		cq.orderBy(cb.desc(from.get(Sample.REPORT_DATE_TIME)));
		
		List<Sample> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<Sample> getAllByCase(Case caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		
		if(caze != null) {
			cq.where(cb.equal(from.get(Sample.ASSOCIATED_CASE), caze));
		}
		cq.orderBy(cb.desc(from.get(Sample.REPORT_DATE_TIME)));
		
		List<Sample> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	/**
	 * Returns the sample that refers to the sample identified by the sampleUuid.
	 * 
	 * @param sampleUuid The UUID of the sample to get the referral for.
	 * @return The sample that refers to this sample, or null if none is found.
	 */
	public Sample getReferredFrom(String sampleUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Sample.REFERRED_TO), getByUuid(sampleUuid)));
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Sample,Sample> samplePath, User user) {
		// whoever created the case the sample is associated with or is assigned to it
		// is allowed to access it
		Path<Case> casePath = samplePath.get(Sample.ASSOCIATED_CASE);
		Predicate filter = caseService.createUserFilter(cb, cq, (From<Case,Case>)casePath, user);
		
		// user that reported it is not able to access it. Otherwise they would also need to access the case
		//filter = cb.or(filter, cb.equal(samplePath.get(Sample.REPORTING_USER), user));
		
		// lab users can see samples assigned to their laboratory
		if(user.getUserRoles().contains(UserRole.LAB_USER)) {
			if(user.getLaboratory() != null) {
				filter = cb.or(filter, cb.equal(samplePath.get(Sample.LAB), user.getLaboratory()));
			}
		}
		
		return filter;
	}
		
}
