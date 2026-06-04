/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;

public enum PathogenTestType {

	// ----------------------------------------------------------------------------------------------
	// Legacy / merged methods: kept for case classification, external-message mapping, exports and so
	// existing records still render, but @NotSelectableForNewTests removes them from the new-test
	// method picker. Their category is still declared so a saved record can be grouped when displayed.
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@NotSelectableForNewTests
	ANTIBODY_DETECTION,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@NotSelectableForNewTests
	ANTIGEN_DETECTION,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.DENGUE,
		Disease.MALARIA }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@NotSelectableForNewTests
	CULTURE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@NotSelectableForNewTests
	ISOLATION,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@NotSelectableForNewTests
	IGM_SERUM_ANTIBODY,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@NotSelectableForNewTests
	IGG_SERUM_ANTIBODY,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@NotSelectableForNewTests
	IGA_SERUM_ANTIBODY,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@NotSelectableForNewTests
	INCUBATION_TIME,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@NotSelectableForNewTests
	MICROSCOPY,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@NotSelectableForNewTests
	LATEX_AGGLUTINATION,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@NotSelectableForNewTests
	CQ_VALUE_DETECTION,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@NotSelectableForNewTests
	SEQUENCING,

	// Legacy "Other <category>" placeholders introduced for Malaria/Dengue (#13801/#13814). Superseded
	// by the specific methods below, but kept (hidden from new tests) because they shipped and may be
	// stored on existing records (@Enumerated(STRING)). Records using them must still load and render.
	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@NotSelectableForNewTests
	OTHER_ANTIGEN_DETECTION_TEST,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@NotSelectableForNewTests
	OTHER_MOLECULAR_ASSAY,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@NotSelectableForNewTests
	OTHER_SEROLOGICAL_TEST,

	// ----------------------------------------------------------------------------------------------
	// Molecular Assays
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText
	PCR_RT_PCR,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	Q_PCR,

	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	MULTIPLEX_PCR,

	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	DIGITAL_PCR,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	LAMP,

	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	NASBA,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	TMA,

	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	CRISPR_DIAGNOSTICS,

	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	LINE_PROBE_ASSAY,

	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	SANGER_SEQUENCING,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	WHOLE_GENOME_SEQUENCING,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	DNA_MICROARRAY,

	@Diseases(value = {
		Disease.DENGUE,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	NAAT,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	MULTILOCUS_SEQUENCE_TYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	CGMLST,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	SNP_TYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	SEROTYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	SEROGROUPING,

	@Diseases(value = {
		Disease.MEASLES,
		Disease.CRYPTOSPORIDIOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	GENOTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	BEIJINGGENOTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	SPOLIGOTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	MIRU_PATTERN_CODE,

	// ----------------------------------------------------------------------------------------------
	// Serological Tests
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	ENZYME_LINKED_IMMUNOSORBENT_ASSAY,

	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	WESTERN_BLOT,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	NEUTRALIZING_ANTIBODIES,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	INDIRECT_FLUORESCENT_ANTIBODY,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES })
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	DIRECT_FLUORESCENT_ANTIBODY,

	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	RAPID_ANTIBODY_TEST,

	// ----------------------------------------------------------------------------------------------
	// Antigen Detection
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	RAPID_ANTIGEN_DETECTION,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	RAPID_TEST,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	LATERAL_FLOW_ASSAY,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	IMMUNOFLUORESCENCE_ASSAY,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	SLIDE_AGGLUTINATION,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	QUELLUNG_REACTION,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	HEMAGGLUTINATION_INHIBITION,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	RDT,

	// ----------------------------------------------------------------------------------------------
	// Culture & Isolation
	// ----------------------------------------------------------------------------------------------

	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	BACTERIAL_CULTURE,

	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	VIRAL_ISOLATION,

	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	FUNGAL_CULTURE,

	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	MALDI_TOF,

	// ----------------------------------------------------------------------------------------------
	// Microscopy & Staining
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	GRAM_STAIN,

	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	ACID_FAST_STAIN,

	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	DARK_FIELD_MICROSCOPY,

	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	GIEMSA_STAIN,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	HISTOPATHOLOGY,

	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	FISH,

	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	IMMUNOHISTOCHEMISTRY,

	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	ELECTRON_MICROSCOPY,

	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	QUANTITATIVE_BUFFY_COAT,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	THICK_BLOOD_SMEAR,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	THIN_BLOOD_SMEAR,

	// ----------------------------------------------------------------------------------------------
	// Antimicrobial Susceptibility Testing
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIMICROBIAL_SUSCEPTIBILITY_TESTING)
	ANTIBIOTIC_SUSCEPTIBILITY,

	@PathogenTestCategoryRel(PathogenTestCategory.ANTIMICROBIAL_SUSCEPTIBILITY_TESTING)
	GENOTYPIC_RESISTANCE_TEST,

	// ----------------------------------------------------------------------------------------------
	// Functional Immune Assays
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS)
	IGRA,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS)
	TST,

	@PathogenTestCategoryRel(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS)
	FLOW_CYTOMETRY,

	// ----------------------------------------------------------------------------------------------
	// No category: reveals a free-text companion field
	// ----------------------------------------------------------------------------------------------

	@RevealsTestTypeText
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static String toString(PathogenTestType value, String details) {
		return toString(value, details, null);
	}

	public static String toString(PathogenTestType value, String details, Disease disease) {
		if (value == null) {
			return "";
		}

		if (value == PathogenTestType.OTHER && !DataHelper.isNullOrEmpty(details)) {
			return details;
		}

		if (revealsTestTypeText(value, disease) && !DataHelper.isNullOrEmpty(details)) {
			return value + " (" + details + ")";
		}

		return value.toString();
	}

	/**
	 * @return true when picking {@code testType} should reveal the {@code PathogenTestDto.testTypeText} free-text
	 *         companion field. The decision is data-driven via {@link RevealsTestTypeText} on the enum value; values
	 *         with no annotation never reveal the field, values annotated without a disease list reveal it for every
	 *         disease, and values with a disease list reveal it only when {@code disease} is one of those listed.
	 */
	public static boolean revealsTestTypeText(PathogenTestType testType, Disease disease) {
		if (testType == null) {
			return false;
		}
		try {
			RevealsTestTypeText annotation = PathogenTestType.class.getField(testType.name()).getAnnotation(RevealsTestTypeText.class);
			if (annotation == null) {
				return false;
			}
			Disease[] diseases = annotation.diseases();
			if (diseases.length == 0) {
				return true;
			}
			for (Disease d : diseases) {
				if (d == disease) {
					return true;
				}
			}
			return false;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

	/**
	 * @return the {@link PathogenTestCategory} this method belongs to, declared via
	 *         {@link PathogenTestCategoryRel}, or {@code null} for values with no category (e.g. the
	 *         free-text {@code OTHER}). The category is derived from the method so it can be
	 *         re-selected when editing an existing test without persisting a separate column.
	 */
	public static PathogenTestCategory getCategory(PathogenTestType testType) {
		if (testType == null) {
			return null;
		}
		try {
			PathogenTestCategoryRel annotation = PathogenTestType.class.getField(testType.name()).getAnnotation(PathogenTestCategoryRel.class);
			return annotation == null ? null : annotation.value();
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	/**
	 * @return false when {@code testType} is a legacy or merged method that must not be offered when
	 *         adding a new pathogen test (marked with {@link NotSelectableForNewTests}). Such values
	 *         are kept for classification, mapping and so existing records still render; they are only
	 *         filtered out of the new-test method picker.
	 */
	public static boolean isSelectableForNewTests(PathogenTestType testType) {
		if (testType == null) {
			return false;
		}
		try {
			return PathogenTestType.class.getField(testType.name()).getAnnotation(NotSelectableForNewTests.class) == null;
		} catch (NoSuchFieldException e) {
			return true;
		}
	}
}
