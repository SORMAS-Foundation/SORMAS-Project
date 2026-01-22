package de.symeda.sormas.backend;

import de.symeda.sormas.api.systemconfiguration.ConfigType;

public interface SystemConfigurationPropertiesFacade {

	void setProperty(ConfigType config, String value);
}
