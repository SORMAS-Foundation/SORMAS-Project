package de.symeda.sormas.ui.samples.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.samples.PathogenTestForm;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.FormEventBus;

class AdditionalTestInfoComponentTest extends AbstractUiBeanTest {

	private final FormEventBus eventBus = new FormEventBus();

	@Test
	void savedYesSurvivesOpeningANonDefaultTest() {
		PathogenTestDto test = savedTest(PathogenTestType.CULTURE, true);

		new AdditionalTestInfoComponent(eventBus, Disease.GONOCOCCAL_INFECTION).setDto(test);

		assertEquals(Boolean.TRUE, test.getPerformedByReferenceLaboratory());
	}

	@Test
	void savedNoSurvivesOpeningANonDefaultTest() {
		PathogenTestDto test = savedTest(PathogenTestType.PCR_RT_PCR, false);

		new AdditionalTestInfoComponent(eventBus, Disease.DIPHTHERIA).setDto(test);

		assertEquals(Boolean.FALSE, test.getPerformedByReferenceLaboratory());
	}

	@Test
	void savedNoSurvivesOpeningAReferenceLabDefaultTest() {
		PathogenTestDto test = savedTest(PathogenTestType.ISOLATION, false);

		new AdditionalTestInfoComponent(eventBus, Disease.MUMPS).setDto(test);

		assertEquals(Boolean.FALSE, test.getPerformedByReferenceLaboratory());
	}

	@Test
	void choosingIsolationForMumpsDefaultsToYes() {
		PathogenTestDto test = savedTest(null, null);
		new AdditionalTestInfoComponent(eventBus, Disease.MUMPS).setDto(test);

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.ISOLATION));

		assertEquals(Boolean.TRUE, test.getPerformedByReferenceLaboratory());
	}

	@Test
	void leavingIsolationForMumpsClearsTheDefault() {
		PathogenTestDto test = savedTest(null, null);
		new AdditionalTestInfoComponent(eventBus, Disease.MUMPS).setDto(test);
		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.ISOLATION));

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.PCR_RT_PCR));

		assertNull(test.getPerformedByReferenceLaboratory());
	}

	@Test
	void prefilledMumpsIsolationOnANewTestDefaultsToYes() {
		PathogenTestDto test = savedTest(PathogenTestType.ISOLATION, null);

		new AdditionalTestInfoComponent(eventBus, Disease.MUMPS).setDto(test);

		assertEquals(Boolean.TRUE, test.getPerformedByReferenceLaboratory());
	}

	@Test
	void savedYesSurvivesLeavingIsolation() {
		PathogenTestDto test = savedTest(PathogenTestType.ISOLATION, true);
		new AdditionalTestInfoComponent(eventBus, Disease.MUMPS).setDto(test);

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.PCR_RT_PCR));

		assertEquals(Boolean.TRUE, test.getPerformedByReferenceLaboratory());
	}

	@Test
	void savedValueSurvivesOpeningInTheFullForm() {
		mockCurrentUiWithFullDataAccess();
		SampleDto sample = SampleDto.build(null, (CaseReferenceDto) null);
		PathogenTestDto test = savedTest(PathogenTestType.ISOLATION, false);
		test.setTestedDisease(Disease.MUMPS);
		PathogenTestForm form = new PathogenTestForm(sample, false, 0, false, true, Disease.MUMPS);

		form.setValue(test);

		assertEquals(Boolean.FALSE, form.getValue().getPerformedByReferenceLaboratory());
	}

	private PathogenTestDto savedTest(PathogenTestType type, Boolean performedByReferenceLaboratory) {
		PathogenTestDto test = PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), null);
		test.setTestType(type);
		test.setPerformedByReferenceLaboratory(performedByReferenceLaboratory);
		return test;
	}
}
