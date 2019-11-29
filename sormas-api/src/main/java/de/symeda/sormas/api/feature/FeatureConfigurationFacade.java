package de.symeda.sormas.api.feature;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface FeatureConfigurationFacade {

	List<FeatureConfigurationIndexDto> getFeatureConfigurations(FeatureConfigurationCriteria criteria, boolean includeInactive);
	
	void saveFeatureConfiguration(FeatureConfigurationIndexDto configuration, FeatureType featureType);
	
	void deleteAllFeatureConfigurations(FeatureConfigurationCriteria criteria);
	
	void deleteAllExpiredFeatureConfigurations(Date date);
	
}
