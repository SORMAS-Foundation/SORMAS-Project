package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class PathogenTestDtoDefaultsTest {

	@Test
	public void resultVerifiedAndReferenceLaboratoryStartUnset() {
		PathogenTestDto test = new PathogenTestDto();
		assertNull(test.getTestResultVerified(), "Result verified by supervisor must start unset (#14220)");
		assertNull(test.getPerformedByReferenceLaboratory(), "Performed by reference laboratory must start unset (#14220)");
	}

	@Test
	public void retestRequestedKeepsItsFalseDefault() {
		PathogenTestDto test = new PathogenTestDto();
		assertEquals(Boolean.FALSE, test.getRetestRequested(), "Retest keeps its false default (#14220 decision Q5)");
	}
}
