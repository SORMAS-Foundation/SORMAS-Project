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

package de.symeda.sormas.api.epipulse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;

/**
 * Laboratory-only mappers from SORMAS values to EpiPulse codes (specimen, result, AST, MIC).
 *
 * <p>
 * Scope is intentional: generic enum-based variables live in reference-value classes, and
 * disease/symptom-derived variables stay in disease field models.
 */
public class EpipulseLaboratoryMapper {

	/**
	 * Maps {@link SampleMaterial} to the general EpiPulse specimen code set.
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
		case CLINICAL_SAMPLE:
		case OTHER:
			return "OTH";
		case SALIVA:
			return "SALOR"; // Saliva/oral fluid
		case EDTA_WHOLE_BLOOD:
			return "EDTA"; // EDTA whole blood
		case DRY_BLOOD:
			return "DRYBLOSP"; // Dry blood spot
		default:
			return null;
		}
	}

	/**
	 * Maps {@link PathogenTestResultType} to EpiPulse POS/NEG/EQUI/NOTEST.
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

	// ==================== IPI-Specific Mappers ====================

	/**
	 * Maps {@link SampleMaterial} to IPI specimen codes.
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

	// ==================== Enteric-Specific Mappers ====================

	/**
	 * Maps {@link SampleMaterial} to the 6-value enteric specimen set (SALM/CAMP/SHIG/STEC).
	 *
	 * <p>
	 * Unmapped materials default to {@code OTH} (except {@code UNKNOWN} -> null), because the
	 * enteric value set is intentionally narrow. Blood variants map to {@code BLOOD};
	 * {@code URINE_PM} remains {@code URINE} for backward data compatibility.
	 */
	public static String mapSampleMaterialToEntericSpecimenCode(SampleMaterial sampleMaterial) {
		if (sampleMaterial == null) {
			return null;
		}

		switch (sampleMaterial) {
		case BLOOD:
		case EDTA_WHOLE_BLOOD:
		case DRY_BLOOD:
		case CORD_BLOOD:
			return "BLOOD";
		case CEREBROSPINAL_FLUID:
			return "CSF";
		case STOOL:
			return "FAECES";
		case PUS:
			return "PUS";
		case URINE:
		case URINE_PM:
			return "URINE";
		case UNKNOWN:
			return null;
		default:
			return "OTH";
		}
	}

	/**
	 * Maps susceptibility test outcome to EpiPulse RESIST/SENS/INTER/NOTEST.
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
	 * Maps susceptibility text to EpiPulse SIR codes ({@code S}, {@code I}, {@code R}).
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

	// ==================== MIC ====================

	/**
	 * Maps sign spellings seen in MIC free text to EpiPulse MICSign codes.
	 * Order matters: composite forms must be matched before single-char/shorter forms.
	 */
	private static final String[][] MIC_SIGN_FORMS = {
		{
			"<=",
			"<=",
			"=<",
			"\u2264",
			"\u2A7D",
			"\u2266",
			"lte",
			"le" },
		{
			">=",
			">=",
			"=>",
			"\u2265",
			"\u2A7E",
			"\u2267",
			"gte",
			"ge" },
		{
			"<",
			"<",
			"lt" },
		{
			">",
			">",
			"gt" },
		{
			"=",
			"==",
			"=",
			"eq" } };

	/**
	 * Number at text start (dot/comma decimals allowed). Anchored to avoid parsing unrelated numbers
	 * from the middle of free text.
	 */
	private static final Pattern MIC_NUMBER = Pattern.compile("^\\s*(\\d+(?:[.,]\\d+)?|[.,]\\d+)");

	/**
	 * Parsed MIC reading: optional sign and optional numeric value from one free-text field.
	 * If no numeric value is found, sign is also treated as absent.
	 */
	public static final class MicReading {

		private static final MicReading NONE = new MicReading(null, null);

		private final String sign;
		private final Double value;

		private MicReading(String sign, Double value) {
			this.sign = sign;
			this.value = value;
		}

		/** @return parsed EpiPulse MIC sign code, or {@code null} */
		public String getSign() {
			return sign;
		}

		/** @return parsed MIC value, or {@code null} */
		public Double getValue() {
			return value;
		}
	}

	/**
	 * Parses MIC free text (for example {@code "<=0.125"}, {@code "> 32"}, {@code "0,5"}) into
	 * sign and numeric value. Values are read from text as written; no range-based sign inference.
	 *
	 * @return never {@code null}; missing pieces are represented as {@code null} members
	 */
	public static MicReading parseMic(String micText) {

		if (micText == null) {
			return MicReading.NONE;
		}

		String text = micText.trim();
		String sign = null;

		for (String[] form : MIC_SIGN_FORMS) {
			String matched = matchSign(text, form);
			if (matched != null) {
				sign = form[0];
				text = text.substring(matched.length());
				break;
			}
		}

		Matcher number = MIC_NUMBER.matcher(text);
		if (!number.find()) {
			return MicReading.NONE;
		}

		return new MicReading(sign, Double.valueOf(number.group(1).replace(',', '.')));
	}

	/**
	 * Returns the matched sign spelling at text start, or {@code null}. Word forms must end before
	 * another letter.
	 */
	private static String matchSign(String text, String[] form) {

		String lower = text.toLowerCase(java.util.Locale.ROOT);

		for (int i = 1; i < form.length; i++) {
			String candidate = form[i];
			if (!lower.startsWith(candidate)) {
				continue;
			}
			boolean isWord = Character.isLetter(candidate.charAt(0));
			if (isWord && lower.length() > candidate.length() && Character.isLetter(lower.charAt(candidate.length()))) {
				continue;
			}
			return candidate;
		}

		return null;
	}
}
