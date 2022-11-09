package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.ui.CheckBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.ui.caze.importer.CountryImporter;

@SuppressWarnings("serial")
public class ImportDefaultCountriesLayout extends AbstractImportDefaultCsvLayout {

	private CheckBox allowOverwrite;

	public ImportDefaultCountriesLayout() {
		super();
	}

	@Override
	protected URI getImportFilePath() {
		return FacadeProvider.getImportFacade().getAllCountriesImportFilePath();
	}

	@Override
	protected void doImport(File importFile) throws IOException, CsvValidationException {
		CountryImporter importer = new CountryImporter(importFile, currentUser, isAllowOverwrite(), (ValueSeparator) separator.getValue());
		importer.setCsvSeparator(',');
		importer.startImport(this::extendDownloadErrorReportButton, currentUI);
	}

	@Override
	protected String getHeadingImport() {
		return Strings.infoImportAllCountries;
	}

	@Override
	protected String getInfoImport() {
		return Strings.headingImportAllCountries;
	}
}
