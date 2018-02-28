package de.symeda.sormas.app.component.menu;

import java.util.ArrayList;

/**
 * Created by Orson on 25/12/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IPageMenuAdapter {

    void initialize(ArrayList<LandingPageMenuItem> data, int cellLayout, int positionColor, int positionActiveColor, int titleColor, int titleActiveColor);
}
