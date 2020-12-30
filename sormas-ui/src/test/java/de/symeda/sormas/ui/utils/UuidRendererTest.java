package de.symeda.sormas.ui.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UuidRendererTest {

	@Test
	public void encodeTest() {
		UuidRenderer renderer = new UuidRenderer();
		String result = renderer.sanitizeInput("<script>alert(1);</script>");
		assertEquals(result.contains("<script>alert(1);</script>"), false);
	}
}
