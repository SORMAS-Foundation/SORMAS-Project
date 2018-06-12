package de.symeda.sormas.app.event.read;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundAdapter;
import de.symeda.sormas.app.core.adapter.databinding.DataBoundViewHolder;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.RowReadEventTaskListItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 26/12/2017.
 */

public class EventReadTaskListAdapter  extends DataBoundAdapter<RowReadEventTaskListItemLayoutBinding> {

    private static final String TAG = EventReadTaskListAdapter.class.getSimpleName();

    private final Context context;
    private List<Task> data = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener;
    //private ActionCallback mActionCallback;

    private LayerDrawable backgroundRowItem;
    private Drawable unreadListItemIndicator;

    public EventReadTaskListAdapter(Context context, int rowLayout, OnListItemClickListener onListItemClickListener) {
        this(context, rowLayout, onListItemClickListener, new ArrayList<Task>());
    }

    public EventReadTaskListAdapter(Context context, int rowLayout, OnListItemClickListener onListItemClickListener, List<Task> data) {
        super(rowLayout);
        this.context = context;
        this.mOnListItemClickListener = onListItemClickListener;

        if (data == null)
            this.data = new ArrayList<>();
        else
            this.data = new ArrayList<>(data);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<RowReadEventTaskListItemLayoutBinding> holder,
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

        updateUnreadIndicator(holder, record);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateUnreadIndicator(DataBoundViewHolder<RowReadEventTaskListItemLayoutBinding> holder, Task item) {
        backgroundRowItem = (LayerDrawable) ContextCompat.getDrawable(holder.context, R.drawable.background_list_activity_row);
        unreadListItemIndicator = backgroundRowItem.findDrawableByLayerId(R.id.unreadListItemIndicator);

        if (item != null) {
            if (item.isUnreadOrChildUnread()) {
                unreadListItemIndicator.setTint(holder.context.getResources().getColor(R.color.unreadIcon));
            } else {
                unreadListItemIndicator.setTint(holder.context.getResources().getColor(android.R.color.transparent));
            }
        }
    }



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