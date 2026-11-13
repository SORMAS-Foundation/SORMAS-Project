package de.symeda.sormas.backend.symptoms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;
import de.symeda.sormas.backend.AbstractUnitTest;

class SymptomsStiMappingTest extends AbstractUnitTest {

	private final SymptomsFacadeEjb victim = new SymptomsFacadeEjb();

	@Test
	void preservesOtherInfectionSiteInBothDirections() {
		// PREPARE
		SymptomsDto source = SymptomsDto.build();
		source.setSyphilisInfectionSite(SyphilisInfectionSite.OTHER);
		source.setSyphilisInfectionSiteOther("Infection site details");

		// EXECUTE
		Symptoms entity = victim.fillOrBuildEntity(source, null, false);
		SymptomsDto result = SymptomsFacadeEjb.toSymptomsDto(entity);

		// CHECK
		assertEquals(source.getSyphilisInfectionSite(), entity.getSyphilisInfectionSite());
		assertEquals(source.getSyphilisInfectionSiteOther(), entity.getSyphilisInfectionSiteOther());
		assertEquals(source.getSyphilisInfectionSite(), result.getSyphilisInfectionSite());
		assertEquals(source.getSyphilisInfectionSiteOther(), result.getSyphilisInfectionSiteOther());
	}
}
