package de.symeda.sormas.api.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

/**
 * @see HtmlHelper
 * @author stefan.kock
 */
public class HtmlHelperTest {

	@Test
	public void testCleanHtmlAttribute() {

		assertThat(HtmlHelper.cleanHtmlAttribute("title", "ABCD-1234"), equalTo("title='ABCD-1234'"));
		assertThat(HtmlHelper.cleanHtmlAttribute("title", "a < b & b < c = a < c"), equalTo("title='a &lt; b &amp; b &lt; c = a &lt; c'"));
		assertThat(HtmlHelper.cleanHtmlAttribute("title", "not <b>bold</b>"), equalTo("title='not bold'"));
	}

	@Test
	public void testBuildHyperlinkTitle() {

		assertThat(HtmlHelper.buildHyperlinkTitle("hoverText", "visualText"), equalTo("<a title=\"hoverText\">visualText</a>"));
		assertThat(
			HtmlHelper.buildHyperlinkTitle("transitive relation", "a < b & b < c = a < c"),
			equalTo("<a title=\"transitive relation\">a &lt; b &amp; b &lt; c = a &lt; c</a>"));
		assertThat(HtmlHelper.buildHyperlinkTitle("not <b>bold</b>", "not <i>italic</i>"), equalTo("<a title=\"not bold\">not italic</a>"));
		assertThat(
			HtmlHelper.buildHyperlinkTitle("tagContent' href=\"javascript:alert(1);\" class='", "breakout in title tag prevented"),
			equalTo("<a title=\"tagContent\">breakout in title tag prevented</a>"));
	}

	@Test
	public void testBuildHyperlinkTitlePreventsHtmlInjection() {

		String maliciuousValue = "<script>alert(1);</script>";
		String result = HtmlHelper.buildHyperlinkTitle(maliciuousValue, maliciuousValue);

		assertThat(result, not(containsString(maliciuousValue)));
		assertThat(result, not(containsString("<script>")));
	}
}
