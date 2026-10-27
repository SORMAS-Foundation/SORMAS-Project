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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.climate.ClimateHistorySource;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Backfill tab of the Climate data screen: new backfills, the climate jobs (fetches, backfills, top-ups and district
 * relinks) and the history each district holds.
 */
@SuppressWarnings("java:S110") // suppress sonar too many parents warning
public class ClimateJobsView extends AbstractClimateDataView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/climateBackfill";

	private final Label backfillUnavailable = new Label();
	private Button newBackfillButton;

	public ClimateJobsView() {

		super(VIEW_NAME);

		if (UiUtil.permitted(UserRight.CLIMATE_MANAGE_SOURCES)) {
			newBackfillButton = ButtonHelper.createIconButton(
				Captions.climateNewBackfill,
				VaadinIcons.CLOUD_DOWNLOAD,
				e -> ClimateBackfillDialog.open(getSummary().getHistoryYears(), this::refresh),
				ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(newBackfillButton);
		}

		backfillUnavailable.setWidthFull();
		backfillUnavailable.addStyleName(CssStyles.LABEL_WARNING);
		backfillUnavailable.setVisible(false);
		contentLayout.addComponent(backfillUnavailable);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		updateBackfillAvailability();
	}

	/**
	 * Backfill needs OpenWeather as the history source and an API key; otherwise the tab says why and shows only the job
	 * list and history coverage.
	 */
	private void updateBackfillAvailability() {

		String reason = null;
		if (getSummary().getHistorySource() != ClimateHistorySource.OPENWEATHER) {
			reason = I18nProperties.getValidationError(Validations.climateBackfillOff);
		} else if (!getSummary().isApiKeySet()) {
			reason = I18nProperties.getString(Strings.climateBackfillNoApiKey);
		}
		backfillUnavailable.setValue(reason);
		backfillUnavailable.setVisible(reason != null);
		if (newBackfillButton != null) {
			newBackfillButton.setVisible(reason == null);
		}
	}

	private void refresh() {
		refreshSummary();
	}
}
