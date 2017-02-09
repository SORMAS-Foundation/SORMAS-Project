package de.symeda.sormas.app.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SamplesListArrayAdapter extends ArrayAdapter<Sample> {

    private final Context context;
    private int resource;

    public SamplesListArrayAdapter(Context context, int resource) {
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

        Sample sample = (Sample) getItem(position);

        TextView uuid = (TextView) convertView.findViewById(R.id.sample_uuid_li);
        uuid.setText(DataHelper.getShortUuid(sample.getUuid()));

        TextView dateTime = (TextView) convertView.findViewById(R.id.sample_date_time_li);
        dateTime.setText(DateHelper.formatDDMMYYYY(sample.getSampleDateTime()));

        TextView shipmentStatus = (TextView) convertView.findViewById(R.id.sample_shipment_status_li);
        shipmentStatus.setText(sample.getShipmentStatus().toString());

        TextView casePerson = (TextView) convertView.findViewById(R.id.sample_case_person_li);
        casePerson.setText(sample.getAssociatedCase().getPerson().toString());

        TextView description = (TextView) convertView.findViewById(R.id.sample_description_li);
        StringBuilder sb = new StringBuilder();
        sb.append(sample.getAssociatedCase().getDisease()!=null?sample.getAssociatedCase().getDisease().toString() + ", ":"");
        sb.append(sample.getSampleMaterial() + "\n");
        sb.append(sample.getLab().toString());
        description.setText(sb.toString());

        TextView testResult = (TextView) convertView.findViewById(R.id.sample_test_result_li);
        SampleTest mostRecentTest = DatabaseHelper.getSampleTestDao().getMostRecentForSample(sample);

        if (sample.getNoTestPossible()) {
            testResult.setText(getContext().getResources().getText(R.string.no_test_possible));
        } else {
            if (mostRecentTest != null) {
                testResult.setText(mostRecentTest.getTestResult().toString());
            } else {
                testResult.setText(getContext().getResources().getText(R.string.no_recent_test));
            }
        }

        return convertView;
    }

}
