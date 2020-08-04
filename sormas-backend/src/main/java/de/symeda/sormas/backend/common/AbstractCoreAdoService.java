package de.symeda.sormas.backend.common;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractCoreAdoService<ADO extends CoreAdo> extends AbstractAdoService<ADO> {

	public static final int NR_OF_LAST_PHONE_DIGITS_TO_SEARCH = 6;

	public AbstractCoreAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	@Override
	public void delete(ADO deleteme) {

		deleteme.setDeleted(true);
		em.persist(deleteme);
		em.flush();
	}

	protected String formatForLike(String textFilter) {
		return "%" + textFilter.toLowerCase() + "%";
	}

	protected Predicate phoneNumberPredicate(CriteriaBuilder cb, Path<Object> path, String textFilter) {
		return cb.like(removeNonNumbersExpression(cb, path), formatPhoneNumberForSearch(textFilter));
	}

	protected String formatPhoneNumberForSearch(String textFilter) {
		final String formattedPhoneNumber = textFilter.replaceAll("[^0-9]", "");
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
