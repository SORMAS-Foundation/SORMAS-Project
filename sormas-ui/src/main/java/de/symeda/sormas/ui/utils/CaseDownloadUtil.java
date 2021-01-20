/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.Date;

import com.vaadin.server.StreamResource;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;

public class CaseDownloadUtil {

	public static StreamResource createCaseExportResource(
		CaseCriteria criteria,
		CaseExportType exportType,
		ExportConfigurationDto exportConfiguration) {

		return DownloadUtil.createCsvExportStreamResource(
			CaseExportDto.class,
			exportType,
			(Integer start, Integer max) -> FacadeProvider.getCaseFacade()
				.getExportList(criteria, exportType, start, max, exportConfiguration, I18nProperties.getUserLanguage()),
			CaseDownloadUtil::captionProvider,
			DownloadUtil.createFileNameWithCurrentDate("sormas_cases_", ".csv"),
			exportConfiguration);
	}

	public static String getPropertyCaption(String propertyId) {

		return I18nProperties.findPrefixCaption(
			propertyId,
			CaseExportDto.I18N_PREFIX,
			CaseDataDto.I18N_PREFIX,
			PersonDto.I18N_PREFIX,
			LocationDto.I18N_PREFIX,
			SymptomsDto.I18N_PREFIX,
			EpiDataDto.I18N_PREFIX,
			HospitalizationDto.I18N_PREFIX);
	}

	private static String captionProvider(String propertyId, Class<?> type) {
		String caption = getPropertyCaption(propertyId);

		if (Date.class.isAssignableFrom(type)) {
			caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
		}

		return caption;
	}
}
