package de.symeda.sormas.app.caze;

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
import de.symeda.sormas.app.backend.caze.Case;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CasesListArrayAdapter extends ArrayAdapter<Case> {

    private static final String TAG = CasesListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public CasesListArrayAdapter(Context context, int resource) {
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

        Case caze = (Case)getItem(position);

        TextView uuid = (TextView) convertView.findViewById(R.id.cli_uuid);
        uuid.setText(DataHelper.getShortUuid(caze.getUuid()));

        TextView disease = (TextView) convertView.findViewById(R.id.cli_disease);
        if(caze.getDisease() != null) {
            String diseaseName = caze.getDisease().getName();
            disease.setText(Disease.valueOf(diseaseName).toShortString());
        } else {
            disease.setText(null);
        }

        TextView reportDate = (TextView) convertView.findViewById(R.id.cli_report_date);
        reportDate.setText(DateHelper.formatDDMMYYYY(caze.getReportDate()));

        TextView caseStatus = (TextView) convertView.findViewById(R.id.cli_case_satus);
        caseStatus.setText(caze.getCaseClassification()!=null?caze.getCaseClassification().toString():null);

        TextView person = (TextView) convertView.findViewById(R.id.cli_person);
        person.setText(caze.getPerson().toString());

        TextView facility = (TextView) convertView.findViewById(R.id.cli_facility);
        facility.setText(caze.getHealthFacility()!=null?caze.getHealthFacility().toString():null);

        TextView reporter = (TextView) convertView.findViewById(R.id.cli_reporter);
        reporter.setText(caze.getReportingUser()!=null?caze.getReportingUser().toString():null);

        return convertView;
    }
}