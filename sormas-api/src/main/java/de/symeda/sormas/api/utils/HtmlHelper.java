/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

// This class provides general XSS-Prevention methods using Jsoup.clean
public class HtmlHelper {

	public static final Whitelist EVENTACTION_WHITELIST =
		Whitelist.relaxed().addTags("hr", "font").addAttributes("font", "size", "face", "color").addAttributes("div", "align");

	public static String cleanHtml(String string) {
		return (string == null) ? "" : Jsoup.clean(string, Whitelist.none());
	}

	public static String cleanHtml(String string, Whitelist whitelist) {
		return (string == null) ? "" : Jsoup.clean(string, whitelist);
	}

	/**
	 * @param attributeKey
	 *            For example {@code title}.
	 * @param attributeValue
	 *            The value for the given {@code attributeKey}.
	 * @return Built syntax for a HTML tag attribute with possible html tags in {@code attributeValue} escaped to prevent HTML injection.
	 */
	public static String cleanHtmlAttribute(String attributeKey, String attributeValue) {

		return String.format("%s='%s'", attributeKey, HtmlHelper.cleanHtml(attributeValue));
	}

	// this method should be used for i18n-strings and captions so that custom whitelist rules can be added when needed
	public static String cleanI18nString(String string) {
		return (string == null) ? "" : Jsoup.clean(string, Whitelist.basic());
	}

	public static String cleanHtmlRelaxed(String string) {
		return (string == null) ? "" : Jsoup.clean(string, Whitelist.relaxed());
	}

	/**
	 * @param title
	 *            Title for {@code a} tag (hover text).
	 * @param caption
	 *            Caption of the {@code a} tag (visual text).
	 * @return Generated hyperlink with possible html tags escaped to prevent HTML injection.
	 */
	public static String buildHyperlinkTitle(String title, String caption) {

		String result = String.format("<a %s>%s</a>", cleanHtmlAttribute("title", title), HtmlHelper.cleanHtml(caption));
		return result;
	}
}
