package de.symeda.sormas.app.component.menu;

import android.widget.AdapterView;

/**
 * Created by Orson on 26/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface OnNotificationCountChangingListener {

    int onNotificationCountChanging(AdapterView<?> parent, LandingPageMenuItem menuItem, int position);
}
