package de.symeda.sormas.ui.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class V7CaseUuidRendererTest {

	@Test
	public void encodeTest() {
		V7CaseUuidRenderer renderer = new V7CaseUuidRenderer(false);
		String result = renderer.sanitizeInput("<script>alert(1);</script>");
		assertEquals(result.contains("<script>"), false);
	}
}
