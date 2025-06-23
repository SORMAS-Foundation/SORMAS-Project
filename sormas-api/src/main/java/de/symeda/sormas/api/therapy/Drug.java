/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.therapy;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.DrugTypes;

public enum Drug {

	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
			@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	AMIKACIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	BEDAQUILINE,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	CAPREOMYCIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	CIPROFLOXACIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	DELAMANID,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	ETHAMBUTOL,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	GATIFLOXACIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	ISONIAZID,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	KANAMYCIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	LEVOFLOXACIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	MOXIFLOXACIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	OFLOXACIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	RIFAMPICIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	STREPTOMYCIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	CEFTRIAXONE,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	PENICILLIN,
	@DrugTypes(value = {
		TypeOfDrug.ANTIBIOTIC })
	@Diseases(value = {Disease.INVASIVE_MENINGOCOCCAL_INFECTION}, hide = true)
	ERYTHROMYCIN,
	@DrugTypes(value = {
			TypeOfDrug.ANTIBIOTIC })
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static List<Drug> byTypeOfDrug(TypeOfDrug typeOfDrug) {
		return Arrays.stream(values()).filter(drug -> {
			try {
				Field f = Drug.class.getField(drug.name());
				DrugTypes ann = f.getAnnotation(DrugTypes.class);
				return ann != null && Arrays.asList(ann.value()).contains(typeOfDrug);
			} catch (NoSuchFieldException e) {
				return false;
			}
		}).collect(Collectors.toList());
	}
}
