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
package de.symeda.sormas.backend.disease;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DiseaseFacadeEjbTest  extends AbstractBeanTest {

    private TestDataCreator.RDCF rdcf;

    @Override
    public void init() {

        super.init();

        rdcf = creator.createRDCF();
        loginWith(creator.createSurveillanceSupervisor(rdcf));
    }

    @Test
    public void testGetDiseaseForDashboard() {

        TestDataCreator.RDCF rdcf2 = creator.createRDCF("Region2", "District2", "Community2", "Facility2");

        RegionReferenceDto region = new RegionReferenceDto(rdcf2.region.getUuid());

        DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
        Disease disease = Disease.EVD;
        Date fromDate = new Date();
        Date toDate = new Date();
        Date previousFrom = new Date(fromDate.getTime() - 1000L * 60 * 60 * 24 * 7); // 1 week before fromDate
        Date previousTo = new Date(toDate.getTime() - 1000L * 60 * 60 * 24 * 7); // 1 week before toDate
        CriteriaDateType newCaseDateType = NewCaseDateType.MOST_RELEVANT;
        CaseClassification caseClassification = CaseClassification.CONFIRMED;

        DiseaseBurdenDto result = getDashboardFacade().getDiseaseForDashboard(
                region,
                district,
                disease,
                fromDate,
                toDate,
                previousFrom,
                previousTo,
                newCaseDateType,
                caseClassification
        );

        assertNotNull(result);
        assertEquals(disease, result.getDisease());
        assertEquals(0L, result.getCaseCount().longValue());
        assertEquals(0L, result.getPreviousCaseCount().longValue());
        assertEquals(0L, result.getEventCount().longValue());
        assertEquals(0L, result.getOutbreakDistrictCount().longValue());
        assertEquals(0L, result.getCaseDeathCount());
        assertEquals("", result.getLastReportedDistrictName());
        assertEquals("", result.getOutbreakDistrict());
        assertEquals(fromDate, result.getFromDate());
        assertEquals(toDate, result.getToDate());
    }

    @Test
    public void testGetDiseaseGridForDashboard() {

        TestDataCreator.RDCF rdcf2 = creator.createRDCF("Region2", "District2", "Community2", "Facility2");

        RegionReferenceDto region = new RegionReferenceDto(rdcf2.region.getUuid());

        DistrictReferenceDto district = new DistrictReferenceDto(rdcf.district.getUuid(), null, null);
        Disease disease = Disease.EVD;
        Date fromDate = new Date();
        Date toDate = new Date();
        Date previousFrom = new Date(fromDate.getTime() - 1000L * 60 * 60 * 24 * 7); // 1 week before fromDate
        Date previousTo = new Date(toDate.getTime() - 1000L * 60 * 60 * 24 * 7); // 1 week before toDate
        CriteriaDateType newCaseDateType = NewCaseDateType.MOST_RELEVANT;
        CaseClassification caseClassification = CaseClassification.CONFIRMED;

        DiseaseBurdenDto result = getDashboardFacade().getDiseaseGridForDashboard(
                region,
                district,
                disease,
                fromDate,
                toDate,
                previousFrom,
                previousTo,
                newCaseDateType,
                caseClassification
        );

        assertNotNull(result);
        assertEquals(region.getUuid(), result.getRegion().getUuid());
        assertEquals("0", result.getTotal());
        assertEquals("0", result.getActiveCases());
        assertEquals("0", result.getRecovered());
        assertEquals("0", result.getDeaths());
        assertEquals("0", result.getOther());
    }
}