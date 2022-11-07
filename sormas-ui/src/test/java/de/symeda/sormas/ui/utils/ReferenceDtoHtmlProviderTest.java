package de.symeda.sormas.ui.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;

public class ReferenceDtoHtmlProviderTest {

	@Test
	public void testApplyPreventsHtmlInjection() {

		ReferenceDtoHtmlProvider renderer = new ReferenceDtoHtmlProvider();
		ReferenceDto dto = new AreaReferenceDto("<script>alert(1);</script>", "<script>alert(2);</script>");
		String result = renderer.apply(dto);

		assertFalse(result.contains("<script>alert(1);</script>"));
		assertFalse(result.contains("<script>alert(2);</script>"));
	}
}
