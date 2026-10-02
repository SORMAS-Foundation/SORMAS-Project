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
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.therapy.SusceptibilityMethod;

/**
 * EpiPulse allows one {@code ASTMethod} per case and SORMAS records one per antimicrobial, so the
 * rule that collapses the panel is what this pins.
 */
public class EpipulseAstMethodRefTest {

	/**
	 * The methods EpiPulse has no code for. {@code DISK_DIFFUSION} reports a zone diameter,
	 * {@code GENOTYPIC_WGS} infers resistance from the genome, and {@code BREAKPOINT} only tests
	 * growth at the breakpoint concentration, so none of the three yields an MIC, which is what
	 * the variable is about. {@code MIC} names the result rather than the technique, and mapping it
	 * to one of EpiPulse's named techniques would claim one that was not recorded.
	 */
	private static final Set<SusceptibilityMethod> NO_EPIPULSE_CODE = EnumSet.of(
		SusceptibilityMethod.DISK_DIFFUSION,
		SusceptibilityMethod.GENOTYPIC_WGS,
		SusceptibilityMethod.BREAKPOINT,
		SusceptibilityMethod.MIC);

	@Test
	@DisplayName("every method SORMAS can record is either mapped or declared to have no code")
	public void everyMethodIsAccountedFor() {

		for (SusceptibilityMethod method : SusceptibilityMethod.values()) {
			if (NO_EPIPULSE_CODE.contains(method)) {
				assertNull(EpipulseAstMethodRef.codeFor(method), method.name() + " is not in EpiPulse's list and must not be mapped");
				continue;
			}

			assertNotNull(
				EpipulseAstMethodRef.codeFor(method),
				method.name() + " is not accounted for: map it in EpipulseAstMethodRef, or add it to NO_EPIPULSE_CODE here "
					+ "so that resolve() reports it as OTH.");
		}
	}

	@Test
	@DisplayName("the four techniques EpiPulse names map to it")
	public void theNamedTechniquesMap() {

		assertEquals("AGARDIL", EpipulseAstMethodRef.codeFor(SusceptibilityMethod.AGAR_DILUTION));
		assertEquals("AUTOM", EpipulseAstMethodRef.codeFor(SusceptibilityMethod.AUTOMATED_MIC));
		assertEquals("BROTHDIL", EpipulseAstMethodRef.codeFor(SusceptibilityMethod.BROTH_MICRODILUTION));
		// EpiPulse's GRAD is "antimicrobial gradient (E-test, etc)"; SORMAS names it after the strip
		assertEquals("GRAD", EpipulseAstMethodRef.codeFor(SusceptibilityMethod.ETEST));
		assertEquals("OTH", EpipulseAstMethodRef.codeFor(SusceptibilityMethod.OTHER));
	}

	@Test
	@DisplayName("a panel that agrees reports the method it agrees on")
	public void agreementIsReported() {

		assertEquals(
			"BROTHDIL",
			EpipulseAstMethodRef.resolve(
				Arrays.asList(
					SusceptibilityMethod.BROTH_MICRODILUTION,
					SusceptibilityMethod.BROTH_MICRODILUTION,
					SusceptibilityMethod.BROTH_MICRODILUTION)));
	}

	@Test
	@DisplayName("a panel that disagrees reports OTH rather than picking a drug")
	public void disagreementIsOther() {

		assertEquals(
			"OTH",
			EpipulseAstMethodRef
				.resolve(Arrays.asList(SusceptibilityMethod.BROTH_MICRODILUTION, SusceptibilityMethod.ETEST, SusceptibilityMethod.AGAR_DILUTION)));
		// two of three agreeing is still a disagreement
		assertEquals(
			"OTH",
			EpipulseAstMethodRef.resolve(Arrays.asList(SusceptibilityMethod.ETEST, SusceptibilityMethod.ETEST, SusceptibilityMethod.AGAR_DILUTION)));
	}

	@Test
	@DisplayName("a panel agreeing on a method EpiPulse has no code for reports OTH")
	public void anUnlistedAgreementIsOther() {

		for (SusceptibilityMethod method : NO_EPIPULSE_CODE) {
			assertEquals("OTH", EpipulseAstMethodRef.resolve(Arrays.asList(method, method, method)), method.name() + " should resolve to OTH");
		}
	}

	@Test
	@DisplayName("a drug with no method recorded does not count as a disagreement")
	public void absentMethodsAreSkipped() {

		// only penicillin carries a method; the case still says exactly one thing
		assertEquals("GRAD", EpipulseAstMethodRef.resolve(Arrays.asList(null, null, SusceptibilityMethod.ETEST)));
		// and an absent method cannot rescue a real disagreement
		assertEquals("OTH", EpipulseAstMethodRef.resolve(Arrays.asList(SusceptibilityMethod.ETEST, null, SusceptibilityMethod.AGAR_DILUTION)));
	}

	@Test
	@DisplayName("a panel with nothing recorded reports nothing")
	public void nothingRecordedIsBlank() {

		assertNull(EpipulseAstMethodRef.resolve(null));
		assertNull(EpipulseAstMethodRef.resolve(Collections.emptyList()));
		assertNull(EpipulseAstMethodRef.resolve(Arrays.asList(null, null, null)));
	}

	@Test
	@DisplayName("the enum covers the whole EpiPulse reference list for PNEU")
	public void theReferenceListIsComplete() {

		// the 5 values EpiPulseCasesMetadata lists for PNEU ASTMethod, in its own order
		List<String> expected = Arrays.asList("AGARDIL", "AUTOM", "BROTHDIL", "GRAD", "OTH");

		assertEquals(expected, Arrays.stream(EpipulseAstMethodRef.values()).map(Enum::name).sorted().collect(Collectors.toList()));
	}
}
