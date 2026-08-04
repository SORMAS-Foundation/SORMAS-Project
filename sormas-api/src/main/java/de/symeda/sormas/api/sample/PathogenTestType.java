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

import com.fasterxml.jackson.annotation.JsonCreator;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.LegacyEnumHelper;
import de.symeda.sormas.api.utils.LegacyEnumNames;

public enum PathogenTestType {

	// ----------------------------------------------------------------------------------------------
	// Legacy / merged methods: kept for case classification, external-message mapping, exports and so
	// existing records still render, but @NotSelectableForNewTests removes them from the new-test
	// method picker. Their category is still declared so a saved record can be grouped when displayed.
	// ----------------------------------------------------------------------------------------------

	// Merged Culture entry (bacterial + fungal) — supersedes BACTERIAL_CULTURE / FUNGAL_CULTURE for new
	// tests (#13951). The qualitative result is the Pos/Neg outcome; TEXT carries the organism identifier
	// and NUMERIC carries the CFU/mL count. The legacy constants remain @NotSelectableForNewTests so
	// historic records and case-classification rules keep working.
	@Diseases(value = {
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.TEXT,
		ResultValueType.NUMERIC })
	CULTURE,

	// Merged Isolation entry (bacterial + viral) — supersedes VIRAL_ISOLATION for new tests (#13951).
	// Result is qualitative (Pos/Neg/Indet/Pending). Legacy VIRAL_ISOLATION stays @NotSelectableForNewTests
	// so historic records still render and case-classification rules (EVD/Lassa/Cholera/…) keep firing.
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	ISOLATION,

	// Resurrected ELISA Ig-class variants (#13951): split the legacy single ENZYME_LINKED_IMMUNOSORBENT_ASSAY
	// entry into per-Ig-class methods so the form can offer the IgM/IgG/IgA distinction that serology
	// workflows already need. Result is qualitative (Pos/Neg) + numeric (titre). No @Diseases — visible
	// for every disease per #13951. The legacy ENZYME_LINKED_IMMUNOSORBENT_ASSAY is now
	// @NotSelectableForNewTests so historic records still render and case-classification rules
	// referencing IGM_/IGG_SERUM_ANTIBODY continue to fire (they bind to the same enum constants).
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
	IGM_SERUM_ANTIBODY,

	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
	IGG_SERUM_ANTIBODY,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
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
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@NotSelectableForNewTests
	INCUBATION_TIME,

