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
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.OnRebindCallback;
import androidx.databinding.ViewDataBinding;

import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;

public abstract class BaseReportFragment<TBinding extends ViewDataBinding> extends BaseFragment {

	private final static String TAG = BaseReportFragment.class.getSimpleName();

	private AsyncTask jobTask;
	private BaseReportActivity baseReportActivity;
	private IUpdateSubHeadingTitle subHeadingHandler;
	private ViewDataBinding rootBinding;
	private TBinding contentViewStubBinding;
	private boolean skipAfterLayoutBinding = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		if (getActivity() instanceof BaseReportActivity) {
			this.baseReportActivity = (BaseReportActivity) this.getActivity();
		} else {
			throw new NotImplementedException("The list activity for fragment must implement BaseReportActivity");
		}

		if (getActivity() instanceof IUpdateSubHeadingTitle) {
			this.subHeadingHandler = (IUpdateSubHeadingTitle) this.getActivity();
		} else {
			throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; " + "implement IUpdateSubHeadingTitle");
		}

		if (!(getActivity() instanceof NotificationContext)) {
			throw new NotImplementedException("Activity for fragment does not support showErrorNotification; " + "implement NotificationContext");
		}

		//Inflate Root
		rootBinding = DataBindingUtil.inflate(inflater, getRootLayoutResId(), container, false);
		View rootView = rootBinding.getRoot();

		final ViewStub vsChildFragmentFrame = rootView.findViewById(R.id.vsChildFragmentFrame);
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
						skipAfterLayoutBinding = true;

						getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
					}
				});
				onLayoutBinding(contentViewStubBinding);
			}
		});

		vsChildFragmentFrame.setLayoutResource(getReportLayout());

		jobTask = new DefaultAsyncTask(getContext()) {

			@Override
			public void onPreExecute() {
				getBaseActivity().showPreloader();
			}

			@Override
			public void doInBackground(final TaskResultHolder resultHolder) {
				prepareFragmentData(savedInstanceState);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				getBaseActivity().hidePreloader();

				if (taskResult.getResultStatus().isFailed())
					return;

				vsChildFragmentFrame.inflate();
			}
		}.executeOnThreadPool();

		return rootView;
	}

	public int getRootLayoutResId() {
		return R.layout.fragment_root_report_layout;
	}

	public IUpdateSubHeadingTitle getSubHeadingHandler() {
		return this.subHeadingHandler;
	}

	public BaseReportActivity getBaseReportActivity() {
		return this.baseReportActivity;
	}

	protected String getSubHeadingTitle() {
		return null;
	}

	protected ViewDataBinding getRootBinding() {
		return rootBinding;
	}

	protected TBinding getContentBinding() {
		return contentViewStubBinding;
	}

	protected abstract void prepareFragmentData(Bundle savedInstanceState);

	protected abstract void onLayoutBinding(TBinding contentBinding);

	protected void onAfterLayoutBinding(TBinding contentBinding) {

	}

	protected abstract int getReportLayout();

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (jobTask != null && !jobTask.isCancelled())
			jobTask.cancel(true);
	}
}
