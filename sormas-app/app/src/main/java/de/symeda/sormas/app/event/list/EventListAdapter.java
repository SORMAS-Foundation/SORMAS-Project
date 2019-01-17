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
import android.graphics.drawable.LayerDrawable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowEventListItemLayoutBinding;

public class EventListAdapter extends DataBoundAdapter<RowEventListItemLayoutBinding> implements ISetOnListItemClickListener {

    private List<Event> data;
    private OnListItemClickListener mOnListItemClickListener;

    public EventListAdapter(int rowLayout) {
        super(rowLayout);
        this.data = new ArrayList<>();
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowEventListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {
        Event record = data.get(position);
        holder.setData(record);
        holder.setOnListItemClickListener(this.mOnListItemClickListener);

        indicateEventType(holder.binding.imgEventTypeIcon, record);

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

    public Event getEvent(int position) {
        if (position < 0)
            return null;

        if (position >= this.data.size())
            return null;

        return (Event) this.data.get(position);
    }

    public void addAll(List<Event> data) {
        if (data == null)
            return;

        this.data.addAll(data);
    }

    public void replaceAll(List<Event> data) {
        if (data == null)
            return;

        this.data.clear();
        this.data.addAll(data);
    }

    public void clear() {
        if (this.data == null)
            return;

        this.data.clear();
    }

    @Override
    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.mOnListItemClickListener = onListItemClickListener;
    }
}
