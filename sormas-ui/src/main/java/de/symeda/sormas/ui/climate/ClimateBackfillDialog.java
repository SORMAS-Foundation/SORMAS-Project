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

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateCallEstimateDto;
import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * A new backfill from OpenWeather for all districts, a region or a district, going back a number of years. The calls are
 * checked on request (a national check reads the days held at every location), and Start needs the tick-box.
 */
public class ClimateBackfillDialog extends AbstractClimateCallDialog {

	private static final long serialVersionUID = 2370148921404757418L;

	private ComboBox<Integer> years;

	/**
	 * @param defaultYears
	 *            the years offered first, CLIMATE_HISTORY_YEARS
	 */
	public static void open(int defaultYears, Runnable onStarted) {
		ClimateBackfillDialog dialog = new ClimateBackfillDialog(onStarted);
		dialog.years.setValue(defaultYears);
		dialog.show(I18nProperties.getString(Strings.headingClimateNewBackfill));
	}

	private ClimateBackfillDialog(Runnable onStarted) {
		super(onStarted);
	}

	@Override
	protected void addPeriodFields() {

		years = new ComboBox<>(
			I18nProperties.getCaption(Captions.climateBackfillYears),
			IntStream.rangeClosed(1, ClimateConfiguration.MAX_HISTORY_YEARS).boxed().collect(Collectors.toList()));
		years.setEmptySelectionAllowed(false);
		years.setWidth(100, Unit.PIXELS);
		years.addStyleName(CssStyles.VSPACE_NONE);
		years.addValueChangeListener(e -> scopeChanged());
		addComponent(years);
	}

	@Override
	protected Button createCheckCallsButton() {
		return ButtonHelper.createButton(Captions.climateCheckCalls, e -> updateEstimate());
	}

	@Override
	protected String getCallsInfo() {
		return I18nProperties.getString(Strings.infoClimateBackfillCalls);
	}

	@Override
	protected ClimateCallEstimateDto calculateEstimate() {
		return FacadeProvider.getClimateJobFacade()
			.calculateBackfillCalls(region.getValue(), district.getValue(), includeEnvironments.getValue(), years.getValue());
	}

	@Override
	protected void startJob(ClimateCallEstimateDto confirmedEstimate) {
		FacadeProvider.getClimateJobFacade()
			.startBackfill(region.getValue(), district.getValue(), includeEnvironments.getValue(), years.getValue(), confirmedEstimate.getCalls());
	}

	@Override
	protected String getStartedMessage() {
		return I18nProperties.getString(Strings.messageClimateBackfillQueued);
	}
}
