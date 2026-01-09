/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.epipulse;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Utility class for mapping SORMAS laboratory and epidemiology data to EpiPulse codes for MEAS export.
 * Based on metadata analysis from 20250929_EpiPulse_CasesMetadata_mapped.xlsx
 */
public class EpipulseLaboratoryMapper {

	/**
	 * Maps SORMAS SampleMaterial enum to EpiPulse specimen type codes.
	 * <p>
	 * EpiPulse Reference Values:
	 * - DRYBLOSP = Dry blood spot
	 * - EDTA = EDTA whole blood
	 * - NASALSWAB = Nasal swab
	 * - OTH = Other
	 * - SALOR = Saliva/oral fluid
	 * - SER = Serum
	 * - URINE = Urine
	 *
	 * @param sampleMaterial
	 *            SORMAS sample material enum
	 * @return EpiPulse specimen type code, or null if not mappable
	 */
	public static String mapSampleMaterialToEpipulseCode(SampleMaterial sampleMaterial) {
		if (sampleMaterial == null) {
			return null;
		}

		switch (sampleMaterial) {
		case BLOOD:
		case SERA:
			return "SER"; // Serum
		case URINE:
			return "URINE";
		case NASAL_SWAB:
			return "NASALSWAB";
		case THROAT_SWAB:
		case RECTAL_SWAB:
		case OTHER:
			return "OTH";
		case SALIVA:
			return "SALOR"; // Saliva/oral fluid
		case EDTA_WHOLE_BLOOD:
			return "EDTA"; // EDTA whole blood
		default:
			return "OTH";
		}
	}

	/**
	 * Maps SORMAS PathogenTestResultType enum to EpiPulse test result codes.
	 * <p>
	 * EpiPulse Reference Values:
	 * - EQUI = Equivocal
	 * - NEG = Negative
	 * - NOTEST = Not tested
	 * - POS = Positive
	 *
	 * @param testResult
	 *            SORMAS test result enum
	 * @return EpiPulse result code (POS/NEG/EQUI/NOTEST)
	 */
	public static String mapTestResultToEpipulseCode(PathogenTestResultType testResult) {
		if (testResult == null) {
			return "NOTEST";
		}

		switch (testResult) {
		case POSITIVE:
			return "POS";
		case NEGATIVE:
			return "NEG";
		case INDETERMINATE:
			return "EQUI";
		case PENDING:
		case NOT_DONE:
			return "NOTEST";
		default:
			return "NOTEST";
		}
	}

	/**
	 * Validates and normalizes genotype string to match EpiPulse reference values.
	 * <p>
	 * EpiPulse accepts 49 measles virus genotypes: MEASV_A, MEASV_B1, MEASV_B2, MEASV_B3,
	 * MEASV_C1, MEASV_C2, MEASV_D1-D11, MEASV_E, MEASV_F, MEASV_G1-G3, MEASV_H1-H2, etc.
	 *
	 * @param genotypeText
	 *            SORMAS genotype string (from typingId or genoTypeResult)
	 * @return Normalized EpiPulse genotype code, or null if not a valid measles genotype
	 */
	public static String normalizeGenotypeForEpipulse(String genotypeText) {
		if (genotypeText == null || genotypeText.trim().isEmpty()) {
			return null;
		}

		String normalized = genotypeText.trim().toUpperCase();

		// If already in MEASV_ format, return as-is
		if (normalized.startsWith("MEASV_")) {
			return normalized;
		}

		// Try to parse formats like "A", "B1", "D10", etc. and add MEASV_ prefix
		// This matches a single uppercase letter optionally followed by digits
		if (normalized.matches("^[A-Z]\\d*$")) {
			return "MEASV_" + normalized;
		}

		// Try to extract genotype from common formats with delimiters
		// Matches patterns like "MeV-A", "Genotype-B1", "MV/A", "MEASLES-D4"
		String extracted = extractGenotypeFromDelimitedFormat(normalized);
		if (extracted != null) {
			return "MEASV_" + extracted;
		}

		// Return null for ambiguous or unparseable inputs
		return null;
	}

	/**
	 * Extracts genotype code from delimited formats like "MeV-A", "Genotype B1", etc.
	 * Uses strict pattern matching to avoid false positives.
	 *
	 * @param normalized
	 *            Uppercase normalized genotype string
	 * @return Extracted genotype code (e.g., "A", "B1", "D4"), or null if not extractable
	 */
	private static String extractGenotypeFromDelimitedFormat(String normalized) {
		// Match patterns like "PREFIX-A", "PREFIX/B1", "PREFIX_D10", "PREFIX A"
		// where PREFIX is some non-numeric text
		// This captures the genotype part after common delimiters
		String pattern = "(?:MEV|MEASLES?|GENOTYPE|MV)[-_/\\s]+([A-Z]\\d*)";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
		java.util.regex.Matcher m = p.matcher(normalized);

		if (m.find()) {
			return m.group(1);
		}

		return null;
	}

