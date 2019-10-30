package de.symeda.sormas.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;

import de.symeda.sormas.api.infrastructure.InfrastructureHelper;

public class CaseCountDto implements Serializable {
	
	private static final long serialVersionUID = 8900419282951754875L;

	private Integer caseCount;
	private Integer population;
	private Object rowKey;
	private Object columnKey;
	
	public CaseCountDto(Integer caseCount, Integer population, Object rowKey, Object columnKey) {
		super();
		this.caseCount = caseCount;
		this.population = population;
		this.rowKey = rowKey;
		this.columnKey = columnKey;
	}
	
	public Integer getCaseCount() {
		return caseCount;
	}
	public void setCaseCount(Integer caseCount) {
		this.caseCount = caseCount;
	}
	public Integer getPopulation() {
		return population;
	}
	public void setPopulation(Integer population) {
		this.population = population;
	}
	public Object getRowKey() {
		return rowKey;
	}
	public void setRowKey(Object rowKey) {
		this.rowKey = rowKey;
	}
	public Object getColumnKey() {
		return columnKey;
	}
	public void setColumnKey(Object columnKey) {
		this.columnKey = columnKey;
	}
	
	public BigDecimal getIncidence(int divisor) {
		if (caseCount == null || population == null) {
			return null;
		}
		
		return InfrastructureHelper.getCaseIncidence(caseCount.intValue(), population.intValue(), divisor);
	}
}