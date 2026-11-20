/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.backend.event.sevenoneseven;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.sevenoneseven.Event717EventStatusesProvider;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationCategoryService;

final class Event717TestHelper {

	private Event717TestHelper() {
	}

	/**
	 * Allows new assessments for all selectable event statuses, because the test data creator creates signals by default.
	 */
	static void allowAllEventStatuses(AbstractBeanTest test) {
		setApplicableEventStatuses(test, Event717EventStatusesProvider.SELECTABLE_EVENT_STATUSES.toArray(new EventStatus[0]));
	}

	/**
	 * Saves the system configuration value of the event statuses for which a 7-1-7 assessment can be created. The value is cached
	 * by the system configuration bean, so every test sets it in its setup.
	 */
	static void setApplicableEventStatuses(AbstractBeanTest test, EventStatus... statuses) {

		SystemConfigurationValueFacade facade = test.getSystemConfigurationValueFacade();
		List<String> uuids = facade.getAllUuids();
		SystemConfigurationValueDto setting = uuids.isEmpty()
			? null
			: facade.getByUuids(uuids).stream().filter(v -> Event717EventStatusesProvider.CONFIG_KEY.equals(v.getKey())).findFirst().orElse(null);
		if (setting == null) {
			SystemConfigurationCategoryDto category = SystemConfigurationCategoryDto.build();
			category.setName(SystemConfigurationCategoryService.DEFAULT_CATEGORY_NAME);
			category.setCaption("General");
			setting = SystemConfigurationValueDto.build();
			setting.setKey(Event717EventStatusesProvider.CONFIG_KEY);
			setting.setCategory(
				test.getSystemConfigurationCategoryFacade()
					.getReferenceByUuid(test.getSystemConfigurationCategoryFacade().save(category).getUuid()));
			setting.setOptional(false);
			setting.setEncrypt(false);
		}
		setting.setValue(Event717EventStatusesProvider.toValue(EnumSet.copyOf(Arrays.asList(statuses))));
		facade.save(setting);
	}
}