	/**
	 * Maps SORMAS ClusterType enum to EpiPulse cluster setting codes.
	 * <p>
	 * EpiPulse Reference Values:
	 * - CHILDCARE = Childcare setting
	 * - FAM = Family
	 * - MIL = Military
	 * - NOS = Nosocomial (healthcare)
	 * - OTH = Other
	 * - SCH = School
	 * - SPORT = Sports team
	 * - UNI = University
	 *
	 * @param clusterType
	 *            SORMAS cluster type enum
	 * @return EpiPulse cluster setting code
	 */
	public static String mapClusterTypeToEpipulseCode(ClusterType clusterType) {
		if (clusterType == null) {
			return null;
		}

		switch (clusterType) {
		case KINDERGARTEN_OR_CHILDCARE:
			return "CHILDCARE";
		case FAMILY:
			return "FAM";
		case MILITARY:
			return "MIL";
		case NOSOCOMIAL:
			return "NOS";
		case SCHOOL:
			return "SCH";
		case SPORTS_TEAM:
			return "SPORT";
		case UNIVERSITY:
			return "UNI";
		case OTHER:
			return "OTH";
		default:
			return null;
		}
	}

	/**
	 * Maps SORMAS CaseImportedStatus enum to EpiPulse imported status codes.
	 * <p>
	 * EpiPulse Reference Values:
	 * - AUTOCH = Autochthonous (not imported case)
	 * - IMP = Imported case
	 * - IMPR = Import-related case
	 * - UNK = Unknown importation status
	 *
	 * @param importedStatus
	 *            SORMAS case imported status enum
	 * @return EpiPulse imported status code
	 */
	public static String mapCaseImportedStatusToEpipulseCode(CaseImportedStatus importedStatus) {
		if (importedStatus == null) {
			return null;
		}

		switch (importedStatus) {
		case IMPORTED_CASE:
			return "IMP";
		case IMPORT_RELATED_CASE:
			return "IMPR";
		case UNKNOWN_IMPORTATION_STATUS:
			return "UNK";
		case NOT_IMPORTED_CASE:
			return "AUTOCH";
		default:
			return null;
		}
	}

	/**
	 * Maps SORMAS Symptoms complication fields to EpiPulse complication diagnosis codes.
	 * <p>
	 * EpiPulse Reference Values:
	 * - ACENCE = Acute encephalitis
	 * - DIARR = Diarrhea
	 * - NONE = No complications
	 * - OME = Otitis media
	 * - OTH = Other
	 * - PNEU = Pneumonia
	 *
	 * @param acuteEncephalitis
	 *            Acute encephalitis symptom state
	 * @param diarrhea
	 *            Diarrhea symptom state
	 * @param otitisMedia
	 *            Otitis media symptom state
	 * @param otherComplications
	 *            Other complications symptom state
	 * @return List of EpiPulse complication codes (empty list returns "NONE" in CSV)
	 */
	public static List<String> mapSymptomsToComplicationCodes(
		SymptomState acuteEncephalitis,
		SymptomState diarrhea,
		SymptomState otitisMedia,
		SymptomState otherComplications) {

		List<String> complications = new ArrayList<>();

		if (acuteEncephalitis == SymptomState.YES) {
			complications.add("ACENCE");
		}
		if (diarrhea == SymptomState.YES) {
			complications.add("DIARR");
		}
		if (otitisMedia == SymptomState.YES) {
			complications.add("OME");
		}
		if (otherComplications == SymptomState.YES) {
			complications.add("OTH");
		}

		// Note: PNEU (pneumonia) not currently available in SORMAS Symptoms for MEAS
		// If no complications found, empty list will result in "NONE" in CSV

		return complications;
	}

	/**
	 * Derives clinical criteria status from clinical confirmation field.
	 * Clinical criteria are considered met if case has clinical confirmation = YES.
	 *
	 * @param clinicalConfirmation
	 *            SORMAS clinical confirmation field
	 * @return true if clinically confirmed, false otherwise
	 */
	public static Boolean deriveClinicalCriteriaStatus(YesNoUnknown clinicalConfirmation) {
		return clinicalConfirmation == YesNoUnknown.YES;
	}
}
