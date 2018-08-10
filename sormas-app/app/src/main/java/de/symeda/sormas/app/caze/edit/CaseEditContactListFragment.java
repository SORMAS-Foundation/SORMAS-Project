package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;

public class CaseEditContactListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Contact>, Case> implements OnListItemClickListener {

    public static final String TAG = CaseEditContactListFragment.class.getSimpleName();

    private List<Contact> record;
    private CaseEditContactListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static CaseEditContactListFragment newInstance(Case activityRootData) {
        return newInstance(CaseEditContactListFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_contacts);
    }

    @Override
    public List<Contact> getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        Case caze = getActivityRootData();
        record = DatabaseHelper.getContactDao().getByCase(caze);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        updateEmptyListHint(record);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseEditContactListAdapter(R.layout.row_read_contact_list_item_layout, this, record);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getRootEditLayout() {
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return false;
    }

    @Override
    public boolean isShowNewAction() {
        return true;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Contact contact = (Contact) item;
        ContactEditActivity.startActivity(getContext(), contact.getUuid(), ContactSection.CONTACT_INFO);
    }
}
