package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import io.swagger.v3.oas.annotations.media.Schema;

@AuditedClass
@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
public class DashboardCaseMeasureDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207658L;

	@Schema(description = "Map containing each district and the corresponding case measeure, calculated by dividing the number of cases"
		+ " within the district by DistrictDto.CASE_INCIDENCE_DIVISOR. Calculation result is rounded to two decimal places with rounding mode half up")
	private Map<DistrictDto, BigDecimal> caseMeasurePerDistrict;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private BigDecimal districtValuesLowerQuartile;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private BigDecimal districtValuesMedianQuartile;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
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
