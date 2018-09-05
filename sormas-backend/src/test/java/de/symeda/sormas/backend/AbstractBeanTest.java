package de.symeda.sormas.backend;

import org.junit.Before;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.report.WeeklyReportEntryFacade;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleTestFacade;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.report.WeeklyReportEntryFacadeEjb.WeeklyReportEntryFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleTestFacadeEjb.SampleTestFacadeEjbLocal;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitFacadeEjb.VisitFacadeEjbLocal;
import info.novatec.beantest.api.BaseBeanTest;

public class AbstractBeanTest extends BaseBeanTest {

	protected final TestDataCreator creator = new TestDataCreator(this);
	
	/**
	 * Resets mocks to their initial state so that mock configurations are not shared between tests.
	 */
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}
	
	public ConfigFacade getConfigFacade() {
		return getBean(ConfigFacadeEjbLocal.class);
	}

	public CaseFacade getCaseFacade() {
		return getBean(CaseFacadeEjbLocal.class);
	}
	
	public CaseClassificationFacadeEjb getCaseClassificationLogic() {
		return getBean(CaseClassificationFacadeEjb.class);
	}
	
	public ContactFacade getContactFacade() {
		return getBean(ContactFacadeEjbLocal.class);
	}
	
	public ContactService getContactService() {
		return getBean(ContactService.class);
	}
	
	public EventFacade getEventFacade() {
		return getBean(EventFacadeEjbLocal.class);
	}
	
	public EventParticipantFacade getEventParticipantFacade() {
		return getBean(EventParticipantFacadeEjbLocal.class);
	}
	
	public VisitFacade getVisitFacade() {
		return getBean(VisitFacadeEjbLocal.class);
	}
	
	public PersonFacade getPersonFacade() {
		return getBean(PersonFacadeEjbLocal.class);
	}

	public TaskFacade getTaskFacade() {
		return getBean(TaskFacadeEjbLocal.class);
	}

	public SampleFacade getSampleFacade() {
		return getBean(SampleFacadeEjbLocal.class);
	}
	
	public SampleTestFacade getSampleTestFacade() {
		return getBean(SampleTestFacadeEjbLocal.class);
	}
	
	public SymptomsFacade getSymptomsFacade() {
		return getBean(SymptomsFacadeEjbLocal.class);
	}

	public FacilityFacade getFacilityFacade() {
		return getBean(FacilityFacadeEjbLocal.class);
	}
	
	public RegionFacade getRegionFacade() {
		return getBean(RegionFacadeEjbLocal.class);
	}
	
	public DistrictFacade getDistrictFacade() {
		return getBean(DistrictFacadeEjbLocal.class);
	}
	
	public CommunityFacade getCommunityFacade() {
		return getBean(CommunityFacadeEjbLocal.class);
	}

	public UserFacade getUserFacade() {
		return getBean(UserFacadeEjbLocal.class);
	}
	
	public HospitalizationFacade getHospitalizationFacade() {
		return getBean(HospitalizationFacadeEjbLocal.class);
	}
	
	public EpiDataFacade getEpiDataFacade() {
		return getBean(EpiDataFacadeEjbLocal.class);
	}
	
	public WeeklyReportFacade getWeeklyReportFacade() {
		return getBean(WeeklyReportFacadeEjbLocal.class);
	}
	
	public WeeklyReportEntryFacade getWeeklyReportEntryFacade() {
		return getBean(WeeklyReportEntryFacadeEjbLocal.class);
	}
	
	public GeoShapeProvider getGeoShapeProvider() {
		return getBean(GeoShapeProviderEjbLocal.class);
	}
	
	public OutbreakFacade getOutbreakFacade() {
		return getBean(OutbreakFacadeEjbLocal.class);
	}
	
	public ImportFacade getImportFacade() {
		return getBean(ImportFacadeEjbLocal.class);
	}
	
	public FacilityService getFacilityService() {
		return getBean(FacilityService.class);
	}
	
	public RegionService getRegionService() {
		return getBean(RegionService.class);
	}
	
	public DistrictService getDistrictService() {
		return getBean(DistrictService.class);
	}
	
	public CommunityService getCommunityService() {
		return getBean(CommunityService.class);
	}
	
}
