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
package de.symeda.sormas.ui;

import de.symeda.sormas.ui.action.ActionController;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.AefiController;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.AefiInvestigationController;
import de.symeda.sormas.ui.campaign.CampaignController;
import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.caze.surveillancereport.SurveillanceReportController;
import de.symeda.sormas.ui.clinicalcourse.ClinicalCourseController;
import de.symeda.sormas.ui.configuration.customizableenum.CustomizableEnumController;
import de.symeda.sormas.ui.configuration.infrastructure.InfrastructureController;
import de.symeda.sormas.ui.configuration.outbreak.OutbreakController;
import de.symeda.sormas.ui.contact.ContactController;
import de.symeda.sormas.ui.customexport.CustomExportController;
import de.symeda.sormas.ui.dashboard.DashboardController;
import de.symeda.sormas.ui.docgeneration.DocGenerationController;
import de.symeda.sormas.ui.email.ExternalEmailController;
import de.symeda.sormas.ui.environment.EnvironmentController;
import de.symeda.sormas.ui.events.EventController;
import de.symeda.sormas.ui.events.EventGroupController;
import de.symeda.sormas.ui.events.EventParticipantsController;
import de.symeda.sormas.ui.externalmessage.ExternalMessageController;
import de.symeda.sormas.ui.immunization.ImmunizationController;
import de.symeda.sormas.ui.person.PersonController;
import de.symeda.sormas.ui.reports.aggregate.AggregateReportController;
import de.symeda.sormas.ui.samples.AdditionalTestController;
import de.symeda.sormas.ui.samples.PathogenTestController;
import de.symeda.sormas.ui.samples.environmentsample.EnvironmentSampleController;
import de.symeda.sormas.ui.samples.humansample.SampleController;
import de.symeda.sormas.ui.selfreport.SelfReportController;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasController;
import de.symeda.sormas.ui.specialcaseaccess.SpecialCaseAccessController;
import de.symeda.sormas.ui.statistics.StatisticsController;
import de.symeda.sormas.ui.survey.SurveyController;
import de.symeda.sormas.ui.survey.SurveyDocumentController;
import de.symeda.sormas.ui.survey.SurveyTokenController;
import de.symeda.sormas.ui.task.TaskController;
import de.symeda.sormas.ui.therapy.TherapyController;
import de.symeda.sormas.ui.travelentry.TravelEntryController;
import de.symeda.sormas.ui.user.UserController;
import de.symeda.sormas.ui.user.UserRoleController;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.BaseControllerProvider;
import de.symeda.sormas.ui.utils.DeleteRestoreController;
import de.symeda.sormas.ui.utils.PermanentDeleteController;
import de.symeda.sormas.ui.vaccination.VaccinationController;
import de.symeda.sormas.ui.visit.VisitController;

public class ControllerProvider extends BaseControllerProvider {

	private final CaseController caseController;
	private final ContactController contactController;
	private final EventController eventController;
	private final EventParticipantsController eventParticipantController;
	private final EventGroupController eventGroupController;
	private final InfrastructureController infrastructureController;
	private final VisitController visitController;
	private final PersonController personController;
	private final UserController userController;
	private final UserRoleController userRoleController;
	private final TaskController taskController;
	private final ActionController actionController;
	private final SampleController sampleController;
	private final PathogenTestController pathogenTestController;
	private final AdditionalTestController additionalTestController;
	private final OutbreakController outbreakController;
	private final StatisticsController statisticsController;
	private final DashboardController dashboardController;
	private final TherapyController therapyController;
	private final ClinicalCourseController clinicalCourseController;
	private final AggregateReportController aggregateReportController;
	private final CampaignController campaignController;
	private final SormasToSormasController sormasToSormasController;
	private final CustomExportController customExportController;
	private final ExternalMessageController externalMessageController;
	private final SurveillanceReportController surveillanceReportController;
	private final DocGenerationController docGenerationController;
	private final TravelEntryController travelEntryController;
	private final ImmunizationController immunizationController;
	private final AefiController aefiController;
	private final AefiInvestigationController aefiInvestigationController;
	private final VaccinationController vaccinationController;
	private final ArchivingController archivingController;
	private final DeleteRestoreController deleteRestoreController;
	private final EnvironmentController environmentController;
	private final PermanentDeleteController permanentDeleteController;
	private final EnvironmentSampleController environmentSampleController;
	private final ExternalEmailController externalEmailController;
	private final CustomizableEnumController customizableEnumController;
	private final SpecialCaseAccessController specialCaseAccessController;
	private final SelfReportController selfReportController;
	private final SurveyController surveyController;
	private final SurveyTokenController surveyTokenController;
	private final SurveyDocumentController surveyDocumentController;

