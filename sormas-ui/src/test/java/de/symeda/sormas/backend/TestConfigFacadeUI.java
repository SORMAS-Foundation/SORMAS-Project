package de.symeda.sormas.backend;

import java.util.Optional;

import de.symeda.sormas.api.systemconfiguration.Config;

/**
 * Partially matches the {@link java.util.Properties}-API to be a drop-in replacement and avoids to rewrite too much code.
 */
public interface TestConfigFacadeUI {

	default void setProperty(Config config, String value) {
		set(config, value);
	}

	void set(Config config, String value);

	Optional<String> get(Config config);

	default String getProperty(Config config) {
		return get(config).orElse(null);
	}

	void remove(Config config);
}
