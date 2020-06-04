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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.MaxLengthValidator;
import de.symeda.sormas.ui.utils.ValidationConstants;

public class EventDataForm extends AbstractEditForm<EventDto> {

	private static final long serialVersionUID = 1L;

	private static final String STATUS_CHANGE = "statusChange";

	private static final String HTML_LAYOUT = 
			h3(I18nProperties.getString(Strings.headingEventData)) +
			fluidRowLocs(4, EventDto.UUID, 3, EventDto.REPORT_DATE_TIME, 5, EventDto.REPORTING_USER) +
			fluidRowLocs(4, EventDto.EVENT_DATE, 8, EventDto.EVENT_STATUS) +
			fluidRowLocs(EventDto.DISEASE, EventDto.DISEASE_DETAILS) +
			fluidRowLocs(EventDto.EVENT_DESC) + 
			
			h3(I18nProperties.getString(Strings.headingInformationSource)) +
			fluidRowLocs(EventDto.SRC_FIRST_NAME, EventDto.SRC_LAST_NAME) +
			fluidRowLocs(EventDto.SRC_TEL_NO, EventDto.SRC_EMAIL) + 
			
			h3(I18nProperties.getString(Strings.headingLocation)) +
			fluidRowLocs(EventDto.TYPE_OF_PLACE, EventDto.TYPE_OF_PLACE_TEXT) +
			fluidRowLocs(EventDto.EVENT_LOCATION) +
			fluidRowLocs("", EventDto.SURVEILLANCE_OFFICER);

	private final VerticalLayout statusChangeLayout;
	private Boolean isCreateForm = null;

	public EventDataForm(boolean create, boolean isInJurisdiction) {
		super(EventDto.class, EventDto.I18N_PREFIX, false, new FieldVisibilityCheckers(),
				new FieldAccessCheckers()
						.add(new SensitiveDataFieldAccessChecker(r -> UserProvider.getCurrent().hasUserRight(r), isInJurisdiction)));

		isCreateForm = create;
		if (create) {
			hideValidationUntilNextCommit();
		}
		statusChangeLayout = new VerticalLayout();
		statusChangeLayout.setSpacing(false);
		statusChangeLayout.setMargin(false);
		getContent().addComponent(statusChangeLayout, STATUS_CHANGE);

		addFields();
	}

	@Override
	protected void addFields() {
		if (isCreateForm == null) {
			return;
		}

		addField(EventDto.UUID, TextField.class);
		addDiseaseField(EventDto.DISEASE, false);
		addField(EventDto.DISEASE_DETAILS, TextField.class);
		DateField eventDate = addField(EventDto.EVENT_DATE, DateField.class);
		addField(EventDto.EVENT_STATUS, OptionGroup.class);
		TextArea descriptionField = addField(EventDto.EVENT_DESC, TextArea.class);
		descriptionField.setRows(2);
		descriptionField.addValidator(new MaxLengthValidator(ValidationConstants.TEXT_AREA_MAX_LENGTH));
		addField(EventDto.EVENT_LOCATION, LocationEditForm.class).setCaption(null);

		LocationEditForm locationForm = (LocationEditForm) getFieldGroup().getField(EventDto.EVENT_LOCATION);
		if (isCreateForm) {
			locationForm.hideValidationUntilNextCommit();
		}
		ComboBox districtField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.DISTRICT);
		ComboBox surveillanceOfficerField = addField(EventDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.setNullSelectionAllowed(true);

		ComboBox typeOfPlace = addField(EventDto.TYPE_OF_PLACE, ComboBox.class);
		typeOfPlace.setNullSelectionAllowed(true);
		addField(EventDto.TYPE_OF_PLACE_TEXT, TextField.class);
		addField(EventDto.REPORT_DATE_TIME, DateTimeField.class);
		addField(EventDto.REPORTING_USER, ComboBox.class);
		TextField srcFirstName = addField(EventDto.SRC_FIRST_NAME, TextField.class);
		TextField srcLastName = addField(EventDto.SRC_LAST_NAME, TextField.class);
		TextField srcTelNo = addField(EventDto.SRC_TEL_NO, TextField.class);
		addField(EventDto.SRC_EMAIL, TextField.class);

		setReadOnly(true, EventDto.UUID, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);

		initializeAccessAndAllowedAccesses();

		FieldHelper.setVisibleWhen(getFieldGroup(), EventDto.TYPE_OF_PLACE_TEXT, EventDto.TYPE_OF_PLACE,
				Arrays.asList(TypeOfPlace.OTHER), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EventDto.DISEASE_DETAILS), EventDto.DISEASE,
				Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), EventDto.DISEASE, Arrays.asList(EventDto.DISEASE_DETAILS),
				Arrays.asList(Disease.OTHER));

		setRequired(true, EventDto.EVENT_STATUS, EventDto.UUID, EventDto.EVENT_DESC,
				EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);
		setTypeOfPlaceTextRequirement();
		locationForm.setFieldsRequirement(true, LocationDto.REGION, LocationDto.DISTRICT);

		districtField.addValueChangeListener(e -> {
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade()
					.getUserRefsByDistrict((DistrictReferenceDto) districtField.getValue(), false,
							UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(surveillanceOfficerField, assignableSurveillanceOfficers);
		});

		FieldHelper.addSoftRequiredStyle(eventDate, typeOfPlace, surveillanceOfficerField, srcFirstName, srcLastName,
				srcTelNo);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@SuppressWarnings("rawtypes")
	public void setTypeOfPlaceTextRequirement() {
		FieldGroup fieldGroup = getFieldGroup();
		ComboBox typeOfPlaceField = (ComboBox) fieldGroup.getField(EventDto.TYPE_OF_PLACE);
		TextField typeOfPlaceTextField = (TextField) fieldGroup.getField(EventDto.TYPE_OF_PLACE_TEXT);
		((AbstractField) typeOfPlaceField).setImmediate(true);

		// initialize
		{
			typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER);
		}

		typeOfPlaceField.addValueChangeListener(event -> {
			typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER);
		});
	}

}
