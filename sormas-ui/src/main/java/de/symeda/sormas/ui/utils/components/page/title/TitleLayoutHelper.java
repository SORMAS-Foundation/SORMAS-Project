package de.symeda.sormas.ui.utils.components.page.title;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DateFormatHelper;

public class TitleLayoutHelper {

	private TitleLayoutHelper() {
	}

	public static StringBuilder buildPersonString(PersonReferenceDto personRefrence) {
		String personFullName = personRefrence.getCaption();
		StringBuilder personString = new StringBuilder();
		if (StringUtils.isNotBlank(personFullName)) {
			personString.append(personFullName);

			PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(personRefrence.getUuid());
			if (person.getBirthdateDD() != null && person.getBirthdateMM() != null && person.getBirthdateYYYY() != null) {
				personString.append(" (* ")
					.append(DateFormatHelper.formatDate(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY()))
					.append(")");
			}
		}
		return personString;
	}
}
