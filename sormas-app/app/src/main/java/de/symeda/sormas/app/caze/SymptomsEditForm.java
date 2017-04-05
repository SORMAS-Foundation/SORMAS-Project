package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SymptomStateField;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.util.ValidationFailedException;


/**
 * Use this tab with arguments:
 * symptomsUuid as string
 * disease as serialized enum
 */
public class SymptomsEditForm extends FormTab {

    public static final String NEW_SYMPTOMS = "newSymptoms";
    private CaseSymptomsFragmentLayoutBinding binding;
    private List<SymptomStateField> nonConditionalSymptoms;
    private List<SymptomStateField> conditionalBleedingSymptoms;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_symptoms_fragment_layout, container, false);
        View view = binding.getRoot();
        //view.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            final Disease disease = (Disease) getArguments().getSerializable(Case.DISEASE);

            Symptoms symptoms;
            // create a new visit from contact data
            if(getArguments().getBoolean(NEW_SYMPTOMS)) {
                symptoms = DataUtils.createNew(Symptoms.class);
            }
            // open the given visit
            else {
                String symptomsUuid = getArguments().getString(Symptoms.UUID);
                symptoms = DatabaseHelper.getSymptomsDao().queryUuid(symptomsUuid);
            }

            binding.setSymptoms(symptoms);

            binding.symptomsOnsetDate.initialize(this);

            List<Item> temperature = new ArrayList<>();
            temperature.add(new Item("",null));
            for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
                temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue),temperatureValue));
            }

            FieldHelper.initSpinnerField(binding.symptomsTemperature, temperature);
            binding.symptomsTemperature.setSelectionOnOpen(37.0f);

            FieldHelper.initSpinnerField(binding.symptomsTemperatureSource, TemperatureSource.class);

            binding.symptomsUnexplainedBleeding.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    toggleUnexplainedBleedingFields();
                }
            });
            binding.symptomsOtherHemorrhagicSymptoms.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    visibilityOtherHemorrhagicSymptoms();
                }
            });
            binding.symptomsOtherNonHemorrhagicSymptoms.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    visibilityOtherNonHemorrhagicSymptoms();
                }
            });

            // set initial UI
            toggleUnexplainedBleedingFields();
            visibilityOtherHemorrhagicSymptoms();
            visibilityOtherNonHemorrhagicSymptoms();

            visibilityDisease(disease);

            nonConditionalSymptoms = Arrays.asList(binding.symptomsFever, binding.symptomsVomiting,
                    binding.symptomsDiarrhea, binding.symptomsBloodInStool1, binding.symptomsNausea, binding.symptomsAbdominalPain,
                    binding.symptomsHeadache, binding.symptomsMusclePain, binding.symptomsFatigueWeakness, binding.symptomsUnexplainedBleeding,
                    binding.symptomsSkinRash, binding.symptomsNeckStiffness, binding.symptomsSoreThroat, binding.symptomsCough,
                    binding.symptomsRunnyNose, binding.symptomsDifficultyBreathing, binding.symptomsChestPain, binding.symptomsConfusedDisoriented,
                    binding.symptomsSeizures, binding.symptomsAlteredConsciousness, binding.symptomsConjunctivitis,
                    binding.symptomsEyePainLightSensitive, binding.symptomsKopliksSpots1, binding.symptomsThrobocytopenia,
                    binding.symptomsOtitisMedia, binding.symptomsHearingloss, binding.symptomsDehydration, binding.symptomsAnorexiaAppetiteLoss,
                    binding.symptomsRefusalFeedorDrink, binding.symptomsJointPain, binding.symptomsShock,
                    binding.symptomsHiccups, binding.symptomsOtherNonHemorrhagicSymptoms);

            conditionalBleedingSymptoms = Arrays.asList(binding.symptomsGumsBleeding1, binding.symptomsInjectionSiteBleeding,
                    binding.symptomsNoseBleeding1, binding.symptomsBloodyBlackStool, binding.symptomsRedBloodVomit,
                    binding.symptomsDigestedBloodVomit, binding.symptomsCoughingBlood, binding.symptomsBleedingVagina,
                    binding.symptomsSkinBruising1, binding.symptomsBloodUrine, binding.symptomsOtherHemorrhagicSymptoms);

