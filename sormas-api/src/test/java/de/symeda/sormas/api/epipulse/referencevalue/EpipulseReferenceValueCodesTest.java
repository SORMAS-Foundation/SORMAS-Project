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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The three reference values whose EpiPulse code cannot be a Java identifier.
 * Just a plain test to check for drift and pin.
 */
public class EpipulseReferenceValueCodesTest {

	@Test
	@DisplayName("Status exports a value no enum constant could be named")
	public void statusCodesAreExplicit() {

		assertEquals("NEW/UPDATE", EpipulseStatusRef.NEW_UPDATE.getCode());
		assertEquals("DELETE", EpipulseStatusRef.DELETE.getCode());
	}

	@Test
	@DisplayName("the dose codes lead with a digit, so they are stated rather than named")
	public void doseCodesAreExplicit() {

		assertEquals("NOTVACC", EpipulseVaccinationStatusRef.NOTVACC.getCode());
		assertEquals("UNKDOSE", EpipulseVaccinationStatusRef.UNKDOSE.getCode());
		assertEquals("1DOSE", EpipulseVaccinationStatusRef.ONE_DOSE.getCode());
		assertEquals("4DOSE", EpipulseVaccinationStatusRef.FOUR_DOSE.getCode());
		assertEquals("10DOSE", EpipulseVaccinationStatusRef.TEN_DOSE.getCode());
	}

	@Test
	@DisplayName("the combined meningococcal serogroup carries a slash too")
	public void theCombinedSerogroupCodeIsExplicit() {

		assertEquals("NEIMENI_Z/29E", EpipulseMeningococcalSerogroupRef.NEIMENI_Z_29E.getCode());
		// its neighbours are identifier-compatible, so they are their own code
		assertEquals("NEIMENI_Z", EpipulseMeningococcalSerogroupRef.NEIMENI_Z.getCode());
		assertEquals("NEIMENI_29E", EpipulseMeningococcalSerogroupRef.NEIMENI_29E.getCode());
	}

	@Test
	@DisplayName("a reference value whose name is already the code is read straight off the constant")
	public void identifierCompatibleValuesUseTheirName() {

		// the convention, stated once: no second spelling to keep in step
		assertEquals("CONF", EpipulseCaseClassificationRef.CONF.name());
		assertEquals("OTH", EpipulseGenderRef.OTH.name());
		assertEquals("QUE", EpipulsePneumococcalSerotypingMethodRef.QUE.name());
	}

	@Test
	@DisplayName("the detection methods are stated because one code belongs to several subject codes")
	public void detectionMethodCodesAreExplicit() {

		// the same value under two subject codes cannot be two constants of the same name, so
		// these carry a prefix and the code is stated. Nothing else asserts the spellings.
		assertEquals("NUC", EpipulsePathogenTestTypeRef.CHIK_NUC.getCode());
		assertEquals("NUC", EpipulsePathogenTestTypeRef.WNF_NUC.getCode());
		assertEquals("PCR", EpipulsePathogenTestTypeRef.PERT_PCR.getCode());
		// mixed case is deliberate and survives, since nothing upper-cases it
		assertEquals("ORALIgG", EpipulsePathogenTestTypeRef.PERT_ORALIgG.getCode());
	}
}
