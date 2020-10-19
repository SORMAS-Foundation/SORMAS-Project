package de.symeda.sormas.api.utils;

public class HtmlHelper {

	// unescape tags escaped using 
	public static String unescapeTags(String escapedString) {
		String res = escapedString;
		res.replaceAll("&lt;.b&gt;", "<b>");
		res.replaceAll("&lt;./b&gt;", "</b>");
		res.replaceAll("&lt;br.&gt;", "<br/>");
		return res;
	}
}
