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

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;

public enum SampleMaterial {

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MUMPS,
		Disease.TUBERCULOSIS,
		Disease.PERTUSSIS }, hide = true)
	BLOOD,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	DRY_BLOOD,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.INFLUENZA }, hide = true)
	SERA,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	STOOL,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.SALMONELLOSIS }, hide = true)
	@Deprecated
	THROAT_ASPIRATE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.SALMONELLOSIS }, hide = true)
	@Deprecated
	NASAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.SALMONELLOSIS }, hide = true)
	@Deprecated
	THROAT_SWAB,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.SYPHILIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.SALMONELLOSIS }, hide = true)
	NP_SWAB,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	RECTAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	CEREBROSPINAL_FLUID,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	CRUST,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.SALMONELLOSIS }, hide = true)
	TISSUE,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	URINE,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.SALMONELLOSIS }, hide = true)
	@Deprecated
	CORNEA_PM,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SHIGELLOSIS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.SALMONELLOSIS }, hide = true)
	SALIVA,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.SALMONELLOSIS }, hide = true)
	@Deprecated
	URINE_PM,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	NUCHAL_SKIN_BIOPSY,

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.TUBERCULOSIS })
	BIOPSY,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.LATENT_TUBERCULOSIS }, hide = true)
	SPUTUM,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	ENDOTRACHEAL_ASPIRATE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.LATENT_TUBERCULOSIS }, hide = true)
	BRONCHOALVEOLAR_LAVAGE,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	BRAIN_TISSUE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	ANTERIOR_NARES_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.CORONAVIRUS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS }, hide = true)
	OP_ASPIRATE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.LATENT_TUBERCULOSIS }, hide = true)
	NP_ASPIRATE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	PLEURAL_FLUID,

	// Re-activated for RSV new samples (#14023): the RSV specimen requirements list Nasopharyngeal lavage.
	// No canonical SNOMED-CT code on the specimen sheet, so its SNOMED export stays null.
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.CORONAVIRUS })
	NASOPHARYNGEAL_LAVAGE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SHIGELLOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.SALMONELLOSIS }, hide = true)
	OROPHARYNGEAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	AMNIOTIC_FLUID,

	// Clinical Sample (Other) is offered for every disease (#14018) — no @Diseases means "visible for all".
	CLINICAL_SAMPLE,

	// Lux hide lifted so Peritoneal fluid shows for IPI on Luxembourg servers (#14156).
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.SHIGELLOSIS }, hide = true)
	PERITONEAL_FLUID,

	@Diseases(value = {
		Disease.MEASLES,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA }, hide = true)
	SYNOVIAL_FLUID,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.NEW_INFLUENZA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION }, hide = true)
	EDTA_WHOLE_BLOOD,

	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.TUBERCULOSIS })
	INTESTINAL_FLUID,

	@Diseases(value = {
		Disease.GIARDIASIS })
	DUODENUM_FLUID,

	@Diseases({
		Disease.TUBERCULOSIS })
	ASPIRATE,

	@Diseases({})
	@Deprecated
	BONE_AND_JOINT,

	@Diseases({})
	CATHETER_EXIT_SITE,

	@Diseases({})
	EYE,

	@Diseases({
		Disease.TUBERCULOSIS })
	GASTRIC_FLUID,

	@Diseases({
		Disease.SYPHILIS })
	GENITAL_SWAB,

	@Diseases({
		Disease.TUBERCULOSIS })
	LOWER_RESPIRATORY_TRACT,

	@Diseases({
		Disease.SHIGELLOSIS,
		Disease.TUBERCULOSIS })
	PUS,

	@Diseases({
		Disease.MUMPS })
	SEMEN,

	@Diseases({})
	SKIN,

	@Diseases({})
	@Deprecated
	SOFT_TISSUE,

	@Diseases({})
	WOUND,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.DENGUE,
		Disease.MEASLES,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	ABSCESS_SWAB,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.MEASLES,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SALMONELLOSIS }, hide = true)
	BONE,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.MEASLES,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION }, hide = true)
	BONE_MARROW,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.DENGUE,
		Disease.MEASLES,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	CONJUNCTIVAL_SWAB,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.DENGUE,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	MIDDLE_EAR_FLUID,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.DENGUE,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	PLASMA,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.DENGUE,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	SWAB_UNSPECIFIED,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.DENGUE,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	TEARS,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.DENGUE,
		Disease.MEASLES,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	CORD_BLOOD,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.MEASLES,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION }, hide = true)
	LUNG_TISSUE,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.DENGUE,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	PLACENTA,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.MUMPS,
		Disease.SYPHILIS,
		Disease.DENGUE,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	ULCER_SWAB,

	@Diseases(value = {
		Disease.DENGUE,
		Disease.MEASLES,
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.MALARIA,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS,
		Disease.SALMONELLOSIS }, hide = true)
	UNKNOWN,

	@Diseases(value = {
		Disease.NEW_INFLUENZA,
		Disease.CORONAVIRUS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.PERTUSSIS,
		Disease.INFLUENZA,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION }, hide = true)
	@Deprecated
	OTHER;

	/**
	 * SNOMED-CT codes for sample materials, aligned with the upgraded specimen list of
	 * issue #13560 (SNOMED-CT 2025-07-01 release). Returns null when no canonical code is mapped
	 * (e.g. UNKNOWN, OTHER, and deprecated values without a current equivalent).
	 */
	private static final Map<SampleMaterial, String> SNOMED_CODES;
	static {
		EnumMap<SampleMaterial, String> map = new EnumMap<>(SampleMaterial.class);
		map.put(ABSCESS_SWAB, "258497007");
		map.put(AMNIOTIC_FLUID, "119373006");
		map.put(ANTERIOR_NARES_SWAB, "697989009");
		map.put(ASPIRATE, "119295008");
		map.put(BIOPSY, "86273004");
		map.put(BLOOD, "119297000");
		map.put(BONE, "258417006");
		map.put(BONE_MARROW, "119359002");
		map.put(BRAIN_TISSUE, "256865009");
		map.put(BRONCHOALVEOLAR_LAVAGE, "258607008");
		map.put(CATHETER_EXIT_SITE, "16227651000119100");
		map.put(CEREBROSPINAL_FLUID, "258450006");
		map.put(CLINICAL_SAMPLE, "123038009");
		map.put(CONJUNCTIVAL_SWAB, "258498002");
		map.put(CORD_BLOOD, "122556008");
		map.put(CRUST, "1332490003");
		map.put(DRY_BLOOD, "440500007");
		map.put(DUODENUM_FLUID, "20779001");
		map.put(EDTA_WHOLE_BLOOD, "258580003");
		map.put(ENDOTRACHEAL_ASPIRATE, "119307008");
		map.put(EYE, "119399004");
		map.put(STOOL, "119339001");
		map.put(GASTRIC_FLUID, "258459007");
		map.put(GENITAL_SWAB, "258508008");
		map.put(LOWER_RESPIRATORY_TRACT, "258606004");
		map.put(LUNG_TISSUE, "399492000");
		map.put(MIDDLE_EAR_FLUID, "258466008");
		map.put(NP_ASPIRATE, "429931000124105");
		map.put(NP_SWAB, "258500001");
		map.put(NUCHAL_SKIN_BIOPSY, "309066003");
		map.put(OP_ASPIRATE, "258412000");
		map.put(OROPHARYNGEAL_SWAB, "258529004");
		// THROAT_SWAB deprecated and merged into OROPHARYNGEAL_SWAB; its code is kept so existing
		// THROAT_SWAB records still export their SNOMED-CT identifier.
		map.put(THROAT_SWAB, "258529004");
		map.put(PERITONEAL_FLUID, "168139001");
		map.put(PLACENTA, "122736005");
		map.put(PLASMA, "50863008");
		map.put(PLEURAL_FLUID, "418564007");
		map.put(PUS, "258502009");
		map.put(RECTAL_SWAB, "258528007");
		map.put(SALIVA, "119342007");
		map.put(SEMEN, "734846002");
		map.put(SERA, "119364003");
		map.put(SKIN, "119325001");
		map.put(SPUTUM, "119334006");
		map.put(SWAB_UNSPECIFIED, "257261003");
		map.put(SYNOVIAL_FLUID, "264380007");
		map.put(TEARS, "122594008");
		map.put(TISSUE, "119376003");
		map.put(ULCER_SWAB, "472871003");
		map.put(URINE, "122575003");
		map.put(WOUND, "119365002");
		// INTESTINAL_FLUID has no canonical SNOMED-CT code on the master specimen list — left unmapped.
		SNOMED_CODES = Collections.unmodifiableMap(map);
	}

	/**
	 * Sample materials retired by issue #13560. Kept in the enum (and marked {@link Deprecated}) so
	 * historical records still de-serialize and render, but hidden from the create-sample dropdown for
	 * new samples. This set is the runtime index used by {@link #isDeprecated()}; it must stay in sync
	 * with the {@code @Deprecated} constants above.
	 */
	private static final Set<SampleMaterial> DEPRECATED_MATERIALS =
		Collections.unmodifiableSet(EnumSet.of(URINE_PM, CORNEA_PM, NASAL_SWAB, THROAT_SWAB, THROAT_ASPIRATE, SOFT_TISSUE, BONE_AND_JOINT, OTHER));

	/**
	 * @return SNOMED-CT code for this sample material, or {@code null} if no canonical code is mapped.
	 */
	public String getSnomedCode() {
		return SNOMED_CODES.get(this);
	}

	/**
	 * @return {@code true} if this material is retired and should not be offered for new samples.
	 */
	public boolean isDeprecated() {
		return DEPRECATED_MATERIALS.contains(this);
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static String toString(SampleMaterial value, String details) {

		if (value == null) {
			return "";
		}

		if (value == SampleMaterial.OTHER) {
			return DataHelper.toStringNullable(details);
		}

		if (value == SampleMaterial.CLINICAL_SAMPLE && StringUtils.isNotBlank(details)) {
			return details;
		}

		return value.toString();
	}
}
