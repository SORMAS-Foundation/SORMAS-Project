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

public enum PathogenTestType {

	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	ANTIBODY_DETECTION,
	ANTIGEN_DETECTION,
	RAPID_TEST,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	CULTURE,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	HISTOPATHOLOGY,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	ISOLATION,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	IGM_SERUM_ANTIBODY,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	IGG_SERUM_ANTIBODY,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	IGA_SERUM_ANTIBODY,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	INCUBATION_TIME,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	INDIRECT_FLUORESCENT_ANTIBODY,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	DIRECT_FLUORESCENT_ANTIBODY,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	MICROSCOPY,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	NEUTRALIZING_ANTIBODIES,
	PCR_RT_PCR,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	GRAM_STAIN,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	LATEX_AGGLUTINATION,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	CQ_VALUE_DETECTION,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	SEQUENCING,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	DNA_MICROARRAY,
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	TMA,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static String toString(PathogenTestType value, String details) {
		if (value == null) {
			return "";
		}

		if (value == PathogenTestType.OTHER) {
			return DataHelper.toStringNullable(details);
		}

		return value.toString();
	}
}
