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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

@Remote
public interface OutbreakFacade {

	List<String> getActiveUuidsAfter(Date date);

	List<String> getInactiveUuidsAfter(Date date);

	List<OutbreakDto> getActiveAfter(Date date);
	
	List<OutbreakDto> getActive(OutbreakCriteria criteria);
	
	List<OutbreakDto> getActiveByRegionAndDisease(RegionReferenceDto region, Disease disease);
	
	OutbreakDto getActiveByDistrictAndDisease(DistrictReferenceDto district, Disease disease);

	boolean hasOutbreak(DistrictReferenceDto district, Disease disease);

	OutbreakDto saveOutbreak(OutbreakDto outbreakDto);

	void deleteOutbreak(OutbreakDto outbreakDto);
	
	/**
	 * @return The freshly started outbreak or an existing one if already started
	 */
	OutbreakDto startOutbreak(DistrictReferenceDto district, Disease disease);

	/**
	 * @return The ended outbreak or null if none was active
	 */
	OutbreakDto endOutbreak(DistrictReferenceDto district, Disease disease);
	
	Map<Disease, Long> getOutbreakDistrictCountByDisease(OutbreakCriteria criteria);
	
	Long getOutbreakDistrictCount (OutbreakCriteria criteria);
}
