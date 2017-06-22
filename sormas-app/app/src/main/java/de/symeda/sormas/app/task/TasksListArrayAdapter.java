package de.symeda.sormas.app.task;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.Date;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class TasksListArrayAdapter extends ArrayAdapter<Task> {

    private static final String TAG = TasksListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;
    private Task task;
    private View convertView;

    public TasksListArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, parent, false);
        }
        this.convertView = convertView;

        task = (Task) getItem(position);

        TextView dueDate = (TextView) convertView.findViewById(R.id.task_dueDate_li);
        dueDate.setText(DateHelper.formatDate(task.getDueDate()));

        Integer dueDateColor = null;
        if(task.getDueDate().compareTo(new Date()) <= 0 && !TaskStatus.DONE.equals(task.getTaskStatus())) {
            dueDateColor = R.color.textColorRed;
        }
        setFontStyle(dueDate, task.getTaskStatus(),dueDateColor);

        TextView disease = (TextView) convertView.findViewById(R.id.task_disease_li);
        if (task.getCaze() != null) {
            disease.setText(task.getCaze().getDisease().toShortString());
        } else if (task.getContact() != null) {
            disease.setText(task.getContact().getCaze().getDisease().toShortString());
        } else if (task.getEvent() != null && task.getEvent().getDisease() != null){
            disease.setText(task.getEvent().getDisease().toShortString());
        } else {
            disease.setText("");
        }

        TextView taskStatus = (TextView) convertView.findViewById(R.id.task_taskStatus_li);
        taskStatus.setText(DataHelper.toStringNullable(task.getTaskStatus()));
        setFontStyle(taskStatus, task.getTaskStatus());

        TextView taskType = (TextView) convertView.findViewById(R.id.task_taskType_li);
        taskType.setText(DataHelper.toStringNullable(task.getTaskType()));
        setFontStyle(taskType, task.getTaskStatus());

        TextView taskInfo = (TextView) convertView.findViewById(R.id.task_name_or_information_li);
        if (task.getCaze() != null) {
            taskInfo.setText(task.getCaze().getPerson().getFirstName() + " " + task.getCaze().getPerson().getLastName().toUpperCase() +
                    " (" + DataHelper.getShortUuid(task.getCaze().getUuid()) + ")");
        } else if (task.getContact() != null) {
            taskInfo.setText(task.getContact().getPerson().getFirstName() + " " + task.getContact().getPerson().getLastName().toUpperCase() +
                    " (" + DataHelper.getShortUuid(task.getContact().getUuid()) + ")");
        } else if (task.getEvent() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(task.getEvent().getEventType());
            sb.append(", " + DateHelper.formatDate(task.getEvent().getEventDate()));
            if (task.getEvent().getEventLocation().getCity() != null && !task.getEvent().getEventLocation().getCity().isEmpty()) {
                sb.append(", " + task.getEvent().getEventLocation().getCity());
            }
            sb.append(" (" + DataHelper.getShortUuid(task.getEvent().getUuid()) + ")");
            sb.append(", " + task.getEvent().getEventDesc().substring(0, 15) + (task.getEvent().getEventDesc().length() > 15 ? "..." : ""));
            taskInfo.setText(sb.toString());
        }

        TextView creatorComment = (TextView) convertView.findViewById(R.id.task_creatorComment_li);
        creatorComment.setText(task.getCreatorComment());
        setFontStyle(creatorComment, task.getTaskStatus());

        View priority = (View) convertView.findViewById(R.id.task_priority_li);
        if (task.getPriority() != null) {
            Integer priorityColor = null;
            ;
            switch (task.getPriority()) {
                case HIGH:
                    priorityColor = R.color.textColorRed;
                    break;
                case NORMAL:
                    priorityColor = R.color.colorPrimaryDark;
                    break;
                case LOW:
                    priorityColor = R.color.colorInvisible;
                    break;
            }

            if (priorityColor != null) {
                priority.setBackgroundColor(getContext().getResources().getColor(priorityColor));
            }
        }

        ImageView synchronizedIcon = (ImageView) convertView.findViewById(R.id.task_synchronized_li);
        if (task.isModifiedOrChildModified()) {
            synchronizedIcon.setImageResource(R.drawable.ic_cached_black_18dp);
        } else {
            synchronizedIcon.setImageResource(R.drawable.ic_done_all_black_18dp);
        }

        updateUnreadIndicator();

        return convertView;
    }

    public void updateUnreadIndicator() {
        if (task != null && convertView != null) {
            LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.task_item_layout);
            if (task.isUnreadOrChildUnread()) {
                itemLayout.setBackgroundResource(R.color.bgColorUnread);
            } else {
                itemLayout.setBackground(null);
            }
        }
    }

    private void setFontStyle(TextView textView, TaskStatus taskStatus) {
        setFontStyle(textView,taskStatus,null);
    }

    private void setFontStyle(TextView textView, TaskStatus taskStatus, Integer textColorGiven) {
        int fontface = Typeface.NORMAL;
        int textColor = R.color.textColorPrimaryDark;
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        if(TaskStatus.REMOVED.equals(taskStatus)) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textColor = R.color.textColorPrimaryLight;
        }

        if(textColorGiven != null) {
            textColor = textColorGiven;
        }

        textView.setTypeface(null, fontface);
        textView.setTextColor(getContext().getResources().getColor(textColor));
    }
}