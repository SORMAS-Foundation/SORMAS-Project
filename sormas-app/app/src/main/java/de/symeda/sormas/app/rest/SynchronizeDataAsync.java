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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.firebase.perf.metrics.Trace;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.CampaignDtoHelper;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormDataDtoHelper;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMetaDtoHelper;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationDtoHelper;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisitDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDtoHelper;
import de.symeda.sormas.app.backend.customizableenum.CustomizableEnumValueDtoHelper;
import de.symeda.sormas.app.backend.disease.DiseaseConfigurationDtoHelper;
import de.symeda.sormas.app.backend.event.EventDtoHelper;
import de.symeda.sormas.app.backend.event.EventParticipantDtoHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.feature.FeatureConfigurationDtoHelper;
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
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.therapy.PrescriptionDtoHelper;
import de.symeda.sormas.app.backend.therapy.TreatmentDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.backend.user.UserRoleConfigDtoHelper;
import de.symeda.sormas.app.backend.visit.VisitDtoHelper;
import de.symeda.sormas.app.core.TaskNotificationService;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import retrofit2.Call;
import retrofit2.Response;

public class SynchronizeDataAsync extends AsyncTask<Void, Void, Void> {

	/**
	 * Should be set to true when the synchronization fails and reset to false as soon
	 * as the last callback is called (i.e. the synchronization has been completed/cancelled).
	 */
	protected boolean syncFailed;
	protected String syncFailedMessage;

	private SyncMode syncMode;
	private final Context context;

