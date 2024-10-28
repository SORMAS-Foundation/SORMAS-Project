/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiExportDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;

public class AefiDownloadUtil {

	private AefiDownloadUtil() {
	}

	public static StreamResource createAefiExportResource(
		AefiCriteria aefiCriteria,
		Supplier<Collection<String>> selectedRows,
		ExportConfigurationDto exportConfiguration) {
		return DownloadUtil.createCsvExportStreamResource(
			AefiExportDto.class,
			null,
			(Integer start, Integer max) -> FacadeProvider.getAefiFacade().getExportList(aefiCriteria, selectedRows.get(), start, max),
			AefiDownloadUtil::captionProvider,
			ExportEntityName.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION,
			exportConfiguration);
	}

	public static String getPropertyCaption(String propertyId, String prefixId) {
		if (prefixId != null) {
			return I18nProperties.getPrefixCaption(prefixId, propertyId);
		}

		return I18nProperties.findPrefixCaption(propertyId, AefiExportDto.I18N_PREFIX, AefiIndexDto.I18N_PREFIX, AdverseEventsDto.I18N_PREFIX);
	}

	private static String captionProvider(String propertyId, Class<?> type) {
		String caption = getPropertyCaption(propertyId, null);

		if (Date.class.isAssignableFrom(type)) {
			caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
		}

		return caption;
	}
}
