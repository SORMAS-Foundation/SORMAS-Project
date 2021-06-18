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

package de.symeda.sormas.ui.events;

import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

public class EventParticipantCaseConversionSelectionGrid extends EventParticipantSelectionGrid {

	public EventParticipantCaseConversionSelectionGrid(List<SimilarEventParticipantDto> eventParticipants) {
		super(eventParticipants);
	}

	@Override
	protected void setColumns() {
		setColumns(
			SimilarEventParticipantDto.UUID,
			SimilarEventParticipantDto.INVOLVEMENT_DESCRIPTION,
			SimilarEventParticipantDto.EVENT_UUID,
			SimilarEventParticipantDto.EVENT_STATUS,
			SimilarEventParticipantDto.EVENT_TITLE,
			SimilarEventParticipantDto.START_DATE);

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.findPrefixCaption(
					column.getPropertyId().toString(),
					SimilarEventParticipantDto.I18N_PREFIX,
					SimilarPersonDto.I18N_PREFIX,
					EventDto.I18N_PREFIX));
		}

		getColumn(SimilarEventParticipantDto.UUID).setRenderer(new V7UuidRenderer());
		getColumn(SimilarEventParticipantDto.EVENT_UUID).setRenderer(new V7UuidRenderer());
	}
}
