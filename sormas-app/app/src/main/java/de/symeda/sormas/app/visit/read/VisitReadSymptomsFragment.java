package de.symeda.sormas.app.visit.read;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.symptom.Symptom;

public class VisitReadSymptomsFragment extends BaseReadFragment<FragmentContactReadSymptomsInfoLayoutBinding, Symptoms, Visit> {

    private Symptoms record;

    private List<String> yesResult;
    private List<String> noResult;
    private List<String> unknownResult;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Visit visit = getActivityRootData();
        record = visit.getSymptoms();

        List<Symptom> sList = Symptom.makeSymptoms(visit.getDisease()).loadState(visit.getSymptoms());
        yesResult = getSymptomsYes(sList);
        noResult = getSymptomsNo(sList);
        unknownResult = getSymptomsUnknown(sList);
    }

    @Override
    public void onLayoutBinding(FragmentContactReadSymptomsInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);

        contentBinding.symptomsSymptomsOccurred.setTags(yesResult);
        contentBinding.symptomsSymptomsUnknownOccurred.setTags(unknownResult);
        contentBinding.symptomsSymptomsNotOccurred.setTags(noResult);
    }
    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_symptom_information);
    }

    @Override
    public Symptoms getPrimaryData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_symptoms_info_layout;
    }

    public static VisitReadSymptomsFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitReadSymptomsFragment.class, capsule, activityRootData);
    }

    private List<String> getSymptomsYes(List<Symptom> list) {
        List<String> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.YES) {
                results.add(s.getName());
            }
        }
        return results;
    }

    private List<String> getSymptomsUnknown(List<Symptom> list) {
        List<String> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.UNKNOWN) {
                results.add(s.getName());
            }
        }
        return results;
    }

    private List<String> getSymptomsNo(List<Symptom> list) {
        List<String> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.NO) {
                results.add(s.getName());
            }
        }
        return results;
    }
}
