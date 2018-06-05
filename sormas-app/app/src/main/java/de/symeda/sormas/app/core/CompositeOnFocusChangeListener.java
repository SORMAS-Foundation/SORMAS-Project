package de.symeda.sormas.app.core;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orson on 05/11/2017.
 */
public class CompositeOnFocusChangeListener implements View.OnFocusChangeListener {
    private List<View.OnFocusChangeListener> registeredListeners = new ArrayList<View.OnFocusChangeListener>();

    public void registerListener(View.OnFocusChangeListener listener) {
        registeredListeners.add(listener);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        for (View.OnFocusChangeListener listener : registeredListeners) {
            listener.onFocusChange(v, hasFocus);
        }
    }
}
