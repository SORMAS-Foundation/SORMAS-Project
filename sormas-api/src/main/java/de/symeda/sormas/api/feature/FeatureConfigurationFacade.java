package de.symeda.sormas.api.feature;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface FeatureConfigurationFacade {
	
	List<FeatureConfigurationDto> getAllAfter(Date date, String userUuid);

	List<FeatureConfigurationDto> getByUuids(List<String> uuids);
	
	List<String> getAllUuids(String userUuid);

	List<String> getDeletedUuids(Date date, String userUuid);

	List<FeatureConfigurationIndexDto> getFeatureConfigurations(FeatureConfigurationCriteria criteria, boolean includeInactive);
	
	void saveFeatureConfigurations(Collection<FeatureConfigurationIndexDto> configurations, FeatureType featureType);
	
	void saveFeatureConfiguration(FeatureConfigurationIndexDto configuration, FeatureType featureType);
	
	void deleteAllFeatureConfigurations(FeatureConfigurationCriteria criteria);
	
	void deleteAllExpiredFeatureConfigurations(Date date);
	
}
