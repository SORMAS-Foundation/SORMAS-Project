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
package de.symeda.sormas.api.infrastructure.district;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Light-weight index information on districts for larger queries")
public class DistrictIndexDto extends EntityDto {

	private static final long serialVersionUID = -1445387465599056704L;
	public static final int CASE_INCIDENCE_DIVISOR = 100000;

	public static final String I18N_PREFIX = "District";

	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String POPULATION = "population";
	public static final String GROWTH_RATE = "growthRate";
	public static final String REGION = "region";
	public static final String EXTERNAL_ID = "externalID";

	@Schema(description = "Name of the District")
	private String name;
	@Schema(description = "District's Eligible Party Identification Code")
	private String epidCode;
	@Schema(description = "Population number in the district")
	private Integer population;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private Float growthRate;
	private RegionReferenceDto region;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String externalID;

	public DistrictIndexDto() {
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

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	@Override
	public String buildCaption() {
		return getName();
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}

	public Integer getPopulation() {
		return population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}

	public Float getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(Float growthRate) {
		this.growthRate = growthRate;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public DistrictReferenceDto toReference() {
		return new DistrictReferenceDto(getUuid(), name, externalID);
	}

	public static DistrictIndexDto build() {
		DistrictIndexDto dto = new DistrictIndexDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}
}
