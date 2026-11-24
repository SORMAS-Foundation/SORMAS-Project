package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

public class GenoTypeRetirementTest {

	@Test
	public void retiredGenotypeBResolvesToB1() {
		assertThat(GenoType.fromLegacyName("GENOTYPE_B"), is(GenoType.GENOTYPE_B1));
	}

	@Test
	public void retiredGenotypeBArrivingAsJsonResolvesToB1() throws Exception {
		// un-upgraded adapters and peers still send the old name
		assertThat(new ObjectMapper().readValue("\"GENOTYPE_B\"", GenoType.class), is(GenoType.GENOTYPE_B1));
	}

	@Test
	public void genotypeBIsNoLongerSelectable() {
		assertThat(Arrays.stream(GenoType.values()).anyMatch(g -> g.name().equals("GENOTYPE_B")), is(false));
	}

	@Test
	public void anUnknownGenotypeNameStillFails() {
		assertThrows(ValueInstantiationException.class, () -> new ObjectMapper().readValue("\"GENOTYPE_Z9\"", GenoType.class));
	}
}
