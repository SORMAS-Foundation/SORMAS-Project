package de.symeda.sormas.app.visit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.visit.Visit;

public class VisitsListArrayAdapter extends ArrayAdapter<Visit> {

    private static final String TAG = VisitsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public VisitsListArrayAdapter(Context context, int resource) {
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

        Visit visit = getItem(position);

        TextView visitDate = (TextView) convertView.findViewById(R.id.visit_date_li);
        visitDate.setText(DateHelper.formatDate(visit.getVisitDateTime()));

        TextView visitTime = (TextView) convertView.findViewById(R.id.visit_time_li);
        visitTime.setText(DateHelper.formatTime(visit.getVisitDateTime()));

        TextView visitStatus = (TextView) convertView.findViewById(R.id.visit_visitStatus_li);
        visitStatus.setText(visit.getVisitStatus()!=null?visit.getVisitStatus().toString():"");

        TextView information = (TextView) convertView.findViewById(R.id.visit_information_li);
        StringBuilder sb = new StringBuilder();
        sb.append(visit.getVisitRemarks()!=null?visit.getVisitRemarks():"");
        if(visit.getSymptoms()!=null) {
            sb.append(visit.getSymptoms().getTemperature()!=null?" | " + visit.getSymptoms().getTemperature()+" °C":"")
              .append(visit.getSymptoms().getSymptomatic()!=null && visit.getSymptoms().getSymptomatic()?" | " + " is symptomatic":" is not symptomatic");

        }
        information.setText(sb.toString());

        return convertView;
    }
}