package de.symeda.sormas.backend;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;

import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.junit.cdi.jee.jpa.DatabaseCleaner;

/**
 * Cleanup database.
 *
 * <p>
 * Brute force implementation.
 * </p>
 */
public class TestDatabaseCleaner implements DatabaseCleaner {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void run(Connection connection) throws SQLException {

		if (isPostgres(connection)) {
			// The schema was built once from sormas_schema.sql when the container started and
			// must not be regenerated from the entities, so only the data is cleared. Emptying
			// every table matches what the H2 path leaves behind, which is what AbstractBeanTest
			// expects: it recreates disease configurations and users in init().
			truncateAllTables(connection);
			connection.commit();
			return;
		}

		// needed to drop h2 functions
		connection.prepareStatement("DROP ALL OBJECTS").execute();
		connection.commit();

		Map<String, String> properties = new HashMap<>();
		// TODO #11032 Switch to org.hibernate.tool.schema.Action.CREATE.getExternalJpaName());
		properties.put(AvailableSettings.HBM2DDL_DATABASE_ACTION, "create");
		Persistence.generateSchema(EntityManagerTestProducer.BEAN_TEST_PU, properties);
	}

	private static void truncateAllTables(Connection connection) throws SQLException {
		StringBuilder tables = new StringBuilder();
		try (Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(
				"SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename")) {
			while (rs.next()) {
				if (tables.length() > 0) {
					tables.append(", ");
				}
				tables.append('"').append(rs.getString(1)).append('"');
			}
		}

		if (tables.length() == 0) {
			return;
		}

		try (Statement statement = connection.createStatement()) {
			// versioning triggers would otherwise copy every deleted row into the history tables
			statement.execute("SET session_replication_role = replica");
			statement.execute("TRUNCATE TABLE " + tables + " RESTART IDENTITY CASCADE");
			statement.execute("SET session_replication_role = DEFAULT");
		}
	}

	private static boolean isPostgres(Connection connection) throws SQLException {
		return connection.getMetaData().getDatabaseProductName().toLowerCase().contains("postgres");
	}
}