//            FieldHelper.initOnsetSymptomSpinnerField(binding.symptomsOnsetSymptom1, new ArrayList<Item>());
//            addListenerForOnsetSymptom();

            Button clearAllBtn = binding.symptomsClearAll;
            clearAllBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (SymptomStateField symptom : nonConditionalSymptoms) {
                        symptom.setValue(null);
                    }
                    for (SymptomStateField symptom : conditionalBleedingSymptoms) {
                        symptom.setValue(null);
                    }
                }
            });

            Button setAllToNoBtn = binding.symptomsSetAllToNo;
            setAllToNoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (SymptomStateField symptom : nonConditionalSymptoms) {
                        if (symptom.getVisibility() == View.VISIBLE) {
                            symptom.setValue(SymptomState.NO);
                        }
                    }
                    for (SymptomStateField symptom : conditionalBleedingSymptoms) {
                        if (symptom.getVisibility() == View.VISIBLE) {
                            symptom.setValue(SymptomState.NO);
                        }
                    }
                }
            });

            // @TODO: Workaround, find a better solution. Remove autofocus on first field.
            getView().requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the entered information with respect to the visit status. Returns a String
     * with the most important error message (because returning a simple boolean would mean
     * that the respective Activity does not know what caused the validation error.
     */
    public boolean validateVisitData(Symptoms symptoms, boolean isCooperative) throws ValidationFailedException {
        if(isCooperative) {
            if(isAnyNonConditionalSymptomSetTo(null)) {
                throw new ValidationFailedException("Not saved. Please specify the symptom states for all symptoms.");
            }
            if(symptoms.getTemperature() == null || symptoms.getTemperatureSource() == null) {
                throw new ValidationFailedException("Not saved. Please specify a temperature and a temperature source.");
            }
            if(isAnyNonConditionalSymptomSetTo(SymptomState.YES)) {
                if(symptoms.getOnsetDate() == null) {
                    throw new ValidationFailedException("Not saved. Please specify the date of initial symptom onset.");
                }
                if(symptoms.getOnsetSymptom() == null) {
                    throw new ValidationFailedException("Not saved. Please specify the initial onset symptom.");
                }
            }
        }
        if(symptoms.getTemperature() != null && symptoms.getTemperature().compareTo(38.0F) >= 0 &&
                symptoms.getFever() != SymptomState.YES) {
            throw new ValidationFailedException(
                    "Not saved. Temperature is in fever range. Please make sure that the fever field is set to 'Yes'.");
        }

        return validateGeneralData(symptoms);
    }

    /**
     * Validates the entered information. Returns a String with the most important error message
     * (because returning a simple boolean would mean that the respective Activity does not know
     * what caused the validation error.
     */
    public boolean validateCaseData(Symptoms symptoms) throws ValidationFailedException {
        if(isAnyNonConditionalSymptomSetTo(SymptomState.YES)) {
            if(symptoms.getOnsetDate() == null) {
                throw new ValidationFailedException("Not saved. Please specify the date of initial symptom onset.");
            }
            if(symptoms.getOnsetSymptom() == null) {
                throw new ValidationFailedException("Not saved. Please specify the initial onset symptom.");
            }
        }

        return validateGeneralData(symptoms);
    }

    /**
     * Handles all validations that are not specific to a visit or case.
     */
    private boolean validateGeneralData(Symptoms symptoms) throws ValidationFailedException {
        if(symptoms.getUnexplainedBleeding() == SymptomState.YES) {
            if(isAnyConditionalBleedingSymptomSetTo(null)) {
                throw new ValidationFailedException("Not saved. Please specify the states for all conditional bleeding symptoms.");
            }
        }
        if(symptoms.getOtherHemorrhagicSymptoms() == SymptomState.YES &&
                (symptoms.getOtherHemorrhagicSymptomsText() == null || symptoms.getOtherHemorrhagicSymptomsText().isEmpty())) {
            throw new ValidationFailedException("Not saved. Please specify the additional hemorrhagic symptoms.");
        }
        if(symptoms.getOtherNonHemorrhagicSymptoms() == SymptomState.YES &&
                (symptoms.getOtherNonHemorrhagicSymptomsText() == null || symptoms.getOtherNonHemorrhagicSymptomsText().isEmpty())) {
            throw new ValidationFailedException("Not saved. Please specify the additional clinical symptoms.");
        }

        return true;
    }

    /**
     * Returns true if there is any visible non-conditional symptom set to the given symptom state
     * or null, in which case true is returned if any symptom state is not set
     */
    private boolean isAnyNonConditionalSymptomSetTo(SymptomState reqSymptomState) {
        for(SymptomStateField field : nonConditionalSymptoms) {
            if(field.getVisibility() == View.VISIBLE && field.getValue() == reqSymptomState) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if there is any visible conditional bleeding symptom set to the given symptom state
     * or null, in which case true is returned if any symptom state is not set
     */
    private boolean isAnyConditionalBleedingSymptomSetTo(SymptomState reqSymptomState) {
        for(SymptomStateField field : conditionalBleedingSymptoms) {
            if(field.getVisibility() == View.VISIBLE && field.getValue() == reqSymptomState) {
                return true;
            }
        }

        return false;
    }

    private void visibilityDisease(Disease disease) {
        for (int i=0; i<binding.caseSymptomsForm.getChildCount(); i++){
            View child = binding.caseSymptomsForm.getChildAt(i);
            if (child instanceof PropertyField) {
                String propertyId = ((PropertyField)child).getPropertyId();
                boolean definedOrMissing = Diseases.DiseasesConfiguration.isDefinedOrMissing(SymptomsDto.class, propertyId, disease);
                child.setVisibility(definedOrMissing ? View.VISIBLE : View.GONE);
            }
        }
    }
    
    private void visibilityOtherHemorrhagicSymptoms() {
        SymptomState symptomState = binding.symptomsOtherHemorrhagicSymptoms.getValue();
        binding.symptomsOtherHemorrhagicSymptomsLayout.setVisibility(symptomState == SymptomState.YES?View.VISIBLE:View.GONE);
        if(symptomState != SymptomState.YES) {
            binding.symptomsOther1HemorrhagicSymptomsText.setValue("");
        }
    }

    private void visibilityOtherNonHemorrhagicSymptoms() {
        SymptomState symptomState = binding.symptomsOtherNonHemorrhagicSymptoms.getValue();
        binding.symptomsOtherNonHemorrhagicSymptomsLayout.setVisibility(symptomState == SymptomState.YES?View.VISIBLE:View.GONE);
        if(symptomState != SymptomState.YES) {
            binding.symptomsOther1NonHemorrhagicSymptomsText.setValue("");
        }
    }

    private void toggleUnexplainedBleedingFields() {

        int[] fieldIds = {
                R.id.symptoms_gumsBleeding1,
                R.id.symptoms_injectionSiteBleeding,
                R.id.symptoms_noseBleeding1,
                R.id.symptoms_bloodyBlackStool,
                R.id.symptoms_redBloodVomit,
                R.id.symptoms_digestedBloodVomit,
                R.id.symptoms_coughingBlood,
                R.id.symptoms_bleedingVagina,
                R.id.symptoms_skinBruising1,
                R.id.symptoms_bloodUrine,
                R.id.symptoms_otherHemorrhagicSymptoms
        };

        SymptomState symptomState = binding.symptomsUnexplainedBleeding.getValue();
        for (int fieldId:fieldIds) {
            if(symptomState == SymptomState.YES) {
                setFieldVisible(getView().findViewById(fieldId), true);
            }
            else {
                View view = getView().findViewById(fieldId);
                // reset value
                ((SymptomStateField)view).setValue(null);
                setFieldGone(view);
            }
        }
    }

//    private void addListenerForOnsetSymptom() {
//        final ArrayAdapter<Item> adapter = (ArrayAdapter<Item>) binding.symptomsOnsetSymptom1.getAdapter();
//
//        for (SymptomStateField symptom : nonConditionalSymptoms) {
//            symptom.addValueChangedListener(new PropertyField.ValueChangeListener() {
//                @Override
//                public void onChange(PropertyField field) {
//                    if (field.getValue() == SymptomState.YES) {
//                        adapter.add(new Item(field.getCaption(), field.getCaption()));
//                    } else {
//                        adapter.remove((Item) binding.symptomsOnsetSymptom1.getItemAtPosition(
//                                binding.symptomsOnsetSymptom1.getPositionOf(new Item(field.getCaption(), field.getCaption()))));
//                    }
//                }
//            });
//        }
//
//        for (SymptomStateField symptom : conditionalBleedingSymptoms) {
//            symptom.addValueChangedListener(new PropertyField.ValueChangeListener() {
//                @Override
//                public void onChange(PropertyField field) {
//                    if (field.getValue() == SymptomState.YES) {
//                        adapter.add(new Item(field.getCaption(), field.getCaption()));
//                    } else {
//                        adapter.remove((Item) binding.symptomsOnsetSymptom1.getItemAtPosition(
//                                binding.symptomsOnsetSymptom1.getPositionOf(new Item(field.getCaption(), field.getCaption()))));
//                    }
//                }
//            });
//        }
//    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getSymptoms();
    }

}