package de.symeda.sormas.ui.caze;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;

class DiagnosisCriteriaLabTestPanelTest {

	private static final String HISTOPATHOLOGY_TEST_LOC = "histopathologyTestLoc";
	private static final String HISTOPATHOLOGY_TEST_RESULT_LOC = "histopathologyTestResultLoc";

	@Test
	void showsHistopathologyRowFilledFromATuberculosisTest() {
		DiagnosisCriteriaLabTestPanel panel = new DiagnosisCriteriaLabTestPanel(
			Disease.TUBERCULOSIS,
			List.of(histopathologyTest(PathogenTestResultType.POSITIVE)));

		OptionGroup testType = (OptionGroup) panel.getComponent(HISTOPATHOLOGY_TEST_LOC);
		assertNotNull(testType);
		assertEquals(PathogenTestType.HISTOPATHOLOGY.toString(), testType.getCaption());
		assertEquals("YES", testType.getValue());
		assertEquals("POSITIVE", ((OptionGroup) panel.getComponent(HISTOPATHOLOGY_TEST_RESULT_LOC)).getValue());
	}

	@Test
	void showsHistopathologyRowAsNotDoneWithoutATest() {
		DiagnosisCriteriaLabTestPanel panel = new DiagnosisCriteriaLabTestPanel(Disease.TUBERCULOSIS, List.of());

		assertEquals("NO", ((OptionGroup) panel.getComponent(HISTOPATHOLOGY_TEST_LOC)).getValue());
	}

	@Test
	void keepsTheFirstOfSeveralHistopathologyTests() {
		List<PathogenTestDto> tests =
			List.of(histopathologyTest(PathogenTestResultType.NEGATIVE), histopathologyTest(PathogenTestResultType.POSITIVE));

		DiagnosisCriteriaLabTestPanel panel = assertDoesNotThrow(() -> new DiagnosisCriteriaLabTestPanel(Disease.TUBERCULOSIS, tests));
		assertEquals("NEGATIVE", ((OptionGroup) panel.getComponent(HISTOPATHOLOGY_TEST_RESULT_LOC)).getValue());
	}

	private static PathogenTestDto histopathologyTest(PathogenTestResultType result) {
		PathogenTestDto test = new PathogenTestDto();
		test.setTestType(PathogenTestType.HISTOPATHOLOGY);
		test.setTestResult(result);
		test.setTestDateTime(new Date());
		return test;
	}
}
