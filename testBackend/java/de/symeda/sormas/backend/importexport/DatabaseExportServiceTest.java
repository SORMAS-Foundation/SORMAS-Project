package de.symeda.sormas.backend.importexport;

import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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

			assertThat(config.getTableName(), not(isEmptyString()));

			if (config.isUseJoinTable()) {
				assertThat(config.getJoinTableName(), not(isEmptyString()));
				assertThat(config.getColumnName(), not(isEmptyString()));
				assertThat(config.getJoinColumnName(), not(isEmptyString()));
			}
		}
	}
}
