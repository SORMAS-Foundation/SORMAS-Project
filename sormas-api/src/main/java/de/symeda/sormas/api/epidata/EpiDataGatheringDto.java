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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.epidata;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.SensitiveData;

public class EpiDataGatheringDto extends PseudonymizableDto {

	private static final long serialVersionUID = 4953376180428831063L;

	public static final String I18N_PREFIX = "EpiDataGathering";

	public static final String DESCRIPTION = "description";
	public static final String GATHERING_DATE = "gatheringDate";
	public static final String GATHERING_ADDRESS = "gatheringAddress";

	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.NEW_INFLUENZA,
		Disease.CSM,
		Disease.CHOLERA,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.DENGUE,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	@SensitiveData
	private String description;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.NEW_INFLUENZA,
		Disease.CSM,
		Disease.CHOLERA,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.DENGUE,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private Date gatheringDate;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.NEW_INFLUENZA,
		Disease.CSM,
		Disease.CHOLERA,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.DENGUE,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private LocationDto gatheringAddress;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getGatheringDate() {
		return gatheringDate;
	}

	public void setGatheringDate(Date gatheringDate) {
		this.gatheringDate = gatheringDate;
	}

	public LocationDto getGatheringAddress() {
		return gatheringAddress;
	}

	public void setGatheringAddress(LocationDto gatheringAddress) {
		this.gatheringAddress = gatheringAddress;
	}

	public static EpiDataGatheringDto build() {

		EpiDataGatheringDto dto = new EpiDataGatheringDto();
		dto.setUuid(DataHelper.createUuid());
		LocationDto location = LocationDto.build();
		dto.setGatheringAddress(location);
		return dto;
	}
}
