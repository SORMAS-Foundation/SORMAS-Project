package de.symeda.sormas.app.task.landing;

import android.content.Context;
import android.util.Xml;

import de.symeda.sormas.app.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orson on 01/12/2017.
 */

public class TaskPrioritySummaryParser {

    private static final String ns = null;
    private Context context;

    private final String TAG_NAME_SUMMARY;
    private final String TAG_NAME_ENTRY;
    private final String TAG_NAME_KEY;
    private final String TAG_NAME_LABEL;
    private final String TAG_NAME_VALUE;

    public TaskPrioritySummaryParser(Context context) {
        this.context = context;

        TAG_NAME_SUMMARY = this.context.getResources().getString(R.string.data_task_priority_summary);
        TAG_NAME_ENTRY = this.context.getResources().getString(R.string.data_tag_task_priority_entry);
        TAG_NAME_KEY = this.context.getResources().getString(R.string.data_tag_task_priority_key);
        TAG_NAME_LABEL = this.context.getResources().getString(R.string.data_tag_task_priority_label);
        TAG_NAME_VALUE = this.context.getResources().getString(R.string.data_tag_task_priority_value);
    }

    public List<TaskPrioritySummaryEntry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readSummary(parser);
        } finally {
            in.close();
        }
    }

    private List<TaskPrioritySummaryEntry> readSummary(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<TaskPrioritySummaryEntry> list = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_SUMMARY);
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            //Start by looking for the menu tag
            if (name.equals(TAG_NAME_ENTRY)) {
                list.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }

        return list;
    }

    // Parses the contents of a menu. If it encounters a title, description, and icon, hands
    // them off to their respective "read" methods for processing. Otherwise, skip the tag.
    private TaskPrioritySummaryEntry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_ENTRY);
        int key = -1;
        String label = null;
        float value = -1f;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(TAG_NAME_KEY)) {
                key = readKey(parser);
            } else if (name.equals(TAG_NAME_LABEL)) {
                label = readLabel(parser);
            } else if (name.equals(TAG_NAME_VALUE)) {
                value = readValue(parser);
            } else {
                skip(parser);
            }
        }

        return new TaskPrioritySummaryEntry(key, label, value);
    }

    private int readKey(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_KEY);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TAG_NAME_KEY);
        return Integer.valueOf(result);
    }

    private String readLabel(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_LABEL);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TAG_NAME_LABEL);
        return result;
    }

    private float readValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TAG_NAME_VALUE);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TAG_NAME_VALUE);
        return Float.valueOf(result);
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
