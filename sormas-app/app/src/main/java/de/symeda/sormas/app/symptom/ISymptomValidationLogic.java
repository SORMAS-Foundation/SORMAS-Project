package de.symeda.sormas.app.symptom;

import java.util.List;

/**
 * Created by Orson on 22/05/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface ISymptomValidationLogic {

    boolean validate(Symptom s, List<Symptom> list);
}
