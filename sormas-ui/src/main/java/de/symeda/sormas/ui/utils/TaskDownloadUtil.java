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

package de.symeda.sormas.ui.utils;

import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;

import com.vaadin.server.StreamResource;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskExportDto;

public class TaskDownloadUtil {

	private TaskDownloadUtil() {
		// Hide Utility Class Constructor
	}

	public static StreamResource createTaskExportResource(
		TaskCriteria taskCriteria,
		Supplier<Collection<String>> selectedRows,
		ExportConfigurationDto exportConfiguration) {
		return DownloadUtil.createCsvExportStreamResource(
			TaskExportDto.class,
			null,
			(Integer start, Integer max) -> FacadeProvider.getTaskFacade().getExportList(taskCriteria, selectedRows.get(), start, max),
			TaskDownloadUtil::captionProvider,
			ExportEntityName.TASKS,
			exportConfiguration);
	}

	public static String getPropertyCaption(String propertyId, String prefixId) {
		if (prefixId != null) {
			return I18nProperties.getPrefixCaption(prefixId, propertyId);
		}
		return I18nProperties.findPrefixCaption(propertyId, TaskExportDto.I18N_PREFIX, TaskDto.I18N_PREFIX);
	}

	private static String captionProvider(String propertyId, Class<?> type) {
		String caption = getPropertyCaption(propertyId, null);

		if (Date.class.isAssignableFrom(type)) {
			caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
		}

		return caption;
	}
}
