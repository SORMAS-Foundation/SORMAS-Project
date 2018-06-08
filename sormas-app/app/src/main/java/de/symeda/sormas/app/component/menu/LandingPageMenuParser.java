package de.symeda.sormas.app.component.menu;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Orson on 25/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageMenuParser implements IMenuParser {

    private static final String TAG = LandingPageMenuParser.class.getSimpleName();

    private static final String NS = null;
    private Context context;

    private final String TAG_NAME_MENUS = "menus";
    private final String TAG_ATTR_MENUS_NAME = "name";
    private final String TAG_ATTR_MENUS_TITLE = "title";
    private final String TAG_NAME_MENU_ENTRY = "menu";
    private final String TAG_NAME_KEY = "key";
    private final String TAG_NAME_TITLE = "title";
    private final String TAG_NAME_DESCRIPTION = "description";
    private final String TAG_NAME_ICON = "icon";
    private final String TAG_ATTR_NAME_ICON_SKIP = "skip";
    private final String TAG_ATTR_NAME_ICON_VIEW_TYPE = "def-viewType";

    private LandingPageMenu mMenus;

    public LandingPageMenuParser(Context context) {
        this.context = context;
    }


    @Override
    public LandingPageMenu parse(XmlResourceParser parser) {
        try {
            while(parser.next() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String menuValue = parser.getName();

                    if (menuValue.equals(TAG_NAME_MENUS)) {
                        mMenus = readMenusTag(parser);
                    }
                }
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            parser.close();
        }

        return mMenus;
    }

    private LandingPageMenu readMenusTag(XmlResourceParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_MENUS);
        String menuName = parser.getAttributeValue(NS, TAG_ATTR_MENUS_NAME);
        String menuTitle = parser.getAttributeValue(NS, TAG_ATTR_MENUS_TITLE);

        mMenus = new LandingPageMenu(menuName, menuTitle);

        while (parser.next() != XmlResourceParser.END_TAG) {
            if (parser.getEventType() != XmlResourceParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(TAG_NAME_MENU_ENTRY)) {
                mMenus.addMenuItem(readMenuTag(parser));
            } else {
                skipTag(parser);
            }
        }

        return mMenus;
    }

    private LandingPageMenuItem readMenuTag(XmlResourceParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_MENU_ENTRY);
        int key = -1;
        String title = null;
        String description = null;
        LandingPageMenuItemIcon icon = null;

        while (parser.next() != XmlResourceParser.END_TAG) {
            if (parser.getEventType() != XmlResourceParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(TAG_NAME_KEY)) {
                key = readKeyTag(parser);
            } else if (name.equals(TAG_NAME_TITLE)) {
                title = readTitleTag(parser);
            } else if (name.equals(TAG_NAME_DESCRIPTION)) {
                description = readDescriptionTag(parser);
            } else if (name.equals(TAG_NAME_ICON)) {
                icon = readIconTag(parser);
            } else {
                skipTag(parser);
            }
        }

        return new LandingPageMenuItem(key, title, description, icon, false);
    }

    private int readKeyTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_KEY);
        String result = parser.nextText();
        parser.require(XmlResourceParser.END_TAG, NS, TAG_NAME_KEY);
        return Integer.valueOf(result);
    }

    private String readTitleTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_TITLE);
        String result = parser.nextText();
        parser.require(XmlResourceParser.END_TAG, NS, TAG_NAME_TITLE);
        return result;
    }

    private String readDescriptionTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_DESCRIPTION);
        String result = parser.nextText();
        parser.require(XmlResourceParser.END_TAG, NS, TAG_NAME_DESCRIPTION);
        return result;
    }

    private LandingPageMenuItemIcon readIconTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_ICON);

        LandingPageMenuItemIcon itemIcon = null;
        boolean skip = parser.getAttributeBooleanValue(NS, TAG_ATTR_NAME_ICON_SKIP, false);
        String defType = parser.getAttributeValue(NS, TAG_ATTR_NAME_ICON_VIEW_TYPE);
        String iconName = parser.nextText();

        skip = (skip || (defType == null || defType.isEmpty()) || (iconName == null || iconName.isEmpty()));

        if (skip)
            return itemIcon;

        parser.require(XmlResourceParser.END_TAG, NS, TAG_NAME_ICON);
        itemIcon = new LandingPageMenuItemIcon(iconName, defType);

        return itemIcon;
    }

    private void skipTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlResourceParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlResourceParser.END_TAG:
                    depth--;
                    break;
                case XmlResourceParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
