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

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.InfrastructureImporter;

public class ImportDefaultSubcontinentsLayout extends AbstractImportDefaultCsvLayout {

	public ImportDefaultSubcontinentsLayout() {
		super();
	}

	@Override
	protected URI getImportFilePath() {
		return FacadeProvider.getImportFacade().getAllSubcontinentsImportFilePath();
	}

	@Override
	protected void doImport(File importFile) throws IOException, CsvValidationException {
		DataImporter importer = new InfrastructureImporter(
			importFile,
			currentUser,
			InfrastructureType.SUBCONTINENT,
			isAllowOverwrite(),
			(ValueSeparator) separator.getValue());
		importer.setCsvSeparator(',');
		importer.startImport(this::extendDownloadErrorReportButton, currentUI, false);
	}

	@Override
	protected String getHeadingImport() {
		return Strings.headingImportAllSubcontinents;
	}

	@Override
	protected String getInfoImport() {
		return Strings.infoImportAllSubcontinents;
	}
}
