package de.symeda.sormas.app.epidata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;

/**
 * Created by Mate Strysewske on 09.03.2017.
 */

public class EpiDataTravelsListArrayAdapter extends ArrayAdapter<EpiDataTravel> {

    private static final String TAG = EpiDataTravelsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public EpiDataTravelsListArrayAdapter(Context context, int resource) {
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

        EpiDataTravel travel = getItem(position);

        StringBuilder periodString = new StringBuilder();
        if (travel.getTravelDateFrom() != null) {
            periodString.append(DateHelper.formatShortDate(travel.getTravelDateFrom()));
            periodString.append(" - ");
        }

        if (travel.getTravelDateTo() != null) {
            periodString.append(DateHelper.formatShortDate(travel.getTravelDateTo()));
        }

        TextView period = (TextView) convertView.findViewById(R.id.travel_period_li);
        period.setText(periodString.toString());

        if (travel.getTravelType() != null) {
            TextView type = (TextView) convertView.findViewById(R.id.travel_type_li);
            type.setText(travel.getTravelType().toString());
        }

        TextView destination = (TextView) convertView.findViewById(R.id.travel_destination_li);
        destination.setText(travel.getTravelDestination());

        return convertView;
    }

}
