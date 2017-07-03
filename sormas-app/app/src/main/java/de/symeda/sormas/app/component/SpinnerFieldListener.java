package de.symeda.sormas.app.component;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mate Strysewske on 09.12.2016.
 */
public class SpinnerFieldListener implements AdapterView.OnItemSelectedListener {
    private List<AdapterView.OnItemSelectedListener> registeredListeners = new ArrayList<>();

    public void registerListener(AdapterView.OnItemSelectedListener listener) {
        registeredListeners.add(listener);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        for(AdapterView.OnItemSelectedListener listener : registeredListeners) {
            listener.onItemSelected(parent, view, position, id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        for(AdapterView.OnItemSelectedListener listener : registeredListeners) {
            listener.onNothingSelected(parent);
        }
    }
}
