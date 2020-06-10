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

package de.symeda.sormas.app.visit.list;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowReadFollowupListItemLayoutBinding;

public class VisitListAdapter extends DataBoundAdapter<RowReadFollowupListItemLayoutBinding> {

	private static final String TAG = VisitListAdapter.class.getSimpleName();

	private List<Visit> data;
	private OnListItemClickListener mOnListItemClickListener;

	public VisitListAdapter(int rowLayout, OnListItemClickListener onListItemClickListener, List<Visit> data) {
		super(rowLayout);
		this.mOnListItemClickListener = onListItemClickListener;

		if (data == null)
			this.data = new ArrayList<>();
		else
			this.data = new ArrayList<>(data);
	}

	@Override
	protected void bindItem(DataBoundViewHolder<RowReadFollowupListItemLayoutBinding> holder, int position, List<Object> payloads) {

		Visit record = data.get(position);
		holder.setData(record);
		holder.setOnListItemClickListener(this.mOnListItemClickListener);

		//Sync Icon
		if (record.isModifiedOrChildModified()) {
			holder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
			holder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
		} else {
			holder.binding.imgSyncIcon.setVisibility(View.GONE);
		}

		// TODO #704
//        updateUnreadIndicator(holder, record);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

//    public void updateUnreadIndicator(DataBoundViewHolder<RowReadFollowupListItemLayoutBinding> holder, Visit item) {
//        backgroundRowItem = (LayerDrawable) ContextCompat.getDrawable(holder.context, R.drawable.background_list_activity_row);
//        unreadListItemIndicator = backgroundRowItem.findDrawableByLayerId(R.id.unreadListItemIndicator);
//
//        if (item != null) {
//            if (item.isUnreadOrChildUnread()) {
//                unreadListItemIndicator.setTint(holder.context.getResources().getColor(R.color.unreadIcon));
//            } else {
//                unreadListItemIndicator.setTint(holder.context.getResources().getColor(android.R.color.transparent));
//            }
//        }
//    }

	public void replaceAll(List<Visit> data) {
		if (data == null)
			return;

		this.data.clear();
		this.data.addAll(data);
	}
}
