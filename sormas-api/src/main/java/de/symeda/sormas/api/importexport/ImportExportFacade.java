package de.symeda.sormas.api.importexport;

import java.io.IOException;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.ExportErrorException;

@Remote
public interface ImportExportFacade {

	/**
	 * Exports the passed database tables as .csv files to the export folder specified in the
	 * properties file, creates a zip archive containing these .csv files and returns the path
	 * to the zip archive that can then be used to offer it as a download.
	 */
	String generateDatabaseExportArchive(List<DatabaseTable> databaseTables) throws ExportErrorException, IOException;
	
	/**
	 * Creates a .csv file with one row containing all relevant column names of the case entity
	 * and its sub-entities and returns the path to the .csv file that can then be used to offer
	 * it as a download.
	 */
	void generateCaseImportTemplateFile() throws IOException;
	
	String getCaseImportTemplateFilePath();
	
	String importCasesFromCsvFile(String csvFilePath, String userUuid) throws IOException, InvalidColumnException;
	
}
