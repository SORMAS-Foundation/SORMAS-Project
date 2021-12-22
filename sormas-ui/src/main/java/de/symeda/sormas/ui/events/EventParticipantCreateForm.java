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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.oneOfTwoCol;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventParticipantCreateForm extends AbstractEditForm<EventParticipantDto> {

	private static final long serialVersionUID = 1L;

	private static final String FIRST_NAME = PersonDto.FIRST_NAME;
	private static final String LAST_NAME = PersonDto.LAST_NAME;
	private static final String PERSON_SEX = PersonDto.SEX;

	private static final String HTML_LAYOUT = fluidRowLocs(EventParticipantDto.INVOLVEMENT_DESCRIPTION)
		+ fluidRowLocs(FIRST_NAME, LAST_NAME)
		+ fluidRow(oneOfTwoCol(PERSON_SEX))
		+ fluidRowLocs(EventParticipantDto.REGION, EventParticipantDto.DISTRICT);

	private boolean jurisdictionFieldsRequired;

	public EventParticipantCreateForm(boolean jurisdictionFieldsRequired) {

		super(EventParticipantDto.class, EventParticipantDto.I18N_PREFIX);
		this.jurisdictionFieldsRequired = jurisdictionFieldsRequired;
		setWidth(540, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {

		addField(EventParticipantDto.INVOLVEMENT_DESCRIPTION, TextField.class);
		addCustomField(FIRST_NAME, String.class, TextField.class);
		addCustomField(LAST_NAME, String.class, TextField.class);

		ComboBox sex = addCustomField(PERSON_SEX, Sex.class, ComboBox.class);
		sex.setCaption(I18nProperties.getCaption(Captions.Person_sex));

		ComboBox region = addInfrastructureField(EventParticipantDto.REGION);
		region.setDescription(I18nProperties.getPrefixDescription(EventParticipantDto.I18N_PREFIX, EventParticipantDto.REGION));
		ComboBox district = addInfrastructureField(EventParticipantDto.DISTRICT);
		district.setDescription(I18nProperties.getPrefixDescription(EventParticipantDto.I18N_PREFIX, EventParticipantDto.DISTRICT));

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();

			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});

		region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		setRequired(true, FIRST_NAME, LAST_NAME, PERSON_SEX);
	}

	@Override
	public void setValue(EventParticipantDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		final PersonDto person = newFieldValue.getPerson();
		if (person != null) {
			final Field<String> firstNameField = getField(FIRST_NAME);
			final Field<String> lastNameField = getField(LAST_NAME);
			final Field<Sex> personSexField = getField(PERSON_SEX);
			if (person.isPseudonymized()) {
				firstNameField.setRequired(false);
				firstNameField.setVisible(false);
				lastNameField.setRequired(false);
				lastNameField.setVisible(false);
			} else {
				firstNameField.setEnabled(false);
				firstNameField.setValue(person.getFirstName());
				lastNameField.setEnabled(false);
				lastNameField.setValue(person.getLastName());
				personSexField.setEnabled(false);
				personSexField.setValue(person.getSex());
			}
			personSexField.setEnabled(false);
			personSexField.setValue(person.getSex());
		}

		setRequired(jurisdictionFieldsRequired, EventParticipantDto.REGION, EventParticipantDto.DISTRICT);
	}

	/**
	 * 
	 * @return first name of the person. ATTENTION: For pseudonymised persons, this method may return an empty string!
	 */
	public String getPersonFirstName() {
		return (String) getField(FIRST_NAME).getValue();
	}

	/**
	 * 
	 * @return first name of the person. ATTENTION: For pseudonymised persons, this method may return an empty string!
	 */
	public String getPersonLastName() {
		return (String) getField(LAST_NAME).getValue();
	}

	/**
	 *
	 * @return sex of the person.
	 */
	public Sex getPersonSex() {
		return (Sex) getField(PERSON_SEX).getValue();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
