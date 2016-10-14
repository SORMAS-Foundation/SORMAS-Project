package de.symeda.sormas.ui.surveillance.caze;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseSymptomsForm extends AbstractEditForm<SymptomsDto> {

	private static final String HTML_LAYOUT = LayoutUtil.h3(CssStyles.VSPACE3, "Case symptoms")
			+ LayoutUtil.divCss(CssStyles.VSPACE3,
				LayoutUtil.fluidRowLocs(SymptomsDto.ONSET_DATE, "", SymptomsDto.TEMPERATURE, SymptomsDto.TEMPERATURE_SOURCE))
			+ LayoutUtil.divCss(CssStyles.VSPACE3, I18nProperties.getFieldCaption("Symptoms.hint"))
			+ LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locs(SymptomsDto.FEVER, SymptomsDto.VOMITING_NAUSEA, SymptomsDto.DIARRHEA,
									SymptomsDto.INTENSE_FATIGUE_WEAKNESS, SymptomsDto.ANOREXIA_APPETITE_LOSS,
									SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.CHEST_PAIN, SymptomsDto.MUSCLE_PAIN,
									SymptomsDto.JOINT_PAIN, SymptomsDto.HEADACHE, SymptomsDto.COUGH,
									SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.DIFFICULTY_SWALLOWING,
									SymptomsDto.SORE_THROAT, SymptomsDto.JAUNDICE, SymptomsDto.CONJUNCTIVITIS,
									SymptomsDto.SKIN_RASH, SymptomsDto.HICCUPS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE,
									SymptomsDto.COMA_UNCONSCIOUS, SymptomsDto.CONFUSED_DISORIENTED)),
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING,
									SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.EPISTAXIS, SymptomsDto.MELENA,
									SymptomsDto.HEMATEMESIS, SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.HEMOPTYSIS,
									SymptomsDto.BLEEDING_VAGINA, SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA)+
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.OTHER_HEMORRHAGIC, SymptomsDto.OTHER_HEMORRHAGIC_TEXT)+
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.OTHER_NON_HEMORRHAGIC, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS)));

	public CaseSymptomsForm() {
		super(SymptomsDto.class, SymptomsDto.I18N_PREFIX);
	}

	@Override
	protected void addFields() {

		addField(SymptomsDto.ONSET_DATE);
		ComboBox temperature = addField(SymptomsDto.TEMPERATURE, ComboBox.class);
		for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
			temperature.addItem(temperatureValue);
			temperature.setItemCaption(temperatureValue, SymptomsHelper.getTemperatureString(temperatureValue));
		}
		
		addField(SymptomsDto.TEMPERATURE_SOURCE);

		addFields(SymptomsDto.FEVER, SymptomsDto.VOMITING_NAUSEA, SymptomsDto.DIARRHEA,
				SymptomsDto.INTENSE_FATIGUE_WEAKNESS, SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.ABDOMINAL_PAIN,
				SymptomsDto.CHEST_PAIN, SymptomsDto.MUSCLE_PAIN, SymptomsDto.JOINT_PAIN, SymptomsDto.HEADACHE,
				SymptomsDto.COUGH, SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.DIFFICULTY_SWALLOWING,
				SymptomsDto.SORE_THROAT, SymptomsDto.JAUNDICE, SymptomsDto.CONJUNCTIVITIS, SymptomsDto.SKIN_RASH,
				SymptomsDto.HICCUPS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, SymptomsDto.COMA_UNCONSCIOUS,
				SymptomsDto.CONFUSED_DISORIENTED);
		addFields(SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING,
				SymptomsDto.EPISTAXIS, SymptomsDto.MELENA, SymptomsDto.HEMATEMESIS, SymptomsDto.DIGESTED_BLOOD_VOMIT,
				SymptomsDto.HEMOPTYSIS, SymptomsDto.BLEEDING_VAGINA, SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA,
				SymptomsDto.OTHER_HEMORRHAGIC, SymptomsDto.OTHER_HEMORRHAGIC_TEXT, SymptomsDto.OTHER_NON_HEMORRHAGIC,
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS);

		FieldHelper.setReadOnlyWhen(getFieldGroup(), 
				Arrays.asList(SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING,
				SymptomsDto.EPISTAXIS, SymptomsDto.MELENA, SymptomsDto.HEMATEMESIS, SymptomsDto.DIGESTED_BLOOD_VOMIT,
				SymptomsDto.HEMOPTYSIS, SymptomsDto.BLEEDING_VAGINA, SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA,
				SymptomsDto.OTHER_HEMORRHAGIC, SymptomsDto.OTHER_HEMORRHAGIC_TEXT),
				SymptomsDto.UNEXPLAINED_BLEEDING, 
				Arrays.asList(null, SymptomState.NO, SymptomState.UNKNOWN), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				Arrays.asList(SymptomsDto.OTHER_HEMORRHAGIC_TEXT),
				SymptomsDto.OTHER_HEMORRHAGIC, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				Arrays.asList(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS),
				SymptomsDto.OTHER_NON_HEMORRHAGIC, 
				Arrays.asList(SymptomState.YES), true);

		setRequired(true, SymptomsDto.ONSET_DATE);
		// setReadOnly(true, );
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
