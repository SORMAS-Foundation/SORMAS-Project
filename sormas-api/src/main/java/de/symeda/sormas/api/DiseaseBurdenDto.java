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
package de.symeda.sormas.api;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.symeda.sormas.api.Disease;

/**
 * @JsonInclude We don't need to transfer properties with a null value. This
 *              will reduce data transferred to something between 20% and 50% -
 *              especially for fields that are not needed for all diseases
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DiseaseBurdenDto {
	private Disease disease;
	private int caseCount;
	private int previousCaseCount;
	private int eventCount;
	private int outbreakDistrictCount;
	private Float caseFatalityRate;
	
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public int getCaseCount() {
		return caseCount;
	}
	public void setCaseCount(int caseCount) {
		this.caseCount = caseCount;
	}
	
	public int getPreviousCaseCount() {
		return previousCaseCount;
	}
	public void setPreviousCaseCount(int previousCaseCount) {
		this.previousCaseCount = previousCaseCount;
	}
	
	public int getEventCount() {
		return eventCount;
	}
	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}
	
	public int getOutbreakDistrictCount() {
		return outbreakDistrictCount;
	}
	public void setOutbreakDistrictCount(int outbreakDistrictCount) {
		this.outbreakDistrictCount = outbreakDistrictCount;
	}
	
	public Float getCaseFatalityRate() {
		return caseFatalityRate;
	}
	public void setCaseFatalityRate(Float caseFatalityRate) {
		this.caseFatalityRate = caseFatalityRate;
	}
}
