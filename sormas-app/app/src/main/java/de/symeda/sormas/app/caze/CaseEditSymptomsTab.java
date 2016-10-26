package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;


/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class CaseEditSymptomsTab extends FormTab {

    private CaseSymptomsFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initModel();
        binding = DataBindingUtil.inflate(inflater, R.layout.case_symptoms_fragment_layout, container, false);
        View view = binding.getRoot();
        //view.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final String caseUuid = (String) getArguments().getString(Case.UUID);
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseUuid);

        final Symptoms symptoms = caze.getSymptoms();
        binding.setSymptoms(symptoms);

        getModel().put(R.id.symptoms_onsetDate, symptoms.getOnsetDate());
        getModel().put(R.id.symptoms_temperature, symptoms.getTemperature());
        getModel().put(R.id.symptoms_temperatureSource, symptoms.getTemperatureSource());
        getModel().put(R.id.symptoms_fever, symptoms.getFever());
        getModel().put(R.id.symptoms_diarrhea, symptoms.getDiarrhea());
        getModel().put(R.id.symptoms_anorexiaAppetiteLoss, symptoms.getAnorexiaAppetiteLoss());
        getModel().put(R.id.symptoms_abdominalPain, symptoms.getAbdominalPain());
        getModel().put(R.id.symptoms_chestPain, symptoms.getChestPain());
        getModel().put(R.id.symptoms_musclePain, symptoms.getMusclePain());
        getModel().put(R.id.symptoms_jointPain, symptoms.getJointPain());
        getModel().put(R.id.symptoms_headache, symptoms.getHeadache());
        getModel().put(R.id.symptoms_cough, symptoms.getCough());
        getModel().put(R.id.symptoms_difficultyBreathing, symptoms.getDifficultyBreathing());
        getModel().put(R.id.symptoms_soreThroat, symptoms.getSoreThroat());
        getModel().put(R.id.symptoms_jaundice, symptoms.getJaundice());
        getModel().put(R.id.symptoms_conjunctivitis, symptoms.getConjunctivitis());
        getModel().put(R.id.symptoms_skinRash, symptoms.getSkinRash());
        getModel().put(R.id.symptoms_hiccups, symptoms.getHiccups());
        getModel().put(R.id.symptoms_eyePainLightSensitive, symptoms.getEyePainLightSensitive());
        getModel().put(R.id.symptoms_comaUnconscious, symptoms.getComaUnconscious());
        getModel().put(R.id.symptoms_confusedDisoriented, symptoms.getConfusedDisoriented());
        getModel().put(R.id.symptoms_unexplainedBleeding, symptoms.getUnexplainedBleeding());
        getModel().put(R.id.symptoms_gumsBleeding, symptoms.getGumsBleeding());
        getModel().put(R.id.symptoms_injectionSiteBleeding, symptoms.getInjectionSiteBleeding());
        getModel().put(R.id.symptoms_epistaxis, symptoms.getEpistaxis());
        getModel().put(R.id.symptoms_melena, symptoms.getMelena());
        getModel().put(R.id.symptoms_hematemesis, symptoms.getHematemesis());
        getModel().put(R.id.symptoms_digestedBloodVomit, symptoms.getDigestedBloodVomit());
        getModel().put(R.id.symptoms_hemoptysis, symptoms.getHemoptysis());
        getModel().put(R.id.symptoms_bleedingVagina, symptoms.getBleedingVagina());
        getModel().put(R.id.symptoms_petechiae, symptoms.getPetechiae());
        getModel().put(R.id.symptoms_hematuria, symptoms.getHematuria());
        getModel().put(R.id.symptoms_otherHemorrhagicSymptoms, symptoms.getOtherHemorrhagicSymptoms());
        getModel().put(R.id.symptoms_otherNonHemorrhagicSymptoms, symptoms.getOtherNonHemorrhagicSymptoms());

        addDateField(R.id.symptoms_onsetDate);

        List<Item> temperature = new ArrayList<>();
        temperature.add(new Item("",null));
        for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
            temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue),temperatureValue));
        }
        addSpinnerField(R.id.symptoms_temperature, temperature);

        addSpinnerField(R.id.symptoms_temperatureSource, TemperatureSource.class);

        // Disable each field by using the boolean parameter: addSymptomStateField(R.id.symptoms_fever,getResources().getString(R.string.symptoms_fever), false);
        addSymptomStateField(R.id.symptoms_fever,getResources().getString(R.string.symptoms_fever));
        addSymptomStateField(R.id.symptoms_vomiting,getResources().getString(R.string.symptoms_vomitingNausea));
        addSymptomStateField(R.id.symptoms_diarrhea,getResources().getString(R.string.symptoms_diarrhea));
        addSymptomStateField(R.id.symptoms_anorexiaAppetiteLoss,getResources().getString(R.string.symptoms_anorexiaAppetiteLoss));
        addSymptomStateField(R.id.symptoms_abdominalPain,getResources().getString(R.string.symptoms_abdominalPain));
        addSymptomStateField(R.id.symptoms_chestPain,getResources().getString(R.string.symptoms_chestPain));
        addSymptomStateField(R.id.symptoms_musclePain,getResources().getString(R.string.symptoms_musclePain));
        addSymptomStateField(R.id.symptoms_jointPain,getResources().getString(R.string.symptoms_jointPain));
        addSymptomStateField(R.id.symptoms_headache,getResources().getString(R.string.symptoms_headache));
        addSymptomStateField(R.id.symptoms_cough,getResources().getString(R.string.symptoms_cough));
        addSymptomStateField(R.id.symptoms_difficultyBreathing,getResources().getString(R.string.symptoms_difficultyBreathing));
        addSymptomStateField(R.id.symptoms_soreThroat,getResources().getString(R.string.symptoms_soreThroat));
        addSymptomStateField(R.id.symptoms_jaundice,getResources().getString(R.string.symptoms_jaundice));
        addSymptomStateField(R.id.symptoms_conjunctivitis,getResources().getString(R.string.symptoms_conjunctivitis));
        addSymptomStateField(R.id.symptoms_skinRash,getResources().getString(R.string.symptoms_skinRash));
        addSymptomStateField(R.id.symptoms_hiccups,getResources().getString(R.string.symptoms_hiccups));
        addSymptomStateField(R.id.symptoms_eyePainLightSensitive,getResources().getString(R.string.symptoms_eyePainLightSensitive));
        addSymptomStateField(R.id.symptoms_comaUnconscious,getResources().getString(R.string.symptoms_comaUnconscious));
        addSymptomStateField(R.id.symptoms_confusedDisoriented,getResources().getString(R.string.symptoms_confusedDisoriented));
        addSymptomStateField(R.id.symptoms_unexplainedBleeding,getResources().getString(R.string.symptoms_unexplainedBleeding), true, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                activationUnexplainedBleedingFields();
            }
        });
        addSymptomStateField(R.id.symptoms_gumsBleeding,getResources().getString(R.string.symptoms_gumsBleeding));
        addSymptomStateField(R.id.symptoms_injectionSiteBleeding,getResources().getString(R.string.symptoms_injectionSiteBleeding));
        addSymptomStateField(R.id.symptoms_epistaxis,getResources().getString(R.string.symptoms_epistaxis));
        addSymptomStateField(R.id.symptoms_melena,getResources().getString(R.string.symptoms_melena));
        addSymptomStateField(R.id.symptoms_hematemesis,getResources().getString(R.string.symptoms_hematemesis));
        addSymptomStateField(R.id.symptoms_digestedBloodVomit,getResources().getString(R.string.symptoms_digestedBloodVomit));
        addSymptomStateField(R.id.symptoms_hemoptysis,getResources().getString(R.string.symptoms_hemoptysis));
        addSymptomStateField(R.id.symptoms_bleedingVagina,getResources().getString(R.string.symptoms_bleedingVagina));
        addSymptomStateField(R.id.symptoms_petechiae,getResources().getString(R.string.symptoms_petechiae));
        addSymptomStateField(R.id.symptoms_hematuria,getResources().getString(R.string.symptoms_hematuria));
        addSymptomStateField(R.id.symptoms_otherHemorrhagicSymptoms,getResources().getString(R.string.symptoms_otherHemorrhagic), true, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                visibilityOtherHemorrhagicText();
            }
        });
        addSymptomStateField(R.id.symptoms_otherNonHemorrhagicSymptoms,getResources().getString(R.string.symptoms_otherNonHemorrhagic), true, new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                visibilityOtherNonHemorrhagicSymptoms();
            }
        });

        // set initial UI
        visibilityOtherHemorrhagicText();
        visibilityOtherNonHemorrhagicSymptoms();
        activationUnexplainedBleedingFields();

        // @TODO: Workaround, find a better solution. Remove autofocus on first field.
        getView().requestFocus();

    }
    
    private void visibilityOtherHemorrhagicText() {
        SymptomState symptomState = (SymptomState)getModel().get(R.id.symptoms_otherHemorrhagicSymptoms);
        getView().findViewById(R.id.symptoms_otherHemorrhagicSymptomsText).setVisibility(symptomState == SymptomState.YES?View.VISIBLE:View.GONE);
    }

    private void visibilityOtherNonHemorrhagicSymptoms() {
        SymptomState symptomState = (SymptomState)getModel().get(R.id.symptoms_otherNonHemorrhagicSymptoms);
        getView().findViewById(R.id.symptoms_otherNonHemorrhagicSymptomsText).setVisibility(symptomState == SymptomState.YES?View.VISIBLE:View.GONE);
    }


    private void activationUnexplainedBleedingFields() {

        int[] fieldIds = {
                R.id.symptoms_gumsBleeding,
                R.id.symptoms_injectionSiteBleeding,
                R.id.symptoms_epistaxis,
                R.id.symptoms_melena,
                R.id.symptoms_hematemesis,
                R.id.symptoms_digestedBloodVomit,
                R.id.symptoms_hemoptysis,
                R.id.symptoms_bleedingVagina,
                R.id.symptoms_petechiae,
                R.id.symptoms_hematuria,
                R.id.symptoms_otherHemorrhagicSymptoms,
                R.id.symptoms_otherNonHemorrhagicSymptoms
        };

        SymptomState symptomState = (SymptomState)getModel().get(R.id.symptoms_unexplainedBleeding);

        for (int fieldId:fieldIds) {
            if(symptomState == SymptomState.YES) {
                activateField(getView().findViewById(fieldId));
            }
            else {
                deactivateField(getView().findViewById(fieldId));
            }
        }
    }


    @Override
    public AbstractDomainObject getData() {
        return commit(binding.getSymptoms());
    }

    /**
     * Commit all values from model to ado.
     * @param ado
     * @return
     */
    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        // Set value to model
        Symptoms symptoms = (Symptoms) ado;
        symptoms.setOnsetDate((Date) getModel().get(R.id.symptoms_onsetDate));
        symptoms.setTemperature((Float) getModel().get(R.id.symptoms_temperature));
        symptoms.setTemperatureSource((TemperatureSource) getModel().get(R.id.symptoms_temperatureSource));
        symptoms.setFever((SymptomState) getModel().get(R.id.symptoms_fever));
        symptoms.setVomiting((SymptomState) getModel().get(R.id.symptoms_vomiting));
        symptoms.setDiarrhea((SymptomState) getModel().get(R.id.symptoms_diarrhea));
        symptoms.setAnorexiaAppetiteLoss((SymptomState) getModel().get(R.id.symptoms_anorexiaAppetiteLoss));
        symptoms.setAbdominalPain((SymptomState) getModel().get(R.id.symptoms_abdominalPain));
        symptoms.setChestPain((SymptomState) getModel().get(R.id.symptoms_chestPain));
        symptoms.setMusclePain((SymptomState) getModel().get(R.id.symptoms_musclePain));
        symptoms.setJointPain((SymptomState) getModel().get(R.id.symptoms_jointPain));
        symptoms.setHeadache((SymptomState) getModel().get(R.id.symptoms_headache));
        symptoms.setCough((SymptomState) getModel().get(R.id.symptoms_cough));
        symptoms.setDifficultyBreathing((SymptomState) getModel().get(R.id.symptoms_difficultyBreathing));
        symptoms.setSoreThroat((SymptomState) getModel().get(R.id.symptoms_soreThroat));
        symptoms.setJaundice((SymptomState) getModel().get(R.id.symptoms_jaundice));
        symptoms.setConjunctivitis((SymptomState) getModel().get(R.id.symptoms_conjunctivitis));
        symptoms.setSkinRash((SymptomState) getModel().get(R.id.symptoms_skinRash));
        symptoms.setHiccups((SymptomState) getModel().get(R.id.symptoms_hiccups));
        symptoms.setEyePainLightSensitive((SymptomState) getModel().get(R.id.symptoms_eyePainLightSensitive));
        symptoms.setComaUnconscious((SymptomState) getModel().get(R.id.symptoms_comaUnconscious));
        symptoms.setConfusedDisoriented((SymptomState) getModel().get(R.id.symptoms_confusedDisoriented));
        symptoms.setUnexplainedBleeding((SymptomState) getModel().get(R.id.symptoms_unexplainedBleeding));
        symptoms.setGumsBleeding((SymptomState) getModel().get(R.id.symptoms_gumsBleeding));
        symptoms.setInjectionSiteBleeding((SymptomState) getModel().get(R.id.symptoms_injectionSiteBleeding));
        symptoms.setEpistaxis((SymptomState) getModel().get(R.id.symptoms_epistaxis));
        symptoms.setMelena((SymptomState) getModel().get(R.id.symptoms_melena));
        symptoms.setHematemesis((SymptomState) getModel().get(R.id.symptoms_hematemesis));
        symptoms.setDigestedBloodVomit((SymptomState) getModel().get(R.id.symptoms_digestedBloodVomit));
        symptoms.setHemoptysis((SymptomState) getModel().get(R.id.symptoms_hemoptysis));
        symptoms.setBleedingVagina((SymptomState) getModel().get(R.id.symptoms_bleedingVagina));
        symptoms.setPetechiae((SymptomState) getModel().get(R.id.symptoms_petechiae));
        symptoms.setHematuria((SymptomState) getModel().get(R.id.symptoms_hematuria));
        symptoms.setOtherHemorrhagicSymptoms((SymptomState) getModel().get(R.id.symptoms_otherHemorrhagicSymptoms));
        symptoms.setOtherNonHemorrhagicSymptoms((SymptomState) getModel().get(R.id.symptoms_otherNonHemorrhagicSymptoms));

        return symptoms;
    }
}