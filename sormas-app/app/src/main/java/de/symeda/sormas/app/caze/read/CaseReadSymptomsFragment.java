package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseReadSymptomsLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.symptom.Symptom;

public class CaseReadSymptomsFragment extends BaseReadFragment<FragmentCaseReadSymptomsLayoutBinding, Symptoms, Case> {

    public static final String TAG = CaseReadSymptomsFragment.class.getSimpleName();

    private Symptoms record;

    private List<String> yesResult;
    private List<String> noResult;
    private List<String> unknownResult;

    // TODO can probably be removed
    private OnLinkClickListener onLinkClickListener;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = caze.getSymptoms();
        List<Symptom> sList = Symptom.makeSymptoms(caze.getDisease()).loadState(record);
        yesResult = getSymptomsYes(sList);
        noResult = getSymptomsNo(sList);
        unknownResult = getSymptomsUnknown(sList);
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadSymptomsLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.tvSymptomsYes.setTags(yesResult);
        contentBinding.tvSymptomsUnknown.setTags(unknownResult);
        contentBinding.tvSymptomsNo.setTags(noResult);
        contentBinding.setLocationClickCallback(onLinkClickListener);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_symptom_information);
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_symptoms_layout;
    }

    public static CaseReadSymptomsFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseReadSymptomsFragment.class, capsule, activityRootData);
    }

    private List<String> getSymptomsYes(List<Symptom> list) {
        List<String> results = new ArrayList();
        for (Symptom s : list) {
            if (s.getState() == SymptomState.YES) {
                results.add(s.getName());
            }
        }

        return results;
    }

    private List<String> getSymptomsUnknown(List<Symptom> list) {
        List<String> results = new ArrayList();
        for (Symptom s : list) {
            if (s.getState() == SymptomState.UNKNOWN) {
                results.add(s.getName());
            }
        }

        return results;
    }

    private List<String> getSymptomsNo(List<Symptom> list) {
        List<String> results = new ArrayList();
        for (Symptom s : list) {
            if (s.getState() == SymptomState.NO) {
                results.add(s.getName());
            }
        }

        return results;
    }
}
