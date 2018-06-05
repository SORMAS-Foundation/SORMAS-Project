package de.symeda.sormas.app.component.menu;

import java.util.List;

/**
 * Created by Orson on 25/12/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IPageMenuAdapter {

    void initialize(List<LandingPageMenuItem> data, int cellLayout,
                    int counterBackgroundColor, int counterBackgroundActiveColor,
                    int iconColor, int iconActiveColor, int positionColor, int positionActiveColor, int titleColor, int titleActiveColor);
}
