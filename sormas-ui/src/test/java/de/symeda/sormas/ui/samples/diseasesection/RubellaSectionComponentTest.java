package de.symeda.sormas.ui.samples.diseasesection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.AvidityInterpretation;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.FormEventBus;

class RubellaSectionComponentTest extends AbstractUiBeanTest {

	private PathogenTestDto pathogenTest;
	private BeanFieldGroup<PathogenTestDto> fieldGroup;
	private FormEventBus eventBus;
	private PathogenTestFormConfig config;

	@BeforeEach
	void setUpForm() {
		pathogenTest = PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), null);
		fieldGroup = new BeanFieldGroup<>(PathogenTestDto.class);
		fieldGroup.setItemDataSource(pathogenTest);
		eventBus = new FormEventBus();
		config = PathogenTestFormConfig.fromCurrentConfig();
	}

	@Test
	void positiveGenotypingShowsSequenceId() {
		RubellaSectionComponent section = activate(Disease.RUBELLA);

		selectTest(PathogenTestType.GENOTYPING, PathogenTestResultType.POSITIVE);

		assertTrue(section.sequenceIdField().isVisible());
	}

	@Test
	void congenitalRubellaAlsoShowsSequenceId() {
		RubellaSectionComponent section = activate(Disease.CONGENITAL_RUBELLA);

		selectTest(PathogenTestType.GENOTYPING, PathogenTestResultType.POSITIVE);

		assertTrue(section.sequenceIdField().isVisible());
	}

	@Test
	void leavingGenotypingClearsSequenceId() {
		pathogenTest.setSequenceId("RVs/Luxembourg.LUX/1.26");
		activate(Disease.RUBELLA);
		selectTest(PathogenTestType.GENOTYPING, PathogenTestResultType.POSITIVE);

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.IGM_SERUM_ANTIBODY));

		assertTrue(StringUtils.isEmpty(pathogenTest.getSequenceId()));
	}

	@Test
	void positiveIggShowsAvidityPerformedOnly() {
		RubellaSectionComponent section = activate(Disease.RUBELLA);

		selectTest(PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);

		assertTrue(section.avidityPerformedField().isVisible());
		assertFalse(section.avidityIndexField().isVisible());
		assertFalse(section.avidityInterpretationField().isVisible());
	}

	@Test
	void tickingAvidityPerformedRevealsIndexAndInterpretation() {
		RubellaSectionComponent section = activate(Disease.CONGENITAL_RUBELLA);
		selectTest(PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);

		section.avidityPerformedField().setValue(true);

		assertTrue(section.avidityIndexField().isVisible());
		assertTrue(section.avidityInterpretationField().isVisible());
	}

	@Test
	void negativeIggClearsAvidity() {
		pathogenTest.setAvidityPerformed(true);
		pathogenTest.setAvidityIndex(42.5f);
		pathogenTest.setAvidityInterpretation(AvidityInterpretation.HIGH);
		activate(Disease.RUBELLA);
		selectTest(PathogenTestType.IGG_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);

		eventBus.fire(new TestResultChangedEvent(PathogenTestResultType.NEGATIVE));

		assertNotEquals(Boolean.TRUE, pathogenTest.getAvidityPerformed());
		assertNull(pathogenTest.getAvidityIndex());
		assertNull(pathogenTest.getAvidityInterpretation());
	}

	@Test
	void avidityHiddenForIgm() {
		RubellaSectionComponent section = activate(Disease.RUBELLA);

		selectTest(PathogenTestType.IGM_SERUM_ANTIBODY, PathogenTestResultType.POSITIVE);

		assertFalse(section.avidityPerformedField().isVisible());
	}

	private void selectTest(PathogenTestType testType, PathogenTestResultType result) {
		eventBus.fire(new TestTypeChangedEvent(testType));
		eventBus.fire(new TestResultChangedEvent(result));
	}

	private RubellaSectionComponent activate(Disease disease) {
		RubellaSectionComponent section = new RubellaSectionComponent();
		section.initialize(fieldGroup, eventBus, config, disease);
		section.setDto(pathogenTest);
		return section;
	}
}
