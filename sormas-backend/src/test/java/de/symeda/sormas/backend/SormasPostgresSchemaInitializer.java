/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.backend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes the test PostgreSQL container with migrated SORMAS schema scripts.
 *
 * <p>
 * Used as Testcontainers {@code TC_INITFUNCTION}; keeps tests aligned with production schema
 * behavior instead of Hibernate-generated DDL.
 */
public final class SormasPostgresSchemaInitializer {

	private static final Logger logger = LoggerFactory.getLogger(SormasPostgresSchemaInitializer.class);

	private static final String VERSIONING_FUNCTION = "sql/temporal_tables/versioning_function.sql";
	private static final String SORMAS_SCHEMA = "sql/sormas_schema.sql";

	private SormasPostgresSchemaInitializer() {
	}

	/**
	 * Testcontainers entry point; executed once per container.
	 */
	public static void initialize(Connection connection) throws SQLException {

		logger.info("Preparing SORMAS schema in the test container");

		// pg_stat_statements is deliberately omitted: it needs shared_preload_libraries and is
		// only used for monitoring
		execute(
			connection,
			"CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql",
			"CREATE EXTENSION IF NOT EXISTS pg_trgm",
			"CREATE EXTENSION IF NOT EXISTS pgcrypto",
			"CREATE EXTENSION IF NOT EXISTS unaccent",
			// sormas_schema.sql assigns table ownership to sormas_user; the container's own
			// bootstrap user is whatever Testcontainers chose, so the role has to exist first
			"DO $$ BEGIN " + "IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'sormas_user') THEN "
				+ "CREATE ROLE sormas_user LOGIN PASSWORD 'sormas_user'; " + "END IF; END $$",
			"GRANT ALL ON SCHEMA public TO sormas_user");

		runScript(connection, VERSIONING_FUNCTION);
		runScript(connection, SORMAS_SCHEMA);

		logger.info("SORMAS schema ready");
	}

	private static void execute(Connection connection, String... statements) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			for (String sql : statements) {
				statement.execute(sql);
			}
		}
	}

	private static void runScript(Connection connection, String classpathResource) throws SQLException {
		String script = read(classpathResource);
		try (Statement statement = connection.createStatement()) {
			// the scripts contain function bodies with embedded semicolons, so they are handed to
			// the server whole rather than split on ';'
			statement.execute(script);
		} catch (SQLException e) {
			throw new SQLException("Failed to apply " + classpathResource + ": " + e.getMessage(), e);
		}
	}

	private static String read(String classpathResource) throws SQLException {
		try (InputStream in = SormasPostgresSchemaInitializer.class.getClassLoader().getResourceAsStream(classpathResource)) {
			if (in == null) {
				throw new SQLException("Not on the test classpath: " + classpathResource);
			}
			try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
				return scanner.useDelimiter("\\A").next();
			}
		} catch (IOException e) {
			throw new SQLException("Could not read " + classpathResource, e);
		}
	}
}
