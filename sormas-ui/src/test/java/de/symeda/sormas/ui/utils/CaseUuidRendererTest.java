package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.region.AreaReferenceDto;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CaseUuidRendererTest {

	@Test
	public void encodeTest() {
		CaseUuidRenderer renderer = new CaseUuidRenderer(e -> {
			return false;
		});
		String result = renderer.sanitizeInput("<script>alert(1);</script>");
		assertEquals(result.contains("<script>alert(1);</script>"), false);
	}
}
