/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.infrastructure.community;

import de.symeda.sormas.api.utils.FeatureIndependent;
import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

@FeatureIndependent
public class CommunityDto extends InfrastructureDto {

	private static final long serialVersionUID = -8833267932522978860L;

	public static final String I18N_PREFIX = "Community";

	public static final String NAME = "name";
	public static final String GROWTH_RATE = "growthRate";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String EXTERNAL_ID = "externalID";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String name;
	private Float growthRate;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private boolean archived;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalID;

	public CommunityDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		boolean archived,
		String name,
		Float growthRate,
		String regionUuid,
		String regionName,
		String regionExternalId,
		String districtUuid,
		String districtName,
		String districtExternalId,
		String externalID) {

		super(creationDate, changeDate, uuid);
		this.archived = archived;
		this.name = name;
		this.growthRate = growthRate;
		this.region = new RegionReferenceDto(regionUuid, regionName, regionExternalId);
		this.district = new DistrictReferenceDto(districtUuid, districtName, districtExternalId);
		this.externalID = externalID;
	}

	public CommunityDto() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(Float growthRate) {
		this.growthRate = growthRate;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
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

	public CommunityReferenceDto toReference() {
		return new CommunityReferenceDto(getUuid(), getName(), getExternalID());
	}

	@Override
	public String toString() {
		return getName();
	}

	public static CommunityDto build() {
		CommunityDto dto = new CommunityDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}
}
