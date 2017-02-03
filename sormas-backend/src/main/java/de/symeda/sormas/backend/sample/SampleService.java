package de.symeda.sormas.backend.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleService extends AbstractAdoService<Sample> {

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
		
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CREATION_DATE)));
		
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
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CREATION_DATE)));
		
		List<Sample> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public Predicate createUserFilter(CriteriaBuilder cb, Path<Sample> samplePath, User user) {
		// whoever created the event or is assigned to it is allowed to access it
		Predicate filter = cb.equal(samplePath.get(Sample.REPORTING_USER), user);
				
		// TODO: define which samples can be seen
	
		return filter;
	}
		
}
