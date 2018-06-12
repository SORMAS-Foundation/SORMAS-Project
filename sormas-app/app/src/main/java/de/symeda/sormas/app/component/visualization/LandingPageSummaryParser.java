package de.symeda.sormas.app.component.visualization;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orson on 26/11/2017.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LandingPageSummaryParser {

    private static final String ns = null;
    private Context context;

    public List<SummaryInfo> parse(Context context, InputStream in) throws XmlPullParserException, IOException {
        try {
            this.context = context;

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readSummary(parser);
        } finally {
            in.close();
        }
    }

    private List<SummaryInfo> readSummary(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<SummaryInfo> list = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "summary");
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            //Start by looking for the menu tag
            if (name.equals("info")) {
                list.add(readInfo(parser));
            } else {
                skip(parser);
            }
        }

        return list;
    }

    // Parses the contents of a menu. If it encounters a title, description, and icon, hands
    // them off to their respective "read" methods for processing. Otherwise, skip the tag.
    private SummaryInfo readInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "info");
        String infoName = null;
        String title = null;
        String description = null;
        LandingSummaryCellLayout cellLayout = null;
        LandingSummaryCellBackground cellBackground = null;
        double layoutWidth = 0;
        int layoutColumnWeight = 0;
        double layoutHeight = 0;
        int columnSpan = 0;
        double padding = 0;
        double layoutMargin = 0;



        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("title")) {

                infoName = readAttribute(parser, "name");
                title = readTitle(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("cell-layout")) {
                cellLayout = readCellLayout(parser);
            } else if (name.equals("background")) {
                cellBackground = readCellBackground(parser);
            } else if (name.equals("layout-width")) {
                layoutWidth = readLayoutWidth(parser);
            } else if (name.equals("layout-column-weight")) {
                layoutColumnWeight = readLayoutColumnWeight(parser);
            } else if (name.equals("layout-height")) {
                layoutHeight = readLayoutHeight(parser);
            } else if (name.equals("column-span")) {
                columnSpan = readColumnSpan(parser);
            } else if (name.equals("padding")) {
                padding = readPadding(parser);
            } else if (name.equals("layout-margin")) {
                layoutMargin = readLayoutMargin(parser);
            } else {
                skip(parser);
            }
        }

        return new SummaryInfo(infoName, title, description, cellLayout, cellBackground, layoutWidth, layoutColumnWeight, layoutHeight, columnSpan, padding, layoutMargin);
        /*

        String name, String title, String description,
                       LandingSummaryCellLayout cellLayout, LandingSummaryCellBackground cellBackground,
                       double layoutWidth, int layoutColumnWeight, double layoutColumnHeight,
                       int columnSpan, double padding, double layoutMargin)

        */
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private LandingSummaryCellLayout readCellLayout(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "cell-layout");
        String defType = readAttribute(parser, "def-viewType");
        String cellLayoutName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "cell-layout");
        return new LandingSummaryCellLayout(cellLayoutName, defType);
    }

    private LandingSummaryCellBackground readCellBackground(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "background");
        String defType = readAttribute(parser, "def-viewType");
        String cellBackground = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "background");
        return new LandingSummaryCellBackground(cellBackground, defType);
    }

    private double readLayoutWidth(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "layout-width");
        double value = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "layout-width");
        return value;
    }

    private int readLayoutColumnWeight(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "layout-column-weight");
        int value = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "layout-column-weight");
        return value;
    }

    private double readLayoutHeight(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "layout-height");
        double value = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "layout-height");
        return value;
    }

    private int readColumnSpan(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "column-span");
        int value = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "column-span");
        return value;
    }

    private double readPadding(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "padding");
        double value = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "padding");
        return value;
    }

    private double readLayoutMargin(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "layout-margin");
        double value = Double.parseDouble(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "layout-margin");
        return value;
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
