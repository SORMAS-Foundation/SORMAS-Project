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
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS }, hide = true)
	BLOOD,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	SERA,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	STOOL,
	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	NASAL_SWAB,
	@Diseases(value = {
			Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	THROAT_SWAB,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	NP_SWAB,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	RECTAL_SWAB,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS }, hide = true)
	CEREBROSPINAL_FLUID,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	CRUST,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS,Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	TISSUE,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	URINE,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	CORNEA_PM,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	SALIVA,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION , Disease.INVASIVE_PNEUMOCOCCAL_INFECTION}, hide = true)
	URINE_PM,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	NUCHAL_SKIN_BIOPSY,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	SPUTUM,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION , Disease.INVASIVE_PNEUMOCOCCAL_INFECTION}, hide = true)
	ENDOTRACHEAL_ASPIRATE,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION }, hide = true)
	BRONCHOALVEOLAR_LAVAGE,
	@Diseases(value = {
		Disease.CORONAVIRUS, Disease.RESPIRATORY_SYNCYTIAL_VIRUS , Disease.INVASIVE_MENINGOCOCCAL_INFECTION, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION}, hide = true)
	BRAIN_TISSUE,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION , Disease.INVASIVE_PNEUMOCOCCAL_INFECTION}, hide = true)
	ANTERIOR_NARES_SWAB,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION , Disease.INVASIVE_PNEUMOCOCCAL_INFECTION}, hide = true)
	OP_ASPIRATE,
	@Diseases(value = {
			Disease.INVASIVE_MENINGOCOCCAL_INFECTION , Disease.INVASIVE_PNEUMOCOCCAL_INFECTION}, hide = true)
	NP_ASPIRATE,
	@Diseases(value = {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS, Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	PLEURAL_FLUID,
	@Diseases(value = {
			Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	PERITONEAL_FLUID,
	SYNOVIAL_FLUID,
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
