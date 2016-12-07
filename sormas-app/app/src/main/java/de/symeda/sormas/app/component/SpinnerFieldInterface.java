package de.symeda.sormas.app.component;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import de.symeda.sormas.app.util.Item;

/**
 * Created by Mate Strysewske on 30.11.2016.
 */

public interface SpinnerFieldInterface {

    public int getCount();
    public Object getItemAtPosition(int i);
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener);

}
