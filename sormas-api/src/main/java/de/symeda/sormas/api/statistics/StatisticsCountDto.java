package de.symeda.sormas.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;

import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.utils.DataHelper;

public class StatisticsCountDto implements Serializable {
	
	private static final long serialVersionUID = 8900419282951754875L;

	private Integer count;
	private Integer population;
	private StatisticsGroupingKey rowKey;
	private StatisticsGroupingKey columnKey;
	
	public StatisticsCountDto(Integer caseCount, Integer population, StatisticsGroupingKey rowKey, StatisticsGroupingKey columnKey) {
		super();
		this.count = caseCount;
		this.population = population;
		this.rowKey = rowKey;
		this.columnKey = columnKey;
	}
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer caseCount) {
		this.count = caseCount;
	}
	public Integer getPopulation() {
		return population;
	}
	public void setPopulation(Integer population) {
		this.population = population;
	}
	public StatisticsGroupingKey getRowKey() {
		return rowKey;
	}
	public void setRowKey(StatisticsGroupingKey rowKey) {
		this.rowKey = rowKey;
	}
	public StatisticsGroupingKey getColumnKey() {
		return columnKey;
	}
	public void setColumnKey(StatisticsGroupingKey columnKey) {
		this.columnKey = columnKey;
	}
	
	public BigDecimal getIncidence(int divisor) {
		if (population == null) {
			return null;
		}
		if (count == null || count.intValue() == 0) {
			return BigDecimal.ZERO;
		}
		
		return InfrastructureHelper.getCaseIncidence(count.intValue(), population.intValue(), divisor);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StatisticsCountDto) {
			StatisticsCountDto other = (StatisticsCountDto)obj;
			return DataHelper.equal(rowKey, other.rowKey) && DataHelper.equal(columnKey, other.columnKey);
		}		
		
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((rowKey == null) ? 0 : rowKey.hashCode());
		result = prime * result + ((columnKey == null) ? 0 : columnKey.hashCode());
		return result;
	}
}