package de.symeda.sormas.app.shared;

import java.util.Date;

import de.symeda.sormas.app.component.controls.TeboDatePicker;

/**
 * Created by Orson on 21/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface OnDateOfDeathChangeListener {
    void onChange(TeboDatePicker view, Date value);
}
