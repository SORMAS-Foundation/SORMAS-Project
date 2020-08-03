package de.symeda.sormas.backend.common;

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

	protected String formatForLike(String textFilter1) {
		return "%" + textFilter1.toLowerCase() + "%";
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
}
