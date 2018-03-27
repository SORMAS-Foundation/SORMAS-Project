package de.symeda.sormas.app.caze.read;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadSymptomsLayoutBinding;
import de.symeda.sormas.app.symptom.Symptom;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadSymptomsFragment extends BaseReadActivityFragment<FragmentCaseReadSymptomsLayoutBinding, Symptoms> {

    public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();

    private String recordUuid = null;
    private CaseClassification pageStatus = null;
    private Symptoms record;

    private List<Tag> yesResult;
    private List<Tag> noResult;
    private List<Tag> unknownResult;

    private OnLinkClickListener onLinkClickListener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Symptoms symptom = null;
            Case caze = DatabaseHelper.getCaseDao().queryUuid(recordUuid);

            if (caze != null)
                symptom = caze.getSymptoms();

            List<Symptom> sList = Symptom.makeSymptoms(caze.getDisease()).loadState(symptom);

            resultHolder.forOther().add(getSymptomsYes(sList));
            resultHolder.forOther().add(getSymptomsNo(sList));
            resultHolder.forOther().add(getSymptomsUnknown(sList));

            resultHolder.forItem().add(symptom);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (otherIterator.hasNext())
                yesResult =  otherIterator.next();

            if (otherIterator.hasNext())
                noResult =  otherIterator.next();

            if (otherIterator.hasNext())
                unknownResult =  otherIterator.next();

            setupCallback();
        }

        return true;
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
    public void onAfterLayoutBinding(FragmentCaseReadSymptomsLayoutBinding contentBinding) {

    }

    @Override
    public void onPageResume(FragmentCaseReadSymptomsLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_symptoms_layout;
    }

    public static CaseReadSymptomsFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadSymptomsFragment.class, capsule);
    }

    private List<Tag> getSymptomsYes(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.YES) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }

    private List<Tag> getSymptomsUnknown(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.UNKNOWN) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }

    private List<Tag> getSymptomsNo(List<Symptom> list) {
        List<Tag> results = new ArrayList();
        for (Symptom s: list) {
            if (s.getState() == SymptomState.NO) {
                results.add(new Tag(s.getName()));
            }
        }

        return results;
    }

    private void setupCallback() {
        onLinkClickListener = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                /*Symptoms s = (Symptoms)item;
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_location_layout, s.getIllLocation());
                AlertDialog dialog = simpleDialog.show();*/
                //Toast.makeText(getContext(), "Hurray!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }
}
