package de.symeda.sormas.app.component.tagview;

import android.graphics.Color;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 03/01/2018.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class Constants {

    //use dp and sp, not px

    //----------------- separator TagView-----------------//
    public static final float DEFAULT_LINE_MARGIN = 5;
    public static final float DEFAULT_TAG_MARGIN = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;

    public static final float LAYOUT_WIDTH_OFFSET = 2;

    //----------------- separator Tag Item-----------------//
    public static final float DEFAULT_TAG_TEXT_SIZE = 14f;
    public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 16f;
    public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 1f;
    public static final float DEFAULT_TAG_RADIUS = 100;
    public static final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#e1e7f0");
    public static final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#374b5a");
    public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#a7b0b7");
    public static final String DEFAULT_TAG_DELETE_ICON = "×";
    public static final boolean DEFAULT_TAG_IS_DELETABLE = false;
    public static final int DEFAULT_TAG_ROW_LAYOUT = R.layout.tagview_item;


    private Constants() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }
}
