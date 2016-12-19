package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;


/**
 * Use this tab with arguments:
 * symptomsUuid as string
 * disease as serialized enum
 */
public class SymptomsEditTab extends FormTab {

    private CaseSymptomsFragmentLayoutBinding binding;

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

        final String symptomsUuid = getArguments().getString(Symptoms.UUID);
        final Disease disease = (Disease) getArguments().getSerializable(Case.DISEASE);


        Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryUuid(symptomsUuid);
        binding.setSymptoms(symptoms);

        binding.symptomsOnsetDate.initialize(this);

        List<Item> temperature = new ArrayList<>();
        temperature.add(new Item("",null));
        for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
            temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue),temperatureValue));
        }

        FieldHelper.initSpinnerField(binding.symptomsTemperature, temperature);

        FieldHelper.initSpinnerField(binding.symptomsTemperatureSource, TemperatureSource.class);

        binding.symptomsUnexplainedBleeding.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                activationUnexplainedBleedingFields();
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
        visibilityOtherHemorrhagicSymptoms();
        visibilityOtherNonHemorrhagicSymptoms();
        activationUnexplainedBleedingFields();

        visibilityDisease(disease);

        // @TODO: Workaround, find a better solution. Remove autofocus on first field.
        getView().requestFocus();
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


    private void activationUnexplainedBleedingFields() {

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
                activateField(getView().findViewById(fieldId));
            }
            else {
                View view = getView().findViewById(fieldId);
                // reset value
                ((SymptomStateField)view).setValue(null);
                deactivateField(view);
            }
        }
    }


    @Override
    public AbstractDomainObject getData() {
        return binding.getSymptoms();
    }

}