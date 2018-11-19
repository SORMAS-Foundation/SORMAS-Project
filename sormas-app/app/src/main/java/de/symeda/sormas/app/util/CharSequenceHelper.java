/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

/**
 * Created by Orson on 25/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class CharSequenceHelper {

    /**
     * Returns a CharSequence that concatenates the specified array of CharSequence
     * objects and then applies a list of zero or more tags to the entire range.
     *
     * @param content an array of character sequences to apply a style to
     * @param tags the styled span objects to apply to the content
     *        such as android.text.style.StyleSpan
     *
     */
    private static CharSequence apply(CharSequence[] content, Object... tags) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        openTags(text, tags);
        for (CharSequence item : content) {
            text.append(item);
        }
        closeTags(text, tags);
        return text;
    }

    /**
     * Iterates over an array of tags and applies them to the beginning of the specified
     * Spannable object so that future text appended to the text will have the styling
     * applied to it. Do not call this method directly.
     */
    private static void openTags(Spannable text, Object[] tags) {
        for (Object tag : tags) {
            text.setSpan(tag, 0, 0, Spannable.SPAN_MARK_MARK);
        }
    }

    /**
     * "Closes" the specified tags on a Spannable by updating the spans to be
     * endpoint-exclusive so that future text appended to the end will not take
     * on the same styling. Do not call this method directly.
     */
    private static void closeTags(Spannable text, Object[] tags) {
        int len = text.length();
        for (Object tag : tags) {
            if (len > 0) {
                text.setSpan(tag, 0, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                text.removeSpan(tag);
            }
        }
    }

    /**
     * Returns a CharSequence that applies boldface to the concatenation
     * of the specified CharSequence objects.
     */
    public static CharSequence bold(CharSequence... content) {
        return apply(content, new StyleSpan(Typeface.BOLD));
    }

    /**
     * Returns a CharSequence that applies italics to the concatenation
     * of the specified CharSequence objects.
     */
    public static CharSequence italic(CharSequence... content) {
        return apply(content, new StyleSpan(Typeface.ITALIC));
    }

    /**
     * Returns a CharSequence that applies a foreground color to the
     * concatenation of the specified CharSequence objects.
     */
    public static CharSequence color(int color, CharSequence... content) {
        return apply(content, new ForegroundColorSpan(color));
    }
}
