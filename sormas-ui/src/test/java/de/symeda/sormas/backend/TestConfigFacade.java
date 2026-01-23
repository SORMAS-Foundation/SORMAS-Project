package de.symeda.sormas.backend;

import de.symeda.sormas.api.systemconfiguration.Config;

/**
 * Partially matches the {@link java.util.Properties}-API to be a drop-in replacement and avoids to rewrite too much code.
 */
public interface TestConfigFacade {

	void setProperty(Config config, String value);

	String getProperty(Config config);

	void remove(Config config);
}
