package de.symeda.sormas.ui.epidata;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.hospitalization.PreviousHospitalizationsField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final String EPI_DATA_CAPTION_LOC = "epiDataCaptionLoc";
	private static final String ANIMAL_CAPTION_LOC = "animalCaptionLoc";
	private static final String ENVIRONMENTAL_LOC = "environmentalLoc";
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.loc(EPI_DATA_CAPTION_LOC) +
			LayoutUtil.fluidRowLocs(EpiDataDto.BURIAL_ATTENDED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.BURIALS) +
			LayoutUtil.fluidRowLocs(EpiDataDto.GATHERING_ATTENDED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.GATHERINGS) +
			LayoutUtil.fluidRowLocs(EpiDataDto.TRAVELED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.TRAVELS) +
			LayoutUtil.loc(ANIMAL_CAPTION_LOC) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE_3,
									EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES,
									EpiDataDto.SWINE, EpiDataDto.BIRDS, EpiDataDto.POULTRY_EAT,
									EpiDataDto.POULTRY, EpiDataDto.POULTRY_DETAILS,
									EpiDataDto.POULTRY_SICK, EpiDataDto.POULTRY_SICK_DETAILS,
									EpiDataDto.POULTRY_DATE, EpiDataDto.POULTRY_LOCATION
							)
					),
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE_3,
									EpiDataDto.CATTLE, EpiDataDto.OTHER_ANIMALS,
									EpiDataDto.OTHER_ANIMALS_DETAILS, EpiDataDto.WILDBIRDS, EpiDataDto.WILDBIRDS_DETAILS,
									EpiDataDto.WILDBIRDS_DATE, EpiDataDto.WILDBIRDS_LOCATION,
									EpiDataDto.DATE_OF_LAST_EXPOSURE, EpiDataDto.PLACE_OF_LAST_EXPOSURE,
									EpiDataDto.ANIMAL_CONDITION
							)
					)
			) +
			LayoutUtil.loc(ENVIRONMENTAL_LOC) +
			LayoutUtil.fluidRowLocs(EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_BODY) +
			LayoutUtil.fluidRowLocs(EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_BODY_DETAILS) +
			LayoutUtil.fluidRow(LayoutUtil.locs(EpiDataDto.TICKBITE, EpiDataDto.FLEABITE))			
	;
	
	private final Disease disease;
	
	public EpiDataForm(Disease disease) {
		super(EpiDataDto.class, EpiDataDto.I18N_PREFIX);
		this.disease = disease;
		addFields();
	}
	
	@Override
	protected void addFields() {
		if (disease == null) {
			return;
		}
		
		OptionGroup burialAttendedField = addField(EpiDataDto.BURIAL_ATTENDED, OptionGroup.class);
		EpiDataBurialsField burialsField = addField(EpiDataDto.BURIALS, EpiDataBurialsField.class);
		OptionGroup gatheringAttendedField = addField(EpiDataDto.GATHERING_ATTENDED, OptionGroup.class);
		EpiDataGatheringsField gatheringsField = addField(EpiDataDto.GATHERINGS, EpiDataGatheringsField.class);
		OptionGroup traveledField = addField(EpiDataDto.TRAVELED, OptionGroup.class);
		EpiDataTravelsField travelsField = addField(EpiDataDto.TRAVELS, EpiDataTravelsField.class);
		addField(EpiDataDto.RODENTS, OptionGroup.class);
		addField(EpiDataDto.BATS, OptionGroup.class);
		addField(EpiDataDto.PRIMATES, OptionGroup.class);
		addField(EpiDataDto.SWINE, OptionGroup.class);
		addField(EpiDataDto.CATTLE, OptionGroup.class);
		addField(EpiDataDto.OTHER_ANIMALS, OptionGroup.class);
		addField(EpiDataDto.BIRDS, OptionGroup.class);
		addField(EpiDataDto.POULTRY_EAT, OptionGroup.class);			
		addField(EpiDataDto.WILDBIRDS, OptionGroup.class);
		addField(EpiDataDto.POULTRY, OptionGroup.class);
		addField(EpiDataDto.POULTRY_SICK, OptionGroup.class);
		addField(EpiDataDto.WATER_BODY, OptionGroup.class);
		addField(EpiDataDto.TICKBITE, OptionGroup.class);
		addField(EpiDataDto.FLEABITE, OptionGroup.class);
		addField(EpiDataDto.POULTRY_DETAILS, TextField.class);
		addField(EpiDataDto.POULTRY_SICK_DETAILS, TextField.class);
		addField(EpiDataDto.POULTRY_DATE, DateField.class);
		addField(EpiDataDto.POULTRY_LOCATION, TextField.class);
		addField(EpiDataDto.WILDBIRDS_DETAILS, TextField.class);
		addField(EpiDataDto.WILDBIRDS_DATE, DateField.class);
		addField(EpiDataDto.WILDBIRDS_LOCATION, TextField.class);
		addField(EpiDataDto.OTHER_ANIMALS_DETAILS, TextField.class);
		addField(EpiDataDto.WATER_SOURCE, ComboBox.class);
		addField(EpiDataDto.WATER_SOURCE_OTHER, TextField.class);
		addField(EpiDataDto.WATER_BODY_DETAILS, TextField.class);
		addField(EpiDataDto.DATE_OF_LAST_EXPOSURE, DateField.class);
		addField(EpiDataDto.PLACE_OF_LAST_EXPOSURE, TextField.class);
		addField(EpiDataDto.ANIMAL_CONDITION, OptionGroup.class);

		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(EpiDataDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
		
		styleAsOptionGroupHorizontal(Arrays.asList(EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES, EpiDataDto.SWINE, EpiDataDto.CATTLE,
				EpiDataDto.OTHER_ANIMALS, EpiDataDto.BIRDS, EpiDataDto.POULTRY_EAT, EpiDataDto.WILDBIRDS));
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.POULTRY_DETAILS), EpiDataDto.POULTRY, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.POULTRY_SICK, EpiDataDto.POULTRY, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.POULTRY_SICK_DETAILS, EpiDataDto.POULTRY_DATE, EpiDataDto.POULTRY_LOCATION), EpiDataDto.POULTRY_SICK, Arrays.asList(YesNoUnknown.YES), true, EpiDataDto.class, disease);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.OTHER_ANIMALS_DETAILS, EpiDataDto.OTHER_ANIMALS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.WILDBIRDS_DETAILS, EpiDataDto.WILDBIRDS_DATE, EpiDataDto.WILDBIRDS_LOCATION), EpiDataDto.WILDBIRDS, Arrays.asList(YesNoUnknown.YES), true, EpiDataDto.class, disease);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_SOURCE, Arrays.asList(WaterSource.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_BODY_DETAILS, EpiDataDto.WATER_BODY, Arrays.asList(YesNoUnknown.YES), true);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.BURIALS, EpiDataDto.BURIAL_ATTENDED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.GATHERINGS, EpiDataDto.GATHERING_ATTENDED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.TRAVELS, EpiDataDto.TRAVELED, Arrays.asList(YesNoUnknown.YES), true);
		
		List<String> epiDataFields = Arrays.asList(EpiDataDto.BURIAL_ATTENDED, EpiDataDto.GATHERING_ATTENDED, EpiDataDto.TRAVELED);
		
		for (String epiDataField : epiDataFields) {
			if (getFieldGroup().getField(epiDataField).isVisible()) {
				String epiDataCaptionLayout = LayoutUtil.h3(CssStyles.VSPACE_3, "Epidemiological Data") + LayoutUtil.divCss(CssStyles.VSPACE_3, I18nProperties.getFieldCaption("EpiData.epiDataHint"));
				Label epiDataCaptionLabel = new Label(epiDataCaptionLayout);
				epiDataCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(epiDataCaptionLabel, EPI_DATA_CAPTION_LOC);
				break;
			}
		}
		
		List<String> animalContacts = Arrays.asList(EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES, EpiDataDto.SWINE, EpiDataDto.CATTLE,
				EpiDataDto.OTHER_ANIMALS, EpiDataDto.WILDBIRDS, EpiDataDto.BIRDS, EpiDataDto.POULTRY_EAT, EpiDataDto.POULTRY, EpiDataDto.POULTRY_SICK);
		
		for (String animalContact : animalContacts) {
			if (getFieldGroup().getField(animalContact).isVisible()) {
				String animalCaptionLayout = LayoutUtil.h3(CssStyles.VSPACE_3, "Animal Contacts") + LayoutUtil.divCss(CssStyles.VSPACE_3, I18nProperties.getFieldCaption("EpiData.animalHint"));
				Label animalCaptionLabel = new Label(animalCaptionLayout);
				animalCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(animalCaptionLabel, ANIMAL_CAPTION_LOC);
				break;
			}
		}
		
		List<String> environmentalExposures = Arrays.asList(EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_BODY, EpiDataDto.TICKBITE, EpiDataDto.FLEABITE);
		
		for (String environmentalExp : environmentalExposures) {
			if (getFieldGroup().getField(environmentalExp).isVisible()) {
				String environmentalCaptionLayout = LayoutUtil.h3(CssStyles.VSPACE_3, "Environmental Exposure");
				Label environmentalCaptionLabel = new Label(environmentalCaptionLayout);
				environmentalCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(environmentalCaptionLabel, ENVIRONMENTAL_LOC);
				break;
			}
		}
		
		burialAttendedField.addValueChangeListener(e -> {
			updateBurialsHint(burialAttendedField, burialsField);
		});
		burialsField.addValueChangeListener(e -> {
			updateBurialsHint(burialAttendedField, burialsField);
		});
		gatheringAttendedField.addValueChangeListener(e -> {
			updateGatheringsHint(gatheringAttendedField, gatheringsField);
		});
		gatheringsField.addValueChangeListener(e -> {
			updateGatheringsHint(gatheringAttendedField, gatheringsField);
		});
		traveledField.addValueChangeListener(e -> {
			updateTravelsHint(traveledField, travelsField);
		});
		travelsField.addValueChangeListener(e -> {
			updateTravelsHint(traveledField, travelsField);
		});
	}
	
	private void updateBurialsHint(OptionGroup burialAttendedField, EpiDataBurialsField burialsField) {
		YesNoUnknown value = (YesNoUnknown) burialAttendedField.getValue();
		if (value == YesNoUnknown.YES && burialsField.getValue().size() == 0) {
			burialAttendedField.setComponentError(new UserError("Please add an entry to the list below if there is any data available to you."));
		} else {
			burialAttendedField.setComponentError(null);
		}
	}
	
	private void updateGatheringsHint(OptionGroup gatheringAttendedField, EpiDataGatheringsField gatheringsField) {
		YesNoUnknown value = (YesNoUnknown) gatheringAttendedField.getValue();
		if (value == YesNoUnknown.YES && gatheringsField.getValue().size() == 0) {
			gatheringAttendedField.setComponentError(new UserError("Please add an entry to the list below if there is any data available to you."));
		} else {
			gatheringAttendedField.setComponentError(null);
		}
	}
	
	private void updateTravelsHint(OptionGroup traveledField, EpiDataTravelsField travelsField) {
		YesNoUnknown value = (YesNoUnknown) traveledField.getValue();
		if (value == YesNoUnknown.YES && travelsField.getValue().size() == 0) {
			traveledField.setComponentError(new UserError("Please add an entry to the list below if there is any data available to you."));
		} else {
			traveledField.setComponentError(null);
		}
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
