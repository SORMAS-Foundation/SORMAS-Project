/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventParticipantEditForm extends AbstractEditForm<EventParticipantDto> {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT = fluidRowLocs(EventParticipantDto.REGION, EventParticipantDto.DISTRICT)
		+ fluidRowLocs(EventParticipantDto.REPORTING_USER)
		+ fluidRowLocs(EventParticipantDto.INVOLVEMENT_DESCRIPTION)
		+ fluidRowLocs(EventParticipantDto.PERSON);

	private final EventDto event;

	private final boolean isPseudonymized;

	public EventParticipantEditForm(EventDto event, boolean isPseudonymized) {
		super(
			EventParticipantDto.class,
			EventParticipantDto.I18N_PREFIX,
			false,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.getDefault(isPseudonymized));
		this.event = event;
		this.isPseudonymized = isPseudonymized;

		if (event == null) {
			throw new IllegalArgumentException("Event cannot be null");
		}
		addFields();
	}

	@Override
	protected void addFields() {
		if (event == null) {
			// workaround to stop initialization until event is set
			return;
		}

		PersonEditForm pef =
			new PersonEditForm(PersonContext.EVENT_PARTICIPANT, event.getDisease(), event.getDiseaseDetails(), null, isPseudonymized);
		pef.setWidth(100, Unit.PERCENTAGE);
		pef.setImmediate(true);
		getFieldGroup().bind(pef, EventParticipantDto.PERSON);
		getContent().addComponent(pef, EventParticipantDto.PERSON);

		addField(EventParticipantDto.INVOLVEMENT_DESCRIPTION, TextField.class);
		setRequired(true, EventParticipantDto.INVOLVEMENT_DESCRIPTION);

		ComboBox region = addInfrastructureField(EventParticipantDto.REGION);
		region.setDescription(I18nProperties.getPrefixDescription(EventParticipantDto.I18N_PREFIX, EventParticipantDto.REGION));
		ComboBox district = addInfrastructureField(EventParticipantDto.DISTRICT);
		district.setDescription(I18nProperties.getPrefixDescription(EventParticipantDto.I18N_PREFIX, EventParticipantDto.DISTRICT));

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();

			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});

		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		addField(EventParticipantDto.REPORTING_USER, ComboBox.class);
		setReadOnly(true, EventParticipantDto.REPORTING_USER);

		initializeAccessAndAllowedAccesses();
	}

	public String getPersonFirstName() {
		return (String) getField(PersonDto.FIRST_NAME).getValue();
	}

	public String getPersonLastName() {
		return (String) getField(PersonDto.LAST_NAME).getValue();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
