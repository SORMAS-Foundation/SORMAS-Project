package de.symeda.sormas.ui.dashboard.disease;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseFacadeEjb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.dashboard.DashboardService;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;

public class DiseaseDashboardServiceTest {

    @Mock
    private CaseFacadeEjbLocal caseFacade;

    @Mock
    private EventFacadeEjbLocal eventFacade;

    @Mock
    private OutbreakFacadeEjbLocal outbreakFacade;

    @Mock
    private PersonFacadeEjbLocal personFacade;

    @Mock
    private DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

    @Mock
    private RegionFacadeEjbLocal regionFacade;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

    @InjectMocks
    private DiseaseFacadeEjb diseaseFacade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDiseaseForDashboard() {
        RegionReferenceDto region = new RegionReferenceDto();
        DistrictReferenceDto district = new DistrictReferenceDto();
        Disease disease = Disease.CORONAVIRUS;
        Date fromDate = new Date();
        Date toDate = new Date();
        Date previousFrom = new Date();
        Date previousTo = new Date();
        CriteriaDateType newCaseDateType = NewCaseDateType.MOST_RELEVANT;
        CaseClassification caseClassification = null;

        // Mocking responses for dashboardService, eventFacade, outbreakFacade, etc.
        Map<Disease, Long> newCases = new HashMap<>();
        newCases.put(disease, 8L);
        when(dashboardService.getCaseCountByDisease(any(DashboardCriteria.class))).thenReturn(newCases);

        Map<Disease, Long> events = new HashMap<>();
        events.put(disease, 5L);
        when(eventFacade.getEventCountByDisease(any(EventCriteria.class))).thenReturn(events);

        Map<Disease, District> outbreakDistricts = new HashMap<>();
        outbreakDistricts.put(disease, new District());
        when(outbreakFacade.getOutbreakDistrictNameByDisease(any(OutbreakCriteria.class))).thenReturn(outbreakDistricts);

        Map<Disease, District> lastReportedDistricts = new HashMap<>();
        lastReportedDistricts.put(disease, new District());
        when(dashboardService.getLastReportedDistrictByDisease(any(DashboardCriteria.class))).thenReturn(lastReportedDistricts);

        Map<Disease, Long> caseFatalities = new HashMap<>();
        caseFatalities.put(disease, 1L);
        when(dashboardService.getDeathCountByDisease(any(DashboardCriteria.class))).thenReturn(caseFatalities);

        Map<Disease, Long> previousCases = new HashMap<>();
        previousCases.put(disease, 8L);
        when(dashboardService.getCaseCountByDisease(any(DashboardCriteria.class))).thenReturn(previousCases);

        // Invoke the method under test
        DiseaseBurdenDto result = diseaseFacade.getDiseaseForDashboard(
                region, district, disease, fromDate, toDate, previousFrom, previousTo, newCaseDateType, caseClassification);

        // Assert expected results
        assertEquals(8L, result.getCaseCount());
        assertEquals(8L, result.getPreviousCaseCount());
        assertEquals(5L, result.getEventCount());
        assertNull(result.getOutbreakDistrict());
        assertEquals(1L, result.getCaseDeathCount());
        assertEquals(Disease.CORONAVIRUS, result.getDisease());

    }
    @Test
    public void testGetDiseaseGridForDashboard() {
        RegionReferenceDto region = new RegionReferenceDto();
        DistrictReferenceDto district = new DistrictReferenceDto();
        Disease disease = Disease.CORONAVIRUS;
        Date fromDate = new Date();
        Date toDate = new Date();
        Date previousFrom = new Date();
        Date previousTo = new Date();
        CriteriaDateType newCaseDateType = NewCaseDateType.MOST_RELEVANT;
        CaseClassification caseClassification = null;

        RegionDto regionDto = new RegionDto();
        Map<Disease, Long> allCasesFetched = new HashMap<>();
        allCasesFetched.put(disease, 15L);
        Map<Disease, Long> caseFatalities = new HashMap<>();
        caseFatalities.put(disease, 2L);

        when(regionFacade.getByUuid(region.getUuid())).thenReturn(regionDto);
        when(dashboardService.getCaseCountByDisease(any(DashboardCriteria.class)))
                .thenReturn(allCasesFetched);
        when(dashboardService.getDeathCountByDisease(any(DashboardCriteria.class)))
                .thenReturn(caseFatalities);

        DiseaseBurdenDto result = diseaseFacade.getDiseaseGridForDashboard(
                region, district, disease, fromDate, toDate, previousFrom, previousTo, newCaseDateType, caseClassification);

        assertEquals("15", result.getTotal());
        assertEquals("2", result.getDeaths());
    }
}
