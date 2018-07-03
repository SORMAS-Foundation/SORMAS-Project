package de.symeda.sormas.app.component.controls;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlSpinnerFieldListeners implements AdapterView.OnItemSelectedListener {

    private List<ValueChangeListener> registeredListeners = new ArrayList<>();
    private Map<ValueChangeListener, AdapterView.OnItemSelectedListener> wrappedListeners = new HashMap<>();

    public void registerListener(final ValueChangeListener listener, final ControlSpinnerField field) {
        registeredListeners.add(listener);
        wrappedListeners.put(listener, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onChange(field);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listener.onChange(field);
            }
        });
    }

    public void unregisterListener(ValueChangeListener listener) {
        registeredListeners.remove(listener);
        wrappedListeners.remove(listener);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        for (ValueChangeListener listener : wrappedListeners.keySet()) {
            wrappedListeners.get(listener).onItemSelected(parent, view, position, id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        for (ValueChangeListener listener : wrappedListeners.keySet()) {
            wrappedListeners.get(listener).onNothingSelected(parent);
        }
    }

}
