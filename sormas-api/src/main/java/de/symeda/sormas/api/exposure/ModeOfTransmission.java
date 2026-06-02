/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.api.exposure;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum ModeOfTransmission {

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	ANIMAL_TO_HUMAN,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	FOOD_OR_WATER,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	PERSON_TO_PERSON,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	RECREATIONAL_WATER,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS,
		Disease.SHIGELLOSIS })
	HEALTHCARE_ASSOCIATED,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	INJECTING_DRUG_USERS,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	LAB_OCCUPATIONAL_EXPOSURE,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	MOTHER_TO_CHILD,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	SEXUAL,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	TRANSFUSION_RECIPIENT,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.SHIGELLOSIS })
	ORGAN_RECIPIENT,

	@Diseases(value = {
		Disease.MALARIA })
	MOSQUITOES_FROM_ENDEMIC_COUNTRY,
	@Diseases(value = {
		Disease.MALARIA })
	MOSQUITOES_BY_AIR,
	@Diseases(value = {
		Disease.MALARIA })
	MEDICAL_CARE,
	@Diseases(value = {
		Disease.MALARIA })
	MOSQUITOES_WITH_STRONG_EPI_EVIDENCE,
	@Diseases(value = {
		Disease.MALARIA })
	MOSQUITOES_WITHOUT_EVIDENCE,
	@Diseases(value = {
		Disease.MALARIA,
		Disease.SHIGELLOSIS })
	FROM_MOTHER_TO_CHILD,
	@Diseases(value = {
		Disease.MALARIA,
		Disease.SHIGELLOSIS })
	BY_LAB,
	@Diseases(value = {
		Disease.MALARIA })
	TRANSFUSION_TRANSPLANT_RECIPIENT,

	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

}
