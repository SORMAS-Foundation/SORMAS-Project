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

package de.symeda.sormas.app.visit;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum VisitSection
	implements
	StatusElaborator {

	VISIT_INFO(R.string.caption_visit_information, R.drawable.ic_recent_actors_black_24dp),
	SYMPTOMS(R.string.caption_visit_symptoms, R.drawable.ic_healing_black_24dp);

	private int friendlyNameResourceId;
	private int iconResourceId;

	VisitSection(int friendlyNameResourceId, int iconResourceId) {
		this.friendlyNameResourceId = friendlyNameResourceId;
		this.iconResourceId = iconResourceId;
	}

	public static VisitSection fromOrdinal(int ordinal) {
		return VisitSection.values()[ordinal];
	}

	@Override
	public String getFriendlyName(Context context) {
		return context.getResources().getString(friendlyNameResourceId);
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
