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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.sample.PathogenTestType;

/**
 * {@code PathogenDetectionMethod}: that the same test means different things to different subject
 * codes, and that a test EpiPulse has no value for is left out rather than guessed at.
 */
public class EpipulsePathogenTestTypeRefTest {

	@Test
	@DisplayName("one test type, four subject codes, three different codes")
	public void theSameTestMeansDifferentThingsPerSubjectCode() {

		// this is the whole reason the lookup takes a subject code. Before it did, every one of
		// these reported PERT's answer.
		assertEquals("PCR", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.PERT, PathogenTestType.PCR_RT_PCR));
		assertEquals("NUC", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.CHIK, PathogenTestType.PCR_RT_PCR));
		assertEquals("NUC", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.WNF, PathogenTestType.PCR_RT_PCR));
		assertEquals("NUC", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.DENGUE, PathogenTestType.PCR_RT_PCR));
	}

	@Test
	@DisplayName("a value covers a family of test types, not one")
	public void oneValueCoversAFamilyOfTests() {

		// SORMAS records the amplification technique; EpiPulse asks only whether nucleic acid was
		// amplified. All of these are one answer.
		for (PathogenTestType amplification : new PathogenTestType[] {
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.Q_PCR,
			PathogenTestType.MULTIPLEX_PCR,
			PathogenTestType.DIGITAL_PCR,
			PathogenTestType.NAAT,
			PathogenTestType.LAMP,
			PathogenTestType.NASBA,
			PathogenTestType.TMA }) {

			assertEquals("NUC", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.CHIK, amplification), amplification.name());
		}
	}

	@Test
	@DisplayName("PERT reports serology, which it could not before")
	public void pertussisReportsSerology() {

		// SERO was declared with no test type, so a case diagnosed by serology alone reported no
		// detection method at all
		assertEquals("SERO", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.PERT, PathogenTestType.IGG_SERUM_ANTIBODY));
		assertEquals("SERO", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.PERT, PathogenTestType.IGM_SERUM_ANTIBODY));
		assertEquals("CULT", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.PERT, PathogenTestType.CULTURE));
	}

	@Test
	@DisplayName("IgM is a value of its own where serology is not")
	public void igmIsItsOwnValueForTheArbovirusCodes() {

		// CHIK, DENGUE, TBE and WNF ask specifically for specific IgM antibodies, so IgG alone
		// answers nothing and is not reported
		assertEquals("SIGM", EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.CHIK, PathogenTestType.IGM_SERUM_ANTIBODY));
		assertNull(EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.CHIK, PathogenTestType.IGG_SERUM_ANTIBODY));
	}

	@Test
	@DisplayName("a test type this subject code has no value for is not reported")
	public void anUnmappedTestTypeIsNotReported() {

		// no invention: microscopy is not one of CHIK's six values, and OTH is not a bucket for
		// tests that answer a different question
		assertNull(EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.CHIK, PathogenTestType.MICROSCOPY));
		assertNull(EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.PERT, PathogenTestType.SEROTYPING));
	}

	@Test
	@DisplayName("PNEU is absent, answering the variable another way")
	public void pneumococcusIsNotMappedHere() {

		// its PathogenDetectionMethod is a serotyping technique -- see
		// EpipulsePneumococcalSerotypingMethodRef
		assertNull(EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.PNEU, PathogenTestType.CULTURE));
		assertTrue(EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(EpipulseSubjectCode.PNEU).isEmpty());
	}

	@Test
	@DisplayName("a subject code that does not declare the variable reads nothing")
	public void anUndeclaringSubjectCodeReadsNothing() {

		// the list is the export's filter too, so an empty one means no test is read at all
		assertTrue(EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(EpipulseSubjectCode.MEAS).isEmpty());
		assertTrue(EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(null).isEmpty());
	}

	@Test
	@DisplayName("the filter lists each test type once, however many values cover it")
	public void theFilterIsDistinct() {

		List<PathogenTestType> pertTypes = EpipulsePathogenTestTypeRef.getPathogenTestTypesByDisease(EpipulseSubjectCode.PERT);

		assertEquals(pertTypes.size(), pertTypes.stream().distinct().count());
		assertTrue(pertTypes.contains(PathogenTestType.CULTURE));
		assertTrue(pertTypes.contains(PathogenTestType.PCR_RT_PCR));
		assertTrue(pertTypes.contains(PathogenTestType.IGG_SERUM_ANTIBODY));
	}

	@Test
	@DisplayName("a value with no test types stays declared, and contributes nothing")
	public void unboundValuesAreDeclaredButContributeNothing() {

		// ORALIgG and SCONV describe a specimen and a change between two results; SORMAS records
		// neither on a pathogen test. They are listed so the gap is visible, and they neither
		// produce a code nor widen the filter.
		assertEquals(0, EpipulsePathogenTestTypeRef.PERT_ORALIgG.getTestTypes().length);
		assertEquals(0, EpipulsePathogenTestTypeRef.CHIK_SCONV.getTestTypes().length);
		assertEquals(0, EpipulsePathogenTestTypeRef.WNF_OTH.getTestTypes().length);
	}

	@Test
	@DisplayName("nothing in, nothing out")
	public void missingArgumentsYieldNothing() {

		assertNull(EpipulsePathogenTestTypeRef.codeFor(null, PathogenTestType.CULTURE));
		assertNull(EpipulsePathogenTestTypeRef.codeFor(EpipulseSubjectCode.PERT, null));
	}
}
