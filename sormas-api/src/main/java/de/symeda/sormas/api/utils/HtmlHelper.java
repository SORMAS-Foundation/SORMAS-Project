package de.symeda.sormas.api.utils;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlHelper {

	// unescape specific tags (<b>,<i>,<br>) escaped using StringEscapeUtils.escapeHtml4(String)
	public static String unescapeTags(String escapedString) {
		String res = escapedString;

		// <b> Tags
		res = res.replaceAll("&lt;b&gt;", "<b>");
		res = res.replaceAll("&lt;/b&gt;", "</b>");
		res = res.replaceAll("&lt;.b&gt;", "<b>");
		res = res.replaceAll("&lt;./b&gt;", "</b>");
		// <i> Tags
		res = res.replaceAll("&lt;i&gt;", "<i>");
		res = res.replaceAll("&lt;/i&gt;", "</i>");
		res = res.replaceAll("&lt;.i&gt;", "<i>");
		res = res.replaceAll("&lt;./i&gt;", "</i>");
		// <br> Tags
		res = res.replaceAll("&lt;br.&gt;", "<br>");
		res = res.replaceAll("&lt;.br&gt;", "<br>");

		return res;
	}

	// escapes html4 and then unescapes specific tags
	public static String escapeAndUnescapeTags(String text) {
		return unescapeTags(StringEscapeUtils.escapeHtml4(text));
	}
}
