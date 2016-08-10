package de.symeda.sormas.app.caze;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CaseListArrayAdapter extends ArrayAdapter<Case> {

    private static final String TAG = CaseListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;
    private final List<Case> values;

    public CaseListArrayAdapter(Context context, int resource, List<Case> values) {
        super(context, resource, values);
        this.context = context;
        this.resource = resource;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, parent, false);
        }

        Case caze = values.get(position);


        TextView uuid = (TextView) convertView.findViewById(R.id.cli_uuid);
        uuid.setText(DataHelper.getShortUuid(caze.getUuid()));

        TextView disease = (TextView) convertView.findViewById(R.id.cli_disease);
        disease.setText(caze.getDisease().toString());

        TextView caseStatus = (TextView) convertView.findViewById(R.id.cli_case_satus);
        caseStatus.setText(caze.getCaseStatus().toString());

        TextView person = (TextView) convertView.findViewById(R.id.cli_person);
        person.setText(caze.getPerson().toString());

        TextView facility = (TextView) convertView.findViewById(R.id.cli_facility);
        if(caze.getHealthFacility()!=null) {
            FacilityDao facilityDao = DatabaseHelper.getFacilityDao();
            Facility fac = facilityDao.queryForId(caze.getHealthFacility().getId());
            facility.setText(fac.toString());
        }

        TextView reporter = (TextView) convertView.findViewById(R.id.cli_reporter);
        reporter.setText(caze.getReportingUser()!=null?caze.getReportingUser().toString():null);

        return convertView;
    }


}


