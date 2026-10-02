/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.api.epipulse.referencevalue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.GenoType;
import de.symeda.sormas.api.utils.Diseases;

/**
 * Genotype is reported from SORMAS's own {@link GenoType} constants, never from parsing what a
 * laboratory typed, so what this fixes is asserted here: a constant maps or the column stays blank.
 */
public class EpipulseMeaslesGenotypeRefTest {

	/**
	 * The one measles constant with no EpiPulse equivalent. EpiPulse has B1, B2 and B3; SORMAS has
	 * B, B2 and B3, and a bare "B" is not a WHO lineage, so there is nothing to map it to.
	 */
	private static final GenoType UNMAPPABLE = GenoType.GENOTYPE_B;

	private static List<GenoType> measlesGenotypes() {

		return Arrays.stream(GenoType.values()).filter(EpipulseMeaslesGenotypeRefTest::isMeasles).collect(Collectors.toList());
	}

	private static boolean isMeasles(GenoType genoType) {

		try {
			Diseases diseases = GenoType.class.getField(genoType.name()).getAnnotation(Diseases.class);
			return diseases != null && Arrays.asList(diseases.value()).contains(Disease.MEASLES);
		} catch (NoSuchFieldException e) {
			throw new AssertionError(e);
		}
	}

	@Test
	@DisplayName("every measles genotype SORMAS can record is mapped, bar the one that has no EpiPulse value")
	public void everyMeaslesGenotypeIsMapped() {

		for (GenoType genoType : measlesGenotypes()) {
			if (genoType == UNMAPPABLE) {
				continue;
			}

			EpipulseMeaslesGenotypeRef ref = EpipulseMeaslesGenotypeRef.getByGenoType(genoType);

			assertNotNull(ref, genoType.name() + " is a measles genotype with no EpiPulse value mapped");
			// GENOTYPE_D10 -> MEASV_D10: the suffix carries through unchanged
			assertEquals("MEASV_" + genoType.name().substring("GENOTYPE_".length()), ref.name());
		}
	}

	@Test
	@DisplayName("GENOTYPE_B reports nothing rather than guessing at B1")
	public void theAmbiguousConstantIsNotReported() {

		assertNull(EpipulseMeaslesGenotypeRef.getByGenoType(UNMAPPABLE));
		// EpiPulse's value exists, it just has nothing in SORMAS to come from
		assertNull(EpipulseMeaslesGenotypeRef.MEASV_B1.getGenoType());
	}

	@Test
	@DisplayName("a genotype belonging to another disease is not reported as a measles genotype")
	public void otherDiseasesAreNotReported() {

		assertNull(EpipulseMeaslesGenotypeRef.getByGenoType(GenoType.MUMPV_A));
		assertNull(EpipulseMeaslesGenotypeRef.getByGenoType(GenoType.GENOTYPE_1A));
		assertNull(EpipulseMeaslesGenotypeRef.getByGenoType(GenoType.CRYPTOSPORIDIUM_HOMINIS));
	}

	@Test
	@DisplayName("no genotype recorded reports nothing")
	public void nullIsNotReported() {

		assertNull(EpipulseMeaslesGenotypeRef.getByGenoType(null));
	}

	@Test
	@DisplayName("the enum covers the whole EpiPulse reference list for MEAS")
	public void theReferenceListIsComplete() {

		// the 24 values EpiPulseCasesMetadata lists for MEAS Genotype, in its own order
		assertEquals(
			Arrays.asList(
				"MEASV_A",
				"MEASV_B1",
				"MEASV_B2",
				"MEASV_B3",
				"MEASV_C1",
				"MEASV_C2",
				"MEASV_D1",
				"MEASV_D10",
				"MEASV_D11",
				"MEASV_D2",
				"MEASV_D3",
				"MEASV_D4",
				"MEASV_D5",
				"MEASV_D6",
				"MEASV_D7",
				"MEASV_D8",
				"MEASV_D9",
				"MEASV_E",
				"MEASV_F",
				"MEASV_G1",
				"MEASV_G2",
				"MEASV_G3",
				"MEASV_H1",
				"MEASV_H2"),
			Arrays.stream(EpipulseMeaslesGenotypeRef.values()).map(Enum::name).sorted().collect(Collectors.toList()));
	}
}
