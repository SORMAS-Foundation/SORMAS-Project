package de.symeda.sormas.backend;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.systemconfiguration.Config;

public class TestHelperConfigImpl implements TestConfigFacade {

	Map<Config, String> properties = new ConcurrentHashMap<>();

	public boolean isPresent(Config config) {
		return properties.containsKey(config);
	}

	@Override
	public void set(Config config, String value) {
		properties.put(config, value);
	}

	@Override
	public Optional<String> get(Config config) {
		return Optional.ofNullable(properties.get(config));
	}

	@Override
	public void remove(Config config) {
		properties.remove(config);
	}

	public void clear() {
		properties.clear();
	}
}
