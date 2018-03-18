package de.symeda.sormas.app.contact.read.sub;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.symptom.Symptom;

/**
 * Created by Orson on 02/01/2018.
 */

public class ContactReadFollowUpSymptomsFragment extends BaseReadActivityFragment<FragmentContactReadSymptomsInfoLayoutBinding, Symptoms> {

    private String recordUuid;
    private VisitStatus pageStatus;
    private Symptoms record;

    private List<Tag> yesResult;
    private List<Tag> noResult;
    private List<Tag> unknownResult;

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

        pageStatus = (VisitStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid == null || recordUuid.isEmpty()) {
                resultHolder.forItem().add(DatabaseHelper.getSymptomsDao().build());
            } else {
                Visit visit = DatabaseHelper.getVisitDao().queryUuid(recordUuid);
                //resultHolder.forItem().add(visit);

                if (visit != null) {
                    Symptoms s = DatabaseHelper.getSymptomsDao().queryUuid(visit.getSymptoms().getUuid());
                    List<Symptom> sList = Symptom.makeSymptoms(visit.getDisease()).loadState(visit.getSymptoms());

                    resultHolder.forOther().add(getSymptomsYes(sList));
                    resultHolder.forOther().add(getSymptomsNo(sList));
                    resultHolder.forOther().add(getSymptomsUnknown(sList));

                    //TODO: Do we need this
                    resultHolder.forItem().add(s);
                }
            }
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            //TODO: Do we need this
            /*if (itemIterator.hasNext())
                symptom = itemIterator.next();*/

            if (otherIterator.hasNext())
                yesResult =  otherIterator.next();

            if (otherIterator.hasNext())
                noResult =  otherIterator.next();

            if (otherIterator.hasNext())
                unknownResult =  otherIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadSymptomsInfoLayoutBinding contentBinding) {
        if (record != null) {
            contentBinding.setData(record);

            contentBinding.setSymptomsYes(yesResult);
            contentBinding.setSymptomsUnknown(unknownResult);
            contentBinding.setSymptomsNo(noResult);
        }
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadSymptomsInfoLayoutBinding contentBinding) {

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_symptoms_info_layout;
    }

    public static ContactReadFollowUpSymptomsFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormFollowUpNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadFollowUpSymptomsFragment.class, capsule);
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
}
