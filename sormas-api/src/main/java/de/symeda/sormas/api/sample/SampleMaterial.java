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

public enum SampleMaterial {

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	BLOOD,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	DRY_BLOOD,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	SERA,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES }, hide = true)
	STOOL,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	THROAT_ASPIRATE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	NASAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	THROAT_SWAB,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	NP_SWAB,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	RECTAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	CEREBROSPINAL_FLUID,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	CRUST,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	TISSUE,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	URINE,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	CORNEA_PM,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	SALIVA,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	URINE_PM,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	NUCHAL_SKIN_BIOPSY,

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	BIOPSY,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	SPUTUM,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	ENDOTRACHEAL_ASPIRATE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	BRONCHOALVEOLAR_LAVAGE,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	BRAIN_TISSUE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	ANTERIOR_NARES_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	OP_ASPIRATE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	NP_ASPIRATE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	PLEURAL_FLUID,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	NASOPHARYNGEAL_LAVAGE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	OROPHARYNGEAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	AMNIOTIC_FLUID,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	CLINICAL_SAMPLE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	PERITONEAL_FLUID,

	@Diseases(value = {
		Disease.MEASLES,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	SYNOVIAL_FLUID,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	EDTA_WHOLE_BLOOD,

	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS })
	INTESTINAL_FLUID,

	@Diseases(value = {
		Disease.GIARDIASIS })
	DUODENUM_FLUID,

	OTHER;

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

		return value.toString();
	}
}
