package de.symeda.sormas.app.epidata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.symeda.sormas.api.caze.YesNoUnknown;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;

/**
 * Created by Mate Strysewske on 09.03.2017.
 */

public class EpiDataBurialsListArrayAdapter extends ArrayAdapter<EpiDataBurial> {

    private static final String TAG = EpiDataBurialsListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public EpiDataBurialsListArrayAdapter(Context context, int resource) {
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

        EpiDataBurial burial = getItem(position);

        StringBuilder periodString = new StringBuilder();
        if (burial.getBurialDateFrom() != null) {
            periodString.append(DateHelper.formatDMY(burial.getBurialDateFrom()));
            periodString.append(" - ");
        }

        if (burial.getBurialDateTo() != null) {
            periodString.append(DateHelper.formatDMY(burial.getBurialDateTo()));
        }

        TextView period = (TextView) convertView.findViewById(R.id.burial_period_li);
        period.setText(periodString.toString());

        TextView lga = (TextView) convertView.findViewById(R.id.burial_lga_li);
        if (burial.getBurialAddress() != null && burial.getBurialAddress().getDistrict() != null) {
            lga.setText(burial.getBurialAddress().getDistrict().toString());
        } else {
            lga.setText("");
        }

        TextView person = (TextView) convertView.findViewById(R.id.burial_person_li);
        person.setText(burial.getBurialPersonname());

        TextView illTouched = (TextView) convertView.findViewById(R.id.burial_ill_touched_li);
        StringBuilder illTouchedString = new StringBuilder();
        illTouchedString.append(YesNoUnknown.YES.equals(burial.getBurialIll())?"Person ill / ":"");
        illTouchedString.append(YesNoUnknown.YES.equals(burial.getBurialTouching())?"Body touched":"Body not touched");
        illTouched.setText(illTouchedString.toString());

        return convertView;
    }

}
