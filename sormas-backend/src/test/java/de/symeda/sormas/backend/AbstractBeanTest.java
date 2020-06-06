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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.symeda.sormas.api.user.UserDto;
import org.junit.Before;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseStatisticsFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitFacade;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.api.disease.DiseaseFacade;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.PopulationDataFacade;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.region.CommunityFacade;
import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.sample.AdditionalTestFacade;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.therapy.PrescriptionFacade;
import de.symeda.sormas.api.therapy.TherapyFacade;
import de.symeda.sormas.api.therapy.TreatmentFacade;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRoleConfigFacade;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseStatisticsFacadeEjb.CaseStatisticsFacadeEjbLocal;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb.ClinicalVisitFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationService;
import de.symeda.sormas.backend.disease.DiseaseFacadeEjb.DiseaseFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb.PrescriptionFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb.TherapyFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb.TreatmentFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.visit.VisitFacadeEjb.VisitFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitService;
import info.novatec.beantest.api.BaseBeanTest;

public class AbstractBeanTest extends BaseBeanTest {

	protected final TestDataCreator creator = new TestDataCreator(this);

	/**
	 * Resets mocks to their initial state so that mock configurations are not
	 * shared between tests.
	 */
	@Before
	public void init() {
		MockProducer.resetMocks();
		initH2Functions();
		
		creator.createUser(null, null, null, "ad", "min", UserRole.ADMIN, UserRole.NATIONAL_USER);
		when(MockProducer.getPrincipal().getName()).thenReturn("admin");

		I18nProperties.setUserLanguage(Language.EN);
	}

	private void initH2Functions() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		Query nativeQuery = em.createNativeQuery("CREATE ALIAS similarity FOR \"de.symeda.sormas.backend.H2Function.similarity\"");
		nativeQuery.executeUpdate();
		nativeQuery = em.createNativeQuery("CREATE ALIAS date_part FOR \"de.symeda.sormas.backend.H2Function.date_part\"");
		nativeQuery.executeUpdate();
		nativeQuery = em.createNativeQuery("CREATE ALIAS epi_week FOR \"de.symeda.sormas.backend.H2Function.epi_week\"");
		nativeQuery.executeUpdate();
		nativeQuery = em.createNativeQuery("CREATE ALIAS epi_year FOR \"de.symeda.sormas.backend.H2Function.epi_year\"");
		nativeQuery.executeUpdate();
		em.getTransaction().commit();
	}
	
	@Before
	public void createDiseaseConfigurations() {
		List<DiseaseConfiguration> diseaseConfigurations = getDiseaseConfigurationService().getAll();
		List<Disease> configuredDiseases = diseaseConfigurations.stream().map(c -> c.getDisease()).collect(Collectors.toList());
		Arrays.stream(Disease.values()).filter(d -> !configuredDiseases.contains(d)).forEach(d -> {
			DiseaseConfiguration configuration = DiseaseConfiguration.build(d);
			getDiseaseConfigurationService().ensurePersisted(configuration);
		});
	}
	
	public EntityManager getEntityManager() {
		return getBean(EntityManagerWrapper.class).getEntityManager();
	}

	public ConfigFacade getConfigFacade() {
		return getBean(ConfigFacadeEjbLocal.class);
	}

	public CaseFacade getCaseFacade() {
		return getBean(CaseFacadeEjbLocal.class);
	}
	
	public CaseService getCaseService() {
		return getBean(CaseService.class);
	}
	
	public CaseStatisticsFacade getCaseStatisticsFacade() {
		return getBean(CaseStatisticsFacadeEjbLocal.class);
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
	
	public VisitService getVisitService() {
		return getBean(VisitService.class);
	}

	public PersonFacade getPersonFacade() {
		return getBean(PersonFacadeEjbLocal.class);
	}
	
	public PersonService getPersonService() {
		return getBean(PersonService.class);
	}

	public TaskFacade getTaskFacade() {
		return getBean(TaskFacadeEjbLocal.class);
	}

	public SampleFacade getSampleFacade() {
		return getBean(SampleFacadeEjbLocal.class);
	}

	public PathogenTestFacade getSampleTestFacade() {
		return getBean(PathogenTestFacadeEjbLocal.class);
	}
	
	public AdditionalTestFacade getAdditionalTestFacade() {
		return getBean(AdditionalTestFacadeEjbLocal.class);
	}

	public SymptomsFacade getSymptomsFacade() {
		return getBean(SymptomsFacadeEjbLocal.class);
	}

	public PointOfEntryFacade getPointOfEntryFacade() {
		return getBean(PointOfEntryFacadeEjbLocal.class);
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

	public UserService getUserService() {
		return getBean(UserService.class);
	}

	public UserRoleConfigFacade getUserRoleConfigFacade() {
		return getBean(UserRoleConfigFacadeEjbLocal.class);
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

	public PointOfEntryService getPointOfEntryService() {
		return getBean(PointOfEntryService.class);
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
	
	public ClinicalCourseFacade getClinicalCourseFacade() {
		return getBean(ClinicalCourseFacadeEjbLocal.class);
	}
	
	public ClinicalVisitFacade getClinicalVisitFacade() {
		return getBean(ClinicalVisitFacadeEjbLocal.class);
	}
	
	public TherapyFacade getTherapyFacade() {
		return getBean(TherapyFacadeEjbLocal.class);
	}
	
	public PrescriptionFacade getPrescriptionFacade() {
		return getBean(PrescriptionFacadeEjbLocal.class);
	}
	
	public TreatmentFacade getTreatmentFacade() {
		return getBean(TreatmentFacadeEjbLocal.class);
	}
	
	public DiseaseConfigurationFacade getDiseaseConfigurationFacade() {
		return getBean(DiseaseConfigurationFacadeEjbLocal.class);
	}
	
	public DiseaseConfigurationService getDiseaseConfigurationService() {
		return getBean(DiseaseConfigurationService.class);
	}
	
	public PopulationDataFacade getPopulationDataFacade() {
		return getBean(PopulationDataFacadeEjbLocal.class);
	}

	public DiseaseFacade getDiseaseFacade() {
		return getBean(DiseaseFacadeEjbLocal.class);
	}
	
	public FeatureConfigurationFacade getFeatureConfigurationFacade() {
		return getBean(FeatureConfigurationFacadeEjbLocal.class);
	}
	
	public PathogenTestFacade getPathogenTestFacade() {
		return getBean(PathogenTestFacadeEjbLocal.class);
	}

	protected UserDto useSurveillanceOfficerLogin(TestDataCreator.RDCF rdcf) {
		if(rdcf == null){
			rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		}

		UserDto survOff = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Off", UserRole.SURVEILLANCE_OFFICER);
		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff");

		return survOff;
	}
}
