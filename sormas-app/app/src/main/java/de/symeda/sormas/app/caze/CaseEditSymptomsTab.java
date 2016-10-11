package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.component.SymptomStateField;
import de.symeda.sormas.app.databinding.CasePersonFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.util.DateUtils;
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
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String caseUuid = (String) getArguments().getString(Case.UUID);
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = caseDao.queryUuid(caseUuid);

        //final Symptoms symptoms = caze.getSymptoms();
        //getModel().put(R.id.symptoms_fever, symptoms.getFever());

        SymptomState stateTestYes = SymptomState.YES;
        getModel().put(R.id.symptoms_fever, stateTestYes);
        getModel().put(R.id.symptoms_vomitingNausea, null);
        getModel().put(R.id.symptoms_diarrhea, null);
        getModel().put(R.id.symptoms_intenseFatigueWeakness, null);
        getModel().put(R.id.symptoms_anorexiaAppetiteLoss, stateTestYes);
        getModel().put(R.id.symptoms_abdominalPain, stateTestYes);
        getModel().put(R.id.symptoms_chestPain, null);
        getModel().put(R.id.symptoms_musclePain, null);
        getModel().put(R.id.symptoms_jointPain, null);
        getModel().put(R.id.symptoms_headache, null);
        getModel().put(R.id.symptoms_cough, null);
        getModel().put(R.id.symptoms_difficultyBreathing, null);
        getModel().put(R.id.symptoms_difficultySwallowing, null);
        getModel().put(R.id.symptoms_soreThroat, null);
        getModel().put(R.id.symptoms_jaundice, null);
        getModel().put(R.id.symptoms_conjunctivitis, null);
        getModel().put(R.id.symptoms_skinRash, null);
        getModel().put(R.id.symptoms_hiccups, null);
        getModel().put(R.id.symptoms_eyePainLightSensitive, null);
        getModel().put(R.id.symptoms_comaUnconscious, null);
        getModel().put(R.id.symptoms_confusedDisoriented, null);
        getModel().put(R.id.symptoms_unexplainedBleeding, null);
        getModel().put(R.id.symptoms_gumsBleeding, null);
        getModel().put(R.id.symptoms_injectionSiteBleeding, null);
        getModel().put(R.id.symptoms_epistaxis, null);
        getModel().put(R.id.symptoms_melena, null);
        getModel().put(R.id.symptoms_hematemesis, null);
        getModel().put(R.id.symptoms_digestedBloodVomit, null);
        getModel().put(R.id.symptoms_hemoptysis, null);
        getModel().put(R.id.symptoms_bleedingVagina, null);
        getModel().put(R.id.symptoms_petechiae, null);
        getModel().put(R.id.symptoms_hematuria, null);
        getModel().put(R.id.symptoms_otherHemorrhagic, null);

        addSymptomStateField(R.id.symptoms_fever,getResources().getString(R.string.symptoms_fever));
        addSymptomStateField(R.id.symptoms_vomitingNausea,getResources().getString(R.string.symptoms_vomitingNausea), false);
        addSymptomStateField(R.id.symptoms_diarrhea,getResources().getString(R.string.symptoms_diarrhea));
        addSymptomStateField(R.id.symptoms_intenseFatigueWeakness,getResources().getString(R.string.symptoms_intenseFatigueWeakness));
        addSymptomStateField(R.id.symptoms_anorexiaAppetiteLoss,getResources().getString(R.string.symptoms_anorexiaAppetiteLoss));
        addSymptomStateField(R.id.symptoms_abdominalPain,getResources().getString(R.string.symptoms_abdominalPain));
        addSymptomStateField(R.id.symptoms_chestPain,getResources().getString(R.string.symptoms_chestPain));
        addSymptomStateField(R.id.symptoms_musclePain,getResources().getString(R.string.symptoms_musclePain));
        addSymptomStateField(R.id.symptoms_jointPain,getResources().getString(R.string.symptoms_jointPain));
        addSymptomStateField(R.id.symptoms_headache,getResources().getString(R.string.symptoms_headache));
        addSymptomStateField(R.id.symptoms_cough,getResources().getString(R.string.symptoms_cough));
        addSymptomStateField(R.id.symptoms_difficultyBreathing,getResources().getString(R.string.symptoms_difficultyBreathing));
        addSymptomStateField(R.id.symptoms_difficultySwallowing,getResources().getString(R.string.symptoms_difficultySwallowing));
        addSymptomStateField(R.id.symptoms_soreThroat,getResources().getString(R.string.symptoms_soreThroat));
        addSymptomStateField(R.id.symptoms_jaundice,getResources().getString(R.string.symptoms_jaundice));
        addSymptomStateField(R.id.symptoms_conjunctivitis,getResources().getString(R.string.symptoms_conjunctivitis));
        addSymptomStateField(R.id.symptoms_skinRash,getResources().getString(R.string.symptoms_skinRash));
        addSymptomStateField(R.id.symptoms_hiccups,getResources().getString(R.string.symptoms_hiccups));
        addSymptomStateField(R.id.symptoms_eyePainLightSensitive,getResources().getString(R.string.symptoms_eyePainLightSensitive));
        addSymptomStateField(R.id.symptoms_comaUnconscious,getResources().getString(R.string.symptoms_comaUnconscious));
        addSymptomStateField(R.id.symptoms_confusedDisoriented,getResources().getString(R.string.symptoms_confusedDisoriented));
        addSymptomStateField(R.id.symptoms_unexplainedBleeding,getResources().getString(R.string.symptoms_unexplainedBleeding));
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
        addSymptomStateField(R.id.symptoms_otherHemorrhagic,getResources().getString(R.string.symptoms_otherHemorrhagic));

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
        Symptoms person = (Symptoms) ado;

        return person;
    }
}