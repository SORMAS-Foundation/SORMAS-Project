/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.core.adapter.databinding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.OnRebindCallback;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.databinding.RowLoadingListItemLayoutBinding;

public abstract class BasePagedDataBoundAdapter<T extends AbstractDomainObject, V extends ViewDataBinding> extends PagedListAdapter<T, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private static final Object DB_PAYLOAD = new Object();

    @LayoutRes
    private final int layoutId;

    @Nullable
    private RecyclerView recyclerView;

    public BasePagedDataBoundAdapter(@LayoutRes int layoutId) {
        super(new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull AbstractDomainObject oldItem, @NonNull AbstractDomainObject newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull AbstractDomainObject oldItem, @NonNull AbstractDomainObject newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.layoutId = layoutId;
    }

    public int getItemLayoutId(int position) {
        return layoutId;
    }

    private final OnRebindCallback mOnRebindCallback = new OnRebindCallback() {
        @Override
        public boolean onPreBind(ViewDataBinding binding) {
            if (recyclerView == null || recyclerView.isComputingLayout()) {
                return true;
            }
            int childAdapterPosition = recyclerView.getChildAdapterPosition(binding.getRoot());
            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                return true;
            }
            notifyItemChanged(childAdapterPosition, DB_PAYLOAD);
            return false;
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PROGRESS) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            RowLoadingListItemLayoutBinding progressBinding = RowLoadingListItemLayoutBinding.inflate(layoutInflater, parent, false);
            ProgressItemViewHolder viewHolder = new ProgressItemViewHolder(progressBinding);
            return viewHolder;
//            PagedDataBoundViewHolder<RowLoadingListItemLayoutBinding> viewHolder = PagedDataBoundViewHolder.create(parent, viewType);
//            viewHolder.binding.addOnRebindCallback(mOnRebindCallback);
//            return viewHolder;
        } else {
            PagedDataBoundViewHolder<V> viewHolder = PagedDataBoundViewHolder.create(parent, viewType);
            viewHolder.binding.addOnRebindCallback(mOnRebindCallback);
            return viewHolder;
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                       List<Object> payloads) {
        // when a VH is rebound to the same item, we don't have to call the setters
        if (holder instanceof PagedDataBoundViewHolder) {
            if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
                bindItem((PagedDataBoundViewHolder<V>) holder, position, payloads);
            }
            ((PagedDataBoundViewHolder<V>) holder).binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
//        if (hasExtraRow() && position == getItemCount() - 1) {
        if (position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

//    private boolean hasExtraRow() {
//        if (getItemCount() < ) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * Override this method to handle binding your items into views
     *
     * @param holder The ViewHolder that has the binding instance
     * @param position The position of the item in the adapter
     * @param payloads The payloads that were passed into the onBind method
     */
    protected abstract void bindItem(PagedDataBoundViewHolder<V> holder, int position,
                                     List<Object> payloads);

    private boolean hasNonDataBindingInvalidate(List<Object> payloads) {
        for (Object payload : payloads) {
            if (payload != DB_PAYLOAD) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            bindItem((PagedDataBoundViewHolder<V>) holder, position, null);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = null;
    }
}
