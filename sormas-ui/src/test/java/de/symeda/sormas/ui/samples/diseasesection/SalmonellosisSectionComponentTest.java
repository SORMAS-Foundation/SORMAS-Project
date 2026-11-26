package de.symeda.sormas.ui.samples.diseasesection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.FormEventBus;

class SalmonellosisSectionComponentTest extends AbstractUiBeanTest {

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
	void factoryReturnsSalmonellosisSection() {
		assertTrue(DiseaseSectionFactory.forDisease(Disease.SALMONELLOSIS) instanceof SalmonellosisSectionComponent);
	}

	@Test
	void serotypingShowsSerotypeOfferingUnknownAndOther() {
		SalmonellosisSectionComponent section = activate();

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.SEROTYPING));

		assertTrue(section.serotypeField().isVisible());
		assertFalse(section.serotypeTextField().isVisible());
		@SuppressWarnings("unchecked")
		List<Serotype> items =
			((ListDataProvider<Serotype>) section.serotypeField().getDataProvider()).getItems().stream().collect(Collectors.toList());
		assertThat(items, containsInAnyOrder(Serotype.UNKNOWN, Serotype.OTHER));
	}

	@Test
	void otherSerotypeRevealsSerotypeText() {
		SalmonellosisSectionComponent section = activate();
		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.SEROTYPING));

		section.serotypeField().setValue(Serotype.OTHER);
		assertTrue(section.serotypeTextField().isVisible());

		section.serotypeField().setValue(Serotype.UNKNOWN);
		assertFalse(section.serotypeTextField().isVisible());
	}

	@Test
	void otherTestTypesHideSerotype() {
		SalmonellosisSectionComponent section = activate();

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.CGMLST));

		assertFalse(section.serotypeField().isVisible());
		assertFalse(section.serotypeTextField().isVisible());
	}

	@Test
	void leavingSerotypingClearsSerotype() {
		SalmonellosisSectionComponent section = activate();
		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.SEROTYPING));
		section.serotypeField().setValue(Serotype.OTHER);
		section.serotypeTextField().setValue("Typhimurium");

		eventBus.fire(new TestTypeChangedEvent(PathogenTestType.CGMLST));

		assertNull(pathogenTest.getSerotype());
		assertTrue(pathogenTest.getSerotypeText() == null || pathogenTest.getSerotypeText().isEmpty());
	}

	@Test
	void cleanupClearsOwnedFields() {
		pathogenTest.setSerotype(Serotype.OTHER);
		pathogenTest.setSerotypeText("Typhimurium");
		SalmonellosisSectionComponent section = activate();

		section.cleanup();

		assertNull(pathogenTest.getSerotype());
		assertNull(pathogenTest.getSerotypeText());
	}

	private SalmonellosisSectionComponent activate() {
		SalmonellosisSectionComponent section = new SalmonellosisSectionComponent();
		section.initialize(fieldGroup, eventBus, config, Disease.SALMONELLOSIS);
		section.setDto(pathogenTest);
		return section;
	}
}
