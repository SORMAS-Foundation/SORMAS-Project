package de.symeda.sormas.app.component.menu;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Orson on 25/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ILandingPageContext {
    Context getAppContext();
    ArrayList<LandingPageMenuItem> getMenuData();

}
