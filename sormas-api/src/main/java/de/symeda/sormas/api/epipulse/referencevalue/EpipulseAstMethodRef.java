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

import java.util.Collection;

import de.symeda.sormas.api.therapy.SusceptibilityMethod;

/**
 * EpiPulse reference values for PNEU {@code ASTMethod}, mapped to
 * {@link SusceptibilityMethod}.
 *
 * <p>
 * EpiPulse expects one case-level method, while SORMAS stores methods per drug. The collapse rule
 * is implemented in {@link #resolve(Collection)}.
 */
public enum EpipulseAstMethodRef {

	AGARDIL(SusceptibilityMethod.AGAR_DILUTION),
	AUTOM(SusceptibilityMethod.AUTOMATED_MIC),
	BROTHDIL(SusceptibilityMethod.BROTH_MICRODILUTION),
	/** "Antimicrobial gradient (E-test, etc)" in EpiPulse terminology. */
	GRAD(SusceptibilityMethod.ETEST),
	/**
	 * SORMAS {@code OTHER}; also fallback for multi-method/unmapped panels.
	 */
	OTH(SusceptibilityMethod.OTHER);

	private final SusceptibilityMethod susceptibilityMethod;

	EpipulseAstMethodRef(SusceptibilityMethod susceptibilityMethod) {
		this.susceptibilityMethod = susceptibilityMethod;
	}

	public SusceptibilityMethod getSusceptibilityMethod() {
		return susceptibilityMethod;
	}

	/**
	 * Returns the EpiPulse code for one recorded method, or {@code null} if unmapped.
	 */
	public static String codeFor(SusceptibilityMethod susceptibilityMethod) {

		if (susceptibilityMethod == null) {
			return null;
		}

		for (EpipulseAstMethodRef ref : values()) {
			if (ref.susceptibilityMethod == susceptibilityMethod) {
				return ref.name();
			}
		}

		return null;
	}

	/**
	 * Resolves case-level {@code ASTMethod} from a drug-level method set.
	 *
	 * <p>
	 * Rules:
	 * <ul>
	 * <li>no non-null method -> {@code null}</li>
	 * <li>all non-null methods agree and are mapped -> mapped code</li>
	 * <li>all non-null methods agree but unmapped -> {@code OTH}</li>
	 * <li>non-null methods disagree -> {@code OTH}</li>
	 * </ul>
	 * Null entries are ignored.
	 */
	public static String resolve(Collection<SusceptibilityMethod> methods) {

		if (methods == null) {
			return null;
		}

		SusceptibilityMethod agreed = null;
		for (SusceptibilityMethod method : methods) {
			if (method == null) {
				continue;
			}
			if (agreed == null) {
				agreed = method;
			} else if (agreed != method) {
				return OTH.name();
			}
		}

		if (agreed == null) {
			return null;
		}

		String code = codeFor(agreed);

		return code != null ? code : OTH.name();
	}
}
