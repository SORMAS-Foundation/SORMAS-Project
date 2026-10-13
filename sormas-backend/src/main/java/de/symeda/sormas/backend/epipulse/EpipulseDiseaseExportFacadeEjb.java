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

package de.symeda.sormas.backend.epipulse;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportFacade;
import de.symeda.sormas.backend.epipulse.strategy.IpiCsvExportStrategy;
import de.symeda.sormas.backend.epipulse.strategy.MeaslesCsvExportStrategy;
import de.symeda.sormas.backend.epipulse.strategy.MeniCsvExportStrategy;
import de.symeda.sormas.backend.epipulse.strategy.PertussisCsvExportStrategy;

@Stateless(name = "EpipulseDiseaseExportFacade")
public class EpipulseDiseaseExportFacadeEjb implements EpipulseDiseaseExportFacade {

	@EJB
	private EpipulseCsvExportOrchestrator orchestrator;

	@EJB
	private PertussisCsvExportStrategy pertussisStrategy;

	@EJB
	private MeaslesCsvExportStrategy measlesStrategy;

	@EJB
	private IpiCsvExportStrategy ipiStrategy;

	@EJB
	private MeniCsvExportStrategy meniStrategy;

	@EJB
	private EpipulseDiseaseExportService diseaseExportService;

	public void startPertussisExport(String uuid) {
		orchestrator.orchestrateExport(uuid, diseaseExportService::exportPertussisCaseBased, pertussisStrategy);
	}

	public void startMeaslesExport(String uuid) {
		orchestrator.orchestrateExport(uuid, diseaseExportService::exportMeaslesCaseBased, measlesStrategy);
	}

	public void startIpiExport(String uuid) {
		orchestrator.orchestrateExport(uuid, diseaseExportService::exportIpiCaseBased, ipiStrategy);
	}

	public void startMeniExport(String uuid) {
		orchestrator.orchestrateExport(uuid, diseaseExportService::exportMeniCaseBased, meniStrategy);
	}

	@LocalBean
	@Stateless
	public static class EpipulseDiseaseExportFacadeEjbLocal extends EpipulseDiseaseExportFacadeEjb {

	}
}
