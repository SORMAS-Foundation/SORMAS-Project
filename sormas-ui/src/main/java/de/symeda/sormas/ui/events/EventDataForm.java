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

import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION_CHECKBOX;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventDataForm extends AbstractEditForm<EventDto> {

	private static final long serialVersionUID = 1L;

	private static final String EVENT_DATA_HEADING_LOC = "contactDataHeadingLoc";
	private static final String MULTI_DAY_EVENT_LOC = "eventMultiDay";
	private static final String INFORMATION_SOURCE_HEADING_LOC = "informationSourceHeadingLoc";
	private static final String LOCATION_HEADING_LOC = "locationHeadingLoc";

	private static final String STATUS_CHANGE = "statusChange";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(EVENT_DATA_HEADING_LOC) +
			fluidRowLocs(4, EventDto.UUID, 3, EventDto.REPORT_DATE_TIME, 5, EventDto.REPORTING_USER) +
			fluidRowLocs(8, EventDto.EVENT_STATUS) +
			fluidRowLocs(4, EventDto.START_DATE, 4, EventDto.MULTI_DAY_EVENT, 4, EventDto.END_DATE) +
			fluidRowLocs(EventDto.DISEASE, EventDto.DISEASE_DETAILS) +
			fluidRowLocs(EventDto.EVENT_ID, "") +
			fluidRowLocs(EventDto.EVENT_DESC) +
			fluidRowLocs(EventDto.NOSOCOMIAL, "") +

			loc(INFORMATION_SOURCE_HEADING_LOC) +
			fluidRowLocs(EventDto.SRC_TYPE, "") +
			fluidRowLocs(EventDto.SRC_FIRST_NAME, EventDto.SRC_LAST_NAME) +
			fluidRowLocs(EventDto.SRC_TEL_NO, EventDto.SRC_EMAIL) +

			fluidRowLocs(EventDto.SRC_MEDIA_WEBSITE, EventDto.SRC_MEDIA_NAME) +
			fluidRowLocs(EventDto.SRC_MEDIA_DETAILS) +

			loc(LOCATION_HEADING_LOC) +
			fluidRowLocs(EventDto.TYPE_OF_PLACE, EventDto.TYPE_OF_PLACE_TEXT) +
			fluidRowLocs(EventDto.EVENT_LOCATION) +
			fluidRowLocs("", EventDto.SURVEILLANCE_OFFICER);
	//@formatter:on

	private final VerticalLayout statusChangeLayout;
	private Boolean isCreateForm = null;

	public EventDataForm(boolean create) {
		super(EventDto.class, EventDto.I18N_PREFIX, false, new FieldVisibilityCheckers(), new FieldAccessCheckers());

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

		Label eventDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingEventData));
		eventDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(eventDataHeadingLabel, EVENT_DATA_HEADING_LOC);

		Label informationSourceHeadingLabel = new Label(I18nProperties.getString(Strings.headingInformationSource));
		informationSourceHeadingLabel.addStyleName(H3);
		getContent().addComponent(informationSourceHeadingLabel, INFORMATION_SOURCE_HEADING_LOC);

		Label locationHeadingLabel = new Label(I18nProperties.getString(Strings.headingLocation));
		locationHeadingLabel.addStyleName(H3);
		getContent().addComponent(locationHeadingLabel, LOCATION_HEADING_LOC);

		addField(EventDto.UUID, TextField.class);
		addDiseaseField(EventDto.DISEASE, false);
		addField(EventDto.DISEASE_DETAILS, TextField.class);
		addFields(EventDto.EVENT_ID);
		DateField startDate = addField(EventDto.START_DATE, DateField.class);
		CheckBox multiDayCheckbox = addField(EventDto.MULTI_DAY_EVENT, CheckBox.class);
		multiDayCheckbox.addStyleName(FORCE_CAPTION_CHECKBOX);
		addField(EventDto.END_DATE, DateField.class);
		addField(EventDto.EVENT_STATUS, OptionGroup.class);
		TextArea descriptionField = addField(EventDto.EVENT_DESC, TextArea.class);
		descriptionField.setRows(2);

		addField(EventDto.NOSOCOMIAL, OptionGroup.class);

		ComboBox typeOfPlace = addField(EventDto.TYPE_OF_PLACE, ComboBox.class);
		typeOfPlace.setNullSelectionAllowed(true);
		addField(EventDto.TYPE_OF_PLACE_TEXT, TextField.class);
		addField(EventDto.REPORT_DATE_TIME, DateTimeField.class);
		addField(EventDto.REPORTING_USER, ComboBox.class);
		addField(EventDto.SRC_TYPE);
		TextField srcFirstName = addField(EventDto.SRC_FIRST_NAME, TextField.class);
		TextField srcLastName = addField(EventDto.SRC_LAST_NAME, TextField.class);
		TextField srcTelNo = addField(EventDto.SRC_TEL_NO, TextField.class);
		addField(EventDto.SRC_EMAIL, TextField.class);

		TextField srcMediaWebsite = addField(EventDto.SRC_MEDIA_WEBSITE, TextField.class);
		TextField srcMediaName = addField(EventDto.SRC_MEDIA_NAME, TextField.class);
		TextArea srcMediaDetails = addField(EventDto.SRC_MEDIA_DETAILS, TextArea.class);
		srcMediaDetails.setRows(2);

		addField(EventDto.EVENT_LOCATION, LocationEditForm.class).setCaption(null);

		LocationEditForm locationForm = (LocationEditForm) getFieldGroup().getField(EventDto.EVENT_LOCATION);
		if (isCreateForm) {
			locationForm.hideValidationUntilNextCommit();
		}
		ComboBox districtField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.DISTRICT);
		ComboBox surveillanceOfficerField = addField(EventDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.setNullSelectionAllowed(true);

		setReadOnly(true, EventDto.UUID, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(EventDto.DISEASE_DETAILS),
			EventDto.DISEASE,
			Collections.singletonList(Disease.OTHER),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			EventDto.DISEASE,
			Collections.singletonList(EventDto.DISEASE_DETAILS),
			Collections.singletonList(Disease.OTHER));

		setRequired(true, EventDto.EVENT_STATUS, EventDto.UUID, EventDto.EVENT_DESC, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);

		FieldHelper.setVisibleWhen(getFieldGroup(), EventDto.END_DATE, EventDto.MULTI_DAY_EVENT, Collections.singletonList(true), true);
		FieldHelper.setCaptionWhen(
			multiDayCheckbox,
			startDate,
			false,
			I18nProperties.getCaption(Captions.Event_eventDate),
			I18nProperties.getCaption(Captions.Event_startDate));
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(EventDto.NOSOCOMIAL),
			EventDto.EVENT_STATUS,
			Collections.singletonList(EventStatus.CLUSTER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EventDto.SRC_FIRST_NAME, EventDto.SRC_LAST_NAME, EventDto.SRC_TEL_NO, EventDto.SRC_EMAIL),
			EventDto.SRC_TYPE,
			Collections.singletonList(EventSourceType.HOTLINE_PERSON),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EventDto.SRC_MEDIA_WEBSITE, EventDto.SRC_MEDIA_NAME, EventDto.SRC_MEDIA_DETAILS),
			EventDto.SRC_TYPE,
			Collections.singletonList(EventSourceType.MEDIA_NEWS),
			true);

		FieldHelper
			.setVisibleWhen(getFieldGroup(), EventDto.TYPE_OF_PLACE_TEXT, EventDto.TYPE_OF_PLACE, Collections.singletonList(TypeOfPlace.OTHER), true);
		setTypeOfPlaceTextRequirement();
		locationForm.setFieldsRequirement(true, LocationDto.REGION, LocationDto.DISTRICT);

		districtField.addValueChangeListener(e -> {
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade()
				.getUserRefsByDistrict((DistrictReferenceDto) districtField.getValue(), false, UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(surveillanceOfficerField, assignableSurveillanceOfficers);
		});

		FieldHelper.addSoftRequiredStyle(
			startDate,
			typeOfPlace,
			surveillanceOfficerField,
			srcFirstName,
			srcLastName,
			srcTelNo,
			srcMediaWebsite,
			srcMediaName);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public void setTypeOfPlaceTextRequirement() {

		FieldGroup fieldGroup = getFieldGroup();
		ComboBox typeOfPlaceField = (ComboBox) fieldGroup.getField(EventDto.TYPE_OF_PLACE);
		typeOfPlaceField.setImmediate(true);

		TextField typeOfPlaceTextField = (TextField) fieldGroup.getField(EventDto.TYPE_OF_PLACE_TEXT);
		typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER);
		typeOfPlaceField.addValueChangeListener(event -> typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER));
	}
}
