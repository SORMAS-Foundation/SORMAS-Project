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

package de.symeda.sormas.ui.exposure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class ExposureForm extends AbstractEditForm<ExposureDto> {

	private static final long serialVersionUID = 8262753698264714832L;

	private static final String LOC_EXPOSURE_DETAILS_HEADING = "locExposureDetailsHeading";
	private static final String LOC_LOCATION_HEADING = "locLocationHeading";
	private static final String LOC_ANIMAL_CONTACT_DETAILS_HEADING = "locAnimalContactDetailsHeading";
	private static final String LOC_BURIAL_DETAILS_HEADING = "locBurialDetailsHeading";

	//@formatter:off
	private static final String HTML_LAYOUT = 
			fluidRowLocs(ExposureDto.UUID, ExposureDto.REPORTING_USER) +
			fluidRowLocs(ExposureDto.START_DATE, ExposureDto.END_DATE) +
			loc(ExposureDto.DESCRIPTION) +
			fluidRow(
					fluidColumnLoc(6, 0, ExposureDto.EXPOSURE_TYPE),
					fluidColumn(6, 0, locs(
							ExposureDto.EXPOSURE_TYPE_DETAILS,
							ExposureDto.GATHERING_TYPE,
							ExposureDto.HABITATION_TYPE,
							ExposureDto.TYPE_OF_ANIMAL
					))
			) +
			fluidRow(
					fluidColumn(12, 0, locs(
							ExposureDto.GATHERING_DETAILS,
							ExposureDto.HABITATION_DETAILS,
							ExposureDto.TYPE_OF_ANIMAL_DETAILS
					))
			) +
			loc(LOC_EXPOSURE_DETAILS_HEADING) +
			loc(ExposureDto.EXPOSURE_ROLE) +
			loc(ExposureDto.RISK_AREA) +
			loc(ExposureDto.INDOORS) +
			loc(ExposureDto.OUTDOORS) +
			loc(ExposureDto.WEARING_MASK) +
			loc(ExposureDto.WEARING_PPE) +
			loc(ExposureDto.OTHER_PROTECTIVE_MEASURES) +
			loc(ExposureDto.PROTECTIVE_MEASURES_DETAILS) +
			loc(ExposureDto.SHORT_DISTANCE) +
			loc(ExposureDto.LONG_FACE_TO_FACE_CONTACT) +
			loc(ExposureDto.ANIMAL_MARKET) +
			loc(ExposureDto.PERCUTANEOUS) +
			loc(ExposureDto.CONTACT_TO_BODY_FLUIDS) +
			loc(ExposureDto.HANDLING_SAMPLES) +
			loc(ExposureDto.EATING_RAW_ANIMAL_PRODUCTS) +
			loc(ExposureDto.HANDLING_ANIMALS) + 
			loc(ExposureDto.CONTACT_TO_CASE) +
			loc(LOC_ANIMAL_CONTACT_DETAILS_HEADING) +	
			loc(ExposureDto.ANIMAL_CONDITION) +
			fluidRowLocs(ExposureDto.ANIMAL_CONTACT_TYPE, ExposureDto.ANIMAL_CONTACT_TYPE_DETAILS) +
			loc(ExposureDto.ANIMAL_VACCINATED) +
			loc(LOC_BURIAL_DETAILS_HEADING) +
			loc(ExposureDto.PHYSICAL_CONTACT_DURING_PREPARATION) +
			loc(ExposureDto.PHYSICAL_CONTACT_WITH_BODY) +
			fluidRowLocs(ExposureDto.DECEASED_PERSON_NAME, ExposureDto.DECEASED_PERSON_RELATION) +
			loc(LOC_LOCATION_HEADING) +
			fluidRow(
					fluidColumnLoc(6, 0, ExposureDto.TYPE_OF_PLACE),
					fluidColumn(6, 0, locs(
							ExposureDto.TYPE_OF_PLACE_DETAILS,
							ExposureDto.MEANS_OF_TRANSPORT
					))
			) +
			loc(ExposureDto.MEANS_OF_TRANSPORT_DETAILS) +
			fluidRowLocs(ExposureDto.CONNECTION_NUMBER, ExposureDto.SEAT_NUMBER) +
			loc(ExposureDto.LOCATION);
	//@formatter:on

	private final Class<? extends EntityDto> epiDataParentClass;
	private final List<ContactReferenceDto> sourceContacts;

	private LocationEditForm locationForm;

	public ExposureForm(
		boolean create,
		Class<? extends EntityDto> epiDataParentClass,
		List<ContactReferenceDto> sourceContacts,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers) {
		super(ExposureDto.class, ExposureDto.I18N_PREFIX, false, fieldVisibilityCheckers, fieldAccessCheckers);

		setWidth(960, Unit.PIXELS);

		this.sourceContacts = sourceContacts;
		this.epiDataParentClass = epiDataParentClass;

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

		addField(ExposureDto.DESCRIPTION, TextArea.class).setRows(5);

		locationForm = addField(ExposureDto.LOCATION, LocationEditForm.class);
		locationForm.setCaption(null);
		addField(ExposureDto.CONNECTION_NUMBER, TextField.class);
		getField(ExposureDto.MEANS_OF_TRANSPORT).addValueChangeListener(e -> {
			if (e.getProperty().getValue() == MeansOfTransport.PLANE) {
				getField(ExposureDto.CONNECTION_NUMBER).setCaption(I18nProperties.getCaption(Captions.exposureFlightNumber));
			} else {
				getField(ExposureDto.CONNECTION_NUMBER)
					.setCaption(I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.CONNECTION_NUMBER));
			}
		});

		if (epiDataParentClass == CaseDataDto.class) {
			addField(ExposureDto.CONTACT_TO_CASE, ComboBox.class);
		}

		setUpVisibilityDependencies();

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		setUpRequirements();
		setReadOnly(true, ExposureDto.UUID, ExposureDto.REPORTING_USER);
	}

	private void addHeadingsAndInfoTexts() {
		getContent()
			.addComponent(new Label(h3(I18nProperties.getString(Strings.headingExposureDetails)), ContentMode.HTML), LOC_EXPOSURE_DETAILS_HEADING);

		getContent().addComponent(
			new Label(h3(I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.LOCATION)), ContentMode.HTML),
			LOC_LOCATION_HEADING);

		getContent().addComponent(
			new Label(h3(I18nProperties.getString(Strings.headingAnimalContactDetails)), ContentMode.HTML),
			LOC_ANIMAL_CONTACT_DETAILS_HEADING);

		getContent()
			.addComponent(new Label(h3(I18nProperties.getString(Strings.headingBurialDetails)), ContentMode.HTML), LOC_BURIAL_DETAILS_HEADING);
	}

	private void addBasicFields() {
		addFields(
			ExposureDto.UUID,
			ExposureDto.REPORTING_USER,
			ExposureDto.START_DATE,
			ExposureDto.END_DATE,
			ExposureDto.EXPOSURE_TYPE,
			ExposureDto.EXPOSURE_TYPE_DETAILS,
			ExposureDto.GATHERING_TYPE,
			ExposureDto.HABITATION_TYPE,
			ExposureDto.TYPE_OF_ANIMAL,
			ExposureDto.GATHERING_DETAILS,
			ExposureDto.HABITATION_DETAILS,
			ExposureDto.TYPE_OF_ANIMAL_DETAILS,
			ExposureDto.PHYSICAL_CONTACT_DURING_PREPARATION,
			ExposureDto.PHYSICAL_CONTACT_WITH_BODY,
			ExposureDto.DECEASED_PERSON_NAME,
			ExposureDto.DECEASED_PERSON_RELATION,
			ExposureDto.PROTECTIVE_MEASURES_DETAILS,
			ExposureDto.ANIMAL_CONDITION,
			ExposureDto.ANIMAL_CONTACT_TYPE,
			ExposureDto.ANIMAL_CONTACT_TYPE_DETAILS,
			ExposureDto.TYPE_OF_PLACE,
			ExposureDto.TYPE_OF_PLACE_DETAILS,
			ExposureDto.MEANS_OF_TRANSPORT,
			ExposureDto.MEANS_OF_TRANSPORT_DETAILS,
			ExposureDto.SEAT_NUMBER,
			ExposureDto.EXPOSURE_ROLE);

		addFieldsWithCss(
			NullableOptionGroup.class,
			Arrays.asList(
				ExposureDto.INDOORS,
				ExposureDto.OUTDOORS,
				ExposureDto.WEARING_MASK,
				ExposureDto.WEARING_PPE,
				ExposureDto.OTHER_PROTECTIVE_MEASURES,
				ExposureDto.SHORT_DISTANCE,
				ExposureDto.LONG_FACE_TO_FACE_CONTACT,
				ExposureDto.ANIMAL_MARKET,
				ExposureDto.PERCUTANEOUS,
				ExposureDto.CONTACT_TO_BODY_FLUIDS,
				ExposureDto.HANDLING_SAMPLES,
				ExposureDto.EATING_RAW_ANIMAL_PRODUCTS,
				ExposureDto.HANDLING_ANIMALS,
				ExposureDto.ANIMAL_VACCINATED,
				ExposureDto.RISK_AREA),
			ValoTheme.OPTIONGROUP_HORIZONTAL,
			CssStyles.OPTIONGROUP_CAPTION_INLINE);
	}

	private void setUpVisibilityDependencies() {
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.EXPOSURE_TYPE_DETAILS, ExposureDto.EXPOSURE_TYPE, ExposureType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.GATHERING_TYPE, ExposureDto.EXPOSURE_TYPE, ExposureType.GATHERING, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.HABITATION_TYPE, ExposureDto.EXPOSURE_TYPE, ExposureType.HABITATION, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TYPE_OF_ANIMAL, ExposureDto.EXPOSURE_TYPE, ExposureType.ANIMAL_CONTACT, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.GATHERING_DETAILS, ExposureDto.GATHERING_TYPE, GatheringType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.HABITATION_DETAILS, ExposureDto.HABITATION_TYPE, HabitationType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TYPE_OF_ANIMAL_DETAILS, ExposureDto.TYPE_OF_ANIMAL, TypeOfAnimal.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				ExposureDto.PHYSICAL_CONTACT_DURING_PREPARATION,
				ExposureDto.PHYSICAL_CONTACT_WITH_BODY,
				ExposureDto.DECEASED_PERSON_NAME,
				ExposureDto.DECEASED_PERSON_RELATION),
			ExposureDto.EXPOSURE_TYPE,
			ExposureType.BURIAL,
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ExposureDto.PROTECTIVE_MEASURES_DETAILS, ExposureDto.OTHER_PROTECTIVE_MEASURES, YesNoUnknown.YES, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ExposureDto.ANIMAL_CONDITION, ExposureDto.ANIMAL_VACCINATED, ExposureDto.ANIMAL_CONTACT_TYPE),
			ExposureDto.EXPOSURE_TYPE,
			ExposureType.ANIMAL_CONTACT,
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ExposureDto.ANIMAL_CONTACT_TYPE_DETAILS, ExposureDto.ANIMAL_CONTACT_TYPE, AnimalContactType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TYPE_OF_PLACE_DETAILS, ExposureDto.TYPE_OF_PLACE, TypeOfPlace.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ExposureDto.MEANS_OF_TRANSPORT, ExposureDto.CONNECTION_NUMBER),
			ExposureDto.TYPE_OF_PLACE,
			TypeOfPlace.MEANS_OF_TRANSPORT,
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ExposureDto.MEANS_OF_TRANSPORT_DETAILS, ExposureDto.MEANS_OF_TRANSPORT, MeansOfTransport.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ExposureDto.SEAT_NUMBER,
			ExposureDto.MEANS_OF_TRANSPORT,
			Arrays.asList(MeansOfTransport.PLANE, MeansOfTransport.TRAIN, MeansOfTransport.OTHER),
			true);

		getContent().getComponent(LOC_ANIMAL_CONTACT_DETAILS_HEADING).setVisible(false);
		getContent().getComponent(LOC_BURIAL_DETAILS_HEADING).setVisible(false);
		getField(ExposureDto.EXPOSURE_TYPE).addValueChangeListener(e -> {
			ExposureType selectedExposureType = (ExposureType) e.getProperty().getValue();
			getContent().getComponent(LOC_ANIMAL_CONTACT_DETAILS_HEADING).setVisible(selectedExposureType == ExposureType.ANIMAL_CONTACT);
			getContent().getComponent(LOC_BURIAL_DETAILS_HEADING).setVisible(selectedExposureType == ExposureType.BURIAL);
		});

		locationForm.setFacilityFieldsVisible(getField(ExposureDto.TYPE_OF_PLACE).getValue() == TypeOfPlace.FACILITY, true);
		getField(ExposureDto.TYPE_OF_PLACE)
			.addValueChangeListener(e -> locationForm.setFacilityFieldsVisible(e.getProperty().getValue() == TypeOfPlace.FACILITY, true));
	}

	private void setUpRequirements() {
		setRequired(true, ExposureDto.EXPOSURE_TYPE);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ExposureDto.EXPOSURE_TYPE,
			Collections.singletonList(ExposureDto.EXPOSURE_TYPE_DETAILS),
			Collections.singletonList(ExposureType.OTHER));
	}

	@Override
	public void setValue(ExposureDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		if (epiDataParentClass == CaseDataDto.class) {
			ComboBox cbContactToCase = getField(ExposureDto.CONTACT_TO_CASE);
			if (sourceContacts != null) {
				cbContactToCase.addItems(sourceContacts);
			}
			cbContactToCase.getItemIds().forEach(i -> cbContactToCase.setItemCaption(i, ((ContactReferenceDto) i).getCaptionAlwaysWithUuid()));
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
