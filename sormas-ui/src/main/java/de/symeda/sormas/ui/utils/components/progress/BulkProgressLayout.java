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

import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class BulkProgressLayout extends AbstractProgressLayout<BulkProgressUpdateInfo> {

	private Label successfulEntriesLabel;
	private int successfulEntriesCount;
	private Label skippedEntriesLabel;
	private int skippedEntriesCount;

	public BulkProgressLayout(UI currentUI, int totalCount, Runnable cancelCallback) {
		super(currentUI, String.format(I18nProperties.getString(Strings.infoBulkProcess), totalCount), totalCount, null, cancelCallback);
		initAdditionalProgressStatusComponents();
	}

	protected void initAdditionalProgressStatusComponents() {

		successfulEntriesLabel = new Label(String.format(I18nProperties.getCaption(Captions.bulkSuccessful), 0));
		CssStyles.style(successfulEntriesLabel, CssStyles.LABEL_POSITIVE);
		progressStatusLayout.addComponent(successfulEntriesLabel);
		skippedEntriesLabel = new Label(String.format(I18nProperties.getCaption(Captions.bulkSkipped), 0));
		CssStyles.style(skippedEntriesLabel, CssStyles.LABEL_MINOR);
		progressStatusLayout.addComponent(skippedEntriesLabel);
	}

	@Override
	protected String getProcessedLabelI18nProperty() {
		return I18nProperties.getCaption(Captions.bulkCompleted);
	}

	@Override
	protected void handleOptionalProgressUpdates(BulkProgressUpdateInfo progressUpdateInfo) {

		successfulEntriesCount += progressUpdateInfo.getSuccessfulEntryCount();
		successfulEntriesLabel.setValue(String.format(I18nProperties.getCaption(Captions.bulkSuccessful), successfulEntriesCount));
		skippedEntriesCount += progressUpdateInfo.getSkippedEntryCount();
		skippedEntriesLabel.setValue(String.format(I18nProperties.getCaption(Captions.bulkSkipped), skippedEntriesCount));
	}

	@Override
	protected String getHintText() {
		return I18nProperties.getString(Strings.infoBulkUnresponsiveWindowHint);
	}
}
