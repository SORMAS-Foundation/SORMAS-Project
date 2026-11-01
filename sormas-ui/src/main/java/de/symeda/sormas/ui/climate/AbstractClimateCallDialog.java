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

import java.util.Collections;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.climate.ClimateCallEstimateDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * A dialog that starts a job calling OpenWeather: the scope (all districts, a region or a district, with or without
 * environment points), the number of calls it takes, OpenWeather's price page, and a tick-box the user must tick before
 * Start is enabled. Calls, never money (decision 37).
 */
public abstract class AbstractClimateCallDialog extends VerticalLayout {

	private static final long serialVersionUID = 8817270611637457403L;

	public static final String OPENWEATHER_PRICE_PAGE = "https://openweathermap.org/price";

	protected final ComboBox<RegionReferenceDto> region;
	protected final ComboBox<DistrictReferenceDto> district;
	protected final CheckBox includeEnvironments = new CheckBox(I18nProperties.getCaption(Captions.climateIncludeEnvironments));

	private final Label estimateLabel = new Label();
	private final CheckBox confirmation = new CheckBox(I18nProperties.getCaption(Captions.climateConfirmCalls));
	private final Button startButton;
	private final Runnable onStarted;
	private Window window;
	private ClimateCallEstimateDto estimate;

	protected AbstractClimateCallDialog(Runnable onStarted) {

		this.onStarted = onStarted;
		setMargin(true);
		setSpacing(true);
		setWidth(640, Unit.PIXELS);

		region = new ComboBox<>(I18nProperties.getCaption(Captions.Region), FacadeProvider.getRegionFacade().getAllActiveAsReference());
		district = new ComboBox<>(I18nProperties.getCaption(Captions.District));
		region.setItemCaptionGenerator(ReferenceDto::buildCaption);
		district.setItemCaptionGenerator(ReferenceDto::buildCaption);
		region.addValueChangeListener(e -> {
			district.setItems(
				e.getValue() != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid()) : Collections.emptyList());
			scopeChanged();
		});
		district.addValueChangeListener(e -> scopeChanged());
		includeEnvironments.addValueChangeListener(e -> scopeChanged());

		Label scopeInfo = new Label(I18nProperties.getString(Strings.infoClimateJobScope));
		scopeInfo.setWidthFull();
		scopeInfo.addStyleName(CssStyles.LABEL_SECONDARY);

		estimateLabel.setWidthFull();
		estimateLabel.addStyleName(CssStyles.LABEL_BOLD);

		Label callsInfo = new Label(getCallsInfo(), ContentMode.TEXT);
		callsInfo.setWidthFull();
		Link pricePage = new Link(I18nProperties.getCaption(Captions.climatePricePage), new ExternalResource(OPENWEATHER_PRICE_PAGE));
		pricePage.setTargetName("_blank");

		confirmation.addValueChangeListener(e -> updateStartButton());

		Button cancel = ButtonHelper.createButton(Captions.actionCancel, e -> window.close());
		startButton = ButtonHelper.createButton(Captions.climateStart, e -> start(), ValoTheme.BUTTON_PRIMARY);
		startButton.setEnabled(false);
		HorizontalLayout buttons = new HorizontalLayout(cancel, startButton);

		HorizontalLayout scope = new HorizontalLayout(region, district);
		scope.setMargin(false);
		addComponents(scope, scopeInfo);
		addPeriodFields();
		addComponent(includeEnvironments);
		Button checkCalls = createCheckCallsButton();
		if (checkCalls != null) {
			addComponent(checkCalls);
		}
		addComponents(estimateLabel, callsInfo, pricePage, confirmation, buttons);
		setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
	}

	/**
	 * Adds the fields that choose the period, below the scope.
	 */
	protected abstract void addPeriodFields();

	/**
	 * @return the button that works out the calls, or null when they are worked out on every change
	 */
	protected abstract Button createCheckCallsButton();

	protected abstract String getCallsInfo();

	protected abstract ClimateCallEstimateDto calculateEstimate();

	/**
	 * Queues the job with the confirmed number of calls.
	 */
	protected abstract void startJob(ClimateCallEstimateDto confirmedEstimate);

	protected abstract String getStartedMessage();

	/**
	 * Called when the scope or period changes: by default the estimate shown no longer applies and must be checked again.
	 */
	protected void scopeChanged() {
		resetEstimate();
	}

	private void resetEstimate() {
		estimate = null;
		estimateLabel.setValue("");
		confirmation.setValue(false);
		updateStartButton();
	}

	/**
	 * Works out the calls for the chosen scope and period and shows them.
	 */
	protected void updateEstimate() {

		resetEstimate();
		try {
			estimate = calculateEstimate();
		} catch (ValidationRuntimeException e) {
			estimateLabel.setValue(e.getMessage());
			return;
		}
		estimateLabel.setValue(
			estimate.getCalls() == 0
				? I18nProperties.getString(Strings.messageClimateNothingToFetch)
				: String.format(
					I18nProperties.getString(Strings.climateCallEstimate),
					ClimateSummaryStrip.formatCount(estimate.getLocations()),
					ClimateSummaryStrip.formatCount(estimate.getDays()),
					ClimateSummaryStrip.formatCount(estimate.getCalls())));
	}

	private void updateStartButton() {
		startButton.setEnabled(estimate != null && estimate.getCalls() > 0 && Boolean.TRUE.equals(confirmation.getValue()));
	}

	private void start() {

		try {
			startJob(estimate);
		} catch (ValidationRuntimeException e) {
			Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return;
		}
		Notification.show(getStartedMessage(), Notification.Type.TRAY_NOTIFICATION);
		window.close();
		onStarted.run();
	}

	/**
	 * Shows the dialog in a window.
	 */
	public void show(String caption) {
		window = VaadinUiUtil.showPopupWindow(this, caption);
	}
}
