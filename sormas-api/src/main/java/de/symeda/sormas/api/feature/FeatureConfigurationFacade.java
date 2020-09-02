package de.symeda.sormas.api.feature;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.task.TaskType;

@Remote
public interface FeatureConfigurationFacade {

	List<FeatureConfigurationDto> getAllAfter(Date date);

	List<FeatureConfigurationDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	List<String> getDeletedUuids(Date date);

	List<FeatureConfigurationIndexDto> getFeatureConfigurations(FeatureConfigurationCriteria criteria, boolean includeInactive);

	void saveFeatureConfigurations(Collection<FeatureConfigurationIndexDto> configurations, FeatureType featureType);

	void saveFeatureConfiguration(FeatureConfigurationIndexDto configuration, FeatureType featureType);

	void deleteAllFeatureConfigurations(FeatureConfigurationCriteria criteria);

	void deleteAllExpiredFeatureConfigurations(Date date);

	boolean isFeatureDisabled(FeatureType featureType);

	boolean isFeatureEnabled(FeatureType featureType);

	boolean isTaskGenerationFeatureEnabled(TaskType taskType);
}
