/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.Objects;

public class CampaignDashboardTotalsReference {

	private final Object key;
	private final String stack;

	public CampaignDashboardTotalsReference(Object key, String stack) {
		this.key = key;
		this.stack = stack;
	}

	public Object getKey() {
		return key;
	}

	public String getStack() {
		return stack;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CampaignDashboardTotalsReference that = (CampaignDashboardTotalsReference) o;
		return Objects.equals(key, that.key) && Objects.equals(stack, that.stack);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, stack);
	}
}
