/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.outbreak;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;

public class DashboardOutbreakDto  implements Serializable {
	
	private static final long serialVersionUID = 2430932452606853497L;
	
	public static final String I18N_PREFIX = "DiseaseBurdenInformation";
	
	public static final String DISEASE = "disease";
	public static final String DISTRICT = "district";
	
	private Disease disease;
	private DistrictReferenceDto district;
	
	public DashboardOutbreakDto(Disease disease, String districtUuid) {
		this.disease = disease;
		this.district = new DistrictReferenceDto(districtUuid);
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	
	
}
