package de.symeda.sormas.api.outbreak;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class OutbreakCriteriaTest {

    @Test
    public void testSetAndGetRegion() {
        RegionReferenceDto region = new RegionReferenceDto();
        OutbreakCriteria criteria = new OutbreakCriteria().region(region);
        assertEquals(region, criteria.getRegion());
    }

    @Test
    public void testSetAndGetDistrict() {
        DistrictReferenceDto district = new DistrictReferenceDto();
        OutbreakCriteria criteria = new OutbreakCriteria().district(district);
        assertEquals(district, criteria.getDistrict());
    }

    @Test
    public void testSetAndGetDiseases() {
        Set<Disease> diseases = new HashSet<>();
        diseases.add(Disease.CORONAVIRUS);
        OutbreakCriteria criteria = new OutbreakCriteria().diseases(diseases);
        assertEquals(diseases, criteria.getDiseases());
    }

    @Test
    public void testSetAndGetDisease() {
        Disease disease = Disease.CORONAVIRUS;
        OutbreakCriteria criteria = new OutbreakCriteria().disease(disease);
        assertEquals(Collections.singleton(disease), criteria.getDiseases());
    }

    @Test
    public void testSetAndGetActive() {
        OutbreakCriteria criteria = new OutbreakCriteria().active(true);
        assertTrue(criteria.getActive());
        criteria.active(false);
        assertFalse(criteria.getActive());
    }

    @Test
    public void testSetAndGetActiveWithDates() {
        Date lower = new Date();
        Date upper = new Date();
        OutbreakCriteria criteria = new OutbreakCriteria().active(true, lower, upper);
        assertTrue(criteria.getActive());
        assertEquals(lower, criteria.getActiveLower());
        assertEquals(upper, criteria.getActiveUpper());
    }

    @Test
    public void testSetAndGetChangeDateAfter() {
        Date changeDate = new Date();
        OutbreakCriteria criteria = new OutbreakCriteria().changeDateAfter(changeDate);
        assertEquals(changeDate, criteria.getChangeDateAfter());
    }

    @Test
    public void testSetAndGetReportedBetween() {
        Date reportedFrom = new Date();
        Date reportedTo = new Date();
        OutbreakCriteria criteria = new OutbreakCriteria().reportedBetween(reportedFrom, reportedTo);
        assertEquals(reportedFrom, criteria.getReportedDateFrom());
        assertEquals(reportedTo, criteria.getReportedDateTo());
    }

    @Test
    public void testSetAndGetReportedDateFrom() {
        Date reportedFrom = new Date();
        OutbreakCriteria criteria = new OutbreakCriteria().reportedDateFrom(reportedFrom);
        assertEquals(reportedFrom, criteria.getReportedDateFrom());
    }

    @Test
    public void testSetAndGetReportedDateTo() {
        Date reportedTo = new Date();
        OutbreakCriteria criteria = new OutbreakCriteria().reportedDateTo(reportedTo);
        assertEquals(reportedTo, criteria.getReportedDateTo());
    }

    @Test
    public void testSetAndGetCaseClassification() {
        CaseClassification caseClassification = CaseClassification.CONFIRMED;
        OutbreakCriteria criteria = new OutbreakCriteria().caseClassification(caseClassification);
        assertEquals(caseClassification, criteria.getCaseClassification());
    }
}