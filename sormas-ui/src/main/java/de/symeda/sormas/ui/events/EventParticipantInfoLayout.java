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
package de.symeda.sormas.ui.events;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class EventParticipantInfoLayout extends HorizontalLayout {

	private final EventParticipantDto eventParticipantDto;
	private final EventDto eventDto;

	public EventParticipantInfoLayout(EventParticipantDto eventParticipantDto, EventDto eventDto) {
		this.eventParticipantDto = eventParticipantDto;
		this.eventDto = eventDto;

		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);
		updateEventParticipantInfo();
	}

	private void updateEventParticipantInfo() {

		this.removeAllComponents();

		final VerticalLayout firstColumn = new VerticalLayout();
		firstColumn.setMargin(false);
		firstColumn.setSpacing(true);

		addDescLabel(firstColumn, DataHelper.getShortUuid(eventDto.getUuid()), I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.UUID))
			.setDescription(eventDto.getUuid());
		addDescLabel(firstColumn, eventDto.getDisease(), I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.DISEASE));
		addDescLabel(
			firstColumn,
			DateFormatHelper.formatDate(eventDto.getEventDate()),
			I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.EVENT_DATE));

		this.addComponent(firstColumn);

		final VerticalLayout secondColumn = new VerticalLayout();
		secondColumn.setMargin(false);
		secondColumn.setSpacing(true);

		final PersonDto personDto = eventParticipantDto.getPerson();
		addDescLabel(
			secondColumn,
			DataHelper.getShortUuid(personDto.getUuid()),
			I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.UUID)).setDescription(personDto.getUuid());

		addDescLabel(secondColumn, personDto, I18nProperties.getPrefixCaption(EventParticipantDto.I18N_PREFIX, EventParticipantDto.PERSON));

		final HorizontalLayout ageSexRow = new HorizontalLayout();
		addDescLabel(
			ageSexRow,
			ApproximateAgeType.ApproximateAgeHelper.formatApproximateAge(personDto.getApproximateAge(), personDto.getApproximateAgeType()),
			I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
		addDescLabel(ageSexRow, personDto.getSex(), I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));

		secondColumn.addComponent(ageSexRow);

		this.addComponent(secondColumn);
	}

	private static Label addDescLabel(AbstractLayout layout, Object content, String caption) {

		String contentString = content != null ? content.toString() : "";
		Label label = new Label(contentString);
		label.setCaption(caption);
		layout.addComponent(label);
		return label;
	}
}
