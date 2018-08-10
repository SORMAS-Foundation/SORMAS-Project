package de.symeda.sormas.app.contact.read;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.visit.VisitSection;
import de.symeda.sormas.app.visit.read.VisitReadActivity;

public class ContactReadFollowUpVisitListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<Visit>, Contact> implements OnListItemClickListener {

    private List<Visit> record;

    private ContactReadFollowupListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static ContactReadFollowUpVisitListFragment newInstance(Contact activityRootData) {
        return newInstance(ContactReadFollowUpVisitListFragment.class, null, activityRootData);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Contact contact = getActivityRootData();
        record = DatabaseHelper.getVisitDao().getByContact(contact);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        updateEmptyListHint(record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new ContactReadFollowupListAdapter(
                R.layout.row_read_followup_list_item_layout, ContactReadFollowUpVisitListFragment.this, record);

        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_visit_information);
    }

    @Override
    public List<Visit> getPrimaryData() {
        return record;
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Visit vist = (Visit) item;
        VisitReadActivity.startActivity(getActivity(), vist.getUuid(), getActivityRootData().getUuid(), VisitSection.VISIT_INFO);
    }

}