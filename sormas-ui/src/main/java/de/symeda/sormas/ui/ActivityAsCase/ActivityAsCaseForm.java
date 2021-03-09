/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.ActivityAsCase;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.Collections;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class ActivityAsCaseForm extends AbstractEditForm<ActivityAsCaseDto> {

	private static final long serialVersionUID = 8262753698264714835L;

	private static final String LOC_LOCATION_HEADING = "locLocationHeading";

	//@formatter:off
        private static final String HTML_LAYOUT =
                fluidRowLocs(ActivityAsCaseDto.UUID, ActivityAsCaseDto.REPORTING_USER) +
                        fluidRowLocs(ActivityAsCaseDto.START_DATE, ActivityAsCaseDto.END_DATE) +
                        loc(ActivityAsCaseDto.DESCRIPTION) +
                        fluidRow(
                                fluidColumnLoc(6, 0, ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE),
                                fluidColumn(6, 0, locs(
                                        ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE_DETAILS,
                                        ActivityAsCaseDto.GATHERING_TYPE,
                                        ActivityAsCaseDto.HABITATION_TYPE
                                ))
                        ) +
                        fluidRow(
                                fluidColumn(12, 0, locs(
                                        ActivityAsCaseDto.GATHERING_DETAILS,
                                        ActivityAsCaseDto.HABITATION_DETAILS
                                ))
                        ) +
                        loc(ActivityAsCaseDto.ROLE) +
                        loc(LOC_LOCATION_HEADING) +
                        fluidRow(
                                fluidColumn(6, 0, locs(ActivityAsCaseDto.TYPE_OF_PLACE)),
                                fluidColumn(6, 0, locs(
                                        ActivityAsCaseDto.TYPE_OF_PLACE_DETAILS,
                                        ActivityAsCaseDto.MEANS_OF_TRANSPORT,
                                        ActivityAsCaseDto.WORK_ENVIRONMENT
                                ))
                        ) +
                        loc(ActivityAsCaseDto.MEANS_OF_TRANSPORT_DETAILS) +
                        fluidRowLocs(ActivityAsCaseDto.CONNECTION_NUMBER, ActivityAsCaseDto.SEAT_NUMBER) +
                        loc(ActivityAsCaseDto.LOCATION);
        //@formatter:on

	private LocationEditForm locationForm;

	public ActivityAsCaseForm(boolean create, FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(ActivityAsCaseDto.class, ActivityAsCaseDto.I18N_PREFIX, false, fieldVisibilityCheckers, fieldAccessCheckers);

		setWidth(960, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		addHeadingsAndInfoTexts();
		addBasicFields();

		addField(ActivityAsCaseDto.DESCRIPTION, TextArea.class).setRows(5);

		locationForm = addField(ActivityAsCaseDto.LOCATION, LocationEditForm.class);
		locationForm.setCaption(null);
		addField(ActivityAsCaseDto.CONNECTION_NUMBER, TextField.class);
		getField(ActivityAsCaseDto.MEANS_OF_TRANSPORT).addValueChangeListener(e -> {
			if (e.getProperty().getValue() == MeansOfTransport.PLANE) {
				getField(ActivityAsCaseDto.CONNECTION_NUMBER).setCaption(I18nProperties.getCaption(Captions.exposureFlightNumber));
			} else {
				getField(ActivityAsCaseDto.CONNECTION_NUMBER)
					.setCaption(I18nProperties.getPrefixCaption(ActivityAsCaseDto.I18N_PREFIX, ActivityAsCaseDto.CONNECTION_NUMBER));
			}
		});

		setUpVisibilityDependencies();

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		setUpRequirements();
		setReadOnly(true, ActivityAsCaseDto.UUID, ActivityAsCaseDto.REPORTING_USER);
	}

	private void addHeadingsAndInfoTexts() {
		getContent().addComponent(
			new Label(h3(I18nProperties.getPrefixCaption(ActivityAsCaseDto.I18N_PREFIX, ActivityAsCaseDto.LOCATION)), ContentMode.HTML),
			LOC_LOCATION_HEADING);
	}

	private void addBasicFields() {
		addFields(
			ActivityAsCaseDto.UUID,
			ActivityAsCaseDto.REPORTING_USER,
			ActivityAsCaseDto.START_DATE,
			ActivityAsCaseDto.END_DATE,
			ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE,
			ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE_DETAILS,
			ActivityAsCaseDto.GATHERING_TYPE,
			ActivityAsCaseDto.HABITATION_TYPE,
			ActivityAsCaseDto.GATHERING_DETAILS,
			ActivityAsCaseDto.HABITATION_DETAILS,
			ActivityAsCaseDto.TYPE_OF_PLACE,
			ActivityAsCaseDto.TYPE_OF_PLACE_DETAILS,
			ActivityAsCaseDto.MEANS_OF_TRANSPORT,
			ActivityAsCaseDto.MEANS_OF_TRANSPORT_DETAILS,
			ActivityAsCaseDto.SEAT_NUMBER,
			ActivityAsCaseDto.ROLE,
			ActivityAsCaseDto.WORK_ENVIRONMENT);
	}

	private void setUpVisibilityDependencies() {
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE_DETAILS,
			ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE,
			ActivityAsCaseType.OTHER,
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ActivityAsCaseDto.GATHERING_TYPE,
			ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE,
			ActivityAsCaseType.GATHERING,
			true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ActivityAsCaseDto.GATHERING_DETAILS, ActivityAsCaseDto.GATHERING_TYPE, GatheringType.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ActivityAsCaseDto.HABITATION_TYPE,
			ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE,
			ActivityAsCaseType.HABITATION,
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ActivityAsCaseDto.HABITATION_DETAILS, ActivityAsCaseDto.HABITATION_TYPE, HabitationType.OTHER, true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ActivityAsCaseDto.TYPE_OF_PLACE_DETAILS, ActivityAsCaseDto.TYPE_OF_PLACE, TypeOfPlace.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ActivityAsCaseDto.MEANS_OF_TRANSPORT, ActivityAsCaseDto.CONNECTION_NUMBER),
			ActivityAsCaseDto.TYPE_OF_PLACE,
			TypeOfPlace.MEANS_OF_TRANSPORT,
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ActivityAsCaseDto.MEANS_OF_TRANSPORT_DETAILS,
			ActivityAsCaseDto.MEANS_OF_TRANSPORT,
			MeansOfTransport.OTHER,
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ActivityAsCaseDto.SEAT_NUMBER,
			ActivityAsCaseDto.MEANS_OF_TRANSPORT,
			Arrays.asList(MeansOfTransport.PLANE, MeansOfTransport.TRAIN, MeansOfTransport.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ActivityAsCaseDto.WORK_ENVIRONMENT,
			locationForm.getFacilityTypeGroup(),
			Collections.singletonList(FacilityTypeGroup.WORKING_PLACE),
			true);

		locationForm.setFacilityFieldsVisible(getField(ActivityAsCaseDto.TYPE_OF_PLACE).getValue() == TypeOfPlace.FACILITY, true);
		getField(ActivityAsCaseDto.TYPE_OF_PLACE)
			.addValueChangeListener(e -> locationForm.setFacilityFieldsVisible(e.getProperty().getValue() == TypeOfPlace.FACILITY, true));
	}

	private void setUpRequirements() {
		setRequired(true, ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE,
			Collections.singletonList(ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE_DETAILS),
			Collections.singletonList(ExposureType.OTHER));
	}

	@Override
	public void setValue(ActivityAsCaseDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
