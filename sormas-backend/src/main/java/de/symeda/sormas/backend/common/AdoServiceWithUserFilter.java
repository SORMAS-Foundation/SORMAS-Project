package de.symeda.sormas.backend.common;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.backend.user.User;

public abstract class AdoServiceWithUserFilter<ADO extends AbstractDomainObject> extends BaseAdoService<ADO> {

	public static final int NR_OF_LAST_PHONE_DIGITS_TO_SEARCH = 6;

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

	protected String formatForLike(String textFilter) {
		return "%" + textFilter.toLowerCase() + "%";
	}

	protected Predicate phoneNumberPredicate(CriteriaBuilder cb, Path<Object> path, String textFilter) {
		return cb.like(removeNonNumbersExpression(cb, path), formatPhoneNumberForSearch(textFilter));
	}

	protected String formatPhoneNumberForSearch(String textFilter) {
		final String formattedPhoneNumber = textFilter.replaceAll("[^0-9a-zA-Z]", "");
		if (StringUtils.isEmpty(formattedPhoneNumber)) {
			return textFilter;
		}
		final int phoneNrLength = formattedPhoneNumber.length();
		return formatForLike(
			phoneNrLength >= NR_OF_LAST_PHONE_DIGITS_TO_SEARCH
				? formattedPhoneNumber.substring(phoneNrLength - NR_OF_LAST_PHONE_DIGITS_TO_SEARCH, phoneNrLength)
				: formattedPhoneNumber);
	}

	protected Expression<String> removeNonNumbersExpression(CriteriaBuilder cb, Path<Object> path) {
		return cb.function("REGEXP_REPLACE", String.class, path, cb.literal("[^0-9]"), cb.literal(""), cb.literal("g"));
	}
}
