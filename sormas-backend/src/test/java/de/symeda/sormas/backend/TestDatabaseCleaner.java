package de.symeda.sormas.backend;

import java.sql.Connection;
import java.sql.SQLException;
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

		// needed to drop h2 functions
		connection.prepareStatement("DROP ALL OBJECTS").execute();
		connection.commit();

		Map<String, String> properties = new HashMap<>();
		properties.put(AvailableSettings.HBM2DDL_DATABASE_ACTION, "create");//Action.CREATE.getExternalJpaName());
		Persistence.generateSchema(EntityManagerTestProducer.BEAN_TEST_PU, properties);
	}
}
