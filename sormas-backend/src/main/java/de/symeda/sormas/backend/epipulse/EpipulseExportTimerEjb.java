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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;

@Stateless
public class EpipulseExportTimerEjb {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long EXPORT_DELAY_MS = 60000; // 5 seconds

	@Resource
	private TimerService timerService;

	@EJB
	private EpipulseDiseaseExportFacadeEjb.EpipulseDiseaseExportFacadeEjbLocal diseaseExportFacadeEjb;

	public void scheduleExportDisease(String uuid, EpipulseSubjectCode subjectCode) {
		if (uuid == null || uuid.isEmpty()) {
			logger.warn("Cannot schedule export: UUID is null or empty");
			return;
		}

		// Store multiple values as an array in timer info
		String[] timerInfo = {
			uuid,
			subjectCode.name() };
		timerService.createTimer(EXPORT_DELAY_MS, timerInfo);
		logger.info("Epipulse export scheduled for UUID: {}, of subject code: {} ", uuid, subjectCode);
	}

	@javax.ejb.Timeout
	public void exportDiseaseTimeout(Timer timer) {
		String[] timerInfo = (String[]) timer.getInfo();
		String uuid = timerInfo[0];
		String subjectCodeStr = timerInfo[1];

		logger.info("Timer fired for export UUID: {} of type: {}", uuid, subjectCodeStr);

		try {
			EpipulseSubjectCode subjectCode = EpipulseSubjectCode.valueOf(subjectCodeStr);

			switch (subjectCode) {
			case PERT:
				diseaseExportFacadeEjb.exportPertussisCaseBased(uuid);
				break;
			default:
				logger.warn("No export for subject code: {}", subjectCodeStr);
				break;
			}
		} catch (Exception e) {
			logger.error("Error during scheduled export for UUID: {} of type: {}: " + e.getMessage());
		} finally {
			timer.cancel();
		}
	}
}
