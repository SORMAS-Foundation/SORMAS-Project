package de.symeda.sormas.app.caze;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
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

        TextView head1 = (TextView) convertView.findViewById(R.id.cli_head1);
        Case caze = values.get(position);

        Log.v(TAG, "caze=" + caze);
        Log.v(TAG, "person=" + caze.getPerson().getUuid());

        Person person = caze.getPerson();
        head1.setText(person.toString());

        TextView sub1 = (TextView) convertView.findViewById(R.id.cli_sub1);
        sub1.setText(values.get(position).getDisease().toString());


        return convertView;
    }
}


