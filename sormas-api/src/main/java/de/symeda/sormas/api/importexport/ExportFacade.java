package de.symeda.sormas.api.importexport;

import java.io.IOException;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.ExportErrorException;

@Remote
public interface ExportFacade {

	/**
	 * Exports the passed database tables as .csv files to the export folder specified in the
	 * properties file, creates a zip archive containing these .csv files and returns the path
	 * to the zip archive that can then be used to offer it as a download.
	 */
	String generateDatabaseExportArchive(List<DatabaseTable> databaseTables) throws ExportErrorException, IOException;

	/**
	 * Exports the passed grid rows as a .csv file to the export folder specified in the
	 * properties file and returns the path that can then be used to offer it as a download.
	 */
	String generateGridExportCsv(List<List<String>> exportedRows, String filePrefix, String userUuid) throws IOException;
	
}
