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

package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.sample.list.SampleListAdapter;
import de.symeda.sormas.app.sample.list.SampleListViewModel;
import de.symeda.sormas.app.sample.read.SampleReadActivity;

public class CaseReadSampleListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<Sample>, Case> implements OnListItemClickListener {

    private SampleListAdapter adapter;
    private SampleListViewModel model;
    private LinearLayoutManager linearLayoutManager;

    public static CaseReadSampleListFragment newInstance(Case activityRootData) {
        return newInstance(CaseReadSampleListFragment.class, null, activityRootData);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((CaseReadActivity) getActivity()).showPreloader();
        adapter = new SampleListAdapter(R.layout.row_sample_list_item_layout, this);
        model = ViewModelProviders.of(this).get(SampleListViewModel.class);
        model.getSamples(getActivityRootData()).observe(this, samples -> {
            adapter.replaceAll(samples);
            adapter.notifyDataSetChanged();
            updateEmptyListHint(samples);
            ((CaseReadActivity) getActivity()).hidePreloader();
        });
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {

    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_samples);
    }

    @Override
    public List<Sample> getPrimaryData() {
        throw new UnsupportedOperationException("Sub list fragments don't hold their data");
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
        Sample sample = (Sample) item;
        SampleReadActivity.startActivity(getActivity(), sample.getUuid());
    }
}
