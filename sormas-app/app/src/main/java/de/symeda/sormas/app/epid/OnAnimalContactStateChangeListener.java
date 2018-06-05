package de.symeda.sormas.app.epid;

import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Created by Orson on 20/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface OnAnimalContactStateChangeListener {

    void onChange(AnimalContact animalContact, YesNoUnknown state);
}
