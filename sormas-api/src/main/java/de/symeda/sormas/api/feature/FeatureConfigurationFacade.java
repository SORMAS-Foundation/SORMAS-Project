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

	/**
	 * Enables or disables a server-level feature (one with no region/district/disease scope), seeding the
	 * configuration row if it does not exist yet. Intended for the administrator feature-configuration view.
	 */
	void setServerFeatureEnabled(FeatureType featureType, boolean enabled);

	/**
	 * Returns the current values of the supported properties of a server-level feature, intended for the administrator
	 * feature-configuration view. Every supported property is contained: the stored value if present and of the
	 * property's return type, otherwise the default declared on the {@link FeatureType}.
	 * <p>
	 * Example: {@code (AUTOMATIC_ARCHIVING, CASE)} with a stored threshold of 180 gives {@code {THRESHOLD_IN_DAYS=180}};
	 * {@code (TASK_MANAGEMENT, null)} with nothing stored gives {@code {ALLOW_FREE_EDITING=false}};
	 * {@code (CAMPAIGNS, null)} gives an empty map.
	 *
	 * @param featureType
	 *            the server feature
	 * @param entityType
	 *            one of {@link FeatureType#getEntityTypes()} for features configured per entity type, otherwise {@code null}
	 * @return a modifiable map, empty when the feature is not a server feature, supports no properties or the entity type does
	 *         not fit the feature
	 */
	Map<FeatureTypeProperty, Object> getServerFeatureProperties(FeatureType featureType, DeletableEntityType entityType);

	/**
	 * Stores property values of a server-level feature, seeding the configuration row if it does not exist yet. Intended
	 * for the administrator feature-configuration view. Properties that are not supported by the feature or whose value
	 * does not match {@link FeatureTypeProperty#getReturnType()} are skipped; all other stored properties are kept.
	 * <p>
	 * Example: {@code (AUTOMATIC_ARCHIVING, CASE, {THRESHOLD_IN_DAYS=180})} changes only the case archiving threshold;
	 * {@code (TASK_MANAGEMENT, null, {ALLOW_FREE_EDITING="yes"})} changes nothing because the value is no {@link Boolean}.
	 *
	 * @param featureType
	 *            the server feature
	 * @param entityType
	 *            one of {@link FeatureType#getEntityTypes()} for features configured per entity type, otherwise {@code null}
	 * @param properties
	 *            the property values to store
	 */
	void setServerFeatureProperties(FeatureType featureType, DeletableEntityType entityType, Map<FeatureTypeProperty, Object> properties);

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
