/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.user;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ProgressBar;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * @author Alex Vidrean
 * @since 11-Dec-20
 */
@SuppressWarnings("serial")
public class UserSyncProgressLayout extends VerticalLayout {

	// Components
	private final ProgressBar progressBar;
	private final Label processedImportsLabel;
	private final Label syncSuccessLabel;
	private final Label syncErrorsLabel;
	private final Button closeCancelButton;
	private final HorizontalLayout infoLayout;
	private final Label infoLabel;

	private ProgressBar progressCircle;
	private Image errorIcon;
	private Image successIcon;
	private Image warningIcon;
	private Component currentInfoComponent;

	private final ClickListener cancelListener;

	// Counts
	private int processedImportsCount;
	private int syncSuccessCount;
	private int syncErrorCount;
	private final long totalCount;

	private final UI currentUI;

	public UserSyncProgressLayout(long totalCount, UI currentUI, Runnable cancelCallback) {
		this.totalCount = totalCount;
		this.currentUI = currentUI;

		setWidth(100, Unit.PERCENTAGE);
		setMargin(true);

		// Info text and icon/progress circle
		infoLayout = new HorizontalLayout();
		infoLayout.setWidth(100, Unit.PERCENTAGE);
		infoLayout.setSpacing(true);
		initializeInfoComponents();
		currentInfoComponent = progressCircle;
		infoLayout.addComponent(currentInfoComponent);
		infoLabel = new Label(String.format(I18nProperties.getString(Strings.infoUserSyncProcess), totalCount));
		infoLabel.setContentMode(ContentMode.HTML);
		infoLayout.addComponent(infoLabel);
		infoLayout.setExpandRatio(infoLabel, 1);

		addComponent(infoLayout);

		// Progress bar
		progressBar = new ProgressBar(0.0f);
		CssStyles.style(progressBar, CssStyles.VSPACE_TOP_3);
		addComponent(progressBar);
		progressBar.setWidth(100, Unit.PERCENTAGE);

		// Progress info
		HorizontalLayout progressInfoLayout = new HorizontalLayout();
		CssStyles.style(progressInfoLayout, CssStyles.VSPACE_TOP_5);
		progressInfoLayout.setSpacing(true);
		processedImportsLabel = new Label(String.format(I18nProperties.getCaption(Captions.syncProcessed), 0, totalCount));
		progressInfoLayout.addComponent(processedImportsLabel);
		syncSuccessLabel = new Label(String.format(I18nProperties.getCaption(Captions.syncSuccessful), 0));
		CssStyles.style(syncSuccessLabel, CssStyles.LABEL_POSITIVE);
		progressInfoLayout.addComponent(syncSuccessLabel);
		syncErrorsLabel = new Label(String.format(I18nProperties.getCaption(Captions.syncErrors), 0));
		CssStyles.style(syncErrorsLabel, CssStyles.LABEL_CRITICAL);
		progressInfoLayout.addComponent(syncErrorsLabel);

		addComponent(progressInfoLayout);
		setComponentAlignment(progressInfoLayout, Alignment.TOP_RIGHT);

		// Cancel button
		cancelListener = e -> cancelCallback.run();

		closeCancelButton = ButtonHelper.createButton(Captions.actionCancel, cancelListener, CssStyles.VSPACE_TOP_2);

		addComponent(closeCancelButton);
		setComponentAlignment(closeCancelButton, Alignment.MIDDLE_RIGHT);
	}

	private void initializeInfoComponents() {
		progressCircle = new ProgressBar();
		progressCircle.setIndeterminate(true);
		CssStyles.style(progressCircle, "v-progressbar-indeterminate-large");

		errorIcon = new Image(null, new ThemeResource("img/error-icon.png"));
		errorIcon.setHeight(35, Unit.PIXELS);
		errorIcon.setWidth(35, Unit.PIXELS);
		successIcon = new Image(null, new ThemeResource("img/success-icon.png"));
		successIcon.setHeight(35, Unit.PIXELS);
		successIcon.setWidth(35, Unit.PIXELS);
		warningIcon = new Image(null, new ThemeResource("img/warning-icon.png"));
		warningIcon.setHeight(35, Unit.PIXELS);
		warningIcon.setWidth(35, Unit.PIXELS);
	}

	public void updateProgress(SyncResult result) {
		currentUI.access(() -> {
			processedImportsCount++;
			if (result == SyncResult.SUCCESS) {
				syncSuccessLabel.setValue(String.format(I18nProperties.getCaption(Captions.syncSuccessful), ++syncSuccessCount));
			} else if (result == SyncResult.ERROR) {
				syncErrorsLabel.setValue(String.format(I18nProperties.getCaption(Captions.syncErrors), ++syncErrorCount));
			}
			processedImportsLabel.setValue(String.format(I18nProperties.getCaption(Captions.syncProcessed), processedImportsCount, totalCount));
			progressBar.setValue((float) processedImportsCount / (float) totalCount);
		});
	}

	public void makeClosable(Runnable closeCallback) {
		closeCancelButton.setCaption(I18nProperties.getCaption(Captions.actionClose));
		closeCancelButton.removeClickListener(cancelListener);
		closeCancelButton.addClickListener(e -> closeCallback.run());
	}

	public void setInfoLabelText(String text) {
		infoLabel.setValue(text);
	}

	public void displayErrorIcon() {
		infoLayout.removeComponent(currentInfoComponent);
		currentInfoComponent = errorIcon;
		infoLayout.addComponentAsFirst(currentInfoComponent);
	}

	public void displaySuccessIcon() {
		infoLayout.removeComponent(currentInfoComponent);
		currentInfoComponent = successIcon;
		infoLayout.addComponentAsFirst(currentInfoComponent);
	}

	public void displayWarningIcon() {
		infoLayout.removeComponent(currentInfoComponent);
		currentInfoComponent = warningIcon;
		infoLayout.addComponentAsFirst(currentInfoComponent);
	}

	public enum SyncResult {
		SUCCESS,
		ERROR
	}
}
