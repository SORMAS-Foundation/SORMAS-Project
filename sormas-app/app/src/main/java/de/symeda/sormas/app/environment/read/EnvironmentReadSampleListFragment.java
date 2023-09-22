/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.environment.read;

import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.environmentsample.list.EnvironmentSampleListAdapter;
import de.symeda.sormas.app.environmentsample.list.EnvironmentSampleListViewModel;
import de.symeda.sormas.app.environmentsample.read.EnvironmentSampleReadActivity;

public class EnvironmentReadSampleListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<EnvironmentSample>, Environment>
	implements OnListItemClickListener {

	private EnvironmentSampleListAdapter adapter;

	public static EnvironmentReadSampleListFragment newInstance(Environment activityRootData) {
		return newInstance(EnvironmentReadSampleListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((EnvironmentReadActivity) getActivity()).showPreloader();
		adapter = new EnvironmentSampleListAdapter();
		EnvironmentSampleListViewModel model = new ViewModelProvider(this).get(EnvironmentSampleListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getEnvironmentSamples().observe(this, samples -> {
			adapter.submitList(samples);
			((EnvironmentReadActivity) getActivity()).hidePreloader();
			updateEmptyListHint(samples);
		});
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		adapter.setOnListItemClickListener(this);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {

	}

	@Override
	public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_environment_samples);
	}

	@Override
	public List<EnvironmentSample> getPrimaryData() {
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
		EnvironmentSample sample = (EnvironmentSample) item;
		EnvironmentSampleReadActivity.startActivity(getActivity(), sample.getUuid());
	}

}
