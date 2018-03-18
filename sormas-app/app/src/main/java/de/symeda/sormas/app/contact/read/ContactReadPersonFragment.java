package de.symeda.sormas.app.contact.read;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadPersonInfoLayoutBinding;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadPersonFragment  extends BaseReadActivityFragment<FragmentContactReadPersonInfoLayoutBinding, Person> {

    private String recordUuid;
    private FollowUpStatus followUpStatus;
    private ContactClassification contactClassification = null;
    private Person record;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, contactClassification);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        followUpStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        contactClassification = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            if (recordUuid != null && !recordUuid.isEmpty()) {
                Person person = null;
                Contact contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);

                if (contact != null)
                    person = DatabaseHelper.getPersonDao().queryUuid(contact.getPerson().getUuid());

                resultHolder.forItem().add(person);
            }
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadPersonInfoLayoutBinding contentBinding) {

    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadPersonInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_person_info_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static ContactReadPersonFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadPersonFragment.class, capsule);
    }
}

