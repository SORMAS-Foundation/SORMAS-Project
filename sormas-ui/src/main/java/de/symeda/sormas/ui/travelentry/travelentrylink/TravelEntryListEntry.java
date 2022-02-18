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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.travelentry.TravelEntryListEntryDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class TravelEntryListEntry extends SideComponentField {

	public static final String SEPARATOR = ": ";

	private final TravelEntryListEntryDto travelEntry;

	public TravelEntryListEntry(TravelEntryListEntryDto travelEntry) {

		this.travelEntry = travelEntry;

		HorizontalLayout uuidReportDateLayout = new HorizontalLayout();
		uuidReportDateLayout.setMargin(false);
		uuidReportDateLayout.setSpacing(true);

		Label travelEntryUuidLabel = new Label(DataHelper.getShortUuid(travelEntry.getUuid()));
		travelEntryUuidLabel.setDescription(travelEntry.getUuid());
		uuidReportDateLayout.addComponent(travelEntryUuidLabel);

		Label reportDateLabel = new Label(
			I18nProperties.getPrefixCaption(TravelEntryListEntryDto.I18N_PREFIX, TravelEntryListEntryDto.REPORT_DATE)
				+ SEPARATOR
				+ DateFormatHelper.formatDate(travelEntry.getReportDate()));
		uuidReportDateLayout.addComponent(reportDateLabel);

		uuidReportDateLayout.setWidthFull();
		uuidReportDateLayout.setComponentAlignment(travelEntryUuidLabel, Alignment.MIDDLE_LEFT);
		uuidReportDateLayout.setComponentAlignment(reportDateLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(uuidReportDateLayout);

		HorizontalLayout diseasePointOfEntryLayout = new HorizontalLayout();
		diseasePointOfEntryLayout.setMargin(false);
		diseasePointOfEntryLayout.setSpacing(true);

		Label diseaseLabel = new Label(DataHelper.toStringNullable(travelEntry.getDisease()));
		CssStyles.style(diseaseLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		diseasePointOfEntryLayout.addComponent(diseaseLabel);

		Label pointOfEntryLabel =
			new Label(I18nProperties.getPrefixCaption(TravelEntryListEntryDto.POINT_OF_ENTRY_I18N_PREFIX, travelEntry.getPointOfEntryName()));
		diseasePointOfEntryLayout.addComponent(pointOfEntryLabel);

		diseasePointOfEntryLayout.setWidthFull();
		diseasePointOfEntryLayout.setComponentAlignment(diseaseLabel, Alignment.MIDDLE_LEFT);
		diseasePointOfEntryLayout.setComponentAlignment(pointOfEntryLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(diseasePointOfEntryLayout);
	}

	public TravelEntryListEntryDto getTravelEntry() {
		return travelEntry;
	}
}
