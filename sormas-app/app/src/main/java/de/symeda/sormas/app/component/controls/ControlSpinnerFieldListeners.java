/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.AdapterView;

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
