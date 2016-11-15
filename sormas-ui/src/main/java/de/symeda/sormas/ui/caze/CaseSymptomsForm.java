package de.symeda.sormas.ui.caze;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
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
							LayoutUtil.locs(SymptomsDto.FEVER, SymptomsDto.CHILLS, SymptomsDto.FATIGUE_WEAKNESS,
									SymptomsDto.SEIZURES, SymptomsDto.HEADACHE, SymptomsDto.NECK_STIFFNESS, 
									SymptomsDto.MUSCLE_PAIN, SymptomsDto.JOINT_PAIN, SymptomsDto.NAUSEA,
									SymptomsDto.VOMITING, SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.DIARRHEA,
									SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.RUNNY_NOSE,
									SymptomsDto.COUGH, SymptomsDto.SORE_THROAT, SymptomsDto.CHEST_PAIN,
									SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.HICCUPS, SymptomsDto.KOPLIKS_SPOTS,
									SymptomsDto.OTITIS_MEDIA, SymptomsDto.CONJUNCTIVITIS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE,
									SymptomsDto.JAUNDICE, SymptomsDto.SKIN_RASH, SymptomsDto.DEHYDRATION,
									SymptomsDto.SWOLLEN_LYMPH_NODES, SymptomsDto.OEDEMA, SymptomsDto.LETHARGY,
									SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.COMA_UNCONSCIOUS, SymptomsDto.SEPSIS,
									SymptomsDto.HIGH_BLOOD_PRESSURE, SymptomsDto.LOW_BLOOD_PRESSURE
									)),
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING, 
									SymptomsDto.EPISTAXIS, SymptomsDto.MELENA, SymptomsDto.HEMATEMESIS, 
									SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.HEMOPTYSIS, SymptomsDto.BLEEDING_VAGINA,
									SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA)+
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT)+
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT)));
	
	private final Disease disease;

	public CaseSymptomsForm(Disease disease) {
		super(SymptomsDto.class, SymptomsDto.I18N_PREFIX);
		this.disease = disease;
		if (disease == null) {
			throw new IllegalArgumentException("disease cannot be null");
		}
		addFields();
	}

	@Override
	protected void addFields() {
		
		if (disease == null) {
			// workaround to stop initialization until disease is set 
			return;
		}

		addField(SymptomsDto.ONSET_DATE);
		ComboBox temperature = addField(SymptomsDto.TEMPERATURE, ComboBox.class);
		for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
			temperature.addItem(temperatureValue);
			temperature.setItemCaption(temperatureValue, SymptomsHelper.getTemperatureString(temperatureValue));
		}
		
		addField(SymptomsDto.TEMPERATURE_SOURCE);

		addFields(SymptomsDto.FEVER, SymptomsDto.CHILLS, SymptomsDto.FATIGUE_WEAKNESS,
				SymptomsDto.SEIZURES, SymptomsDto.HEADACHE, SymptomsDto.NECK_STIFFNESS, 
				SymptomsDto.MUSCLE_PAIN, SymptomsDto.JOINT_PAIN, SymptomsDto.NAUSEA,
				SymptomsDto.VOMITING, SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.DIARRHEA,
				SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.RUNNY_NOSE,
				SymptomsDto.COUGH, SymptomsDto.SORE_THROAT, SymptomsDto.CHEST_PAIN,
				SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.HICCUPS, SymptomsDto.KOPLIKS_SPOTS,
				SymptomsDto.OTITIS_MEDIA, SymptomsDto.CONJUNCTIVITIS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE,
				SymptomsDto.JAUNDICE, SymptomsDto.SKIN_RASH, SymptomsDto.DEHYDRATION,
				SymptomsDto.SWOLLEN_LYMPH_NODES, SymptomsDto.OEDEMA, SymptomsDto.LETHARGY,
				SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.COMA_UNCONSCIOUS, SymptomsDto.SEPSIS,
				SymptomsDto.HIGH_BLOOD_PRESSURE, SymptomsDto.LOW_BLOOD_PRESSURE);
		addFields(SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING, 
				SymptomsDto.EPISTAXIS, SymptomsDto.MELENA, SymptomsDto.HEMATEMESIS, 
				SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.HEMOPTYSIS, SymptomsDto.BLEEDING_VAGINA,
				SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA);
		addFields(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT, 
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT);

		getFieldGroup().getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT).setCaption(null);
		getFieldGroup().getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT).setCaption(null);
		
		FieldHelper.setReadOnlyWhen(getFieldGroup(), 
				Arrays.asList(SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING,
				SymptomsDto.EPISTAXIS, SymptomsDto.MELENA, SymptomsDto.HEMATEMESIS, SymptomsDto.DIGESTED_BLOOD_VOMIT,
				SymptomsDto.HEMOPTYSIS, SymptomsDto.BLEEDING_VAGINA, SymptomsDto.PETECHIAE, SymptomsDto.HEMATURIA,
				SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT),
				SymptomsDto.UNEXPLAINED_BLEEDING, 
				Arrays.asList(null, SymptomState.NO, SymptomState.UNKNOWN), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				Arrays.asList(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT),
				SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				Arrays.asList(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT),
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, 
				Arrays.asList(SymptomState.YES), true);
		
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(SymptomsDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}

		setRequired(true, SymptomsDto.ONSET_DATE);
		// setReadOnly(true, );
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
