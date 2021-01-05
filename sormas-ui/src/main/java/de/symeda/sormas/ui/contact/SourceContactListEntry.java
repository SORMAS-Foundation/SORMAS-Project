/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.contact;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class SourceContactListEntry extends HorizontalLayout {

	private final ContactIndexDto contact;
	private Button editButton;

	public SourceContactListEntry(ContactIndexDto contact) {

		this.contact = contact;

		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(true);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);

		VerticalLayout leftColumn = new VerticalLayout();
		leftColumn.setMargin(false);
		leftColumn.setSpacing(false);
		{
			Label lblSourceCaseName = new Label(
				contact.getCaze() != null
					? I18nProperties.getCaption(Captions.contactSourceCase) + ":<br/>" + contact.getCaze().getCaption()
					: I18nProperties.getString(Strings.infoNoSourceCase),
				ContentMode.HTML);
			CssStyles.style(lblSourceCaseName, CssStyles.LABEL_BOLD, CssStyles.VSPACE_3);
			lblSourceCaseName.setWidth(100, Unit.PERCENTAGE);
			leftColumn.addComponent(lblSourceCaseName);

			Label lblRegion = new Label(StringUtils.isNotBlank(contact.getRegionName()) ? contact.getRegionName() : contact.getCaseRegionName());
			lblRegion.setWidth(100, Unit.PERCENTAGE);
			leftColumn.addComponent(lblRegion);

			Label lblDistrict =
				new Label(StringUtils.isNotBlank(contact.getDistrictName()) ? contact.getDistrictName() : contact.getCaseDistrictName());
			lblDistrict.setWidth(100, Unit.PERCENTAGE);
			leftColumn.addComponent(lblDistrict);
		}
		mainLayout.addComponent(leftColumn);

		VerticalLayout rightColumn = new VerticalLayout();
		rightColumn.addStyleName(CssStyles.ALIGN_RIGHT);
		rightColumn.setMargin(false);
		rightColumn.setSpacing(false);
		{
			Label lblContactUuid = new Label(
				I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.UUID) + ":<br/>"
					+ DataHelper.getShortUuid(contact.getUuid().toUpperCase()),
				ContentMode.HTML);
			CssStyles.style(lblContactUuid, CssStyles.LABEL_BOLD, CssStyles.VSPACE_3, CssStyles.LABEL_TEXT_ALIGN_RIGHT);
			lblContactUuid.setWidth(100, Unit.PERCENTAGE);
			rightColumn.addComponent(lblContactUuid);

			if (contact.getContactProximity() != null) {
				Label lblContactProximity = new Label(StringUtils.abbreviate(contact.getContactProximity().toString(), 50));
				CssStyles.style(lblContactProximity, CssStyles.LABEL_TEXT_ALIGN_RIGHT);
				lblContactProximity.setWidth(100, Unit.PERCENTAGE);
				lblContactProximity.setDescription(contact.getContactProximity().toString());
				rightColumn.addComponent(lblContactProximity);
			}

			Label lblLastContactDate =
				new Label(DateHelper.formatLocalDate(contact.getLastContactDate(), I18nProperties.getUserLanguage()));
			CssStyles.style(lblLastContactDate, CssStyles.LABEL_TEXT_ALIGN_RIGHT);
			lblLastContactDate.setWidth(100, Unit.PERCENTAGE);
			rightColumn.addComponent(lblLastContactDate);
		}
		mainLayout.addComponent(rightColumn);
		mainLayout.setComponentAlignment(rightColumn, Alignment.TOP_RIGHT);
	}

	public void addEditListener(int rowIndex, ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-task-" + rowIndex,
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public ContactIndexDto getContact() {
		return contact;
	}

}
