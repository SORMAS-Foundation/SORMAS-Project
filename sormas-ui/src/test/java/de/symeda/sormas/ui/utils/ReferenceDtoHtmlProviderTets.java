package de.symeda.sormas.ui.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.region.AreaReferenceDto;

public class ReferenceDtoHtmlProviderTets {

	@Test
	public void encodeTest() {
		ReferenceDtoHtmlProvider renderer = new ReferenceDtoHtmlProvider();
		ReferenceDto dto = new AreaReferenceDto("<script>alert(1);</script>", "<script>alert(2);</script>");
		String result = renderer.apply(dto);

		assertEquals(result.contains("<script>alert(1);</script>"), false);
		assertEquals(result.contains("<script>alert(2);</script>"), false);
	}
}
