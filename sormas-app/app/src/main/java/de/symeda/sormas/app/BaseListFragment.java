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

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.util.Bundler;

public abstract class BaseListFragment<TListAdapter extends RecyclerView.Adapter> extends BaseFragment implements OnListItemClickListener {

	private AsyncTask jobTask;
	private BaseListActivity baseListActivity;
	private IUpdateSubHeadingTitle subHeadingHandler;
	private TListAdapter adapter;
	private Enum listFilter;

	protected static <TFragment extends BaseListFragment> TFragment newInstance(Class<TFragment> fragmentClass, Bundle data, Enum listFilter) {
		data = new Bundler(data).setListFilter(listFilter).get();
		TFragment fragment = newInstance(fragmentClass, data);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null)
			savedInstanceState = getArguments();
		listFilter = new Bundler(savedInstanceState).getListFilter();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setListFilter(listFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(this.getRootListLayout(), container, false);

		if (getActivity() instanceof BaseListActivity) {
			this.baseListActivity = (BaseListActivity) this.getActivity();
		} else {
			throw new NotImplementedException("The list activity for fragment must implement BaseListActivity");
		}

		if (getActivity() instanceof IUpdateSubHeadingTitle) {
			this.subHeadingHandler = (IUpdateSubHeadingTitle) this.getActivity();
		} else {
			throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; " + "implement IUpdateSubHeadingTitle");
		}

		this.adapter = getNewListAdapter();
		this.adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onChanged() {
				updateEmptyListHint();
			}
		});

		if (this.adapter instanceof HasOnListItemClickListener) {
			((HasOnListItemClickListener) this.adapter).setOnListItemClickListener(this);
		} else {
			throw new NotImplementedException(
				"setOnListItemClickListener is not supported by the adapter; " + "implement HasOnListItemClickListener");
		}

//        jobTask = new DefaultAsyncTask(getContext()) {
//            @Override
//            public void onPreExecute() {
//                getBaseActivity().showPreloader();
//            }
//
//            @Override
//            public void doInBackground(final TaskResultHolder resultHolder) {
//                prepareFragmentData();
//            }
//
//            @Override
//            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
//                getBaseActivity().hidePreloader();
//            }
//        }.executeOnThreadPool();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout) this.getView().findViewById(R.id.swiperefresh);
		if (swiperefresh != null) {
			swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

				@Override
				public void onRefresh() {
					getBaseActivity().synchronizeChangedData();
				}
			});
		}

		subHeadingHandler.updateSubHeadingTitle();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public int getRootListLayout() {
		return R.layout.fragment_root_list_layout;
	}

	public abstract TListAdapter getNewListAdapter();

	public abstract void onListItemClick(View view, int position, Object item);

	public TListAdapter getListAdapter() {
		return this.adapter;
	}

	public Enum getListFilter() {
		return listFilter;
	}

	protected void updateEmptyListHint() {
		if (getView() == null) {
			return;
		}

		TextView emptyListHintView = (TextView) getView().findViewById(R.id.emptyListHint);

		if (emptyListHintView == null) {
			return;
		}

		if (adapter.getItemCount() == 0) {
			emptyListHintView
				.setText(getResources().getString(canAddToList() ? R.string.hint_no_records_found_add_new : R.string.hint_no_records_found));
			emptyListHintView.setVisibility(View.VISIBLE);
		} else {
			emptyListHintView.setVisibility(View.GONE);
		}
	}

	public IUpdateSubHeadingTitle getSubHeadingHandler() {
		return this.subHeadingHandler;
	}

	protected boolean canAddToList() {
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (jobTask != null && !jobTask.isCancelled())
			jobTask.cancel(true);
	}
}
