package de.symeda.sormas.api.externalmessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.sample.GenoType;

public class RubeCodeMapperTest {

	private final RubeCodeMapper mapper = new RubeCodeMapper();

	@Test
	public void mapsGenotype1A() {
		assertEquals(GenoType.GENOTYPE_1A, mapper.mapGenotype("1A"));
	}

	@Test
	public void mapsGenotype2C() {
		assertEquals(GenoType.GENOTYPE_2C, mapper.mapGenotype("2C"));
	}

	@Test
	public void mapsUnknownCode() {
		assertEquals(GenoType.GENOTYPE_UNK, mapper.mapGenotype("UNK"));
	}

	@Test
	public void returnsNullForUnmappedCode() {
		assertNull(mapper.mapGenotype("ZZZ"));
	}

	@Test
	public void returnsNullForNullInput() {
		assertNull(mapper.mapGenotype(null));
	}
}
