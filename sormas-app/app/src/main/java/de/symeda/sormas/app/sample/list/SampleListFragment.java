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

package de.symeda.sormas.app.sample.list;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.read.SampleReadActivity;

public class SampleListFragment extends BaseListFragment<SampleListAdapter> implements OnListItemClickListener {

    private List<Sample> samples;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static SampleListFragment newInstance(ShipmentStatus listFilter) {
        return newInstance(SampleListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    protected void prepareFragmentData() {

        switch ((ShipmentStatus) getListFilter()) {
            case NOT_SHIPPED:
                samples = DatabaseHelper.getSampleDao().queryNotShipped();
                break;
            case SHIPPED:
                samples = DatabaseHelper.getSampleDao().queryShipped();
                break;
            case RECEIVED:
                samples = DatabaseHelper.getSampleDao().queryReceived();
                break;
            case REFERRED_OTHER_LAB:
                samples = DatabaseHelper.getSampleDao().queryReferred();
                break;
            default:
                throw new IllegalArgumentException(getListFilter().toString());
        }
        getListAdapter().replaceAll(samples);
    }

    @Override
    public SampleListAdapter getNewListAdapter() {
        return new SampleListAdapter(R.layout.row_sample_list_item_layout, this, samples);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Sample sample = (Sample) item;
        SampleReadActivity.startActivity(getContext(), sample.getUuid());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }
}
