package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadSymptomsLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.symptom.Symptom;

public class CaseReadSymptomsFragment extends BaseReadFragment<FragmentCaseReadSymptomsLayoutBinding, Symptoms, Case> {

    public static final String TAG = CaseReadSymptomsFragment.class.getSimpleName();

    private Symptoms record;

    private List<Tag> yesResult;
    private List<Tag> noResult;
    private List<Tag> unknownResult;

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
        contentBinding.setSymptomsYes(yesResult);
        contentBinding.setSymptomsUnknown(unknownResult);
        contentBinding.setSymptomsNo(noResult);
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

    private List<Tag> getSymptomsYes(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s : list) {
            if (s.getState() == SymptomState.YES) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }

    private List<Tag> getSymptomsUnknown(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s : list) {
            if (s.getState() == SymptomState.UNKNOWN) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }

    private List<Tag> getSymptomsNo(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s : list) {
            if (s.getState() == SymptomState.NO) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }
}
