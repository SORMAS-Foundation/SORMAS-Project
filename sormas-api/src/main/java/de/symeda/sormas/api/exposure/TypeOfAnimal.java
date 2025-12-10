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

public enum TypeOfAnimal {

	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	BAT,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	POULTRY,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	CAMEL,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	CANIDAE,
	CAT,
	CATTLE,
	DOG,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	PRIMATE,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	SNAKE,
	SWINE,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	RABBIT,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	RODENT,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	TICK,
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	FLEA,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	BIRDS,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	GOAT,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	HORSE,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	SHEEP,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
