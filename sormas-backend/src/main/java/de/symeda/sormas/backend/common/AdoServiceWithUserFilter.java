package de.symeda.sormas.backend.common;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.user.User;

public abstract class AdoServiceWithUserFilter<ADO extends AbstractDomainObject> extends BaseAdoService<ADO> {

	public AdoServiceWithUserFilter(Class<ADO> elementClass) {
		super(elementClass);
	}

	/**
	 * Used by most getAll* and getAllUuids methods to filter by user
	 */
	@SuppressWarnings("rawtypes")
	public abstract Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ADO> from);

	public List<ADO> getAllAfter(Date since, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> root = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, root);
		if (since != null) {
			Predicate dateFilter = createChangeDateFilter(cb, root, since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(root.get(AbstractDomainObject.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ADO> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(from.get(AbstractDomainObject.UUID));
		return em.createQuery(cq).getResultList();
	}

	public List<Long> getAllIds(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());

		if (user != null) {
			Predicate filter = createUserFilter(cb, cq, from);
			if (filter != null) {
				cq.where(filter);
			}
		}

		cq.select(from.get(AbstractDomainObject.ID));
		return em.createQuery(cq).getResultList();
	}
}
