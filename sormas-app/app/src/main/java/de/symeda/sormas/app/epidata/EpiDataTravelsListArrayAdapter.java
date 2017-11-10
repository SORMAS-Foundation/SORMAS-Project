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

        String travelPeriod = "";
        if (travel.getTravelDateFrom() == null && travel.getTravelDateTo() == null) {
            travelPeriod = getContext().getResources().getString(R.string.caption_unknown);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(travel.getTravelDateFrom() != null ? DateHelper.formatShortDate(travel.getTravelDateFrom()) : "?");
            sb.append(" - ");
            sb.append(travel.getTravelDateTo() != null ? DateHelper.formatShortDate(travel.getTravelDateTo()) : "?");
            travelPeriod = sb.toString();
        }

        TextView period = (TextView) convertView.findViewById(R.id.travel_period_li);
        period.setText(travelPeriod);

        if (travel.getTravelType() != null) {
            TextView type = (TextView) convertView.findViewById(R.id.travel_type_li);
            type.setText(travel.getTravelType().toString());
        }

        TextView destination = (TextView) convertView.findViewById(R.id.travel_destination_li);
        destination.setText(travel.getTravelDestination());

        return convertView;
    }

}
