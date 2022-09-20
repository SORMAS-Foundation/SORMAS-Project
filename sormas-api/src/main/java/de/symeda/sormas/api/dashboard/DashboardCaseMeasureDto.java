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
import java.math.BigDecimal;
import java.util.Map;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;

@AuditedClass
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
