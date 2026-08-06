/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.common.cron;

import java.util.List;

import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryReferenceDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;

class CronTestConfigurationHelper {

	private static final String CRON_CATEGORY_NAME = "CRON";

	private final SystemConfigurationValueFacade configurationValueFacade;
	private final SystemConfigurationCategoryFacade categoryFacade;

	CronTestConfigurationHelper(SystemConfigurationValueFacade configurationValueFacade, SystemConfigurationCategoryFacade categoryFacade) {
		this.configurationValueFacade = configurationValueFacade;
		this.categoryFacade = categoryFacade;
	}

	SystemConfigurationValueDto setConfigurationValue(CronJob job, String expression) {

		SystemConfigurationValueDto value = getOrBuildConfigurationValue(job);
		value.setValue(expression);
		return configurationValueFacade.save(value);
	}

	SystemConfigurationValueDto getOrBuildConfigurationValue(CronJob job) {

		List<String> existingUuids = configurationValueFacade.getAllUuids();
		return existingUuids.isEmpty()
			? buildConfigurationValue(job)
			: configurationValueFacade.getByUuids(existingUuids)
				.stream()
				.filter(candidate -> job.getConfigKey().equals(candidate.getKey()))
				.findFirst()
				.orElseGet(() -> buildConfigurationValue(job));
	}

	SystemConfigurationValueDto buildConfigurationValue(CronJob job) {

		SystemConfigurationValueDto value = SystemConfigurationValueDto.build();
		value.setKey(job.getConfigKey());
		value.setCategory(cronCategoryReference());
		value.setOptional(true);
		value.setEncrypt(false);
		value.setPattern(CronExpressionValidator.VALUE_PATTERN);
		return value;
	}

	SystemConfigurationCategoryReferenceDto cronCategoryReference() {

		SystemConfigurationCategoryReferenceDto existing = categoryFacade.getCategoryReferenceDtoByName(CRON_CATEGORY_NAME);
		if (existing != null) {
			return existing;
		}

		SystemConfigurationCategoryDto category = SystemConfigurationCategoryDto.build();
		category.setName(CRON_CATEGORY_NAME);
		category.setCaption("Scheduled jobs");
		return categoryFacade.getReferenceByUuid(categoryFacade.save(category).getUuid());
	}
}
