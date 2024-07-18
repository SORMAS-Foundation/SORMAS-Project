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
        assertEquals(region.getUuid(), result.getRegion().getUuid());
        assertEquals("0", result.getTotal());
        assertEquals("0", result.getActiveCases());
        assertEquals("0", result.getRecovered());
        assertEquals("0", result.getDeaths());
        assertEquals("0", result.getOther());
    }


}
