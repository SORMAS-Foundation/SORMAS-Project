package de.symeda.sormas.app.symptom;

import java.util.List;

import de.symeda.sormas.app.backend.symptoms.Symptoms;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ISymptomValueLoader {
    List<Symptom> unloaded();
    List<Symptom> loadState(Symptoms record);
}
