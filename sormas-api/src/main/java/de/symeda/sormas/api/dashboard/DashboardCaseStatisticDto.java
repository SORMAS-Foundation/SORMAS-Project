package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Map;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.CaseClassification;
import io.swagger.v3.oas.annotations.media.Schema;

@AuditedClass
@Schema(description = "Data transfer object for statistical data about cases of a researched disease")
public class DashboardCaseStatisticDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207648L;
	public static final String I18N_PREFIX = "CaseStatistics";

	@Schema(description = "Map containing case classification and the number of cases assigned the classification during the primary time period.")
	private Map<CaseClassification, Integer> caseClassificationCount;
	@Schema(description = "Number of cases newly registered during the primary time period")
	private Integer newCases;
	@Schema(description = "Number of fatalities caused by the researched disease during the primary time period")
	private Long fatalityCount;
	@Schema(description = "Fatality rate calculated over all registered cases during the primary time period")
	private Float fatalityRate;
	@Schema(description = "Number of districts that have declared an outbreak during the primary time period")
	private Long outbreakDistrictCount;
	@Schema(description = "Number of cases that are quarantined during the primary time period")
	private Long quarantineCaseCount;
	@Schema(description = "Number of cases placed in quarantine during the primary time period")
	private Long quarantinePlacedCaseCount;
	@Schema(description = "Numer of cases that have fulfilled the reference definition of a fulfilled case during the primary time period")
	private Long referenceDefinitionFulfilledCaseCount;
	@Schema(description = "Number of cases that have resulted from a contact during the primary time period")
	private Long contactResultingCaseCount;
	@Schema(description = "Name of the district that last reported a case during the primary time period")
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
