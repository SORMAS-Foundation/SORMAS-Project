package de.symeda.sormas.backend;

import java.util.Optional;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.systemconfiguration.Config;

public class ConfigFacadeEjbLocalMock implements ConfigFacade {

	private TestHelperConfigImpl testHelperConfig;

	@Override
	public boolean isPresent(Config config) {
		return testHelperConfig.isPresent(config);
	}

	@Override
	public Optional<Integer> getAsInteger(Config config) {
		return Optional.empty();
	}

	@Override
	public Optional<Double> getAsDouble(Config config) {
		return Optional.empty();
	}

	@Override
	public Optional<Long> getAsLong(Config config) {
		return Optional.empty();
	}

	@Override
	public Optional<String> getAsString(Config config) {
		return Optional.empty();
	}

	@Override
	public boolean getAsBoolean(Config config) {
		return false;
	}

	@Override
	public boolean isConfiguredCountry(String countryCode) {
		return false;
	}

	@Override
	public String getCountryCode() {
		return "";
	}
}
