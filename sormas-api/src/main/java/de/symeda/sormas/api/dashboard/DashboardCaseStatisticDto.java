/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Map;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.CaseClassification;

@AuditedClass
public class DashboardCaseStatisticDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207648L;
	public static final String I18N_PREFIX = "CaseStatistics";

	private Map<CaseClassification, Integer> caseClassificationCount;
	private Integer newCases;
	private Long fatalityCount;
	private Float fatalityRate;
	private Long outbreakDistrictCount;
	private Long quarantineCaseCount;
	private Long quarantinePlacedCaseCount;
	private Long referenceDefinitionFulfilledCaseCount;
	private Long contactResultingCaseCount;
	@AuditInclude
	private String lastReportedDistrict;

	public DashboardCaseStatisticDto(
		Map<CaseClassification, Integer> caseClassificationCount,
		Integer newCases,
		Long fatalityCount,
		Float fatalityRate,
		Long outbreakDistrictCount,
		Long quarantineCaseCount,
		Long quarantinePlacedCaseCount,
		Long referenceDefinitionFulfilledCaseCount,
		Long contactResultingCaseCount,
		String lastReportedDistrict) {
		this.caseClassificationCount = caseClassificationCount;
		this.newCases = newCases;
		this.fatalityCount = fatalityCount;
		this.fatalityRate = fatalityRate;
		this.outbreakDistrictCount = outbreakDistrictCount;
		this.quarantineCaseCount = quarantineCaseCount;
		this.quarantinePlacedCaseCount = quarantinePlacedCaseCount;
		this.referenceDefinitionFulfilledCaseCount = referenceDefinitionFulfilledCaseCount;
		this.contactResultingCaseCount = contactResultingCaseCount;
		this.lastReportedDistrict = lastReportedDistrict;
	}

	public Map<CaseClassification, Integer> getCaseClassificationCount() {
		return caseClassificationCount;
	}

	public Integer getNewCases() {
		return newCases;
	}

	public Long getFatalityCount() {
		return fatalityCount;
	}

	public Float getFatalityRate() {
		return fatalityRate;
	}

	public Long getOutbreakDistrictCount() {
		return outbreakDistrictCount;
	}

	public Long getQuarantineCaseCount() {
		return quarantineCaseCount;
	}

	public Long getQuarantinePlacedCaseCount() {
		return quarantinePlacedCaseCount;
	}

	public Long getReferenceDefinitionFulfilledCaseCount() {
		return referenceDefinitionFulfilledCaseCount;
	}

	public Long getContactResultingCaseCount() {
		return contactResultingCaseCount;
	}

	public String getLastReportedDistrict() {
		return lastReportedDistrict;
	}
}
