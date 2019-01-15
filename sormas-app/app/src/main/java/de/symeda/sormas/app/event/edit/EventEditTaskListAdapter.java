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

package de.symeda.sormas.app.event.edit;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowReadTaskListItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.task.Task;

public class EventEditTaskListAdapter extends DataBoundAdapter<RowReadTaskListItemLayoutBinding> {

    private static final String TAG = EventEditTaskListAdapter.class.getSimpleName();

    private List<Task> data;
    private OnListItemClickListener mOnListItemClickListener;

    public EventEditTaskListAdapter(int rowLayout, OnListItemClickListener onListItemClickListener, List<Task> data) {
        super(rowLayout);
        this.mOnListItemClickListener = onListItemClickListener;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowReadTaskListItemLayoutBinding> holder,
                            int position, List<Object> payloads) {

        Task record = data.get(position);
        holder.setData(record);
        holder.setOnListItemClickListener(this.mOnListItemClickListener);

        indicatePriority(holder.binding.imgPriorityStatusIcon, record);
        indicateStatus(holder.binding.imgTaskStatusIcon, record);

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

//    public void updateUnreadIndicator(DataBoundViewHolder<RowReadTaskListItemLayoutBinding> holder, Task item) {
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

    public void indicatePriority(ImageView imgTaskPriorityIcon, Task task) {
        Resources resources = imgTaskPriorityIcon.getContext().getResources();
        Drawable drw = (Drawable)ContextCompat.getDrawable(imgTaskPriorityIcon.getContext(), R.drawable.indicator_status_circle);
        if (task.getPriority() == TaskPriority.HIGH) {
            drw.setColorFilter(resources.getColor(R.color.indicatorTaskPriorityHigh), PorterDuff.Mode.SRC_OVER);
        } else if (task.getPriority() == TaskPriority.LOW) {
            drw.setColorFilter(resources.getColor(R.color.indicatorTaskPriorityLow), PorterDuff.Mode.SRC_OVER);
        } else if (task.getPriority() == TaskPriority.NORMAL) {
            drw.setColorFilter(resources.getColor(R.color.indicatorTaskPriorityNormal), PorterDuff.Mode.SRC_OVER);
        }

        imgTaskPriorityIcon.setBackground(drw);
    }

    public void indicateStatus(ImageView imgTaskStatusIcon, Task task) {
        Resources resources = imgTaskStatusIcon.getContext().getResources();
        Drawable drw = (Drawable)ContextCompat.getDrawable(imgTaskStatusIcon.getContext(), R.drawable.indicator_status_circle);
        if (task.getTaskStatus() == TaskStatus.PENDING) {
            drw.setColorFilter(resources.getColor(R.color.indicatorTaskPending), PorterDuff.Mode.SRC_OVER);
        } else if (task.getTaskStatus() == TaskStatus.DONE) {
            drw.setColorFilter(resources.getColor(R.color.indicatorTaskDone), PorterDuff.Mode.SRC_OVER);
        } else if (task.getTaskStatus() == TaskStatus.REMOVED) {
            drw.setColorFilter(resources.getColor(R.color.indicatorTaskRemoved), PorterDuff.Mode.SRC_OVER);
        } else if (task.getTaskStatus() == TaskStatus.NOT_EXECUTABLE) {
            drw.setColorFilter(resources.getColor(R.color.indicatorTaskNotExecutable), PorterDuff.Mode.SRC_OVER);
        }

        imgTaskStatusIcon.setBackground(drw);
    }
}