package de.symeda.sormas.backend.disease;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.dashboard.DashboardService;
import de.symeda.sormas.backend.infrastructure.district.District;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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

        DiseaseBurdenDto result = getDiseaseFacade().getDiseaseForDashboard(
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
        assertEquals(fromDate, result.getFrom());
        assertEquals(toDate, result.getTo());
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

        DiseaseBurdenDto result = getDiseaseFacade().getDiseaseGridForDashboard(
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
        assertEquals(region, result.getRegion());
        assertEquals("0", result.getTotal());
        assertEquals("0", result.getActiveCases());
        assertEquals("0", result.getRecovered());
        assertEquals("0", result.getDeaths());
        assertEquals("0", result.getOther());
    }


}
