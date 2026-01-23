package de.symeda.sormas.backend;

import java.util.Optional;
import java.util.function.Function;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Specializes;

import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.backend.systemconfiguration.ConfigFacadeEjb;

@Stateless
@LocalBean
@Specializes
public class ConfigFacadeEjbLocalMock extends ConfigFacadeEjb {

	public static final TestHelperConfigImpl testHelperConfig = new TestHelperConfigImpl();

	private <T> Optional<T> getTypedValueFor(Config config, Function<String, T> parsingFct) {
		return testHelperConfig.get(config).map(parsingFct);
	}

	@Override
	public boolean isPresent(Config config) {
		return testHelperConfig.isPresent(config);
	}

	@Override
	public Optional<Integer> getAsInteger(Config config) {
		return getTypedValueFor(config, Integer::parseInt);
	}

	@Override
	public Optional<Double> getAsDouble(Config config) {
		return getTypedValueFor(config, Double::parseDouble);
	}

	@Override
	public Optional<Long> getAsLong(Config config) {
		return getTypedValueFor(config, Long::parseLong);
	}

	@Override
	public Optional<String> getAsString(Config config) {
		return getTypedValueFor(config, Function.identity());
	}

	@Override
	public boolean getAsBoolean(Config config) {
		return getTypedValueFor(config, Boolean::parseBoolean).orElse(false);
	}

	@Override
	public String getCountryCode() {
		String normalizedLocale = normalizeLocaleStringStatic(getAsStringOrThrow(Config.COUNTRY_LOCALE));

		if (normalizedLocale.contains(COUNTRY_SEPARATION_CHAR)) {
			return normalizedLocale.substring(normalizedLocale.lastIndexOf(COUNTRY_SEPARATION_CHAR) + 1);
		}
		return normalizedLocale;
	}

}
