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

package de.symeda.sormas.app.core.adapter;

import java.util.List;

import android.content.Context;
import android.os.Build;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableList;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.adapters.ListenerUtil;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;

/**
 * Created by Orson on 19/12/2017.
 */

public class SimpleListBindingAdapters {

	public static final String TAG = SimpleListBindingAdapters.class.getSimpleName();

	/**
	 * Prevent instantiation
	 */
	private SimpleListBindingAdapters() {
	}

	@BindingAdapter(value = {
		"entries",
		"layout",
		"callback" }, requireAll = false)
	public static <T> void setEntries(
		ViewGroup viewGroup,
		List<T> oldEntries,
		int oldLayoutId,
		IEntryItemOnClickListener oldCallback,
		List<T> newEntries,
		int newLayoutId,
		IEntryItemOnClickListener newCallback) {

		/*
		 * if (newEntries.size() > 0)
		 * viewGroup.setVisibility(View.VISIBLE);
		 * else
		 * viewGroup.setVisibility(View.GONE);
		 */

		if (oldEntries == newEntries && oldLayoutId == newLayoutId && oldCallback == newCallback) {
			return;
		}

		EntryChangeListener listener = ListenerUtil.getListener(viewGroup, R.id.entryListener);
		if (oldEntries != newEntries && listener != null && oldEntries instanceof ObservableList) {
			((ObservableList) oldEntries).removeOnListChangedCallback(listener);
		}

		if (newEntries == null || newEntries.size() <= 0) {
			viewGroup.removeAllViews();
			viewGroup.setVisibility(View.GONE);
		} else {
			viewGroup.setVisibility(View.VISIBLE);
			if (newEntries instanceof ObservableList) {
				if (listener == null) {
					listener = new EntryChangeListener(viewGroup, newLayoutId, newCallback);
					ListenerUtil.trackListener(viewGroup, listener, R.id.entryListener);
				} else {
					listener.setLayoutId(newLayoutId);
				}
				if (newEntries != oldEntries) {
					((ObservableList) newEntries).addOnListChangedCallback(listener);
				}
			}
			resetViews(viewGroup, newLayoutId, newEntries, newCallback);
		}
	}

	private static ViewDataBinding bindLayout(
		LayoutInflater inflater,
		ViewGroup parent,
		int layoutId,
		Object entry,
		int entryIndex,
		Object callback) {
		ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
		String layoutName = parent.getResources().getResourceEntryName(layoutId);
		View rootView = binding.getRoot();

		rootView.setId(entryIndex);

		if (!binding.setVariable(BR.data, entry)) {
			Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
		}

		if (!binding.setVariable(BR.index, entryIndex)) {
			Log.e(TAG, "There is no variable 'index' in layout " + layoutName);
		}

		if (callback != null) {
			if (!binding.setVariable(BR.callback, callback)) {
				Log.e(TAG, "There is no variable 'callback' in layout " + layoutName);
			}
		}

		return binding;
	}

	private static void resetViews(ViewGroup parent, int layoutId, List entries, Object callback) {
		parent.removeAllViews();
		if (layoutId == 0) {
			return;
		}
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < entries.size(); i++) {
			Object entry = entries.get(i);
			ViewDataBinding binding = bindLayout(inflater, parent, layoutId, entry, i, callback);
			parent.addView(binding.getRoot());
		}
	}

	private static void startTransition(ViewGroup root) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			TransitionManager.beginDelayedTransition(root);
		}
	}

	private static class EntryChangeListener extends ObservableList.OnListChangedCallback {

		private final ViewGroup mTarget;
		private int mLayoutId;
		private IEntryItemOnClickListener mCallback;

		public EntryChangeListener(ViewGroup target, int layoutId, IEntryItemOnClickListener callback) {
			mTarget = target;
			mLayoutId = layoutId;
			mCallback = callback;
		}

		public void setLayoutId(int layoutId) {
			mLayoutId = layoutId;
		}

		@Override
		public void onChanged(ObservableList observableList) {
			resetViews(mTarget, mLayoutId, observableList, mCallback);
		}

		@Override
		public void onItemRangeChanged(ObservableList observableList, int start, int count) {
			if (mLayoutId == 0) {
				return;
			}
			LayoutInflater inflater = (LayoutInflater) mTarget.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//startTransition(mTarget);
			final int end = start + count;
			for (int i = start; i < end; i++) {
				Object data = observableList.get(i);
				ViewDataBinding binding = bindLayout(inflater, mTarget, mLayoutId, data, i, mCallback);
				binding.setVariable(BR.data, observableList.get(i));
				mTarget.removeViewAt(i);
				mTarget.addView(binding.getRoot(), i);
			}
		}

		@Override
		public void onItemRangeInserted(ObservableList observableList, int start, int count) {
			if (mLayoutId == 0) {
				return;
			}
			//startTransition(mTarget);
			final int end = start + count;
			LayoutInflater inflater = (LayoutInflater) mTarget.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (int i = end - 1; i >= start; i--) {
				Object entry = observableList.get(i);
				ViewDataBinding binding = bindLayout(inflater, mTarget, mLayoutId, entry, i, mCallback);
				mTarget.addView(binding.getRoot(), start);
			}
		}

		@Override
		public void onItemRangeMoved(ObservableList observableList, int from, int to, int count) {
			if (mLayoutId == 0) {
				return;
			}
			//startTransition(mTarget);
			for (int i = 0; i < count; i++) {
				View view = mTarget.getChildAt(from);
				mTarget.removeViewAt(from);
				int destination = (from > to) ? to + i : to;
				mTarget.addView(view, destination);
			}
		}

		@Override
		public void onItemRangeRemoved(ObservableList observableList, int start, int count) {
			if (mLayoutId == 0) {
				return;
			}
			//startTransition(mTarget);
			for (int i = 0; i < count; i++) {
				mTarget.removeViewAt(start);
			}
		}
	}
}
