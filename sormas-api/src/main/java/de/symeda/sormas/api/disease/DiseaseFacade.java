package de.symeda.sormas.api.disease;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;


@Remote
public interface DiseaseFacade {

    List<DiseaseBurdenDto> getDiseaseBurdenForDashboard(
            RegionReferenceDto regionRef,
            DistrictReferenceDto districtRef,
            Date from,
            Date to,
            Date previousFromDate,
            Date previousToDate);


    DiseaseBurdenDto getDiseaseForDashboard(
            RegionReferenceDto regionRef,
            DistrictReferenceDto districtRef,
            Disease disease,
            Date fromDate,
            Date toDate,
            Date previousFromDate,
            Date previousToDate,
            CriteriaDateType newCaseDateType,
            CaseClassification caseClassification
    );
    //
    DiseaseBurdenDto getDiseaseGridForDashboard(
            RegionReferenceDto regionRef,
            DistrictReferenceDto districtRef,
            Disease disease,
            Date from,
            Date to,
            Date previousFromDate,
            Date previousToDate,
            CriteriaDateType newCaseDateType,
            CaseClassification caseClassification
    );
}

