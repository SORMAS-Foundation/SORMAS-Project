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

package de.symeda.sormas.app.core.adapter.databinding;

import java.util.List;

import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.OnRebindCallback;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @deprecated Use {@link BindingPagedListAdapter}
 */
@Deprecated
public abstract class DataBoundAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<DataBoundViewHolder<T>> {

	private static final Object DB_PAYLOAD = new Object();

	@Nullable
	private RecyclerView mRecyclerView;

	@LayoutRes
	private final int mLayoutId;

	/**
	 * This is used to block items from updating themselves. RecyclerView wants to know when an
	 * item is invalidated and it prefers to refresh it via onRebind. It also helps with performance
	 * since data binding will not update views that are not changed.
	 */
	private final OnRebindCallback mOnRebindCallback = new OnRebindCallback() {

		@Override
		public boolean onPreBind(ViewDataBinding binding) {
			if (mRecyclerView == null || mRecyclerView.isComputingLayout()) {
				return true;
			}
			int childAdapterPosition = mRecyclerView.getChildAdapterPosition(binding.getRoot());
			if (childAdapterPosition == RecyclerView.NO_POSITION) {
				return true;
			}
			notifyItemChanged(childAdapterPosition, DB_PAYLOAD);
			return false;
		}
	};

	/**
	 * Creates a DataBoundAdapter with the given item layout
	 *
	 * @param layoutId
	 *            The layout to be used for items. It must use data binding.
	 */
	public DataBoundAdapter(@LayoutRes int layoutId) {
		mLayoutId = layoutId;
	}

	@Override
	@CallSuper
	public DataBoundViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
		DataBoundViewHolder<T> vh = DataBoundViewHolder.create(parent, viewType);
		vh.binding.addOnRebindCallback(mOnRebindCallback);
		return vh;
	}

	@Override
	public final void onBindViewHolder(DataBoundViewHolder<T> holder, int position, List<Object> payloads) {
		// when a VH is rebound to the same item, we don't have to call the setters
		if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
			bindItem(holder, position, payloads);
		}
		holder.binding.executePendingBindings();
	}

	/**
	 * Override this method to handle binding your items into views
	 *
	 * @param holder
	 *            The ViewHolder that has the binding instance
	 * @param position
	 *            The position of the item in the adapter
	 * @param payloads
	 *            The payloads that were passed into the onBind method
	 */
	protected abstract void bindItem(DataBoundViewHolder<T> holder, int position, List<Object> payloads);

	private boolean hasNonDataBindingInvalidate(List<Object> payloads) {
		for (Object payload : payloads) {
			if (payload != DB_PAYLOAD) {
				return true;
			}
		}
		return false;
	}

	@Override
	public final void onBindViewHolder(DataBoundViewHolder<T> holder, int position) {
		throw new IllegalArgumentException("just overridden to make final.");
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		mRecyclerView = recyclerView;
	}

	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		mRecyclerView = null;
	}

	@Override
	public final int getItemViewType(int position) {
		return getItemLayoutId(position);
	}

	@LayoutRes
	public int getItemLayoutId(int position) {
		return mLayoutId;
	}
}
