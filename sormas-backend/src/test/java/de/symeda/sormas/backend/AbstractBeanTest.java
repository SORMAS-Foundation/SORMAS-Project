/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.hilling.junit.cdi.CdiTestJunitExtension;
import de.hilling.junit.cdi.ContextControlWrapper;
import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.action.ActionFacade;
import de.symeda.sormas.api.bagexport.BAGExportFacade;
import de.symeda.sormas.api.campaign.data.CampaignFormDataFacade;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionFacade;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaFacade;
import de.symeda.sormas.api.caze.CaseStatisticsFacade;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoFacade;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitFacade;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.dashboard.DashboardFacade;
import de.symeda.sormas.api.dashboard.sample.SampleDashboardFacade;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateFacade;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.document.DocumentFacade;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleFacade;
import de.symeda.sormas.api.externalemail.ExternalEmailFacade;
import de.symeda.sormas.api.externalmessage.ExternalMessageFacade;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportFacade;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.geo.GeoShapeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.ExportFacade;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.info.InfoFacade;
import de.symeda.sormas.api.infrastructure.PopulationDataFacade;
import de.symeda.sormas.api.infrastructure.area.AreaFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityFacade;
import de.symeda.sormas.api.infrastructure.continent.ContinentFacade;
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentFacade;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogFacade;
import de.symeda.sormas.api.outbreak.OutbreakFacade;
import de.symeda.sormas.api.report.AggregateReportFacade;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.sample.AdditionalTestFacade;
import de.symeda.sormas.api.sample.PathogenTestFacade;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.share.ExternalShareInfoFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptionFacade;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseFacade;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactFacade;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventFacade;
import de.symeda.sormas.api.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageFacade;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestFacade;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessFacade;
import de.symeda.sormas.api.systemevents.SystemEventFacade;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.therapy.PrescriptionFacade;
import de.symeda.sormas.api.therapy.TherapyFacade;
import de.symeda.sormas.api.therapy.TreatmentFacade;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.api.vaccination.VaccinationFacade;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.backend.action.ActionFacadeEjb;
import de.symeda.sormas.backend.bagexport.BAGExportFacadeEjb;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb.CampaignFacadeEjbLocal;
import de.symeda.sormas.backend.campaign.data.CampaignFormDataFacadeEjb.CampaignFormDataFacadeEjbLocal;
import de.symeda.sormas.backend.campaign.diagram.CampaignDiagramDefinitionFacadeEjb;
import de.symeda.sormas.backend.campaign.form.CampaignFormMetaFacadeEjb.CampaignFormMetaFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseStatisticsFacadeEjb.CaseStatisticsFacadeEjbLocal;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfoFacadeEjb.PortHealthInfoFacadeEjbLocal;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitFacadeEjb.ClinicalVisitFacadeEjbLocal;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.DefaultEntitiesCreator;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValueService;
import de.symeda.sormas.backend.dashboard.DashboardFacadeEjb;
import de.symeda.sormas.backend.dashboard.DashboardService;
import de.symeda.sormas.backend.dashboard.sample.SampleDashboardFacadeEjb;
import de.symeda.sormas.backend.deletionconfiguration.CoreEntityDeletionService;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfiguration;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfigurationService;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationService;
import de.symeda.sormas.backend.docgeneration.DocumentTemplateFacadeEjb.DocumentTemplateFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.EventDocumentFacadeEjb;
import de.symeda.sormas.backend.docgeneration.QuarantineOrderFacadeEjb;
import de.symeda.sormas.backend.document.DocumentFacadeEjb;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb.EnvironmentFacadeEjbLocal;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleFacadeEjb.EnvironmentSampleFacadeEjbLocal;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleService;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.externalemail.ExternalEmailFacadeEjb.ExternalEmailFacadeEjbLocal;
import de.symeda.sormas.backend.externaljournal.ExternalJournalService;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.externalmessage.labmessage.AutomaticLabMessageProcessor;
import de.symeda.sormas.backend.externalmessage.labmessage.ExternalMessageProcessingFacadeEjbLocal;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReportFacadeEjb;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReportService;
import de.symeda.sormas.backend.externalmessage.labmessage.TestReportFacadeEjb;
import de.symeda.sormas.backend.externalmessage.labmessage.TestReportService;
import de.symeda.sormas.backend.externalsurveillancetool.ExternalSurveillanceToolGatewayFacadeEjb.ExternalSurveillanceToolGatewayFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfiguration;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.geo.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.geocoding.GeocodingService;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.importexport.ExportFacadeEjb;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.parser.ImportParserService;
import de.symeda.sormas.backend.info.InfoFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PopulationDataFacadeEjb.PopulationDataFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb.AreaFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentService;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentService;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLogFacadeEjb.ManualMessageLogFacadeEjbLocal;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLogService;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb.OutbreakFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.report.AggregateReportFacadeEjb;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.AdditionalTestService;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestService;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.selfreport.SelfReportFacadeEjb.SelfReportFacadeEjbLocal;
import de.symeda.sormas.backend.share.ExternalShareInfoFacadeEjb.ExternalShareInfoFacadeEjbLocal;
import de.symeda.sormas.backend.share.ExternalShareInfoService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.crypto.SormasToSormasEncryptionFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.caze.ReceivedCaseProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.caze.SormasToSormasCaseDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.caze.SormasToSormasCaseFacadeEjb.SormasToSormasCaseFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.contact.ReceivedContactProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.contact.SormasToSormasContactDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.contact.SormasToSormasContactFacadeEjb.SormasToSormasContactFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.event.ReceivedEventProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.event.SormasToSormasEventDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.event.SormasToSormasEventFacadeEjb.SormasToSormasEventFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.eventparticipant.SormasToSormasEventParticipantDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageFacadeEjb.SormasToSormasExternalMessageFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.immunization.ReceivedImmunizationProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.immunization.SormasToSormasImmunizationDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.sample.ReceivedSampleProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.sample.SormasToSormasSampleDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDtoValidator;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestFacadeEJB.SormasToSormasShareRequestFacadeEJBLocal;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoFacadeEjb.SormasToSormasShareInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccessFacadeEjb.SpecialCaseAccessFacadeEjbLocal;
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccessService;
import de.symeda.sormas.backend.symptoms.SymptomsService;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.therapy.PrescriptionFacadeEjb.PrescriptionFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.PrescriptionService;
import de.symeda.sormas.backend.therapy.TherapyFacadeEjb.TherapyFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.TreatmentFacadeEjb.TreatmentFacadeEjbLocal;
import de.symeda.sormas.backend.therapy.TreatmentService;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserRoleFacadeEjb.UserRoleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserRoleService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationService;
import de.symeda.sormas.backend.visit.VisitFacadeEjb.VisitFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitService;

