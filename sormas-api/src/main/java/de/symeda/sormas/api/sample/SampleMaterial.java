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
import java.util.Map;

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
		Disease.MEASLES,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	STOOL,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	THROAT_ASPIRATE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	NASAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	THROAT_SWAB,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
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
		Disease.DENGUE }, hide = true)
	RECTAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
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
		Disease.DENGUE }, hide = true)
	CRUST,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
	TISSUE,

	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
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
		Disease.DENGUE }, hide = true)
	CORNEA_PM,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA }, hide = true)
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
		Disease.DENGUE }, hide = true)
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
		Disease.DENGUE }, hide = true)
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
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	SPUTUM,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	ENDOTRACHEAL_ASPIRATE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
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
		Disease.DENGUE }, hide = true)
	BRAIN_TISSUE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	ANTERIOR_NARES_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	OP_ASPIRATE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	NP_ASPIRATE,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
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
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
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
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
	PERITONEAL_FLUID,

	@Diseases(value = {
		Disease.MEASLES,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE }, hide = true)
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

	@Diseases({
		Disease.SALMONELLOSIS })
	ASPIRATE,

	@Diseases({
		Disease.SALMONELLOSIS })
	BONE_AND_JOINT,

	@Diseases({
		Disease.SALMONELLOSIS })
	CATHETER_EXIT_SITE,

	@Diseases({
		Disease.SALMONELLOSIS })
	EYE,

	@Diseases({
		Disease.SALMONELLOSIS })
	GASTRIC_FLUID,

	@Diseases({
		Disease.SALMONELLOSIS })
	GENITAL_SWAB,

	@Diseases({
		Disease.SALMONELLOSIS })
	LOWER_RESPIRATORY_TRACT,

	@Diseases({
		Disease.SALMONELLOSIS })
	PUS,

	@Diseases({
		Disease.SALMONELLOSIS })
	SEMEN,

	@Diseases({
		Disease.SALMONELLOSIS })
	SKIN,

	@Diseases({
		Disease.SALMONELLOSIS })
	SOFT_TISSUE,

	@Diseases({
		Disease.SALMONELLOSIS })
	WOUND,

	OTHER;

	/**
	 * SNOMED-CT codes for sample materials. Reference data per the v1.1 Salmonellosis spec
	 * (Lux requirement Salmonella v1.1.docx, sample list table). Optional: returns null
	 * when no canonical code is mapped.
	 */
	private static final Map<SampleMaterial, String> SNOMED_CODES;
	static {
		EnumMap<SampleMaterial, String> map = new EnumMap<>(SampleMaterial.class);
		map.put(ASPIRATE, "119295008");
		map.put(BRONCHOALVEOLAR_LAVAGE, "258607008");
		map.put(BLOOD, "119297000");
		map.put(BONE_AND_JOINT, "258539005");
		map.put(CATHETER_EXIT_SITE, "16227651000119102");
		map.put(CRUST, "1332490003");
		map.put(CEREBROSPINAL_FLUID, "258450006");
		map.put(DRY_BLOOD, "440500007");
		map.put(EDTA_WHOLE_BLOOD, "57921000052103");
		map.put(EYE, "119399004");
		map.put(STOOL, "119339001");
		map.put(GENITAL_SWAB, "258508008");
		map.put(GASTRIC_FLUID, "258459007");
		map.put(LOWER_RESPIRATORY_TRACT, "258606004");
		map.put(TISSUE, "399492000");
		map.put(WOUND, "119365002");
		map.put(NP_SWAB, "258500001");
		map.put(OROPHARYNGEAL_SWAB, "461911000124106");
		map.put(PLEURAL_FLUID, "418564007");
		map.put(PUS, "119323008");
		map.put(RECTAL_SWAB, "258528007");
		map.put(SALIVA, "119342007");
		map.put(SEMEN, "119347001");
		map.put(SERA, "119364003");
		map.put(SKIN, "608969007");
		map.put(SOFT_TISSUE, "309072003");
		map.put(SPUTUM, "119334006");
		map.put(SYNOVIAL_FLUID, "119332005");
		map.put(URINE, "122575003");
		SNOMED_CODES = Collections.unmodifiableMap(map);
	}

	/**
	 * @return SNOMED-CT code for this sample material, or {@code null} if no canonical code is mapped.
	 */
	public String getSnomedCode() {
		return SNOMED_CODES.get(this);
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

		return value.toString();
	}
}
