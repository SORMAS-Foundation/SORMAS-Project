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

package de.symeda.sormas.app.task.list;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.BindingPagedListAdapter;
import de.symeda.sormas.app.core.adapter.databinding.BindingViewHolder;
import de.symeda.sormas.app.databinding.RowTaskListItemLayoutBinding;
import de.symeda.sormas.app.util.ResourceUtils;

public class TaskListAdapter extends BindingPagedListAdapter<Task, RowTaskListItemLayoutBinding> {

	public TaskListAdapter() {
		super(R.layout.row_task_list_item_layout);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		if (getItemViewType(position) == TYPE_ITEM) {
			BindingViewHolder<Task, RowTaskListItemLayoutBinding> pagedHolder = (BindingViewHolder) holder;
			Task item = getItem(position);

			pagedHolder.setOnListItemClickListener(this.mOnListItemClickListener);

			indicatePriority(pagedHolder.binding.imgPriorityStatusIcon, item);
			indicateStatus(pagedHolder.binding.imgTaskStatusIcon, item);

			if (item.isModifiedOrChildModified()) {
				pagedHolder.binding.imgSyncIcon.setVisibility(View.VISIBLE);
				pagedHolder.binding.imgSyncIcon.setImageResource(R.drawable.ic_sync_blue_24dp);
			} else {
				pagedHolder.binding.imgSyncIcon.setVisibility(View.GONE);
			}

			// TODO #704
			//updateUnreadIndicator(holder, record);
		}
	}

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

	public void indicatePriority(@NonNull ImageView imgTaskPriorityIcon, @NonNull Task task) {
		Drawable drw = (Drawable) ResourceUtils.getDrawable(imgTaskPriorityIcon.getContext(), R.drawable.indicator_status_circle);
		assert drw != null;
		if (task.getPriority() == TaskPriority.HIGH) {
			drw.setColorFilter(ResourceUtils.getColor(imgTaskPriorityIcon.getContext(), R.color.indicatorTaskPriorityHigh), PorterDuff.Mode.SRC_OVER);
		} else if (task.getPriority() == TaskPriority.LOW) {
			drw.setColorFilter(ResourceUtils.getColor(imgTaskPriorityIcon.getContext(), R.color.indicatorTaskPriorityLow), PorterDuff.Mode.SRC_OVER);
		} else if (task.getPriority() == TaskPriority.NORMAL) {
			drw.setColorFilter(ResourceUtils.getColor(imgTaskPriorityIcon.getContext(), R.color.indicatorTaskPriorityNormal), PorterDuff.Mode.SRC_OVER);
		}

		imgTaskPriorityIcon.setBackground(drw);
	}

	public void indicateStatus(@NonNull ImageView imgTaskStatusIcon, @NonNull Task task) {
		Drawable drw = (Drawable) ResourceUtils.getDrawable(imgTaskStatusIcon.getContext(), R.drawable.indicator_status_circle);
		assert drw != null;
		if (task.getTaskStatus() == TaskStatus.PENDING) {
			drw.setColorFilter(ResourceUtils.getColor(imgTaskStatusIcon.getContext(), R.color.indicatorTaskPending), PorterDuff.Mode.SRC_OVER);
		} else if (task.getTaskStatus() == TaskStatus.DONE) {
			drw.setColorFilter(ResourceUtils.getColor(imgTaskStatusIcon.getContext(), R.color.indicatorTaskDone), PorterDuff.Mode.SRC_OVER);
		} else if (task.getTaskStatus() == TaskStatus.REMOVED) {
			drw.setColorFilter(ResourceUtils.getColor(imgTaskStatusIcon.getContext(), R.color.indicatorTaskRemoved), PorterDuff.Mode.SRC_OVER);
		} else if (task.getTaskStatus() == TaskStatus.NOT_EXECUTABLE) {
			drw.setColorFilter(ResourceUtils.getColor(imgTaskStatusIcon.getContext(), R.color.indicatorTaskNotExecutable), PorterDuff.Mode.SRC_OVER);
		}

		imgTaskStatusIcon.setBackground(drw);
	}
}
