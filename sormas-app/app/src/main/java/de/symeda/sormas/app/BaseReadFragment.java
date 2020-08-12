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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.OnRebindCallback;
import androidx.databinding.ViewDataBinding;

import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.util.AppFieldAccessCheckers;
import de.symeda.sormas.app.util.SoftKeyboardHelper;

public abstract class BaseReadFragment<TBinding extends ViewDataBinding, TData, TActivityRootData extends AbstractDomainObject> extends BaseFragment {

	public static final String TAG = BaseReadFragment.class.getSimpleName();

//    private AsyncTask jobTask;
	private BaseReadActivity baseReadActivity;
	private IUpdateSubHeadingTitle subHeadingHandler;

	private TBinding contentViewStubBinding;
	private View contentViewStubRoot;
	private ViewDataBinding rootBinding;
	private boolean skipAfterLayoutBinding = false;
	private TActivityRootData activityRootData;
	private View rootView;

	protected static <TFragment extends BaseReadFragment> TFragment newInstance(
		Class<TFragment> fragmentClass,
		Bundle data,
		AbstractDomainObject activityRootData) {
		TFragment fragment = newInstance(fragmentClass, data);
		fragment.setActivityRootData(activityRootData);
		return fragment;
	}

	protected static <TFragment extends BaseReadFragment> TFragment newInstanceWithFieldCheckers(
		Class<TFragment> fragmentClass,
		Bundle data,
		AbstractDomainObject activityRootData,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		AppFieldAccessCheckers fieldAccessCheckers) {
		TFragment fragment = newInstance(fragmentClass, data, activityRootData);
		fragment.setFieldVisibilityCheckers(fieldVisibilityCheckers);
		fragment.setFieldAccessCheckers(fieldAccessCheckers);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

		if (getActivity() instanceof BaseReadActivity) {
			this.baseReadActivity = (BaseReadActivity) this.getActivity();
		} else {
			throw new NotImplementedException("The read activity for fragment must implement BaseReadActivity");
		}

		if (getActivity() instanceof IUpdateSubHeadingTitle) {
			this.subHeadingHandler = (IUpdateSubHeadingTitle) this.getActivity();
		} else {
			throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; " + "implement IUpdateSubHeadingTitle");
		}

		super.onCreateView(inflater, container, savedInstanceState);

		//Inflate Root
		rootBinding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
		rootView = rootBinding.getRoot();

		if (getActivityRootData() == null) {
			// may happen when android tries to re-create old fragments for an activity
			return rootView;
		}

		final ViewStub vsChildFragmentFrame = (ViewStub) rootView.findViewById(R.id.vsChildFragmentFrame);
		vsChildFragmentFrame.setOnInflateListener(new ViewStub.OnInflateListener() {

			@Override
			public void onInflate(ViewStub stub, View inflated) {

				contentViewStubBinding = DataBindingUtil.bind(inflated);
				contentViewStubBinding.addOnRebindCallback(new OnRebindCallback() {

					@Override
					public void onBound(ViewDataBinding binding) {
						super.onBound(binding);

						if (!skipAfterLayoutBinding)
							onAfterLayoutBinding(contentViewStubBinding);
						skipAfterLayoutBinding = false;

						getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
					}
				});
				onLayoutBinding(contentViewStubBinding);
				contentViewStubRoot = contentViewStubBinding.getRoot();

				if (makeHeightMatchParent()) {
					contentViewStubRoot.getLayoutParams().height = MATCH_PARENT;
				} else {
					contentViewStubRoot.getLayoutParams().height = WRAP_CONTENT;
				}
			}
		});

		vsChildFragmentFrame.setLayoutResource(getReadLayout());
		prepareFragmentData(savedInstanceState);
		vsChildFragmentFrame.inflate();

//        jobTask = new DefaultAsyncTask(getContext()) {
//            @Override
//            public void onPreExecute() {
//                getBaseActivity().showPreloader();
//            }
//
//            @Override
//            public void doInBackground(final TaskResultHolder resultHolder) {
//                prepareFragmentData(savedInstanceState);
//            }
//
//            @Override
//            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
//                getBaseActivity().hidePreloader();
//
//                if (taskResult.getResultStatus().isFailed())
//                    return;
//
//                vsChildFragmentFrame.inflate();
//            }
//        }.executeOnThreadPool();

		return rootView;
	}

	protected void updateEmptyListHint(List list) {
		if (rootView == null)
			return;
		TextView emptyListHintView = (TextView) rootView.findViewById(R.id.emptyListHint);
		if (emptyListHintView == null)
			return;

		if (list == null || list.isEmpty()) {
			emptyListHintView.setText(getResources().getString(R.string.hint_no_records_found));
			emptyListHintView.setVisibility(View.VISIBLE);
		} else {
			emptyListHintView.setVisibility(View.GONE);
		}
	}

	protected abstract void prepareFragmentData(Bundle savedInstanceState);

	protected abstract void onLayoutBinding(TBinding contentBinding);

	protected void onAfterLayoutBinding(TBinding contentBinding) {
	}

	public boolean makeHeightMatchParent() {
		return false;
	}

	@Override
	public void onPause() {
		super.onPause();

		SoftKeyboardHelper.hideKeyboard(getActivity(), this);
	}

	@Deprecated
	public void onPageResume(TBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
	}

	public int getRootReadLayout() {
		return R.layout.fragment_root_read_layout;
	}

	public abstract int getReadLayout();

	public IUpdateSubHeadingTitle getSubHeadingHandler() {
		return this.subHeadingHandler;
	}

	public BaseReadActivity getBaseReadActivity() {
		return this.baseReadActivity;
	}

	protected String getSubHeadingTitle() {
		return null;
	}

	protected void setActivityRootData(TActivityRootData activityRootData) {
		this.activityRootData = activityRootData;
	}

	protected TActivityRootData getActivityRootData() {
		return this.activityRootData;
	}

	public abstract TData getPrimaryData();

	public ViewDataBinding getRootBinding() {
		return rootBinding;
	}

	public TBinding getContentBinding() {
		return contentViewStubBinding;
	}

	public boolean showEditAction() {
		return true;
	}

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (jobTask != null && !jobTask.isCancelled())
//            jobTask.cancel(true);
//    }
}
