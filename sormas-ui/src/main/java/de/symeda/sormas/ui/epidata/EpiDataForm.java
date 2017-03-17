package de.symeda.sormas.ui.epidata;

import java.util.Arrays;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.YesNoUnknown;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final String ANIMAL_CAPTION_LOC = "animalCaptionLoc";
	private static final String ENVIRONMENTAL_LOC = "environmentalLoc";
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(CssStyles.VSPACE3, "Epidemiological data") +
			LayoutUtil.fluidRowLocs(EpiDataDto.BURIAL_ATTENDED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.BURIALS) +
			LayoutUtil.fluidRowLocs(EpiDataDto.GATHERING_ATTENDED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.GATHERINGS) +
			LayoutUtil.fluidRowLocs(EpiDataDto.TRAVELED) +
			LayoutUtil.fluidRowLocs(EpiDataDto.TRAVELS) +
			LayoutUtil.loc(ANIMAL_CAPTION_LOC) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES,
									EpiDataDto.SWINE, EpiDataDto.BIRDS, EpiDataDto.POULTRY_EAT,
									EpiDataDto.POULTRY, EpiDataDto.POULTRY_DETAILS,
									EpiDataDto.POULTRY_SICK, EpiDataDto.POULTRY_SICK_DETAILS,
									EpiDataDto.POULTRY_DATE, EpiDataDto.POULTRY_LOCATION
							)
					),
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									EpiDataDto.CATTLE, EpiDataDto.OTHER_ANIMALS,
									EpiDataDto.OTHER_ANIMALS_DETAILS, EpiDataDto.WILDBIRDS, EpiDataDto.WILDBIRDS_DETAILS,
									EpiDataDto.WILDBIRDS_DATE, EpiDataDto.WILDBIRDS_LOCATION
							)
					)
			) +
			LayoutUtil.loc(ENVIRONMENTAL_LOC) +
			LayoutUtil.fluidRowLocs(EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_BODY) +
			LayoutUtil.fluidRowLocs(EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_BODY_DETAILS) +
			LayoutUtil.fluidRowLocs(EpiDataDto.TICKBITE, "")			
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
		
		addField(EpiDataDto.BURIAL_ATTENDED, OptionGroup.class);
		addField(EpiDataDto.BURIALS, EpiDataBurialsField.class);
		addField(EpiDataDto.GATHERING_ATTENDED, OptionGroup.class);
		addField(EpiDataDto.GATHERINGS, EpiDataGatheringsField.class);
		addField(EpiDataDto.TRAVELED, OptionGroup.class);
		addField(EpiDataDto.TRAVELS, EpiDataTravelsField.class);
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

		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(EpiDataDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
		
		styleAsRow(Arrays.asList(EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES, EpiDataDto.SWINE, EpiDataDto.CATTLE,
				EpiDataDto.OTHER_ANIMALS, EpiDataDto.BIRDS, EpiDataDto.POULTRY_EAT, EpiDataDto.WILDBIRDS));
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.POULTRY_DETAILS), EpiDataDto.POULTRY, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.POULTRY_SICK_DETAILS, EpiDataDto.POULTRY_DATE, EpiDataDto.POULTRY_LOCATION), EpiDataDto.POULTRY_SICK, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.OTHER_ANIMALS_DETAILS, EpiDataDto.OTHER_ANIMALS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EpiDataDto.WILDBIRDS_DETAILS, EpiDataDto.WILDBIRDS_DATE, EpiDataDto.WILDBIRDS_LOCATION), EpiDataDto.WILDBIRDS, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_SOURCE_OTHER, EpiDataDto.WATER_SOURCE, Arrays.asList(WaterSource.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.WATER_BODY_DETAILS, EpiDataDto.WATER_BODY, Arrays.asList(YesNoUnknown.YES), true);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.BURIALS, EpiDataDto.BURIAL_ATTENDED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.GATHERINGS, EpiDataDto.GATHERING_ATTENDED, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.TRAVELS, EpiDataDto.TRAVELED, Arrays.asList(YesNoUnknown.YES), true);
		
		List<String> animalContacts = Arrays.asList(EpiDataDto.RODENTS, EpiDataDto.BATS, EpiDataDto.PRIMATES, EpiDataDto.SWINE, EpiDataDto.CATTLE,
				EpiDataDto.OTHER_ANIMALS, EpiDataDto.WILDBIRDS, EpiDataDto.BIRDS, EpiDataDto.POULTRY_EAT, EpiDataDto.POULTRY, EpiDataDto.POULTRY_SICK);
		
		for (String animalContact : animalContacts) {
			if (getFieldGroup().getField(animalContact).isVisible()) {
				String animalCaptionLayout = LayoutUtil.h3(CssStyles.VSPACE3, "Animal contacts") + LayoutUtil.divCss(CssStyles.VSPACE3, I18nProperties.getFieldCaption("EpiData.hint"));
				Label animalCaptionLabel = new Label(animalCaptionLayout);
				animalCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(animalCaptionLabel, ANIMAL_CAPTION_LOC);
				break;
			}
		}
		
		List<String> environmentalExposures = Arrays.asList(EpiDataDto.WATER_SOURCE, EpiDataDto.WATER_BODY, EpiDataDto.TICKBITE);
		
		for (String environmentalExp : environmentalExposures) {
			if (getFieldGroup().getField(environmentalExp).isVisible()) {
				String environmentalCaptionLayout = LayoutUtil.h3(CssStyles.VSPACE3, "Environmental exposure");
				Label environmentalCaptionLabel = new Label(environmentalCaptionLayout);
				environmentalCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(environmentalCaptionLabel, ENVIRONMENTAL_LOC);
				break;
			}
		}
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
