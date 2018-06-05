package de.symeda.sormas.app.component.menu;

import android.content.res.XmlResourceParser;

/**
 * Created by Orson on 25/12/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IMenuParser {
    LandingPageMenu parse(XmlResourceParser parser);
}
