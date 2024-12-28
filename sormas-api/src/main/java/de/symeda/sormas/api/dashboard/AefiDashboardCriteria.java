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

package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDashboardFilterDateType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;

public class AefiDashboardCriteria extends BaseDashboardCriteria<AefiDashboardCriteria> {

	private AefiDashboardFilterDateType aefiDashboardFilterDateType;
	private AefiType aefiType;

	private boolean showSeriousAefiForMap;
	private boolean showNonSeriousAefiForMap;

	public AefiDashboardCriteria() {
		super(AefiDashboardCriteria.class);
	}

	public AefiDashboardFilterDateType getAefiDashboardFilterDateType() {
		return aefiDashboardFilterDateType;
	}

	public AefiType getAefiType() {
		return aefiType;
	}

	public boolean isShowSeriousAefiForMap() {
		return showSeriousAefiForMap;
	}

	public boolean isShowNonSeriousAefiForMap() {
		return showNonSeriousAefiForMap;
	}

	public AefiDashboardCriteria aefiDashboardDateType(AefiDashboardFilterDateType aefiDashboardFilterDateType) {
		this.aefiDashboardFilterDateType = aefiDashboardFilterDateType;
		return self;
	}

	public AefiDashboardCriteria aefiType(AefiType aefiType) {
		this.aefiType = aefiType;
		return self;
	}

	public AefiDashboardCriteria showSeriousAefiForMap(boolean showSeriousAefi) {
		this.showSeriousAefiForMap = showSeriousAefi;
		return self;
	}

	public AefiDashboardCriteria showNonSeriousAefiForMap(boolean showNonSeriousAefi) {
		this.showNonSeriousAefiForMap = showNonSeriousAefi;
		return self;
	}
}
