/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.utils;

import java.util.Date;

import com.vaadin.server.StreamResource;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantExportDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;

public class EventParticipantDownloadUtil {

	public static StreamResource createExtendedEventParticipantExportResource(EventParticipantCriteria criteria) {

		return DownloadUtil.createCsvExportStreamResource(
			EventParticipantExportDto.class,
			null,
			(Integer start, Integer max) -> FacadeProvider.getEventParticipantFacade()
				.getExportList(criteria, start, max, I18nProperties.getUserLanguage()),
			EventParticipantDownloadUtil::captionProvider,
			ExportEntityName.EVENT_PARTICIPANTS,
			null);
	}

	private static String captionProvider(String propertyId, Class<?> type) {
		String caption = I18nProperties.findPrefixCaption(
			propertyId,
			EventDto.I18N_PREFIX,
			PersonDto.I18N_PREFIX,
			EventParticipantExportDto.I18N_PREFIX,
			EventParticipantDto.I18N_PREFIX,
			CaseDataDto.I18N_PREFIX,
			LocationDto.I18N_PREFIX);
		if (Date.class.isAssignableFrom(type)) {
			caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
		}

		return caption;
	}

}
