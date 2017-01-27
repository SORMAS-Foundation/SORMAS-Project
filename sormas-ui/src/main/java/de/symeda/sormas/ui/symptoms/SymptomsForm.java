package de.symeda.sormas.ui.symptoms;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.api.visit.VisitStatus;
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
	private List<Object> unconditionalSymptomFieldIds;

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

//		getFieldGroup().getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT).setCaption(null);
//		getFieldGroup().getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT).setCaption(null);
		
		List<Object> conditionalBleedingSymptomFieldIds = Arrays.asList(SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.NOSE_BLEEDING, 
				SymptomsDto.BLOODY_BLACK_STOOL, SymptomsDto.RED_BLOOD_VOMIT, SymptomsDto.DIGESTED_BLOOD_VOMIT, 
				SymptomsDto.COUGHING_BLOOD, SymptomsDto.BLEEDING_VAGINA, SymptomsDto.SKIN_BRUISING, 
				SymptomsDto.BLOOD_URINE, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS);

		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(SymptomsDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible);
			
		FieldHelper.setVisibleWhen(getFieldGroup(), 
				conditionalBleedingSymptomFieldIds,
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
		
		}
		
//		getFieldGroup().getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT).setVisible(false);
//		getFieldGroup().getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT).setVisible(false);
//		for (Object fieldId : conditionalBleedingSymptomFieldIds) {
//			getFieldGroup().getField(fieldId).setVisible(false);
//		}

		unconditionalSymptomFieldIds = Arrays.asList(SymptomsDto.FEVER, SymptomsDto.VOMITING, SymptomsDto.DIARRHEA, SymptomsDto.BLOOD_IN_STOOL,
				SymptomsDto.NAUSEA, SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.HEADACHE, SymptomsDto.MUSCLE_PAIN, SymptomsDto.FATIGUE_WEAKNESS, SymptomsDto.SKIN_RASH,
				SymptomsDto.NECK_STIFFNESS, SymptomsDto.SORE_THROAT, SymptomsDto.COUGH, SymptomsDto.RUNNY_NOSE, SymptomsDto.DIFFICULTY_BREATHING,
				SymptomsDto.CHEST_PAIN, SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.SEIZURES, SymptomsDto.ALTERED_CONSCIOUSNESS, SymptomsDto.CONJUNCTIVITIS,
				SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, SymptomsDto.KOPLIKS_SPOTS, SymptomsDto.THROBOCYTOPENIA, SymptomsDto.OTITIS_MEDIA, SymptomsDto.HEARINGLOSS,
				SymptomsDto.DEHYDRATION, SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.JOINT_PAIN, SymptomsDto.SHOCK,
				SymptomsDto.HICCUPS, SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS);
		
		
		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(SymptomsDto.UNEXPLAINED_BLEEDING), conditionalBleedingSymptomFieldIds, Arrays.asList(SymptomState.YES), disease);
		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS), 
				Arrays.asList(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT), Arrays.asList(SymptomState.YES), disease);
		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS), 
				Arrays.asList(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT), Arrays.asList(SymptomState.YES), disease);
		// setReadOnly(true, );
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
	public void initializeSymptomRequirementsForVisit(OptionGroup visitStatus) {
		FieldHelper.setRequiredWhen(getFieldGroup(), visitStatus, unconditionalSymptomFieldIds, Arrays.asList(VisitStatus.COOPERATIVE), disease);
		FieldHelper.setRequiredWhen(getFieldGroup(), visitStatus, Arrays.asList(SymptomsDto.TEMPERATURE, SymptomsDto.TEMPERATURE_SOURCE), Arrays.asList(VisitStatus.COOPERATIVE), disease);
		setRequiredWhenSymptomaticAndCooperative(getFieldGroup(), SymptomsDto.ONSET_DATE, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), visitStatus);
		setRequiredWhenSymptomaticAndCooperative(getFieldGroup(), SymptomsDto.ONSET_SYMPTOM, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), visitStatus);
		getFieldGroup().getField(SymptomsDto.FEVER).addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if(getFieldGroup().getField(SymptomsDto.TEMPERATURE).getValue() != null) {
					if((Float)(getFieldGroup().getField(SymptomsDto.TEMPERATURE).getValue()) >= 38.0f) {
						if(value != SymptomState.YES) {
							throw new InvalidValueException("Fever needs to be set to 'Yes' for temperatures >= 38");
						}
					}
				}
			}
		});
	}	
	
	public void initializeSymptomRequirementsForCase() {
		setRequiredWhenSymptomatic(getFieldGroup(), SymptomsDto.ONSET_DATE, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES));
		setRequiredWhenSymptomatic(getFieldGroup(), SymptomsDto.ONSET_SYMPTOM, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES));
	}
	
	private void setRequiredWhenSymptomatic(FieldGroup fieldGroup, Object targetPropertyId, List<Object> sourcePropertyIds, 
			List<Object> sourceValues) {
		setRequiredWhenSymptomaticAndCooperative(fieldGroup, targetPropertyId, sourcePropertyIds, sourceValues, null);
	}
	
	/**
	 * Sets the fields defined by the ids contained in sourceValues to required when the person is symptomatic
	 * and - if a visit is processed - cooperative. When this method is called from within a case, it needs to 
	 * be called with visitStatusField set to null in order to ignore the visit status requirement.
	 */
	private void setRequiredWhenSymptomaticAndCooperative(FieldGroup fieldGroup, Object targetPropertyId,
			List<Object> sourcePropertyIds, List<Object> sourceValues, Field visitStatusField) {
				
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if(sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}
		}
		
		// Initialize
		final Field targetField = fieldGroup.getField(targetPropertyId);
		if(visitStatusField != null) {
			targetField.setRequired(isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) && 
					visitStatusField.getValue() == VisitStatus.COOPERATIVE);
		} else {
			targetField.setRequired(isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues));
		}
		
		// Add listeners
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			sourceField.addValueChangeListener(event -> {
				if(visitStatusField != null) {
					targetField.setRequired(isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) &&
							visitStatusField.getValue() == VisitStatus.COOPERATIVE);
				} else {
					targetField.setRequired(isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues));
				}
			});
		}
		
		if(visitStatusField != null) {
			visitStatusField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
					targetField.setRequired(isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) &&
							visitStatusField.getValue() == VisitStatus.COOPERATIVE);
				}
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
	private boolean isAnySymptomSetToYes(FieldGroup fieldGroup, List<Object> sourcePropertyIds, 
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
