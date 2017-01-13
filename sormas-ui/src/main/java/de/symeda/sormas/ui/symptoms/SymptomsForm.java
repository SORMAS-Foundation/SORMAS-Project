package de.symeda.sormas.ui.symptoms;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;

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
public class SymptomsForm extends AbstractEditForm<SymptomsDto> {

	private static final String HTML_LAYOUT = LayoutUtil.h3(CssStyles.VSPACE3, "Symptoms")
			+ LayoutUtil.divCss(CssStyles.VSPACE3,
				LayoutUtil.fluidRowLocs(SymptomsDto.ONSET_DATE, SymptomsDto.ONSET_SYMPTOM, SymptomsDto.TEMPERATURE, SymptomsDto.TEMPERATURE_SOURCE))
			+ LayoutUtil.divCss(CssStyles.VSPACE3, I18nProperties.getFieldCaption("Symptoms.hint"))
			+ LayoutUtil.fluidRow(
					LayoutUtil.fluidColumn(6, 0,
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.FEVER, SymptomsDto.VOMITING, SymptomsDto.DIARRHEA, SymptomsDto.BLOOD_IN_STOOL,
									SymptomsDto.NAUSEA, SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.HEADACHE, SymptomsDto.MUSCLE_PAIN,
									SymptomsDto.FATIGUE_WEAKNESS)
							+ LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING,
									SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.NOSE_BLEEDING, SymptomsDto.BLOODY_BLACK_STOOL,
									SymptomsDto.RED_BLOOD_VOMIT, SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.COUGHING_BLOOD,
									SymptomsDto.BLEEDING_VAGINA, SymptomsDto.SKIN_BRUISING, SymptomsDto.BLOOD_URINE)
							+ LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT)),
					LayoutUtil.fluidColumn(6,  0, 
							LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.SKIN_RASH, SymptomsDto.NECK_STIFFNESS, SymptomsDto.SORE_THROAT, SymptomsDto.COUGH,
									SymptomsDto.RUNNY_NOSE, SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.CHEST_PAIN,
									SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.SEIZURES, SymptomsDto.ALTERED_CONSCIOUSNESS,
									SymptomsDto.CONJUNCTIVITIS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, SymptomsDto.KOPLIKS_SPOTS,
									SymptomsDto.THROBOCYTOPENIA, SymptomsDto.OTITIS_MEDIA, SymptomsDto.HEARINGLOSS, SymptomsDto.DEHYDRATION,
									SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.JOINT_PAIN,
									SymptomsDto.SHOCK, SymptomsDto.HICCUPS)
							+ LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT)
							+ LayoutUtil.locsCss(CssStyles.VSPACE3,
									SymptomsDto.SYMPTOMS_COMMENTS)));
									
	private final Disease disease;

	public SymptomsForm(Disease disease) {
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
		addField(SymptomsDto.ONSET_SYMPTOM);
		ComboBox temperature = addField(SymptomsDto.TEMPERATURE, ComboBox.class);
		for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
			temperature.addItem(temperatureValue);
			temperature.setItemCaption(temperatureValue, SymptomsHelper.getTemperatureString(temperatureValue));
		}
		
		addField(SymptomsDto.TEMPERATURE_SOURCE);

		addFields(SymptomsDto.FEVER, SymptomsDto.VOMITING, SymptomsDto.DIARRHEA, SymptomsDto.BLOOD_IN_STOOL, SymptomsDto.NAUSEA, 
				SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.HEADACHE, SymptomsDto.MUSCLE_PAIN, SymptomsDto.FATIGUE_WEAKNESS);
		addFields(SymptomsDto.SKIN_RASH, SymptomsDto.NECK_STIFFNESS, SymptomsDto.SORE_THROAT, SymptomsDto.COUGH, SymptomsDto.RUNNY_NOSE, 
				SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.CHEST_PAIN, SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.SEIZURES, SymptomsDto.ALTERED_CONSCIOUSNESS,
				SymptomsDto.CONJUNCTIVITIS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, SymptomsDto.KOPLIKS_SPOTS);
		addFields(SymptomsDto.THROBOCYTOPENIA, SymptomsDto.OTITIS_MEDIA, SymptomsDto.HEARINGLOSS, SymptomsDto.DEHYDRATION, SymptomsDto.ANOREXIA_APPETITE_LOSS, 
				SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.JOINT_PAIN, SymptomsDto.SHOCK, SymptomsDto.HICCUPS, SymptomsDto.SYMPTOMS_COMMENTS);
		addFields(SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.NOSE_BLEEDING, 
				SymptomsDto.BLOODY_BLACK_STOOL, SymptomsDto.RED_BLOOD_VOMIT, SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.COUGHING_BLOOD,
				SymptomsDto.BLEEDING_VAGINA, SymptomsDto.SKIN_BRUISING, SymptomsDto.BLOOD_URINE);
		addFields(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS,
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT);

		getFieldGroup().getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT).setCaption(null);
		getFieldGroup().getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT).setCaption(null);
		
		List<Object> unexplainedBleedingFields = Arrays.asList(SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.NOSE_BLEEDING, 
				SymptomsDto.BLOODY_BLACK_STOOL, SymptomsDto.RED_BLOOD_VOMIT, SymptomsDto.DIGESTED_BLOOD_VOMIT, 
				SymptomsDto.COUGHING_BLOOD, SymptomsDto.BLEEDING_VAGINA, SymptomsDto.SKIN_BRUISING, 
				SymptomsDto.BLOOD_URINE, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), 
				unexplainedBleedingFields,
				SymptomsDto.UNEXPLAINED_BLEEDING, 
				Arrays.asList(SymptomState.YES), true);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), 
				SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT,
				SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT,
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, 
				Arrays.asList(SymptomState.YES), true);
		
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(SymptomsDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
		
		getFieldGroup().getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT).setVisible(false);
		getFieldGroup().getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT).setVisible(false);
		for (Object fieldId : unexplainedBleedingFields) {
			getFieldGroup().getField(fieldId).setVisible(false);
		}

		List<String> symptomFieldIds = Arrays.asList(SymptomsDto.FEVER, SymptomsDto.VOMITING, 
				SymptomsDto.DIARRHEA, SymptomsDto.BLOOD_IN_STOOL, SymptomsDto.NAUSEA, SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.HEADACHE, 
				SymptomsDto.MUSCLE_PAIN, SymptomsDto.FATIGUE_WEAKNESS, SymptomsDto.SKIN_RASH, SymptomsDto.NECK_STIFFNESS, SymptomsDto.SORE_THROAT, 
				SymptomsDto.COUGH, SymptomsDto.RUNNY_NOSE, SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.CHEST_PAIN, SymptomsDto.CONFUSED_DISORIENTED, 
				SymptomsDto.SEIZURES, SymptomsDto.ALTERED_CONSCIOUSNESS, SymptomsDto.CONJUNCTIVITIS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, 
				SymptomsDto.KOPLIKS_SPOTS, SymptomsDto.THROBOCYTOPENIA, SymptomsDto.OTITIS_MEDIA, SymptomsDto.HEARINGLOSS, SymptomsDto.DEHYDRATION,
				SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.JOINT_PAIN, SymptomsDto.SHOCK, 
				SymptomsDto.HICCUPS, SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS);
		
		setRequiredWhenSymptomatic(getFieldGroup(), SymptomsDto.ONSET_DATE, symptomFieldIds, Arrays.asList(SymptomState.YES));
		setRequiredWhenSymptomatic(getFieldGroup(), SymptomsDto.ONSET_SYMPTOM, symptomFieldIds, Arrays.asList(SymptomState.YES));
		setRequiredWhenSymptomatic(getFieldGroup(), SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT, Arrays.asList(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS), Arrays.asList(SymptomState.YES));
		setRequiredWhenSymptomatic(getFieldGroup(), SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT, Arrays.asList(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS), Arrays.asList(SymptomState.YES));
		
		// setReadOnly(true, );
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
	/**
	 * Sets the field with the targetPropertyId to required when at least one of the
	 * fields associated with the sourcePropertyIds has its value set to "Yes" or the
	 * field associated with textFieldPropertyId is not empty. This method is intended to be used
	 * with the onsetDate and onsetSymptom fields.
	 * 
	 * @param fieldGroup
	 * @param targetPropertyId
	 * @param sourcePropertyIds
	 * @param sourceValues
	 */
	public static void setRequiredWhenSymptomatic(FieldGroup fieldGroup, Object targetPropertyId,
			List<String> sourcePropertyIds, List<Object> sourceValues) {
				
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if(sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}
		}
		
		// Initialize
		final Field targetField = fieldGroup.getField(targetPropertyId);
		targetField.setRequired(isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues));
		
		// Add listeners
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			sourceField.addValueChangeListener(event -> {
				targetField.setRequired(isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues));
			});
		}
		
	}
	
	/**
	 * Returns true if if the value of any field associated with the sourcePropertyIds
	 * is set to one of the values contained in sourceValues.
	 * 
	 * @param fieldGroup
	 * @param sourcePropertyIds
	 * @param sourceValues
	 * @return
	 */
	private static boolean isAnySymptomSetToYes(FieldGroup fieldGroup, List<String> sourcePropertyIds, 
			List<Object> sourceValues) {
		
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if(sourceValues.contains(sourceField.getValue())) {
				return true;
			}
		}
		
		return false;
	}
}
