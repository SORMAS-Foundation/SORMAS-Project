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
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	BLOOD,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	DRY_BLOOD,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.DENGUE,
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
	@Deprecated
	THROAT_SWAB,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	CRUST,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.MEASLES,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.DENGUE,
		Disease.SHIGELLOSIS }, hide = true)
	@Deprecated
	CORNEA_PM,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	NUCHAL_SKIN_BIOPSY,

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
	OP_ASPIRATE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.SHIGELLOSIS }, hide = true)
	PLEURAL_FLUID,

	// Re-activated for RSV new samples (#14023): the RSV specimen requirements list Nasopharyngeal lavage.
	// No canonical SNOMED-CT code on the specimen sheet, so its SNOMED export stays null. 
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	NASOPHARYNGEAL_LAVAGE,

	@Diseases(value = {
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	OROPHARYNGEAL_SWAB,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
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
		Disease.DENGUE,
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	PERITONEAL_FLUID,

	@Diseases(value = {
		Disease.MEASLES,
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	SYNOVIAL_FLUID,

	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS }, hide = true)
	EDTA_WHOLE_BLOOD,

	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	INTESTINAL_FLUID,

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	DUODENUM_FLUID,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	ASPIRATE,

	@Diseases({
		Disease.SALMONELLOSIS })
	@Deprecated
	BONE_AND_JOINT,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	CATHETER_EXIT_SITE,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	EYE,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	GASTRIC_FLUID,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	GENITAL_SWAB,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	LOWER_RESPIRATORY_TRACT,

	@Diseases({
		Disease.SHIGELLOSIS,
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	PUS,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	SEMEN,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	SKIN,

	@Diseases({
		Disease.SALMONELLOSIS })
	@Deprecated
	SOFT_TISSUE,

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	WOUND,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	ABSCESS_SWAB,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE }, hide = true)
	BONE,

	@Diseases(value = {
		Disease.SHIGELLOSIS,
		Disease.DENGUE }, hide = true)
	BONE_MARROW,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	CONJUNCTIVAL_SWAB,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	MIDDLE_EAR_FLUID,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	PLASMA,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	SWAB_UNSPECIFIED,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	TEARS,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	CORD_BLOOD,

	@Diseases(value = {
		Disease.SALMONELLOSIS,
		Disease.SHIGELLOSIS,
		Disease.DENGUE }, hide = true)
	LUNG_TISSUE,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	PLACENTA,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	ULCER_SWAB,

	@Diseases(value = {
		Disease.SALMONELLOSIS }, hide = true)
	UNKNOWN,

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
