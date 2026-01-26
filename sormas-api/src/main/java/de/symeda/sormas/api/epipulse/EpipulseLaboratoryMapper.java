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
			return null;
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
	 * @return EpiPulse result code (POS/NEG/EQUI/NOTEST), or null if input is null
	 */
	public static String mapTestResultToEpipulseCode(PathogenTestResultType testResult) {
		if (testResult == null) {
			return null;
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
			return null;
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

		// If already in MEASV_ format, validate the suffix and return if valid
		if (normalized.startsWith("MEASV_")) {
			String suffix = normalized.substring(6); // Extract part after "MEASV_"
			if (isValidMeaslesGenotype(suffix)) {
				return normalized;
			}
			return null;
		}

		// Try to parse formats like "A", "B1", "D10", etc. and add MEASV_ prefix
		// Measles genotypes are limited (e.g., A, B1-3, C1-2, D1-11, E, F, G1-3, H1-2)
		if (normalized.matches("^(A|B[1-3]|C[1-2]|D(1[0-1]|[1-9])|E|F|G[1-3]|H[1-2])$")) {
			return "MEASV_" + normalized;
		}

		// Try to extract genotype from common formats with delimiters
		// Matches patterns like "MeV-A", "Genotype-B1", "MV/A", "MEASLES-D4"
		String extracted = extractGenotypeFromDelimitedFormat(normalized);
		if (isValidMeaslesGenotype(extracted)) {
			return "MEASV_" + extracted;
		}

		// Return null for ambiguous or unparseable inputs
		return null;
	}

	/**
	 * Validates if a genotype code matches the known measles genotype pattern.
	 *
	 * @param genotype
	 *            Genotype code without MEASV_ prefix (e.g., "A", "B1", "D10")
	 * @return true if the genotype is a valid measles genotype, false otherwise
	 */
	private static boolean isValidMeaslesGenotype(String genotype) {
		if (genotype == null) {
			return false;
		}
		return genotype.matches("^(A|B[1-3]|C[1-2]|D(1[0-1]|[1-9])|E|F|G[1-3]|H[1-2])$");
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
		if (clinicalConfirmation == null || clinicalConfirmation == YesNoUnknown.UNKNOWN) {
			return null;
		}
		return clinicalConfirmation == YesNoUnknown.YES;
	}

	// ==================== IPI-Specific Mappers ====================

	/**
	 * Maps SORMAS SampleMaterial enum to EpiPulse IPI specimen type codes.
	 * <p>
	 * EpiPulse Reference Values for IPI:
	 * - BLOOD = Blood
	 * - CSF = Cerebrospinal fluid
	 * - OTH = Other
	 * - PLEURAL = Pleural fluid
	 * - SER = Serum
	 * - SYNOVIAL = Synovial fluid (joint fluid)
	 * - THROAT = Throat swab
	 * - NPSWAB = Nasopharyngeal swab
	 *
	 * @param sampleMaterial
	 *            SORMAS sample material enum
	 * @return EpiPulse IPI specimen type code, or null if not mappable
	 */
	public static String mapSampleMaterialToIpiSpecimenCode(SampleMaterial sampleMaterial) {
		if (sampleMaterial == null) {
			return null;
		}

		switch (sampleMaterial) {
		case BLOOD:
			return "BLOOD";
		case SERA:
			return "SER"; // Serum
		case CEREBROSPINAL_FLUID:
			return "CSF";
		case PLEURAL_FLUID:
			return "PLEURAL";
		case SYNOVIAL_FLUID:
			return "SYNOVIAL";
		case THROAT_SWAB:
			return "THROAT";
		case NP_SWAB:
			return "NPSWAB";
		case OTHER:
			return "OTH";
		default:
			return null;
		}
	}

	/**
	 * Maps SORMAS Symptoms to EpiPulse IPI clinical presentation codes.
	 * <p>
	 * EpiPulse Reference Values for IPI:
	 * - MENING = Meningitis
	 * - SEPT = Septicaemia
	 * - PNEUM = Pneumonia
	 * - OME = Otitis media
	 * - PERITON = Peritonitis
	 * - ARTH = Arthritis
	 * - OTH = Other
	 * - ASYMP = Asymptomatic
	 * - NONE = No clinical presentation recorded
	 *
	 * @param meningitis
	 *            Meningitis symptom state (IPI-specific)
	 * @param septicaemia
	 *            Septicaemia symptom state (IPI-specific)
	 * @param pneumonia
	 *            Pneumonia symptom state
	 * @param otitisMedia
	 *            Otitis media symptom state
	 * @param peritonitis
	 *            Peritonitis symptom state
	 * @param arthritis
	 *            Arthritis symptom state
	 * @param otherClinical
	 *            Other clinical presentation symptom state
	 * @param asymptomatic
	 *            Asymptomatic symptom state
	 * @return List of EpiPulse clinical presentation codes (empty list returns "NONE" in CSV)
	 */
	public static List<String> mapSymptomsToClinicalPresentation(
		SymptomState meningitis,
		SymptomState septicaemia,
		SymptomState pneumonia,
		SymptomState otitisMedia,
		SymptomState peritonitis,
		SymptomState arthritis,
		SymptomState otherClinical,
		SymptomState asymptomatic) {

		List<String> presentations = new ArrayList<>();

		// Priority: IPI-defining symptoms first
		if (meningitis == SymptomState.YES) {
			presentations.add("MENING");
		}
		if (septicaemia == SymptomState.YES) {
			presentations.add("SEPT");
		}
		if (pneumonia == SymptomState.YES) {
			presentations.add("PNEUM");
		}
		if (otitisMedia == SymptomState.YES) {
			presentations.add("OME");
		}
		if (peritonitis == SymptomState.YES) {
			presentations.add("PERITON");
		}
		if (arthritis == SymptomState.YES) {
			presentations.add("ARTH");
		}
		if (otherClinical == SymptomState.YES) {
			presentations.add("OTH");
		}
		if (asymptomatic == SymptomState.YES) {
			presentations.add("ASYMP");
		}

		// If no presentations found, empty list will result in "NONE" in CSV
		return presentations;
	}

	/**
	 * Maps SORMAS drug susceptibility test result to EpiPulse antibiotic resistance codes.
	 * <p>
	 * EpiPulse Reference Values:
	 * - RESIST = Resistant
	 * - SENS = Sensitive
	 * - INTER = Intermediate resistance
	 * - NOTEST = Not tested
	 *
	 * @param testResult
	 *            SORMAS PathogenTestResultType from drug susceptibility test
	 * @return EpiPulse antibiotic resistance code
	 */
	public static String mapDrugSusceptibilityToEpipulseCode(PathogenTestResultType testResult) {
		if (testResult == null) {
			return null;
		}

		switch (testResult) {
		case POSITIVE:
			return "RESIST"; // Positive drug susceptibility = Resistant
		case NEGATIVE:
			return "SENS"; // Negative drug susceptibility = Sensitive
		case INDETERMINATE:
			return "INTER"; // Intermediate resistance
		case PENDING:
		case NOT_DONE:
			return "NOTEST";
		default:
			return null;
		}
	}

	/**
	 * Validates and normalizes pneumococcal serotype string for EpiPulse export.
	 * <p>
	 * Pneumococcal serotypes include 90+ types: 1, 2, 3, 4, 5, 6A, 6B, 7F, 8, 9N, 9V, 10A, etc.
	 * Accepts formats like "6A", "19F", "23F", "SEROTYPE 6A", "TYPE 19F", etc.
	 *
	 * @param serotypeText
	 *            SORMAS serotype string (from PathogenTest typingId or serotype field)
	 * @return Normalized EpiPulse serotype code (e.g., "6A", "19F"), or null if invalid
	 */
	public static String normalizeSerotypeForEpipulse(String serotypeText) {
		if (serotypeText == null || serotypeText.trim().isEmpty()) {
			return null;
		}

		String normalized = serotypeText.trim().toUpperCase();

		// Remove common prefixes: "SEROTYPE ", "TYPE ", "S.", "PNEUMOCOCCAL ", etc.
		normalized = normalized.replaceAll("^(SEROTYPE|TYPE|S\\.|PNEUMOCOCCAL|STREPTOCOCCUS PNEUMONIAE)\\s*", "");

		// Validate format: digit(s) optionally followed by letter(s)
		// Examples: "1", "6A", "6B", "19F", "23F", "15A", "33F"
		if (normalized.matches("^\\d{1,2}[A-Z]{0,2}$")) {
			return normalized;
		}

		return null; // Invalid format
	}

	/**
	 * Maps SORMAS Symptoms to EpiPulse ClinicalCriteria codes for PNEU.
	 * <p>
	 * EpiPulse Reference Values for PNEU ClinicalCriteria:
	 * - BACTERPNEUMO = Bacteraemic pneumonia
	 * - MENI = Meningitis/Meningeal/Meningoencephalitic
	 * - MENISEPTI = Meningitis and septicaemia
	 * - OTH = Other
	 * - SEPTI = Septicaemia
	 *
	 * @param meningitis
	 *            Meningitis symptom state
	 * @param septicaemia
	 *            Septicaemia symptom state
	 * @param pneumonia
	 *            Pneumonia symptom state (clinical or radiologic)
	 * @return EpiPulse clinical criteria code, or null if no criteria met
	 */
	public static String mapSymptomsToClinicalCriteria(SymptomState meningitis, SymptomState septicaemia, SymptomState pneumonia) {

		boolean hasMeningitis = meningitis == SymptomState.YES;
		boolean hasSepticaemia = septicaemia == SymptomState.YES;
		boolean hasPneumonia = pneumonia == SymptomState.YES;

		// Priority order based on severity/specificity
		if (hasMeningitis && hasSepticaemia) {
			return "MENISEPTI"; // Both present
		} else if (hasMeningitis) {
			return "MENI"; // Meningitis only
		} else if (hasSepticaemia) {
			return "SEPTI"; // Septicaemia only
		} else if (hasPneumonia) {
			return "BACTERPNEUMO"; // Bacteraemic pneumonia
		}

		return null; // No specific clinical criteria met
	}

	/**
	 * Maps SORMAS Vaccine enum to EpiPulse vaccine codes for PNEU.
	 * <p>
	 * EpiPulse Reference Values for PNEU Vaccine:
	 * - PCV7 = Pneumococcal conjugate vaccine 7
	 * - PCV10 = Pneumococcal conjugate vaccine 10
	 * - PCV13 = Pneumococcal conjugate vaccine 13
	 * - PCV15 = Pneumococcal conjugate vaccine 15
	 * - PCV20 = Pneumococcal conjugate vaccine 20
	 * - PCV3 = Pneumococcal conjugate vaccine - third dose
	 * - PPV23 = Pneumococcal polysaccharide vaccine
	 *
	 * @param vaccineName
	 *            SORMAS vaccine name/type
	 * @return EpiPulse vaccine code, or null if not pneumococcal vaccine
	 */
	public static String mapVaccineToEpipulseCode(String vaccineName) {
		if (vaccineName == null || vaccineName.trim().isEmpty()) {
			return null;
		}

		String normalized = vaccineName.trim().toUpperCase();

		// Map PCV vaccines (conjugate vaccines)
		// Brand names: Prevenar/Prevnar (PCV7, PCV13, PCV20), Synflorix (PCV10), Vaxneuvance (PCV15)
		if (normalized.contains("PCV") || normalized.contains("CONJUGATE")
			|| normalized.contains("PREVENAR") || normalized.contains("PREVNAR")
			|| normalized.contains("SYNFLORIX") || normalized.contains("VAXNEUVANCE")) {
			if (normalized.contains("20")) {
				return "PCV20";
			} else if (normalized.contains("15") || normalized.contains("VAXNEUVANCE")) {
				return "PCV15";
			} else if (normalized.contains("13")) {
				return "PCV13";
			} else if (normalized.contains("10") || normalized.contains("SYNFLORIX")) {
				return "PCV10";
			} else if (normalized.contains("7")) {
				return "PCV7";
			} else if (normalized.contains("3")) {
				return "PCV3";
			}
			// Default PCV if no specific number (Prevenar without number is usually PCV13)
			return "PCV13";
		}

		// Map PPV vaccine (polysaccharide vaccines)
		// Brand names: Pneumovax 23 (PPV23)
		if (normalized.contains("PPV") || normalized.contains("POLYSACCHARIDE")
			|| normalized.contains("PNEUMOVAX") || normalized.contains("23")) {
			return "PPV23";
		}

		// Check for general pneumococcal terms
		if (normalized.contains("PNEUMO")) {
			return "PCV13"; // Default to most common PCV
		}

		return null; // Not a pneumococcal vaccine
	}

	/**
	 * Maps DrugSusceptibilityType enum to EpiPulse SIR codes.
	 * <p>
	 * EpiPulse Reference Values for SIR (Susceptible/Intermediate/Resistant):
	 * - S = Susceptible
	 * - I = Intermediate
	 * - R = Resistant
	 *
	 * @param susceptibility
	 *            SORMAS drug susceptibility enum
	 * @return EpiPulse SIR code (S/I/R), or null if not tested
	 */
	public static String mapDrugSusceptibilityToSIR(String susceptibility) {
		if (susceptibility == null || susceptibility.trim().isEmpty()) {
			return null;
		}

		String normalized = susceptibility.trim().toUpperCase();

		if (normalized.equals("SUSCEPTIBLE") || normalized.equals("S")) {
			return "S";
		} else if (normalized.equals("INTERMEDIATE") || normalized.equals("I")) {
			return "I";
		} else if (normalized.equals("RESISTANT") || normalized.equals("R")) {
			return "R";
		}

		return null; // Unknown susceptibility
	}

	/**
	 * Maps SORMAS PathogenTestType to EpiPulse PathogenDetectionMethod codes for PNEU.
	 * <p>
	 * EpiPulse Reference Values for PNEU PathogenDetectionMethod:
	 * - COAGG = Coagglutination
	 * - GDIFF = Gel diffusion
	 * - MPCR = Multiplex PCR
	 * - OTH = Other
	 * - PTEST = Pneumotest
	 * - QUE = Quellung
	 * - SLAGG = Slide agglutination
	 *
	 * @param testType
	 *            SORMAS pathogen test type
	 * @return EpiPulse detection method code, or null if not mappable
	 */
	public static String mapPathogenTestTypeToDetectionMethod(String testType) {
		if (testType == null || testType.trim().isEmpty()) {
			return null;
		}

		String normalized = testType.trim().toUpperCase();

		// Map specific test types
		if (normalized.contains("PCR") || normalized.contains("RT-PCR") || normalized.contains("RTPCR")) {
			if (normalized.contains("MULTIPLEX")) {
				return "MPCR"; // Multiplex PCR
			}
			return "MPCR"; // Default PCR to multiplex
		} else if (normalized.contains("QUELLUNG")) {
			return "QUE";
		} else if (normalized.contains("COAGG") || normalized.contains("CO-AGGLUTINATION")) {
			return "COAGG";
		} else if (normalized.contains("SLIDE") && normalized.contains("AGGLUT")) {
			return "SLAGG";
		} else if (normalized.contains("GEL") && normalized.contains("DIFF")) {
			return "GDIFF";
		} else if (normalized.contains("PNEUMOTEST")) {
			return "PTEST";
		} else if (normalized.contains("CULTURE")) {
			return "QUE"; // Culture often followed by Quellung
		} else if (normalized.contains("SEROGROUPING") || normalized.contains("SEROTYPING")) {
			return "QUE"; // Serotyping typically uses Quellung
		}

		return "OTH"; // Other methods
	}

	// ==================== MENI-Specific Mappers ====================

	/**
	 * Maps SORMAS meningococcal serogroup to EpiPulse MENI serogroup codes.
	 * <p>
	 * EpiPulse Reference Values for MENI Serogroup:
	 * - NEIMENI_A = Serogroup A
	 * - NEIMENI_B = Serogroup B
	 * - NEIMENI_C = Serogroup C
	 * - NEIMENI_W = Serogroup W (including W135)
	 * - NEIMENI_X = Serogroup X
	 * - NEIMENI_Y = Serogroup Y
	 * - NEIMENI_Z = Serogroup Z
	 * - NEIMENI_29E = Serogroup 29E
	 * - NEIMENI_Z29E = Serogroup Z/29E
	 * - NEIMENI_NGA = Non-groupable
	 * - NEIMENI_OTH = Other
	 *
	 * @param serogroupText
	 *            SORMAS serogroup text
	 * @return EpiPulse MENI serogroup code
	 */
	public static String normalizeSerogroupForMeni(String serogroupText) {
		if (serogroupText == null || serogroupText.trim().isEmpty()) {
			return null;
		}

		String normalized = serogroupText.trim().toUpperCase();

		// Remove common prefixes
		normalized = normalized.replaceAll("^(SEROGROUP|GROUP|NEISSERIA|N\\.?\\s*MENINGITIDIS)\\s*", "");

		// Map specific serogroups
		if (normalized.matches("^A$|^SEROGROUP\\s*A$")) {
			return "NEIMENI_A";
		} else if (normalized.matches("^B$|^SEROGROUP\\s*B$")) {
			return "NEIMENI_B";
		} else if (normalized.matches("^C$|^SEROGROUP\\s*C$")) {
			return "NEIMENI_C";
		} else if (normalized.matches("^W$|^W135$|^SEROGROUP\\s*W$")) {
			return "NEIMENI_W";
		} else if (normalized.matches("^X$|^SEROGROUP\\s*X$")) {
			return "NEIMENI_X";
		} else if (normalized.matches("^Y$|^SEROGROUP\\s*Y$")) {
			return "NEIMENI_Y";
		} else if (normalized.matches("^Z$|^SEROGROUP\\s*Z$")) {
			return "NEIMENI_Z";
		} else if (normalized.matches("^29E$|^SEROGROUP\\s*29E$")) {
			return "NEIMENI_29E";
		} else if (normalized.matches("^Z/29E$|^Z29E$")) {
			return "NEIMENI_Z29E";
		} else if (normalized.contains("NON") && normalized.contains("GROUP")) {
			return "NEIMENI_NGA"; // Non-groupable
		} else if (normalized.contains("NGA") || normalized.contains("NG")) {
			return "NEIMENI_NGA";
		}

		return "NEIMENI_OTH"; // Other
	}

	/**
	 * Maps SORMAS PathogenTestType to EpiPulse MENI detection method codes.
	 * <p>
	 * EpiPulse Reference Values for MENI PathogenDetectionMethod:
	 * - ANTIGEN = Antigen detection
	 * - CULT = Culture
	 * - GENOSEQ = Genome sequencing
	 * - MICRO = Microscopy
	 * - NUCLACID = Nucleic acid detection (PCR)
	 * - OTH = Other
	 * <p>
	 * SORMAS PathogenTestType mappings:
	 * - CULTURE -> CULT
	 * - PCR_RT_PCR, CQ_VALUE_DETECTION, DNA_MICROARRAY, TMA -> NUCLACID
	 * - ANTIGEN_DETECTION, RAPID_ANTIGEN_DETECTION, RAPID_TEST -> ANTIGEN
	 * - MICROSCOPY, GRAM_STAIN -> MICRO
	 * - WHOLE_GENOME_SEQUENCING, SEQUENCING, MULTILOCUS_SEQUENCE_TYPING, GENOTYPING -> GENOSEQ
	 *
	 * @param testType
	 *            SORMAS pathogen test type string (enum name)
	 * @return EpiPulse MENI detection method code
	 */
	public static String mapToMeniDetectionMethod(String testType) {
		if (testType == null || testType.trim().isEmpty()) {
			return null;
		}

		String normalized = testType.trim().toUpperCase();

		// Direct mapping of SORMAS PathogenTestType enum values
		switch (normalized) {
		case "CULTURE":
			return "CULT";

		case "PCR_RT_PCR":
		case "CQ_VALUE_DETECTION":
		case "DNA_MICROARRAY":
		case "TMA":
			return "NUCLACID";

		case "ANTIGEN_DETECTION":
		case "RAPID_ANTIGEN_DETECTION":
		case "RAPID_TEST":
			return "ANTIGEN";

		case "MICROSCOPY":
		case "GRAM_STAIN":
			return "MICRO";

		case "WHOLE_GENOME_SEQUENCING":
		case "SEQUENCING":
		case "MULTILOCUS_SEQUENCE_TYPING":
		case "GENOTYPING":
			return "GENOSEQ";

		default:
			// Fallback pattern matching for flexibility
			if (normalized.contains("CULTURE")) {
				return "CULT";
			} else if (normalized.contains("PCR") || normalized.contains("NUCLEIC")) {
				return "NUCLACID";
			} else if (normalized.contains("ANTIGEN")) {
				return "ANTIGEN";
			} else if (normalized.contains("MICRO") || normalized.contains("GRAM")) {
				return "MICRO";
			} else if (normalized.contains("GENOM") || normalized.contains("SEQUENC")) {
				return "GENOSEQ";
			}
			return "OTH";
		}
	}

	/**
	 * Normalizes MLST (Multi-Locus Sequence Typing) result for MENI.
	 * <p>
	 * MLST results should be in ST-* format (e.g., ST-11, ST-32, ST-269).
	 * EpiPulse accepts 75 different ST complex values.
	 *
	 * @param mlstText
	 *            SORMAS MLST result text
	 * @return Normalized ST-* format, or null if invalid
	 */
	public static String normalizeMlstForMeni(String mlstText) {
		if (mlstText == null || mlstText.trim().isEmpty()) {
			return null;
		}

		String normalized = mlstText.trim().toUpperCase();

		// Already in ST-* format
		if (normalized.startsWith("ST-")) {
			return normalized;
		}

		// Handle "ST " format (with space)
		if (normalized.startsWith("ST ")) {
			return "ST-" + normalized.substring(3);
		}

		// Handle bare number (e.g., "11" -> "ST-11")
		if (normalized.matches("^\\d+$")) {
			return "ST-" + normalized;
		}

		// Handle various formats like "MLST-11", "ST11", etc.
		String extracted = normalized.replaceAll("^(MLST|ST|SEQUENCE\\s*TYPE)[-\\s]*", "");
		if (extracted.matches("^\\d+$")) {
			return "ST-" + extracted;
		}

		return null; // Unable to normalize
	}

	/**
	 * Maps SORMAS CaseImportedStatus to EpiPulse MENI imported status codes.
	 * <p>
	 * EpiPulse Reference Values for MENI ImportedStatus:
	 * - IMP = Imported case
	 * - IMPREL = Import-related case
	 * - IMPUNK = Unknown import-relation
	 * - NOTIMP = Not imported
	 *
	 * @param importedStatus
	 *            SORMAS case imported status
	 * @return EpiPulse MENI imported status code
	 */
	public static String mapMeniImportedStatus(CaseImportedStatus importedStatus) {
		if (importedStatus == null) {
			return null;
		}

		switch (importedStatus) {
		case IMPORTED_CASE:
			return "IMP";
		case IMPORT_RELATED_CASE:
			return "IMPREL";
		case UNKNOWN_IMPORTATION_STATUS:
			return "IMPUNK";
		case NOT_IMPORTED_CASE:
			return "NOTIMP";
		default:
			return null;
		}
	}

	/**
	 * Maps SORMAS Symptoms to EpiPulse ClinicalCriteria codes for MENI.
	 * <p>
	 * EpiPulse Reference Values for MENI ClinicalCriteria:
	 * - MENI = Meningitis only
	 * - MENISEPTI = Meningitis and septicaemia
	 * - SEPTI = Septicaemia only
	 * - PNEU = Pneumonia
	 * - OTH = Other
	 *
	 * @param meningitis
	 *            Meningitis symptom state
	 * @param septicaemia
	 *            Septicaemia symptom state
	 * @param pneumonia
	 *            Pneumonia symptom state
	 * @return EpiPulse MENI clinical criteria code
	 */
	public static String mapSymptomsToClinicalCriteriaMeni(SymptomState meningitis, SymptomState septicaemia, SymptomState pneumonia) {

		boolean hasMeningitis = meningitis == SymptomState.YES;
		boolean hasSepticaemia = septicaemia == SymptomState.YES;
		boolean hasPneumonia = pneumonia == SymptomState.YES;

		if (hasMeningitis && hasSepticaemia) {
			return "MENISEPTI";
		} else if (hasMeningitis) {
			return "MENI";
		} else if (hasSepticaemia) {
			return "SEPTI";
		} else if (hasPneumonia) {
			return "PNEU";
		}

		return null; // No specific criteria or OTH
	}

	// ==================== MIC Sign Mapping ====================

	/** Standard lower boundary for MIC dilution series (mg/L) */
	public static final float MIC_DILUTION_LOWER_BOUND = 0.002f;

	/** Standard upper boundary for MIC dilution series (mg/L) */
	public static final float MIC_DILUTION_UPPER_BOUND = 32f;

	/**
	 * Maps MIC value to EpiPulse MIC sign code based on dilution range boundaries.
	 * <p>
	 * The MIC sign indicates the relationship between the measured value and the
	 * laboratory's dilution range. Values at the boundaries indicate the actual MIC
	 * may be beyond the tested range.
	 * <p>
	 * EpiPulse Reference Values for MIC Sign:
	 * - {@code <} = Less than
	 * - {@code <=} = Less than or equal (at or below lower dilution boundary)
	 * - {@code =} = Equal (exact measurement within dilution range)
	 * - {@code >} = Greater than
	 * - {@code >=} = Greater than or equal (at or above upper dilution boundary)
	 *
	 * @param micValue
	 *            The MIC value in mg/L
	 * @param lowerBound
	 *            The lower boundary of the dilution range (e.g., 0.002 mg/L)
	 * @param upperBound
	 *            The upper boundary of the dilution range (e.g., 32 mg/L)
	 * @return EpiPulse MIC sign code: {@code <=} if at/below lower bound,
	 *         {@code >=} if at/above upper bound, {@code =} otherwise, or null if micValue is null
	 */
	public static String mapMicSign(Float micValue, float lowerBound, float upperBound) {
		if (micValue == null) {
			return null;
		}

		if (micValue <= lowerBound) {
			return "<=";
		} else if (micValue >= upperBound) {
			return ">=";
		}
		return " = ";
	}

	/**
	 * Maps MIC value to EpiPulse MIC sign code using standard dilution range (0.002 - 32 mg/L).
	 * <p>
	 * This is a convenience method that uses the most common dilution range for
	 * antimicrobial susceptibility testing.
	 *
	 * @param micValue
	 *            The MIC value in mg/L
	 * @return EpiPulse MIC sign code, or null if micValue is null
	 * @see #mapMicSign(Float, float, float)
	 */
	public static String mapMicSign(Float micValue) {
		return mapMicSign(micValue, MIC_DILUTION_LOWER_BOUND, MIC_DILUTION_UPPER_BOUND);
	}
}
