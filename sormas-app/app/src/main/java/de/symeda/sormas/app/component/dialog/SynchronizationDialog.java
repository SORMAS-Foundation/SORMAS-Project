package de.symeda.sormas.app.component.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
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
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.DtoFeatureConfigHelper;
import de.symeda.sormas.app.backend.common.DtoUserRightsHelper;
import de.symeda.sormas.app.databinding.DialogSynchronizationProgressItemLayoutBinding;
import de.symeda.sormas.app.databinding.DialogSynchronizationRootLayoutBinding;

public class SynchronizationDialog extends AbstractDialog {

	private static final Logger logger = LoggerFactory.getLogger(SynchronizationDialog.class);

	private static final String BINDING_LOCK = "BINDING_LOCK";

	private DialogSynchronizationRootLayoutBinding contentBinding;
	private SynchronizationStep currentSyncStep;
	private int currentSyncStepIndex = 0;
	private final List<DialogSynchronizationProgressItemLayoutBinding> progressItemBindings = new ArrayList<>();
	private int progressItemBindingsIndex = 0;
	private boolean updatingProgressItemBindings = false;
	private DialogSynchronizationProgressItemLayoutBinding currentProgressItemBinding;
	private final SynchronizationCallbacks syncCallbacks;

	public SynchronizationDialog(final FragmentActivity activity) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_synchronization_root_layout,
			R.layout.dialog_root_two_button_panel_layout,
			R.string.heading_synchronization,
			-1);

		syncCallbacks = new SynchronizationCallbacks(
			this::updateStepNumber,
			this::updateSynchronizationStep,
			this::loadNext,
			this::updatePulls,
			this::updatePushes,
			this::updateDeletions,
			this::updatePushTotal,
			this::showDialog,
			this::showNextClearItems);
	}

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogSynchronizationRootLayoutBinding) binding;
		contentBinding.setLifecycleOwner(getActivity());
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		// Nothing to do here
	}

	private void showDialog() {
		getActivity().runOnUiThread(this::show);
	}

	/**
	 * Updates the dialog to show the next synchronization step as active and display
	 * the corresponding progress items.
	 */
	private void updateSynchronizationStep(SynchronizationStep synchronizationStep) {

		synchronized (BINDING_LOCK) {
			try {
				if (updatingProgressItemBindings) {
					BINDING_LOCK.wait();
				}
			} catch (InterruptedException e) {
				logger.error(
					"InterruptedException when trying to perform BINDING_LOCK.wait() in SynchronizationDialog.updateSynchronizationStep: "
						+ e.getMessage());
			}
		}

		updatingProgressItemBindings = true;
		currentProgressItemBinding = null;
		currentSyncStep = synchronizationStep;

		switch (synchronizationStep) {
		case PUSH_NEW:
			showPushNewProgressItems();
			break;
		case PULL_INFRASTRUCTURE:
			showPullInfrastructureProgressItems(false);
			break;
		case PULL_INFRASTRUCTURE_INITIAL:
			showPullInfrastructureProgressItems(true);
			break;
		case CLEAR_UP:
			showClearUpProgressItems(true);
			break;
		case SYNCHRONIZE:
			showSynchronizeProgressItems();
			break;
		case CLEAR_UP_INFRASTRUCTURE:
			showClearUpInfrastructureProgressItems(true);
			break;
		case DELETE_OBSOLETE:
			showDeleteObsoleteProgressItems();
			break;
		case PULL_MODIFIED:
			showPullModifiedProgressItems();
			break;
		case REPULL:
			showRepullProgressItems();
			break;
		default:
			throw new IllegalArgumentException(synchronizationStep.name());
		}

		getActivity().runOnUiThread(() -> {
			contentBinding.stepsLayout.setCurrentStep(++currentSyncStepIndex);
			contentBinding.stepsLayout.stepText
				.setText(String.format(getActivity().getResources().getString(synchronizationStep.getCaptionResource()), currentSyncStepIndex));
		});
	}

	/**
	 * Waits until the UI thread has set up the progress item bindings to ensure that they
	 * are present when the synchronization process accesses them.
	 */
	private void waitForProgressItemBindings() {

		if (!updatingProgressItemBindings && currentProgressItemBinding != null) {
			return;
		}

		synchronized (BINDING_LOCK) {
			try {
				if (updatingProgressItemBindings || currentProgressItemBinding == null) {
					BINDING_LOCK.wait();
				}
			} catch (InterruptedException e) {
				logger.error(
					"InterruptedException when trying to perform BINDING_LOCK.wait() in SynchronizationDialog.waitForProgressItemBindings: "
						+ e.getMessage());
			}
		}
	}

	private void updateStepNumber(int stepNumber) {
		getActivity().runOnUiThread(() -> contentBinding.stepsLayout.setStepNumber(stepNumber));
	}

	/**
	 * Activates the next progress item.
	 */
	private void loadNext() {

		waitForProgressItemBindings();

		currentProgressItemBinding.setActive(false);
		currentProgressItemBinding.setDone(true);

		if (progressItemBindings.size() > ++progressItemBindingsIndex) {
			currentProgressItemBinding = progressItemBindings.get(progressItemBindingsIndex);
			currentProgressItemBinding.setActive(true);
		}
	}

	private void updatePulls(int pulled) {
		waitForProgressItemBindings();
		currentProgressItemBinding.setPulls(currentProgressItemBinding.getPulls() + pulled);
	}

	private void updatePushes(int pushed) {
		waitForProgressItemBindings();
		currentProgressItemBinding.setPushes(currentProgressItemBinding.getPushes() + pushed);
	}

	private void updatePushTotal(int pushTotal) {
		waitForProgressItemBindings();
		currentProgressItemBinding.setPushTotal(pushTotal);
	}

	private void updateDeletions(int deleted) {
		waitForProgressItemBindings();
		currentProgressItemBinding.setDeletions(currentProgressItemBinding.getDeletions() + deleted);
	}

	/**
	 * Replaces the current progress items with new ones that match the next step in the
	 * clear up synchronization steps. Since the two clear up steps belong together, they are
	 * not separated by individual synchronization steps. However, the two actions (deletions
	 * and pulls) also can't be displayed at the same time because they are performed in
	 * reverse order.
	 */
	private void showNextClearItems() {

		synchronized (BINDING_LOCK) {
			try {
				if (updatingProgressItemBindings) {
					BINDING_LOCK.wait();
				}
			} catch (InterruptedException e) {
				logger.error(
					"InterruptedException when trying to perform BINDING_LOCK.wait() in SynchronizationDialog.updateSynchronizationStep: "
						+ e.getMessage());
			}
		}

		updatingProgressItemBindings = true;
		currentProgressItemBinding = null;

		if (currentSyncStep == SynchronizationStep.CLEAR_UP_INFRASTRUCTURE) {
			showClearUpInfrastructureProgressItems(false);
		} else if (currentSyncStep == SynchronizationStep.CLEAR_UP) {
			showClearUpProgressItems(false);
		} else {
			throw new UnsupportedOperationException("Current sync step is no clear up step");
		}
	}

	private void showProgressItems(boolean showPulls, boolean showPushes, List<String> captions) {

		showProgressItems(showPulls, showPushes, false, captions);
	}

	private void showProgressItems(boolean showPulls, boolean showPushes, boolean showDeletions, List<String> captions) {

		getActivity().runOnUiThread(() -> {
			assert (currentProgressItemBinding == null);

			progressItemBindings.clear();
			progressItemBindingsIndex = 0;
			contentBinding.syncProgressLayout.removeAllViews();

			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			captions.forEach(caption -> progressItemBindings.add(createBinding(inflater, caption, showPulls, showPushes, showDeletions)));

			synchronized (BINDING_LOCK) {
				currentProgressItemBinding = progressItemBindings.get(0);
				currentProgressItemBinding.setActive(true);
				updatingProgressItemBindings = false;
				BINDING_LOCK.notify();
			}
		});
	}

	private void showPullInfrastructureProgressItems(boolean initialSync) {

		List<String> allowedEntities = new ArrayList<>();
		allowedEntities.add(Strings.entityContinents);
		allowedEntities.add(Strings.entitySubcontinents);
		allowedEntities.add(Strings.entityCountries);
		allowedEntities.add(Strings.entityAreas);
		allowedEntities.add(Strings.entityRegions);
		allowedEntities.add(Strings.entityDistricts);
		allowedEntities.add(Strings.entityCommunities);
		allowedEntities.add(Strings.entityFacilities);
		allowedEntities.add(Strings.entityPointsOfEntry);
		allowedEntities.add(Strings.entityUserRoles);
		allowedEntities.add(Strings.entityUsers);
		allowedEntities.add(Strings.entityDiseaseClassifications);
		allowedEntities.add(Strings.entityDiseaseConfigurations);
		if (initialSync) {
			allowedEntities.add(Strings.entityCustomizableEnumValues);
		}
		allowedEntities.add(Strings.entityFeatureConfigurations);
		showProgressItems(true, false, allowedEntities);
	}

	private void showDeleteObsoleteProgressItems() {

		List<String> allowedEntities = new ArrayList<>();
		addEntityIfViewAllowed(CaseDataDto.class, Strings.entityCases, allowedEntities);
		addEntityIfViewAllowed(ContactDto.class, Strings.entityContacts, allowedEntities);
		addEntityIfViewAllowed(EventDto.class, Strings.entityEvents, allowedEntities);
		addEntityIfViewAllowed(EventParticipantDto.class, Strings.entityEventParticipants, allowedEntities);
		addEntityIfViewAllowed(ImmunizationDto.class, Strings.entityImmunizations, allowedEntities);
		addEntityIfViewAllowed(SampleDto.class, Strings.entitySamples, allowedEntities);
		addEntityIfViewAllowed(EnvironmentDto.class, Strings.entityEnvironments, allowedEntities);
		addEntityIfViewAllowed(EnvironmentSampleDto.class, Strings.entityEnvironmentSamples, allowedEntities);
		addEntityIfViewAllowed(TaskDto.class, Strings.entityTasks, allowedEntities);
		addEntityIfViewAllowed(OutbreakDto.class, Strings.entityOutbreaks, allowedEntities);
		addEntityIfViewAllowed(AggregateReportDto.class, Strings.entityAggregateReports, allowedEntities);
		showProgressItems(true, false, allowedEntities);
	}

	private void showPushNewProgressItems() {

		List<String> allowedEntities = new ArrayList<>();
		addEntityIfEditAllowed(PersonDto.class, Strings.entityPersons, allowedEntities);
		addEntityIfEditAllowed(CaseDataDto.class, Strings.entityCases, allowedEntities);
		addEntityIfEditAllowed(ImmunizationDto.class, Strings.entityImmunizations, allowedEntities);
		addEntityIfEditAllowed(EventDto.class, Strings.entityEvents, allowedEntities);
		addEntityIfEditAllowed(EventParticipantDto.class, Strings.entityEventParticipants, allowedEntities);
		addEntityIfEditAllowed(SampleDto.class, Strings.entitySamples, allowedEntities);
		addEntityIfEditAllowed(PathogenTestDto.class, Strings.entityPathogenTests, allowedEntities);
		addEntityIfEditAllowed(AdditionalTestDto.class, Strings.entityAdditionalTests, allowedEntities);
		addEntityIfEditAllowed(EnvironmentDto.class, Strings.entityEnvironments, allowedEntities);
		addEntityIfEditAllowed(EnvironmentSampleDto.class, Strings.entityEnvironmentSamples, allowedEntities);
		addEntityIfEditAllowed(ContactDto.class, Strings.entityContacts, allowedEntities);
		addEntityIfEditAllowed(VisitDto.class, Strings.entityVisits, allowedEntities);
		addEntityIfEditAllowed(TaskDto.class, Strings.entityTasks, allowedEntities);
		addEntityIfEditAllowed(WeeklyReportDto.class, Strings.entityWeeklyReports, allowedEntities);
		addEntityIfEditAllowed(AggregateReportDto.class, Strings.entityAggregateReports, allowedEntities);
		addEntityIfEditAllowed(PrescriptionDto.class, Strings.entityPrescriptions, allowedEntities);
		addEntityIfEditAllowed(TreatmentDto.class, Strings.entityTreatments, allowedEntities);
		addEntityIfEditAllowed(ClinicalVisitDto.class, Strings.entityClinicalVisits, allowedEntities);
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			addEntityIfEditAllowed(CampaignFormDataDto.class, Strings.entityCampaignFormData, allowedEntities);
		}
		showProgressItems(false, true, allowedEntities);
	}

	private void showRepullProgressItems() {

		List<String> allowedEntities = new ArrayList<>();
		addEntityIfViewAllowed(UserRoleDto.class, Strings.entityUserRoles, allowedEntities);
		addEntityIfViewAllowed(DiseaseClassificationCriteriaDto.class, Strings.entityDiseaseClassifications, allowedEntities);
		addEntityIfViewAllowed(UserDto.class, Strings.entityUsers, allowedEntities);
		addEntityIfViewAllowed(OutbreakDto.class, Strings.entityOutbreaks, allowedEntities);
		addEntityIfViewAllowed(DiseaseConfigurationDto.class, Strings.entityDiseaseConfigurations, allowedEntities);
		addEntityIfViewAllowed(CustomizableEnumValueDto.class, Strings.entityCustomizableEnumValues, allowedEntities);
		addEntityIfViewAllowed(FeatureConfigurationDto.class, Strings.entityFeatureConfigurations, allowedEntities);
		addEntityIfViewAllowed(PersonDto.class, Strings.entityPersons, allowedEntities);
		addEntityIfViewAllowed(CaseDataDto.class, Strings.entityCases, allowedEntities);
		addEntityIfViewAllowed(ImmunizationDto.class, Strings.entityImmunizations, allowedEntities);
		addEntityIfViewAllowed(EventDto.class, Strings.entityEvents, allowedEntities);
		addEntityIfViewAllowed(EventParticipantDto.class, Strings.entityEventParticipants, allowedEntities);
		addEntityIfViewAllowed(EnvironmentDto.class, Strings.entityEnvironments, allowedEntities);
		addEntityIfViewAllowed(EnvironmentSampleDto.class, Strings.entityEnvironmentSamples, allowedEntities);
		addEntityIfViewAllowed(SampleDto.class, Strings.entitySamples, allowedEntities);
		addEntityIfViewAllowed(PathogenTestDto.class, Strings.entityPathogenTests, allowedEntities);
		addEntityIfViewAllowed(AdditionalTestDto.class, Strings.entityAdditionalTests, allowedEntities);
		addEntityIfViewAllowed(ContactDto.class, Strings.entityContacts, allowedEntities);
		addEntityIfViewAllowed(VisitDto.class, Strings.entityVisits, allowedEntities);
		addEntityIfViewAllowed(TaskDto.class, Strings.entityTasks, allowedEntities);
		addEntityIfViewAllowed(WeeklyReportDto.class, Strings.entityWeeklyReports, allowedEntities);
		addEntityIfViewAllowed(AggregateReportDto.class, Strings.entityAggregateReports, allowedEntities);
		addEntityIfViewAllowed(PrescriptionDto.class, Strings.entityPrescriptions, allowedEntities);
		addEntityIfViewAllowed(TreatmentDto.class, Strings.entityTreatments, allowedEntities);
		addEntityIfViewAllowed(ClinicalVisitDto.class, Strings.entityClinicalVisits, allowedEntities);
		showProgressItems(true, false, allowedEntities);
	}

	private void showSynchronizeProgressItems() {

		List<String> allowedEntities = new ArrayList<>();
		addEntityIfViewOrEditAllowed(OutbreakDto.class, Strings.entityOutbreaks, allowedEntities);
		addEntityIfViewOrEditAllowed(DiseaseConfigurationDto.class, Strings.entityDiseaseConfigurations, allowedEntities);
		addEntityIfViewOrEditAllowed(CustomizableEnumValueDto.class, Strings.entityCustomizableEnumValues, allowedEntities);
		addEntityIfViewOrEditAllowed(PersonDto.class, Strings.entityPersons, allowedEntities);
		addEntityIfViewOrEditAllowed(CaseDataDto.class, Strings.entityCases, allowedEntities);
		addEntityIfViewOrEditAllowed(ImmunizationDto.class, Strings.entityImmunizations, allowedEntities);
		addEntityIfViewOrEditAllowed(EventDto.class, Strings.entityEvents, allowedEntities);
		addEntityIfViewOrEditAllowed(EventParticipantDto.class, Strings.entityEventParticipants, allowedEntities);
		addEntityIfViewOrEditAllowed(SampleDto.class, Strings.entitySamples, allowedEntities);
		addEntityIfViewOrEditAllowed(PathogenTestDto.class, Strings.entityPathogenTests, allowedEntities);
		addEntityIfViewOrEditAllowed(AdditionalTestDto.class, Strings.entityAdditionalTests, allowedEntities);
		addEntityIfViewOrEditAllowed(EnvironmentDto.class, Strings.entityEnvironments, allowedEntities);
		addEntityIfViewOrEditAllowed(EnvironmentSampleDto.class, Strings.entityEnvironmentSamples, allowedEntities);
		addEntityIfViewOrEditAllowed(ContactDto.class, Strings.entityContacts, allowedEntities);
		addEntityIfViewOrEditAllowed(VisitDto.class, Strings.entityVisits, allowedEntities);
		addEntityIfViewOrEditAllowed(TaskDto.class, Strings.entityTasks, allowedEntities);
		addEntityIfViewOrEditAllowed(WeeklyReportDto.class, Strings.entityWeeklyReports, allowedEntities);
		addEntityIfViewOrEditAllowed(AggregateReportDto.class, Strings.entityAggregateReports, allowedEntities);
		addEntityIfViewOrEditAllowed(PrescriptionDto.class, Strings.entityPrescriptions, allowedEntities);
		addEntityIfViewOrEditAllowed(TreatmentDto.class, Strings.entityTreatments, allowedEntities);
		addEntityIfViewOrEditAllowed(ClinicalVisitDto.class, Strings.entityClinicalVisits, allowedEntities);
		showProgressItems(true, true, allowedEntities);
	}

	private void showPullModifiedProgressItems() {

		List<String> allowedEntities = new ArrayList<>();
		addEntityIfViewAllowed(PersonDto.class, Strings.entityPersons, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForCaseEnabled())
			addEntityIfViewAllowed(CaseDataDto.class, Strings.entityCases, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForImmunizationEnabled())
			addEntityIfViewAllowed(ImmunizationDto.class, Strings.entityImmunizations, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForEventsEnabled())
			addEntityIfViewAllowed(EventDto.class, Strings.entityEvents, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForEventParticipantsEnabled())
			addEntityIfViewAllowed(EventParticipantDto.class, Strings.entityEventParticipants, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForSampleEnabled())
			addEntityIfViewAllowed(SampleDto.class, Strings.entitySamples, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForSampleTestsEnabled()) {
			addEntityIfViewAllowed(PathogenTestDto.class, Strings.entityPathogenTests, allowedEntities);
		}
		if (DtoFeatureConfigHelper.isFeatureConfigForAdditionalTestsEnabled()) {
			addEntityIfViewAllowed(AdditionalTestDto.class, Strings.entityAdditionalTests, allowedEntities);
		}
		if (DtoFeatureConfigHelper.isFeatureConfigForEnvironmentEnabled()) {
			addEntityIfViewAllowed(EnvironmentDto.class, Strings.entityEnvironments, allowedEntities);
			addEntityIfViewAllowed(EnvironmentSampleDto.class, Strings.entityEnvironmentSamples, allowedEntities);
		}
		if (DtoFeatureConfigHelper.isFeatureConfigForContactsEnabled())
			addEntityIfViewAllowed(ContactDto.class, Strings.entityContacts, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForVisitsEnabled())
			addEntityIfViewAllowed(VisitDto.class, Strings.entityVisits, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForTasksEnabled())
			addEntityIfViewAllowed(TaskDto.class, Strings.entityTasks, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForWeeklyReportsEnabled())
			addEntityIfViewAllowed(WeeklyReportDto.class, Strings.entityWeeklyReports, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForAggregateReportsEnabled())
			addEntityIfViewAllowed(AggregateReportDto.class, Strings.entityAggregateReports, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForPrescriptionsEnabled())
			addEntityIfViewAllowed(PrescriptionDto.class, Strings.entityPrescriptions, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForTreatmentsEnabled())
			addEntityIfViewAllowed(TreatmentDto.class, Strings.entityTreatments, allowedEntities);
		if (DtoFeatureConfigHelper.isFeatureConfigForClinicalVisitsEnabled())
			addEntityIfViewAllowed(ClinicalVisitDto.class, Strings.entityClinicalVisits, allowedEntities);
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			addEntityIfViewAllowed(CampaignFormMetaDto.class, Strings.entityCampaignFormMeta, allowedEntities);
			addEntityIfViewAllowed(CampaignDto.class, Strings.entityCampaigns, allowedEntities);
			addEntityIfViewAllowed(CampaignFormDataDto.class, Strings.entityCampaignFormData, allowedEntities);
		}
		showProgressItems(true, false, allowedEntities);
	}

	private void showClearUpProgressItems(boolean forDeletion) {

		List<String> allowedEntities = new ArrayList<>();
		addEntityIfViewAllowed(PersonDto.class, Strings.entityPersons, allowedEntities);
		addEntityIfViewAllowed(CaseDataDto.class, Strings.entityCases, allowedEntities);
		addEntityIfViewAllowed(ImmunizationDto.class, Strings.entityImmunizations, allowedEntities);
		addEntityIfViewAllowed(EventDto.class, Strings.entityEvents, allowedEntities);
		addEntityIfViewAllowed(EventParticipantDto.class, Strings.entityEventParticipants, allowedEntities);
		addEntityIfViewAllowed(SampleDto.class, Strings.entitySamples, allowedEntities);
		addEntityIfViewAllowed(PathogenTestDto.class, Strings.entityPathogenTests, allowedEntities);
		addEntityIfViewAllowed(AdditionalTestDto.class, Strings.entityAdditionalTests, allowedEntities);
		addEntityIfViewAllowed(EnvironmentDto.class, Strings.entityEnvironments, allowedEntities);
		addEntityIfViewAllowed(EnvironmentSampleDto.class, Strings.entityEnvironmentSamples, allowedEntities);
		addEntityIfViewAllowed(ContactDto.class, Strings.entityContacts, allowedEntities);
		addEntityIfViewAllowed(VisitDto.class, Strings.entityVisits, allowedEntities);
		addEntityIfViewAllowed(TaskDto.class, Strings.entityTasks, allowedEntities);
		addEntityIfViewAllowed(WeeklyReportDto.class, Strings.entityWeeklyReports, allowedEntities);
		addEntityIfViewAllowed(AggregateReportDto.class, Strings.entityAggregateReports, allowedEntities);
		addEntityIfViewAllowed(PrescriptionDto.class, Strings.entityPrescriptions, allowedEntities);
		addEntityIfViewAllowed(TreatmentDto.class, Strings.entityTreatments, allowedEntities);
		addEntityIfViewAllowed(ClinicalVisitDto.class, Strings.entityClinicalVisits, allowedEntities);
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			addEntityIfViewAllowed(CampaignFormMetaDto.class, Strings.entityCampaignFormMeta, allowedEntities);
			addEntityIfViewAllowed(CampaignDto.class, Strings.entityCampaigns, allowedEntities);
			addEntityIfViewAllowed(CampaignFormDataDto.class, Strings.entityCampaignFormData, allowedEntities);
		}

		if (forDeletion) {
			Collections.reverse(allowedEntities);
			addEntityIfViewAllowed(OutbreakDto.class, Strings.entityOutbreaks, allowedEntities);
		}

		showProgressItems(!forDeletion, false, forDeletion, allowedEntities);
	}

	private void showClearUpInfrastructureProgressItems(boolean forDeletion) {

		List<String> allowedEntities = Arrays.asList(
			Strings.entityContinents,
			Strings.entitySubcontinents,
			Strings.entityCountries,
			Strings.entityAreas,
			Strings.entityRegions,
			Strings.entityDistricts,
			Strings.entityCommunities,
			Strings.entityFacilities,
			Strings.entityPointsOfEntry,
			Strings.entityUserRoles,
			Strings.entityUsers,
			Strings.entityDiseaseConfigurations,
			Strings.entityCustomizableEnumValues,
			Strings.entityFeatureConfigurations);

		if (forDeletion) {
			Collections.reverse(allowedEntities);
		}

		showProgressItems(!forDeletion, false, forDeletion, allowedEntities);
	}

	private void addEntityIfViewAllowed(Class<? extends EntityDto> dtoClass, String entityString, List<String> list) {

		if (DtoUserRightsHelper.isViewAllowed(dtoClass)) {
			list.add(entityString);
		}
	}

	private void addEntityIfEditAllowed(Class<? extends EntityDto> dtoClass, String entityString, List<String> list) {

		if (DtoUserRightsHelper.isEditAllowed(dtoClass)) {
			list.add(entityString);
		}
	}

	private void addEntityIfViewOrEditAllowed(Class<? extends EntityDto> dtoClass, String entityString, List<String> list) {

		if (DtoUserRightsHelper.isViewAllowed(dtoClass) || DtoUserRightsHelper.isEditAllowed(dtoClass)) {
			list.add(entityString);
		}
	}

	private DialogSynchronizationProgressItemLayoutBinding createBinding(
		LayoutInflater inflater,
		String i18nProperty,
		boolean showPulls,
		boolean showPushes,
		boolean showDeletions) {

		DialogSynchronizationProgressItemLayoutBinding binding =
			DataBindingUtil.inflate(inflater, R.layout.dialog_synchronization_progress_item_layout, contentBinding.syncProgressLayout, true);
		binding.setName(I18nProperties.getString(i18nProperty));
		binding.setShowPulls(showPulls);
		binding.setShowPushes(showPushes);
		binding.setShowDeletions(showDeletions);
		binding.setPushes(0);
		binding.setPushTotal(0);
		binding.setPulls(0);
		binding.setDeletions(0);
		return binding;
	}

	public SynchronizationCallbacks getSyncCallbacks() {
		return syncCallbacks;
	}

	@Override
	public boolean isButtonPanelVisible() {
		return false;
	}

	public static class SynchronizationCallbacks {

		private final Consumer<Integer> updateStepNumberCallback;
		private final Consumer<SynchronizationStep> updateSynchronizationStepCallback;
		private final Runnable loadNextCallback;
		private final Consumer<Integer> updatePullsCallback;
		private final Consumer<Integer> updatePushesCallback;
		private final Consumer<Integer> updateDeletionsCallback;
		private final Consumer<Integer> updatePushTotalCallback;
		private final Runnable showDialogCallback;
		private final Runnable showNextCleanupItemsCallback;

		public SynchronizationCallbacks(
			Consumer<Integer> updateStepNumberCallback,
			Consumer<SynchronizationStep> updateSynchronizationStepCallback,
			Runnable loadNextCallback,
			Consumer<Integer> updatePullsCallback,
			Consumer<Integer> updatePushesCallback,
			Consumer<Integer> updateDeletionsCallback,
			Consumer<Integer> updatePushTotalCallback,
			Runnable showDialogCallback,
			Runnable showNextCleanupItemsCallback) {

			this.updateStepNumberCallback = updateStepNumberCallback;
			this.updateSynchronizationStepCallback = updateSynchronizationStepCallback;
			this.loadNextCallback = loadNextCallback;
			this.updatePullsCallback = updatePullsCallback;
			this.updatePushesCallback = updatePushesCallback;
			this.updateDeletionsCallback = updateDeletionsCallback;
			this.updatePushTotalCallback = updatePushTotalCallback;
			this.showDialogCallback = showDialogCallback;
			this.showNextCleanupItemsCallback = showNextCleanupItemsCallback;
		}

		public Consumer<Integer> getUpdateStepNumberCallback() {
			return updateStepNumberCallback;
		}

		public Consumer<SynchronizationStep> getUpdateSynchronizationStepCallback() {
			return updateSynchronizationStepCallback;
		}

		public Runnable getLoadNextCallback() {
			return loadNextCallback;
		}

		public Consumer<Integer> getUpdatePullsCallback() {
			return updatePullsCallback;
		}

		public Consumer<Integer> getUpdatePushesCallback() {
			return updatePushesCallback;
		}

		public Consumer<Integer> getUpdateDeletionsCallback() {
			return updateDeletionsCallback;
		}

		public Consumer<Integer> getUpdatePushTotalCallback() {
			return updatePushTotalCallback;
		}

		public Runnable getShowDialogCallback() {
			return showDialogCallback;
		}

		public Runnable getShowNextCleanupItemsCallback() {
			return showNextCleanupItemsCallback;
		}
	}

	public enum SynchronizationStep {

		PUSH_NEW(R.string.caption_sync_send_new),
		PULL_INFRASTRUCTURE(R.string.caption_sync_pull_infrastructure),
		PULL_INFRASTRUCTURE_INITIAL(R.string.caption_sync_pull_infrastructure),
		CLEAR_UP_INFRASTRUCTURE(R.string.caption_sync_clear_up_infrastructure),
		DELETE_OBSOLETE(R.string.caption_sync_delete_obsolete),
		CLEAR_UP(R.string.caption_sync_clear_up),
		SYNCHRONIZE(R.string.caption_sync_synchronize),
		PULL_MODIFIED(R.string.caption_sync_pull_modified),
		REPULL(R.string.caption_sync_repull);

		private final int captionResource;

		SynchronizationStep(int captionResource) {
			this.captionResource = captionResource;
		}

		public int getCaptionResource() {
			return captionResource;
		}
	}

}
