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

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.sample.SerotypingMethod;

/**
 * PNEU's {@code PathogenDetectionMethod}: which serotyping techniques EpiPulse names, and what
 * happens to the ones it does not.
 */
public class EpipulsePneumococcalSerotypingMethodRefTest {

	/**
	 * Techniques that belong to another disease's serotyping, or to no serotyping at all.
	 *
	 * <p>
	 * {@code SerotypingMethod} is a container shared across diseases: {@code AGGLUTINATION} and
	 * {@code WGS_PREDICTION} are SHIG's, and {@code DISK_DIFFUSION} is a susceptibility method that
	 * happens to live in the same enum. None is a pneumococcal serotyping technique EpiPulse names,
	 * so none has a code of its own.
	 */
	private static final Set<SerotypingMethod> NOT_A_PNEU_METHOD =
		EnumSet.of(SerotypingMethod.AGGLUTINATION, SerotypingMethod.WGS_PREDICTION, SerotypingMethod.DISK_DIFFUSION);

	@Test
	@DisplayName("every serotyping method is either mapped or declared not to be PNEU's")
	public void everySerotypingMethodIsAccountedFor() {

		// A tripwire, not an inventory. SerotypingMethod grows when a disease is added, and this
		// fails on the new constant so that whoever adds it decides where it goes.
		for (SerotypingMethod method : SerotypingMethod.values()) {

			boolean mapped = EpipulsePneumococcalSerotypingMethodRef.codeFor(method) != null;

			if (mapped == NOT_A_PNEU_METHOD.contains(method)) {
				throw new AssertionError(
					"SerotypingMethod." + method.name() + " is unaccounted for. Give it a constant in "
						+ "EpipulsePneumococcalSerotypingMethodRef if EpiPulse names it for PNEU, or add it to "
						+ "NOT_A_PNEU_METHOD if it belongs to another disease.");
			}
		}
	}

	@Test
	@DisplayName("the methods EpiPulse names report their own code")
	public void namedMethodsReportTheirOwnCode() {

		assertEquals("QUE", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.QUELLUNG_REACTION));
		assertEquals("COAGG", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.COAGGLUTINATION));
		assertEquals("GDIFF", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.GEL_DIFFUSION));
		assertEquals("PTEST", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.PNEUMOTEST));
		assertEquals("SLAGG", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.SLIDE_AGGLUTINATION));
		assertEquals("OTH", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.OTHER));
	}

	@Test
	@DisplayName("multiplex PCR is reachable, which is why serotyping tests are read as well as serogrouping ones")
	public void multiplexPcrIsReachable() {

		// SerotypingMethod declares MULTIPLEX_PCR applicable to SEROTYPING, where the other five
		// PNEU techniques sit on SEROGROUPING. Reading only serogrouping tests would leave this
		// code unreachable, so PneuSql reads both.
		assertEquals("MPCR", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.MULTIPLEX_PCR));
	}

	@Test
	@DisplayName("a technique EpiPulse does not name is reported as OTH, not dropped")
	public void anUnnamedMethodBecomesOther() {

		// it is still a technique that was used to serotype, which is what OTH says. Contrast
		// EpipulsePathogenTestTypeRef, where an unmapped test type is not reported at all.
		for (SerotypingMethod method : NOT_A_PNEU_METHOD) {
			assertEquals("OTH", EpipulsePneumococcalSerotypingMethodRef.resolve(method), method.name());
		}
	}

	@Test
	@DisplayName("codeFor and resolve disagree on purpose")
	public void lookupAndResolveAreDifferentQuestions() {

		// "does EpiPulse name this?" and "what do we report for it?" -- kept apart so the tripwire
		// above can ask the first without the fallback answering for it
		assertNull(EpipulsePneumococcalSerotypingMethodRef.codeFor(SerotypingMethod.AGGLUTINATION));
		assertEquals("OTH", EpipulsePneumococcalSerotypingMethodRef.resolve(SerotypingMethod.AGGLUTINATION));
	}

	@Test
	@DisplayName("nothing recorded is not a method")
	public void nothingRecordedIsNull() {

		assertNull(EpipulsePneumococcalSerotypingMethodRef.codeFor(null));
		assertNull(EpipulsePneumococcalSerotypingMethodRef.resolve(null));
	}
}
