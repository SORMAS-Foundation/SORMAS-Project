package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import de.symeda.sormas.api.infrastructure.district.DistrictDto;

public class DashboardCaseMeasureDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207658L;

	private Map<DistrictDto, BigDecimal> caseMeasurePerDistrict;
	private BigDecimal districtValuesLowerQuartile;
	private BigDecimal districtValuesMedianQuartile;
	private BigDecimal districtValuesUpperQuartile;

	public DashboardCaseMeasureDto(
		Map<DistrictDto, BigDecimal> caseMeasurePerDistrict,
		BigDecimal districtValuesLowerQuartile,
		BigDecimal districtValuesMedianQuartile,
		BigDecimal districtValuesUpperQuartile) {
		this.caseMeasurePerDistrict = caseMeasurePerDistrict;
		this.districtValuesLowerQuartile = districtValuesLowerQuartile;
		this.districtValuesMedianQuartile = districtValuesMedianQuartile;
		this.districtValuesUpperQuartile = districtValuesUpperQuartile;
	}

	public Map<DistrictDto, BigDecimal> getCaseMeasurePerDistrict() {
		return caseMeasurePerDistrict;
	}

	public BigDecimal getDistrictValuesLowerQuartile() {
		return districtValuesLowerQuartile;
	}

	public BigDecimal getDistrictValuesMedianQuartile() {
		return districtValuesMedianQuartile;
	}

	public BigDecimal getDistrictValuesUpperQuartile() {
		return districtValuesUpperQuartile;
	}
}
