package de.symeda.sormas.backend.symptoms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;
import de.symeda.sormas.backend.AbstractUnitTest;

class SyphilisInfectionSiteSetConverterTest extends AbstractUnitTest {

	private final SyphilisInfectionSiteSetConverter victim = new SyphilisInfectionSiteSetConverter();

	@Test
	void roundTripsMultipleSites() {
		// PREPARE
		Set<SyphilisInfectionSite> sites = Set.of(SyphilisInfectionSite.GENITAL, SyphilisInfectionSite.OTHER);

		// EXECUTE
		Set<SyphilisInfectionSite> result = victim.convertToEntityAttribute(victim.convertToDatabaseColumn(sites));

		// CHECK
		assertEquals(sites, result);
	}

	@Test
	void readsExistingSingleSite() {
		// PREPARE
		String stored = "GENITAL";

		// EXECUTE
		Set<SyphilisInfectionSite> result = victim.convertToEntityAttribute(stored);

		// CHECK
		assertEquals(Set.of(SyphilisInfectionSite.GENITAL), result);
	}

	@Test
	void preservesNull() {
		// PREPARE
		Set<SyphilisInfectionSite> sites = null;

		// EXECUTE
		String stored = victim.convertToDatabaseColumn(sites);
		Set<SyphilisInfectionSite> result = victim.convertToEntityAttribute(stored);

		// CHECK
		assertNull(stored);
		assertNull(result);
	}

	@Test
	void roundTripsEmptySet() {
		// PREPARE
		Set<SyphilisInfectionSite> sites = Set.of();

		// EXECUTE
		Set<SyphilisInfectionSite> result = victim.convertToEntityAttribute(victim.convertToDatabaseColumn(sites));

		// CHECK
		assertEquals(sites, result);
	}
}
