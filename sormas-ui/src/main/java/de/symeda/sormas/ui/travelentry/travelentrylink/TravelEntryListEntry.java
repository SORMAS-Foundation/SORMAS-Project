/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.travelentry.travelentrylink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class TravelEntryListEntry extends HorizontalLayout {

	public static final String SEPARATOR = ": ";

	private final TravelEntryIndexDto travelEntry;
	private Button editButton;

	public TravelEntryListEntry(TravelEntryIndexDto travelEntry) {

		this.travelEntry = travelEntry;

		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);

		HorizontalLayout uuidReportDateLayout = new HorizontalLayout();
		uuidReportDateLayout.setMargin(false);
		uuidReportDateLayout.setSpacing(true);

		Label travelEntryUuidLabel = new Label(DataHelper.getShortUuid(travelEntry.getUuid()));
		travelEntryUuidLabel.setDescription(travelEntry.getUuid());
		uuidReportDateLayout.addComponent(travelEntryUuidLabel);

		Label reportDateLabel = new Label(
			I18nProperties.getPrefixCaption(TravelEntryIndexDto.I18N_PREFIX, TravelEntryIndexDto.REPORT_DATE)
				+ SEPARATOR
				+ DateFormatHelper.formatDate(travelEntry.getReportDate()));
		uuidReportDateLayout.addComponent(reportDateLabel);

		uuidReportDateLayout.setWidthFull();
		uuidReportDateLayout.setComponentAlignment(travelEntryUuidLabel, Alignment.MIDDLE_LEFT);
		uuidReportDateLayout.setComponentAlignment(reportDateLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(uuidReportDateLayout);

		HorizontalLayout diseasePointOfEntryLayout = new HorizontalLayout();
		diseasePointOfEntryLayout.setMargin(false);
		diseasePointOfEntryLayout.setSpacing(true);

		Label diseaseLabel = new Label(DataHelper.toStringNullable(travelEntry.getDisease()));
		CssStyles.style(diseaseLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		diseasePointOfEntryLayout.addComponent(diseaseLabel);

		Label pointOfEntryLabel = new Label(travelEntry.getPointOfEntryName());
		diseasePointOfEntryLayout.addComponent(pointOfEntryLabel);

		diseasePointOfEntryLayout.setWidthFull();
		diseasePointOfEntryLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_LEFT);
		diseasePointOfEntryLayout.setComponentAlignment(pointOfEntryLabel, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(diseasePointOfEntryLayout);
	}

	public void addEditListener(Button.ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-travelEntry-" + travelEntry.getUuid(),
				null,
				VaadinIcons.PENCIL,
				editClickListener,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.TOP_RIGHT);
			setExpandRatio(editButton, 0);
		}
	}

	public TravelEntryIndexDto getTravelEntry() {
		return travelEntry;
	}
}
