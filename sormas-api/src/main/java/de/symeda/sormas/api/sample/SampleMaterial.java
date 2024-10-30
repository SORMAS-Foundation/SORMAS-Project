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

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;

public enum SampleMaterial {

	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	BLOOD,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	SERA,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	STOOL,
	NASAL_SWAB,
	THROAT_SWAB,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	NP_SWAB,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	RECTAL_SWAB,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	CEREBROSPINAL_FLUID,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	CRUST,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	TISSUE,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	URINE,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	CORNEA_PM,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	SALIVA,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	URINE_PM,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	NUCHAL_SKIN_BIOPSY,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	SPUTUM,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	ENDOTRACHEAL_ASPIRATE,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	BRONCHOALVEOLAR_LAVAGE,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	BRAIN_TISSUE,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	ANTERIOR_NARES_SWAB,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	OP_ASPIRATE,
	NP_ASPIRATE,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	PLEURAL_FLUID,
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
