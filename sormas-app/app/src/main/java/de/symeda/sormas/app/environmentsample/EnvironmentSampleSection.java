/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.environmentsample;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum EnvironmentSampleSection
	implements
	StatusElaborator {

	ENVIRONMENT_SAMPLE_INFO(R.string.caption_environment_sample_information, R.drawable.ic_drawer_environment_sample_blue_24dp),
	PATHOGEN_TESTS(R.string.heading_pathogen_tests_list, R.drawable.ic_petri_dish_fill_blue_24);

	private final int friendlyNameResourceId;
	private final int iconResourceId;

	EnvironmentSampleSection(int friendlyNameResourceId, int iconResourceId) {
		this.friendlyNameResourceId = friendlyNameResourceId;
		this.iconResourceId = iconResourceId;
	}

	public static EnvironmentSampleSection fromOrdinal(int ordinal) {
		return EnvironmentSampleSection.values()[ordinal];
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
		return null;
	}

	@Override
	public int getIconResourceId() {
		return iconResourceId;
	}
}
