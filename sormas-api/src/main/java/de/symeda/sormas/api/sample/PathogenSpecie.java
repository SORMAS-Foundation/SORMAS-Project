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

package de.symeda.sormas.api.sample;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;
import de.symeda.sormas.api.utils.Diseases;

public enum PathogenSpecie {

	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SPOLIGOTYPING })
	MYCOBATERIUM_AFRICANUM,
	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SPOLIGOTYPING })
	MYCOBATERIUM_BOVIS,
	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SPOLIGOTYPING })
	MYCOBATERIUM_TUBERCULOSIS,
	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SPOLIGOTYPING })
	OTHER_MTBC_MEMBER,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	SPP,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	FALCIPARUM,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	VIVAX,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	MALARIAE,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	OVALE,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	KNOWLESI,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	CYNOMOLGI,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	NOT_SPECIFIED,
	@Diseases({
		Disease.MALARIA })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST })
	COINFECTION,

	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING,
		PathogenTestType.SEROTYPING,
		PathogenTestType.BACTERIAL_CULTURE })
	BOYDII,
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING,
		PathogenTestType.SEROTYPING,
		PathogenTestType.BACTERIAL_CULTURE })
	DYSENTERIAE,
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING,
		PathogenTestType.SEROTYPING,
		PathogenTestType.BACTERIAL_CULTURE })
	FLEXNERI,
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING,
		PathogenTestType.SEROTYPING,
		PathogenTestType.BACTERIAL_CULTURE })
	SONNEI,
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING,
		PathogenTestType.SEROTYPING,
		PathogenTestType.BACTERIAL_CULTURE })
	SHISPP,
	@Diseases({
		Disease.MALARIA,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST,
		PathogenTestType.BACTERIAL_CULTURE,
		PathogenTestType.SEROGROUPING,
		PathogenTestType.SEROTYPING })
	OTHER,
	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS,
		Disease.MALARIA,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SPOLIGOTYPING,
		PathogenTestType.THIN_BLOOD_SMEAR,
		PathogenTestType.RAPID_TEST,
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.LAMP,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST,
		PathogenTestType.BACTERIAL_CULTURE,
		PathogenTestType.SEROGROUPING,
		PathogenTestType.SEROTYPING })
	UNKNOWN,
	@Diseases({
		Disease.TUBERCULOSIS,
		Disease.LATENT_TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SPOLIGOTYPING })
	NOT_APPLICABLE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static List<PathogenSpecie> forPathogenTest(PathogenTestType pathogenTest) {
		return Arrays.stream(values()).filter(status -> {
			try {
				Field f = PathogenSpecie.class.getField(status.name());
				ApplicableToPathogenTests ann = f.getAnnotation(ApplicableToPathogenTests.class);
				return ann != null && Arrays.asList(ann.value()).contains(pathogenTest);
			} catch (NoSuchFieldException e) {
				return false;
			}
		}).collect(Collectors.toList());
	}
}
