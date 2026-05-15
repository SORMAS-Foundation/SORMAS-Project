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

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS }, hide = true)
	ANTIBODY_DETECTION,

	ANTIGEN_DETECTION,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	RAPID_ANTIGEN_DETECTION,

	RAPID_TEST,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.DENGUE,
		Disease.MALARIA }, hide = true)
	CULTURE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS }, hide = true)
	HISTOPATHOLOGY,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
	ISOLATION,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
	IGM_SERUM_ANTIBODY,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
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
		Disease.SALMONELLOSIS }, hide = true)
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
		Disease.SALMONELLOSIS }, hide = true)
	INCUBATION_TIME,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.MALARIA })
	INDIRECT_FLUORESCENT_ANTIBODY,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES })
	DIRECT_FLUORESCENT_ANTIBODY,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS }, hide = true)
	MICROSCOPY,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SALMONELLOSIS }, hide = true)
	NEUTRALIZING_ANTIBODIES,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MALARIA })
	ENZYME_LINKED_IMMUNOSORBENT_ASSAY,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@RevealsTestTypeText
	PCR_RT_PCR,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS }, hide = true)
	GRAM_STAIN,

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
		Disease.SALMONELLOSIS }, hide = true)
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
		Disease.SALMONELLOSIS }, hide = true)
	CQ_VALUE_DETECTION,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION })
	SEQUENCING,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS }, hide = true)
	DNA_MICROARRAY,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.DENGUE,
		Disease.MALARIA,
		Disease.SALMONELLOSIS }, hide = true)
	TMA,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	IGRA,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	TST,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	BEIJINGGENOTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	SPOLIGOTYPING,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	MIRU_PATTERN_CODE,

	@Diseases(value = {
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	ANTIBIOTIC_SUSCEPTIBILITY,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	MULTILOCUS_SEQUENCE_TYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	CGMLST,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	SNP_TYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS })
	@RevealsTestTypeText(diseases = Disease.SALMONELLOSIS)
	SEROTYPING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	SLIDE_AGGLUTINATION,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	WHOLE_GENOME_SEQUENCING,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	SEROGROUPING,

	@Diseases(value = {
		Disease.MEASLES,
		Disease.CRYPTOSPORIDIOSIS })
	GENOTYPING,

	@Diseases(value = {
		Disease.DENGUE })
	NAAT,
	@Diseases({
		Disease.MALARIA })
	THICK_BLOOD_SMEAR,
	@Diseases({
		Disease.MALARIA })
	THIN_BLOOD_SMEAR,
	@Diseases({
		Disease.MALARIA })
	Q_PCR,
	@Diseases({
		Disease.MALARIA })
	LAMP,

	// @Herold need to refactor this as part of the test categories.
	// Antigen detection test is a test category. To create tests for the below categories, decided to use as OTHER_<<category>>
	@Diseases({
		Disease.MALARIA })
	OTHER_ANTIGEN_DETECTION_TEST,
	// Test for category
	@Diseases({
		Disease.MALARIA })
	OTHER_MOLECULAR_ASSAY,
	// Test for category
	@Diseases({
		Disease.MALARIA })
	OTHER_SEROLOGICAL_TEST,
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

		if (value == PathogenTestType.OTHER) {
			return DataHelper.toStringNullable(details);
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
}
