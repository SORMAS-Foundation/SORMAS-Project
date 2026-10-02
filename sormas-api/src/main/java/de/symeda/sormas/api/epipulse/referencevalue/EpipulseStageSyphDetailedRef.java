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

import de.symeda.sormas.api.symptoms.syphilis.SyphilisStage;

/**
 * {@code StageSYPHdetailed} mapping from {@code symptoms.syphilisstage}.
 *
 * <p>
 * Detailed stage is reported directly (see {@link EpipulseStageSyphRef} for coarse stage).
 * SORMAS has no unqualified latent stage, so EpiPulse {@code L} is unreachable.
 * Tertiary and neurological stages are intentionally unmapped.
 */
public enum EpipulseStageSyphDetailedRef {

	P(SyphilisStage.PRIMARY_SYPHILIS),
	S(SyphilisStage.SECONDARY_SYPHILIS),
	EL(SyphilisStage.EARLY_LATENT_SYPHILIS),
	LL(SyphilisStage.LATE_LATENT_SYPHILIS);

	private final SyphilisStage stage;

	EpipulseStageSyphDetailedRef(SyphilisStage stage) {
		this.stage = stage;
	}

	public SyphilisStage getStage() {
		return stage;
	}

	/** Returns EpiPulse code for mapped stage, otherwise {@code null}. */
	public static String codeFor(SyphilisStage stage) {

		if (stage == null) {
			return null;
		}

		for (EpipulseStageSyphDetailedRef ref : values()) {
			if (ref.stage == stage) {
				return ref.name();
			}
		}

		return null;
	}
}
