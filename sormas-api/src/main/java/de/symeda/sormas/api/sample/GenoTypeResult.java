/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import de.symeda.sormas.api.utils.Diseases;

public enum GenoTypeResult {

	@Diseases({
		Disease.MEASLES })
	GENOTYPE_A,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_B,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_B2,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_B3,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_C1,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_C2,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D1,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D10,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D11,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D2,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D3,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D4,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D5,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D6,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D7,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D8,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_D9,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_E,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_F,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_G1,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_G2,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_G3,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_H1,
	@Diseases({
		Disease.MEASLES })
	GENOTYPE_H2,
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS })
	CRYPTOSPORIDIUM_HOMINIS,
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS })
	CRYPTOSPORIDIUM_PARVUM,
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS })
	CRYPTOSPORIDIUM_SPECIES,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
