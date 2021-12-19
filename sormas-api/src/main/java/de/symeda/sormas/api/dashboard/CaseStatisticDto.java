package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Map;

import de.symeda.sormas.api.caze.CaseClassification;

public class CaseStatisticDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207648L;
	public static final String I18N_PREFIX = "CaseStatistics";

	private Map<CaseClassification, Integer> caseClassificationCount;

	private Long fatalityCount;
	private Float fatalityRate;
	private Long outbreakDistrictCount;
	private Long quarantineCaseCount;
	private Long quarantinePlacedCaseCount;
	private Long referenceDefinitionFulfilledCaseCount;
	private Long contactResultingCaseCount;
	private String lastReportedDistrict;

	public CaseStatisticDto(
		Map<CaseClassification, Integer> caseClassificationCount,
		Long fatalityCount,
		Float fatalityRate,
		Long outbreakDistrictCount,
		Long quarantineCaseCount,
		Long quarantinePlacedCaseCount,
		Long referenceDefinitionFulfilledCaseCount,
		Long contactResultingCaseCount,
		String lastReportedDistrict) {
		this.caseClassificationCount = caseClassificationCount;
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
