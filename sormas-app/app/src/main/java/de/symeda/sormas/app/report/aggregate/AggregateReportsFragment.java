package de.symeda.sormas.app.report.aggregate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ViewDataBinding;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.disease.AgeGroupUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.report.AggregateReport;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentReportsAggregateLayoutBinding;
import de.symeda.sormas.app.databinding.RowReportAggregateAgegroupLayoutBinding;
import de.symeda.sormas.app.databinding.RowReportAggregateAgegroupLayoutBindingImpl;
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
		EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());

		contentBinding.aggregateReportsYear.initializeSpinner(DataUtils.toItems(DateHelper.getYearsToNow()), epiWeek.getYear(), field -> {
			Integer year = (Integer) field.getValue();
			if (year != null) {
				contentBinding.aggregateReportsWeek.setSpinnerData(DataUtils.toItems(DateHelper.createEpiWeekList(year)));
			} else {
				contentBinding.aggregateReportsWeek.setSpinnerData(null);
			}
		});

		contentBinding.aggregateReportsWeek.initializeSpinner(DataUtils.toItems(DateHelper.createEpiWeekList(epiWeek.getYear())), epiWeek, field -> {
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

		if (epiWeek != null) {
			showReportData();
		}
	}

	private void showReportData() {

		contentBinding.reportContent.removeAllViews();

		Date latestLocalChangeDate = null;

		final Map<Disease, List<AggregateReport>> reportsByDisease = new HashMap<>();
		final EpiWeek epiWeek = (EpiWeek) contentBinding.aggregateReportsWeek.getValue();
		final User user = ConfigProvider.getUser();
		reports = DatabaseHelper.getAggregateReportDao().getReportsByEpiWeekAndUser(epiWeek, user);

		List<Disease> diseaseList = DiseaseConfigurationCache.getInstance().getAllDiseases(true, false, false);
		Map<String, Disease> diseaseMap = diseaseList.stream().collect(Collectors.toMap(Disease::toString, disease -> disease));
		Map<String, Disease> diseasesWithoutReport = new HashMap<>(diseaseMap);

		for (AggregateReport report : reports) {

			Disease disease = report.getDisease();
			if (reportsByDisease.containsKey(disease)) {
				List<AggregateReport> aggregateReports = reportsByDisease.get(disease);
				aggregateReports.add(report);
			} else {
				ArrayList<AggregateReport> aggregateReports = new ArrayList<>();
				aggregateReports.add(report);
				reportsByDisease.put(disease, aggregateReports);
			}
			diseasesWithoutReport.remove(disease.toString());
		}

		for (Map.Entry<Disease, List<AggregateReport>> entry : reportsByDisease.entrySet()) {
			Disease key = entry.getKey();
			List<AggregateReport> aggregateReports = entry.getValue();
			if (aggregateReports.size() == 1) {
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateLayoutBinding binding =
						DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_layout, contentBinding.reportContent, true);
				AggregateReport report = aggregateReports.get(0);
				binding.setData(report);
				if (latestLocalChangeDate == null
					|| (report.getLocalChangeDate() != null && latestLocalChangeDate.before(report.getLocalChangeDate()))) {
					latestLocalChangeDate = report.getLocalChangeDate();
				}
			} else {
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateDiseaseLayoutBinding diseaseBinding = DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_disease_layout, contentBinding.reportContent, true);
				diseaseBinding.setDisease(key.toString());
				for (AggregateReport report : aggregateReports) {
					RowReportAggregateAgegroupLayoutBinding binding =
							DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_agegroup_layout, contentBinding.reportContent, true);
					binding.setData(report);
					String ageGroup = report.getAgeGroup();
					if (ageGroup != null) {
						binding.setAgeGroup(AgeGroupUtils.createCaption(ageGroup));
					}
					if (latestLocalChangeDate == null
							|| (report.getLocalChangeDate() != null && latestLocalChangeDate.before(report.getLocalChangeDate()))) {
						latestLocalChangeDate = report.getLocalChangeDate();
					}
				}
			}
		}

		for (String disease : diseasesWithoutReport.keySet()) {

			Disease diseaseEnum = diseasesWithoutReport.get(disease);
			List<String> ageGroups = DatabaseHelper.getDiseaseConfigurationDao().getDiseaseConfiguration(diseaseEnum).getAgeGroups();
			if (ageGroups == null || ageGroups.isEmpty()) {
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateLayoutBinding binding =
					DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_layout, contentBinding.reportContent, true);
				AggregateReport data = new AggregateReport();
				data.setDisease(diseaseEnum);
				binding.setData(data);
			} else {
				LayoutInflater diseaseInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RowReportAggregateDiseaseLayoutBinding viewBinding =
					DataBindingUtil.inflate(diseaseInflater, R.layout.row_report_aggregate_disease_layout, contentBinding.reportContent, true);
				viewBinding.setDisease(disease);
				for (String ageGroup : ageGroups) {
					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					RowReportAggregateAgegroupLayoutBinding binding =
						DataBindingUtil.inflate(inflater, R.layout.row_report_aggregate_agegroup_layout, contentBinding.reportContent, true);
					binding.setAgeGroup(AgeGroupUtils.createCaption(ageGroup));
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
					for (AggregateReport report : reports) {
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

	private ObservableArrayList makeObservable(List<AggregateReport> reports) {
		ObservableArrayList<AggregateReport> newList = new ObservableArrayList<>();
		newList.addAll(reports);
		return newList;
	}
}
