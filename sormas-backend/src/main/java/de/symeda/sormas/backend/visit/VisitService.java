package de.symeda.sormas.backend.visit;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class VisitService extends AbstractAdoService<Visit> {
	
	public VisitService() {
		super(Visit.class);
	}
	
	public List<Visit> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

		// TODO add Filter for User
		Predicate filter = null;
		
		if (date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		List<Visit> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
}
