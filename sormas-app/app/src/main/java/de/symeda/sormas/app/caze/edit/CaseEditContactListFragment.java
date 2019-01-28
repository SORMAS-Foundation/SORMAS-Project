/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.contact.list.ContactListAdapter;
import de.symeda.sormas.app.contact.list.ContactListViewModel;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;

public class CaseEditContactListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Contact>, Case> implements OnListItemClickListener {

    public static final String TAG = CaseEditContactListFragment.class.getSimpleName();

    private ContactListAdapter adapter;
    private ContactListViewModel model;
    private LinearLayoutManager linearLayoutManager;

    public static CaseEditContactListFragment newInstance(Case activityRootData) {
        return newInstance(CaseEditContactListFragment.class, null, activityRootData);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((CaseEditActivity) getActivity()).showPreloader();
        adapter = new ContactListAdapter(R.layout.row_read_contact_list_item_layout, this);
        model = ViewModelProviders.of(this).get(ContactListViewModel.class);
        model.getContacts(getActivityRootData()).observe(this, contacts -> {
            adapter.replaceAll(contacts);
            adapter.notifyDataSetChanged();
            updateEmptyListHint(contacts);
            ((CaseEditActivity) getActivity()).hidePreloader();
        });
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_contacts);
    }

    @Override
    public List<Contact> getPrimaryData() {
        throw new UnsupportedOperationException("Sub list fragments don't hold their data");
    }

    @Override
    protected void prepareFragmentData() {

    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
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
