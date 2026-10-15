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

package de.symeda.sormas.backend.epipulse;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportFacade;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.backend.epipulse.disease.EpipulseFieldModels;
import de.symeda.sormas.backend.epipulse.export.EpipulseCaseBasedExportStrategy;
import de.symeda.sormas.backend.epipulse.export.EpipulseCsvExportOrchestrator;

@Stateless(name = "EpipulseDiseaseExportFacade")
public class EpipulseDiseaseExportFacadeEjb implements EpipulseDiseaseExportFacade {

	@EJB
	private EpipulseCsvExportOrchestrator orchestrator;

	@EJB
	private EpipulseCaseBasedExportStrategy caseBasedExportStrategy;

	/**
	 * Runs the export for whichever disease the request names.
	 *
	 * <p>
	 * This replaced one {@code start<Disease>Export} method per disease. One field model drives
	 * both halves --- it selects and reads the rows, and it writes the CSV --- so naming the disease
	 * once, to {@link EpipulseFieldModels}, is the whole of the disease-specific wiring.
	 *
	 * @param uuid
	 *            the export request to run
	 * @param subjectCode
	 *            the disease to export
	 */
	public void startExport(String uuid, EpipulseSubjectCode subjectCode) {
		orchestrator.orchestrateExport(uuid, () -> EpipulseFieldModels.forSubjectCode(subjectCode), caseBasedExportStrategy::export);
	}

	@LocalBean
	@Stateless
	public static class EpipulseDiseaseExportFacadeEjbLocal extends EpipulseDiseaseExportFacadeEjb {

	}
}
