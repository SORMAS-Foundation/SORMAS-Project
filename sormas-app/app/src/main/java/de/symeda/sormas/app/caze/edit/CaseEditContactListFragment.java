package de.symeda.sormas.app.caze.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.caze.edit.sub.CaseEditContactInfoActivity;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEditListLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditContactListFragment extends BaseEditActivityFragment<FragmentEditListLayoutBinding, List<Contact>> implements OnListItemClickListener {

    private String recordUuid;
    private InvestigationStatus pageStatus = null;
    private List<Contact> record;
    private CaseEditContactListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

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
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public List<Contact> getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = DatabaseHelper.getCaseDao().queryUuidReference(recordUuid);

            if (caze != null) {
                resultHolder.forList().add(DatabaseHelper.getContactDao().getByCase(caze));
            } else {
                resultHolder.forList().add(new ArrayList<Contact>());
            }
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            if (listIterator.hasNext()) {
                record = listIterator.next();

            }
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEditListLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseEditContactListAdapter(this.getActivity(), R.layout.row_read_contact_list_item_layout, this, record);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentEditListLayoutBinding contentBinding) {

    }

    @Override
    public void onResume() {
        super.onResume();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)getRootBinding().getRoot()
                .findViewById(R.id.swiperefresh);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseEditActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly,
                        true, false, swiperefresh, null);
            }
        });
    }

    @Override
    public int getRootEditLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_edit_list_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Contact r = (Contact)item;
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(getContext(), r.getUuid(), r.getContactClassification());
        CaseEditContactInfoActivity.goToActivity(getActivity(), dataCapsule);
    }
    public static CaseEditContactListFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditContactListFragment.class, capsule);
    }
}
