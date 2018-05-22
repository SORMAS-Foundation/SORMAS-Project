package de.symeda.sormas.app.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.app.component.EditTeboPropertyField;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.symptom.Symptom;
import de.symeda.sormas.app.symptom.SymptomType;

/**
 * Created by Orson on 09/05/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class NewSymptomValidator {

    private static List<Symptom> nonConditionalSymptomList;
    private static List<Symptom> conditionalBleedingSymptomList;
    private static List<Symptom> lesionsSymptomList;
    private static List<Symptom> monkeypoxSymptomList;

    public static void clearErrorsForSymptoms(CaseSymptomsFragmentLayoutBinding binding) {
        /*for (SymptomStateField field : getNonConditionalSymptoms(binding)) {
            field.clearError();
        }

        for (SymptomStateField field : getConditionalBleedingSymptoms(binding)) {
            field.clearError();
        }

        for (PropertyField field : getOtherSymptomsFields(binding)) {
            field.clearError();
        }

        for (PropertyField field : getLesionsFields(binding)) {
            field.clearError();
        }

        for (PropertyField field : getMonkeypoxFields(binding)) {
            field.clearError();
        }*/
    }

    public static boolean validateCaseSymptoms(List<Symptom> list) {
        boolean success = true;

        for(Symptom s: list) {
            success = s.validate(s, list);
        }






        /*Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        List<SymptomStateField> nonConditionalSymptoms = getNonConditionalSymptoms(binding);
        List<SymptomStateField> conditionalBleedingSymptoms = getConditionalBleedingSymptoms(binding);
        List<SymptomStateField> lesionsFields = getLesionsFields(binding);
        List<SymptomStateField> monkeypoxFields = getMonkeypoxFields(binding);

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
            if (markAnySymptomSetTo(null, conditionalBleedingSymptoms, resources)) {
                success = false;
            }
        }

        // Lesions fields
        if (symptoms.getLesions() == SymptomState.YES) {
            if (markAnySymptomSetTo(null, lesionsFields, resources)) {
                success = false;
            }
        }

        // Monkeypox fields
        if (symptoms.getLesions() == SymptomState.YES) {
            if (markAnySymptomSetTo(null, monkeypoxFields, resources)) {
                success = false;
            }
        }

        return success;*/
        return false;
    }


    public static void init(List<Symptom> list) {
        nonConditionalSymptomList = getNonConditionalSymptoms(list);
        conditionalBleedingSymptomList = getConditionalBleedingSymptoms(list);
        lesionsSymptomList = getLesionsFields(list);
        monkeypoxSymptomList = getMonkeypoxFields(list);
    }

    private static List<Symptom> getNonConditionalSymptoms(List<Symptom> list) {
        List<Symptom> filterList = new ArrayList<>();
        for(Symptom s: list) {
            if (s.getType() == SymptomType.NON_CONDITIONAL) {
                filterList.add(s);
            }
        }

        return filterList;
    }

    private static List<Symptom> getConditionalBleedingSymptoms(List<Symptom> list) {
        List<Symptom> filterList = new ArrayList<>();
        for(Symptom s: list) {
            if (s.getType() == SymptomType.CONDITIONAL_BLEEDING) {
                filterList.add(s);
            }
        }

        return filterList;
    }

    private static List<Symptom> getLesionsFields(List<Symptom> list) {
        List<Symptom> filterList = new ArrayList<>();
        for(Symptom s: list) {
            if (s.getType() == SymptomType.LESIONS) {
                filterList.add(s);
            }
        }

        return filterList;
    }

    private static List<Symptom> getMonkeypoxFields(List<Symptom> list) {
        List<Symptom> filterList = new ArrayList<>();
        for(Symptom s: list) {
            if (s.getType() == SymptomType.MONKEYPOX) {
                filterList.add(s);
            }
        }

        return filterList;
    }












    private static List<? extends EditTeboPropertyField<?>> getOtherSymptomsFields(FragmentCaseEditSymptomsInfoLayoutBinding binding) {
        return Arrays.asList(binding.dtpSymptomOnset, binding.spnFirstSymptoms,
                binding.txtSymptomComment,
                binding.spnBodyTemperature, binding.spnBodyTemperatureSource, binding.txtSymptomaticLocation);
        //binding.symptomsOther1NonHemorrhagicSymptomsText,
    }


}
