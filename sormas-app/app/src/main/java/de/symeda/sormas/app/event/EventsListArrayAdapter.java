package de.symeda.sormas.app.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;

public class EventsListArrayAdapter extends ArrayAdapter<Event> {

    private static final String TAG = EventsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public EventsListArrayAdapter(Context context, int resource) {
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

        Event event = (Event)getItem(position);

        TextView uuid = (TextView) convertView.findViewById(R.id.eli_uuid);
        uuid.setText(DataHelper.getShortUuid(event.getUuid()));

        TextView disease = (TextView) convertView.findViewById(R.id.eli_disease);
        if(event.getDisease() != null) {
            String diseaseName = event.getDisease().getName();
            disease.setText(Disease.valueOf(diseaseName).toShortString());
        } else {
            disease.setText(null);
        }
//        TextView eventStatus = (TextView) convertView.findViewById(R.id.eli_eventStatus);
//        eventStatus.setText(event.getEventStatus()!=null?event.getEventStatus().toString():null);

        TextView summary = (TextView) convertView.findViewById(R.id.eli_summary);
        StringBuilder sb = new StringBuilder();
        sb.append(event.getEventType()!=null?event.getEventType().toString()+ " ":"");
        sb.append(event.getEventDate()!=null?"(" + DateHelper.formatDDMMYYYY(event.getEventDate()) + ")":"");
        summary.setText(sb.toString());

        TextView description = (TextView) convertView.findViewById(R.id.eli_description);
        description.setText(event.getEventDesc()!=null?event.getEventDesc().toString():null);

        return convertView;
    }
}