/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils.components.progress;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public abstract class AbstractProgressLayout<P extends ProgressUpdateInfo> extends VerticalLayout {

	private final UI currentUI;
	private final int totalCount;

	private HorizontalLayout infoLayout;
	private HorizontalLayout descriptionLayout;
	private Label infoLabel;
	private Label descriptionLabel;
	/**
	 * The component that is displayed to the left of the info label,
	 * indicating the result of the operation; set to a progress circle in the beginning
	 * and then updated with one of the icons as the operation finishes.
	 */
	private Component progressResultComponent;
	private Image successIcon;
	private Image warningIcon;
	private Image errorIcon;

	private ProgressBar progressBar;
	protected HorizontalLayout progressStatusLayout;
	private Label progressCountLabel;
	private int progressCount = 0;

	protected HorizontalLayout hintLayout;

	private Button cancelButton;
	private Button closeButton;
	private final Runnable cancelCallback;

	public AbstractProgressLayout(UI currentUI, String initialInfoText, int totalCount, String descriptionText, Runnable cancelCallback) {

		this.currentUI = currentUI;
		this.totalCount = totalCount;
		this.cancelCallback = cancelCallback;

		setWidth(100, Unit.PERCENTAGE);
		setMargin(true);

		initInfoSection(initialInfoText);
		initDescriptionSection(descriptionText);
		initProgressSection();
		initButtonPanel();
		initHintSection();
	}

	/**
	 * @return The i18n property for the count of processed entries, shown below the progress bar
	 */
	protected abstract String getProcessedLabelI18nProperty();

	/**
	 * Contains additional steps that need to be taken when the progress is updated, based on the
	 * implementation of this class. Called in each updateProgress call.
	 */
	protected abstract void handleOptionalProgressUpdates(P progressUpdateInfo);

	public void updateProgress(P progressUpdateInfo) {

		currentUI.access(() -> {
			progressCount += progressUpdateInfo.getProcessedEntryCount();
			progressCountLabel.setValue(String.format(getProcessedLabelI18nProperty(), progressCount, totalCount));
			progressBar.setValue((float) progressCount / (float) totalCount);
			handleOptionalProgressUpdates(progressUpdateInfo);
		});
	}

	/**
	 * To be called when the operation indicated by the progress layout has finished.
	 * Sets the result icon, hides the Cancel button, shows the Close button, and
	 * optionally replaces the info text.
	 */
	public void finishProgress(ProgressResult result, String newInfoText, String descriptionText, Runnable closeCallback) {

		infoLayout.removeComponent(progressResultComponent);
		switch (result) {
		case SUCCESS:
			progressResultComponent = successIcon;
			break;
		case SUCCESS_WITH_WARNING:
			progressResultComponent = warningIcon;
			break;
		case FAILURE:
			progressResultComponent = errorIcon;
			break;
		}
		infoLayout.addComponentAsFirst(progressResultComponent);

		if (newInfoText != null) {
			infoLabel.setValue(newInfoText);
		}

		if (descriptionText != null) {
			descriptionLabel.setValue(descriptionText);
		}

		cancelButton.setVisible(false);
		closeButton.setVisible(true);
		closeButton.addClickListener(e -> closeCallback.run());
	}

	protected String getHintText() {
		return null;
	}

	private void initInfoSection(String initialInfoText) {

		infoLayout = new HorizontalLayout();
		infoLayout.setWidth(100, Unit.PERCENTAGE);
		infoLayout.setSpacing(true);

		initInfoComponents();
		infoLayout.addComponent(progressResultComponent);

		infoLabel = new Label(initialInfoText);
		infoLabel.setContentMode(ContentMode.HTML);
		infoLabel.setWidthFull();
		infoLayout.addComponent(infoLabel);
		infoLayout.setExpandRatio(infoLabel, 1);

		addComponent(infoLayout);
	}

	private void initDescriptionSection(String descriptionText) {
		descriptionLayout = new HorizontalLayout();
		descriptionLayout.setWidth(90, Unit.PERCENTAGE);
		descriptionLayout.setSpacing(true);

		descriptionLabel = new Label(descriptionText);
		descriptionLabel.setContentMode(ContentMode.HTML);
		descriptionLabel.setWidthFull();
		descriptionLabel.addStyleNames(CssStyles.VSPACE_TOP_3, CssStyles.HSPACE_LEFT_1);

		descriptionLayout.addComponent(descriptionLabel);
		descriptionLayout.setExpandRatio(descriptionLabel, 1);

		addComponent(descriptionLayout);
	}

	private void initInfoComponents() {

		ProgressBar progressCircle = new ProgressBar();
		progressCircle.setIndeterminate(true);
		CssStyles.style(progressCircle, "v-progressbar-indeterminate-large");
		successIcon = buildInfoIcon("img/success-icon.png");
		warningIcon = buildInfoIcon("img/warning-icon.png");
		errorIcon = buildInfoIcon("img/error-icon.png");
		progressResultComponent = progressCircle;
	}

	private Image buildInfoIcon(String resourceId) {

		Image infoIcon = new Image(null, new ThemeResource(resourceId));
		infoIcon.setHeight(35, Unit.PIXELS);
		infoIcon.setWidth(35, Unit.PIXELS);
		return infoIcon;
	}

	private void initProgressSection() {

		VerticalLayout progressLayout = new VerticalLayout();
		progressLayout.setSpacing(true);

		progressBar = new ProgressBar(0.0f);
		progressBar.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(progressBar, CssStyles.VSPACE_TOP_3);
		progressLayout.addComponent(progressBar);

		progressStatusLayout = new HorizontalLayout();
		progressStatusLayout.setSpacing(true);
		progressCountLabel = new Label(String.format(getProcessedLabelI18nProperty(), progressCount, totalCount));
		progressStatusLayout.addComponent(progressCountLabel);
		progressLayout.addComponent(progressStatusLayout);
		progressLayout.setComponentAlignment(progressStatusLayout, Alignment.TOP_RIGHT);

		addComponent(progressLayout);
	}

	private void initButtonPanel() {

		HorizontalLayout buttonPanel = new HorizontalLayout();
		buttonPanel.setSpacing(true);
		CssStyles.style(buttonPanel, CssStyles.VSPACE_TOP_2);

		cancelButton = ButtonHelper.createButton(Captions.actionCancel, e -> cancelCallback.run());
		buttonPanel.addComponent(cancelButton);
		closeButton = ButtonHelper.createButton(Captions.actionClose);
		closeButton.setVisible(false);
		buttonPanel.addComponent(closeButton);

		addComponent(buttonPanel);
		setComponentAlignment(buttonPanel, Alignment.MIDDLE_RIGHT);
	}

	private void initHintSection() {

		if (StringUtils.isNotBlank(getHintText())) {
			hintLayout = new HorizontalLayout();
			CssStyles.style(hintLayout, CssStyles.VSPACE_TOP_3);

			Label hintLabel = new Label(getHintText(), ContentMode.HTML);
			CssStyles.style(hintLabel, CssStyles.LABEL_MINOR, CssStyles.LABEL_SMALL);
			hintLayout.addComponent(hintLabel);

			addComponent(hintLayout);
		}
	}

}
