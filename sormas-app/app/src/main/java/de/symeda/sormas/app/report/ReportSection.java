/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.report;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum ReportSection
	implements
	StatusElaborator {

	MY_REPORTS(R.string.caption_my_reports, R.drawable.ic_assignment_black_24dp),
	INFORMANT_REPORTS(R.string.caption_informant_reports, R.drawable.ic_assignment_ind_black_24dp);

	private int captionResourceId;
	private int iconResourceId;

	ReportSection(int captionResourceId, int iconResourceId) {
		this.captionResourceId = captionResourceId;
		this.iconResourceId = iconResourceId;
	}

	public static ReportSection fromOrdinal(int ordinal) {
		return ReportSection.values()[ordinal];
	}

	@Override
	public String getFriendlyName(Context context) {
		return context.getResources().getString(captionResourceId);
	}

	@Override
	public int getColorIndicatorResource() {
		return 0;
	}

	@Override
	public Enum getValue() {
		return this;
	}

	@Override
	public int getIconResourceId() {
		return iconResourceId;
	}
}
