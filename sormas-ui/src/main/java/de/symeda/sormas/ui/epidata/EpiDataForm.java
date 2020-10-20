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
package de.symeda.sormas.ui.epidata;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locsCss;

import java.util.Arrays;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String EPI_DATA_CAPTION_LOC = "epiDataCaptionLoc";
	private static final String ANIMAL_CAPTION_LOC = "animalCaptionLoc";
	private static final String ENVIRONMENTAL_CAPTION_LOC = "environmentalLoc";
	private static final String KIND_OF_EXPOSURE_LOC = "kindOfExposureLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = 
			loc(EPI_DATA_CAPTION_LOC) +
			fluidRowLocs(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE, EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE) +
			fluidRowLocs(EpiDataDto.DIRECT_CONTACT_PROBABLE_CASE, EpiDataDto.AREA_CONFIRMED_CASES) +
			fluidRowLocs(EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE, EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE) +
			fluidRowLocs(EpiDataDto.CONTACT_WITH_SOURCE_RESPIRATORY_CASE, EpiDataDto.PERCUTANEOUS_CASE_BLOOD) +
			fluidRowLocs(EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE, "") +
			fluidRowLocs(EpiDataDto.VISITED_HEALTH_FACILIY, EpiDataDto.VISITED_ANIMAL_MARKET) +
			loc(ANIMAL_CAPTION_LOC) +
			fluidRow(
					fluidColumn(6, 0, locsCss(VSPACE_3,
							EpiDataDto.BATS,  
							EpiDataDto.BIRDS, 
							EpiDataDto.CAMELS, 
							EpiDataDto.CANIDAE, 
							EpiDataDto.CATS, 
							EpiDataDto.CATTLE,  
							EpiDataDto.AREA_INFECTED_ANIMALS,
							EpiDataDto.SICK_DEAD_ANIMALS, 
							EpiDataDto.SICK_DEAD_ANIMALS_DETAILS,
							EpiDataDto.SICK_DEAD_ANIMALS_DATE, 
							EpiDataDto.SICK_DEAD_ANIMALS_LOCATION
					)),
					fluidColumn(6, 0, locsCss(VSPACE_3,
							EpiDataDto.DOGS, 
							EpiDataDto.PRIMATES, 
							EpiDataDto.SNAKES,
							EpiDataDto.SWINE,
							EpiDataDto.RABBITS, 
							EpiDataDto.RODENTS, 
							EpiDataDto.OTHER_ANIMALS, 
							EpiDataDto.OTHER_ANIMALS_DETAILS, 
							EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA, 
							EpiDataDto.EATING_RAW_ANIMALS, 
							EpiDataDto.EATING_RAW_ANIMALS_DETAILS
					))
			) +
			fluidRow(
					fluidColumn(6, 0, locsCss(VSPACE_3,
							EpiDataDto.DATE_OF_LAST_EXPOSURE,
							EpiDataDto.ANIMAL_CONDITION,
							EpiDataDto.PROPHYLAXIS_STATUS
					)),
					fluidColumn(6, 0, locsCss(VSPACE_3,
							EpiDataDto.PLACE_OF_LAST_EXPOSURE,
							EpiDataDto.ANIMAL_VACCINATION_STATUS,
							EpiDataDto.DATE_OF_PROPHYLAXIS
					))
			) +			
			loc(KIND_OF_EXPOSURE_LOC) +
			fluidRow(
					fluidColumn(6, 0, locsCss(VSPACE_3,
							EpiDataDto.KIND_OF_EXPOSURE_BITE, 
							EpiDataDto.KIND_OF_EXPOSURE_TOUCH, 
							EpiDataDto.KIND_OF_EXPOSURE_SCRATCH
					)),
					fluidColumn(6, 0, locsCss(VSPACE_3,
							EpiDataDto.KIND_OF_EXPOSURE_LICK,
							EpiDataDto.KIND_OF_EXPOSURE_OTHER, 
							EpiDataDto.KIND_OF_EXPOSURE_DETAILS
					))
			) +
			loc(ENVIRONMENTAL_CAPTION_LOC) +
			fluidRowLocs(EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_BODY) +
			fluidRowLocs(EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_BODY_DETAILS) +
			fluidRowLocs(EpiDataDto.TICK_BITE, EpiDataDto.FLEA_BITE);
	//@formatter:on

	private final Disease disease;

	public EpiDataForm(Disease disease, boolean isPseudonymized) {
		super(
			EpiDataDto.class,
			EpiDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease),
			UiFieldAccessCheckers.forSensitiveData(isPseudonymized));
		this.disease = disease;
		addFields();
	}

	@Override
	protected void addFields() {
		if (disease == null) {
			return;
		}

		addFields(
			EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE,
			EpiDataDto.DIRECT_CONTACT_PROBABLE_CASE,
			EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE,
			EpiDataDto.AREA_CONFIRMED_CASES,
			EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE,
			EpiDataDto.PERCUTANEOUS_CASE_BLOOD,
			EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE,
			EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE,
			EpiDataDto.CONTACT_WITH_SOURCE_RESPIRATORY_CASE,
			EpiDataDto.VISITED_ANIMAL_MARKET,
			EpiDataDto.VISITED_HEALTH_FACILIY);

		addFields(
			EpiDataDto.AREA_INFECTED_ANIMALS,
			EpiDataDto.SICK_DEAD_ANIMALS,
			EpiDataDto.SICK_DEAD_ANIMALS_DETAILS,
			EpiDataDto.SICK_DEAD_ANIMALS_DATE,
			EpiDataDto.SICK_DEAD_ANIMALS_LOCATION,
			EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA,
			EpiDataDto.EATING_RAW_ANIMALS,
			EpiDataDto.EATING_RAW_ANIMALS_DETAILS,
			EpiDataDto.RODENTS,
			EpiDataDto.BATS,
			EpiDataDto.BIRDS,
			EpiDataDto.RABBITS,
			EpiDataDto.PRIMATES,
			EpiDataDto.SWINE,
			EpiDataDto.CATTLE,
			EpiDataDto.DOGS,
			EpiDataDto.CATS,
			EpiDataDto.CANIDAE,
			EpiDataDto.SNAKES,
			EpiDataDto.CAMELS,
			EpiDataDto.OTHER_ANIMALS,
			EpiDataDto.OTHER_ANIMALS_DETAILS,
			EpiDataDto.ANIMAL_CONDITION,
			EpiDataDto.DATE_OF_LAST_EXPOSURE,
			EpiDataDto.PLACE_OF_LAST_EXPOSURE);

		addFields(
			EpiDataDto.KIND_OF_EXPOSURE_BITE,
			EpiDataDto.KIND_OF_EXPOSURE_TOUCH,
			EpiDataDto.KIND_OF_EXPOSURE_SCRATCH,
			EpiDataDto.KIND_OF_EXPOSURE_LICK,
			EpiDataDto.KIND_OF_EXPOSURE_OTHER,
			EpiDataDto.KIND_OF_EXPOSURE_DETAILS);

		Label kindOfExposureLabel = new Label(I18nProperties.getCaption(Captions.EpiData_kindOfExposure));
		CssStyles.style(kindOfExposureLabel, CssStyles.H3);
		getContent().addComponent(kindOfExposureLabel, KIND_OF_EXPOSURE_LOC);

		addField(EpiDataDto.ANIMAL_VACCINATION_STATUS, OptionGroup.class);
		OptionGroup prophylaxisStatus = addField(EpiDataDto.PROPHYLAXIS_STATUS, OptionGroup.class);
		CssStyles.style(prophylaxisStatus, CssStyles.ERROR_COLOR_PRIMARY);
		addFields(EpiDataDto.DATE_OF_PROPHYLAXIS);

		addFields(
			EpiDataDto.WATER_BODY,
			EpiDataDto.WATER_BODY_DETAILS,
			EpiDataDto.WATER_SOURCE,
			EpiDataDto.WATER_SOURCE_OTHER,
			EpiDataDto.TICK_BITE,
			EpiDataDto.FLEA_BITE);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EpiDataDto.SICK_DEAD_ANIMALS_DETAILS, EpiDataDto.SICK_DEAD_ANIMALS_DATE, EpiDataDto.SICK_DEAD_ANIMALS_LOCATION),
			EpiDataDto.SICK_DEAD_ANIMALS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EpiDataDto.EATING_RAW_ANIMALS_DETAILS),
			EpiDataDto.EATING_RAW_ANIMALS,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), EpiDataDto.OTHER_ANIMALS_DETAILS, EpiDataDto.OTHER_ANIMALS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_SOURCE, Arrays.asList(WaterSource.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_BODY_DETAILS, EpiDataDto.WATER_BODY, Arrays.asList(YesNoUnknown.YES), true);

		String animalCaptionLayout = h3(I18nProperties.getString(Strings.headingAnimalContacts))
			+ divsCss(VSPACE_3, I18nProperties.getString(Strings.messageAnimalContactsHint));

		Label animalCaptionLabel = new Label(animalCaptionLayout);
		animalCaptionLabel.setContentMode(ContentMode.HTML);
		getContent().addComponent(animalCaptionLabel, ANIMAL_CAPTION_LOC);

		for (String propertyId : EpiDataDto.ANIMAL_EXPOSURE_PROPERTIES) {
			getField(propertyId).addValueChangeListener(e -> updateAnimalExposureFields());
		}
		updateAnimalExposureFields();

		Label environmentalCaptionLabel = new Label(h3(I18nProperties.getString(Strings.headingEnvironmentalExposure)));
		environmentalCaptionLabel.setContentMode(ContentMode.HTML);
		getContent().addComponent(environmentalCaptionLabel, ENVIRONMENTAL_CAPTION_LOC);

		for (String propertyId : EpiDataDto.ENVIRONMENTAL_EXPOSURE_PROPERTIES) {
			getField(propertyId).addValueChangeListener(e -> updateEnvironmentalExposureFields());
		}
		updateEnvironmentalExposureFields();

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.KIND_OF_EXPOSURE_DETAILS,
			EpiDataDto.KIND_OF_EXPOSURE_OTHER,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), EpiDataDto.DATE_OF_PROPHYLAXIS, EpiDataDto.PROPHYLAXIS_STATUS, Arrays.asList(YesNoUnknown.YES), true);
	}

	private void updateEnvironmentalExposureFields() {
		boolean environmentalVisible =
			Arrays.stream(EpiDataDto.ENVIRONMENTAL_EXPOSURE_PROPERTIES).anyMatch(property -> getField(property).isVisible());
		getContent().getComponent(ENVIRONMENTAL_CAPTION_LOC).setVisible(environmentalVisible);

	}

	private void updateAnimalExposureFields() {
		boolean animalsVisible = Arrays.stream(EpiDataDto.ANIMAL_EXPOSURE_PROPERTIES).anyMatch(property -> getField(property).isVisible());
		getContent().getComponent(ANIMAL_CAPTION_LOC).setVisible(animalsVisible);

		boolean hadExposure = Arrays.stream(EpiDataDto.ANIMAL_EXPOSURE_PROPERTIES)
			.map(property -> getField(property).getValue())
			.anyMatch(value -> value == YesNoUnknown.YES);

		setVisible(hadExposure, EpiDataDto.EXPOSURE_DEPENDENT_PROPERTIES);

		boolean kindOfExposureVisible = Arrays.stream(EpiDataDto.KIND_OF_EXPOSURE_PROPERTIES).anyMatch(property -> getField(property).isVisible());
		getContent().getComponent(KIND_OF_EXPOSURE_LOC).setVisible(kindOfExposureVisible);

	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
