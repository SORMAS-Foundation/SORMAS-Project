package de.symeda.sormas.app.report.aggregate;

import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.report.DiseaseAgeGroup;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.InfrastructureAdo;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.report.AggregateReport;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentReportsAggregateLayoutBinding;
import de.symeda.sormas.app.databinding.RowReportAggregateAgegroupLayoutBinding;
import de.symeda.sormas.app.databinding.RowReportAggregateDiseaseLayoutBinding;
import de.symeda.sormas.app.databinding.RowReportAggregateLayoutBinding;
import de.symeda.sormas.app.report.EpiWeekFilterOption;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DateFormatHelper;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class AggregateReportsFragment extends BaseReportFragment<FragmentReportsAggregateLayoutBinding> {

	private EpiWeek lastUpdateEpiWeek;

	private AsyncTask confirmCaseNumbersTask;
	private FragmentReportsAggregateLayoutBinding contentBinding;

	private List<AggregateReport> reports = new ArrayList<>();
	private List<AggregateReport> userReports = new ArrayList<>();

	public static AggregateReportsFragment newInstance() {
		return newInstance(AggregateReportsFragment.class, null);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		// Not required
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.hint_case_numbers_not_submitted);
	}

	@Override
	protected void onLayoutBinding(final FragmentReportsAggregateLayoutBinding contentBinding) {
		this.contentBinding = contentBinding;

		contentBinding.setReportFilterOptionClass(EpiWeekFilterOption.class);
		setupControls();
	}

	@Override
	protected void onAfterLayoutBinding(FragmentReportsAggregateLayoutBinding contentBinding) {
		super.onAfterLayoutBinding(contentBinding);
		contentBinding.reportSelector.setValue(EpiWeekFilterOption.THIS_WEEK);
	}

	private void setupControls() {
		EpiWeek previousEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		EpiWeek currentEpiWeek = DateHelper.getEpiWeek(new Date());
		List<Integer> yearList = DateHelper.getYearsToNow();
		if (currentEpiWeek.getYear() > previousEpiWeek.getYear()) {
			yearList.add(currentEpiWeek.getYear());
		}

		contentBinding.aggregateReportsYear.initializeSpinner(DataUtils.toItems(yearList), previousEpiWeek.getYear(), field -> {
			Integer year = (Integer) field.getValue();
			if (year != null) {
				if (year.equals(currentEpiWeek.getYear())) {
					contentBinding.aggregateReportsWeek
						.setSpinnerData(DataUtils.toItems(DateHelper.createEpiWeekListFromInterval(new EpiWeek(year, 1), currentEpiWeek)));
				} else {
					contentBinding.aggregateReportsWeek.setSpinnerData(DataUtils.toItems(DateHelper.createEpiWeekList(year)));
				}

			} else {
				contentBinding.aggregateReportsWeek.setSpinnerData(null);
			}
		});

		contentBinding.aggregateReportsWeek
			.initializeSpinner(DataUtils.toItems(DateHelper.createEpiWeekList(previousEpiWeek.getYear())), previousEpiWeek, field -> {
				updateByEpiWeek();
			});

		contentBinding.reportSelector.addValueChangedListener(field -> {
			EpiWeekFilterOption filter = (EpiWeekFilterOption) field.getValue();
			if (filter != null) {
				switch (filter) {
				case LAST_WEEK:
					setEpiWeek(DateHelper.getPreviousEpiWeek(new Date()));
					contentBinding.aggregateReportsYear.setEnabled(false);
					contentBinding.aggregateReportsWeek.setEnabled(false);
					break;
				case THIS_WEEK:
					setEpiWeek(DateHelper.getEpiWeek(new Date()));
					contentBinding.aggregateReportsYear.setEnabled(false);
					contentBinding.aggregateReportsWeek.setEnabled(false);
					break;
				case SPECIFY_WEEK:
					contentBinding.aggregateReportsYear.setEnabled(true);
					contentBinding.aggregateReportsWeek.setEnabled(true);
					break;
				default:
					throw new IllegalArgumentException(filter.toString());
				}
			}
		});

		contentBinding.aggregateReportsReport.initializeSpinner(new ArrayList<>(), null, field -> {
			showReportData();
		});

		contentBinding.submitReport.setOnClickListener(view -> showSubmitCaseNumbersConfirmationDialog());
	}

	public void setEpiWeek(EpiWeek epiWeek) {
		if (!DataHelper.equal(contentBinding.aggregateReportsYear.getValue(), epiWeek.getYear())) {
			contentBinding.aggregateReportsYear.setValue(epiWeek.getYear());
		}
		if (!DataHelper.equal(contentBinding.aggregateReportsWeek.getValue(), epiWeek)) {
			contentBinding.aggregateReportsWeek.setValue(epiWeek);
		}
	}

	private void updateByEpiWeek() {

		EpiWeek epiWeek = (EpiWeek) contentBinding.aggregateReportsWeek.getValue();

		if (DataHelper.equal(epiWeek, lastUpdateEpiWeek)) {
			return;
		}
		lastUpdateEpiWeek = epiWeek;

		EpiWeekFilterOption filterOption;
		if (epiWeek == null) {
			filterOption = EpiWeekFilterOption.SPECIFY_WEEK;
		} else if (epiWeek.equals(DateHelper.getPreviousEpiWeek(new Date()))) {
			filterOption = EpiWeekFilterOption.LAST_WEEK;
		} else if (epiWeek.equals(DateHelper.getEpiWeek(new Date()))) {
			filterOption = EpiWeekFilterOption.THIS_WEEK;
		} else {
			filterOption = EpiWeekFilterOption.SPECIFY_WEEK;
		}
		if (!DataHelper.equal(contentBinding.reportSelector.getValue(), filterOption)) {
			contentBinding.reportSelector.setValue(filterOption);
		}

		fillReportsDropdown();
	}

	public void refreshAggregateReports() {
		fillReportsDropdown();
		showReportData();
	}

	private void fillReportsDropdown() {

		if (contentBinding.aggregateReportsWeek.getValue() == null) {
			contentBinding.reportContent.removeAllViews();
			contentBinding.submitReport.setEnabled(false);
			contentBinding.aggregateReportsReport.setSpinnerData(new ArrayList<>());
			return;
		}

		reports = DatabaseHelper.getAggregateReportDao().getReportsByEpiWeek((EpiWeek) contentBinding.aggregateReportsWeek.getValue());

		List<ReportUserInfo> reportUserInfo = reports.stream()
			.map(r -> new ReportUserInfo(r.getReportingUser(), r.getDistrict(), r.getHealthFacility(), r.getPointOfEntry()))
			.distinct()
			.collect(Collectors.toList());

		addJurisdictionsWithoutReports(reportUserInfo);
		sortReportUserInfo(reportUserInfo);

		contentBinding.aggregateReportsReport
			.setSpinnerData(reportUserInfo.stream().map(r -> new Item<>(r.getCaption(), r)).collect(Collectors.toList()));
	}

	private void showReportData() {

		contentBinding.reportContent.removeAllViews();

		if (contentBinding.aggregateReportsWeek.getValue() == null || contentBinding.aggregateReportsReport.getValue() == null) {
			contentBinding.submitReport.setEnabled(false);
			return;
		}

		Date latestLocalChangeDate = null;
		User user = ConfigProvider.getUser();
		boolean triggerDuplicateWarning = false;
		boolean triggerExpiredWarning = false;

		final Map<Disease, List<AggregateReport>> reportsByDisease = new HashMap<>();
		final EpiWeek epiWeek = (EpiWeek) contentBinding.aggregateReportsWeek.getValue();
		final User selectedUser = ((ReportUserInfo) contentBinding.aggregateReportsReport.getValue()).user;

		final InfrastructureAdo selectedInfrastructure = ((ReportUserInfo) contentBinding.aggregateReportsReport.getValue()).getInfrastructure();
		final boolean enabled = selectedUser == null || user.equals(selectedUser);
		userReports = reports.stream()
			.filter(r -> r.getReportingUser().equals(selectedUser) && isSameInfrastructure(r, selectedInfrastructure))
			.collect(Collectors.toList());
		DatabaseHelper.getAggregateReportDao().sortAggregateReports(userReports);

		List<Disease> diseaseList = DiseaseConfigurationCache.getInstance().getAllDiseases(true, null, false, true);
		List<Disease> diseasesWithoutReport = new ArrayList<>(diseaseList);
		List<DiseaseAgeGroup> diseaseAgeGroupsWithoutReport = new ArrayList<>();
		diseaseList.forEach(disease -> {
			List<String> ageGroups = DatabaseHelper.getDiseaseConfigurationDao().getDiseaseConfiguration(disease).getAgeGroups();
			if (ageGroups != null) {
				ageGroups.forEach(ageGroup -> {
					diseaseAgeGroupsWithoutReport.add(new DiseaseAgeGroup(disease, ageGroup));
				});
			} else {
				diseaseAgeGroupsWithoutReport.add(new DiseaseAgeGroup(disease, null));
			}
		});

		for (AggregateReport report : userReports) {
			Disease disease = report.getDisease();
			if (reportsByDisease.containsKey(disease)) {
				List<AggregateReport> aggregateReports = reportsByDisease.get(disease);
				aggregateReports.add(report);
			} else {
				ArrayList<AggregateReport> aggregateReports = new ArrayList<>();
				aggregateReports.add(report);
				reportsByDisease.put(disease, aggregateReports);
			}
			diseasesWithoutReport.remove(disease);
			diseaseAgeGroupsWithoutReport.remove(new DiseaseAgeGroup(disease, report.getAgeGroup()));
		}

		for (Map.Entry<Disease, List<AggregateReport>> entry : reportsByDisease.entrySet()) {
			Disease disease = entry.getKey();
			List<AggregateReport> aggregateReports = entry.getValue();
			List<String> diseaseAgeGroups =
				aggregateReports.stream().map(aggregateReport -> aggregateReport.getAgeGroup()).collect(Collectors.toList());

			List<DiseaseAgeGroup> noDataAgeGroups = diseaseAgeGroupsWithoutReport.stream()
				.filter(diseaseAgeGroup -> diseaseAgeGroup.getDisease().equals(disease) && !diseaseAgeGroups.contains(diseaseAgeGroup.getAgeGroup()))
				.collect(Collectors.toList());

			if (!noDataAgeGroups.isEmpty()) {
				noDataAgeGroups.forEach(diseaseAgeGroup -> {
					AggregateReport aggregateReport =
						DatabaseHelper.getAggregateReportDao().build(diseaseAgeGroup.getDisease(), epiWeek, selectedInfrastructure);
					aggregateReport.setAgeGroup(diseaseAgeGroup.getAgeGroup());
					userReports.add(aggregateReport);
					aggregateReports.add(aggregateReport);
				});

				DatabaseHelper.getAggregateReportDao().sortAggregateReports(aggregateReports);
			}

			if (aggregateReports.size() == 1) {
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateLayoutBinding binding =
					DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_layout, contentBinding.reportContent, true);
				binding.aggregateReportDeaths.setEnabled(enabled);
				binding.aggregateReportLabConfirmations.setEnabled(enabled);
				binding.aggregateReportNewCases.setEnabled(enabled);
				contentBinding.submitReport.setEnabled(enabled);
				AggregateReport report = aggregateReports.get(0);
				binding.setData(report);
				if (isDuplicateByDiseaseEpiWeekAndInfrastructure(disease, epiWeek, selectedInfrastructure, selectedUser)) {
					binding.diseaseName.setTextColor(getResources().getColor(R.color.red));
					triggerDuplicateWarning = true;
				}
				if (latestLocalChangeDate == null
					|| (report.getLocalChangeDate() != null && latestLocalChangeDate.before(report.getLocalChangeDate()))) {
					latestLocalChangeDate = report.getLocalChangeDate();
				}
			} else {
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateDiseaseLayoutBinding diseaseBinding =
					DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_disease_layout, contentBinding.reportContent, true);
				diseaseBinding.setDisease(disease.toString());

				if (isDuplicateByDiseaseEpiWeekAndInfrastructure(disease, epiWeek, selectedInfrastructure, selectedUser)) {
					diseaseBinding.diseaseName.setTextColor(getResources().getColor(R.color.red));
					triggerDuplicateWarning = true;
				}

				for (AggregateReport report : aggregateReports) {
					RowReportAggregateAgegroupLayoutBinding binding =
						DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_agegroup_layout, contentBinding.reportContent, true);
					binding.aggregateReportDeaths.setEnabled(enabled);
					binding.aggregateReportLabConfirmations.setEnabled(enabled);
					binding.aggregateReportNewCases.setEnabled(enabled);
					if (!DatabaseHelper.getAggregateReportDao().isCurrentAgeGroup(disease, report.getAgeGroup())) {
						binding.expired.setText(I18nProperties.getCaption(Captions.aggregateReportExpiredAgeGroups));
						if (report.getAgeGroup() == null) {
							report.setAgeGroup(I18nProperties.getCaption(Captions.aggregateReportNoAgeGroup));
						}
						triggerExpiredWarning = true;
					}
					contentBinding.submitReport.setEnabled(enabled);
					String ageGroup = report.getAgeGroup();
					if (ageGroup != null) {
						report.setAgeGroup(ageGroup);
					}
					binding.setData(report);
					if (latestLocalChangeDate == null
						|| (report.getLocalChangeDate() != null && latestLocalChangeDate.before(report.getLocalChangeDate()))) {
						latestLocalChangeDate = report.getLocalChangeDate();
					}
				}
			}
		}

		for (Disease disease : diseasesWithoutReport) {
			List<String> ageGroups = DatabaseHelper.getDiseaseConfigurationDao().getDiseaseConfiguration(disease).getAgeGroups();
			if (ageGroups == null || ageGroups.isEmpty()) {
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateLayoutBinding binding =
					DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_layout, contentBinding.reportContent, true);
				binding.aggregateReportDeaths.setEnabled(enabled);
				binding.aggregateReportLabConfirmations.setEnabled(enabled);
				binding.aggregateReportNewCases.setEnabled(enabled);
				contentBinding.submitReport.setEnabled(enabled);
				AggregateReport data = DatabaseHelper.getAggregateReportDao().build(disease, epiWeek, selectedInfrastructure);
				binding.setData(data);
				if (isDuplicateByDiseaseEpiWeekAndInfrastructure(disease, epiWeek, selectedInfrastructure, selectedUser)) {
					binding.diseaseName.setTextColor(getResources().getColor(R.color.red));
					triggerDuplicateWarning = true;
				}
				userReports.add(data);
			} else {
				LayoutInflater diseaseInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateDiseaseLayoutBinding viewBinding =
					DataBindingUtil.inflate(diseaseInflater, R.layout.row_report_aggregate_disease_layout, contentBinding.reportContent, true);
				viewBinding.setDisease(disease.toString());

				if (isDuplicateByDiseaseEpiWeekAndInfrastructure(disease, epiWeek, selectedInfrastructure, selectedUser)) {
					viewBinding.diseaseName.setTextColor(getResources().getColor(R.color.red));
					triggerDuplicateWarning = true;
				}

				for (String ageGroup : ageGroups) {
					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					RowReportAggregateAgegroupLayoutBinding binding =
						DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_agegroup_layout, contentBinding.reportContent, true);
					binding.aggregateReportDeaths.setEnabled(enabled);
					binding.aggregateReportLabConfirmations.setEnabled(enabled);
					binding.aggregateReportNewCases.setEnabled(enabled);
					if (!DatabaseHelper.getAggregateReportDao().isCurrentAgeGroup(disease, ageGroup)) {
						binding.expired.setText(I18nProperties.getCaption(Captions.aggregateReportExpiredAgeGroups));
						triggerExpiredWarning = true;
					}
					contentBinding.submitReport.setEnabled(enabled);
					AggregateReport data = DatabaseHelper.getAggregateReportDao().build(disease, epiWeek, selectedInfrastructure);
					data.setAgeGroup(ageGroup);
					binding.setData(data);
					userReports.add(data);
				}
			}
		}

		Resources r = getResources();
		if (latestLocalChangeDate == null) {
			getSubHeadingHandler().updateSubHeadingTitle(r.getString(R.string.hint_case_numbers_not_submitted));
		} else {
			getSubHeadingHandler().updateSubHeadingTitle(
				String.format(r.getString(R.string.caption_latest_submission), DateFormatHelper.formatLocalDateTime(latestLocalChangeDate)));
		}

		if (triggerDuplicateWarning) {
			NotificationHelper.showNotification((NotificationContext) getActivity(), WARNING, getString(R.string.message_aggregate_report_found));
		}

		if (triggerExpiredWarning) {
			if (triggerDuplicateWarning) {
				NotificationHelper.showNotification(
					(NotificationContext) getActivity(),
					WARNING,
					getString(R.string.message_aggregate_report_found) + "<br>" + getString(R.string.message_aggregate_report_expired_age_groups));
			} else {
				NotificationHelper
					.showNotification((NotificationContext) getActivity(), WARNING, getString(R.string.message_aggregate_report_expired_age_groups));
			}
		}
	}

	private boolean isDuplicateByDiseaseEpiWeekAndInfrastructure(
		Disease disease,
		EpiWeek epiWeek,
		InfrastructureAdo selectedInfrastructure,
		User selectedUser) {

		List<AggregateReport> duplicates = reports.stream()
			.filter(
				aggregateReport -> aggregateReport.getDisease().equals(disease)
					&& aggregateReport.getYear().equals(epiWeek.getYear())
					&& aggregateReport.getEpiWeek().equals(epiWeek.getWeek())
					&& isSameInfrastructure(aggregateReport, selectedInfrastructure))
			.collect(Collectors.toList());

		return duplicates.size() > 1 || (duplicates.size() == 1 && !duplicates.get(0).getReportingUser().equals(selectedUser));

	}

	private void showSubmitCaseNumbersConfirmationDialog() {
		final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
			getActivity(),
			R.string.heading_confirmation_dialog,
			R.string.info_add_cases_before_report_submit,
			R.string.action_submit_case_numbers,
			R.string.action_cancel);

		confirmationDialog.setPositiveCallback(() -> {
			confirmCaseNumbersTask = new DefaultAsyncTask(getContext()) {

				@Override
				public void onPreExecute() {
					getBaseActivity().showPreloader();
				}

				@Override
				public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
					for (AggregateReport report : userReports) {
						// Don't save if a generated report has no case numbers
						if (report.getLocalChangeDate() == null
							&& (report.getNewCases() == null || report.getNewCases() == 0)
							&& (report.getLabConfirmations() == null || report.getLabConfirmations() == 0)
							&& (report.getDeaths() == null || report.getDeaths() == 0)) {
							continue;
						}

						if (report.getNewCases() == null) {
							report.setNewCases(0);
						}
						if (report.getLabConfirmations() == null) {
							report.setLabConfirmations(0);
						}
						if (report.getDeaths() == null) {
							report.setDeaths(0);
						}

						if (report.getAgeGroup() != null
							&& report.getAgeGroup().equals(I18nProperties.getCaption(Captions.aggregateReportNoAgeGroup))) {
							report.setAgeGroup(null);
						}

						DatabaseHelper.getAggregateReportDao().saveAndSnapshot(report);
					}
				}

				@Override
				protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
					super.onPostExecute(taskResult);
					getBaseActivity().hidePreloader();
					Intent intent = new Intent(getContext(), AggregateReportsActivity.class);
					getContext().startActivity(intent);

					if (!taskResult.getResultStatus().isSuccess()) {
						NotificationHelper
							.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, taskResult.getResultStatus().getMessage());
						return;
					}

					NotificationHelper
						.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, R.string.message_case_numbers_submitted);
				}
			}.executeOnThreadPool();
		});

		confirmationDialog.show();
	}

	private void sortReportUserInfo(List<ReportUserInfo> reportUserInfo) {

		reportUserInfo.sort((r1, r2) -> {
			if (r1.isCurrentUserReport()) {
				return -1;
			} else if (r2.isCurrentUserReport()) {
				return 1;
			} else if (r1.district != null && (r1.user == null || r2.district == null)) {
				return -1;
			} else if (r2.district != null && (r2.user == null || r1.district == null)) {
				return 1;
			} else if (r1.district != null) {
				return r1.user.getUserName().compareTo(r2.user.getUserName());
			} else if (r1.facility != null && (r1.user == null || r2.facility == null)) {
				return -1;
			} else if (r2.facility != null && (r2.user == null || r1.facility == null)) {
				return 1;
			} else if (r1.facility != null) {
				return r1.user.getUserName().compareTo(r2.user.getUserName());
			} else if (r1.pointOfEntry != null && (r1.user == null || r2.pointOfEntry == null)) {
				return -1;
			} else if (r2.pointOfEntry != null && (r2.user == null || r1.pointOfEntry == null)) {
				return 1;
			} else if (r1.pointOfEntry != null) {
				return r1.user.getUserName().compareTo(r2.user.getUserName());
			} else {
				return 0;
			}
		});
	}

	private void addJurisdictionsWithoutReports(List<ReportUserInfo> reportUserInfoList) {

		User user = ConfigProvider.getUser();
		JurisdictionLevel jurisdictionLevel = user.getJurisdictionLevel();

		if (jurisdictionLevel == JurisdictionLevel.DISTRICT) {
			if (reports.stream()
				.noneMatch(
					r -> user.getDistrict().equals(r.getDistrict())
						&& r.getHealthFacility() == null
						&& r.getPointOfEntry() == null
						&& r.getReportingUser().equals(user))) {
				reportUserInfoList.add(new ReportUserInfo(user, user.getDistrict(), user.getHealthFacility(), user.getPointOfEntry()));
			}

			DatabaseHelper.getFacilityDao()
				.getActiveHealthFacilitiesByDistrictAndType(user.getDistrict(), FacilityType.HOSPITAL, false, false)
				.stream()
				.filter(f -> reports.stream().noneMatch(r -> f.equals(r.getHealthFacility())))
				.collect(Collectors.toList())
				.forEach(f -> {
					reportUserInfoList.add(new ReportUserInfo(null, null, f, null));
				});

			DatabaseHelper.getPointOfEntryDao()
				.getActiveByDistrict(user.getDistrict(), false)
				.stream()
				.filter(p -> reports.stream().noneMatch(r -> p.equals(r.getPointOfEntry())))
				.collect(Collectors.toList())
				.forEach(p -> {
					reportUserInfoList.add(new ReportUserInfo(null, null, null, p));
				});
		} else if (jurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY
			&& reports.stream().noneMatch(r -> user.getHealthFacility().equals(r.getHealthFacility()) && r.getReportingUser().equals(user))) {
			reportUserInfoList.add(new ReportUserInfo(user, user.getDistrict(), user.getHealthFacility(), user.getPointOfEntry()));
		} else if (jurisdictionLevel == JurisdictionLevel.POINT_OF_ENTRY
			&& reports.stream().noneMatch(r -> user.getPointOfEntry().equals(r.getPointOfEntry()) && r.getReportingUser().equals(user))) {
			reportUserInfoList.add(new ReportUserInfo(user, user.getDistrict(), user.getHealthFacility(), user.getPointOfEntry()));
		}
	}

	private boolean isSameInfrastructure(AggregateReport report, InfrastructureAdo infrastructure) {

		return infrastructure instanceof Facility
			? report.getHealthFacility() != null && infrastructure.getUuid().equals(report.getHealthFacility().getUuid())
			: infrastructure instanceof PointOfEntry
				? report.getPointOfEntry() != null && infrastructure.getUuid().equals(report.getPointOfEntry().getUuid())
				: report.getHealthFacility() == null
					&& report.getPointOfEntry() == null
					&& infrastructure.getUuid().equals(report.getDistrict().getUuid());
	}

	@Override
	protected int getReportLayout() {
		return R.layout.fragment_reports_aggregate_layout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (confirmCaseNumbersTask != null && !confirmCaseNumbersTask.isCancelled()) {
			confirmCaseNumbersTask.cancel(true);
		}
	}

	private static class ReportUserInfo {

		public User user;
		public District district;
		public Facility facility;
		public PointOfEntry pointOfEntry;

		public ReportUserInfo(User user, District district, Facility facility, PointOfEntry pointOfEntry) {

			this.user = user;
			this.district = district;
			this.facility = facility;
			this.pointOfEntry = pointOfEntry;
		}

		public String getCaption() {

			return getInfrastructure().buildCaption() + (user != null ? " - " + user.getUserName() : "");
		}

		public boolean isCurrentUserReport() {

			User currentUser = ConfigProvider.getUser();
			return currentUser.equals(user) && (facility != null && facility.equals(currentUser.getHealthFacility()))
				|| (pointOfEntry != null && pointOfEntry.equals(currentUser.getPointOfEntry()))
				|| (facility == null && pointOfEntry == null && district != null && district.equals(currentUser.getDistrict()));
		}

		public InfrastructureAdo getInfrastructure() {

			return facility != null ? facility : pointOfEntry != null ? pointOfEntry : district;
		}

		@Override
		public boolean equals(Object o) {

			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			ReportUserInfo that = (ReportUserInfo) o;
			return Objects.equals(user, that.user)
				&& Objects.equals(district, that.district)
				&& Objects.equals(facility, that.facility)
				&& Objects.equals(pointOfEntry, that.pointOfEntry);
		}

		@Override
		public int hashCode() {

			return Objects.hash(user, district, facility, pointOfEntry);
		}
	}
}
