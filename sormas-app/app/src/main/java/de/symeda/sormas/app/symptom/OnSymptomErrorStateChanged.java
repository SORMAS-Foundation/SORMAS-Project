package de.symeda.sormas.app.symptom;

/**
 * Created by Orson on 22/05/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface OnSymptomErrorStateChanged {
    void onChanged(Symptom symptom, boolean errorState);
}
