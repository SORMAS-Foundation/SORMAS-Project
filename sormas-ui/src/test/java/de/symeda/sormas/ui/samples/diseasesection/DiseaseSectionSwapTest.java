package de.symeda.sormas.ui.samples.diseasesection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.FormEventBus;

class DiseaseSectionSwapTest extends AbstractUiBeanTest {

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
	void swappingFromGonococcalToAnotherAstSectionRebindsTheGrid() {
		AbstractDiseaseSectionComponent gonococcal = activate(new GonococcalInfectionSectionComponent(), Disease.GONOCOCCAL_INFECTION);
		gonococcal.cleanup();
		assertNull(pathogenTest.getDrugSusceptibility());

		assertDoesNotThrow(() -> activate(new ImiSectionComponent(), Disease.INVASIVE_MENINGOCOCCAL_INFECTION));

		assertNotNull(pathogenTest.getDrugSusceptibility());
		assertSame(pathogenTest.getDrugSusceptibility(), fieldGroup.getField(PathogenTestDto.DRUG_SUSCEPTIBILITY).getValue());
	}

	@Test
	void swappingFromShigellosisToGonococcalRebindsTheGrid() {
		activate(new ShigellosisSectionComponent(), Disease.SHIGELLOSIS).cleanup();

		assertDoesNotThrow(() -> activate(new GonococcalInfectionSectionComponent(), Disease.GONOCOCCAL_INFECTION));

		assertNotNull(pathogenTest.getDrugSusceptibility());
	}

	@Test
	void cleanedUpGonococcalSectionStopsReactingToTestTypeChanges() {
		activate(new GonococcalInfectionSectionComponent(), Disease.GONOCOCCAL_INFECTION).cleanup();
		AtomicInteger resultRequests = new AtomicInteger();
		eventBus.on(SetTestResultEvent.class, event -> resultRequests.incrementAndGet());

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.GENOTYPING));

		assertEquals(0, resultRequests.get());
	}

	@Test
	void hidingGenotypingClearsTheTypingValues() {
		pathogenTest.setPorBAllele("porB-908");
		pathogenTest.setSequenceType("ST-1901");
		activate(new GonococcalInfectionSectionComponent(), Disease.GONOCOCCAL_INFECTION);
		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.GENOTYPING));

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.NAAT));

		assertTrue(StringUtils.isEmpty(pathogenTest.getPorBAllele()));
		assertTrue(StringUtils.isEmpty(pathogenTest.getSequenceType()));
	}

	private AbstractDiseaseSectionComponent activate(AbstractDiseaseSectionComponent section, Disease disease) {
		section.initialize(fieldGroup, eventBus, config, disease);
		section.setDto(pathogenTest);
		return section;
	}
}
