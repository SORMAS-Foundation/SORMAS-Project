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
package de.symeda.sormas.api.disease;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.location.LocationReferenceDto;

public class DiseaseBurdenDto  implements Serializable {
	
	private static final long serialVersionUID = 2430932452606853497L;
	
	public static final String DISEASE = "disease";
	public static final String CASE_COUNT = "caseCount";
	public static final String PREVIOUS_CASE_COUNT = "previousCaseCount";
	public static final String EVENT_COUNT = "eventCount";
	public static final String OUTBREAK_DISTRICT_COUNT = "outbreakDistrictCount";
	public static final String CASE_FATALITY_RATE = "caseFatalityRate";
	
	private Disease disease;
	private int caseCount;
	private int previousCaseCount;
	private int eventCount;
	private int outbreakDistrictCount;
	private Float caseFatalityRate;
	
	public DiseaseBurdenDto(Disease disease, int caseCount, int previousCaseCount, int eventCount, int outbreakDistrictCount, Float caseFatalityRate) {
		this.disease = disease;
		this.caseCount = caseCount;
		this.previousCaseCount = previousCaseCount;
		this.eventCount = eventCount;
		this.outbreakDistrictCount = outbreakDistrictCount;
		this.caseFatalityRate = caseFatalityRate;
	}
	
	public DiseaseBurdenDto(Disease disease, int caseCount) {
		this.disease = disease;
		this.caseCount = caseCount;
		this.previousCaseCount = 0;
		this.eventCount = 0;
		this.outbreakDistrictCount = 0;
		this.caseFatalityRate = 0.00f;
	}
	
	public DiseaseBurdenDto(Disease disease) {
		this.disease = disease;
		this.caseCount = 0;
		this.previousCaseCount = 0;
		this.eventCount = 0;
		this.outbreakDistrictCount = 0;
		this.caseFatalityRate = 0.00f;
	}
	
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
