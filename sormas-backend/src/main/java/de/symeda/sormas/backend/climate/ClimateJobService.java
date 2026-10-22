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

package de.symeda.sormas.backend.climate;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ClimateJobService extends BaseAdoService<ClimateJob> {

	public ClimateJobService() {
		super(ClimateJob.class);
	}

	/**
	 * Stores a district's move to another climate location as a finished RELINK entry, so data managers can see which days
	 * were not copied. Only called when rows were copied or skipped.
	 *
	 * @param startedBy
	 *            the user who saved the district; null for system relinks (switch-on, nightly safety net, grid size change)
	 */
	public ClimateJob recordRelink(District district, ClimateLocation from, ClimateLocation to, ClimateRowCopyResult copyResult, User startedBy) {

		ClimateJob job = new ClimateJob();
		job.setJobType(ClimateJobType.RELINK);
		job.setStatus(ClimateJobStatus.DONE);
		job.setDistrict(district);
		job.setFromClimateLocation(from);
		job.setClimateLocation(to);
		job.setRowsAdded(copyResult.getCopied());
		job.setRowsSkipped(copyResult.getSkipped());
		job.setFromDate(copyResult.getFirstSkippedDay());
		job.setToDate(copyResult.getLastSkippedDay());
		job.setStartedBy(startedBy);
		Date now = new Date();
		job.setStartedAt(now);
		job.setFinishedAt(now);
		ensurePersisted(job);
		return job;
	}
}
