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
package de.symeda.sormas.ui.epidata;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

@SuppressWarnings("serial")
public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final String EPI_DATA_CAPTION_LOC = "epiDataCaptionLoc";
	private static final String ANIMAL_CAPTION_LOC = "animalCaptionLoc";
	private static final String ENVIRONMENTAL_LOC = "environmentalLoc";
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.loc(EPI_DATA_CAPTION_LOC) +
			LayoutUtil.fluidRowLocs(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE, EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE) +
			LayoutUtil.fluidRowLocs(EpiDataDto.DIRECT_CONTACT_PROBABLE_CASE, EpiDataDto.AREA_CONFIRMED_CASES) +
			LayoutUtil.fluidRowLocs(EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE, EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE) +
			LayoutUtil.fluidRowLocs(EpiDataDto.PERCUTANEOUS_CASE_BLOOD, EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE) +
			LayoutUtil.fluidRowLoc(6, EpiDataDto.BURIAL_ATTENDED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.BURIALS) +
			LayoutUtil.fluidRowLoc(6, EpiDataDto.GATHERING_ATTENDED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.GATHERINGS) +
			LayoutUtil.fluidRowLoc(6, EpiDataDto.TRAVELED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.TRAVELS) +
			LayoutUtil.loc(ANIMAL_CAPTION_LOC) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE_3,
									EpiDataDto.AREA_INFECTED_ANIMALS,
									EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES,
									EpiDataDto.SWINE, EpiDataDto.BIRDS, EpiDataDto.CATTLE, 
									EpiDataDto.SICK_DEAD_ANIMALS, EpiDataDto.SICK_DEAD_ANIMALS_DETAILS,
									EpiDataDto.SICK_DEAD_ANIMALS_DATE, EpiDataDto.SICK_DEAD_ANIMALS_LOCATION
							)
					),
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE_3,
									EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA, EpiDataDto.EATING_RAW_ANIMALS, 
									EpiDataDto.EATING_RAW_ANIMALS_DETAILS,
									EpiDataDto.OTHER_ANIMALS, EpiDataDto.OTHER_ANIMALS_DETAILS, 
									EpiDataDto.DATE_OF_LAST_EXPOSURE, EpiDataDto.PLACE_OF_LAST_EXPOSURE,
									EpiDataDto.ANIMAL_CONDITION
							)
					)
			) +
			LayoutUtil.fluidRowLoc(6, EpiDataDto.KIND_OF_EXPOSURE) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE_3,
									EpiDataDto.KIND_OF_EXPOSURE_BITE, 
									EpiDataDto.KIND_OF_EXPOSURE_TOUCH, 
									EpiDataDto.KIND_OF_EXPOSURE_SCRATCH)
					),
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE_3,
									EpiDataDto.KIND_OF_EXPOSURE_LICK,
									EpiDataDto.KIND_OF_EXPOSURE_OTHER, 
									EpiDataDto.KIND_OF_EXPOSURE_DETAILS
							)
					)
			) +
			LayoutUtil.loc(ENVIRONMENTAL_LOC) +
			LayoutUtil.fluidRowLocs(EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_BODY) +
			LayoutUtil.fluidRowLocs(EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_BODY_DETAILS) +
			LayoutUtil.fluidRowLocs(EpiDataDto.TICK_BITE, EpiDataDto.FLEA_BITE)			
	;
	
	private final Disease disease;
	private final ViewMode viewMode;
	
	public EpiDataForm(Disease disease, UserRight editOrCreateUserRight, ViewMode viewMode) {
		super(EpiDataDto.class, EpiDataDto.I18N_PREFIX, editOrCreateUserRight);
		this.disease = disease;
		this.viewMode = viewMode;
		addFields();
	}
	
	@Override
	protected void addFields() {
		if (disease == null) {
			return;
		}
		
		addFields(EpiDataDto.DIRECT_CONTACT_CONFIRMED_CASE, EpiDataDto.DIRECT_CONTACT_PROBABLE_CASE, 
				EpiDataDto.CLOSE_CONTACT_PROBABLE_CASE, EpiDataDto.AREA_CONFIRMED_CASES,
				EpiDataDto.PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE, EpiDataDto.PERCUTANEOUS_CASE_BLOOD,
				EpiDataDto.DIRECT_CONTACT_DEAD_UNSAFE, EpiDataDto.PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE);
		
		OptionGroup burialAttendedField = addField(EpiDataDto.BURIAL_ATTENDED, OptionGroup.class);
		CssStyles.style(burialAttendedField, CssStyles.ERROR_COLOR_PRIMARY);
		EpiDataBurialsField burialsField = addField(EpiDataDto.BURIALS, EpiDataBurialsField.class);
		OptionGroup gatheringAttendedField = addField(EpiDataDto.GATHERING_ATTENDED, OptionGroup.class);
		CssStyles.style(gatheringAttendedField, CssStyles.ERROR_COLOR_PRIMARY);
		EpiDataGatheringsField gatheringsField = addField(EpiDataDto.GATHERINGS, EpiDataGatheringsField.class);
		OptionGroup traveledField = addField(EpiDataDto.TRAVELED, OptionGroup.class);
		CssStyles.style(traveledField, CssStyles.ERROR_COLOR_PRIMARY);
		EpiDataTravelsField travelsField = addField(EpiDataDto.TRAVELS, EpiDataTravelsField.class);
		
		addFields(EpiDataDto.AREA_INFECTED_ANIMALS, EpiDataDto.SICK_DEAD_ANIMALS,
				EpiDataDto.SICK_DEAD_ANIMALS_DETAILS, EpiDataDto.SICK_DEAD_ANIMALS_DATE, EpiDataDto.SICK_DEAD_ANIMALS_LOCATION,
				EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA, 
				EpiDataDto.EATING_RAW_ANIMALS,EpiDataDto.EATING_RAW_ANIMALS_DETAILS,
				EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.BIRDS, EpiDataDto.PRIMATES, EpiDataDto.SWINE, EpiDataDto.CATTLE, 
				EpiDataDto.OTHER_ANIMALS, EpiDataDto.OTHER_ANIMALS_DETAILS, 
				EpiDataDto.ANIMAL_CONDITION, EpiDataDto.DATE_OF_LAST_EXPOSURE, EpiDataDto.PLACE_OF_LAST_EXPOSURE);
		
		OptionGroup kindOfExposureField = addField(EpiDataDto.KIND_OF_EXPOSURE, OptionGroup.class);
		CssStyles.style(kindOfExposureField, CssStyles.ERROR_COLOR_PRIMARY);
		addFields(EpiDataDto.KIND_OF_EXPOSURE_BITE, EpiDataDto.KIND_OF_EXPOSURE_TOUCH,
				EpiDataDto.KIND_OF_EXPOSURE_SCRATCH, EpiDataDto.KIND_OF_EXPOSURE_LICK, 
				EpiDataDto.KIND_OF_EXPOSURE_OTHER, EpiDataDto.KIND_OF_EXPOSURE_DETAILS);
		
		addFields(EpiDataDto.WATER_BODY, EpiDataDto.WATER_BODY_DETAILS, 
				EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_SOURCE_OTHER, 
				EpiDataDto.TICK_BITE, EpiDataDto.FLEA_BITE);

		initializeVisibilitiesAndAllowedVisibilities(disease, viewMode);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.SICK_DEAD_ANIMALS_DETAILS, EpiDataDto.SICK_DEAD_ANIMALS_DATE, EpiDataDto.SICK_DEAD_ANIMALS_LOCATION), EpiDataDto.SICK_DEAD_ANIMALS, Arrays.asList(YesNoUnknown.YES), true, EpiDataDto.class, disease);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.EATING_RAW_ANIMALS_DETAILS), EpiDataDto.EATING_RAW_ANIMALS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.OTHER_ANIMALS_DETAILS, EpiDataDto.OTHER_ANIMALS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_SOURCE, Arrays.asList(WaterSource.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_BODY_DETAILS, EpiDataDto.WATER_BODY, Arrays.asList(YesNoUnknown.YES), true);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.BURIALS, EpiDataDto.BURIAL_ATTENDED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.GATHERINGS, EpiDataDto.GATHERING_ATTENDED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.TRAVELS, EpiDataDto.TRAVELED, Arrays.asList(YesNoUnknown.YES), true);
		
		List<String> epiDataFields = Arrays.asList(EpiDataDto.BURIAL_ATTENDED, EpiDataDto.GATHERING_ATTENDED, EpiDataDto.TRAVELED);
		
		for (String epiDataField : epiDataFields) {
			if (getFieldGroup().getField(epiDataField).isVisible()) {
				String epiDataCaptionLayout = LayoutUtil.h3(I18nProperties.getCaption(EpiDataDto.I18N_PREFIX)) + LayoutUtil.divsCss(CssStyles.VSPACE_3, I18nProperties.getString(Strings.messageEpiDataHint));
				Label epiDataCaptionLabel = new Label(epiDataCaptionLayout);
				epiDataCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(epiDataCaptionLabel, EPI_DATA_CAPTION_LOC);
				break;
			}
		}
		
		List<String> animalContacts = Arrays.asList(EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES, EpiDataDto.SWINE, EpiDataDto.CATTLE,
				EpiDataDto.OTHER_ANIMALS, EpiDataDto.SICK_DEAD_ANIMALS, EpiDataDto.EATING_RAW_ANIMALS_IN_INFECTED_AREA, EpiDataDto.EATING_RAW_ANIMALS);
		
		for (String animalContact : animalContacts) {
			if (getFieldGroup().getField(animalContact).isVisible()) {
				String animalCaptionLayout = LayoutUtil.h3(I18nProperties.getString(Strings.headingAnimalContacts)) + LayoutUtil.divsCss(CssStyles.VSPACE_3, I18nProperties.getString(Strings.messageAnimalContactsHint));
				Label animalCaptionLabel = new Label(animalCaptionLayout);
				animalCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(animalCaptionLabel, ANIMAL_CAPTION_LOC);
				break;
			}
		}
		
		List<String> environmentalExposures = Arrays.asList(EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_BODY, EpiDataDto.TICK_BITE, EpiDataDto.FLEA_BITE);
		
		for (String environmentalExp : environmentalExposures) {
			if (getFieldGroup().getField(environmentalExp).isVisible()) {
				String environmentalCaptionLayout = LayoutUtil.h3(I18nProperties.getString(Strings.headingEnvironmentalExposure));
				Label environmentalCaptionLabel = new Label(environmentalCaptionLayout);
				environmentalCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(environmentalCaptionLabel, ENVIRONMENTAL_LOC);
				break;
			}
		}
		
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.KIND_OF_EXPOSURE_BITE, EpiDataDto.KIND_OF_EXPOSURE, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.KIND_OF_EXPOSURE_TOUCH, EpiDataDto.KIND_OF_EXPOSURE, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.KIND_OF_EXPOSURE_SCRATCH, EpiDataDto.KIND_OF_EXPOSURE, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.KIND_OF_EXPOSURE_LICK, EpiDataDto.KIND_OF_EXPOSURE, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.KIND_OF_EXPOSURE_OTHER, EpiDataDto.KIND_OF_EXPOSURE, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.KIND_OF_EXPOSURE_DETAILS, EpiDataDto.KIND_OF_EXPOSURE_OTHER, Arrays.asList(YesNoUnknown.YES), true);
		
		burialAttendedField.addValueChangeListener(e -> {
			updateBurialsHint(burialAttendedField, burialsField);
		});
		burialsField.addValueChangeListener(e -> {
			updateBurialsHint(burialAttendedField, burialsField);
			burialAttendedField.setEnabled(burialsField.getValue() == null || burialsField.getValue().size() == 0);
		});
		gatheringAttendedField.addValueChangeListener(e -> {
			updateGatheringsHint(gatheringAttendedField, gatheringsField);
		});
		gatheringsField.addValueChangeListener(e -> {
			updateGatheringsHint(gatheringAttendedField, gatheringsField);
			gatheringAttendedField.setEnabled(gatheringsField.getValue() == null || gatheringsField.getValue().size() == 0);
		});
		traveledField.addValueChangeListener(e -> {
			updateTravelsHint(traveledField, travelsField);
		});
		travelsField.addValueChangeListener(e -> {
			updateTravelsHint(traveledField, travelsField);
			traveledField.setEnabled(travelsField.getValue() == null || travelsField.getValue().size() == 0);
		});
	}
	
	private void updateBurialsHint(OptionGroup burialAttendedField, EpiDataBurialsField burialsField) {
		YesNoUnknown value = (YesNoUnknown) burialAttendedField.getValue();
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT) && value == YesNoUnknown.YES && (burialsField == null || burialsField.getValue() == null || burialsField.getValue().size() == 0)) {
			burialAttendedField.setComponentError(new UserError(I18nProperties.getValidationError(Validations.softAddEntryToList)));
		} else {
			burialAttendedField.setComponentError(null);
		}
	}
	
	private void updateGatheringsHint(OptionGroup gatheringAttendedField, EpiDataGatheringsField gatheringsField) {
		YesNoUnknown value = (YesNoUnknown) gatheringAttendedField.getValue();
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT) && value == YesNoUnknown.YES && (gatheringsField == null || gatheringsField.getValue() == null || gatheringsField.getValue().size() == 0)) {
			gatheringAttendedField.setComponentError(new UserError(I18nProperties.getValidationError(Validations.softAddEntryToList)));
		} else {
			gatheringAttendedField.setComponentError(null);
		}
	}
	
	private void updateTravelsHint(OptionGroup traveledField, EpiDataTravelsField travelsField) {
		YesNoUnknown value = (YesNoUnknown) traveledField.getValue();
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT) && value == YesNoUnknown.YES && (travelsField == null || travelsField.getValue() == null || travelsField.getValue().size() == 0)) {
			traveledField.setComponentError(new UserError(I18nProperties.getValidationError(Validations.softAddEntryToList)));
		} else {
			traveledField.setComponentError(null);
		}
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
