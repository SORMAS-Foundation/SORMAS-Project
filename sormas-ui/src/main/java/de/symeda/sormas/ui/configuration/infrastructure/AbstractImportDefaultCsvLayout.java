/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;

public abstract class AbstractImportDefaultCsvLayout extends AbstractImportLayout {

	private CheckBox allowOverwrite;

	public AbstractImportDefaultCsvLayout() {
		super();

		addImportDefaultSubcontinentsCsvComponent(1, (event) -> {
			URI subcontinentsFileUri = getImportFilePath();
			File subcontinentsFile = Paths.get(subcontinentsFileUri).toFile();
			resetDownloadErrorReportButton();
			try {
				doImport(subcontinentsFile);
			} catch (IOException | CsvValidationException e) {
				new Notification(
					I18nProperties.getString(Strings.headingImportFailed),
					I18nProperties.getString(Strings.messageImportFailed),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		});

		addDownloadErrorReportComponent(2);
	}

	protected abstract void doImport(File subcontinentsFile) throws IOException, CsvValidationException;

	protected abstract URI getImportFilePath();

	protected abstract String getHeadingImport();

	protected abstract String getInfoImport();

	protected void addImportDefaultSubcontinentsCsvComponent(int step, Button.ClickListener clickListener) {
		String headline = I18nProperties.getString(getHeadingImport());
		String infoText = I18nProperties.getString(getInfoImport());
		ImportLayoutComponent importCsvComponent = new ImportLayoutComponent(
			step,
			headline,
			infoText,
			null,
			I18nProperties.getCaption(Captions.actionImport),
			I18nProperties.getCaption(Captions.infrastructureImportAllowOverwrite),
			I18nProperties.getString(Strings.infoImportInfrastructureAllowOverwrite));
		importCsvComponent.getButton().addClickListener(clickListener);
		allowOverwrite = importCsvComponent.getCheckbox();
		addComponent(importCsvComponent);
	}

	protected boolean isAllowOverwrite() {
		return allowOverwrite != null && allowOverwrite.getValue();
	}
}
