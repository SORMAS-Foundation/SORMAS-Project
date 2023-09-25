/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.feature;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface FeatureConfigurationFacade {

	List<FeatureConfigurationDto> getAllAfter(Date date);

	List<FeatureConfigurationDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	List<String> getDeletedUuids(Date date);

	List<FeatureConfigurationIndexDto> getFeatureConfigurations(FeatureConfigurationCriteria criteria, boolean includeInactive);

	Map<Disease, List<FeatureConfigurationIndexDto>> getEnabledFeatureConfigurations(FeatureConfigurationCriteria criteria);

	Page<FeatureConfigurationIndexDto> getIndexPage(
		@NotNull FeatureConfigurationCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties);

	void saveFeatureConfigurations(@Valid Collection<FeatureConfigurationIndexDto> configurations, FeatureType featureType);

	void saveFeatureConfiguration(@Valid FeatureConfigurationIndexDto configuration, FeatureType featureType);

	void deleteAllFeatureConfigurations(FeatureConfigurationCriteria criteria);

	void deleteAllExpiredFeatureConfigurations(Date date);

	boolean isFeatureDisabled(FeatureType featureType);

	boolean isFeatureEnabled(FeatureType featureType);

	boolean isAnyFeatureEnabled(FeatureType... featureType);

	boolean areAllFeatureEnabled(FeatureType... featureType);

	boolean isFeatureEnabled(FeatureType featureType, DeletableEntityType entityType);

	<T extends Object> T getProperty(FeatureType featureType, DeletableEntityType entityType, FeatureTypeProperty property, Class<T> returnType);

	/**
	 * Checks whether the property of the specified feature type in the database equals to true.
	 * If the property is not defined in the database, does the check against the property's default instead.
	 */
	boolean isPropertyValueTrue(FeatureType featureType, FeatureTypeProperty property);

	boolean isAnySurveillanceEnabled();

	boolean isCountryEnabled();

	boolean isTaskGenerationFeatureEnabled(TaskType taskType);

	List<FeatureConfigurationDto> getActiveServerFeatureConfigurations();
}
