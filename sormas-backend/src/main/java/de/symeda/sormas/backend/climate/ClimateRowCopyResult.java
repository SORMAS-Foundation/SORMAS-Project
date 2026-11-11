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

/**
 * Outcome of copying a district's IMPORT and MANUAL rows to another climate location: how many were copied, and how many
 * days were skipped because the target already had a row of equal or higher rank that day (with the first and last such
 * day).
 */
public class ClimateRowCopyResult {

	public static final ClimateRowCopyResult NOTHING = new ClimateRowCopyResult();

	private int copied;
	private int skipped;
	private Date firstSkippedDay;
	private Date lastSkippedDay;

	void addCopied() {
		copied++;
	}

	void addSkipped(Date day) {
		skipped++;
		if (firstSkippedDay == null || day.before(firstSkippedDay)) {
			firstSkippedDay = day;
		}
		if (lastSkippedDay == null || day.after(lastSkippedDay)) {
			lastSkippedDay = day;
		}
	}

	public int getCopied() {
		return copied;
	}

	public int getSkipped() {
		return skipped;
	}

	public Date getFirstSkippedDay() {
		return firstSkippedDay;
	}

	public Date getLastSkippedDay() {
		return lastSkippedDay;
	}

	public boolean isEmpty() {
		return copied == 0 && skipped == 0;
	}
}
