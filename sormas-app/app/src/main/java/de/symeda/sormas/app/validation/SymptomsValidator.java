package de.symeda.sormas.app.validation;

import android.content.res.Resources;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.component.SymptomStateField;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 21.07.2017.
 */
public class SymptomsValidator {

    public static void clearErrors(CaseSymptomsFragmentLayoutBinding binding) {
        List<SymptomStateField> nonConditionalSymptoms = Arrays.asList(binding.symptomsFever, binding.symptomsVomiting,
                binding.symptomsDiarrhea, binding.symptomsBloodInStool1, binding.symptomsNausea, binding.symptomsAbdominalPain,
                binding.symptomsHeadache, binding.symptomsMusclePain, binding.symptomsFatigueWeakness, binding.symptomsUnexplainedBleeding,
                binding.symptomsSkinRash, binding.symptomsNeckStiffness, binding.symptomsSoreThroat, binding.symptomsCough,
                binding.symptomsRunnyNose, binding.symptomsDifficultyBreathing, binding.symptomsChestPain, binding.symptomsConfusedDisoriented,
                binding.symptomsSeizures, binding.symptomsAlteredConsciousness, binding.symptomsConjunctivitis,
                binding.symptomsEyePainLightSensitive, binding.symptomsKopliksSpots1, binding.symptomsThrobocytopenia,
                binding.symptomsOtitisMedia, binding.symptomsHearingloss, binding.symptomsDehydration, binding.symptomsAnorexiaAppetiteLoss,
                binding.symptomsRefusalFeedorDrink, binding.symptomsJointPain, binding.symptomsShock,
                binding.symptomsHiccups, binding.symptomsOtherNonHemorrhagicSymptoms);

        List<SymptomStateField> conditionalBleedingSymptoms = Arrays.asList(binding.symptomsGumsBleeding1, binding.symptomsInjectionSiteBleeding,
                binding.symptomsNoseBleeding1, binding.symptomsBloodyBlackStool, binding.symptomsRedBloodVomit,
                binding.symptomsDigestedBloodVomit, binding.symptomsCoughingBlood, binding.symptomsBleedingVagina,
                binding.symptomsSkinBruising1, binding.symptomsBloodUrine, binding.symptomsOtherHemorrhagicSymptoms);

        for (SymptomStateField field : nonConditionalSymptoms) {
            field.setError(null);
        }

        for (SymptomStateField field : conditionalBleedingSymptoms) {
            field.setError(null);
        }

        binding.symptomsOnsetDate.setError(null);
        binding.symptomsOnsetSymptom1.setError(null);
        binding.symptomsOther1HemorrhagicSymptomsText.setError(null);
        binding.symptomsOther1NonHemorrhagicSymptomsText.setError(null);
    }

    public static boolean validateCaseSymptoms(Symptoms symptoms, CaseSymptomsFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // These should be in reverse order of how they're displayed on the screen
        List<SymptomStateField> nonConditionalSymptoms = Arrays.asList(binding.symptomsOtherNonHemorrhagicSymptoms,
                binding.symptomsHiccups, binding.symptomsShock, binding.symptomsJointPain,
                binding.symptomsRefusalFeedorDrink, binding.symptomsAnorexiaAppetiteLoss, binding.symptomsDehydration, binding.symptomsHearingloss,
                binding.symptomsOtitisMedia, binding.symptomsThrobocytopenia, binding.symptomsKopliksSpots1,
                binding.symptomsEyePainLightSensitive, binding.symptomsConjunctivitis, binding.symptomsAlteredConsciousness,
                binding.symptomsSeizures, binding.symptomsConfusedDisoriented, binding.symptomsChestPain, binding.symptomsDifficultyBreathing,
                binding.symptomsRunnyNose, binding.symptomsCough, binding.symptomsSoreThroat, binding.symptomsNeckStiffness,
                binding.symptomsSkinRash, binding.symptomsUnexplainedBleeding, binding.symptomsFatigueWeakness, binding.symptomsMusclePain,
                binding.symptomsHeadache, binding.symptomsAbdominalPain, binding.symptomsNausea, binding.symptomsBloodInStool1,
                binding.symptomsDiarrhea, binding.symptomsVomiting, binding.symptomsFever);

        // These should be in reverse order of how they're displayed on the screen
        List<SymptomStateField> conditionalBleedingSymptoms = Arrays.asList(binding.symptomsOtherHemorrhagicSymptoms, binding.symptomsBloodUrine,
                binding.symptomsSkinBruising1, binding.symptomsBleedingVagina, binding.symptomsCoughingBlood,
                binding.symptomsDigestedBloodVomit, binding.symptomsRedBloodVomit, binding.symptomsBloodyBlackStool,
                binding.symptomsNoseBleeding1, binding.symptomsInjectionSiteBleeding, binding.symptomsGumsBleeding1);

        // Date of initial symptom onset & initial onset symptom
        if (isAnyNonConditionalSymptomSetTo(SymptomState.YES, nonConditionalSymptoms)) {
            if (symptoms.getOnsetSymptom() == null) {
                binding.symptomsOnsetSymptom1.setError(resources.getString(R.string.validation_symptoms_onset_symptom));
                success = false;
            }
            if (symptoms.getOnsetDate() == null) {
                binding.symptomsOnsetDate.setError(resources.getString(R.string.validation_symptoms_onset_date));
                success = false;
            }
        }

        // Other clinical symptoms
        if (symptoms.getOtherNonHemorrhagicSymptoms() == SymptomState.YES &&
                (symptoms.getOtherNonHemorrhagicSymptomsText() == null || symptoms.getOtherNonHemorrhagicSymptomsText().trim().isEmpty())) {
            binding.symptomsOther1NonHemorrhagicSymptomsText.setError(resources.getString(R.string.validation_symptoms_other_clinical));
            success = false;
        }

        // Other hemorrhagic symptoms
        if (symptoms.getOtherHemorrhagicSymptoms() == SymptomState.YES &&
                (symptoms.getOtherHemorrhagicSymptomsText() == null || symptoms.getOtherHemorrhagicSymptomsText().trim().isEmpty())) {
            binding.symptomsOther1HemorrhagicSymptomsText.setError(resources.getString(R.string.validation_symptoms_other_hemorrhagic));
            success = false;
        }

        // Unexplained bleeding symptoms
        if (symptoms.getUnexplainedBleeding() == SymptomState.YES) {
            if (markAnyConditionalBleedingSymptomSetTo(null, conditionalBleedingSymptoms, resources)) {
                success = false;
            }
        }

        return success;
    }

    /**
     * Returns true if there is any visible non-conditional symptom set to the given symptom state
     * or null, in which case true is returned if any symptom state is not set
     */
    private static boolean isAnyNonConditionalSymptomSetTo(SymptomState reqSymptomState, List<SymptomStateField> nonConditionalSymptoms) {
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
    private static boolean markAnyConditionalBleedingSymptomSetTo(SymptomState reqSymptomState, List<SymptomStateField> conditionalBleedingSymptoms, Resources resources) {
        boolean fieldMarked = false;
        for(SymptomStateField field : conditionalBleedingSymptoms) {
            if(field.getVisibility() == View.VISIBLE && field.getValue() == reqSymptomState) {
                field.setError(resources.getString(R.string.validation_symptoms_symptom));
                fieldMarked = true;
            }
        }

        return fieldMarked;
    }

}
