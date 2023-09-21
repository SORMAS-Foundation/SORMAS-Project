/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.firebase.perf.metrics.Trace;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.CampaignDtoHelper;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormDataDtoHelper;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMetaDtoHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationDtoHelper;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisitDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.DtoFeatureConfigHelper;
import de.symeda.sormas.app.backend.common.DtoUserRightsHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.customizableenum.CustomizableEnumValueDtoHelper;
import de.symeda.sormas.app.backend.disease.DiseaseConfigurationDtoHelper;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.environment.EnvironmentDtoHelper;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSampleDtoHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDtoHelper;
import de.symeda.sormas.app.backend.event.EventParticipantDtoHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.feature.FeatureConfigurationDtoHelper;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.immunization.ImmunizationDtoHelper;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureHelper;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntryDtoHelper;
import de.symeda.sormas.app.backend.outbreak.OutbreakDtoHelper;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.region.AreaDtoHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.ContinentDtoHelper;
import de.symeda.sormas.app.backend.region.CountryDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.region.SubcontinentDtoHelper;
import de.symeda.sormas.app.backend.report.AggregateReportDtoHelper;
import de.symeda.sormas.app.backend.report.WeeklyReportDtoHelper;
import de.symeda.sormas.app.backend.sample.AdditionalTestDtoHelper;
import de.symeda.sormas.app.backend.sample.PathogenTestDtoHelper;
import de.symeda.sormas.app.backend.sample.SampleDtoHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.therapy.PrescriptionDtoHelper;
import de.symeda.sormas.app.backend.therapy.TreatmentDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.backend.user.UserRoleDtoHelper;
import de.symeda.sormas.app.backend.visit.VisitDtoHelper;
import de.symeda.sormas.app.component.dialog.SynchronizationDialog;
import de.symeda.sormas.app.core.TaskNotificationService;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;
import retrofit2.Response;

public class SynchronizeDataAsync extends AsyncTask<Void, Void, Void> {

	private static final Logger logger = LoggerFactory.getLogger(SynchronizeDataAsync.class);

	/**
	 * Should be set to true when the synchronization fails and reset to false as soon
	 * as the last callback is called (i.e. the synchronization has been completed/cancelled).
	 */
	protected boolean syncFailed;
	protected String syncFailedMessage;

	private SyncMode syncMode;
	private final Context context;
	private final Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks;

	private SynchronizeDataAsync(SyncMode syncMode, Context context, SynchronizationDialog.SynchronizationCallbacks syncCallbacks) {
		this.syncMode = syncMode;
		this.context = context;
		this.syncCallbacks = Optional.ofNullable(syncCallbacks);
	}

