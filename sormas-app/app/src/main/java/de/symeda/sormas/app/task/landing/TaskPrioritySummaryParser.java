/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.task.landing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * Created by Orson on 01/12/2017.
 */

public class TaskPrioritySummaryParser {

	private static final String TAG = TaskPrioritySummaryParser.class.getSimpleName();

	private static final String NS = null;
	private Context context;

	private final String TAG_NAME_SUMMARY = "summary";
	private final String TAG_NAME_SUMMARY_NAME = "name";
	private final String TAG_NAME_ENTRY = "entry";
	private final String TAG_NAME_KEY = "key";
	private final String TAG_NAME_LABEL = "label";
	private final String TAG_NAME_VALUE = "value";

	List<TaskPrioritySummaryEntry> mEntries;

	public TaskPrioritySummaryParser(Context context) {
		this.context = context;
	}

	public List<TaskPrioritySummaryEntry> parse(XmlResourceParser parser) {
		try {
			while (parser.next() != XmlResourceParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlResourceParser.START_TAG) {
					String menuValue = parser.getName();

					if (menuValue.equals(TAG_NAME_SUMMARY)) {
						mEntries = readSummaryTag(parser);
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

		return mEntries;
	}

	private List<TaskPrioritySummaryEntry> readSummaryTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
		//Guard.That.NotNull.isTrue(parser);

		parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_SUMMARY);
		String summaryName = parser.getAttributeValue(NS, TAG_NAME_SUMMARY_NAME);

		mEntries = new ArrayList<>();

		while (parser.next() != XmlResourceParser.END_TAG) {
			if (parser.getEventType() != XmlResourceParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(TAG_NAME_ENTRY)) {
				mEntries.add(readEntryTag(parser));
			} else {
				skipTag(parser);
			}
		}

		return mEntries;
	}

	private TaskPrioritySummaryEntry readEntryTag(XmlResourceParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_ENTRY);
		int key = -1;
		String label = null;
		float value = -1f;

		while (parser.next() != XmlResourceParser.END_TAG) {
			if (parser.getEventType() != XmlResourceParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(TAG_NAME_KEY)) {
				key = readKeyTag(parser);
			} else if (name.equals(TAG_NAME_LABEL)) {
				label = readLabelTag(parser);
			} else if (name.equals(TAG_NAME_VALUE)) {
				value = readValueTag(parser);
			} else {
				skipTag(parser);
			}
		}

		return new TaskPrioritySummaryEntry(key, label, value);
	}

	private int readKeyTag(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_KEY);
		String result = parser.nextText();
		parser.require(XmlResourceParser.END_TAG, NS, TAG_NAME_KEY);
		return Integer.valueOf(result);
	}

	private String readLabelTag(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_LABEL);
		String result = parser.nextText();
		parser.require(XmlResourceParser.END_TAG, NS, TAG_NAME_LABEL);
		return result;
	}

	private float readValueTag(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlResourceParser.START_TAG, NS, TAG_NAME_VALUE);
		String result = parser.nextText();
		parser.require(XmlResourceParser.END_TAG, NS, TAG_NAME_VALUE);
		return Float.valueOf(result);
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
