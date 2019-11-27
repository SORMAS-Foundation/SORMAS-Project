package de.symeda.sormas.api.feature;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface FeatureConfigurationFacade {

	List<FeatureConfigurationIndexDto> getFeatureConfigurations(FeatureConfigurationCriteria criteria, boolean includeInactive);
	
}
