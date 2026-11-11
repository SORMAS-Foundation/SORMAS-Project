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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateCallEstimateDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Fetch now: fetches the chosen period again from OpenWeather for all districts, a region or a district, and updates
 * OpenWeather values that changed. Runs within a few minutes; its calls count against the account's daily limit.
 */
public class ClimateFetchNowDialog extends AbstractClimateCallDialog {

	private static final long serialVersionUID = -2914170618315330410L;

	/** The nightly fetch's window: yesterday and the 9 days before. */
	private static final int DEFAULT_DAYS = 10;

	private DateField from;
	private DateField to;

	public static void open(Runnable onStarted) {
		ClimateFetchNowDialog dialog = new ClimateFetchNowDialog(onStarted);
		dialog.updateEstimate();
		dialog.show(I18nProperties.getString(Strings.headingClimateFetchNow));
	}

	private ClimateFetchNowDialog(Runnable onStarted) {
		super(onStarted);
	}

	@Override
	protected void addPeriodFields() {

		LocalDate yesterday = LocalDate.now().minusDays(1);
		from = new DateField(I18nProperties.getCaption(Captions.climateFilterDateFrom), yesterday.minusDays(DEFAULT_DAYS - 1));
		to = new DateField(I18nProperties.getCaption(Captions.climateFilterDateTo), yesterday);
		for (DateField field : new DateField[] {
			from,
			to }) {
			field.setDateFormat(DateFormatHelper.getDateFormatPattern());
			field.setRangeEnd(yesterday);
			field.addStyleName(CssStyles.VSPACE_NONE);
			field.addValueChangeListener(e -> scopeChanged());
		}
		HorizontalLayout period = new HorizontalLayout(from, to);
		period.setMargin(false);
		addComponent(period);
	}

	@Override
	protected Button createCheckCallsButton() {
		// a fetch costs one call per 10 days per location, quick to work out on every change
		return null;
	}

	@Override
	protected void scopeChanged() {
		if (from != null) {
			updateEstimate();
		}
	}

	@Override
	protected String getCallsInfo() {
		return I18nProperties.getString(Strings.infoClimateFetchNowCalls);
	}

	@Override
	protected ClimateCallEstimateDto calculateEstimate() {
		return FacadeProvider.getClimateJobFacade()
			.calculateFetchCalls(
				region.getValue(),
				district.getValue(),
				includeEnvironments.getValue(),
				toDate(from.getValue()),
				toDate(to.getValue()));
	}

	@Override
	protected void startJob(ClimateCallEstimateDto confirmedEstimate) {
		FacadeProvider.getClimateJobFacade()
			.fetchNow(region.getValue(), district.getValue(), includeEnvironments.getValue(), toDate(from.getValue()), toDate(to.getValue()));
	}

	@Override
	protected String getStartedMessage() {
		return I18nProperties.getString(Strings.messageClimateFetchQueued);
	}

	private static Date toDate(LocalDate day) {
		return day != null ? Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
	}
}
