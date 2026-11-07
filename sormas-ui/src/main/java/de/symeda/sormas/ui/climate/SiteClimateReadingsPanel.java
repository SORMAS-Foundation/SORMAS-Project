/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.climate;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * The site readings of the period shown, newest first (decision 99). Users who may edit the environment correct or delete
 * them; deleting asks first and removes the reading with its change history.
 */
public class SiteClimateReadingsPanel extends VerticalLayout {

	private static final long serialVersionUID = -1874018745019436931L;

	private final Grid<SiteClimateReadingDto> grid = new Grid<>();
	private final Label empty = new Label(I18nProperties.getString(Strings.climateSiteReadingsNone));

	/**
	 * @param editable
	 *            whether the user may correct and delete readings (environment edit right and the environment may be
	 *            edited)
	 */
	public SiteClimateReadingsPanel(boolean editable, Runnable onChanged) {

		setMargin(false);
		setSpacing(false);
		setWidthFull();
		addStyleName(CssStyles.VSPACE_TOP_4);

		Label heading = new Label(I18nProperties.getCaption(Captions.climateSiteReadings));
		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		empty.addStyleName(CssStyles.LABEL_SECONDARY);

		grid.setWidthFull();
		grid.addStyleName(CssStyles.CLIMATE_GRID);
		grid.addColumn(r -> DateFormatHelper.formatLocalDateTime(r.getReadingDateTime())).setCaption(caption(SiteClimateReadingDto.READING_DATE_TIME));
		grid.addColumn(r -> ClimateCaptions.number(r.getAirTemperature())).setCaption(caption(SiteClimateReadingDto.AIR_TEMPERATURE));
		grid.addColumn(SiteClimateReadingsPanel::rainfall).setCaption(caption(SiteClimateReadingDto.RAINFALL));
		grid.addColumn(r -> ClimateCaptions.number(r.getRelativeHumidity())).setCaption(caption(SiteClimateReadingDto.RELATIVE_HUMIDITY));
		grid.addColumn(SiteClimateReadingDto::getNotes).setCaption(caption(SiteClimateReadingDto.NOTES)).setExpandRatio(1);
		grid.addColumn(r -> r.getReportingUser() != null ? r.getReportingUser().getCaption() : "")
			.setCaption(caption(SiteClimateReadingDto.REPORTING_USER));
		if (editable) {
			grid.addComponentColumn(reading -> {
				Button edit = new Button(VaadinIcons.EDIT, e -> SiteClimateReadingForm.openEdit(reading, onChanged));
				edit.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_SMALL);
				edit.setDescription(I18nProperties.getCaption(Captions.edit));
				Button delete = new Button(VaadinIcons.TRASH, e -> confirmDelete(reading, onChanged));
				delete.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_SMALL);
				delete.setDescription(I18nProperties.getCaption(Captions.actionDelete));
				HorizontalLayout actions = new HorizontalLayout(edit, delete);
				actions.setMargin(false);
				actions.setSpacing(false);
				return actions;
			}).setWidth(100);
		}
		addComponents(heading, empty, grid);
	}

	public void update(List<SiteClimateReadingDto> readings) {
		grid.setItems(readings);
		grid.setHeightByRows(Math.max(1, Math.min(readings.size(), 8)));
		grid.setVisible(!readings.isEmpty());
		empty.setVisible(readings.isEmpty());
	}

	/**
	 * @return "12 mm in 24 h", or empty
	 */
	static String rainfall(SiteClimateReadingDto reading) {
		if (reading.getRainfall() == null) {
			return "";
		}
		return String.format(
			I18nProperties.getString(Strings.climateReadingRainfall),
			ClimateCaptions.number(reading.getRainfall()),
			reading.getRainfallPeriodHours());
	}

	private static void confirmDelete(SiteClimateReadingDto reading, Runnable onChanged) {
		VaadinUiUtil.showDeleteConfirmationWindow(
			String.format(
				I18nProperties.getString(Strings.confirmationDeleteSiteClimateReading),
				DateFormatHelper.formatLocalDateTime(reading.getReadingDateTime())),
			() -> {
				try {
					FacadeProvider.getSiteClimateReadingFacade().deleteReading(reading.getUuid());
				} catch (ValidationRuntimeException e) {
					Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
					return;
				}
				Notification.show(I18nProperties.getString(Strings.messageSiteClimateReadingDeleted), Notification.Type.TRAY_NOTIFICATION);
				onChanged.run();
			});
	}

	private static String caption(String property) {
		return I18nProperties.getPrefixCaption(SiteClimateReadingDto.I18N_PREFIX, property);
	}

	Grid<SiteClimateReadingDto> getGrid() {
		return grid;
	}
}
