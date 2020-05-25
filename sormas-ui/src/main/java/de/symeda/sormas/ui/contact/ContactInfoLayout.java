/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class ContactInfoLayout extends HorizontalLayout {

	private final ContactDto contactDto;

	public ContactInfoLayout(ContactDto contactDto) {
		this.contactDto = contactDto;
		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);
		updateContactInfo();
	}

	private void updateContactInfo() {
		this.removeAllComponents();

		final PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(contactDto.getPerson().getUuid());

		final VerticalLayout firstColumn = new VerticalLayout();
		firstColumn.setMargin(false);
		firstColumn.setSpacing(true);

		{
			addDescLabel(firstColumn, DataHelper.getShortUuid(contactDto.getUuid()),
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.UUID))
			.setDescription(contactDto.getUuid());

			addDescLabel(firstColumn, contactDto.getPerson(),
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.PERSON));
			
			if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {

				final HorizontalLayout ageSexRow = new HorizontalLayout();
				ageSexRow.setMargin(false);
				ageSexRow.setSpacing(true);

				addDescLabel(ageSexRow, ApproximateAgeHelper.formatApproximateAge(
						personDto.getApproximateAge(), personDto.getApproximateAgeType()),
						I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));

				addDescLabel(ageSexRow, personDto.getSex(),
						I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
				firstColumn.addComponent(ageSexRow);

				addDescLabel(firstColumn, contactDto.getContactOfficer(),
						I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.CONTACT_OFFICER));
			}
		}
		this.addComponent(firstColumn);

		final VerticalLayout secondColumn = new VerticalLayout();
		secondColumn.setMargin(false);
		secondColumn.setSpacing(true);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {

			addDescLabel(secondColumn, contactDto.getDisease(),
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.DISEASE));

			addDescLabel(secondColumn, contactDto.getContactClassification(),
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.CONTACT_CLASSIFICATION));

			addDescLabel(secondColumn, DateFormatHelper.formatDate(contactDto.getLastContactDate()),
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.LAST_CONTACT_DATE));
		}
		this.addComponent(secondColumn);
	}

	private static Label addDescLabel(AbstractLayout layout, Object content, String caption) {
		String contentString = content != null ? content.toString() : "";
		Label label = new Label(contentString);
		label.setCaption(caption);
		layout.addComponent(label);
		return label;
	}

}
