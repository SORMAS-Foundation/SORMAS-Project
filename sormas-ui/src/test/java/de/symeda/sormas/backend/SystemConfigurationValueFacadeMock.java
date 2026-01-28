package de.symeda.sormas.backend;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.inject.Specializes;

import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationValueEjb;

/**
 * Mock for UI-tests as now DB is started
 */
@Stateless
@Specializes
public class SystemConfigurationValueFacadeMock extends SystemConfigurationValueEjb {

	static TestHelperConfigImpl testHelperConfig = new TestHelperConfigImpl();

	@Override
	public Optional<String> getValue(Config key) {
		return testHelperConfig.get(key);
	}
}
