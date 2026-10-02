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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.symptoms.syphilis.SyphilisStage;

public class EpipulseStageSyphDetailedRefTest {

	/**
	 * Stages SORMAS records that EpiPulse has no value for.
	 *
	 * <p>
	 * {@code TERTIARY} and {@code NEUROLOGICAL} are deliberately not merged into {@code LL}.
	 * If EpiPulse adds values for them, they move out of this set.
	 */
	private static final Set<SyphilisStage> NO_EPIPULSE_VALUE =
		EnumSet.of(SyphilisStage.TERTIARY_SYPHILIS, SyphilisStage.NEUROLOGICAL_SYPHILIS, SyphilisStage.UNKNOWN);

	@Test
	@DisplayName("every constant is accounted for: a stage EpiPulse names maps, anything else does not")
	public void everyStageIsAccountedFor() {

		for (SyphilisStage stage : SyphilisStage.values()) {
			if (NO_EPIPULSE_VALUE.contains(stage)) {
				assertNull(EpipulseStageSyphDetailedRef.codeFor(stage), stage.name() + " must not be reported as a detailed stage");
				continue;
			}
			assertNotNull(
				EpipulseStageSyphDetailedRef.codeFor(stage),
				stage.name() + " is not accounted for. Map it in EpipulseStageSyphDetailedRef if EpiPulse names it, "
					+ "or add it to NO_EPIPULSE_VALUE here with the reason.");
		}
	}

	@Test
	@DisplayName("each stage carries the code EpiPulse defines for it")
	public void eachStageCarriesItsCode() {

		assertEquals("P", EpipulseStageSyphDetailedRef.codeFor(SyphilisStage.PRIMARY_SYPHILIS));
		assertEquals("S", EpipulseStageSyphDetailedRef.codeFor(SyphilisStage.SECONDARY_SYPHILIS));
		assertEquals("EL", EpipulseStageSyphDetailedRef.codeFor(SyphilisStage.EARLY_LATENT_SYPHILIS));
		assertEquals("LL", EpipulseStageSyphDetailedRef.codeFor(SyphilisStage.LATE_LATENT_SYPHILIS));
	}

	@Test
	@DisplayName("EpiPulse's unqualified Latent code is unreachable, because SORMAS always qualifies it")
	public void theUnqualifiedLatentCodeIsUnreachable() {

		// EpiPulse defines L = Latent, but SORMAS asks whether latency is early or late and has no
		// plain latent constant. Nothing can produce L, and this records that it is a gap in the
		// source rather than a mapping someone forgot to write.
		for (SyphilisStage stage : SyphilisStage.values()) {
			assertNotEquals("L", EpipulseStageSyphDetailedRef.codeFor(stage), stage.name() + " produced the unqualified latent code");
		}
	}

	@Test
	@DisplayName("nothing recorded reports nothing")
	public void nothingRecordedReportsNothing() {
		assertNull(EpipulseStageSyphDetailedRef.codeFor(null));
	}
}
