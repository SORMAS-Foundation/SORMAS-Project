package de.symeda.sormas.backend;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.symeda.sormas.api.systemconfiguration.Config;

public class TestHelperConfigImpl implements TestConfigFacade {

	Map<Config, String> properties = new ConcurrentHashMap<>();

	@Override
	public void setProperty(Config config, String value) {
		properties.put(config, value);
	}

	@Override
	public String getProperty(Config config) {
		return properties.get(config);
	}

	@Override
	public void remove(Config config) {
		properties.remove(config);
	}

	public void clear() {
		properties.clear();
	}
}
