package de.symeda.sormas.api.importexport;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.ejb.Remote;

@Remote
public interface ImportFacade {

	/**
	 * Creates a .csv file with one row containing all relevant column names of the case entity
	 * and its sub-entities and returns the path to the .csv file that can then be used to offer
	 * it as a download.
	 */
	void generateCaseImportTemplateFile() throws IOException;
	
	String getCaseImportTemplateFilePath();
	
	String importCasesFromCsvFile(String csvFilePath, String userUuid) throws IOException, InvalidColumnException;
	
	boolean importCasesFromCsvFile(Reader reader, Writer writer, String userUuid) throws IOException, InvalidColumnException;
	
}
