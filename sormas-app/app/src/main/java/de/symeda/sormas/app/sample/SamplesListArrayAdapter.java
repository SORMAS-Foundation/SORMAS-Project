package de.symeda.sormas.app.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.SpecimenCondition;
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
    private Sample sample;
    private View convertView;

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

        this.convertView = convertView;

        sample = (Sample) getItem(position);

        TextView dateTime = (TextView) convertView.findViewById(R.id.sample_date_time_li);
        dateTime.setText(DateHelper.formatDate(sample.getSampleDateTime()));

        TextView disease = (TextView) convertView.findViewById(R.id.sample_disease_li);
        disease.setText(sample.getAssociatedCase().getDisease().toShortString() +
                (sample.getAssociatedCase().getDisease() == Disease.OTHER ? " (" + sample.getAssociatedCase().getDiseaseDetails() + ")" : ""));

        TextView shipmentStatus = (TextView) convertView.findViewById(R.id.sample_shipment_status_li);
        if (sample.getReferredTo() != null) {
            shipmentStatus.setText(R.string.sample_referred);
        } else if (sample.isReceived()) {
            shipmentStatus.setText(R.string.sample_received);
        } else if (sample.isShipped()) {
            shipmentStatus.setText(R.string.sample_shipped);
        } else {
            shipmentStatus.setText(R.string.sample_not_shipped);
        }

        TextView casePerson = (TextView) convertView.findViewById(R.id.sample_case_person_li);
        casePerson.setText(sample.getAssociatedCase().getPerson().toString());

        TextView description = (TextView) convertView.findViewById(R.id.sample_description_li);
        StringBuilder sb = new StringBuilder();
        sb.append(sample.getSampleMaterial() + "\n");
        sb.append(sample.getLab().toString());
        description.setText(sb.toString());

        TextView testResult = (TextView) convertView.findViewById(R.id.sample_test_result_li);
        SampleTest mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(sample);

        if (sample.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
            testResult.setText(getContext().getResources().getText(R.string.inadequate_specimen_cond));
        } else {
            if (mostRecentTest != null) {
                testResult.setText(mostRecentTest.getTestResult().toString());
            } else {
                testResult.setText(getContext().getResources().getText(R.string.no_recent_test));
            }
        }

        ImageView synchronizedIcon = (ImageView) convertView.findViewById(R.id.sample_synchronized_li);
        if (sample.isModifiedOrChildModified()) {
            synchronizedIcon.setVisibility(View.VISIBLE);
            synchronizedIcon.setImageResource(R.drawable.ic_cached_black_18dp);
        } else {
            synchronizedIcon.setVisibility(View.GONE);
        }

        updateUnreadIndicator();

        return convertView;
    }

    public void updateUnreadIndicator() {
        if (sample != null && convertView != null) {
            LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.samples_list_item_layout);
            if (sample.isUnreadOrChildUnread()) {
                itemLayout.setBackgroundResource(R.color.bgColorUnread);
            } else {
                itemLayout.setBackground(null);
            }
        }
    }

}
