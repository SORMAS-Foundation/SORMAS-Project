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
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateDailySource;
import de.symeda.sormas.api.climate.ClimateJobIndexDto;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateSummaryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.system.SystemConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The read-only strip at the top of the Climate data screen: where data comes from, whether a key is set, the minimum
 * history, the calls needed per night and how last night's fetch went. Settings are edited in System Configuration.
 */
public class ClimateSummaryStrip extends VerticalLayout {

	private static final long serialVersionUID = 4520813262019418632L;

	private final Label sources = new Label();
	private final Label calls = new Label();
	private final Label warning = new Label();

	public ClimateSummaryStrip() {

		setMargin(false);
		setSpacing(false);
		addStyleName(CssStyles.VSPACE_3);

		sources.setWidthFull();
		calls.setWidthFull();
		warning.setWidthFull();
		warning.addStyleName(CssStyles.LABEL_WARNING);
		addComponents(sources, calls, warning);

		if (UiUtil.permitted(UserRight.SYSTEM_CONFIGURATION)) {
			Button settings =
				ButtonHelper.createIconButton(Captions.climateEditSettings, VaadinIcons.COG, e -> openClimateSettings(), ValoTheme.BUTTON_LINK);
			addComponent(settings);
		}
	}

	public void update(ClimateSummaryDto summary) {

		String apiKey = I18nProperties.getString(summary.isApiKeySet() ? Strings.climateSummaryApiKeySet : Strings.climateSummaryApiKeyMissing);
		sources.setValue(
			String.format(
				I18nProperties.getString(Strings.climateSummarySources),
				summary.getDailySource(),
				summary.getHistorySource(),
				apiKey,
				summary.getMinHistoryYears(),
				summary.getHistoryYears()));

		boolean fromOpenWeather = summary.getDailySource() == ClimateDailySource.OPENWEATHER;
		calls.setVisible(fromOpenWeather);
		if (fromOpenWeather) {
			calls.setValue(
				String.format(
					I18nProperties.getString(Strings.climateSummaryCallsPerNight),
					formatCount(summary.getCallsPerNight()),
					formatCount(summary.getDistrictLocations()),
					formatCount(summary.getEnvironmentOnlyLocations()))
					+ " · "
					+ describeLastNight(summary.getLastNightlyFetch()));
		}

		String warningText = fromOpenWeather ? getWarning(summary) : null;
		warning.setValue(warningText);
		warning.setVisible(warningText != null);
	}

	private static String describeLastNight(ClimateJobIndexDto lastNight) {

		if (lastNight == null) {
			return I18nProperties.getString(Strings.climateSummaryNoNightlyFetch);
		}
		return String.format(
			I18nProperties.getString(Strings.climateSummaryLastNight),
			formatCount(lastNight.getCallsMade()),
			lastNight.getStatus(),
			DateFormatHelper.formatLocalDateTime(lastNight.getFinishedAt() != null ? lastNight.getFinishedAt() : lastNight.getCreationDate()));
	}

	private static String getWarning(ClimateSummaryDto summary) {

		if (!summary.isApiKeySet()) {
			return I18nProperties.getString(Strings.climateWarningNoApiKey);
		}
		ClimateJobIndexDto lastNight = summary.getLastNightlyFetch();
		if (lastNight == null) {
			return null;
		}
		if (lastNight.getStatus() == ClimateJobStatus.STOPPED_AT_LIMIT) {
			return String.format(
				I18nProperties.getString(Strings.climateWarningLimitReached),
				formatCount(lastNight.getCallsMade()),
				formatCount(Math.max(0, lastNight.getLocationsTotal() - lastNight.getLocationsDone())));
		}
		if (lastNight.getStatus() == ClimateJobStatus.FAILED) {
			return String.format(I18nProperties.getString(Strings.climateWarningLastNightFailed), lastNight.getLastError());
		}
		return null;
	}

	/**
	 * Opens System Configuration filtered to the Climate category.
	 */
	private static void openClimateSettings() {

		SystemConfigurationValueCriteria criteria = new SystemConfigurationValueCriteria();
		criteria.setCategory(FacadeProvider.getSystemConfigurationCategoryFacade().getCategoryReferenceDtoByName(ClimateConfiguration.CATEGORY_NAME));
		ViewModelProviders.of(SystemConfigurationView.class).remove(SystemConfigurationValueCriteria.class);
		ViewModelProviders.of(SystemConfigurationView.class).get(SystemConfigurationValueCriteria.class, criteria);
		SormasUI.get().getNavigator().navigateTo(SystemConfigurationView.VIEW_NAME);
	}

	/**
	 * @return the count with thousands separators, as the climate cost warning writes call numbers
	 */
	static String formatCount(long count) {
		return String.format("%,d", count);
	}
}
