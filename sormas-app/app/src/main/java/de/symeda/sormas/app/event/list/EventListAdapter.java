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

package de.symeda.sormas.app.event.list;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowEventListItemLayoutBinding;

public class EventListAdapter extends BindingPagedListAdapter<Event, RowEventListItemLayoutBinding> {

    public EventListAdapter() {
        super(R.layout.row_event_list_item_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (getItemViewType(position) == TYPE_ITEM) {
            BindingViewHolder<Event, RowEventListItemLayoutBinding> pagedHolder = (BindingViewHolder) holder;
            Event item = getItem(position);

            pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);

            indicateEventType(pagedHolder.binding.imgEventTypeIcon, item);

            if (item.isModifiedOrChildModified()) {
                pagedHolder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
                pagedHolder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
            } else {
                pagedHolder.binding.imgSyncIcon.setVisibility(View.GONE);
            }
        }

        // TODO #704
//        updateUnreadIndicator(holder, record);
    }

//    public void updateUnreadIndicator(DataBoundViewHolder<RowEventListItemLayoutBinding> holder, Event item) {
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

    public void indicateEventType(ImageView imgEventTypeIcon, Event eventRecord) {
        Resources resources = imgEventTypeIcon.getContext().getResources();
        Drawable drw = ContextCompat.getDrawable(imgEventTypeIcon.getContext(), R.drawable.indicator_status_circle);
        if (eventRecord.getEventType() == EventType.RUMOR) {
            drw.setColorFilter(resources.getColor(R.color.indicatorRumorEvent), PorterDuff.Mode.SRC_OVER);
        } else if (eventRecord.getEventType() == EventType.OUTBREAK) {
            drw.setColorFilter(resources.getColor(R.color.indicatorOutbreakEvent), PorterDuff.Mode.SRC_OVER);
        }

        imgEventTypeIcon.setBackground(drw);
    }

}
