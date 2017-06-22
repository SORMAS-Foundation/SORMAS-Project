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
		
		Predicate filter = createUserFilter(cb, from, user);
		
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
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, From<Sample,Sample> samplePath, User user) {
		// whoever created the case the sample is associated with or is assigned to it
		// is allowed to access it
		Path<Case> casePath = samplePath.get(Sample.ASSOCIATED_CASE);
		@SuppressWarnings("unchecked")
		Predicate filter = caseService.createUserFilter(cb, (From<Case,Case>)casePath, user);
		
		// whoever created the sample is allowed to access it
		filter = cb.or(filter, cb.equal(samplePath.get(Sample.REPORTING_USER), user));
		
		// lab users can see samples assigned to their laboratory
		if(user.getUserRoles().contains(UserRole.LAB_USER)) {
			if(user.getLaboratory() != null) {
				filter = cb.or(filter, cb.equal(samplePath.get(Sample.LAB), user.getLaboratory()));
				filter = cb.or(filter, cb.equal(samplePath.get(Sample.OTHER_LAB), user.getLaboratory()));
			}
		}	
	
		return filter;
	}
		
}
