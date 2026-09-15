package de.symeda.sormas.api.externalmessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.sample.GenoType;

public class RubeCodeMapperTest {

	private static final Map<String, GenoType> EXPECTED_GENOTYPE_CODES = Map.ofEntries(
		Map.entry("1A", GenoType.GENOTYPE_1A),
		Map.entry("1B", GenoType.GENOTYPE_1B),
		Map.entry("1C", GenoType.GENOTYPE_1C),
		Map.entry("1D", GenoType.GENOTYPE_1D),
		Map.entry("1E", GenoType.GENOTYPE_1E),
		Map.entry("1F", GenoType.GENOTYPE_1F),
		Map.entry("1G", GenoType.GENOTYPE_1G),
		Map.entry("1H", GenoType.GENOTYPE_1H),
		Map.entry("1I", GenoType.GENOTYPE_1I),
		Map.entry("1J", GenoType.GENOTYPE_1J),
		Map.entry("2A", GenoType.GENOTYPE_2A),
		Map.entry("2B", GenoType.GENOTYPE_2B),
		Map.entry("2C", GenoType.GENOTYPE_2C),
		Map.entry("NA", GenoType.GENOTYPE_NA),
		Map.entry("UNK", GenoType.GENOTYPE_UNK));

	@Test
	public void mapsGenotype1A() {
		assertEquals(GenoType.GENOTYPE_1A, RubeCodeMapper.mapGenotype("1A"));
	}

	@Test
	public void mapsGenotype2C() {
		assertEquals(GenoType.GENOTYPE_2C, RubeCodeMapper.mapGenotype("2C"));
	}

	@Test
	public void mapsUnknownCode() {
		assertEquals(GenoType.GENOTYPE_UNK, RubeCodeMapper.mapGenotype("UNK"));
	}

	@Test
	public void returnsNullForUnmappedCode() {
		assertNull(RubeCodeMapper.mapGenotype("ZZZ"));
	}

	@Test
	public void returnsNullForNullInput() {
		assertNull(RubeCodeMapper.mapGenotype(null));
	}

	@Test
	public void mapsEveryRube6GenotypeCode() {
		EXPECTED_GENOTYPE_CODES.forEach((code, expected) -> assertEquals(expected, RubeCodeMapper.mapGenotype(code), code));
	}

	@Test
	public void mapGenotypeIsCaseInsensitive() {
		EXPECTED_GENOTYPE_CODES.forEach((code, expected) -> assertEquals(expected, RubeCodeMapper.mapGenotype(code.toLowerCase(Locale.ROOT)), code));
	}

	@Test
	public void mapGenotypeIgnoresTheDefaultLocale() {
		// Turkish uppercases "i" to "İ", which would miss the "1I" key.
		Locale original = Locale.getDefault();
		try {
			Locale.setDefault(new Locale("tr", "TR"));
			assertEquals(GenoType.GENOTYPE_1I, RubeCodeMapper.mapGenotype("1i"));
		} finally {
			Locale.setDefault(original);
		}
	}

	@Test
	public void mapGenotypeTrimsWhitespace() {
		assertEquals(GenoType.GENOTYPE_1A, RubeCodeMapper.mapGenotype(" 1A "));
		assertEquals(GenoType.GENOTYPE_UNK, RubeCodeMapper.mapGenotype(" UNK "));
	}
}
