/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.sample.read;

import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.pathogentest.list.PathogenTestListAdapter;
import de.symeda.sormas.app.pathogentest.list.PathogenTestListViewModel;
import de.symeda.sormas.app.pathogentest.read.PathogenTestReadActivity;

public class SampleReadPathogenTestListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<PathogenTest>, Sample>
	implements OnListItemClickListener {

	public static final String TAG = SampleReadPathogenTestListFragment.class.getSimpleName();

	private PathogenTestListAdapter adapter;

	public static SampleReadPathogenTestListFragment newInstance(Sample activityRootData) {
		return newInstance(SampleReadPathogenTestListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((SampleReadActivity) getActivity()).showPreloader();
		adapter = new PathogenTestListAdapter();
		PathogenTestListViewModel model = ViewModelProviders.of(this).get(PathogenTestListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getPathogenTests().observe(this, contacts -> {
			((SampleReadActivity) getActivity()).hidePreloader();
			adapter.submitList(contacts);
			updateEmptyListHint(contacts);
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
		return r.getString(R.string.heading_pathogen_tests_list);
	}

	@Override
	public List<PathogenTest> getPrimaryData() {
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
		PathogenTest rec = (PathogenTest) item;
		PathogenTestReadActivity.startActivity(getActivity(), rec.getUuid());
	}
}
