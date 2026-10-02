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

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectiousness;

public class EpipulseStageSyphRefTest {

	/** EpiPulse offers Infectious and Not infectious and nothing for "not established". */
	private static final Set<SyphilisInfectiousness> NOT_ESTABLISHED = EnumSet.of(SyphilisInfectiousness.UNKNOWN);

	@Test
	@DisplayName("every constant is accounted for: an infectiousness maps, anything else does not")
	public void everyInfectiousnessIsAccountedFor() {

		for (SyphilisInfectiousness infectiousness : SyphilisInfectiousness.values()) {
			if (NOT_ESTABLISHED.contains(infectiousness)) {
				assertNull(EpipulseStageSyphRef.codeFor(infectiousness), infectiousness.name() + " must not be reported as a stage");
				continue;
			}
			assertNotNull(
				EpipulseStageSyphRef.codeFor(infectiousness),
				infectiousness.name() + " is not accounted for. Map it in EpipulseStageSyphRef if EpiPulse names it, "
					+ "or add it to NOT_ESTABLISHED here.");
		}
	}

	@Test
	@DisplayName("the two stages carry the codes EpiPulse defines")
	public void theTwoStagesCarryTheirCodes() {

		assertEquals("I", EpipulseStageSyphRef.codeFor(SyphilisInfectiousness.INFECTIOUS));
		assertEquals("NI", EpipulseStageSyphRef.codeFor(SyphilisInfectiousness.NOT_INFECTIOUS));
	}

	@Test
	@DisplayName("nothing recorded reports nothing")
	public void nothingRecordedReportsNothing() {
		assertNull(EpipulseStageSyphRef.codeFor(null));
	}
}
