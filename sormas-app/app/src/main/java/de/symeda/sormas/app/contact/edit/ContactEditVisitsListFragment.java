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

package de.symeda.sormas.app.contact.edit;

import android.content.res.Resources;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.visit.VisitSection;
import de.symeda.sormas.app.visit.edit.VisitEditActivity;


public class ContactEditVisitsListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Visit>, Contact> implements OnListItemClickListener {

    private List<Visit> record;

    private ContactEditVisitsListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static ContactEditVisitsListFragment newInstance(Contact activityRootData) {
        return newInstance(ContactEditVisitsListFragment.class, null, activityRootData);
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
    protected void prepareFragmentData() {
        Contact contact = getActivityRootData();
        record = DatabaseHelper.getVisitDao().getByContact(contact);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        updateEmptyListHint(record);
        adapter = new ContactEditVisitsListAdapter(R.layout.row_read_followup_list_item_layout, this, record);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
    public boolean isShowNewAction() {
        return ConfigProvider.hasUserRight(UserRight.VISIT_CREATE)
                && getActivityRootData().getContactStatus() != ContactStatus.CONVERTED;
    }

    @Override
    public boolean isShowSaveAction() {
        return false;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Visit visit = (Visit) item;
        VisitEditActivity.startActivity(getContext(), visit.getUuid(), getActivityRootData().getUuid(), VisitSection.VISIT_INFO);
    }
}