@ExtendWith(CdiTestJunitExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractBeanTest {

	protected final TestDataCreator creator = new TestDataCreator(this);

	protected UserDto nationalAdmin;

	@BeforeAll
	public static void beforeAll() {

	}

	@BeforeEach
	public void beforeEach() {
		// so we can override init
		init();
	}

	@AfterEach
	public void afterEach() {

	}

	/**
	 * Resets mocks to their initial state so that mock configurations are not
	 * shared between tests.
	 */
	public void init() {

		initH2Functions();

		MockProducer.resetMocks();

		// this is used to provide the current user to the ADO Listener taking care of updating the last change user
		System.setProperty("java.naming.factory.initial", MockProducer.class.getCanonicalName());
		try {
			when(InitialContext.doLookup("java:global/sormas-ear/sormas-backend/CurrentUserService")).thenReturn(getCurrentUserService());
		} catch (NamingException e) {
			e.printStackTrace();
		}

		useNationalAdminLogin();

		when(MockProducer.getSessionContext().isCallerInRole(any(String.class))).thenAnswer(invocationOnMock -> {
			String role = invocationOnMock.getArgument(0);
			UserRight userRight = UserRight.valueOf(role);
			return getCurrentUserService().getCurrentUser()
				.getUserRoles()
				.stream()
				.anyMatch(userRole -> userRole.getUserRights().contains(userRight));
		});

		I18nProperties.setUserLanguage(Language.EN);

		createDiseaseConfigurations();
	}

	protected void initH2Functions() {
		executeInTransaction(em -> {
			Query nativeQuery = em.createNativeQuery("CREATE ALIAS similarity FOR \"de.symeda.sormas.backend.H2Function.similarity\"");
			nativeQuery.executeUpdate();
			nativeQuery = em.createNativeQuery("CREATE ALIAS date_part FOR \"de.symeda.sormas.backend.H2Function.date_part\"");
			nativeQuery.executeUpdate();
			nativeQuery = em.createNativeQuery("CREATE ALIAS epi_week FOR \"de.symeda.sormas.backend.H2Function.epi_week\"");
			nativeQuery.executeUpdate();
			nativeQuery = em.createNativeQuery("CREATE ALIAS epi_year FOR \"de.symeda.sormas.backend.H2Function.epi_year\"");
			nativeQuery.executeUpdate();
			nativeQuery = em.createNativeQuery("CREATE ALIAS similarity_operator FOR \"de.symeda.sormas.backend.H2Function.similarity_operator\"");
			nativeQuery.executeUpdate();
			nativeQuery = em.createNativeQuery("CREATE ALIAS set_limit FOR \"de.symeda.sormas.backend.H2Function.set_limit\"");
			nativeQuery.executeUpdate();
			nativeQuery = em.createNativeQuery("CREATE ALIAS date FOR \"de.symeda.sormas.backend.H2Function.date\"");
			nativeQuery.executeUpdate();
			nativeQuery = em
				.createNativeQuery("CREATE ALIAS timestamp_subtract_14_days FOR \"de.symeda.sormas.backend.H2Function.timestamp_subtract_14_days\"");
			nativeQuery.executeUpdate();
			nativeQuery = em.createNativeQuery("CREATE ALIAS at_end_of_day FOR \"de.symeda.sormas.backend.H2Function.at_end_of_day\"");
			nativeQuery.executeUpdate();
		});
	}

	private void createDiseaseConfigurations() {
		List<DiseaseConfiguration> diseaseConfigurations = getDiseaseConfigurationService().getAll();
		List<Disease> configuredDiseases = diseaseConfigurations.stream().map(DiseaseConfiguration::getDisease).collect(Collectors.toList());
		Arrays.stream(Disease.values()).filter(d -> !configuredDiseases.contains(d)).forEach(d -> {
			DiseaseConfiguration configuration = DiseaseConfiguration.build(d);
			getDiseaseConfigurationService().ensurePersisted(configuration);
		});
	}

	protected <T> T getBean(Class<T> beanClass, Annotation... qualifiers) {
		return ContextControlWrapper.getInstance().getContextualReference(beanClass, qualifiers);
	}

	public void executeInTransaction(Consumer<EntityManager> callback) {
		getBean(TransactionalTestEjb.class).executeInTransaction(callback);
	}

	public <R> R executeInTransaction(Function<EntityManager, R> callback) {
		return getBean(TransactionalTestEjb.class).executeInTransaction(callback);
	}

	public <T, R> R executeInTransaction(BiFunction<EntityManager, T, R> callback, T param) {
		return getBean(TransactionalTestEjb.class).executeInTransaction(callback, param);
	}

	/**
	 * This is private, because the entity manager does not have an active transaction when called from a test method
	 * and should not be used in test code.
	 * Use executeInTransaction or getEntityAttached instead!
	 */
	private EntityManager getEntityManager() {
		return getBean(EntityManager.class);
	}

	/**
	 * Attaches the entity to the persistence context
	 * <b>Important</b>: The entity will only be attached until the next ejb call is concluded, which clears the entity manager.
	 */
	public <E extends AbstractDomainObject> E mergeToEntityManager(E entity) {
		return getEntityManager().contains(entity) ? entity : getEntityManager().merge(entity);
	}

	public UserRole getEagerUserRole(String uuid) {
		return executeInTransaction(em -> {
			UserRole userRole = getUserRoleService().getByUuid(uuid);
			initUserRole(userRole);
			return userRole;
		});
	}

	public User getEagerUser(String uuid) {
		return executeInTransaction(em -> {
			User user = getUserService().getByUuid(uuid);
			for (UserRole userRole : user.getUserRoles()) {
				initUserRole(userRole);
			}
			return user;
		});
	}

	/**
	 * Initializes lazy references.
	 */
	private void initUserRole(UserRole userRole) {
		userRole.getEmailNotificationTypes().size();
		userRole.getSmsNotificationTypes().size();
	}

	protected void createDeletionConfigurations() {
		createDeletionConfigurations(DeletableEntityType.CASE, DeletionReference.CREATION);
		createDeletionConfigurations(DeletableEntityType.CONTACT, DeletionReference.CREATION);
		createDeletionConfigurations(DeletableEntityType.EVENT, DeletionReference.CREATION);
		createDeletionConfigurations(DeletableEntityType.EVENT_PARTICIPANT, DeletionReference.CREATION);
		createDeletionConfigurations(DeletableEntityType.IMMUNIZATION, DeletionReference.CREATION);
		createDeletionConfigurations(DeletableEntityType.TRAVEL_ENTRY, DeletionReference.CREATION);
	}

	protected void createDeletionConfigurations(DeletableEntityType deletableEntityType, DeletionReference automaticDeletionReference) {
		DeletionConfigurationService deletionConfigurationService = getBean(DeletionConfigurationService.class);
		deletionConfigurationService.ensurePersisted(DeletionConfiguration.build(deletableEntityType, automaticDeletionReference, 3650));
		deletionConfigurationService.ensurePersisted(DeletionConfiguration.build(deletableEntityType, DeletionReference.MANUAL_DELETION, 90));
	}

	protected void createFeatureConfiguration(FeatureType featureType, boolean enabled) {
		createFeatureConfiguration(featureType, enabled, null);
	}

	protected void createFeatureConfiguration(FeatureType featureType, boolean enabled, Map<FeatureTypeProperty, Object> properties) {
		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, enabled, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, featureType);
		if (properties != null) {
			executeInTransaction(em -> {
				Query query = em.createQuery("select f from featureconfiguration f where featureType = '" + featureType.name() + "'");
				FeatureConfiguration singleResult = (FeatureConfiguration) query.getSingleResult();
				singleResult.setProperties(properties);
				em.persist(singleResult);
			});
		}
	}

	public ConfigFacade getConfigFacade() {
		return getBean(ConfigFacadeEjbLocal.class);
	}

	/**
	 * Using local bean here to avoid multiple transactions in test.
	 * 
	 * @return
	 */
	public CaseFacadeEjb getCaseFacade() {
		return getBean(CaseFacadeEjbLocal.class);
	}

	public CaseService getCaseService() {
		return getBean(CaseService.class);
	}

	public ImmunizationFacadeEjbLocal getImmunizationFacade() {
		return getBean(ImmunizationFacadeEjbLocal.class);
	}

	public ImmunizationService getImmunizationService() {
		return getBean(ImmunizationService.class);
	}

	public VaccinationFacade getVaccinationFacade() {
		return getBean(VaccinationFacadeEjb.VaccinationFacadeEjbLocal.class);
	}

	public VaccinationService getVaccinationService() {
		return getBean(VaccinationService.class);
	}

	public TravelEntryFacade getTravelEntryFacade() {
		return getBean(TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal.class);
	}

	public TravelEntryService getTravelEntryService() {
		return getBean(TravelEntryService.class);
	}

	public CaseStatisticsFacade getCaseStatisticsFacade() {
		return getBean(CaseStatisticsFacadeEjbLocal.class);
	}

	public ContactFacadeEjbLocal getContactFacade() {
		return getBean(ContactFacadeEjbLocal.class);
	}

	public ContactService getContactService() {
		return getBean(ContactService.class);
	}

	public DashboardFacade getDashboardFacade() {
		return getBean(DashboardFacadeEjb.DashboardFacadeEjbLocal.class);
	}

	public SampleDashboardFacade getSampleDashboardFacade() {
		return getBean(SampleDashboardFacadeEjb.SampleDashboardFacadeEjbLocal.class);
	}

	public DashboardService getDashboardService() {
		return getBean(DashboardService.class);
	}

	public EventFacadeEjbLocal getEventFacade() {
		return getBean(EventFacadeEjbLocal.class);
	}

	public EventService getEventService() {
		return getBean(EventService.class);
	}

	public EventParticipantFacadeEjbLocal getEventParticipantFacade() {
		return getBean(EventParticipantFacadeEjbLocal.class);
	}

	public EventParticipantService getEventParticipantService() {
		return getBean(EventParticipantService.class);
	}

	public SurveillanceReportFacade getSurveillanceReportFacade() {
		return getBean(SurveillanceReportFacadeEjb.SurveillanceReportFacadeEjbLocal.class);
	}

	public SurveillanceReportService getSurveillanceReportService() {
		return getBean(SurveillanceReportService.class);
	}

	public ActionFacade getActionFacade() {
		return getBean(ActionFacadeEjb.ActionFacadeEjbLocal.class);
	}

	public AggregateReportFacade getAggregateReportFacade() {
		return getBean(AggregateReportFacadeEjb.AggregateReportFacadeEjbLocal.class);
	}

	public VisitFacade getVisitFacade() {
		return getBean(VisitFacadeEjbLocal.class);
	}

	public VisitService getVisitService() {
		return getBean(VisitService.class);
	}

	public PersonFacadeEjbLocal getPersonFacade() {
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

	public SampleService getSampleService() {
		return getBean(SampleService.class);
	}

	public AdditionalTestFacade getAdditionalTestFacade() {
		return getBean(AdditionalTestFacadeEjbLocal.class);
	}

	public SymptomsService getSymptomsService() {
		return getBean(SymptomsService.class);
	}

	public PointOfEntryFacade getPointOfEntryFacade() {
		return getBean(PointOfEntryFacadeEjbLocal.class);
	}

	public PortHealthInfoFacade getPortHealthInfoFacade() {
		return getBean(PortHealthInfoFacadeEjbLocal.class);
	}

	public FacilityFacade getFacilityFacade() {
		return getBean(FacilityFacadeEjbLocal.class);
	}

	public ContinentFacade getContinentFacade() {
		return getBean(ContinentFacadeEjb.ContinentFacadeEjbLocal.class);
	}

	public SubcontinentFacade getSubcontinentFacade() {
		return getBean(SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal.class);
	}

	public CountryFacade getCountryFacade() {
		return getBean(CountryFacadeEjb.CountryFacadeEjbLocal.class);
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

	public UserFacadeEjbLocal getUserFacade() {
		return getBean(UserFacadeEjbLocal.class);
	}

	public UserService getUserService() {
		return getBean(UserService.class);
	}

	public UserRoleFacadeEjbLocal getUserRoleFacade() {
		return getBean(UserRoleFacadeEjbLocal.class);
	}

	public UserRoleService getUserRoleService() {
		return getBean(UserRoleService.class);
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

	public ContinentService getContinentService() {
		return getBean(ContinentService.class);
	}

	public SubcontinentService getSubcontinentService() {
		return getBean(SubcontinentService.class);
	}

	public CountryService getCountryService() {
		return getBean(CountryService.class);
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

	public ClinicalVisitService getClinicalVisitService() {
		return getBean(ClinicalVisitService.class);
	}

	public TherapyFacade getTherapyFacade() {
		return getBean(TherapyFacadeEjbLocal.class);
	}

	public PrescriptionFacade getPrescriptionFacade() {
		return getBean(PrescriptionFacadeEjbLocal.class);
	}

	public PrescriptionService getPrescriptionService() {
		return getBean(PrescriptionService.class);
	}

	public TreatmentFacade getTreatmentFacade() {
		return getBean(TreatmentFacadeEjbLocal.class);
	}

	public TreatmentService getTreatmentService() {
		return getBean(TreatmentService.class);
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

	public FeatureConfigurationFacade getFeatureConfigurationFacade() {
		return getBean(FeatureConfigurationFacadeEjbLocal.class);
	}

	public PathogenTestFacade getPathogenTestFacade() {
		return getBean(PathogenTestFacadeEjbLocal.class);
	}

	public CampaignFormMetaFacade getCampaignFormFacade() {
		return getBean(CampaignFormMetaFacadeEjbLocal.class);
	}

	public SormasToSormasFacadeEjbLocal getSormasToSormasFacade() {
		return getBean(SormasToSormasFacadeEjbLocal.class);
	}

	public SormasToSormasOriginInfoFacadeEjbLocal getSormasToSormasOriginInfoFacade() {
		return getBean(SormasToSormasOriginInfoFacadeEjbLocal.class);
	}

	public SormasToSormasShareInfoFacadeEjbLocal getSormasToSormasShareInfoFacade() {
		return getBean(SormasToSormasShareInfoFacadeEjbLocal.class);
	}

	public SormasToSormasCaseFacade getSormasToSormasCaseFacade() {
		return getBean(SormasToSormasCaseFacadeEjbLocal.class);
	}

	public SormasToSormasContactFacade getSormasToSormasContactFacade() {
		return getBean(SormasToSormasContactFacadeEjbLocal.class);
	}

	public SormasToSormasEventFacade getSormasToSormasEventFacade() {
		return getBean(SormasToSormasEventFacadeEjbLocal.class);
	}

	public ExternalMessageFacade getExternalMessageFacade() {
		return getBean(ExternalMessageFacadeEjbLocal.class);
	}

	public ExternalMessageService getExternalMessageService() {
		return getBean(ExternalMessageService.class);
	}

	public SormasToSormasExternalMessageFacade getSormasToSormasLabMessageFacade() {
		return getBean(SormasToSormasExternalMessageFacadeEjbLocal.class);
	}

	public SormasToSormasShareInfoService getSormasToSormasShareInfoService() {
		return getBean(SormasToSormasShareInfoService.class);
	}

	public ShareRequestInfoService getShareRequestInfoService() {
		return getBean(ShareRequestInfoService.class);
	}

	public SormasToSormasEncryptionFacade getSormasToSormasEncryptionFacade() {
		return getBean(SormasToSormasEncryptionFacadeEjb.SormasToSormasEncryptionFacadeEjbLocal.class);
	}

	public GeocodingService getGeocodingService() {
		return getBean(GeocodingService.class);
	}

	public CurrentUserService getCurrentUserService() {
		return getBean(CurrentUserService.class);
	}

	public CustomizableEnumValueService getCustomizableEnumValueService() {
		return getBean(CustomizableEnumValueService.class);
	}

	public CustomizableEnumFacade getCustomizableEnumFacade() {
		return getBean(CustomizableEnumFacadeEjb.CustomizableEnumFacadeEjbLocal.class);
	}

	public ShareDataBuilderHelper getShareDataBuilderHelper() {
		return getBean(ShareDataBuilderHelper.class);
	}

	public ReceivedCaseProcessor getReceivedCaseProcessor() {
		return getBean(ReceivedCaseProcessor.class);
	}

	public ReceivedContactProcessor getReceivedContactProcessor() {
		return getBean(ReceivedContactProcessor.class);
	}

	public ReceivedEventProcessor getReceivedEventProcessor() {
		return getBean(ReceivedEventProcessor.class);
	}

	public ReceivedImmunizationProcessor getReceivedImmunizationProcessor() {
		return getBean(ReceivedImmunizationProcessor.class);
	}

	public ReceivedSampleProcessor getReceivedSampleProcessor() {
		return getBean(ReceivedSampleProcessor.class);
	}

	public CampaignFormDataFacade getCampaignFormDataFacade() {
		return getBean(CampaignFormDataFacadeEjbLocal.class);
	}

	public CampaignFacadeEjbLocal getCampaignFacade() {
		return getBean(CampaignFacadeEjbLocal.class);
	}

	public CampaignDiagramDefinitionFacade getCampaignDiagramDefinitionFacade() {
		return getBean(CampaignDiagramDefinitionFacadeEjb.CampaignDiagramDefinitionFacadeEjbLocal.class);
	}

	protected UserDto loginWith(UserDto user) {
		when(MockProducer.getPrincipal().getName()).thenReturn(user.getUserName());
		// load into cache, to work-around CurrentUserService.fetchUser @Transactional annotation not working in tests
		getCurrentUserService().getCurrentUser();
		return user;
	}

	protected UserDto useNationalUserLogin() {
		return loginWith(creator.createNationalUser());
	}

	protected UserDto useNationalAdminLogin() {
		if (nationalAdmin == null) {
			// we don't use TestDataCreator.createUser here, because we first need any user to have the user right to access backend facades
			User user = new User();
			user.setUuid(DataHelper.createUuid());
			user.setFirstName("ad");
			user.setLastName("min");
			user.setUserName("admin");
			String password = PasswordHelper.createPass(12);
			user.setSeed(PasswordHelper.createPass(16));
			user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
			user.setUserRoles(
				new HashSet<>(Arrays.asList(creator.getUserRole(DefaultUserRole.ADMIN), creator.getUserRole(DefaultUserRole.NATIONAL_USER))));

			getUserService().persist(user);
			nationalAdmin = getUserFacade().getByUuid(user.getUuid());
		}
		return loginWith(nationalAdmin);
	}

	protected UserDto useSurveillanceOfficerLogin(TestDataCreator.RDCF rdcf) {
		return loginWith(creator.createSurveillanceOfficer(rdcf));
	}

	protected void useSystemUser() {
		when(MockProducer.getPrincipal().getName()).thenReturn("SYSTEM");
	}

	public PathogenTestService getPathogenTestService() {
		return getBean(PathogenTestService.class);
	}

	public AdditionalTestService getAdditionalTestService() {
		return getBean(AdditionalTestService.class);
	}

	public DocumentTemplateFacade getDocumentTemplateFacade() {
		return getBean(DocumentTemplateFacadeEjbLocal.class);
	}

	public QuarantineOrderFacade getQuarantineOrderFacade() {
		return getBean(QuarantineOrderFacadeEjb.class);
	}

	public EventDocumentFacade getEventDocumentFacade() {
		return getBean(EventDocumentFacadeEjb.class);
	}

	public BAGExportFacade getBAGExportFacade() {
		return getBean(BAGExportFacadeEjb.BAGExportFacadeEjbLocal.class);
	}

	public ExternalJournalService getExternalJournalService() {
		return getBean(ExternalJournalService.class);
	}

	public DocumentFacade getDocumentFacade() {
		return getBean(DocumentFacadeEjb.DocumentFacadeEjbLocal.class);
	}

	public DocumentService getDocumentService() {
		return getBean(DocumentService.class);
	}

	public ExportFacade getExportFacade() {
		return getBean(ExportFacadeEjb.ExportFacadeEjbLocal.class);
	}

	public SystemEventFacade getSystemEventFacade() {
		return getBean(SystemEventFacadeEjb.SystemEventFacadeEjbLocal.class);
	}

	public ExternalSurveillanceToolFacade getExternalSurveillanceToolGatewayFacade() {
		return getBean(ExternalSurveillanceToolGatewayFacadeEjbLocal.class);
	}

	public ExternalShareInfoFacade getExternalShareInfoFacade() {
		return getBean(ExternalShareInfoFacadeEjbLocal.class);
	}

	public ExternalShareInfoService getExternalShareInfoService() {
		return getBean(ExternalShareInfoService.class);
	}

	public SormasToSormasShareRequestFacade getSormasToSormasShareRequestFacade() {
		return getBean(SormasToSormasShareRequestFacadeEJBLocal.class);
	}

	public SormasToSormasShareRequestService getSormasToSormasShareRequestService() {
		return getBean(SormasToSormasShareRequestService.class);
	}

	public TestReportFacade getTestReportFacade() {
		return getBean(TestReportFacadeEjb.TestReportFacadeEjbLocal.class);
	}

	public TestReportService getTestReportService() {
		return getBean(TestReportService.class);
	}

	public ImportParserService getImportParserService() {
		return getBean(ImportParserService.class);
	}

	public AreaFacade getAreaFacade() {
		return getBean(AreaFacadeEjbLocal.class);
	}

	public SormasToSormasCaseDtoValidator getSormasToSormasCaseDtoValidator() {
		return getBean(SormasToSormasCaseDtoValidator.class);
	}

	public SormasToSormasContactDtoValidator getSormasToSormasContactDtoValidator() {
		return getBean(SormasToSormasContactDtoValidator.class);
	}

	public SormasToSormasEventDtoValidator getSormasToSormasEventDtoValidator() {
		return getBean(SormasToSormasEventDtoValidator.class);
	}

	public SormasToSormasEventParticipantDtoValidator getSormasToSormasEventParticipantDtoValidator() {
		return getBean(SormasToSormasEventParticipantDtoValidator.class);
	}

	public SormasToSormasImmunizationDtoValidator getSormasToSormasImmunizationDtoValidator() {
		return getBean(SormasToSormasImmunizationDtoValidator.class);
	}

	public SormasToSormasSampleDtoValidator getSormasToSormasSampleDtoValidator() {
		return getBean(SormasToSormasSampleDtoValidator.class);
	}

	public SormasToSormasExternalMessageDtoValidator getSormasToSormasLabMessageDtoValidator() {
		return getBean(SormasToSormasExternalMessageDtoValidator.class);
	}

	public SormasToSormasSurveillanceReportDtoValidator getSormasToSormasSurveillanceReportDtoValidator() {
		return getBean(SormasToSormasSurveillanceReportDtoValidator.class);
	}

	public DefaultEntitiesCreator getDefaultEntitiesCreator() {
		return getBean(DefaultEntitiesCreator.class);
	}

	public CoreEntityDeletionService getCoreEntityDeletionService() {
		return getBean(CoreEntityDeletionService.class);
	}

	public DeletionConfigurationService getDeletionConfigurationService() {
		return getBean(DeletionConfigurationService.class);
	}

	public InfoFacade getInfoFacade() {
		return getBean(InfoFacadeEjb.InfoFacadeEjbLocal.class);
	}

	public TaskService getTaskService() {
		return getBean(TaskService.class);
	}

	public SampleReportFacade getSampleReportFacade() {
		return getBean(SampleReportFacadeEjb.SampleReportFacadeEjbLocal.class);
	}

	public SampleReportService getSampleReportService() {
		return getBean(SampleReportService.class);
	}

	public <T extends Throwable> void assertThrowsWithMessage(Class<T> expectedType, String expectedMessage, Executable executable) {
		T throwable = assertThrows(expectedType, executable);
		assertEquals(expectedMessage, throwable.getMessage());
	}

	public EnvironmentFacadeEjbLocal getEnvironmentFacade() {
		return getBean(EnvironmentFacadeEjbLocal.class);
	}

	public EnvironmentService getEnvironmentService() {
		return getBean(EnvironmentService.class);
	}

	public EnvironmentSampleFacade getEnvironmentSampleFacade() {
		return getBean(EnvironmentSampleFacadeEjbLocal.class);
	}

	public EnvironmentSampleService getEnvironmentSampleService() {
		return getBean(EnvironmentSampleService.class);
	}

	public AutomaticLabMessageProcessor getAutomaticLabMessageProcessingFlow() {
		return getBean(AutomaticLabMessageProcessor.class);
	}

	public ExternalMessageProcessingFacade getExternalMessageProcessingFacade() {
		return getBean(ExternalMessageProcessingFacadeEjbLocal.class);
	}

	public ExternalEmailFacade getExternalEmailFacade() {
		return getBean(ExternalEmailFacadeEjbLocal.class);
	}

	public ManualMessageLogFacade getManualMessageLogFacade() {
		return getBean(ManualMessageLogFacadeEjbLocal.class);
	}

	public ManualMessageLogService getManualMessageLogService() {
		return getBean(ManualMessageLogService.class);
	}

	public SpecialCaseAccessFacade getSpecialCaseAccessFacade() {
		return getBean(SpecialCaseAccessFacadeEjbLocal.class);
	}

	public SpecialCaseAccessService getSpecialCaseAccessService() {
		return getBean(SpecialCaseAccessService.class);
	}

	public SelfReportFacadeEjbLocal getSelfReportFacade() {
		return getBean(SelfReportFacadeEjbLocal.class);
	}
}
