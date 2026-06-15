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
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;
import de.symeda.sormas.api.utils.Diseases;

public enum SerotypingMethod {

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROTYPING })
	MULTIPLEX_PCR,
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING })
	QUELLUNG_REACTION,
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING })
	COAGGLUTINATION,
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROTYPING })
	AGGLUTINATION,
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING })
	GEL_DIFFUSION,
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING })
	PNEUMOTEST,
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROGROUPING })
	SLIDE_AGGLUTINATION,
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	DISK_DIFFUSION,
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	MIC_DETERMINATION,
	@Diseases(value = {
		Disease.SHIGELLOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.SEROTYPING,
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY })
	WGS_PREDICTION,
	// OTHER is mostly applicable for all diseases and pathogen tests, so we don't specify them as applicable to specific diseases or tests
	@Diseases
	@ApplicableToPathogenTests
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
