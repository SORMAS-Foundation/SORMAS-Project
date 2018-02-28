package de.symeda.sormas.app.component.menu;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Orson on 25/12/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IMenuParser {
    LandingPageMenu parse(InputStream in) throws XmlPullParserException, IOException;
}