	// Resurrected for IMI new-test selection (#14034): scoped to Invasive Meningococcal Infection only.
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
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

	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
	PCR_RT_PCR,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
	Q_PCR,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	MULTIPLEX_PCR,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
	DIGITAL_PCR,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	LAMP,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
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
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	TMA,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	CRISPR_DIAGNOSTICS,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.BOOLEAN)
	LINE_PROBE_ASSAY,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	SANGER_SEQUENCING,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.TEXT)
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
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	DNA_MICROARRAY,

	@Diseases(value = {
		Disease.DENGUE,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
	NAAT,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	MULTILOCUS_SEQUENCE_TYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	CGMLST,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	SNP_TYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@RevealsTestTypeText(diseases = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS })
	@ResultValueTypeRel(ResultValueType.TEXT)
	SEROTYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	SEROGROUPING,

	@Diseases(value = {
		Disease.MEASLES,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MUMPS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	GENOTYPING,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	RSV_SUBTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	BEIJINGGENOTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	SPOLIGOTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.MOLECULAR_ASSAYS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	MIRU_PATTERN_CODE,

	// ----------------------------------------------------------------------------------------------
	// Serological Tests
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	ANTIBODY_DETECTION,

	// Superseded by IGM_/IGG_/IGA_SERUM_ANTIBODY for new tests (#13951). Kept here so historic records
	// still render and so case-classification logic (which binds to this constant) keeps working.
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
	@NotSelectableForNewTests
	ENZYME_LINKED_IMMUNOSORBENT_ASSAY,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel({
		ResultValueType.TEXT,
		ResultValueType.WESTERN_BLOT })
	WESTERN_BLOT,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel(ResultValueType.TEXT)
	NEUTRALIZING_ANTIBODIES,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.MALARIA,
		Disease.MUMPS })
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	INDIRECT_FLUORESCENT_ANTIBODY,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES })
	@PathogenTestCategoryRel(PathogenTestCategory.SEROLOGICAL_TESTS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	DIRECT_FLUORESCENT_ANTIBODY,

	// ----------------------------------------------------------------------------------------------
	// Antigen Detection
	// ----------------------------------------------------------------------------------------------

	// Single antigen-detection entry: the four merged methods are translated on read (see LegacyEnumNames)
	// and rewritten onto this constant by schema migration 649.
	@LegacyEnumNames({
		"ANTIGEN_DETECTION",
		"RAPID_TEST",
		"RAPID_ANTIGEN_DETECTION",
		"RDT" })
	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	LATERAL_FLOW_ASSAY,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	IMMUNOFLUORESCENCE_ASSAY,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	SLIDE_AGGLUTINATION,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	QUELLUNG_REACTION,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIGEN_DETECTION)
	// Positive/Negative interpretation plus the reciprocal titre (#14105): TEXT alone rendered no result
	// field. The titre ('1:160') is kept as text, not a Float numeric value, like NEUTRALIZING_ANTIBODIES.
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.TEXT })
	HEMAGGLUTINATION_INHIBITION,

	// ----------------------------------------------------------------------------------------------
	// Culture & Isolation
	// ----------------------------------------------------------------------------------------------

	// Superseded by the merged CULTURE entry for new tests (#13951). Kept for historic records.
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@ResultValueTypeRel({
		ResultValueType.TEXT,
		ResultValueType.NUMERIC })
	@NotSelectableForNewTests
	BACTERIAL_CULTURE,

	// Superseded by the merged ISOLATION entry for new tests (#13951). Kept for historic records.
	@Diseases(value = {
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	@NotSelectableForNewTests
	VIRAL_ISOLATION,

	// Superseded by the merged CULTURE entry for new tests (#13951). Kept for historic records.
	@Diseases(value = {
		Disease.SHIGELLOSIS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@ResultValueTypeRel(ResultValueType.TEXT)
	@NotSelectableForNewTests
	FUNGAL_CULTURE,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.CULTURE_AND_ISOLATION)
	@ResultValueTypeRel(ResultValueType.TEXT)
	MALDI_TOF,

	// ----------------------------------------------------------------------------------------------
	// Microscopy & Staining
	// ----------------------------------------------------------------------------------------------

	// Single microscopy entry: qualitative-only (Pos/Neg/Indeterminate/Pending). DIRECT_MICROSCOPY is NOT merged
	// into this constant: it is hidden for six diseases where DIRECT_MICROSCOPY was visible, and it is a
	// confirmation trigger for several classification rules that DIRECT_MICROSCOPY never satisfied.
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	MICROSCOPY,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel(ResultValueType.TEXT)
	GRAM_STAIN,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel(ResultValueType.SMEAR_GRADE)
	ACID_FAST_STAIN,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	DARK_FIELD_MICROSCOPY,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.NUMERIC })
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
		Disease.SHIGELLOSIS,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.TEXT })
	HISTOPATHOLOGY,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.TEXT })
	FISH,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	IMMUNOHISTOCHEMISTRY,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	ELECTRON_MICROSCOPY,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.TEXT,
		ResultValueType.NUMERIC })
	QUANTITATIVE_BUFFY_COAT,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel({
		ResultValueType.QUALITATIVE,
		ResultValueType.TEXT })
	THICK_BLOOD_SMEAR,

	@Diseases({
		Disease.MALARIA })
	@PathogenTestCategoryRel(PathogenTestCategory.MICROSCOPY_AND_STAINING)
	@ResultValueTypeRel({
		ResultValueType.NUMERIC,
		ResultValueType.TEXT })
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
	// AST has no result value type of its own — its result is the drug-susceptibility grid, not a
	// Positive/Negative/numeric/text value. The empty set hides the Test result selector and all quantitative
	// result fields; the result is kept as Not applicable.
	@ResultValueTypeRel({})
	ANTIBIOTIC_SUSCEPTIBILITY,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	@PathogenTestCategoryRel(PathogenTestCategory.ANTIMICROBIAL_SUSCEPTIBILITY_TESTING)
	@ResultValueTypeRel(ResultValueType.BOOLEAN)
	GENOTYPIC_RESISTANCE_TEST,

	// ----------------------------------------------------------------------------------------------
	// Functional Immune Assays
	// ----------------------------------------------------------------------------------------------

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	IGRA,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@PathogenTestCategoryRel(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS)
	@ResultValueTypeRel(ResultValueType.QUALITATIVE)
	TST,

	@PathogenTestCategoryRel(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS)
	@ResultValueTypeRel(ResultValueType.NUMERIC)
	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS }, hide = true)
	FLOW_CYTOMETRY,

	// ----------------------------------------------------------------------------------------------
	// No category: reveals a free-text companion field
	// ----------------------------------------------------------------------------------------------

	// DIRECT_MICROSCOPY and RAPID_ANTIBODY_TEST had no successor, so their records land here with the former
	// caption preserved in the free-text companion field.
	@LegacyEnumNames({
		"DIRECT_MICROSCOPY",
		"RAPID_ANTIBODY_TEST" })
	@RevealsTestTypeText
	OTHER;

	/**
	 * Deserialization entry point. Retired names still arrive from an un-upgraded sormas-to-sormas peer, an
	 * external lab message, or a REST client — none of which a database migration can reach — so they are
	 * translated here via {@link LegacyEnumNames}. A genuinely unknown name still fails.
	 */
	@JsonCreator
	public static PathogenTestType fromLegacyName(String name) {
		return LegacyEnumHelper.resolve(PathogenTestType.class, name);
	}

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
	 * Cached, reflection-free map of {@code PathogenTestType -> Set<ResultValueType>}. Built once at class
	 * initialization from each constant's {@link ResultValueTypeRel} annotation. The returned sets are
	 * unmodifiable so callers cannot accidentally mutate the shared instance.
	 */
	private static final java.util.Map<PathogenTestType, java.util.Set<ResultValueType>> RESULT_VALUE_TYPES_BY_METHOD;

	/** Default for {@code null} and unannotated test types (preserves long-standing behaviour). */
	private static final java.util.Set<ResultValueType> DEFAULT_RESULT_VALUE_TYPES =
		java.util.Collections.unmodifiableSet(java.util.EnumSet.of(ResultValueType.QUALITATIVE));

	static {
		java.util.EnumMap<PathogenTestType, java.util.Set<ResultValueType>> map = new java.util.EnumMap<>(PathogenTestType.class);
		for (PathogenTestType type : values()) {
			java.util.Set<ResultValueType> valueTypes = DEFAULT_RESULT_VALUE_TYPES;
			try {
				ResultValueTypeRel annotation = PathogenTestType.class.getField(type.name()).getAnnotation(ResultValueTypeRel.class);
				if (annotation != null) {
					// An explicit empty set (e.g. Antibiotic Susceptibility, whose result is the drug-susceptibility
					// grid) means the method has no result value type of its own.
					java.util.EnumSet<ResultValueType> set = java.util.EnumSet.noneOf(ResultValueType.class);
					java.util.Collections.addAll(set, annotation.value());
					valueTypes = java.util.Collections.unmodifiableSet(set);
				}
			} catch (NoSuchFieldException e) {
				// fall through to the default
			}
			map.put(type, valueTypes);
		}
		RESULT_VALUE_TYPES_BY_METHOD = java.util.Collections.unmodifiableMap(map);
	}

	/**
	 * @return the {@link ResultValueType}(s) this method produces, declared via
	 *         {@link ResultValueTypeRel}; drives which result fields the pathogen-test form shows.
	 *         A method without the annotation (e.g. legacy/hidden values and {@code OTHER}) is treated
	 *         as {@link ResultValueType#QUALITATIVE} only, preserving the long-standing behaviour. The
	 *         returned set is unmodifiable.
	 */
	public static java.util.Set<ResultValueType> getResultValueTypes(PathogenTestType testType) {
		if (testType == null) {
			return DEFAULT_RESULT_VALUE_TYPES;
		}
		return RESULT_VALUE_TYPES_BY_METHOD.getOrDefault(testType, DEFAULT_RESULT_VALUE_TYPES);
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

	/**
	 * Single source of truth for the disease + method combinations that show the Cq value input (the existing
	 * {@code CtCqValueComponent}). Used by both that component and by {@code TestResultComponent} to suppress
	 * the generic numeric value/unit fields when the Cq input applies. Both call sites must use this method so
	 * the rule cannot drift.
	 *
	 * <p>
	 * The Cq input applies for {@code PCR_RT_PCR}, {@code CQ_VALUE_DETECTION}, or {@code Q_PCR} on Malaria.
	 * Tuberculosis is included as well (#14030): a positive TB PCR shows the Cq value like every other disease.
	 *
	 * @param disease
	 *            the tested disease (may be {@code null})
	 * @param testType
	 *            the test method (may be {@code null})
	 */
	public static boolean cqInputApplies(Disease disease, PathogenTestType testType) {
		return testType == PCR_RT_PCR || testType == CQ_VALUE_DETECTION || (disease == Disease.MALARIA && testType == Q_PCR);
	}
}