	public ControllerProvider() {
		super();

		caseController = new CaseController();
		contactController = new ContactController();
		eventController = new EventController();
		eventParticipantController = new EventParticipantsController();
		eventGroupController = new EventGroupController();
		infrastructureController = new InfrastructureController();
		visitController = new VisitController();
		personController = new PersonController();
		userController = new UserController();
		userRoleController = new UserRoleController();
		taskController = new TaskController();
		actionController = new ActionController();
		sampleController = new SampleController();
		pathogenTestController = new PathogenTestController();
		additionalTestController = new AdditionalTestController();
		outbreakController = new OutbreakController();
		statisticsController = new StatisticsController();
		dashboardController = new DashboardController();
		therapyController = new TherapyController();
		clinicalCourseController = new ClinicalCourseController();
		aggregateReportController = new AggregateReportController();
		campaignController = new CampaignController();
		sormasToSormasController = new SormasToSormasController();
		customExportController = new CustomExportController();
		externalMessageController = new ExternalMessageController();
		surveillanceReportController = new SurveillanceReportController();
		docGenerationController = new DocGenerationController();
		travelEntryController = new TravelEntryController();
		immunizationController = new ImmunizationController();
		aefiController = new AefiController();
		aefiInvestigationController = new AefiInvestigationController();
		vaccinationController = new VaccinationController();
		archivingController = new ArchivingController();
		deleteRestoreController = new DeleteRestoreController();
		environmentController = new EnvironmentController();
		permanentDeleteController = new PermanentDeleteController();
		environmentSampleController = new EnvironmentSampleController();
		externalEmailController = new ExternalEmailController();
		customizableEnumController = new CustomizableEnumController();
		specialCaseAccessController = new SpecialCaseAccessController();
		selfReportController = new SelfReportController();
		surveyController = new SurveyController();
		surveyTokenController = new SurveyTokenController();
		surveyDocumentController = new SurveyDocumentController();
	}

	protected static ControllerProvider get() {
		return (ControllerProvider) BaseControllerProvider.get();
	}

	public static CaseController getCaseController() {
		return get().caseController;
	}

	public static ContactController getContactController() {
		return get().contactController;
	}

	public static EventController getEventController() {
		return get().eventController;
	}

	public static EventParticipantsController getEventParticipantController() {
		return get().eventParticipantController;
	}

	public static EventGroupController getEventGroupController() {
		return get().eventGroupController;
	}

	public static InfrastructureController getInfrastructureController() {
		return get().infrastructureController;
	}

	public static VisitController getVisitController() {
		return get().visitController;
	}

	public static PersonController getPersonController() {
		return get().personController;
	}

	public static UserController getUserController() {
		return get().userController;
	}

	public static UserRoleController getUserRoleController() {
		return get().userRoleController;
	}

	public static TaskController getTaskController() {
		return get().taskController;
	}

	public static ActionController getActionController() {
		return get().actionController;
	}

	public static SampleController getSampleController() {
		return get().sampleController;
	}

	public static AdditionalTestController getAdditionalTestController() {
		return get().additionalTestController;
	}

	public static PathogenTestController getPathogenTestController() {
		return get().pathogenTestController;
	}

	public static OutbreakController getOutbreakController() {
		return get().outbreakController;
	}

	public static StatisticsController getStatisticsController() {
		return get().statisticsController;
	}

	public static DashboardController getDashboardController() {
		return get().dashboardController;
	}

	public static TherapyController getTherapyController() {
		return get().therapyController;
	}

	public static ClinicalCourseController getClinicalCourseController() {
		return get().clinicalCourseController;
	}

	public static AggregateReportController getAggregateReportController() {
		return get().aggregateReportController;
	}

	public static CampaignController getCampaignController() {
		return get().campaignController;
	}

	public static SormasToSormasController getSormasToSormasController() {
		return get().sormasToSormasController;
	}

	public static CustomExportController getCustomExportController() {
		return get().customExportController;
	}

	public static ExternalMessageController getExternalMessageController() {
		return get().externalMessageController;
	}

	public static SurveillanceReportController getSurveillanceReportController() {
		return get().surveillanceReportController;
	}

	public static DocGenerationController getDocGenerationController() {
		return get().docGenerationController;
	}

	public static TravelEntryController getTravelEntryController() {
		return get().travelEntryController;
	}

	public static ImmunizationController getImmunizationController() {
		return get().immunizationController;
	}

	public static AefiController getAefiController() {
		return get().aefiController;
	}

	public static AefiInvestigationController getAefiInvestigationController() {
		return get().aefiInvestigationController;
	}

	public static VaccinationController getVaccinationController() {
		return get().vaccinationController;
	}

	public static ArchivingController getArchiveController() {
		return get().archivingController;
	}

	public static DeleteRestoreController getDeleteRestoreController() {
		return get().deleteRestoreController;
	}

	public static EnvironmentController getEnvironmentController() {
		return get().environmentController;
	}

	public static PermanentDeleteController getPermanentDeleteController() {
		return get().permanentDeleteController;
	}

	public static EnvironmentSampleController getEnvironmentSampleController() {
		return get().environmentSampleController;
	}

	public static ExternalEmailController getExternalEmailController() {
		return get().externalEmailController;
	}

	public static CustomizableEnumController getCustomizableEnumController() {
		return get().customizableEnumController;
	}

	public static SpecialCaseAccessController getSpecialCaseAccessController() {
		return get().specialCaseAccessController;
	}

	public static SelfReportController getSelfReportController() {
		return get().selfReportController;
	}

	public static SurveyController getSurveyController() {
		return get().surveyController;
	}

	public static SurveyTokenController getSurveyTokenController() {
		return get().surveyTokenController;
	}

	public static SurveyDocumentController getSurveyDocumentController() {
		return get().surveyDocumentController;
	}
}
