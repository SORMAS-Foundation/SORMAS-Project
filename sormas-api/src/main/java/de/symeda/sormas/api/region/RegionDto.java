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
package de.symeda.sormas.api.region;

import de.symeda.sormas.api.EntityDto;

public class RegionDto extends EntityDto {

	private static final long serialVersionUID = -1610675328037466348L;

	public static final String I18N_PREFIX = "Region";
	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String POPULATION = "population";
	public static final String GROWTH_RATE = "growthRate";
	
	private String name;
	private String epidCode;
	private Integer population;
	private Float growthRate;
	
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
	
	public RegionReferenceDto toReference() {
		return new RegionReferenceDto(getUuid());
	}
	
	public static RegionDto build() {
		RegionDto dto = new RegionDto();
		return dto;
	}
}
