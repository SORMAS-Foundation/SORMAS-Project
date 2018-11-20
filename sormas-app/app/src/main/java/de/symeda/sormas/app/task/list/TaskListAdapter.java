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

package de.symeda.sormas.app.task.list;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowTaskListItemLayoutBinding;

public class TaskListAdapter extends DataBoundAdapter<RowTaskListItemLayoutBinding> implements ISetOnListItemClickListener {

    private static final String TAG = TaskListAdapter.class.getSimpleName();

    private final Context context;
    private List<Task> data = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener;

    public TaskListAdapter(Context context, int rowLayout, OnListItemClickListener onListItemClickListener) {
        this(context, rowLayout, onListItemClickListener, new ArrayList<Task>());
    }

    public TaskListAdapter(Context context, int rowLayout, OnListItemClickListener onListItemClickListener, List<Task> data) {
        super(rowLayout);
        this.context = context;
        this.mOnListItemClickListener = onListItemClickListener;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowTaskListItemLayoutBinding> holder,
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
//
//    public void updateUnreadIndicator(DataBoundViewHolder<RowTaskListItemLayoutBinding> holder, Task item) {
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
        Drawable drw = (Drawable) ContextCompat.getDrawable(imgTaskPriorityIcon.getContext(), R.drawable.indicator_status_circle);
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
        Drawable drw = (Drawable) ContextCompat.getDrawable(imgTaskStatusIcon.getContext(), R.drawable.indicator_status_circle);
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

    public Task getTask(int position) {
        if (position < 0)
            return null;

        if (position >= this.data.size())
            return null;

        return (Task) this.data.get(position);
    }

    public void addAll(List<Task> data) {
        if (data == null)
            return;

        this.data.addAll(data);
    }

    public void replaceAll(List<Task> data) {
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
