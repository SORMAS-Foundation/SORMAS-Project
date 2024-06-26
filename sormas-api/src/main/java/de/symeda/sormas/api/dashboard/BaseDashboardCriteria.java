/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditExcludeProperty;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;

public class BaseDashboardCriteria<SELF extends BaseDashboardCriteria<?>> extends BaseCriteria {

	@AuditExcludeProperty
	protected final SELF self;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private Date dateFrom;
	private Date dateTo;
	private Date previousDateFrom;
	private Date previousDateTo;

	protected Class<? extends CriteriaDateType> criteriaDateType;
	protected NewCaseDateType dateTypeClass;

	protected BaseDashboardCriteria(final Class<SELF> selfClass) {
		self = selfClass.cast(this);
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public SELF region(RegionReferenceDto region) {
		this.region = region;
		return self;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public SELF district(DistrictReferenceDto district) {
		this.district = district;
		return self;
	}

	public Disease getDisease() {
		return disease;
	}

	public SELF disease(Disease disease) {
		this.disease = disease;
		return self;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public SELF dateBetween(Date dateFrom, Date dateTo) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;

		return self;
	}

	public Date getPreviousDateFrom() {
		return previousDateFrom;
	}

	public Date getPreviousDateTo() {
		return previousDateTo;
	}

}
