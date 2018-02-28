package de.symeda.sormas.app.symptom;

import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.app.backend.symptoms.Symptoms;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SymptomFacade {


    public static List<Symptom> loadState(List<Symptom> list, Symptoms record) {

        for (Symptom symptom : list) {
            if (symptom.equals(Symptom.FEVER)) {
                symptom.setState(SymptomState.YES); // record.getFever());
                symptom.setDetail("");
            }
        }

        return list;
    }
}
