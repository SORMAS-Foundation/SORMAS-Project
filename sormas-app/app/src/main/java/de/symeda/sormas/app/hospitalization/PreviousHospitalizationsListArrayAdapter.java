package de.symeda.sormas.app.hospitalization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.YesNoUnknown;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;

public class PreviousHospitalizationsListArrayAdapter extends ArrayAdapter<PreviousHospitalization> {

    private static final String TAG = PreviousHospitalizationsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public PreviousHospitalizationsListArrayAdapter(Context context, int resource) {
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

        PreviousHospitalization previousHospitalization = (PreviousHospitalization)getItem(position);

        StringBuilder sb = new StringBuilder();
        if(previousHospitalization.getAdmissionDate()!=null) {
            sb.append(DateHelper.formatDMY(previousHospitalization.getAdmissionDate()));
            sb.append(" - ");
        }
        if(previousHospitalization.getDischargeDate()!=null) {
            sb.append(DateHelper.formatDMY(previousHospitalization.getDischargeDate()));
        }
        TextView period = (TextView) convertView.findViewById(R.id.previousHospitalization_period_li);
        period.setText(sb.toString());

        TextView isolated = (TextView) convertView.findViewById(R.id.previousHospitalization_isolated_li);
        isolated.setText(YesNoUnknown.YES.equals(previousHospitalization.getIsolated())?"was isolated":null);

        TextView facility = (TextView) convertView.findViewById(R.id.previousHospitalization_healthFacility_li);
        facility.setText(previousHospitalization.getHealthFacility()!=null?previousHospitalization.getHealthFacility().toString():null);

        return convertView;
    }
}