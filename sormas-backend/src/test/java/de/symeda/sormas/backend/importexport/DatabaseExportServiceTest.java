package de.symeda.sormas.backend.importexport;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.symeda.sormas.api.importexport.DatabaseTable;

/**
 * @see DatabaseExportService
 * @author Stefan Kock
 */
public class DatabaseExportServiceTest {

	/**
	 * Assure, that every {@link DatabaseTable} has an export configuration defined.
	 */
	@Test
	public void testGetConfigFullyDefined() {

		for (DatabaseTable databaseTable : DatabaseTable.values()) {
			DatabaseExportConfiguration config = DatabaseExportService.getConfig(databaseTable);
			assertNotNull(
				String.format("No export configuration defined for %s.%s", DatabaseTable.class.getSimpleName(), databaseTable.name()),
				config);
		}
	}
}
