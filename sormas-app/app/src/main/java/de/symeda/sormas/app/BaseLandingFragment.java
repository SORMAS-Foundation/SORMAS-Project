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

package de.symeda.sormas.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.component.menu.PageMenuControl;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;

public abstract class BaseLandingFragment<E extends Enum<E>, TAdapter extends EnumMapDataBinderAdapter<E>> extends BaseFragment {

	private BaseLandingActivity baseLandingActivity;
	private RecyclerView.LayoutManager layoutManager;
	private TAdapter adapter;
	private RecyclerView recyclerView;
	protected ViewDataBinding rootBinding;
	protected View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(this.getRootLandingLayout(), container, false);

		if (getActivity() instanceof BaseLandingActivity) {
			this.baseLandingActivity = (BaseLandingActivity) this.getActivity();
		} else {
			throw new NotImplementedException("The landing activity for fragment must implement BaseLandingActivity");
		}

		super.onCreateView(inflater, container, savedInstanceState);

		//Inflate Root
		rootBinding = DataBindingUtil.inflate(inflater, getRootLandingLayout(), container, false);
		rootView = rootBinding.getRoot();

		this.recyclerView = createRecyclerView(view);
		this.layoutManager = createLayoutManager();
		this.adapter = createLandingAdapter();

		return view;
	}

	public int getRootLandingLayout() {
		return R.layout.fragment_root_landing_layout;
	}

	public PageMenuControl createMenuControl(View view) {
		return (PageMenuControl) view.findViewById(R.id.landingPageMenuControl);
	}

	public RecyclerView createRecyclerView(View view) {
		return (RecyclerView) view.findViewById(R.id.recyclerview_main);
	}

	public abstract TAdapter createLandingAdapter();

	public abstract RecyclerView.LayoutManager createLayoutManager();

	public TAdapter getLandingAdapter() {
		return this.adapter;
	}

	public RecyclerView.LayoutManager getLandingLayoutManager() {
		return this.layoutManager;
	}

	public BaseLandingActivity getBaseLandingActivity() {
		return this.baseLandingActivity;
	}

	public RecyclerView getRecyclerView() {
		return this.recyclerView;
	}

	public void configure() {
		this.recyclerView.setAdapter(this.adapter);
		this.recyclerView.setLayoutManager(this.layoutManager);
	}

	public boolean isShowSaveAction() {
		return true;
	}

	public boolean isShowNewAction() {
		return false;
	}
}