	private SynchronizeDataAsync(SyncMode syncMode, Context context) {
		this.syncMode = syncMode;
		this.context = context;
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
			Trace syncModeTrace;

			switch (syncMode) {
			case Changes:
				syncModeTrace = FirebasePerformance.getInstance().newTrace("syncModeChangesTrace");
				syncModeTrace.start();

				// Prioritize pushing new data
				pushNewData();
				// Infrastructure always has to be pulled - otherwise referenced data may be lost (e.g. #586)
				pullInfrastructure();
				// Pull and remove deleted entities when the last time this has been done is more than 24 hours ago
				if (ConfigProvider.getLastDeletedSyncDate() == null
					|| DateHelper.getFullDaysBetween(ConfigProvider.getLastDeletedSyncDate(), new Date()) >= 1) {
					pullAndRemoveDeletedUuidsSince(ConfigProvider.getLastDeletedSyncDate());
				}
				// Pull and remove archived entities when the last time this has been done is more than 24 hours ago
				if (ConfigProvider.getLastArchivedSyncDate() == null
					|| DateHelper.getFullDaysBetween(ConfigProvider.getLastArchivedSyncDate(), new Date()) >= 0) {
					pullAndRemoveArchivedUuidsSince(ConfigProvider.getLastArchivedSyncDate());
				}
				// Pull changed data and push existing data that has been changed on the mobile device
				synchronizeChangedData();

				syncModeTrace.stop();
				break;
			case Complete:
				syncModeTrace = FirebasePerformance.getInstance().newTrace("syncModeCompleteTrace");
				syncModeTrace.start();

				pullInfrastructure(); // do before missing, because we may have a completely empty database
				pullMissingAndDeleteInvalidInfrastructure();
				pushNewPullMissingAndDeleteInvalidData();
				synchronizeChangedData();

				syncModeTrace.stop();
				break;
			case CompleteAndRepull:
				syncModeTrace = FirebasePerformance.getInstance().newTrace("syncModeCompleteAndRepullTrace");
				syncModeTrace.start();

				pullInfrastructure(); // do before missing, because we may have a completely empty database
				pullMissingAndDeleteInvalidInfrastructure();
				repullData();
				pushNewPullMissingAndDeleteInvalidData();
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
			&& (DatabaseHelper.getCampaignFormDataDao().isAnyModified());
		return DatabaseHelper.getCaseDao().isAnyModified()
			|| DatabaseHelper.getImmunizationDao().isAnyModified()
			|| DatabaseHelper.getContactDao().isAnyModified()
			|| DatabaseHelper.getPersonDao().isAnyModified()
			|| DatabaseHelper.getEventDao().isAnyModified()
			|| DatabaseHelper.getEventParticipantDao().isAnyModified()
			|| DatabaseHelper.getSampleDao().isAnyModified()
			|| DatabaseHelper.getSampleTestDao().isAnyModified()
			|| DatabaseHelper.getAdditionalTestDao().isAnyModified()
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

		new PersonDtoHelper().pushEntities(true);
		new CaseDtoHelper().pushEntities(true);
		new ImmunizationDtoHelper().pushEntities(true);
		new EventDtoHelper().pushEntities(true);
		new EventParticipantDtoHelper().pushEntities(true);
		new SampleDtoHelper().pushEntities(true);
		new PathogenTestDtoHelper().pushEntities(true);
		new AdditionalTestDtoHelper().pushEntities(true);
		new ContactDtoHelper().pushEntities(true);
		new VisitDtoHelper().pushEntities(true);
		new TaskDtoHelper().pushEntities(true);
		new WeeklyReportDtoHelper().pushEntities(true);
		new AggregateReportDtoHelper().pushEntities(true);
		new PrescriptionDtoHelper().pushEntities(true);
		new TreatmentDtoHelper().pushEntities(true);
		new ClinicalVisitDtoHelper().pushEntities(true);
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
		ContactDtoHelper contactDtoHelper = new ContactDtoHelper();
		VisitDtoHelper visitDtoHelper = new VisitDtoHelper();
		TaskDtoHelper taskDtoHelper = new TaskDtoHelper();
		WeeklyReportDtoHelper weeklyReportDtoHelper = new WeeklyReportDtoHelper();
		AggregateReportDtoHelper aggregateReportDtoHelper = new AggregateReportDtoHelper();
		PrescriptionDtoHelper prescriptionDtoHelper = new PrescriptionDtoHelper();
		TreatmentDtoHelper treatmentDtoHelper = new TreatmentDtoHelper();
		ClinicalVisitDtoHelper clinicalVisitDtoHelper = new ClinicalVisitDtoHelper();

		// order is important, due to dependencies (e.g. case & person)

		new OutbreakDtoHelper().pullEntities(false, context);
		new DiseaseConfigurationDtoHelper().pullEntities(false, context);
		new CustomizableEnumValueDtoHelper().pullEntities(false, context);

		boolean personsNeedPull = personDtoHelper.pullAndPushEntities(context);
		boolean casesNeedPull = caseDtoHelper.pullAndPushEntities(context);
		boolean immunizationsNeedPull = immunizationDtoHelper.pullAndPushEntities(context);
		boolean eventsNeedPull = eventDtoHelper.pullAndPushEntities(context);
		boolean eventParticipantsNeedPull = eventParticipantDtoHelper.pullAndPushEntities(context);
		boolean samplesNeedPull = sampleDtoHelper.pullAndPushEntities(context);
		boolean sampleTestsNeedPull = pathogenTestDtoHelper.pullAndPushEntities(context);
		boolean additionalTestsNeedPull = additionalTestDtoHelper.pullAndPushEntities(context);
		boolean contactsNeedPull = contactDtoHelper.pullAndPushEntities(context);
		boolean visitsNeedPull = visitDtoHelper.pullAndPushEntities(context);
		boolean tasksNeedPull = taskDtoHelper.pullAndPushEntities(context);
		boolean weeklyReportsNeedPull = weeklyReportDtoHelper.pullAndPushEntities(context);
		boolean aggregateReportsNeedPull = aggregateReportDtoHelper.pullAndPushEntities(context);
		boolean prescriptionsNeedPull = prescriptionDtoHelper.pullAndPushEntities(context);
		boolean treatmentsNeedPull = treatmentDtoHelper.pullAndPushEntities(context);
		boolean clinicalVisitsNeedPull = clinicalVisitDtoHelper.pullAndPushEntities(context);

		casesNeedPull |= clinicalVisitsNeedPull;

		if (personsNeedPull)
			personDtoHelper.pullEntities(true, context);
		if (casesNeedPull)
			caseDtoHelper.pullEntities(true, context);
		if (immunizationsNeedPull)
			immunizationDtoHelper.pullEntities(true, context);
		if (eventsNeedPull)
			eventDtoHelper.pullEntities(true, context);
		if (eventParticipantsNeedPull)
			eventParticipantDtoHelper.pullEntities(true, context);
		if (samplesNeedPull)
			sampleDtoHelper.pullEntities(true, context);
		if (sampleTestsNeedPull)
			pathogenTestDtoHelper.pullEntities(true, context);
		if (additionalTestsNeedPull)
			additionalTestDtoHelper.pullEntities(true, context);
		if (contactsNeedPull)
			contactDtoHelper.pullEntities(true, context);
		if (visitsNeedPull)
			visitDtoHelper.pullEntities(true, context);
		if (tasksNeedPull)
			taskDtoHelper.pullEntities(true, context);
		if (weeklyReportsNeedPull)
			weeklyReportDtoHelper.pullEntities(true, context);
		if (aggregateReportsNeedPull)
			aggregateReportDtoHelper.pullEntities(true, context);
		if (prescriptionsNeedPull)
			prescriptionDtoHelper.pullEntities(true, context);
		if (treatmentsNeedPull)
			treatmentDtoHelper.pullEntities(true, context);
		if (clinicalVisitsNeedPull)
			clinicalVisitDtoHelper.pullEntities(true, context);

		// Campaigns
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			final CampaignFormDataDtoHelper campaignFormDataDtoHelper = new CampaignFormDataDtoHelper();
			if (campaignFormDataDtoHelper.pullAndPushEntities(context))
				campaignFormDataDtoHelper.pullEntities(true, context);
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
		ContactDtoHelper contactDtoHelper = new ContactDtoHelper();
		VisitDtoHelper visitDtoHelper = new VisitDtoHelper();
		TaskDtoHelper taskDtoHelper = new TaskDtoHelper();
		WeeklyReportDtoHelper weeklyReportDtoHelper = new WeeklyReportDtoHelper();
		AggregateReportDtoHelper aggregateReportDtoHelper = new AggregateReportDtoHelper();
		PrescriptionDtoHelper prescriptionDtoHelper = new PrescriptionDtoHelper();
		TreatmentDtoHelper treatmentDtoHelper = new TreatmentDtoHelper();
		ClinicalVisitDtoHelper clinicalVisitDtoHelper = new ClinicalVisitDtoHelper();

		// order is important, due to dependencies (e.g. case & person)

		new UserRoleConfigDtoHelper().repullEntities(context);
		new DiseaseClassificationDtoHelper().repullEntities(context);
		new UserDtoHelper().repullEntities(context);
		new OutbreakDtoHelper().repullEntities(context);
		new DiseaseConfigurationDtoHelper().repullEntities(context);
		new CustomizableEnumValueDtoHelper().repullEntities(context);
		new FeatureConfigurationDtoHelper().repullEntities(context);
		personDtoHelper.repullEntities(context);
		caseDtoHelper.repullEntities(context);
		immunizationDtoHelper.repullEntities(context);
		eventDtoHelper.repullEntities(context);
		eventParticipantDtoHelper.repullEntities(context);
		sampleDtoHelper.repullEntities(context);
		pathogenTestDtoHelper.repullEntities(context);
		additionalTestDtoHelper.repullEntities(context);
		contactDtoHelper.repullEntities(context);
		visitDtoHelper.repullEntities(context);
		taskDtoHelper.repullEntities(context);
		weeklyReportDtoHelper.repullEntities(context);
		aggregateReportDtoHelper.repullEntities(context);
		prescriptionDtoHelper.repullEntities(context);
		treatmentDtoHelper.repullEntities(context);
		clinicalVisitDtoHelper.repullEntities(context);

		// Campaigns
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			final CampaignFormDataDtoHelper campaignFormDataDtoHelper = new CampaignFormDataDtoHelper();
			campaignFormDataDtoHelper.repullEntities(context);
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
						InfrastructureHelper.handlePulledInfrastructureData(infrastructureData);
					}
				}
			} catch (IOException e) {
				Log.e(SynchronizeDataAsync.class.getSimpleName(), "Error when trying to pull infrastructure data: " + e.getMessage());
			}
		}
	}

	@AddTrace(name = "pullInitialInfrastructureTrace")
	private void pullInitialInfrastructure() throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {
		new ContinentDtoHelper().pullEntities(false, context);
		new SubcontinentDtoHelper().pullEntities(false, context);
		new CountryDtoHelper().pullEntities(false, context);

		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
			new AreaDtoHelper().pullEntities(false, context);
		}
		new RegionDtoHelper().pullEntities(false, context);
		new DistrictDtoHelper().pullEntities(false, context);
		new CommunityDtoHelper().pullEntities(false, context);
		new FacilityDtoHelper().pullEntities(false, context);
		new PointOfEntryDtoHelper().pullEntities(false, context);
		new UserDtoHelper().pullEntities(false, context);
		new DiseaseClassificationDtoHelper().pullEntities(false, context);
		new DiseaseConfigurationDtoHelper().pullEntities(false, context);
		new CustomizableEnumValueDtoHelper().pullEntities(false, context);

		// user role configurations may be removed, so have to pull the deleted uuids
		// this may be applied to other entities later as well
		Date latestChangeDate = DatabaseHelper.getUserRoleConfigDao().getLatestChangeDate();
		List<String> userRoleConfigUuids =
			executeUuidCall(RetroProvider.getUserRoleConfigFacade().pullDeletedUuidsSince(latestChangeDate != null ? latestChangeDate.getTime() : 0));
		DatabaseHelper.getUserRoleConfigDao().delete(userRoleConfigUuids);

		new UserRoleConfigDtoHelper().pullEntities(false, context);

		Date featureConfigurationChangeDate = DatabaseHelper.getFeatureConfigurationDao().getLatestChangeDate();
		List<String> featureConfigurationConfigUuids = executeUuidCall(
			RetroProvider.getFeatureConfigurationFacade()
				.pullDeletedUuidsSince(featureConfigurationChangeDate != null ? featureConfigurationChangeDate.getTime() : 0));
		DatabaseHelper.getFeatureConfigurationDao().delete(featureConfigurationConfigUuids);

		new FeatureConfigurationDtoHelper().pullEntities(false, context);

		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			new CampaignFormMetaDtoHelper().pullEntities(false, context);
			new CampaignDtoHelper().pullEntities(false, context);
		}

		ConfigProvider.setInitialSyncRequired(false);
	}

	@AddTrace(name = "pullAndRemoveArchivedUuidsSinceTrace")
	private void pullAndRemoveArchivedUuidsSince(Date since) throws NoConnectionException, ServerConnectionException, ServerCommunicationException {
		Log.d(SynchronizeDataAsync.class.getSimpleName(), "pullArchivedUuidsSince");

		try {
			// Cases
			List<String> caseUuids = executeUuidCall(RetroProvider.getCaseFacade().pullArchivedUuidsSince(since != null ? since.getTime() : 0));
			for (String caseUuid : caseUuids) {
				DatabaseHelper.getCaseDao().deleteCaseAndAllDependingEntities(caseUuid);
			}

			// Contacts
			List<String> contactUuids = executeUuidCall(RetroProvider.getContactFacade().pullArchivedUuidsSince(since != null ? since.getTime() : 0));
			for (String contactUuid : contactUuids) {
				DatabaseHelper.getContactDao().deleteContactAndAllDependingEntities(contactUuid);
			}

			// Events
			List<String> eventUuids = executeUuidCall(RetroProvider.getEventFacade().pullArchivedUuidsSince(since != null ? since.getTime() : 0));
			for (String eventUuid : eventUuids) {
				DatabaseHelper.getEventDao().deleteEventAndAllDependingEntities(eventUuid);
			}

			// EventParticipant
			List<String> eventParticipantUuids =
				executeUuidCall(RetroProvider.getEventParticipantFacade().pullArchivedUuidsSince(since != null ? since.getTime() : 0));
			for (String eventParticipantUuid : eventParticipantUuids) {
				DatabaseHelper.getEventParticipantDao().deleteEventParticipantAndAllDependingEntities(eventParticipantUuid);
			}

			// Tasks
			List<String> taskUuids = executeUuidCall(RetroProvider.getTaskFacade().pullArchivedUuidsSince(since != null ? since.getTime() : 0));
			for (String taskUuid : taskUuids) {
				DatabaseHelper.getTaskDao().deleteTaskAndAllDependingEntities(taskUuid);
			}

			// Inactive outbreaks
			List<String> outbreakUuids =
				executeUuidCall(RetroProvider.getOutbreakFacade().pullInactiveUuidsSince(since != null ? since.getTime() : 0));
			for (String outbreakUuid : outbreakUuids) {
				DatabaseHelper.getOutbreakDao().deleteOutbreakAndAllDependingEntities(outbreakUuid);
			}

			ConfigProvider.setLastArchivedSyncDate(new Date());
		} catch (SQLException e) {
			Log.e(SynchronizeDataAsync.class.getSimpleName(), "pullAndRemoveArchivedUuidsSince failed: " + e.getMessage());
		}
	}

	@AddTrace(name = "pullAndRemoveDeletedUuidsSinceTrace")
	private void pullAndRemoveDeletedUuidsSince(Date since) throws NoConnectionException, ServerConnectionException, ServerCommunicationException {
		Log.d(SynchronizeDataAsync.class.getSimpleName(), "pullDeletedUuidsSince");

		try {
			// Cases
			List<String> caseUuids = executeUuidCall(RetroProvider.getCaseFacade().pullDeletedUuidsSince(since != null ? since.getTime() : 0));
			for (String caseUuid : caseUuids) {
				DatabaseHelper.getCaseDao().deleteCaseAndAllDependingEntities(caseUuid);
			}

			// Immunization
			List<String> immunizationUuids =
				executeUuidCall(RetroProvider.getImmunizationFacade().pullDeletedUuidsSince(since != null ? since.getTime() : 0));
			for (String immunizationUuid : immunizationUuids) {
				DatabaseHelper.getImmunizationDao().deleteImmunizationAndAllDependingEntities(immunizationUuid);
			}

			// Events
			List<String> eventUuids = executeUuidCall(RetroProvider.getEventFacade().pullDeletedUuidsSince(since != null ? since.getTime() : 0));
			for (String eventUuid : eventUuids) {
				DatabaseHelper.getEventDao().deleteEventAndAllDependingEntities(eventUuid);
			}

			//Event participants
			List<String> eventParticipantUuids =
				executeUuidCall(RetroProvider.getEventParticipantFacade().pullDeletedUuidsSince(since != null ? since.getTime() : 0));
			for (String eventParticipantUuid : eventParticipantUuids) {
				DatabaseHelper.getEventParticipantDao().deleteEventParticipant(eventParticipantUuid);
			}

			// Contacts
			List<String> contactUuids = executeUuidCall(RetroProvider.getContactFacade().pullDeletedUuidsSince(since != null ? since.getTime() : 0));
			for (String contactUuid : contactUuids) {
				DatabaseHelper.getContactDao().deleteContactAndAllDependingEntities(contactUuid);
			}

			// Samples
			List<String> sampleUuids = executeUuidCall(RetroProvider.getSampleFacade().pullDeletedUuidsSince(since != null ? since.getTime() : 0));
			for (String sampleUuid : sampleUuids) {
				DatabaseHelper.getSampleDao().deleteSampleAndAllDependingEntities(sampleUuid);
			}

			ConfigProvider.setLastDeletedSyncDate(new Date());
		} catch (SQLException e) {
			Log.e(SynchronizeDataAsync.class.getSimpleName(), "pullAndRemoveDeletedUuidsSince failed: " + e.getMessage());
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
		pushNewData();

		// weekly reports and entries
		List<String> weeklyReportUuids = executeUuidCall(RetroProvider.getWeeklyReportFacade().pullUuids());
		DatabaseHelper.getWeeklyReportDao().deleteInvalid(weeklyReportUuids);
		// aggregate reports
		List<String> aggregateReportUuids = executeUuidCall(RetroProvider.getAggregateReportFacade().pullUuids());
		DatabaseHelper.getAggregateReportDao().deleteInvalid(aggregateReportUuids);
		// tasks
		List<String> taskUuids = executeUuidCall(RetroProvider.getTaskFacade().pullUuids());
		DatabaseHelper.getTaskDao().deleteInvalid(taskUuids);
		// visits
		List<String> visitUuids = executeUuidCall(RetroProvider.getVisitFacade().pullUuids());
		DatabaseHelper.getVisitDao().deleteInvalid(visitUuids);
		// contacts
		List<String> contactUuids = executeUuidCall(RetroProvider.getContactFacade().pullUuids());
		DatabaseHelper.getContactDao().deleteInvalid(contactUuids);
		// sample tests
		List<String> sampleTestUuids = executeUuidCall(RetroProvider.getSampleTestFacade().pullUuids());
		DatabaseHelper.getSampleTestDao().deleteInvalid(sampleTestUuids);
		// additional tests
		List<String> additionalTestUuids = executeUuidCall(RetroProvider.getAdditionalTestFacade().pullUuids());
		DatabaseHelper.getAdditionalTestDao().deleteInvalid(additionalTestUuids);
		// samples
		List<String> sampleUuids = executeUuidCall(RetroProvider.getSampleFacade().pullUuids());
		DatabaseHelper.getSampleDao().deleteInvalid(sampleUuids);
		// event participants
		List<String> eventParticipantUuids = executeUuidCall(RetroProvider.getEventParticipantFacade().pullUuids());
		DatabaseHelper.getEventParticipantDao().deleteInvalid(eventParticipantUuids);
		// events
		List<String> eventUuids = executeUuidCall(RetroProvider.getEventFacade().pullUuids());
		DatabaseHelper.getEventDao().deleteInvalid(eventUuids);
		// treatments
		List<String> treatmentUuids = executeUuidCall(RetroProvider.getTreatmentFacade().pullUuids());
		DatabaseHelper.getTreatmentDao().deleteInvalid(treatmentUuids);
		// prescriptions
		List<String> prescriptionUuids = executeUuidCall(RetroProvider.getPrescriptionFacade().pullUuids());
		DatabaseHelper.getPrescriptionDao().deleteInvalid(prescriptionUuids);
		// clinical visits
		List<String> clinicalVisitUuids = executeUuidCall(RetroProvider.getClinicalVisitFacade().pullUuids());
		DatabaseHelper.getClinicalVisitDao().deleteInvalid(clinicalVisitUuids);
		// immunizations
		List<String> immunizationUuids = executeUuidCall(RetroProvider.getImmunizationFacade().pullUuids());
		DatabaseHelper.getImmunizationDao().deleteInvalid(immunizationUuids);
		// cases
		List<String> caseUuids = executeUuidCall(RetroProvider.getCaseFacade().pullUuids());
		DatabaseHelper.getCaseDao().deleteInvalid(caseUuids);
		// persons
		List<String> personUuids = executeUuidCall(RetroProvider.getPersonFacade().pullUuids());
		DatabaseHelper.getPersonDao().deleteInvalid(personUuids);
		// outbreak
		List<String> outbreakUuids = executeUuidCall(RetroProvider.getOutbreakFacade().pullActiveUuids());
		DatabaseHelper.getOutbreakDao().deleteInvalid(outbreakUuids);

		// order is important, due to dependencies (e.g. case & person)

		new PersonDtoHelper().pullMissing(personUuids);
		new CaseDtoHelper().pullMissing(caseUuids);
		new ImmunizationDtoHelper().pullMissing(caseUuids);
		new PrescriptionDtoHelper().pullMissing(prescriptionUuids);
		new TreatmentDtoHelper().pullMissing(treatmentUuids);
		new EventDtoHelper().pullMissing(eventUuids);
		new EventParticipantDtoHelper().pullMissing(eventParticipantUuids);
		new SampleDtoHelper().pullMissing(sampleUuids);
		new AdditionalTestDtoHelper().pullMissing(additionalTestUuids);
		new PathogenTestDtoHelper().pullMissing(sampleTestUuids);
		new ContactDtoHelper().pullMissing(contactUuids);
		new VisitDtoHelper().pullMissing(visitUuids);
		new TaskDtoHelper().pullMissing(taskUuids);
		new WeeklyReportDtoHelper().pullMissing(weeklyReportUuids);
		new AggregateReportDtoHelper().pullMissing(aggregateReportUuids);
		new PrescriptionDtoHelper().pullMissing(prescriptionUuids);
		new TreatmentDtoHelper().pullMissing(treatmentUuids);
		new ClinicalVisitDtoHelper().pullMissing(clinicalVisitUuids);

		// CampaignData
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			final CampaignFormDataDtoHelper campaignFormDataDtoHelper = new CampaignFormDataDtoHelper();
			campaignFormDataDtoHelper.pushEntities(true);
			final List<String> campaignFormDataUuids = executeUuidCall(RetroProvider.getCampaignFormDataFacade().pullUuids());
			DatabaseHelper.getCampaignFormDataDao().deleteInvalid(campaignFormDataUuids);
			campaignFormDataDtoHelper.pullMissing(campaignFormDataUuids);
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
		DatabaseHelper.getUserDao().deleteInvalid(userUuids);
		// disease configurations
		List<String> diseaseConfigurationUuids = executeUuidCall(RetroProvider.getDiseaseConfigurationFacade().pullUuids());
		DatabaseHelper.getDiseaseConfigurationDao().deleteInvalid(diseaseConfigurationUuids);
		// Disease variants
		List<String> customizableEnumValueUuids = executeUuidCall(RetroProvider.getCustomizableEnumValueFacade().pullUuids());
		DatabaseHelper.getCustomizableEnumValueDao().deleteInvalid(customizableEnumValueUuids);
		// feature configurations
		List<String> featureConfigurationUuids = executeUuidCall(RetroProvider.getFeatureConfigurationFacade().pullUuids());
		DatabaseHelper.getFeatureConfigurationDao().deleteInvalid(featureConfigurationUuids);
		// user role config
		List<String> userRoleConfigUuids = executeUuidCall(RetroProvider.getUserRoleConfigFacade().pullUuids());
		DatabaseHelper.getUserRoleConfigDao().deleteInvalid(userRoleConfigUuids);
		// points of entry
		List<String> pointOfEntryUuids = executeUuidCall(RetroProvider.getPointOfEntryFacade().pullUuids());
		DatabaseHelper.getPointOfEntryDao().deleteInvalid(pointOfEntryUuids);
		// facilities
		List<String> facilityUuids = executeUuidCall(RetroProvider.getFacilityFacade().pullUuids());
		DatabaseHelper.getFacilityDao().deleteInvalid(facilityUuids);
		// communities
		List<String> communityUuids = executeUuidCall(RetroProvider.getCommunityFacade().pullUuids());
		DatabaseHelper.getCommunityDao().deleteInvalid(communityUuids);
		// districts
		List<String> districtUuids = executeUuidCall(RetroProvider.getDistrictFacade().pullUuids());
		DatabaseHelper.getDistrictDao().deleteInvalid(districtUuids);
		// regions
		List<String> regionUuids = executeUuidCall(RetroProvider.getRegionFacade().pullUuids());
		DatabaseHelper.getRegionDao().deleteInvalid(regionUuids);
		// areas
		List<String> areaUuids = executeUuidCall(RetroProvider.getAreaFacade().pullUuids());
		DatabaseHelper.getAreaDao().deleteInvalid(areaUuids);
		// countries
		List<String> countryUuids = executeUuidCall(RetroProvider.getCountryFacade().pullUuids());
		DatabaseHelper.getCountryDao().deleteInvalid(countryUuids);
		// subcontinents
		List<String> subcontinentUuids = executeUuidCall(RetroProvider.getSubcontinentFacade().pullUuids());
		DatabaseHelper.getSubcontinentDao().deleteInvalid(subcontinentUuids);
		// continents
		List<String> continentUuids = executeUuidCall(RetroProvider.getContinentFacade().pullUuids());
		DatabaseHelper.getContinentDao().deleteInvalid(continentUuids);

		// order is important, due to dependencies

		new ContinentDtoHelper().pullMissing(continentUuids);
		new SubcontinentDtoHelper().pullMissing(subcontinentUuids);
		new CountryDtoHelper().pullMissing(countryUuids);

		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
			new AreaDtoHelper().pullMissing(areaUuids);
		}

		new RegionDtoHelper().pullMissing(regionUuids);
		new DistrictDtoHelper().pullMissing(districtUuids);
		new CommunityDtoHelper().pullMissing(communityUuids);
		new FacilityDtoHelper().pullMissing(facilityUuids);
		new PointOfEntryDtoHelper().pullMissing(pointOfEntryUuids);
		new UserRoleConfigDtoHelper().pullMissing(userRoleConfigUuids);
		new UserDtoHelper().pullMissing(userUuids);
		new DiseaseConfigurationDtoHelper().pullMissing(diseaseConfigurationUuids);
		new CustomizableEnumValueDtoHelper().pullMissing(customizableEnumValueUuids);
		new FeatureConfigurationDtoHelper().pullMissing(featureConfigurationUuids);

		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			// campaigns
			List<String> campaignUuids = executeUuidCall(RetroProvider.getCampaignFacade().pullUuids());
			DatabaseHelper.getCampaignDao().deleteInvalid(campaignUuids);
			// campaignFormMetas
			List<String> campaignFormMetaUuids = executeUuidCall(RetroProvider.getCampaignFormMetaFacade().pullUuids());
			DatabaseHelper.getCampaignFormMetaDao().deleteInvalid(campaignFormMetaUuids);

			new CampaignFormMetaDtoHelper().pullMissing(campaignFormMetaUuids);
			new CampaignDtoHelper().pullMissing(campaignUuids);
		}
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

	public static void call(SyncMode syncMode, final Context context, final SyncCallback callback) {
		new SynchronizeDataAsync(syncMode, context) {

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
