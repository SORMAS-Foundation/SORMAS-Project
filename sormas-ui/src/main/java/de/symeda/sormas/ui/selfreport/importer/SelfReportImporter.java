package de.symeda.sormas.ui.selfreport.importer;

import java.io.File;
import java.io.IOException;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportImportFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportLineResult;

public class SelfReportImporter extends DataImporter {

	private SelfReportImportFacade selfReportImportFacade;

	public SelfReportImporter(File inputFile, boolean hasEntityClassRow, UserDto currentUser, ValueSeparator csvSeparator) throws IOException {
		super(inputFile, hasEntityClassRow, currentUser, csvSeparator);

		selfReportImportFacade = FacadeProvider.getSelfReportImportFacade();
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException, InterruptedException {

		ImportLineResultDto<SelfReportDto> importResult =
			selfReportImportFacade.importSelfReportData(values, entityClasses, entityProperties, entityPropertyPaths, !firstLine);

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
