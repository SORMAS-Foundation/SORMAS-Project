/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

public enum ExposureType {

	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS }, hide = true)
	WORK,
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS })
	TRAVEL,
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS }, hide = true)
	SPORT,
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS }, hide = true)
	VISIT,
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS }, hide = true)
	GATHERING,
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS }, hide = true)
	HABITATION,
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS }, hide = true)
	PERSONAL_SERVICES,
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	CHILDCARE_FACILITY,
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	BURIAL,
	@Diseases(value = {
		Disease.CORONAVIRUS }, hide = true)
	ANIMAL_CONTACT,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	RECREATIONAL_WATER,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	FOOD,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	SEXUAL_CONTACT,
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS })
	SYMPTOMATIC_CONTACT,
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS })
	FLOOD_EXPOSURE,
	OTHER,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
