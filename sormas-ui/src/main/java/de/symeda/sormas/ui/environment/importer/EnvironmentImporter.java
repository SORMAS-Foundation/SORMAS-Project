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

package de.symeda.sormas.ui.environment.importer;

import java.io.File;
import java.io.IOException;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentImportFacade;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportLineResult;

public class EnvironmentImporter extends DataImporter {

	private final EnvironmentImportFacade environmentImportFacade;

	public EnvironmentImporter(File inputFile, boolean hasEntityClassRow, UserDto currentUser, ValueSeparator csvSeparator) throws IOException {
		super(inputFile, hasEntityClassRow, currentUser, csvSeparator);

		environmentImportFacade = FacadeProvider.getEnvironmentImportFacade();
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException, InterruptedException {

		ImportLineResultDto<EnvironmentDto> importResult =
			environmentImportFacade.importEnvironmentData(values, entityClasses, entityProperties, entityPropertyPaths, !firstLine);

		if (importResult.isError()) {
			writeImportError(values, importResult.getMessage());
			return ImportLineResult.ERROR;
		} else if (importResult.isDuplicate()) {
			writeImportError(values, importResult.getMessage());
			return ImportLineResult.DUPLICATE;
		}

		return ImportLineResult.SUCCESS;
	}
}
