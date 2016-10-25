package de.symeda.sormas.app.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class TasksListArrayAdapter extends ArrayAdapter<Task> {

    private static final String TAG = TasksListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public TasksListArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, parent, false);
        }

        Task task = (Task)getItem(position);

        TextView uuid = (TextView) convertView.findViewById(R.id.task_uuid_li);
        uuid.setText(DataHelper.getShortUuid(task.getUuid()));

        TextView dueDate = (TextView) convertView.findViewById(R.id.task_dueDate_li);
        dueDate.setText(task.getDueDate().toString());


        return convertView;
    }
}