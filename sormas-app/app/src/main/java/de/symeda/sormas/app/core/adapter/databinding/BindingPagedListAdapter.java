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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.databinding.RowLoadingListItemLayoutBinding;

public abstract class BindingPagedListAdapter<T extends AbstractDomainObject, V extends ViewDataBinding>
	extends PagedListAdapter<T, RecyclerView.ViewHolder>
	implements OnListItemClickListener.HasOnListItemClickListener {

	protected static final int TYPE_ITEM = 0;
	protected static final int TYPE_PROGRESS = 1;
	protected static final int TYPE_HIDDEN = 2;

	protected OnListItemClickListener mOnListItemClickListener;

	@LayoutRes
	private final int layoutId;

	public BindingPagedListAdapter(@LayoutRes int layoutId) {
		super(new DiffUtil.ItemCallback<T>() {

			@Override
			public boolean areItemsTheSame(@NonNull AbstractDomainObject oldItem, @NonNull AbstractDomainObject newItem) {
				return oldItem.equals(newItem);
			}

			@Override
			public boolean areContentsTheSame(@NonNull AbstractDomainObject oldItem, @NonNull AbstractDomainObject newItem) {
				return oldItem.getLocalChangeDate().equals(newItem.getLocalChangeDate());
			}
		});
		this.layoutId = layoutId;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		switch (viewType) {
		case TYPE_ITEM:
			BindingViewHolder<T, V> itemViewHolder = BindingViewHolder.create(parent, layoutId);
			return itemViewHolder;
		case TYPE_PROGRESS:
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			RowLoadingListItemLayoutBinding progressBinding = RowLoadingListItemLayoutBinding.inflate(layoutInflater, parent, false);
			ProgressItemViewHolder progressItemViewHolder = new ProgressItemViewHolder(progressBinding);
			return progressItemViewHolder;
		case TYPE_HIDDEN:
			return new RecyclerView.ViewHolder(new View(parent.getContext())) {
			};
		default:
			throw new IllegalArgumentException(String.valueOf(viewType));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		switch (getItemViewType(position)) {
		case TYPE_ITEM:
			T item = getItem(position);
			((BindingViewHolder) holder).bind(item);
			break;
		case TYPE_PROGRESS:
			((ProgressItemViewHolder) holder).setPosition(position, getCurrentList().size());
			break;
		case TYPE_HIDDEN:
			// do nothing
			break;
		default:
			throw new IllegalArgumentException(String.valueOf(getItemViewType(position)));
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position) == null) {
			if (position > 0 && getItem(position - 1) != null) {
				return TYPE_PROGRESS; // show a progress entry for the first unloaded item
			} else {
				return TYPE_HIDDEN; // hide all other unloaded items
			}
		} else {
			return TYPE_ITEM;
		}
	}

	@Override
	public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
		this.mOnListItemClickListener = onListItemClickListener;
	}
}
