package de.symeda.sormas.ui.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.symeda.sormas.ui.AbstractBeanTest;

public class V7UuidRendererTest extends AbstractBeanTest {

	@Test
	public void encodeTest() {
		V7UuidRenderer renderer = new V7UuidRenderer();
		String result = renderer.sanitizeInput("<script>alert(1);</script>");
		assertEquals(result.contains("<script>"), false);
	}
}
