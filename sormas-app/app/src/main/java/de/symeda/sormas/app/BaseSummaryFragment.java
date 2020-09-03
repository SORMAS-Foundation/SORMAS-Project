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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.dashboard.ISummaryLoadingStatusCommunicator;

public abstract class BaseSummaryFragment<E extends Enum<E>, TAdapter extends EnumMapDataBinderAdapter<E>> extends BaseFragment { // implements ISummaryLoadingStatus

	private RecyclerView.LayoutManager mLayoutManager;
	private TAdapter mAdapter;
	private RecyclerView mRecyclerView;
	private TextView mSummarySectionTitle;
	private ProgressBar mPreloader;
	private TextView mEmptySummaryHint;
	private ISummaryLoadingStatusCommunicator mDashboardActivityCommunicator;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(this.getRootListLayout(), container, false);

		mPreloader = (ProgressBar) view.findViewById(R.id.preloader);
		mSummarySectionTitle = (TextView) view.findViewById(R.id.summarySectionTitle);
		mEmptySummaryHint = (TextView) view.findViewById(R.id.emptySummaryHint);

		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_main);
		mLayoutManager = createLayoutManager();
		mAdapter = createSummaryAdapter();

		mRecyclerView.setNestedScrollingEnabled(false);

		if (mSummarySectionTitle != null)
			mSummarySectionTitle.setText(getResources().getString(getSectionTitleResId()));

		if (mEmptySummaryHint != null)
			mEmptySummaryHint.setText(
				String.format(getResources().getString(R.string.hint_dashboard_section_not_data), getResources().getString(getEntityResId())));

		//view.setMinimumHeight(getResources().getDimensionPixelSize(getMinHeightResId()));

		return view;
	}

	public int getRootListLayout() {
		return R.layout.fragment_root_summary_layout;
	}

	public RecyclerView getRecyclerView() {
		return mRecyclerView;
	}

	public TAdapter getLandingAdapter() {
		return this.mAdapter;
	}

	public void configure() {
		this.mRecyclerView.setAdapter(this.mAdapter);
		this.mRecyclerView.setLayoutManager(this.mLayoutManager);
	}

	protected void showPreloader() {
		if (mPreloader != null)
			mPreloader.setVisibility(View.VISIBLE);
	}

	protected void hidePreloader() {
		if (mPreloader != null)
			mPreloader.setVisibility(View.GONE);
	}

	protected void showEmptySummaryHint() {
		if (mEmptySummaryHint != null)
			mEmptySummaryHint.setVisibility(View.VISIBLE);
	}

	protected void hideEmptySummaryHint() {
		if (mEmptySummaryHint != null)
			mEmptySummaryHint.setVisibility(View.GONE);
	}

	protected abstract int getSectionTitleResId();

	protected abstract int getEntityResId();

	protected abstract TAdapter createSummaryAdapter();

	protected abstract RecyclerView.LayoutManager createLayoutManager();

	protected abstract int getContainerResId();

	public abstract String getIdentifier();
}
