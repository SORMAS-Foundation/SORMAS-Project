/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.epipulse;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum EpipulseSubjectCode {

	PERT(true, Disease.PERTUSSIS, false),
	MEAS(true, Disease.MEASLES, false),
	PNEU(true, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, false), // Invasive Pneumococcal Infection
	MENI(true, Disease.INVASIVE_MENINGOCOCCAL_INFECTION, false), // Invasive Meningococcal Infection
	CHIK(true, Disease.CHIKUNGUNYA, false), // Chikungunya virus disease
	CONSYPH(true, Disease.SYPHILIS, false, EpipulseCaseSubset.SYPHILIS_CONGENITAL), // Congenital syphilis - ConSyphFieldModel
	DENGUE(true, Disease.DENGUE, false), // Dengue - DengueFieldModel
	DIPH(true, Disease.DIPHTHERIA, false), // Diphtheria
	GONO(true, Disease.GONOCOCCAL_INFECTION, false), // Gonorrhoea - GonoFieldModel
	MALA(true, Disease.MALARIA, false), // Malaria - MalaFieldModel
	MUMP(true, Disease.MUMPS, false), // Mumps
	POLI(true, Disease.POLIO, false), // Poliomyelitis
	RUBE(true, Disease.RUBELLA, false), // Rubella - RubeFieldModel
	SALM(true, Disease.SALMONELLOSIS, false), // Salmonellosis - SalmFieldModel
	SHIG(true, Disease.SHIGELLOSIS, false), // Shigellosis - ShigFieldModel
	SYPH(true, Disease.SYPHILIS, false, EpipulseCaseSubset.SYPHILIS_ACQUIRED), // Syphilis - SyphFieldModel
	WNF(true, Disease.WEST_NILE_FEVER, false); // West Nile virus infection

	private final boolean diseaseModel;
	private final Disease disease;
	private final boolean aggregatedReporting;
	private final EpipulseCaseSubset caseSubset;

	EpipulseSubjectCode(boolean diseaseModel, Disease disease, boolean aggregatedReporting) {
		this(diseaseModel, disease, aggregatedReporting, null);
	}

	EpipulseSubjectCode(boolean diseaseModel, Disease disease, boolean aggregatedReporting, EpipulseCaseSubset caseSubset) {
		this.diseaseModel = diseaseModel;
		this.disease = disease;
		this.aggregatedReporting = aggregatedReporting;
		this.caseSubset = caseSubset;
	}

	/**
	 * The part of {@link #getDisease()}'s cases this code reports, or {@code null} where it reports
	 * all of them.
	 *
	 * <p>
	 * Two codes sharing a disease must both declare a subset, or the cases of one appear in the
	 * other's file as well: {@code SYPH} and {@code CONSYPH} divide {@code Disease.SYPHILIS}
	 * between them, so narrowing only the new one would leave congenital cases reported twice.
	 */
	public EpipulseCaseSubset getCaseSubset() {
		return caseSubset;
	}

	public boolean isDiseaseModel() {
		return diseaseModel;
	}

	public Disease getDisease() {
		return disease;
	}

	/**
	 * Whether EpiPulse expects this code as counts rather than one record per case.
	 *
	 * <p>
	 * This is the axis the export strategies divide on: a case-based code is exported by
	 * {@code EpipulseCaseBasedExportStrategy}, while an aggregate code would require a sibling strategy
	 * that counts over different tables and produces no per-case row.
	 *
	 * <p>
	 * No code is currently marked aggregate, so nothing reads this value and that sibling strategy
	 * does not exist yet.
	 */
	public boolean isAggregatedReporting() {
		return aggregatedReporting;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
