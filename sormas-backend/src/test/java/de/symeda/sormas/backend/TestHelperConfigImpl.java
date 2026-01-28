package de.symeda.sormas.backend;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hilling.junit.cdi.ContextControlWrapper;
import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.backend.json.ObjectMapperProvider;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationValueEjb;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationValueProjection;

public class TestHelperConfigImpl implements TestConfigFacade {

	private static final Logger log = LoggerFactory.getLogger(TestHelperConfigImpl.class);

	private static volatile TestHelperConfigImpl instance;

	private static SystemConfigurationValueEjb systemConfigurationValueEjb;

	private TestHelperConfigImpl() {
		if (instance != null) {
			throw new IllegalStateException("ObjectMapper instance already created");
		}
	}

	public static TestHelperConfigImpl getInstance() {
		if (instance == null) {
			synchronized (ObjectMapperProvider.class) {
				if (instance == null) {
					instance = new TestHelperConfigImpl();
				}
			}
		}
		return instance;
	}

	@Override
	public void set(Config config, String value) {
		ConcurrentHashMap<Config, SystemConfigurationValueProjection> configurationValuesByKey =
			getSystemConfigurationValueEjb().getConfigurationValuesByKey();
		configurationValuesByKey.compute(
			config,
			(k, systemConfigurationValueProjection) -> new SystemConfigurationValueProjection(
				config,
				value,
				systemConfigurationValueProjection.getDefaultValue()));
	}

	private SystemConfigurationValueEjb getSystemConfigurationValueEjb() {
		if (systemConfigurationValueEjb == null) {
			systemConfigurationValueEjb = getBean(SystemConfigurationValueEjb.class);

			systemConfigurationValueEjb.loadDataIfEmpty();
		}

		return systemConfigurationValueEjb;
	}

	@Override
	public Optional<String> get(Config config) {
		return getBean(ConfigFacadeEjb.ConfigFacadeEjbLocal.class).getAsString(config);
	}

	@Override
	public void remove(Config config) {
		ConcurrentHashMap<Config, SystemConfigurationValueProjection> configurationValuesByKey =
			getSystemConfigurationValueEjb().getConfigurationValuesByKey();

		configurationValuesByKey.computeIfPresent(
			config,
			(k, systemConfigurationValueProjection) -> new SystemConfigurationValueProjection(
				config,
				null,
				systemConfigurationValueProjection.getDefaultValue()));
	}

	protected <T> T getBean(Class<T> beanClass, Annotation... qualifiers) {
		return ContextControlWrapper.getInstance().getContextualReference(beanClass, qualifiers);
	}
}
