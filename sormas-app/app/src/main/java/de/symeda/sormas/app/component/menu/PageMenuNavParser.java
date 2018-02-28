package de.symeda.sormas.app.component.menu;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 25/12/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class PageMenuNavParser implements IMenuParser {
    private static final String ns = null;
    private Context context;

    private final String TAG_NAME_MENUS;
    private final String TAG_ATTR_MENUS_NAME;
    private final String TAG_ATTR_MENUS_TITLE;
    private final String TAG_NAME_MENU_ENTRY;
    private final String TAG_NAME_KEY;
    private final String TAG_NAME_TITLE;
    private final String TAG_NAME_DESCRIPTION;
    private final String TAG_ATTR_NAME_ICON_VIEW_TYPE;

    public PageMenuNavParser(Context context) {
        this.context = context;

        TAG_NAME_MENUS = this.context.getResources().getString(R.string.data_tag_menu_menus);
        TAG_ATTR_MENUS_NAME = this.context.getResources().getString(R.string.data_tag_menu_menus_name);
        TAG_ATTR_MENUS_TITLE = this.context.getResources().getString(R.string.data_tag_menu_menus_title);
        TAG_NAME_MENU_ENTRY = this.context.getResources().getString(R.string.data_tag_menu_entry);
        TAG_NAME_KEY = this.context.getResources().getString(R.string.data_tag_menu_key);
        TAG_NAME_TITLE = this.context.getResources().getString(R.string.data_tag_menu_title);
        TAG_NAME_DESCRIPTION = this.context.getResources().getString(R.string.data_tag_menu_description);
        TAG_ATTR_NAME_ICON_VIEW_TYPE = this.context.getResources().getString(R.string.data_tag_menu_icon_attr_view_type);
    }

    @Override
    public LandingPageMenu parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readMenus(parser);
        } finally {
            in.close();
        }
    }

    private LandingPageMenu readMenus(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_MENUS);
        String menuName = readAttribute(parser, TAG_ATTR_MENUS_NAME);
        String menuTitle = readAttribute(parser, TAG_ATTR_MENUS_TITLE);

        LandingPageMenu menu = new LandingPageMenu(menuName, menuTitle);
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            //Start by looking for the menu tag
            if (name.equals(TAG_NAME_MENU_ENTRY)) {
                menu.addMenuItem(readMenuItem(parser));
            } else {
                skip(parser);
            }
        }

        return menu;
    }

    // Parses the contents of a menu. If it encounters a title, description, and icon, hands
    // them off to their respective "read" methods for processing. Otherwise, skip the tag.
    private LandingPageMenuItem readMenuItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_MENU_ENTRY);
        int key = -1;
        String title = null;
        String description = null;
        LandingPageMenuItemIcon icon = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(TAG_NAME_KEY)) {
                key = readKey(parser);
            } else if (name.equals(TAG_NAME_TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(TAG_NAME_DESCRIPTION)) {
                description = readDescription(parser);
            } else {
                skip(parser);
            }
        }

        return new LandingPageMenuItem(key, title, description, icon, false);
    }

    private int readKey(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_KEY);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TAG_NAME_KEY);
        return Integer.valueOf(result);
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_TITLE);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TAG_NAME_TITLE);
        return result;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_DESCRIPTION);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TAG_NAME_DESCRIPTION);
        return result;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }

    private String readAttribute(XmlPullParser parser, String attrName) throws IOException, XmlPullParserException {
        String result = parser.getAttributeValue(null, attrName);

        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
