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
package de.symeda.sormas.api.region;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class RegionDto extends EntityDto {

	private static final long serialVersionUID = -1610675328037466348L;

	public static final String I18N_PREFIX = "Region";
	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String GROWTH_RATE = "growthRate";
	public static final String EXTERNAL_ID = "externalID";

	private String name;
	private String epidCode;
	private Float growthRate;
	private boolean archived;
	private String externalID;

	public RegionDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		boolean archived,
		String name,
		String epidCode,
		Float growthRate,
		String externalID) {

		super(creationDate, changeDate, uuid);
		this.archived = archived;
		this.name = name;
		this.epidCode = epidCode;
		this.growthRate = growthRate;
		this.externalID = externalID;
	}

	public RegionDto() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEpidCode() {
		return epidCode;
	}

	public void setEpidCode(String epidCode) {
		this.epidCode = epidCode;
	}

	@Override
	public String toString() {
		return getName();
	}

	public Float getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(Float growthRate) {
		this.growthRate = growthRate;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public RegionReferenceDto toReference() {
		return new RegionReferenceDto(getUuid());
	}

	public static RegionDto build() {

		RegionDto dto = new RegionDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}
}
