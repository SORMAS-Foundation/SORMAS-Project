package de.symeda.sormas.ui.utils.components.page.title;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateFormatHelper;

public class TitleLayoutHelper {

	private TitleLayoutHelper() {
	}

	public static StringBuilder buildPersonString(PersonDto person) {
		StringBuilder personString = new StringBuilder();
		String personFullName = PersonDto.buildCaption(person.getFirstName(), person.getLastName());
		if (StringUtils.isNotBlank(personFullName)) {
			personString.append(personFullName);

			String dateOfBirth = DateFormatHelper.formatDate(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY());
			if (StringUtils.isNotBlank(dateOfBirth)) {
				personString.append(" (* ").append(dateOfBirth).append(")");
			}
		}
		return personString;
	}
}