	@Override
	protected Void doInBackground(Void... params) {

		if (!RetroProvider.isConnected()) {
			return null;
		}

		if (ConfigProvider.isRepullNeeded()) {
			syncMode = SyncMode.CompleteAndRepull;
		}

		try {
			boolean pullAndRemoveObsoleteNecessary = ConfigProvider.getLastObsoleteUuidsSyncDate() == null
				|| DateHelper.getFullDaysBetween(ConfigProvider.getLastObsoleteUuidsSyncDate(), new Date()) >= 1;
			syncCallbacks
				.ifPresent(c -> c.getUpdateStepNumberCallback().accept(syncMode == SyncMode.Changes ? pullAndRemoveObsoleteNecessary ? 5 : 4 : 6));
			syncCallbacks.ifPresent(c -> c.getShowDialogCallback().run());

			Trace syncModeTrace;

			switch (syncMode) {
			case Changes:
				syncModeTrace = FirebasePerformance.getInstance().newTrace("syncModeChangesTrace");
				syncModeTrace.start();

				// Prioritize pushing new data
				syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.PUSH_NEW));
				pushNewData();
				// Infrastructure always has to be pulled - otherwise referenced data may be lost (e.g. #586)
				syncCallbacks
					.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.PULL_INFRASTRUCTURE));
				pullInfrastructure();
				// Pull and remove obsolete entities when the last time this has been done is more than 24 hours ago
				if (pullAndRemoveObsoleteNecessary) {
					syncCallbacks
						.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.DELETE_OBSOLETE));
					pullAndRemoveObsoleteUuidsSince(ConfigProvider.getLastObsoleteUuidsSyncDate());
				}
				// Pull changed data and push existing data that has been changed on the mobile device
				syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.SYNCHRONIZE));
				synchronizeChangedData();

				syncModeTrace.stop();
				break;
			case Complete:
				syncModeTrace = FirebasePerformance.getInstance().newTrace("syncModeCompleteTrace");
				syncModeTrace.start();

				syncCallbacks
					.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.PULL_INFRASTRUCTURE));
				pullInfrastructure(); // do before missing, because we may have a completely empty database
				syncCallbacks.ifPresent(
					c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.CLEAR_UP_INFRASTRUCTURE));
				pullMissingAndDeleteInvalidInfrastructure();
				pushNewPullMissingAndDeleteInvalidData();
				syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.SYNCHRONIZE));
				synchronizeChangedData();

				syncModeTrace.stop();
				break;
			case CompleteAndRepull:
				syncModeTrace = FirebasePerformance.getInstance().newTrace("syncModeCompleteAndRepullTrace");
				syncModeTrace.start();

				syncCallbacks
					.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.PULL_INFRASTRUCTURE));
				pullInfrastructure(); // do before missing, because we may have a completely empty database
				syncCallbacks.ifPresent(
					c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.CLEAR_UP_INFRASTRUCTURE));
				pullMissingAndDeleteInvalidInfrastructure();
				syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.REPULL));
				repullData();
				pushNewPullMissingAndDeleteInvalidData();
				syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.SYNCHRONIZE));
				synchronizeChangedData();
				ConfigProvider.setRepullNeeded(false);

				syncModeTrace.stop();
				break;
			default:
				throw new IllegalArgumentException(syncMode.toString());
			}

			if (syncMode == SyncMode.Changes && hasAnyUnsynchronizedData()) {
				Log.w(getClass().getName(), "Still having unsynchronized data. Trying again in complete mode.");

				syncMode = SyncMode.Complete;
				doInBackground(params);
			}

		} catch (ServerConnectionException e) {
			syncFailed = true;
			syncFailedMessage = e.getMessage(context);
			RetroProvider.disconnect();

		} catch (NoConnectionException | ServerCommunicationException e) {

			Log.e(getClass().getName(), "Error trying to synchronizing data in mode '" + syncMode + "'", e);

			ErrorReportingHelper.sendCaughtException(e);

			syncFailed = true;
			syncFailedMessage = DatabaseHelper.getContext().getString(R.string.error_server_communication);
			RetroProvider.disconnect();

		} catch (RuntimeException | DaoException e) {

			SyncMode newSyncMode = null;
			switch (syncMode) {
			case Changes:
				newSyncMode = SyncMode.Complete;
				break;
			case Complete:
				newSyncMode = SyncMode.CompleteAndRepull;
				break;
			case CompleteAndRepull:
				break;
			default:
				throw new IllegalArgumentException(syncMode.toString());
			}

			if (newSyncMode != null) {

				Log.w(getClass().getName(), "Error trying to synchronizing data in mode '" + syncMode + "'", e);

				ErrorReportingHelper.sendCaughtException(e);

				syncMode = newSyncMode;
				doInBackground(params);

			} else {

				Log.e(getClass().getName(), "Error trying to synchronizing data in mode '" + syncMode + "'", e);

				ErrorReportingHelper.sendCaughtException(e);

				syncFailed = true;
				syncFailedMessage = DatabaseHelper.getContext().getString(R.string.error_synchronization);
				RetroProvider.disconnect();
			}
		}

		return null;
	}

	public static boolean hasAnyUnsynchronizedData() {
		final boolean hasUnsynchronizedCampaignData = !DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)
			&& (DatabaseHelper.getCampaignDao().isAnyModified()
				|| DatabaseHelper.getCampaignFormMetaDao().isAnyModified()
				|| DatabaseHelper.getCampaignFormDataDao().isAnyModified());

		return DatabaseHelper.getCaseDao().isAnyModified()
			|| DatabaseHelper.getImmunizationDao().isAnyModified()
			|| DatabaseHelper.getContactDao().isAnyModified()
			|| DatabaseHelper.getPersonDao().isAnyModified()
			|| DatabaseHelper.getEventDao().isAnyModified()
			|| DatabaseHelper.getEventParticipantDao().isAnyModified()
			|| DatabaseHelper.getSampleDao().isAnyModified()
			|| DatabaseHelper.getSampleTestDao().isAnyModified()
			|| DatabaseHelper.getAdditionalTestDao().isAnyModified()
			|| DatabaseHelper.getEnvironmentDao().isAnyModified()
			|| DatabaseHelper.getEnvironmentSampleDao().isAnyModified()
			|| DatabaseHelper.getTaskDao().isAnyModified()
			|| DatabaseHelper.getVisitDao().isAnyModified()
			|| DatabaseHelper.getWeeklyReportDao().isAnyModified()
			|| DatabaseHelper.getAggregateReportDao().isAnyModified()
			|| DatabaseHelper.getPrescriptionDao().isAnyModified()
			|| DatabaseHelper.getTreatmentDao().isAnyModified()
			|| DatabaseHelper.getClinicalVisitDao().isAnyModified()
			|| hasUnsynchronizedCampaignData;
	}

	@AddTrace(name = "pushNewDataTrace")
	private void pushNewData() throws ServerCommunicationException, ServerConnectionException, DaoException, NoConnectionException {

		new PersonDtoHelper().pushEntities(true, syncCallbacks);
		new CaseDtoHelper().pushEntities(true, syncCallbacks);
		new ImmunizationDtoHelper().pushEntities(true, syncCallbacks);
		new EventDtoHelper().pushEntities(true, syncCallbacks);
		new EventParticipantDtoHelper().pushEntities(true, syncCallbacks);
		new SampleDtoHelper().pushEntities(true, syncCallbacks);
		new PathogenTestDtoHelper().pushEntities(true, syncCallbacks);
		new AdditionalTestDtoHelper().pushEntities(true, syncCallbacks);
		new EnvironmentDtoHelper().pushEntities(true, syncCallbacks);
		new EnvironmentSampleDtoHelper().pushEntities(true, syncCallbacks);
		new ContactDtoHelper().pushEntities(true, syncCallbacks);
		new VisitDtoHelper().pushEntities(true, syncCallbacks);
		new TaskDtoHelper().pushEntities(true, syncCallbacks);
		new WeeklyReportDtoHelper().pushEntities(true, syncCallbacks);
		new AggregateReportDtoHelper().pushEntities(true, syncCallbacks);
		new PrescriptionDtoHelper().pushEntities(true, syncCallbacks);
		new TreatmentDtoHelper().pushEntities(true, syncCallbacks);
		new ClinicalVisitDtoHelper().pushEntities(true, syncCallbacks);

		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			new CampaignFormDataDtoHelper().pushEntities(true, syncCallbacks);
		}
	}

	@AddTrace(name = "synchronizeChangedDataTrace")
	private void synchronizeChangedData() throws DaoException, NoConnectionException, ServerConnectionException, ServerCommunicationException {

		PersonDtoHelper personDtoHelper = new PersonDtoHelper();
		CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
		ImmunizationDtoHelper immunizationDtoHelper = new ImmunizationDtoHelper();
		EventDtoHelper eventDtoHelper = new EventDtoHelper();
		EventParticipantDtoHelper eventParticipantDtoHelper = new EventParticipantDtoHelper();
		SampleDtoHelper sampleDtoHelper = new SampleDtoHelper();
		PathogenTestDtoHelper pathogenTestDtoHelper = new PathogenTestDtoHelper();
		AdditionalTestDtoHelper additionalTestDtoHelper = new AdditionalTestDtoHelper();
		EnvironmentDtoHelper environmentDtoHelper = new EnvironmentDtoHelper();
		EnvironmentSampleDtoHelper environmentSampleDtoHelper = new EnvironmentSampleDtoHelper();
		ContactDtoHelper contactDtoHelper = new ContactDtoHelper();
		VisitDtoHelper visitDtoHelper = new VisitDtoHelper();
		TaskDtoHelper taskDtoHelper = new TaskDtoHelper();
		WeeklyReportDtoHelper weeklyReportDtoHelper = new WeeklyReportDtoHelper();
		AggregateReportDtoHelper aggregateReportDtoHelper = new AggregateReportDtoHelper();
		PrescriptionDtoHelper prescriptionDtoHelper = new PrescriptionDtoHelper();
		TreatmentDtoHelper treatmentDtoHelper = new TreatmentDtoHelper();
		ClinicalVisitDtoHelper clinicalVisitDtoHelper = new ClinicalVisitDtoHelper();

		// order is important, due to dependencies (e.g. case & person)

		new OutbreakDtoHelper().pullEntities(false, context, syncCallbacks);
		new DiseaseConfigurationDtoHelper().pullEntities(false, context, syncCallbacks);
		new CustomizableEnumValueDtoHelper().pullEntities(false, context, syncCallbacks);

		boolean personsNeedPull = personDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean casesNeedPull = caseDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean immunizationsNeedPull = immunizationDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean eventsNeedPull = eventDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean eventParticipantsNeedPull = eventParticipantDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean samplesNeedPull = sampleDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean sampleTestsNeedPull = pathogenTestDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean additionalTestsNeedPull = additionalTestDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean environmentsNeedPull = environmentDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean environmentSamplesNeedPull = environmentSampleDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean contactsNeedPull = contactDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean visitsNeedPull = visitDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean tasksNeedPull = taskDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean weeklyReportsNeedPull = weeklyReportDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean aggregateReportsNeedPull = aggregateReportDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean prescriptionsNeedPull = prescriptionDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean treatmentsNeedPull = treatmentDtoHelper.pullAndPushEntities(context, syncCallbacks);
		boolean clinicalVisitsNeedPull = clinicalVisitDtoHelper.pullAndPushEntities(context, syncCallbacks);

		boolean casesVisible = DtoUserRightsHelper.isViewAllowed(CaseDataDto.class) && DtoFeatureConfigHelper.isFeatureConfigForCaseEnabled();
		boolean immunizationsVisible =
			DtoUserRightsHelper.isViewAllowed(ImmunizationDto.class) && DtoFeatureConfigHelper.isFeatureConfigForImmunizationEnabled();
		boolean eventsVisible = DtoUserRightsHelper.isViewAllowed(EventDto.class) && DtoFeatureConfigHelper.isFeatureConfigForEventsEnabled();
		boolean eventParticipantsVisible =
			DtoUserRightsHelper.isViewAllowed(EventParticipantDto.class) && DtoFeatureConfigHelper.isFeatureConfigForEventParticipantsEnabled();
		boolean samplesVisible = DtoUserRightsHelper.isViewAllowed(SampleDto.class) && DtoFeatureConfigHelper.isFeatureConfigForSampleEnabled();
		boolean sampleTestsVisible =
			DtoUserRightsHelper.isViewAllowed(PathogenTestDto.class) && DtoFeatureConfigHelper.isFeatureConfigForSampleTestsEnabled();
		boolean additionalTestsVisible =
			DtoUserRightsHelper.isViewAllowed(AdditionalTestDto.class) && DtoFeatureConfigHelper.isFeatureConfigForAdditionalTestsEnabled();
		boolean environmentsVisible =
			DtoUserRightsHelper.isViewAllowed(EnvironmentDto.class) && DtoFeatureConfigHelper.isFeatureConfigForEnvironmentEnabled();
		boolean environmentSamplesVisible =
			DtoUserRightsHelper.isViewAllowed(EnvironmentSampleDto.class) && DtoFeatureConfigHelper.isFeatureConfigForEnvironmentEnabled();
		boolean contactsVisible = DtoUserRightsHelper.isViewAllowed(ContactDto.class) && DtoFeatureConfigHelper.isFeatureConfigForContactsEnabled();
		boolean visitsVisible = DtoUserRightsHelper.isViewAllowed(VisitDto.class) && DtoFeatureConfigHelper.isFeatureConfigForVisitsEnabled();
		boolean tasksVisible = DtoUserRightsHelper.isViewAllowed(TaskDto.class) && DtoFeatureConfigHelper.isFeatureConfigForTasksEnabled();
		boolean weeklyReportsVisible =
			DtoUserRightsHelper.isViewAllowed(WeeklyReportDto.class) && DtoFeatureConfigHelper.isFeatureConfigForWeeklyReportsEnabled();
		boolean aggregateReportsVisible =
			DtoUserRightsHelper.isViewAllowed(AggregateReportDto.class) && DtoFeatureConfigHelper.isFeatureConfigForAggregateReportsEnabled();
		boolean prescriptionsVisible =
			DtoUserRightsHelper.isViewAllowed(PrescriptionDto.class) && DtoFeatureConfigHelper.isFeatureConfigForPrescriptionsEnabled();
		boolean treatmentsVisible =
			DtoUserRightsHelper.isViewAllowed(TreatmentDto.class) && DtoFeatureConfigHelper.isFeatureConfigForTreatmentsEnabled();
		boolean clinicalVisitsVisible =
			DtoUserRightsHelper.isViewAllowed(ClinicalVisitDto.class) && DtoFeatureConfigHelper.isFeatureConfigForClinicalVisitsEnabled();

		syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.PULL_MODIFIED));

		casesNeedPull |= clinicalVisitsNeedPull;

		if (personsNeedPull) {
			personDtoHelper.pullEntities(true, context, syncCallbacks, false);
		}
		if (casesVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (casesNeedPull) {
				caseDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (immunizationsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (immunizationsNeedPull) {
				immunizationDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (eventsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (eventsNeedPull) {
				eventDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (eventParticipantsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (eventParticipantsNeedPull) {
				eventParticipantDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (samplesVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (samplesNeedPull) {
				sampleDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (sampleTestsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (sampleTestsNeedPull) {
				pathogenTestDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (additionalTestsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (additionalTestsNeedPull) {
				additionalTestDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (environmentsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (environmentsNeedPull) {
				environmentDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (environmentSamplesVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (environmentSamplesNeedPull) {
				environmentSampleDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (contactsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (contactsNeedPull) {
				contactDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (visitsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (visitsNeedPull) {
				visitDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (tasksVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (tasksNeedPull) {
				taskDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (weeklyReportsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (weeklyReportsNeedPull) {
				weeklyReportDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (aggregateReportsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (aggregateReportsNeedPull) {
				aggregateReportDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (prescriptionsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (prescriptionsNeedPull) {
				prescriptionDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (treatmentsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (treatmentsNeedPull) {
				treatmentDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		if (clinicalVisitsVisible) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			if (clinicalVisitsNeedPull) {
				clinicalVisitDtoHelper.pullEntities(true, context, syncCallbacks, false);
			}
		}
		syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

		// Campaigns
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {

			// meta first
			final CampaignFormMetaDtoHelper campaignFormMetaDtoHelper = new CampaignFormMetaDtoHelper();
			// no meta editing in mobile app - if (campaignFormMetaDtoHelper.pullAndPushEntities(context))
			campaignFormMetaDtoHelper.pullEntities(true, context, syncCallbacks, false);
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			final CampaignDtoHelper campaignDtoHelper = new CampaignDtoHelper();
			// no campaign editing yet - if (campaignDtoHelper.pullAndPushEntities(context))
			campaignDtoHelper.pullEntities(true, context, syncCallbacks, false);
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			final CampaignFormDataDtoHelper campaignFormDataDtoHelper = new CampaignFormDataDtoHelper();
			if (campaignFormDataDtoHelper.pullAndPushEntities(context))
				campaignFormDataDtoHelper.pullEntities(true, context, syncCallbacks, false);
		}
	}

	@AddTrace(name = "repullDataTrace")
	private void repullData() throws DaoException, NoConnectionException, ServerConnectionException, ServerCommunicationException {

		PersonDtoHelper personDtoHelper = new PersonDtoHelper();
		CaseDtoHelper caseDtoHelper = new CaseDtoHelper();
		ImmunizationDtoHelper immunizationDtoHelper = new ImmunizationDtoHelper();
		EventDtoHelper eventDtoHelper = new EventDtoHelper();
		EventParticipantDtoHelper eventParticipantDtoHelper = new EventParticipantDtoHelper();
		SampleDtoHelper sampleDtoHelper = new SampleDtoHelper();
		PathogenTestDtoHelper pathogenTestDtoHelper = new PathogenTestDtoHelper();
		AdditionalTestDtoHelper additionalTestDtoHelper = new AdditionalTestDtoHelper();
		EnvironmentDtoHelper environmentDtoHelper = new EnvironmentDtoHelper();
		EnvironmentSampleDtoHelper environmentSampleDtoHelper = new EnvironmentSampleDtoHelper();
		ContactDtoHelper contactDtoHelper = new ContactDtoHelper();
		VisitDtoHelper visitDtoHelper = new VisitDtoHelper();
		TaskDtoHelper taskDtoHelper = new TaskDtoHelper();
		WeeklyReportDtoHelper weeklyReportDtoHelper = new WeeklyReportDtoHelper();
		AggregateReportDtoHelper aggregateReportDtoHelper = new AggregateReportDtoHelper();
		PrescriptionDtoHelper prescriptionDtoHelper = new PrescriptionDtoHelper();
		TreatmentDtoHelper treatmentDtoHelper = new TreatmentDtoHelper();
		ClinicalVisitDtoHelper clinicalVisitDtoHelper = new ClinicalVisitDtoHelper();

		// order is important, due to dependencies (e.g. case & person)

		new UserRoleDtoHelper().repullEntities(context, syncCallbacks);
		new DiseaseClassificationDtoHelper().repullEntities(context, syncCallbacks);
		new UserDtoHelper().repullEntities(context, syncCallbacks);
		new OutbreakDtoHelper().repullEntities(context, syncCallbacks);
		new DiseaseConfigurationDtoHelper().repullEntities(context, syncCallbacks);
		new CustomizableEnumValueDtoHelper().repullEntities(context, syncCallbacks);
		new FeatureConfigurationDtoHelper().repullEntities(context, syncCallbacks);
		personDtoHelper.repullEntities(context, syncCallbacks);
		caseDtoHelper.repullEntities(context, syncCallbacks);
		immunizationDtoHelper.repullEntities(context, syncCallbacks);
		eventDtoHelper.repullEntities(context, syncCallbacks);
		eventParticipantDtoHelper.repullEntities(context, syncCallbacks);
		sampleDtoHelper.repullEntities(context, syncCallbacks);
		pathogenTestDtoHelper.repullEntities(context, syncCallbacks);
		additionalTestDtoHelper.repullEntities(context, syncCallbacks);
		environmentDtoHelper.repullEntities(context, syncCallbacks);
		environmentSampleDtoHelper.repullEntities(context, syncCallbacks);
		contactDtoHelper.repullEntities(context, syncCallbacks);
		visitDtoHelper.repullEntities(context, syncCallbacks);
		taskDtoHelper.repullEntities(context, syncCallbacks);
		weeklyReportDtoHelper.repullEntities(context, syncCallbacks);
		aggregateReportDtoHelper.repullEntities(context, syncCallbacks);
		prescriptionDtoHelper.repullEntities(context, syncCallbacks);
		treatmentDtoHelper.repullEntities(context, syncCallbacks);
		clinicalVisitDtoHelper.repullEntities(context, syncCallbacks);

		// Campaigns
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			// meta first
			final CampaignFormMetaDtoHelper campaignFormMetaDtoHelper = new CampaignFormMetaDtoHelper();
			campaignFormMetaDtoHelper.repullEntities(context, syncCallbacks);

			final CampaignDtoHelper campaignDtoHelper = new CampaignDtoHelper();
			campaignDtoHelper.repullEntities(context, syncCallbacks);

			final CampaignFormDataDtoHelper campaignFormDataDtoHelper = new CampaignFormDataDtoHelper();
			campaignFormDataDtoHelper.repullEntities(context, syncCallbacks);
		}
	}

	@AddTrace(name = "pullInfrastructureTrace")
	private void pullInfrastructure() throws DaoException, NoConnectionException, ServerConnectionException, ServerCommunicationException {

		if (ConfigProvider.isInitialSyncRequired()) {
			pullInitialInfrastructure();
		} else {
			InfrastructureChangeDatesDto changeDates = InfrastructureHelper.getInfrastructureChangeDates();

			try {
				Response<InfrastructureSyncDto> response = RetroProvider.getInfrastructureFacade().pullInfrastructureSyncData(changeDates).execute();
				if (!response.isSuccessful()) {
					RetroProvider.throwException(response);
				}

				InfrastructureSyncDto infrastructureData = response.body();
				if (infrastructureData != null) {
					if (infrastructureData.isInitialSyncRequired()) {
						ConfigProvider.setInitialSyncRequired(true);
						pullInfrastructure();
					} else {
						InfrastructureHelper.handlePulledInfrastructureData(infrastructureData, syncCallbacks);
					}
				}
			} catch (IOException e) {
				Log.e(SynchronizeDataAsync.class.getSimpleName(), "Error when trying to pull infrastructure data: " + e.getMessage());
			}
		}
	}

	@AddTrace(name = "pullInitialInfrastructureTrace")
	private void pullInitialInfrastructure() throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {

		new ContinentDtoHelper().pullEntities(false, context, syncCallbacks);
		new SubcontinentDtoHelper().pullEntities(false, context, syncCallbacks);
		new CountryDtoHelper().pullEntities(false, context, syncCallbacks);
		new AreaDtoHelper().pullEntities(false, context, syncCallbacks);
		new RegionDtoHelper().pullEntities(false, context, syncCallbacks);
		new DistrictDtoHelper().pullEntities(false, context, syncCallbacks);
		new CommunityDtoHelper().pullEntities(false, context, syncCallbacks);
		new FacilityDtoHelper().pullEntities(false, context, syncCallbacks);
		new PointOfEntryDtoHelper().pullEntities(false, context, syncCallbacks);
		new UserRoleDtoHelper().pullEntities(false, context, syncCallbacks);
		new UserDtoHelper().pullEntities(false, context, syncCallbacks);
		new DiseaseClassificationDtoHelper().pullEntities(false, context, syncCallbacks);
		new DiseaseConfigurationDtoHelper().pullEntities(false, context, syncCallbacks);
		new CustomizableEnumValueDtoHelper().pullEntities(false, context, syncCallbacks);

		// feature configurations may be removed, so have to pull the deleted uuids
		// this may be applied to other entities later as well
		Date featureConfigurationChangeDate = DatabaseHelper.getFeatureConfigurationDao().getLatestChangeDate();
		List<String> featureConfigurationConfigUuids = executeUuidCall(
			RetroProvider.getFeatureConfigurationFacade()
				.pullDeletedUuidsSince(featureConfigurationChangeDate != null ? featureConfigurationChangeDate.getTime() : 0));
		DatabaseHelper.getFeatureConfigurationDao().delete(featureConfigurationConfigUuids);

		new FeatureConfigurationDtoHelper().pullEntities(false, context, syncCallbacks);

		ConfigProvider.setInitialSyncRequired(false);
	}

	@AddTrace(name = "pullAndRemoveObsoleteUuidsSinceTrace")
	private void pullAndRemoveObsoleteUuidsSince(Date since) throws NoConnectionException, ServerConnectionException, ServerCommunicationException {

		Log.d(SynchronizeDataAsync.class.getSimpleName(), "pullAndRemoveObsoleteUuidsSince");

		try {
			// Cases
			if (DtoUserRightsHelper.isViewAllowed(CaseDataDto.class)) {
				List<String> caseUuids = executeUuidCall(RetroProvider.getCaseFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String caseUuid : caseUuids) {
					DatabaseHelper.getCaseDao().deleteCaseAndAllDependingEntities(caseUuid);
				}
				// Remove obsolete cases based on change date
				List<Case> obsoleteCases = DatabaseHelper.getCaseDao().queryForObsolete();
				for (Case obsoleteCase : obsoleteCases) {
					DatabaseHelper.getCaseDao().deleteCaseAndAllDependingEntities(obsoleteCase.getUuid());
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Contacts
			if (DtoUserRightsHelper.isViewAllowed(ContactDto.class)) {
				List<String> contactUuids =
					executeUuidCall(RetroProvider.getContactFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String contactUuid : contactUuids) {
					DatabaseHelper.getContactDao().deleteContactAndAllDependingEntities(contactUuid);
				}
				// Remove obsolete contacts based on change date
				List<Contact> obsoleteContacts = DatabaseHelper.getContactDao().queryForObsolete();
				for (Contact obsoleteContact : obsoleteContacts) {
					DatabaseHelper.getContactDao().deleteContactAndAllDependingEntities(obsoleteContact.getUuid());
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Events
			if (DtoUserRightsHelper.isViewAllowed(EventDto.class)) {
				List<String> eventUuids = executeUuidCall(RetroProvider.getEventFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String eventUuid : eventUuids) {
					DatabaseHelper.getEventDao().deleteEventAndAllDependingEntities(eventUuid);
				}
				// Remove obsolete events based on change date
				List<Event> obsoleteEvents = DatabaseHelper.getEventDao().queryForObsolete();
				for (Event obsoleteEvent : obsoleteEvents) {
					DatabaseHelper.getEventDao().deleteEventAndAllDependingEntities(obsoleteEvent.getUuid());
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// EventParticipant
			if (DtoUserRightsHelper.isViewAllowed(EventParticipantDto.class)) {
				List<String> eventParticipantUuids =
					executeUuidCall(RetroProvider.getEventParticipantFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String eventParticipantUuid : eventParticipantUuids) {
					DatabaseHelper.getEventParticipantDao().deleteEventParticipantAndAllDependingEntities(eventParticipantUuid);
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Immunization
			if (DtoUserRightsHelper.isViewAllowed(ImmunizationDto.class)) {
				List<String> immunizationUuids =
					executeUuidCall(RetroProvider.getImmunizationFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String immunizationUuid : immunizationUuids) {
					DatabaseHelper.getImmunizationDao().deleteImmunizationAndAllDependingEntities(immunizationUuid);
				}
				// Remove obsolete immunizations based on change date
				List<Immunization> obsoleteImmunizations = DatabaseHelper.getImmunizationDao().queryForObsolete();
				for (Immunization obsoleteImmunization : obsoleteImmunizations) {
					DatabaseHelper.getImmunizationDao().deleteImmunizationAndAllDependingEntities(obsoleteImmunization.getUuid());
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Samples
			if (DtoUserRightsHelper.isViewAllowed(SampleDto.class)) {
				List<String> sampleUuids =
					executeUuidCall(RetroProvider.getSampleFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String sampleUuid : sampleUuids) {
					DatabaseHelper.getSampleDao().deleteSampleAndAllDependingEntities(sampleUuid);
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Environments
			if (DtoUserRightsHelper.isViewAllowed(EnvironmentDto.class)) {
				List<String> environmentUuids =
					executeUuidCall(RetroProvider.getEnvironmentFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String environmentUuid : environmentUuids) {
					DatabaseHelper.getEnvironmentDao().deleteEnvironmentAndAllDependingEntities(environmentUuid);
				}
				// Remove obsolete environments based on change date
				List<Environment> obsoleteEnvironments = DatabaseHelper.getEnvironmentDao().queryForObsolete();
				for (Environment obsoleteEnvironment : obsoleteEnvironments) {
					DatabaseHelper.getEnvironmentDao().deleteEnvironmentAndAllDependingEntities(obsoleteEnvironment.getUuid());
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Environment samples
			if (DtoUserRightsHelper.isViewAllowed(EnvironmentSampleDto.class)) {
				List<String> sampleUuids =
					executeUuidCall(RetroProvider.getEnvironmentSampleFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String sampleUuid : sampleUuids) {
					DatabaseHelper.getEnvironmentSampleDao().deleteEnvironmentSampleAndAllDependingEntities(sampleUuid);
				}
				// Remove obsolete samples based on change date
				List<EnvironmentSample> obsoleteSamples = DatabaseHelper.getEnvironmentSampleDao().queryForObsolete();
				for (EnvironmentSample obsoleteSample : obsoleteSamples) {
					DatabaseHelper.getEnvironmentSampleDao().deleteEnvironmentSampleAndAllDependingEntities(obsoleteSample.getUuid());
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Tasks
			if (DtoUserRightsHelper.isViewAllowed(TaskDto.class)) {
				List<String> taskUuids = executeUuidCall(RetroProvider.getTaskFacade().pullObsoleteUuidsSince(since != null ? since.getTime() : 0));
				for (String taskUuid : taskUuids) {
					DatabaseHelper.getTaskDao().deleteTaskAndAllDependingEntities(taskUuid);
				}
				// Remove obsolete tasks based on change date
				List<Task> obsoleteTasks = DatabaseHelper.getTaskDao().queryForObsolete();
				for (Task obsoleteTask : obsoleteTasks) {
					DatabaseHelper.getTaskDao().deleteTaskAndAllDependingEntities(obsoleteTask.getUuid());
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Inactive outbreaks
			if (DtoUserRightsHelper.isViewAllowed(OutbreakDto.class)) {
				List<String> outbreakUuids =
					executeUuidCall(RetroProvider.getOutbreakFacade().pullInactiveUuidsSince(since != null ? since.getTime() : 0));
				for (String outbreakUuid : outbreakUuids) {
					DatabaseHelper.getOutbreakDao().deleteOutbreakAndAllDependingEntities(outbreakUuid);
				}
			}
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

			// Aggregate reports
			if (DtoUserRightsHelper.isViewAllowed(AggregateReportDto.class)) {
				List<String> aggregateReportUuids = executeUuidCall(RetroProvider.getAggregateReportFacade().pullUuids());
				DatabaseHelper.getAggregateReportDao().deleteInvalid(aggregateReportUuids, syncCallbacks);
			}

			ConfigProvider.setLastObsoleteUuidsSyncDate(new Date());
		} catch (SQLException | DaoException e) {
			Log.e(SynchronizeDataAsync.class.getSimpleName(), "pullAndRemoveArchivedUuidsSince failed: " + e.getMessage());
		}
	}

	@AddTrace(name = "pushNewPullMissingAndDeleteInvalidDataTrace")
	private void pushNewPullMissingAndDeleteInvalidData()
		throws NoConnectionException, ServerConnectionException, ServerCommunicationException, DaoException {
		// ATTENTION: Since we are working with UUID lists we have no type safety. Look for typos!

		Log.d(SynchronizeDataAsync.class.getSimpleName(), "pushNewPullMissingAndDeleteInvalidData");

		// order is important, due to dependencies (e.g. case & person)

		// first push everything that has been CREATED by the user - otherwise this data my lose it's references to other entities.
		// Example: Case is created using an existing person, meanwhile user loses access to the person
		syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.PUSH_NEW));
		pushNewData();

		boolean viewAllowed;

		syncCallbacks.ifPresent(c -> c.getUpdateSynchronizationStepCallback().accept(SynchronizationDialog.SynchronizationStep.CLEAR_UP));

		// clinical visits
		viewAllowed = DtoUserRightsHelper.isViewAllowed(ClinicalVisitDto.class);
		List<String> clinicalVisitUuids = viewAllowed ? executeUuidCall(RetroProvider.getClinicalVisitFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getClinicalVisitDao().deleteInvalid(clinicalVisitUuids, syncCallbacks);
		// treatments
		viewAllowed = DtoUserRightsHelper.isViewAllowed(TreatmentDto.class);
		List<String> treatmentUuids = viewAllowed ? executeUuidCall(RetroProvider.getTreatmentFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getTreatmentDao().deleteInvalid(treatmentUuids, syncCallbacks);
		// prescriptions
		viewAllowed = DtoUserRightsHelper.isViewAllowed(PrescriptionDto.class);
		List<String> prescriptionUuids = viewAllowed ? executeUuidCall(RetroProvider.getPrescriptionFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getPrescriptionDao().deleteInvalid(prescriptionUuids, syncCallbacks);
		// aggregate reports
		viewAllowed = DtoUserRightsHelper.isViewAllowed(AggregateReportDto.class);
		List<String> aggregateReportUuids = viewAllowed ? executeUuidCall(RetroProvider.getAggregateReportFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getAggregateReportDao().deleteInvalid(aggregateReportUuids, syncCallbacks);
		// weekly reports and entries
		viewAllowed = DtoUserRightsHelper.isViewAllowed(WeeklyReportDto.class);
		List<String> weeklyReportUuids = viewAllowed ? executeUuidCall(RetroProvider.getWeeklyReportFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getWeeklyReportDao().deleteInvalid(weeklyReportUuids, syncCallbacks);
		// tasks
		viewAllowed = DtoUserRightsHelper.isViewAllowed(TaskDto.class);
		List<String> taskUuids = viewAllowed ? executeUuidCall(RetroProvider.getTaskFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getTaskDao().deleteInvalid(taskUuids, syncCallbacks);
		// visits
		viewAllowed = DtoUserRightsHelper.isViewAllowed(VisitDto.class);
		List<String> visitUuids = viewAllowed ? executeUuidCall(RetroProvider.getVisitFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getVisitDao().deleteInvalid(visitUuids, syncCallbacks);
		// contacts
		viewAllowed = DtoUserRightsHelper.isViewAllowed(ContactDto.class);
		List<String> contactUuids = viewAllowed ? executeUuidCall(RetroProvider.getContactFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getContactDao().deleteInvalid(contactUuids, syncCallbacks);
		// additional tests
		viewAllowed = DtoUserRightsHelper.isViewAllowed(AdditionalTestDto.class);
		List<String> additionalTestUuids = viewAllowed ? executeUuidCall(RetroProvider.getAdditionalTestFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getAdditionalTestDao().deleteInvalid(additionalTestUuids, syncCallbacks);
		// sample tests
		viewAllowed = DtoUserRightsHelper.isViewAllowed(PathogenTestDto.class);
		List<String> sampleTestUuids = viewAllowed ? executeUuidCall(RetroProvider.getSampleTestFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getSampleTestDao().deleteInvalid(sampleTestUuids, syncCallbacks);
		// samples
		viewAllowed = DtoUserRightsHelper.isViewAllowed(SampleDto.class);
		List<String> sampleUuids = viewAllowed ? executeUuidCall(RetroProvider.getSampleFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getSampleDao().deleteInvalid(sampleUuids, syncCallbacks);
		//environments
		viewAllowed = DtoUserRightsHelper.isViewAllowed(EnvironmentDto.class);
		List<String> environmentUuids = viewAllowed ? executeUuidCall(RetroProvider.getEnvironmentFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getEnvironmentDao().deleteInvalid(environmentUuids, syncCallbacks);
		//environment samples
		viewAllowed = DtoUserRightsHelper.isViewAllowed(EnvironmentSampleDto.class);
		List<String> environmentSampleUuids =
			viewAllowed ? executeUuidCall(RetroProvider.getEnvironmentSampleFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getEnvironmentSampleDao().deleteInvalid(environmentSampleUuids, syncCallbacks);
		// event participants
		viewAllowed = DtoUserRightsHelper.isViewAllowed(EventParticipantDto.class);
		List<String> eventParticipantUuids = viewAllowed ? executeUuidCall(RetroProvider.getEventParticipantFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getEventParticipantDao().deleteInvalid(eventParticipantUuids, syncCallbacks);
		// events
		viewAllowed = DtoUserRightsHelper.isViewAllowed(EventDto.class);
		List<String> eventUuids = viewAllowed ? executeUuidCall(RetroProvider.getEventFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getEventDao().deleteInvalid(eventUuids, syncCallbacks);
		// immunizations
		viewAllowed = DtoUserRightsHelper.isViewAllowed(ImmunizationDto.class);
		List<String> immunizationUuids = viewAllowed ? executeUuidCall(RetroProvider.getImmunizationFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getImmunizationDao().deleteInvalid(immunizationUuids, syncCallbacks);
		// cases
		viewAllowed = DtoUserRightsHelper.isViewAllowed(CaseDataDto.class);
		List<String> caseUuids = viewAllowed ? executeUuidCall(RetroProvider.getCaseFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getCaseDao().deleteInvalid(caseUuids, syncCallbacks);
		// persons
		viewAllowed = DtoUserRightsHelper.isViewAllowed(PersonDto.class);
		List<String> personUuids = viewAllowed ? executeUuidCall(RetroProvider.getPersonFacade().pullUuids()) : new ArrayList<>();
		DatabaseHelper.getPersonDao().deleteInvalid(personUuids, syncCallbacks);
		// outbreak
		viewAllowed = DtoUserRightsHelper.isViewAllowed(OutbreakDto.class);
		List<String> outbreakUuids = viewAllowed ? executeUuidCall(RetroProvider.getOutbreakFacade().pullActiveUuids()) : new ArrayList<>();
		DatabaseHelper.getOutbreakDao().deleteInvalid(outbreakUuids, syncCallbacks);

		final CampaignFormDataDtoHelper campaignFormDataDtoHelper = new CampaignFormDataDtoHelper();
		List<String> campaignFormMetaUuids = null;
		List<String> campaignUuids = null;
		List<String> campaignFormDataUuids = null;
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			// campaigns
			viewAllowed = DtoUserRightsHelper.isViewAllowed(CampaignFormMetaDto.class);
			campaignFormMetaUuids = viewAllowed ? executeUuidCall(RetroProvider.getCampaignFormMetaFacade().pullUuids()) : new ArrayList<>();
			DatabaseHelper.getCampaignFormMetaDao().deleteInvalid(campaignFormMetaUuids, syncCallbacks);
			viewAllowed = DtoUserRightsHelper.isViewAllowed(CampaignDto.class);
			campaignUuids = viewAllowed ? executeUuidCall(RetroProvider.getCampaignFacade().pullUuids()) : new ArrayList<>();
			DatabaseHelper.getCampaignDao().deleteInvalid(campaignUuids, syncCallbacks);
			campaignFormDataDtoHelper.pushEntities(true);
			viewAllowed = DtoUserRightsHelper.isViewAllowed(CampaignFormDataDto.class);
			campaignFormDataUuids = viewAllowed ? executeUuidCall(RetroProvider.getCampaignFormDataFacade().pullUuids()) : new ArrayList<>();
			DatabaseHelper.getCampaignFormDataDao().deleteInvalid(campaignFormDataUuids, syncCallbacks);
		}

		syncCallbacks.ifPresent(c -> c.getShowNextCleanupItemsCallback().run());

		// order is important, due to dependencies (e.g. case & person)
		new PersonDtoHelper().pullMissing(personUuids, syncCallbacks);
		new CaseDtoHelper().pullMissing(caseUuids, syncCallbacks);
		new ImmunizationDtoHelper().pullMissing(caseUuids, syncCallbacks);
		new EventDtoHelper().pullMissing(eventUuids, syncCallbacks);
		new EventParticipantDtoHelper().pullMissing(eventParticipantUuids, syncCallbacks);
		new SampleDtoHelper().pullMissing(sampleUuids, syncCallbacks);
		new PathogenTestDtoHelper().pullMissing(sampleTestUuids, syncCallbacks);
		new AdditionalTestDtoHelper().pullMissing(additionalTestUuids, syncCallbacks);
		new EnvironmentDtoHelper().pullMissing(environmentUuids, syncCallbacks);
		new EnvironmentSampleDtoHelper().pullMissing(environmentSampleUuids, syncCallbacks);
		new ContactDtoHelper().pullMissing(contactUuids, syncCallbacks);
		new VisitDtoHelper().pullMissing(visitUuids, syncCallbacks);
		new TaskDtoHelper().pullMissing(taskUuids, syncCallbacks);
		new WeeklyReportDtoHelper().pullMissing(weeklyReportUuids, syncCallbacks);
		new AggregateReportDtoHelper().pullMissing(aggregateReportUuids, syncCallbacks);
		new PrescriptionDtoHelper().pullMissing(prescriptionUuids, syncCallbacks);
		new TreatmentDtoHelper().pullMissing(treatmentUuids, syncCallbacks);
		new ClinicalVisitDtoHelper().pullMissing(clinicalVisitUuids, syncCallbacks);

		// CampaignData
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			// meta first
			final CampaignFormMetaDtoHelper campaignFormMetaDtoHelper = new CampaignFormMetaDtoHelper();
			campaignFormMetaDtoHelper.pullMissing(campaignFormMetaUuids, syncCallbacks);

			final CampaignDtoHelper campaignDtoHelper = new CampaignDtoHelper();
			campaignDtoHelper.pullMissing(campaignUuids, syncCallbacks);

			campaignFormDataDtoHelper.pullMissing(campaignFormDataUuids, syncCallbacks);
		}
	}

	@AddTrace(name = "pullMissingAndDeleteInvalidInfrastructureTrace")
	private void pullMissingAndDeleteInvalidInfrastructure()
		throws NoConnectionException, ServerConnectionException, ServerCommunicationException, DaoException {
		// ATTENTION: Since we are working with UUID lists we have no type safety. Look for typos!

		Log.d(SynchronizeDataAsync.class.getSimpleName(), "pullMissingAndDeleteInvalidInfrastructure");

		// TODO get a count first and only retrieve all uuids when count is different?

		// users
		List<String> userUuids = executeUuidCall(RetroProvider.getUserFacade().pullUuids());
		DatabaseHelper.getUserDao().deleteInvalid(userUuids, syncCallbacks);
		// disease configurations
		List<String> diseaseConfigurationUuids = executeUuidCall(RetroProvider.getDiseaseConfigurationFacade().pullUuids());
		DatabaseHelper.getDiseaseConfigurationDao().deleteInvalid(diseaseConfigurationUuids, syncCallbacks);
		// Disease variants
		List<String> customizableEnumValueUuids = executeUuidCall(RetroProvider.getCustomizableEnumValueFacade().pullUuids());
		DatabaseHelper.getCustomizableEnumValueDao().deleteInvalid(customizableEnumValueUuids, syncCallbacks);
		// feature configurations
		List<String> featureConfigurationUuids = executeUuidCall(RetroProvider.getFeatureConfigurationFacade().pullUuids());
		DatabaseHelper.getFeatureConfigurationDao().deleteInvalid(featureConfigurationUuids, syncCallbacks);
		// user role config
		List<String> userRoleConfigUuids = executeUuidCall(RetroProvider.getUserRoleFacade().pullUuids());
		DatabaseHelper.getUserRoleDao().deleteInvalid(userRoleConfigUuids, syncCallbacks);
		// points of entry
		List<String> pointOfEntryUuids = executeUuidCall(RetroProvider.getPointOfEntryFacade().pullUuids());
		DatabaseHelper.getPointOfEntryDao().deleteInvalid(pointOfEntryUuids, syncCallbacks);
		// facilities
		List<String> facilityUuids = executeUuidCall(RetroProvider.getFacilityFacade().pullUuids());
		DatabaseHelper.getFacilityDao().deleteInvalid(facilityUuids, syncCallbacks);
		// communities
		List<String> communityUuids = executeUuidCall(RetroProvider.getCommunityFacade().pullUuids());
		DatabaseHelper.getCommunityDao().deleteInvalid(communityUuids, syncCallbacks);
		// districts
		List<String> districtUuids = executeUuidCall(RetroProvider.getDistrictFacade().pullUuids());
		DatabaseHelper.getDistrictDao().deleteInvalid(districtUuids, syncCallbacks);
		// regions
		List<String> regionUuids = executeUuidCall(RetroProvider.getRegionFacade().pullUuids());
		DatabaseHelper.getRegionDao().deleteInvalid(regionUuids, syncCallbacks);
		// areas
		List<String> areaUuids = executeUuidCall(RetroProvider.getAreaFacade().pullUuids());
		DatabaseHelper.getAreaDao().deleteInvalid(areaUuids, syncCallbacks);
		// countries
		List<String> countryUuids = executeUuidCall(RetroProvider.getCountryFacade().pullUuids());
		DatabaseHelper.getCountryDao().deleteInvalid(countryUuids, syncCallbacks);
		// subcontinents
		List<String> subcontinentUuids = executeUuidCall(RetroProvider.getSubcontinentFacade().pullUuids());
		DatabaseHelper.getSubcontinentDao().deleteInvalid(subcontinentUuids, syncCallbacks);
		// continents
		List<String> continentUuids = executeUuidCall(RetroProvider.getContinentFacade().pullUuids());
		DatabaseHelper.getContinentDao().deleteInvalid(continentUuids, syncCallbacks);

		syncCallbacks.ifPresent(c -> c.getShowNextCleanupItemsCallback().run());

		// order is important, due to dependencies
		new ContinentDtoHelper().pullMissing(continentUuids, syncCallbacks);
		new SubcontinentDtoHelper().pullMissing(subcontinentUuids, syncCallbacks);
		new CountryDtoHelper().pullMissing(countryUuids, syncCallbacks);
		new AreaDtoHelper().pullMissing(areaUuids, syncCallbacks);
		new RegionDtoHelper().pullMissing(regionUuids, syncCallbacks);
		new DistrictDtoHelper().pullMissing(districtUuids, syncCallbacks);
		new CommunityDtoHelper().pullMissing(communityUuids, syncCallbacks);
		new FacilityDtoHelper().pullMissing(facilityUuids, syncCallbacks);
		new PointOfEntryDtoHelper().pullMissing(pointOfEntryUuids, syncCallbacks);
		new UserRoleDtoHelper().pullMissing(userRoleConfigUuids, syncCallbacks);
		new UserDtoHelper().pullMissing(userUuids, syncCallbacks);
		new DiseaseConfigurationDtoHelper().pullMissing(diseaseConfigurationUuids, syncCallbacks);
		new CustomizableEnumValueDtoHelper().pullMissing(customizableEnumValueUuids, syncCallbacks);
		new FeatureConfigurationDtoHelper().pullMissing(featureConfigurationUuids, syncCallbacks);
	}

	private List<String> executeUuidCall(Call<List<String>> call) throws ServerConnectionException, ServerCommunicationException {
		Response<List<String>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			throw new ServerCommunicationException(e);
		}
		if (!response.isSuccessful()) {
			RetroProvider.throwException(response);
		}
		return response.body();
	}

	public static void call(
		SyncMode syncMode,
		final Context context,
		SynchronizationDialog.SynchronizationCallbacks callbacks,
		final SyncCallback callback) {

		new SynchronizeDataAsync(syncMode, context, callbacks) {

			@Override
			protected void onPostExecute(Void aVoid) {
				if (callback != null) {
					callback.call(syncFailed, syncFailedMessage);
				}
				if (context != null) {
					TaskNotificationService.doTaskNotification(context);
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public static void call(SyncMode syncMode, final Context context, final SyncCallback callback) {

		call(syncMode, context, null, callback);
	}

	public enum SyncMode {
		Changes,
		Complete,
		/**
		 * Also repulls all non-infrastructure data and users.
		 * Use to handle conflict states resulting out of bugs.
		 */
		CompleteAndRepull,
	}
}
