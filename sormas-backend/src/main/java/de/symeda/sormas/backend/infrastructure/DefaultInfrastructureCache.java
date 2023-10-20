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

package de.symeda.sormas.backend.infrastructure;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionService;

@Singleton(name = "DefaultInfrastructureCache")
public class DefaultInfrastructureCache {

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;

	private final DefaultInfrastructure<Region> defaultRegion = new DefaultInfrastructure<>();
	private final DefaultInfrastructure<District> defaultDistrict = new DefaultInfrastructure<>();
	private final DefaultInfrastructure<Community> defaultCommunity = new DefaultInfrastructure<>();

	public Region getDefaultRegion() {

		if (defaultRegion.isQueried()) {
			return defaultRegion.defaultInfrastructure;
		} else {
			Region newDefault = regionService.getDefaultInfrastructure();
			defaultRegion.setDefaultInfrastructure(newDefault);
			defaultRegion.setQueried(true);
			return defaultRegion.getDefaultInfrastructure();
		}
	}

	public void resetDefaultRegion() {
		defaultRegion.setDefaultInfrastructure(null);
		defaultRegion.setQueried(false);
	}

	public District getDefaultDistrict() {

		if (defaultDistrict.isQueried()) {
			return defaultDistrict.defaultInfrastructure;
		} else {
			District newDefault = districtService.getDefaultInfrastructure();
			defaultDistrict.setDefaultInfrastructure(newDefault);
			defaultDistrict.setQueried(true);
			return defaultDistrict.getDefaultInfrastructure();
		}
	}

	public void resetDefaultDistrict() {
		defaultDistrict.setDefaultInfrastructure(null);
		defaultDistrict.setQueried(false);
	}

	public Community getDefaultCommunity() {

		if (defaultCommunity.isQueried()) {
			return defaultCommunity.defaultInfrastructure;
		} else {
			Community newDefault = communityService.getDefaultInfrastructure();
			defaultCommunity.setDefaultInfrastructure(newDefault);
			defaultCommunity.setQueried(true);
			return defaultCommunity.getDefaultInfrastructure();
		}
	}

	public void resetDefaultCommunity() {
		defaultCommunity.setDefaultInfrastructure(null);
		defaultCommunity.setQueried(false);
	}

	private static class DefaultInfrastructure<ADO extends InfrastructureAdoWithDefault> {

		private ADO defaultInfrastructure;
		private boolean queried;

		public ADO getDefaultInfrastructure() {
			return defaultInfrastructure;
		}

		public void setDefaultInfrastructure(ADO defaultInfrastructure) {
			this.defaultInfrastructure = defaultInfrastructure;
		}

		public boolean isQueried() {
			return queried;
		}

		public void setQueried(boolean queried) {
			this.queried = queried;
		}
	}

}
