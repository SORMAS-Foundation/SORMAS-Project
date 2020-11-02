package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;
import de.symeda.sormas.ui.importer.InfrastructureImporter;

@SuppressWarnings("serial")
public class ImportAllCountriesLayout extends AbstractImportLayout {

	public ImportAllCountriesLayout() {
		super();

		addImportAllCountriesCsvComponent(1, (event) -> {
			URI countriesFileUri = FacadeProvider.getImportFacade().getAllCountriesImportFilePath();
			File countriesFile = Paths.get(countriesFileUri).toFile();
			resetDownloadErrorReportButton();
			try {
				DataImporter importer = new InfrastructureImporter(countriesFile, currentUser, InfrastructureType.COUNTRY);
				importer.startImport(this::extendDownloadErrorReportButton, currentUI, true);
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

	protected void addImportAllCountriesCsvComponent(int step, Button.ClickListener clickListener) {
		String headline = I18nProperties.getString(Strings.headingImportAllCountries);
		String infoText = I18nProperties.getString(Strings.infoImportAllCountries);
		ImportLayoutComponent importCsvComponent = new ImportLayoutComponent(step, headline, infoText, null, I18nProperties.getCaption(Captions.actionImport));
		importCsvComponent.getButton().addClickListener(clickListener);
		addComponent(importCsvComponent);
	}
}
