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

import static de.symeda.sormas.api.epipulse.EpipulseSubjectCode.CHIK;
import static de.symeda.sormas.api.epipulse.EpipulseSubjectCode.DENGUE;
import static de.symeda.sormas.api.epipulse.EpipulseSubjectCode.PERT;
import static de.symeda.sormas.api.epipulse.EpipulseSubjectCode.WNF;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestType;

/**
 * Subject-code-specific mapping from {@link PathogenTestType} to EpiPulse
 * {@code PathogenDetectionMethod}.
 *
 * <p>
 * The same test type can map differently per subject code. PNEU is handled separately by
 * {@link EpipulsePneumococcalSerotypingMethodRef}.
 */
public enum EpipulsePathogenTestTypeRef {

	// --- CHIK: ISOV, NEU, NUC, OTH, SCONV, SIGM ---
	CHIK_ISOV(CHIK, "ISOV", Types.VIRUS_ISOLATION),
	CHIK_NEU(CHIK, "NEU", Types.NEUTRALISATION),
	CHIK_NUC(CHIK, "NUC", Types.NUCLEIC_ACID_AMPLIFICATION),
	CHIK_SIGM(CHIK, "SIGM", Types.IGM),
	CHIK_SCONV(CHIK, "SCONV"),
	CHIK_OTH(CHIK, "OTH"),

	// --- DENGUE: ANTIGEN, ISOV, NEU, NUC, OTH, SCONV, SIGM ---
	DENGUE_ANTIGEN(DENGUE, "ANTIGEN", Types.ANTIGEN_DETECTION),
	DENGUE_ISOV(DENGUE, "ISOV", Types.VIRUS_ISOLATION),
	DENGUE_NEU(DENGUE, "NEU", Types.NEUTRALISATION),
	DENGUE_NUC(DENGUE, "NUC", Types.NUCLEIC_ACID_AMPLIFICATION),
	DENGUE_SIGM(DENGUE, "SIGM", Types.IGM),
	DENGUE_SCONV(DENGUE, "SCONV"),
	DENGUE_OTH(DENGUE, "OTH"),

	// --- PERT: CULT, ORALIgG, PCR, SERO ---
	PERT_CULT(PERT, "CULT", Types.CULTURE),
	PERT_PCR(PERT, "PCR", Types.NUCLEIC_ACID_AMPLIFICATION),
	PERT_SERO(PERT, "SERO", Types.SEROLOGY),
	PERT_ORALIgG(PERT, "ORALIgG"),

	// --- WNF: ISOV, NEU, NUC, OTH, SIGM ---
	WNF_ISOV(WNF, "ISOV", Types.VIRUS_ISOLATION),
	WNF_NEU(WNF, "NEU", Types.NEUTRALISATION),
	WNF_NUC(WNF, "NUC", Types.NUCLEIC_ACID_AMPLIFICATION),
	WNF_SIGM(WNF, "SIGM", Types.IGM),
	WNF_OTH(WNF, "OTH");

	private final EpipulseSubjectCode subjectCode;
	private final String code;
	private final PathogenTestType[] testTypes;

	EpipulsePathogenTestTypeRef(EpipulseSubjectCode subjectCode, String code, PathogenTestType... testTypes) {
		this.subjectCode = subjectCode;
		this.code = code;
		this.testTypes = testTypes;
	}

	public EpipulseSubjectCode getSubjectCode() {
		return subjectCode;
	}

	/** EpiPulse code (not enum name, which includes subject-code prefix). */
	public String getCode() {
		return code;
	}

	public PathogenTestType[] getTestTypes() {
		return testTypes.clone();
	}

	/** Returns mapped code for this subject/test-type pair, or {@code null}. */
	public static String codeFor(EpipulseSubjectCode subjectCode, PathogenTestType testType) {

		if (subjectCode == null || testType == null) {
			return null;
		}

		for (EpipulsePathogenTestTypeRef ref : values()) {
			if (ref.subjectCode == subjectCode) {
				for (PathogenTestType candidate : ref.testTypes) {
					if (candidate == testType) {
						return ref.code;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns all mapped test types for a subject code.
	 * Used as filter for this variable.
	 */
	public static List<PathogenTestType> getPathogenTestTypesByDisease(EpipulseSubjectCode subjectCode) {

		List<PathogenTestType> testTypes = new ArrayList<>();
		if (subjectCode == null) {
			return testTypes;
		}

		for (EpipulsePathogenTestTypeRef ref : values()) {
			if (ref.subjectCode == subjectCode) {
				for (PathogenTestType testType : ref.testTypes) {
					if (!testTypes.contains(testType)) {
						testTypes.add(testType);
					}
				}
			}
		}

		return testTypes;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	/**
	 * Test-type families used by enum constants.
	 * Kept nested because enum constants cannot reference later-initialized static fields.
	 */
	private static final class Types {

		private Types() {
		}

		/** Nucleic acid amplification techniques. */
		static final PathogenTestType[] NUCLEIC_ACID_AMPLIFICATION = {
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.Q_PCR,
			PathogenTestType.MULTIPLEX_PCR,
			PathogenTestType.DIGITAL_PCR,
			PathogenTestType.NAAT,
			PathogenTestType.LAMP,
			PathogenTestType.NASBA,
			PathogenTestType.TMA };

		/** Culture-based organism growth methods. */
		static final PathogenTestType[] CULTURE = {
			PathogenTestType.CULTURE,
			PathogenTestType.BACTERIAL_CULTURE };

		/** Virus isolation methods. */
		static final PathogenTestType[] VIRUS_ISOLATION = {
			PathogenTestType.VIRAL_ISOLATION,
			PathogenTestType.ISOLATION };

		/** Serology methods. */
		static final PathogenTestType[] SEROLOGY = {
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.IGG_SERUM_ANTIBODY,
			PathogenTestType.IGA_SERUM_ANTIBODY,
			PathogenTestType.ANTIBODY_DETECTION,
			PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
			PathogenTestType.WESTERN_BLOT,
			PathogenTestType.OTHER_SEROLOGICAL_TEST };

		/** IgM-only methods. */
		static final PathogenTestType[] IGM = {
			PathogenTestType.IGM_SERUM_ANTIBODY };

		static final PathogenTestType[] NEUTRALISATION = {
			PathogenTestType.NEUTRALIZING_ANTIBODIES };

		static final PathogenTestType[] ANTIGEN_DETECTION = {
			PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
			PathogenTestType.LATERAL_FLOW_ASSAY };

		static final PathogenTestType[] SEQUENCING = {
			PathogenTestType.SEQUENCING,
			PathogenTestType.SANGER_SEQUENCING,
			PathogenTestType.WHOLE_GENOME_SEQUENCING };
	}
}
