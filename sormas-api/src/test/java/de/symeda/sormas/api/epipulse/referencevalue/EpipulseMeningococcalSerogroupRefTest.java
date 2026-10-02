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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.sample.SeroGroupSpecification;

/**
 * Serogroup is reported from SORMAS's own {@link SeroGroupSpecification} constants, never from
 * parsing what a laboratory typed into {@code typingid}, so what that replaced is asserted here: a
 * constant maps or the column stays blank.
 */
public class EpipulseMeningococcalSerogroupRefTest {

	/**
	 * The two constants that are not serogroup findings. {@code UNKNOWN} says the serogroup was not
	 * determined; {@code NOT_UNDER_SURVEILLANCE} is about the surveillance programme, not the
	 * isolate. Neither is {@code NEIMENI_OTH}, which asserts a serogroup was found and was none of
	 * the listed ones.
	 */
	private static final Set<SeroGroupSpecification> NOT_A_FINDING =
		EnumSet.of(SeroGroupSpecification.UNKNOWN, SeroGroupSpecification.NOT_UNDER_SURVEILLANCE);

	/**
	 * Another disease's serogroups. Empty today, because meningococcus is the only disease in the
	 * container so far, but {@link SeroGroupSpecification} is a container across diseases, the way
	 * {@code GenoType} holds measles, mumps, rubella and cryptosporidium constants together, so
	 * LEGI's {@code LPNE_*} and {@code LLONG_*} constants will land in it.
	 *
	 * <p>
	 * They belong here rather than in the MENI reference enum: a legionella serogroup on a
	 * meningococcal case is not a {@code NEIMENI_*} value and not {@code NEIMENI_OTH} either.
	 */
	private static final Set<SeroGroupSpecification> ANOTHER_DISEASE = EnumSet.noneOf(SeroGroupSpecification.class);

	@Test
	@DisplayName("every constant in the container is accounted for: a MENI serogroup maps, anything else does not")
	public void everySerogroupIsAccountedFor() {

		for (SeroGroupSpecification seroGroup : SeroGroupSpecification.values()) {
			if (NOT_A_FINDING.contains(seroGroup) || ANOTHER_DISEASE.contains(seroGroup)) {
				assertNull(EpipulseMeningococcalSerogroupRef.codeFor(seroGroup), seroGroup.name() + " must not be reported as a MENI serogroup");
				continue;
			}

			assertNotNull(
				EpipulseMeningococcalSerogroupRef.codeFor(seroGroup),
				seroGroup.name() + " is not accounted for. If it is a meningococcal serogroup, map it in EpipulseMeningococcalSerogroupRef; "
					+ "if it belongs to another disease now sharing the container, add it to ANOTHER_DISEASE here and map it "
					+ "in that disease's own reference enum.");
		}
	}

	@Test
	@DisplayName("the lettered serogroups map to the code of the same name")
	public void letteredSerogroupsCarryThrough() {

		assertEquals("NEIMENI_A", EpipulseMeningococcalSerogroupRef.codeFor(SeroGroupSpecification.SEROGROUP_A));
		assertEquals("NEIMENI_W", EpipulseMeningococcalSerogroupRef.codeFor(SeroGroupSpecification.SEROGROUP_W));
		assertEquals("NEIMENI_29E", EpipulseMeningococcalSerogroupRef.codeFor(SeroGroupSpecification.SEROGROUP_29E));
	}

	@Test
	@DisplayName("not groupable and other are serogroup findings, and are reported as such")
	public void theNonLetteredFindingsAreReported() {

		assertEquals("NEIMENI_NGA", EpipulseMeningococcalSerogroupRef.codeFor(SeroGroupSpecification.NOT_GROUPABLE));
		assertEquals("NEIMENI_OTH", EpipulseMeningococcalSerogroupRef.codeFor(SeroGroupSpecification.OTHER));
	}

	@Test
	@DisplayName("a constant that is not a serogroup finding reports nothing rather than OTH")
	public void whatIsNotAFindingIsNotReported() {

		for (SeroGroupSpecification seroGroup : NOT_A_FINDING) {
			assertNull(EpipulseMeningococcalSerogroupRef.codeFor(seroGroup), seroGroup.name() + " is not a serogroup and must not be reported");
		}
	}

	@Test
	@DisplayName("no serogroup recorded reports nothing")
	public void nullIsNotReported() {

		assertNull(EpipulseMeningococcalSerogroupRef.codeFor(null));
	}

	@Test
	@DisplayName("Z/29E is declared but has nothing in SORMAS to come from")
	public void theCombinedGroupIsDeclaredAndUnmapped() {

		assertNull(EpipulseMeningococcalSerogroupRef.NEIMENI_Z_29E.getSeroGroupSpecification());
		// nothing maps to it, so no recorded serogroup can produce its code
		assertEquals(
			0,
			Arrays.stream(SeroGroupSpecification.values())
				.filter(seroGroup -> "NEIMENI_Z/29E".equals(EpipulseMeningococcalSerogroupRef.codeFor(seroGroup)))
				.count());
	}

	@Test
	@DisplayName("the enum covers the whole EpiPulse reference list for MENI")
	public void theReferenceListIsComplete() {

		// the 11 values EpiPulseCasesMetadata lists for MENI Serogroup, in its own order
		List<String> expected = Arrays.asList(
			"NEIMENI_29E",
			"NEIMENI_A",
			"NEIMENI_B",
			"NEIMENI_C",
			"NEIMENI_NGA",
			"NEIMENI_OTH",
			"NEIMENI_W",
			"NEIMENI_X",
			"NEIMENI_Y",
			"NEIMENI_Z",
			"NEIMENI_Z/29E");

		assertEquals(
			expected,
			Arrays.stream(EpipulseMeningococcalSerogroupRef.values())
				.map(EpipulseMeningococcalSerogroupRef::getCode)
				.sorted()
				.collect(Collectors.toList()));
	}
}
