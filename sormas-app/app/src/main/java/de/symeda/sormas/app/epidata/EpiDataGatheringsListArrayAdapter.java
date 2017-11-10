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
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;

/**
 * Created by Mate Strysewske on 09.03.2017.
 */

public class EpiDataGatheringsListArrayAdapter extends ArrayAdapter<EpiDataGathering> {

    private static final String TAG = EpiDataGatheringsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public EpiDataGatheringsListArrayAdapter(Context context, int resource) {
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

        EpiDataGathering gathering = getItem(position);

        TextView date = (TextView) convertView.findViewById(R.id.gathering_date_li);
        date.setText(gathering.getGatheringDate() != null ? DateHelper.formatShortDate(gathering.getGatheringDate()) : context.getResources().getString(R.string.caption_unknown));

        TextView lga = (TextView) convertView.findViewById(R.id.gathering_lga_li);
        if (gathering.getGatheringAddress() != null && gathering.getGatheringAddress().getDistrict() != null)
            lga.setText(gathering.getGatheringAddress().getDistrict().toString());

        TextView description = (TextView) convertView.findViewById(R.id.gathering_description_li);
        description.setText(gathering.getDescription());

        return convertView;
    }

}
