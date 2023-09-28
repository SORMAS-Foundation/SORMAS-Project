/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.ui.importer;

import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.progress.AbstractProgressLayout;

public class ImportProgressLayout extends AbstractProgressLayout<ImportProgressUpdateInfo> {

	private Label successfulImportsLabel;
	private int successfulImportsCount;
	private Label failedImportsLabel;
	private int failedImportsCount;
	private Label skippedImportsLabel;
	private int skippedImportsCount;
	private Label duplicateImportsLabel;
	private int duplicateImportsCount;

	boolean showDuplicates;
	boolean showSkips;

	public ImportProgressLayout(UI currentUI, int totalCount, Runnable cancelCallback, boolean showDuplicates) {
		this(currentUI, totalCount, cancelCallback, showDuplicates, true);
	}

	public ImportProgressLayout(UI currentUI, int totalCount, Runnable cancelCallback, boolean showDuplicates, boolean showSkips) {

		super(currentUI, String.format(I18nProperties.getString(Strings.infoImportProcess), totalCount), totalCount, null, cancelCallback);
		this.showDuplicates = showDuplicates;
		this.showSkips = showSkips;
		initAdditionalProgressStatusComponents();
	}

	protected void initAdditionalProgressStatusComponents() {

		successfulImportsLabel = new Label(String.format(I18nProperties.getCaption(Captions.importImports), 0));
		CssStyles.style(successfulImportsLabel, CssStyles.LABEL_POSITIVE);
		progressStatusLayout.addComponent(successfulImportsLabel);
		failedImportsLabel = new Label(String.format(I18nProperties.getCaption(Captions.importErrors), 0));
		CssStyles.style(failedImportsLabel, CssStyles.LABEL_CRITICAL);
		progressStatusLayout.addComponent(failedImportsLabel);

		if (showDuplicates) {
			duplicateImportsLabel = new Label(String.format(I18nProperties.getCaption(Captions.importDuplicates), 0));
			CssStyles.style(duplicateImportsLabel, CssStyles.LABEL_WARNING);
			progressStatusLayout.addComponent(duplicateImportsLabel);
		}
		if (showSkips) {
			skippedImportsLabel = new Label(String.format(I18nProperties.getCaption(Captions.importSkips), 0));
			CssStyles.style(skippedImportsLabel, CssStyles.LABEL_MINOR);
			progressStatusLayout.addComponent(skippedImportsLabel);
		}
	}

	@Override
	protected String getProcessedLabelI18nProperty() {
		return I18nProperties.getCaption(Captions.importProcessed);
	}

	@Override
	protected void handleOptionalProgressUpdates(ImportProgressUpdateInfo progressUpdateInfo) {

		ImportLineResult result = progressUpdateInfo.getImportLineResult();
		if (result == ImportLineResult.SUCCESS) {
			successfulImportsCount++;
			successfulImportsLabel.setValue(String.format(I18nProperties.getCaption(Captions.importImports), successfulImportsCount));
		} else if (result == ImportLineResult.ERROR) {
			failedImportsCount++;
			failedImportsLabel.setValue(String.format(I18nProperties.getCaption(Captions.importErrors), failedImportsCount));
		} else if (result == ImportLineResult.SKIPPED) {
			skippedImportsCount++;
			skippedImportsLabel.setValue(String.format(I18nProperties.getCaption(Captions.importSkips), skippedImportsCount));
		} else if (result == ImportLineResult.DUPLICATE) {
			duplicateImportsCount++;
			duplicateImportsLabel.setValue(String.format(I18nProperties.getCaption(Captions.importDuplicates), duplicateImportsCount));
		}
	}

}
