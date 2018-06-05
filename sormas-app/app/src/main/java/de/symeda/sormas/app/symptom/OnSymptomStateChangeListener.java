package de.symeda.sormas.app.symptom;

import de.symeda.sormas.api.symptoms.SymptomState;

/**
 * Created by Orson on 14/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface OnSymptomStateChangeListener {

    void onChange(Symptom symptom, SymptomState state);
}
